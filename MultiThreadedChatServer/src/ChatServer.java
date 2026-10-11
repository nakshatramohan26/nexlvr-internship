import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * ChatServer - the main (acceptor) thread of the application.
 *
 * Responsibilities:
 *   1. Listen on a TCP port.
 *   2. Accept incoming client connections in a loop.
 *   3. Start one ClientHandler thread per connected client.
 *   4. Own the SHARED list of connected clients and protect it with a lock.
 *
 * Usage:  java ChatServer [port]      (default port: 5000)
 */
public class ChatServer {

    private static final int DEFAULT_PORT = 5000;

    /** Shared state: every connected (and registered) client. */
    private final List<ClientHandler> clients = new ArrayList<>();

    /** The lock object that guards the 'clients' list. */
    private final Object clientsLock = new Object();

    private final int port;

    public ChatServer(int port) {
        this.port = port;
    }

    /** Runs the accept loop. This method runs on the "main" thread. */
    public void start() {
        // try-with-resources closes the ServerSocket automatically on exit.
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            log("Chat server started on port " + port + ". Waiting for clients...");

            while (true) {
                // accept() BLOCKS until a client connects. Because the main
                // thread does nothing else, new clients are never kept waiting
                // by a slow existing client.
                Socket clientSocket = serverSocket.accept();
                log("New connection from " + clientSocket.getRemoteSocketAddress());

                // Hand the connection to a new thread and go straight back to accept().
                ClientHandler handler = new ClientHandler(clientSocket, this);
                Thread thread = new Thread(handler, "client-handler-" + clientSocket.getPort());
                thread.start();
            }
        } catch (IOException e) {
            log("Server error: " + e.getMessage()
                    + " (is port " + port + " already in use? Try another port.)");
        }
    }

    /**
     * Adds a client to the shared list if its name is not taken yet.
     * The name check and the add happen in ONE synchronized block, so two
     * clients cannot both claim the same name at the same moment.
     *
     * @return true if added, false if the name is already in use
     */
    boolean registerClient(ClientHandler client) {
        synchronized (clientsLock) {
            for (ClientHandler existing : clients) {
                if (existing.getClientName().equalsIgnoreCase(client.getClientName())) {
                    return false;
                }
            }
            clients.add(client);
            log("Registered '" + client.getClientName() + "'. Active clients: " + clients.size());
            return true;
        }
    }

    /** Removes a client from the shared list. Safe to call more than once. */
    void removeClient(ClientHandler client) {
        synchronized (clientsLock) {
            if (clients.remove(client)) {
                log("Removed '" + client.getClientName() + "'. Active clients: " + clients.size());
            }
        }
    }

    /** Number of clients currently registered. */
    int getClientCount() {
        synchronized (clientsLock) {
            return clients.size();
        }
    }

    /**
     * Sends a message to every registered client except 'sender'
     * (pass null to send to everybody).
     *
     * We take a SNAPSHOT of the list while holding the lock, then send outside
     * the lock. That way (a) the list can never be modified while we iterate
     * over it (no ConcurrentModificationException) and (b) one slow client
     * cannot block other threads from joining or leaving.
     */
    void broadcast(String message, ClientHandler sender) {
        List<ClientHandler> snapshot;
        synchronized (clientsLock) {
            snapshot = new ArrayList<>(clients);
        }
        for (ClientHandler client : snapshot) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    /** Timestamp-free console logging; synchronized printing is handled by System.out itself. */
    static void log(String message) {
        System.out.println("[SERVER] " + message);
    }

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
                if (port < 1 || port > 65535) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid port '" + args[0] + "'. Use a number from 1 to 65535.");
                return;
            }
        }
        new ChatServer(port).start();
    }
}
