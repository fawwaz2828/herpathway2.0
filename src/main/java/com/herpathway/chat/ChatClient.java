package com.herpathway.chat;

import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

/**
 * Connection to {@link ChatServer} for live message delivery.
 * Incoming lines are delivered to the listener on the JavaFX thread.
 */
public class ChatClient implements AutoCloseable {

    private final Socket socket;
    private final PrintWriter out;

    private ChatClient(Socket socket, Consumer<String> onMessage) throws IOException {
        this.socket = socket;
        this.out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        Thread reader = new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    String message = line;
                    Platform.runLater(() -> onMessage.accept(message));
                }
            } catch (IOException e) {
                if (!socket.isClosed()) {
                    e.printStackTrace();
                }
            }
        }, "chat-reader");
        reader.setDaemon(true); // don't keep the JVM alive after the window closes
        reader.start();
    }

    /**
     * @return a connected client, or {@code null} if the chat server isn't running
     *         (messages are still saved to the database, just not pushed live)
     */
    public static ChatClient connect(Consumer<String> onMessage) {
        try {
            return new ChatClient(new Socket(ChatServer.HOST, ChatServer.PORT), onMessage);
        } catch (IOException e) {
            System.err.println("Chat server not reachable at " + ChatServer.HOST + ":" + ChatServer.PORT
                    + " (" + e.getMessage() + "). Live updates are disabled.");
            return null;
        }
    }

    public void send(String line) {
        out.println(line);
    }

    @Override
    public void close() {
        try {
            socket.close();
        } catch (IOException ignored) {
            // closing anyway
        }
    }
}
