import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Dependency-free automated test for the chat server (no JUnit needed).
 * It starts a real ChatServer on a free port and connects real TCP clients.
 *
 * Run from the project root (see README):
 *   javac -d out src/*.java test/ChatServerTest.java
 *   java -cp out ChatServerTest
 */
public class ChatServerTest {

    private static int passed = 0;
    private static int failed = 0;

    /** A tiny test client: socket + reader + writer. */
    static class TestClient implements AutoCloseable {
        final Socket socket;
        final BufferedReader in;
        final PrintWriter out;

        TestClient(int port, String name) throws IOException {
            socket = new Socket("localhost", port);
            socket.setSoTimeout(3000); // never hang forever
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println(name);
        }

        void send(String text) {
            out.println(text);
        }

        /** Reads lines until one contains 'expected'; returns that line or null on timeout/EOF. */
        String waitFor(String expected) {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    if (line.contains(expected)) {
                        return line;
                    }
                }
            } catch (IOException e) {
                // timeout
            }
            return null;
        }

        @Override
        public void close() {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }

    static void check(String testName, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("PASS  " + testName);
        } else {
            failed++;
            System.out.println("FAIL  " + testName);
        }
    }

    static int freePort() throws IOException {
        try (ServerSocket s = new ServerSocket(0)) {
            return s.getLocalPort();
        }
    }

    static boolean waitForCount(ChatServer server, int expected) throws InterruptedException {
        for (int i = 0; i < 40; i++) { // up to ~2 seconds
            if (server.getClientCount() == expected) {
                return true;
            }
            Thread.sleep(50);
        }
        return false;
    }

    public static void main(String[] args) throws Exception {
        int port = freePort();
        ChatServer server = new ChatServer(port);
        Thread serverThread = new Thread(server::start, "test-server");
        serverThread.setDaemon(true);
        serverThread.start();
        Thread.sleep(500); // give the server a moment to open its port

        // 1. Server started: we can connect.
        TestClient alice = new TestClient(port, "Alice");
        check("1. Server starts and one client can connect (welcome received)",
                alice.waitFor("Welcome, Alice") != null);

        // 2. Multiple clients + join notification.
        TestClient bob = new TestClient(port, "Bob");
        check("2a. Second client connects (welcome received)", bob.waitFor("Welcome, Bob") != null);
        check("2b. Existing client is told that Bob joined", alice.waitFor("Bob joined") != null);

        TestClient carol = new TestClient(port, "Carol");
        check("2c. Third client connects (welcome received)", carol.waitFor("Welcome, Carol") != null);
        check("2d. Alice is told that Carol joined", alice.waitFor("Carol joined") != null);
        check("2e. Bob is told that Carol joined", bob.waitFor("Carol joined") != null);
        check("2f. Server tracks 3 active clients", waitForCount(server, 3));

        // 3. Broadcast reaches the others.
        alice.send("hello everyone");
        check("3a. Bob receives Alice's message", bob.waitFor("Alice: hello everyone") != null);
        check("3b. Carol receives Alice's message", carol.waitFor("Alice: hello everyone") != null);

        // 4. Graceful disconnect with /quit.
        bob.send("/quit");
        check("4a. Others are told Bob left", alice.waitFor("Bob left") != null);
        check("4b. Bob removed from active client list", waitForCount(server, 2));
        bob.close();

        // 5. Remaining clients still talk to each other.
        carol.send("still here");
        check("5a. Alice receives Carol's message after Bob left", alice.waitFor("Carol: still here") != null);
        alice.send("me too");
        check("5b. Carol receives Alice's message after Bob left", carol.waitFor("Alice: me too") != null);

        // 6. Abrupt disconnect (socket closed with no /quit) must not crash the server.
        carol.close();
        check("6a. Others are told Carol left (abrupt disconnect)", alice.waitFor("Carol left") != null);
        check("6b. Carol removed from active client list", waitForCount(server, 1));

        // 7. Duplicate name rejected and does not corrupt the list.
        try (TestClient duplicate = new TestClient(port, "alice")) {
            check("7a. Duplicate name (case-insensitive) is rejected",
                    duplicate.waitFor("already in use") != null);
        }
        check("7b. Active client count is still 1", waitForCount(server, 1));

        // 8. Server still alive after all of the above.
        try (TestClient dave = new TestClient(port, "Dave")) {
            check("8a. Server still accepts new clients after failures", dave.waitFor("Welcome, Dave") != null);
            check("8b. Alice is told that Dave joined", alice.waitFor("Dave joined") != null);
        }
        alice.close();
        check("8c. All clients gone -> 0 active clients", waitForCount(server, 0));

        // 9. Client-side connection failure: nothing listens on this port.
        int closedPort = freePort();
        boolean refused = false;
        try (Socket s = new Socket("localhost", closedPort)) {
            // should not connect
        } catch (ConnectException e) {
            refused = true;
        }
        check("9. Connecting to a port with no server fails with ConnectException", refused);

        System.out.println();
        System.out.println("Result: " + passed + " passed, " + failed + " failed");
        System.exit(failed == 0 ? 0 : 1);
    }
}
