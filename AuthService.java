import java.sql.SQLException;

public interface AuthService {
    void start() throws ClassNotFoundException, SQLException;
    void stop();
    String getNickByLoginAndPass(String login, String password);
    String changeNickname(String oldNickname, String newNickname);
    boolean isNickUnique(String nickname);
}
