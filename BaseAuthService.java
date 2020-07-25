import javax.xml.transform.Result;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BaseAuthService implements AuthService {
    Connection connection;
    Statement statement;

    public BaseAuthService() {

    }
//Подключает базу
    @Override
    public void start() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:ChatClients.db");
        statement = connection.createStatement();
        System.out.println("Сервер Auth запущен...");
    }
//Закрывает ресурсы
    @Override
    public void stop() {
        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            statement.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        System.out.println("Сервер Auth остановлен...");
    }
//Получает из базы ник пользователя по логину и паролю
    @Override
    public String getNickByLoginAndPass(String login, String password) {
        try {
            ResultSet result = statement.executeQuery("SELECT * FROM Clients WHERE Login = '" + login + "' AND Password = '" + password + "'");
            if(result.next()) return result.getString("Nickname");
            else return null;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }
//Меняет ник
    @Override
    public String changeNickname(String oldNickname, String newNickname) {
        try {
            statement.executeUpdate("UPDATE Clients SET Nickname ='" + newNickname + "' " +
                    "WHERE Nickname ='" + oldNickname +"'");
            return newNickname;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }
//Проверяет есть ли в базе указанный ник
    @Override
    public boolean isNickUnique(String nickname) {
        try {
            ResultSet result = statement.executeQuery("SELECT * FROM Clients WHERE Nickname = '" + nickname +"'");
            if(result.next()) return false;
            else return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

}
