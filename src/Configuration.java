public class Configuration {

    //Enables various debug info related print statements if true
    public static final boolean DebugMode = false;
    //Change this to the port you want the Android/Headset App to connect to
    //Note that it needs to be defined in the headset application project as well
    public static final int SERVER_PORT = 3306;
    //Change this to the necessary value for your given OracleDB setup
    public static final String JdbcUrl = "jdbc:mysql://46.253.93.12/ptt?characterEncoding=utf8";
    public static final String userName = "admin";
    public static final String password = "adm9n@Z0ne";
}
