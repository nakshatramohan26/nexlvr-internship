import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * ClientHandler - one instance (and one thread) per connected client.
 *
 * Protocol (plain text, one message per line):
 *   - The first line the client sends is its chosen name.
 *   - Every following line is a chat message.
 *   - The line "/quit" asks the server to disconnect the client.
 */
public class ClientHandler implements Runnable {

    private final Socket socket;
    private final ChatServer server;

    private PrintWriter out;
    private String clientName = "unknown";
    private boolean registered = false;

    public ClientHandler(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }

    public String getClientName() {
        return clientName;
    }

    /** The code that runs on this client's own thread. */
    @Override
    public void run() {
        // try-with-resources: 'in' and 'out' are closed automatically.
        // The socket is closed in 'finally' via cleanup().
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

            this.out = writer;

            // Step 1: read the name.
            String name = in.readLine();
            if (name == null || name.trim().isEmpty()) {
                return; // client left before sending a name
            }
            clientName = name.trim();

            // Step 2: register in the shared list (fails if the name is taken).
            if (!server.registerClient(this)) {
                sendMessage("[Server] The name '" + clientName + "' is already in use. Please reconnect with another name.");
                return;
            }
            registered = true;

            sendMessage("[Server] Welcome, " + clientName + "! There are "
                    + server.getClientCount() + " user(s) online. Type /quit to leave.");
            server.broadcast("[Server] " + clientName + " joined the chat.", this);

            // Step 3: read messages until the client quits or disconnects.
            String line;
            while ((line = in.readLine()) != null) {
                if (line.equalsIgnoreCase("/quit")) {
                    break;
                }
                if (line.trim().isEmpty()) {
                    continue;
                }
                ChatServer.log(clientName + ": " + line);
                server.broadcast(clientName + ": " + line, this);
            }
        } catch (IOException e) {
            // Happens when the client crashes or the network drops (e.g. "Connection reset").
            ChatServer.log("Connection problem with '" + clientName + "': " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    /** Sends one line to this client. PrintWriter methods are internally synchronized. */
    void sendMessage(String message) {
        PrintWriter writer = out;
        if (writer != null) {
            writer.println(message);
            if (writer.checkError()) {
                // The write failed (client is gone). The reading loop on this
                // client's own thread will notice and perform cleanup.
                ChatServer.log("Could not deliver message to '" + clientName + "'.");
            }
        }
    }

    /**
     * Removes this client from the shared list, announces the departure and
     * closes the socket. Runs on the client's own thread, exactly once, from 'finally'.
     */
    private void cleanup() {
        server.removeClient(this);
        if (registered) {
            server.broadcast("[Server] " + clientName + " left the chat.", this);
            ChatServer.log(clientName + " disconnected.");
        }
        try {
            socket.close();
        } catch (IOException ignored) {
            // nothing more we can do
        }
    }
}
