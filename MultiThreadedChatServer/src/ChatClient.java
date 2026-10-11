import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * ChatClient - console chat client.
 *
 * Two threads run in the client:
 *   - the main thread reads what you type and sends it to the server;
 *   - a "receiver" thread prints every line the server sends.
 * Two threads are needed because reading the keyboard and reading the network
 * both BLOCK, and neither may stop the other.
 *
 * Usage:  java ChatClient [host] [port] [name]
 * Defaults: localhost 5000, and the name is asked interactively.
 */
public class ChatClient {

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = 5000;
        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port '" + args[1] + "'.");
                return;
            }
        }

        try (BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in))) {
            String name;
            if (args.length > 2) {
                name = args[2].trim();
            } else {
                System.out.print("Enter your name: ");
                System.out.flush();
                name = keyboard.readLine();
            }
            if (name == null || name.trim().isEmpty()) {
                System.err.println("A name is required.");
                return;
            }

            try (Socket socket = new Socket(host, port);
                 BufferedReader fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter toServer = new PrintWriter(socket.getOutputStream(), true)) {

                System.out.println("Connected to " + host + ":" + port + " as '" + name.trim() + "'.");
                toServer.println(name.trim());

                // Receiver thread: prints incoming messages until the server closes the connection.
                Thread receiver = new Thread(() -> {
                    try {
                        String line;
                        while ((line = fromServer.readLine()) != null) {
                            System.out.println(line);
                        }
                    } catch (IOException e) {
                        // socket closed while reading - normal when we quit
                    }
                    System.out.println("Disconnected from server.");
                }, "receiver");
                receiver.start();

                // Main thread: send what the user types.
                String input;
                while ((input = keyboard.readLine()) != null) {
                    toServer.println(input);
                    if (input.equalsIgnoreCase("/quit")) {
                        break;
                    }
                }

                // Tell the server we are leaving (needed when input ended without /quit),
                // then wait briefly so any final server messages are printed.
                if (input == null) {
                    toServer.println("/quit");
                }
                receiver.join(1000);
            }
        } catch (IOException e) {
            System.err.println("Could not connect to " + host + ":" + port + " - " + e.getMessage());
            System.err.println("Make sure the server is running and the port is correct.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
