import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client extends JFrame  {

    private static final String SERVER_ADDR = "localhost";
    private static final int SERVER_PORT = 8554;

    private DataInputStream in;
    private DataOutputStream out;
    private Socket socket;

    private JTextField msgInputField;
    private JTextArea chatArea;

    private boolean isClosed = false;


    public Client(){
        start();
        prepareGUI();
    }

    public void start() {
        try {
            socket = new Socket(SERVER_ADDR, SERVER_PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        System.out.println("Ожидание сообщения");
                        String incomeMessage = in.readUTF();
                        if (incomeMessage.equalsIgnoreCase("/auth time is over")){
                            out.writeUTF("/end");
                            chatArea.append("Время вышло. Вы отключены от сервера.");
                            chatArea.append("\n");
                            return;
                        }else if (incomeMessage.equalsIgnoreCase("/end")) {
                            chatArea.append("Вы отключены от сервера");
                            chatArea.append("\n");
                            return;
                        }
                        chatArea.append(incomeMessage);
                        chatArea.append("\n");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    close();
                }
            }
        }).start();
    }

    public void send() {
        if (!msgInputField.getText().trim().isEmpty()) {
            if(isClosed){
                chatArea.append("Вы не подключены к серверу");
                chatArea.append("\n");
            }else {
                try {
                    out.writeUTF(msgInputField.getText());
                    msgInputField.setText("");
                    msgInputField.grabFocus();
                } catch (IOException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Ошибка отправки сообщения");
                }
            }
        }
    }

    public void prepareGUI() {
        // Параметры окна
        setBounds(600, 300, 500, 500);
        setTitle("Клиент");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Текстовое поле для вывода сообщений
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        // Нижняя панель с полем для ввода сообщений и кнопкой отправки сообщений
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JButton btnSendMsg = new JButton("Отправить");
        bottomPanel.add(btnSendMsg, BorderLayout.EAST);
        msgInputField = new JTextField();
        add(bottomPanel, BorderLayout.SOUTH);
        bottomPanel.add(msgInputField, BorderLayout.CENTER);

        btnSendMsg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                send();
            }
        });
        msgInputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //if(msgInputField.getText().equals("/end")){
                    //msgInputField.setEditable(false);
                    //msgInputField.setVisible(false);
                //}
                send();
            }
        });

        // Настраиваем действие на закрытие окна
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                //super.windowClosing(e);
                try {
                    if(!isClosed){
                    out.writeUTF("/end");
                    //close();
                    }
                } catch (IOException exc) {
                    exc.printStackTrace();
                }
            }
        });

        setVisible(true);
    }

    public void close() {
        isClosed = true;
        try {
            if(in != null)in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if(out != null){
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if(socket != null)socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
