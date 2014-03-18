/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package obsolete.uit.tkorg.paperrecommender.utility;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Minh
 * ket noi CSDL  xu li data
 */
public class Mysqlconnection {
     public Connection connection = null;
    public Statement stmt = null;

    public void connectDb() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Dataset1", "root", "2409");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Mysqlconnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Mysqlconnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
       public void closeConnect()
    {
        try {
            connection.close();
        } catch (SQLException ex) {
            Logger.getLogger(Mysqlconnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    // chen du lieu vao bang paper_keyword
    /* public void insertDatabase(String c, int a, double b) 
     {
        try {
            stmt = connection.createStatement();
            String sql = "INSERT INTO paper_keyword" + "VALUES ('" + c + "','" + a + "','" + b + "')";
            stmt.executeUpdate(sql);
        } catch (SQLException ex) {
            Logger.getLogger(Mysqlconnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/
       // Ham import data cua JuniorR, Recommend, Senior vao
       public void insertDatabase(String c, String a, String b) {
            try {
                stmt = connection.createStatement();
                String sql = "INSERT INTO paper_keyword " + "VALUES ('" + c + "','" + a + "','" + b + "')";
                stmt.executeUpdate(sql);
            } catch (SQLException ex) {
                Logger.getLogger(Mysqlconnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

     // Ham import data vao mang trich dan
        public void insertDatabase(String a, String b)
        {
                try {
                    stmt = connection.createStatement();
                    String sql = "INSERT INTO paper_paper " + "VALUES ('" + a + "','" + b + "')";
                    stmt.executeUpdate(sql);
                } catch (SQLException ex) {
                    Logger.getLogger(Mysqlconnection.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        // Ham import vocabulary
       public void insertDatabase(int a, String b)
       {
            try {
                stmt = connection.createStatement();
                String sql = "INSERT INTO paper_paper " + "VALUES ('" + a + "','" + b + "')";
                stmt.executeUpdate(sql);
            } catch (SQLException ex) {
                Logger.getLogger(Mysqlconnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    // ham đanh chi so id cho key word trong bang paper-keyword
     public int returnid(String t) throws SQLException
     {
        int a = 0;
        String query = "select idKeyword from Vocabulary where keyword = '" + t + "'";
        stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            a = rs.getInt(1);
        }
        rs.close();
        stmt.close();
        return a;
    }
  
    
}
