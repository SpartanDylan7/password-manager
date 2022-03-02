package org.eagle.passwordmanager.util;

import org.eagle.passwordmanager.model.Login;
import org.eagle.passwordmanager.model.User;

import java.sql.*;

public class DatabaseManager {
    private static final String url = "jdbc:derby:passmgr;create=true";

    private DatabaseManager() {
    }

    public static void initializeDatabase() throws SQLException {

        try (Connection conn = DriverManager.getConnection(url)) {
//            System.out.println("connected to database");
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "USERS", null);
            if (tables.next()) {
//                System.out.println("Table USERS already exists");
                return;
            }
            createUsersTable(conn);
            createItemsTable(conn);


        }
    }

    private static void createUsersTable(Connection conn) {
        String createUsersTableQuery = "CREATE TABLE USERS( "
                + "ID INT NOT NULL GENERATED ALWAYS AS IDENTITY, "
                + "USER_NAME VARCHAR(255) NOT NULL UNIQUE , "
                + "PASSWORD VARCHAR(255) NOT NULL, "
                + "EMAIL VARCHAR(255)DEFAULT NULL, "
                + "PRIMARY KEY (Id))";
        try (PreparedStatement statement = conn.prepareStatement(createUsersTableQuery)) {
            statement.executeUpdate();
//            System.out.println("added USERS table");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void createItemsTable(Connection conn) {
        String createItemsTableQuery = "CREATE TABLE ITEMS( "
                + "ID INT NOT NULL GENERATED ALWAYS AS IDENTITY, "
                + "USERS_ID INT, "
                + "NAME VARCHAR(255) NOT NULL, "
                + "USER_NAME VARCHAR(255) DEFAULT NULL, "
                + "PASSWORD VARCHAR(255) DEFAULT NULL, "
                + "URL VARCHAR(255) DEFAULT NULL, "
                + "NOTES VARCHAR(255)DEFAULT NULL, "
                + "PRIMARY KEY (ID),"
                + "CONSTRAINT FK_USERS FOREIGN KEY (USERS_ID) REFERENCES USERS (ID))";
        try (PreparedStatement statement = conn.prepareStatement(createItemsTableQuery)) {
            statement.executeUpdate();
//            System.out.println("added ITEMS table");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static User getUser(String userName) {
        User user = new User(userName);
        try (Connection conn = DriverManager.getConnection(url)) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM USERS WHERE USER_NAME = ?");
            ps.setString(1, userName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("ID");
                String token = rs.getString("PASSWORD");
                String email = rs.getString("EMAIL");
                user.setUserId(userId);
                user.setAuthToken(token);
                user.setEmail(email);
            }
            ps = conn.prepareStatement("SELECT * FROM ITEMS WHERE USERS_ID = ?");
            ps.setInt(1, user.getUserId());
            rs = ps.executeQuery();
            while (rs.next()) {
                int loginId = rs.getInt("ID");
                String name = rs.getString("NAME");
                String usrName = rs.getString("USER_NAME");
                String pass = rs.getString("PASSWORD");
                String url = rs.getString("URL");
                String notes = rs.getString("NOTES");
                Login login = new Login(loginId, name, usrName, pass, url, notes);
                user.getUserItems().add(login);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public static boolean insertNewUser(User user) {
        String INSERT = "INSERT INTO USERS(USER_NAME,PASSWORD,EMAIL) VALUES (?,?,?)";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUserName());
            ps.setString(2, getPasswordToken(user));
            ps.setString(3, user.getEmail());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (!rs.next()) {
                return false;
            }
            user.setUserId(rs.getInt(1));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    public static boolean insertItem(int userId, Login loginItem) {
        Login encryptedLogin = SecretsManager.encryptLogin(loginItem);
        String INSERT = "INSERT INTO ITEMS(USERS_ID,NAME,USER_NAME,PASSWORD,URL,NOTES) VALUES (?,?,?,?,?,?)";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setString(2, encryptedLogin.getName());
            ps.setString(3, encryptedLogin.getUserName());
            ps.setString(4, encryptedLogin.getPassword());
            ps.setString(5, encryptedLogin.getURL());
            ps.setString(6, encryptedLogin.getNotes());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (!rs.next()) {
                return false;
            }
            loginItem.setLoginId(rs.getInt(1));

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Update existing item in the database
     * @param loginItem will be updated
     * @return true if successful
     */
    public static boolean updateItem(Login loginItem) {
        Login encryptedLogin = SecretsManager.encryptLogin(loginItem);
        String UPDATE = "UPDATE ITEMS SET NAME =?,USER_NAME=?,PASSWORD=?,URL=?,NOTES=? WHERE ID = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(UPDATE)) {
            ps.setString(1, encryptedLogin.getName());
            ps.setString(2, encryptedLogin.getUserName());
            ps.setString(3, encryptedLogin.getPassword());
            ps.setString(4, encryptedLogin.getURL());
            ps.setString(5, encryptedLogin.getNotes());
            ps.setInt(6, encryptedLogin.getLoginId());
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Delete login item from database
     * @param loginItem will be deleted from the database
     * @return true if successful
     */
    public static boolean deleteItem(Login loginItem) {
        String DELETE = "DELETE FROM ITEMS WHERE ID = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(DELETE)) {
            ps.setInt(1, loginItem.getLoginId());
            ps.execute();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Password token will be saved in the database for user authentication.
     *
     * @return
     */
    private static String getPasswordToken(User user) {
        PasswordAuthentication authentication = new PasswordAuthentication();
        return authentication.hash(user.getPassword());
    }
}
