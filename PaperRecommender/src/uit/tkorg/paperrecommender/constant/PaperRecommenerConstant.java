package uit.tkorg.paperrecommender.constant;

import java.util.logging.Level;

public class PaperRecommenerConstant {

    public static final String DATASETFOLDER = "D:\\Dropbox\\De tai Paper Recommendation\\Data\\Dataset 1\\20100825-SchPaperRecData\\20100825-SchPaperRecData";
    public static final String SAVEDATAFOLDER = "";
    
    public static final String DB = "MYSQL";
    //public static final String DB = "SQLSERVER";

    public static final String HOST = "localhost";
    public static final String PORT = "3306";
    public static final String DATABASE = "CSPublicationCrawler";
    public static final String USERNAME = "root";
    public static final String PASSWORD = "root";

    public static final String HOSTMSSQLSERVER = "localhost";
    public static final String PORTMSSQLSERVER = "1433";
    public static final String DATABASEMSSQLSERVER = "CSPublicationCrawler";
    public static final String USERNAMEMSSQLSERVER = "sa";
    public static final String PASSWORDMSSQLSERVER = "12345";
   
    //public static final Level LOGGING_LEVEL = Level.ALL;
    public static final Level LOGGING_LEVEL = Level.WARNING;
}
