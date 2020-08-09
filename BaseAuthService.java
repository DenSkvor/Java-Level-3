package chat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public class BaseAuthService implements AuthService {
    Connection connection;
    Statement statement;
/**/private static final Logger logger = LogManager.getLogger(BaseAuthService.class);

    public BaseAuthService() {

    }

    @Override
    public void start() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:ChatClients.db");
        statement = connection.createStatement();
/**/    logger.info("Сервер Auth запущен...");
        //System.out.println("Сервер Auth запущен...");
    }

    @Override
    public void stop() {
        try {
            connection.close();
        } catch (SQLException throwables) {
/**/        logger.error(throwables.getMessage(),throwables);
            //throwables.printStackTrace();
        }
        try {
            statement.close();
        } catch (SQLException throwables) {
/**/        logger.error(throwables.getMessage(),throwables);
            //throwables.printStackTrace();
        }
/**/    logger.info("Сервер Auth остановлен...");
        //System.out.println("Сервер Auth остановлен...");
    }

    @Override
    public String getNickByLoginAndPass(String login, String password) {
        try {
            ResultSet result = statement.executeQuery("SELECT * FROM Clients WHERE Login = '" + login + "' AND Password = '" + password + "'");
            if(result.next()) return result.getString("Nickname");
            else return null;

        } catch (SQLException throwables) {
/**/        logger.error(throwables.getMessage(),throwables);
            //throwables.printStackTrace();
        }
        return null;
    }

    @Override
    public String changeNickname(String oldNickname, String newNickname) {
        try {
            statement.executeUpdate("UPDATE Clients SET Nickname ='" + newNickname + "' " +
                    "WHERE Nickname ='" + oldNickname +"'");
            return newNickname;
        } catch (SQLException throwables) {
/**/        logger.error(throwables.getMessage(),throwables);
            //throwables.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean isNickUnique(String nickname) {
        try {
            ResultSet result = statement.executeQuery("SELECT * FROM Clients WHERE Nickname = '" + nickname +"'");
            if(result.next()) return false;
            else return true;
        } catch (SQLException throwables) {
/**/        logger.error(throwables.getMessage(),throwables);
            //throwables.printStackTrace();
            return false;
        }
    }

}
