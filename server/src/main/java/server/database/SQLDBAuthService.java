package server.database;

import server.AuthService;

public class SQLDBAuthService implements AuthService {
    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        return SQLDB.getNicknameByLoginAndPassword(login, password);
    }

    @Override
    public boolean registration(String login, String password, String nickname) {
        return SQLDB.registration(login, password, nickname);
    }

    @Override
    public boolean changeNick(String oldNickname, String newNickname) {
        return SQLDB.changeNick(oldNickname, newNickname);
    }
}
