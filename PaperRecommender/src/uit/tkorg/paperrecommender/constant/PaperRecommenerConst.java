package uit.tkorg.paperrecommender.constant;

import java.util.logging.Level;

public class PaperRecommenerConst {

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

    public static final int MAX_RETRY_TIMES = 4;
    public static final int TIME_SLEEP_AFTER_EXCEPTION = 60000;//180000;
    public static final int TIME_SLEEP = 750;//3000;
    public static final int TIME_WAIT_FOR_SUGGESTION = 10000;
    public static final int ITEM_LIST_SIZE = 100; //MAS supports max size 100
    public static final int CRAWLING_FUNCTION = 1;
    public static final int STEP = 0;

    //public static final Level LOGGING_LEVEL = Level.ALL;
    public static final Level LOGGING_LEVEL = Level.WARNING;
}
