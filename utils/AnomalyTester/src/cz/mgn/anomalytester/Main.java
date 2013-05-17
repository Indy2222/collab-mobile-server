package cz.mgn.anomalytester;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Martin Indra <aktive at seznam.cz>
 */
public class Main {

    private static Connection trans1;
    private static Connection trans2;

    public static void main(String[] args) {

        establishConnections();
        demonstrateUnrepeatableRead();
        demonstratePhantom();
        cleanDBafterDemonstration();
    }

    public static void establishConnections() {

        try {
            String connectionURL = "jdbc:postgresql://localhost:5432/collabms";
            trans1 = DriverManager.getConnection(connectionURL, "collabms", "collabms");
            trans2 = DriverManager.getConnection(connectionURL, "collabms", "collabms");
            System.out.println("Connection established successfully...");
        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void demonstrateUnrepeatableRead() {

        System.out.println("-----------------------------------------------");
        System.out.println("---DEMONSTRATING ANOMALY - Unrepeatable Read---");
        System.out.println("-----------------------------------------------");
        try {
            trans1.setAutoCommit(false);
            trans2.setAutoCommit(false);

            System.out.println("--- 1st TRANSACTION BEGIN ---");
            System.out.println("1st transaction read");
            String read = "SELECT * FROM collabms_message WHERE message_id = ?";
            System.out.println(read + "\n(Parameters will be substituted for question marks.)");
            PreparedStatement rd = trans1.prepareStatement(read);
            rd.setInt(1, 4);
            ResultSet rs = rd.executeQuery();
            while (rs.next()) {
                System.out.println("message_id = " + rs.getInt(1));
            }
            System.out.println("");

            System.out.println("--- 2nd TRANSACTION BEGIN ---");
            System.out.println("2nd transaction updates genre \"Fantasy\" and changes it to \"Series\"");
            String update = "UPDATE collabms_message SET text = ? WHERE message_id = ?";
            System.out.println(update + "\n(Parameters will be substituted for question marks.)");
            PreparedStatement upd = trans2.prepareStatement(update);
            upd.setString(1, "AAABBBXXX");
            upd.setInt(2, 4);
            upd.execute();
            trans2.commit();
            System.out.println("--- 2nd TRANSACTION COMMIT ---");
            System.out.println();
            
            System.out.println("1st transaction reads again");
            System.out.println(read + "\n(Parameters will be substituted for question marks.)");
            PreparedStatement rd2 = trans1.prepareStatement(read);
            rd2.setInt(1, 4);
            rs = rd2.executeQuery();
            while (rs.next()) {
                System.out.println("message_id = " + rs.getInt(1) + " | text = " + rs.getString(2));
            }
            System.out.println("ANOMALY DETECTED - Title of the genre was changed by another transaction!");
            System.out.println("--- 1st TRANSACTION COMMIT ---");
            trans1.commit();
        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("-------------------------------------");
        System.out.println();
    }

    private static void demonstratePhantom() {

        System.out.println("-------------------------------------");
        System.out.println("---DEMONSTRATING ANOMALY - Phantom---");
        System.out.println("-------------------------------------");
        try {
            trans1.setAutoCommit(false);
            trans2.setAutoCommit(false);

            // trans1 read
            System.out.println("--- 1st TRANSACTION BEGIN ---");
            System.out.println("1st transaction READ");
            String count = "SELECT COUNT(*) FROM collabms_message";
            System.out.println(count);
            Statement cnt = trans1.createStatement();
            ResultSet rs = cnt.executeQuery(count);
            while (rs.next()) {
                System.out.println("Count = " + rs.getString(1));
            }

            // trans2 insert
            System.out.println("");
            System.out.println("--- 2nd TRANSACTION BEGIN ---");
            System.out.println("2nd transaction inserts new row");
            String insert = "INSERT INTO collabms_message (text, time, expiration, author)"
                    + " values (?, ?, ?, ?)";
            System.out.println(insert + "\n(New parameters will be substituted for question marks.)");
            PreparedStatement ins = trans2.prepareStatement(insert);
            ins.setString(1, "TextToDelete");
            ins.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            ins.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            ins.setInt(4, 5);
            ins.execute();
            trans2.commit();
            System.out.println("--- 2nd TRANSACTION COMMIT ---");
            System.out.println();

            // trans1 read
            System.out.println("1st transaction same read again");
            System.out.println(count);
            rs = cnt.executeQuery(count);
            while (rs.next()) {
                System.out.println("Count = " + rs.getString(1));
            }
            System.out.println("ANOMALY DETECTED - Another transaction added new row to the table collabms_message.");
            System.out.println("--- 1st TRANSACTION COMMIT ---");
            trans1.commit();
        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("-------------------------------------");
    }

    private static void cleanDBafterDemonstration() {

        System.out.println("Cleaning after demonstration..");
        try {
            trans1.setAutoCommit(true);
            String upd = "UPDATE collabms_message SET text = ? WHERE message_id = ?";
            PreparedStatement up = trans1.prepareStatement(upd);
            up.setString(1, "Fantasy");
            up.setInt(2, 4);
            up.execute();
            String del = "DELETE FROM collabms_message WHERE text = ?";
            PreparedStatement dl = trans1.prepareStatement(del);
            dl.setString(1, "TextToDelete");
            dl.execute();
        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Clean successfull..");
    }
}