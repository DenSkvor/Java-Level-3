package chat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private ExecutorService executorService;
/**/private static final Logger logger = LogManager.getLogger(ClientHandler.class);

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            executorService = Executors.newFixedThreadPool(2);
            name = "";

            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        doAuth();
                        if (isAuthorized) readMessage();
                    } catch (IOException e) {
/**/                    logger.error(e.getMessage(),e);
                        e.printStackTrace();
                    } finally {
                        closeConnection();
                    }
                }
            });

        } catch (IOException e) {
/**/        logger.error(e.getMessage(), e);
            throw new RuntimeException("Ошибка при инициализации client handler");
        }
    }

    public String getName() {
        return name;
    }

    public void doAuth() throws IOException {
        //таймер
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                while (true){
                    long currentTime = System.currentTimeMillis();
                    if((currentTime - startTime) == 120000 || isAuthorized || authStop) break;
                }
                if(!isAuthorized && !authStop) {
/**/                logger.info("Превышено время ожидания авторизации.");
                    sendMessage("/auth time is over");
                }
            }
        });

        while (true) {
            String strFromClient = in.readUTF();
/**/        logger.info("Сообщение от клиента сокет {}: {}", socket.toString(), strFromClient);
            if (strFromClient.startsWith("/auth")) {
/**/            logger.info("Попытка авторизации клиента сокет {}", socket.toString());
                String[] parts = strFromClient.split("\\s");
                String nickname = server.getAuthService().getNickByLoginAndPass(parts[1], parts[2]);
                if (nickname != null) {
                    if (server.isNickFree(nickname)) {
                        sendMessage("/login " + parts[1]);
                        sendMessage("/authok " + nickname);
                        name = nickname;
/**/                    logger.info("Клиент сокет {} залогинился. Данные: логин {}, ник {}", socket.toString(), parts[1], name);
                        server.broadcastMessage(name + " вошел в чат");
                        server.subscribe(this);
                        isAuthorized = true;
                        return;
                    } else {
/**/                    logger.info("Неудачная попытка авторизации клиента сокет {}. Такой ник уже подключен к чату.", socket.toString());
                        sendMessage(String.format("Nickname[%s] уже подключен к чату", nickname));
                    }
                } else {
/**/                logger.info("Неудачная попытка авторизации клиента сокет {}. Неверный логин/пароль.", socket.toString());
                    sendMessage("Неверный login и/или password");
                }
            } else if(strFromClient.equalsIgnoreCase("/end")) {
/**/            logger.info("Клиент сокет {} ник {} отключился от сервера авторизации.", socket.toString(), name);
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
/**/            logger.trace("Личное сообщение от клиента сокет {} ник {} клиенту сокет {} ник {}: {}", socket.toString(), name, o.socket.toString(), nickTo, msg);
                return ;
            }
        }
        this.sendMessage("Участника с ником " + nickTo + " нет в чате");
    }

    public void readMessage() throws IOException {
        while (true) {
            String strFromClient = in.readUTF();
/**/        logger.info("Сообщение от клиента сокет {} ник {}: {}", socket.toString(), name, strFromClient);
            if (strFromClient.equals("/end")) {
/**/            logger.info("Клиент сокет {} ник {} отключился от сервера.", socket.toString(), name);
                sendMessage("/end");
                return;

            }else if(strFromClient.startsWith("/change_nick")) {
/**/            logger.info("Попытка смены ника клиентом сокет {} ник {}.", socket.toString(), name);
                String[] parts = strFromClient.split("\\s");
                if(!server.getAuthService().isNickUnique(parts[1])){
/**/            logger.info("Неудачная попытка смены ника клиентом сокет {} ник {}. Такой ник занят.", socket.toString(), name);
                    sendMessage("Ник занят.");
                    continue;
                };
                String oldName = name;
                String nickName = server.getAuthService().changeNickname(oldName, parts[1]);
                if(nickName != null) {
/**/            logger.info("Клиенто сокет {} ник {} сменил ник на {}", socket.toString(), name, nickName);
                    name = nickName;
                    server.broadcastMessage(String.format("%s сменил ник на %s", oldName, name));
                }
                else sendMessage("Смена ника не удалась.");
/**/            logger.warn("Неудачная попытка смены ника клиентом сокет {} ник {}.", socket.toString(), name);
                continue;

            }else if (strFromClient.startsWith("/w ")) {
                String[] tokens = strFromClient.split("\\s");
                String nick = tokens[1];
                String msg = strFromClient.substring(4 + nick.length());
                sendMsgToClient(nick, msg);
                continue;
            }
/**/        logger.trace("Сообщение в общий чат от клиента сокет {} ник {}: {}", socket.toString(), name, strFromClient);
            server.broadcastMessage(String.format("%s: %s", name, strFromClient));
        }
    }

    public void closeConnection() {
        if(isAuthorized) {
            server.unsubscribe(this);
            server.broadcastMessage(name + " left chat");
        }
        try {
            executorService.shutdown();
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
