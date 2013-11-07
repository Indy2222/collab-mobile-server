/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.mgn.fakedatafiller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Martin Indra
 */
public class Main {

    protected static final String JDBC_DRIVER = "org.postgresql.Driver";
    // JDBC driver name and database URL
    protected static final String DB_URL = "jdbc:postgresql://localhost:5432/collabms";
    //  Database credentials
    static final String USER = "collabms";
    static final String PASS = "collabms";

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName(JDBC_DRIVER);
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            System.out.println("Creating statement...");
            stmt = conn.createStatement();

            //generateUsers(stmt);
            generateMessages(stmt);

//            String sql;
//            sql = "SELECT id, first, last, age FROM Employees";
//            ResultSet rs = stmt.executeQuery(sql);
//
//            //STEP 5: Extract data from result set
//            while (rs.next()) {
//                //Retrieve by column name
//                int id = rs.getInt("id");
//                int age = rs.getInt("age");
//                String first = rs.getString("first");
//                String last = rs.getString("last");
//
//            }  


        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                stmt.close();
            } catch (SQLException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("Goodbye!");
    }

    protected static void generateMessages(Statement stmt) throws SQLException {
        System.out.println("Genering messages...");
        
        String sql = "SELECT user_id FROM collabms_user";
        System.out.println("Loading users...");
        ResultSet rs = stmt.executeQuery(sql);

        ArrayList<Integer> users = new ArrayList<Integer>();

        while (rs.next()) {
            int id = rs.getInt("user_id");
            users.add(id);
        }

        Random r = new Random();
        LoremIpsum ipsum = new LoremIpsum();
        for (int i = 0; i < 50; i++) {
            String text = ipsum.getWords(10);
            String time = "2001-09-28 23:00";
            String expiration = "2013-05-28 23:00";
            int author = users.get(r.nextInt(users.size()));

            String query = "INSERT INTO collabms_message (text, time, expiration, author)"
                    + " VALUES ('" + text + "', '" + time + "', '" + expiration + "', " + author + " );";
            System.out.println("Generating message...");
            stmt.execute(query);
        }

        sql = "SELECT message_id FROM collabms_message";
        rs = stmt.executeQuery(sql);

        ArrayList<Integer> messages = new ArrayList<Integer>();
        while (rs.next()) {
            int id = rs.getInt("message_id");
            messages.add(id);
        }

        for (int message : messages) {
            for (int j = 0; j < 4; j++) {
                int owner = users.get(r.nextInt(users.size()));
                String query = "INSERT INTO collabms_has_message (readed, message_id, user_id)"
                        + " VALUES (" + r.nextBoolean() + ", " + message + ", " + owner + ");";
                System.out.println("Assingning message...");
                stmt.execute(query);
            }
        }
    }

    protected static void generateUsers(Statement stmt) throws SQLException {
        String[] names = {"Sophia", "Emma", "Olivia", "Isabella", "Ava", "Lily", "Zoe", "Chloe", "Mia", "Madison"};
        String[] surnames = {"Smith", "Brown", "Lee", "", "Wilson", "Martin", "Patel", "Taylor", "Wong", "Campbell"};
        String[] nicks = {"Fatso", "Ginger", "Curley", "Chatterbox", "Brainiac"};

        System.out.println("Generating users...");
        Random r = new Random(System.currentTimeMillis());
        for (int i = 0; i < 20; i++) {
            String nick = nicks[r.nextInt(nicks.length)];
            String name = names[r.nextInt(names.length)] + " " + surnames[r.nextInt(surnames.length)];
            System.out.println("inserting user " + name + " to database");
            String query = "INSERT INTO collabms_user (name, nick, status) VALUES ('" + name + "', '" + nick + "', 0);";
            stmt.execute(query);
        }
    }
}
