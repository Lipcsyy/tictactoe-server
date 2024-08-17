import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {
    private static final int PORT = 5000;

    // Use an AtomicBoolean to allow the shutdown thread to safely update the flag
    private static final AtomicBoolean running = new AtomicBoolean(true);

    public static void main(String[] args) {
        // Start a separate thread to listen for the shutdown command
        Thread shutdownThread = new Thread(() -> {
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            while (running.get()) {
                try {
                    if (consoleReader.readLine().equalsIgnoreCase("shutdown")) {
                        running.set(false);
                        System.out.println("Shutting down the server...");
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        shutdownThread.start();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            serverSocket.setSoTimeout(1000); // Set a timeout so we can check the running flag
            System.out.println("Server started. Listening on port " + PORT + "...");
            System.out.println("Type 'shutdown' to stop the server.");

            while (running.get()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    handleClient(clientSocket);
                } catch (java.net.SocketTimeoutException e) {
                    // This is expected, do nothing and continue the loop
                } catch (IOException e) {
                    System.out.println("Error accepting client connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Could not start server: " + e.getMessage());
        }

        System.out.println("Server has been shut down.");
    }

    private static void handleClient(Socket clientSocket) {
        new Thread(() -> {
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))
            ) {
                System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());

                Opponent opponent = new Opponent();

                while (running.get()) {
                    String message = in.readLine();
                    if (message == null) {
                        System.out.println("Client disconnected.");
                        break;
                    }

                    System.out.println("Received state: " + message);

                    try {

                        //Read from the client
                        int state = Integer.parseInt(message);
                        int[] bestMove = opponent.FindBestMove(state);

                        //Send the best move as a response
                        String response = bestMove[0] + "," + bestMove[1];
                        out.write(response);
                        out.newLine();
                        out.flush();

                        System.out.println("Sent move: " + response);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid state received: " + message);
                        out.write("ERROR: Invalid state");
                        out.newLine();
                        out.flush();
                    }
                }
            } catch (IOException e) {
                System.out.println("Error handling client: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.out.println("Error closing client socket: " + e.getMessage());
                }
            }
        }).start();
    }
}