package server.database;

import java.sql.*;

public class SQLDB {
    private static Connection connection;
    private static Statement stmt;
    private static PreparedStatement GetNickname;
    private static PreparedStatement Registration;
    private static PreparedStatement ChangeNick;

      private static void prepareAllStatement() throws SQLException {
          GetNickname = connection.prepareStatement("SELECT nickname FROM users WHERE login = ? AND password = ?;");
          Registration = connection.prepareStatement("INSERT INTO users(login, password, nickname) VALUES (? ,? ,? );");
          ChangeNick = connection.prepareStatement("UPDATE users SET nickname = ? WHERE nickname = ?;");
      }

    public static boolean connect() throws Exception {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:main.db");
        stmt = connection.createStatement();
        return false;
    }

    public static void disconnect() {
        try {
            stmt.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static String getNicknameByLoginAndPassword(String login, String password) {
        String nick = null;
        try {
            GetNickname.setString(1, login);
            GetNickname.setString(2, password);
            ResultSet rs = GetNickname.executeQuery();
            if (rs.next()) {
                nick = rs.getString(1);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nick;
    }

    public static boolean registration(String login, String password, String nickname) {
        try {
            Registration.setString(1, login);
            Registration.setString(2, password);
            Registration.setString(3, nickname);
            Registration.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean changeNick(String oldNickname, String newNickname) {
        try {
            ChangeNick.setString(1, newNickname);
            ChangeNick.setString(2, oldNickname);
            ChangeNick.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}