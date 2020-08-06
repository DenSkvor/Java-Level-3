import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String name;
    private boolean isAuthorized = false;
    private boolean authStop = false;
/**/private ExecutorService executorService;

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
/**/        executorService = Executors.newFixedThreadPool(2);
            name = "";

/**/        executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        doAuth();
                        if (isAuthorized) readMessage();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        closeConnection();
                    }
                }
            });

        } catch (IOException e) {
            throw new RuntimeException("Ошибка при инициализации client handler");
        }
    }

    public String getName() {
        return name;
    }

    public void doAuth() throws IOException {
        //таймер
/**/    executorService.execute(new Runnable() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                while (true){
                    long currentTime = System.currentTimeMillis();
                    if((currentTime - startTime) == 120000 || isAuthorized || authStop) break;
                }
                if(!isAuthorized && !authStop) {
                    sendMessage("/auth time is over");
                }
            }
        });

        while (true) {
            String strFromClient = in.readUTF();
            if (strFromClient.startsWith("/auth")) {
                String[] parts = strFromClient.split("\\s");
                String nickname = server.getAuthService().getNickByLoginAndPass(parts[1], parts[2]);
                if (nickname != null) {
                    if (server.isNickFree(nickname)) {
                        sendMessage("/login " + parts[1]);
                        sendMessage("/authok " + nickname);
                        name = nickname;
                        server.broadcastMessage(name + " вошел в чат");
                        server.subscribe(this);
                        isAuthorized = true;
                        return;
                    } else {
                        sendMessage(String.format("Nickname[%s] уже подключен к чату", nickname));
                    }
                } else {
                    sendMessage("Неверный login и/или password");
                }
            } else if(strFromClient.equalsIgnoreCase("/end")) {
                sendMessage("/end");
                authStop = true;
                break;
            }
        }
    }


    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsgToClient(String nickTo, String msg) {
        for (ClientHandler o : server.getClients()) {
            if (o.getName().equals(nickTo)) {
                o.sendMessage("от " + this.getName() + ": " + msg);
                this.sendMessage("клиенту " + nickTo + ": " + msg);
                return ;
            }
        }
        this.sendMessage("Участника с ником " + nickTo + " нет в чате");
    }

    public void readMessage() throws IOException {
        while (true) {
            String strFromClient = in.readUTF();
            if (strFromClient.equals("/end")) {
                sendMessage("/end");
                return;

            }else if(strFromClient.startsWith("/change_nick")) {
                String[] parts = strFromClient.split("\\s");
                if(!server.getAuthService().isNickUnique(parts[1])){
                    sendMessage("Ник занят.");
                    continue;
                };
                String oldName = name;
                String nickName = server.getAuthService().changeNickname(oldName, parts[1]);
                if(nickName != null) {
                    name = nickName;
                    server.broadcastMessage(String.format("%s сменил ник на %s", oldName, name));
                }
                else sendMessage("Смена ника не удалась.");
                continue;

            }else if (strFromClient.startsWith("/w ")) {
                String[] tokens = strFromClient.split("\\s");
                String nick = tokens[1];
                String msg = strFromClient.substring(4 + nick.length());
                sendMsgToClient(nick, msg);
                continue;
            }
            server.broadcastMessage(String.format("%s: %s", name, strFromClient));
        }
    }

    public void closeConnection() {
        if(isAuthorized) {
            server.unsubscribe(this);
            server.broadcastMessage(name + " left chat");
        }
        try {
/**/        executorService.shutdown();
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
