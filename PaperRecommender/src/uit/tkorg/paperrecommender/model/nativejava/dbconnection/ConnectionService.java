package uit.tkorg.paperrecommender.model.nativejava.dbconnection;

import java.sql.Connection;
import java.sql.DriverManager;

import uit.tkorg.paperrecommender.constant.PaperRecommenerConst;

public class ConnectionService {

    /**
     * loadJDBCDriver
     * @throws Exception 
     */
    protected static void loadJDBCDriver() throws Exception {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (java.lang.ClassNotFoundException e) {
            throw new Exception("SQL JDBC Driver not found ...");
        }
    }

    /**
     * getConnection
     * @return
     * @throws Exception 
     */
    public static Connection getConnection() throws Exception {
        if (PaperRecommenerConst.DB.compareTo("MYSQL") == 0) {
            return getConnectionMySQL();
        }
        else {
            return getConnectionMSSQLServer();
        }
        
    }
    
    /**
     * getConnectionMySQL
     * @return
     * @throws Exception 
     */
    public static Connection getConnectionMySQL() throws Exception {
        Connection connect = null;
        if (connect == null) {
            loadJDBCDriver();
            String url = "jdbc:mysql://" + PaperRecommenerConst.HOST
                    + ":" + PaperRecommenerConst.PORT
                    + "/" + PaperRecommenerConst.DATABASE
                    + "?user=" + PaperRecommenerConst.USERNAME
                    + "&password="+""// + PaperRecommenerConst.PASSWORD
                    + "&autoReconnect=true"
                    + "&connectTimeout=300"
                    + "&useBlobToStoreUTF8OutsideBMP=true";

            try {
                connect = DriverManager.getConnection(url);
            } catch (java.sql.SQLException e) {
                throw new Exception("Can not access to Database Server ..." + url + e.getMessage());
            }
        }
        return connect;
    }
    
    /**
     * getConnectionMSSQLServer
     * @return
     * @throws Exception 
     */
    public static Connection getConnectionMSSQLServer() throws Exception {
        Connection connect = null;
        if (connect == null) {
            //loadJDBCDriver(); // Edit lai ham loadJDBCDriver de load driver cho sqlServer.
            String url = "jdbc:sqlserver://" + PaperRecommenerConst.HOSTMSSQLSERVER
                    + ":" + PaperRecommenerConst.PORTMSSQLSERVER
                    + ";databaseName=" + PaperRecommenerConst.DATABASEMSSQLSERVER
                    + ";user=" + PaperRecommenerConst.USERNAMEMSSQLSERVER
                    + ";password=" + PaperRecommenerConst.PASSWORDMSSQLSERVER
                    + ";loginTimeout=300";            
            try {
                connect = DriverManager.getConnection(url);
            } catch (java.sql.SQLException e) {
                throw new Exception("Can not access to Database Server ..." + url + e.getMessage());
            } catch (Exception ex) {
                throw new Exception();
            }
            
        }
        return connect;
    }    
}
