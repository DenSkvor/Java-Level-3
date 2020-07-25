import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private final int port;
    private AuthService authService;

    private List<ClientHandler> clients;

    public Server(int port) {
        this.port = port;
        this.clients = new ArrayList<>();
        start();
    }

    public AuthService getAuthService() {
        return authService;
    }

    public synchronized List<ClientHandler> getClients() {
        return clients;
    }

    private void start() {
        try (ServerSocket server = new ServerSocket(this.port)) {
            authService = new BaseAuthService();
            authService.start();

            while(true) {
                System.out.println("Server started on port: " + port);
                System.out.println("Server is waiting for clients...");
                Socket socket = server.accept();
                System.out.println(String.format("Client connected: %s", socket.toString()));
                ClientHandler clientHandler = new ClientHandler(this, socket);
            }
        } catch (IOException | ClassNotFoundException | SQLException e) {
            System.out.println("Something went wrong during server startup");
        } finally {
            if (authService != null) {
                authService.stop();
            }
        }
    }

    public synchronized boolean isNickBusy(String nickname) {
        for (ClientHandler clientHandler : clients) {
            if (clientHandler.getName().equals(nickname)) {
                return true;
            }
        }
        return false;
    }

    public synchronized boolean isNickFree(String nickname) {
        return !isNickBusy(nickname);
    }

    public synchronized void broadcastMessage(String message) {
        for (ClientHandler clientHandler : clients) {
            clientHandler.sendMessage(message);
        }
    }

    public synchronized void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
    }

    public synchronized void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }
}

