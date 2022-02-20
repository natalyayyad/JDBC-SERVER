import com.example.andrd_ado_vdo_tkbk_demo.db.DBCommand;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.common.base.Strings;


/**
 * Handles all the things associated with serving a client while allowing the server to continue accepting new ones
 */
class ClientHandler extends Thread {

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Connection dbconn;



    public ClientHandler(ObjectInputStream input, ObjectOutputStream output, Connection dbconn) {
        this.input = input;
        this.output = output;
        this.dbconn = dbconn;
    }

    @Override
    public void run() {
        // Read a String from the client that will be used to construct our SQL command
        try {
            DBCommand command = (DBCommand) input.readObject();
            executeCommand(command);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            this.cleanup();
            return;
        }
    }

    private void executeCommand (DBCommand command){
        //Now that we have an input command string, let's build an SQL statement out of it, execute it, and return the results to the client
        try {
            System.out.println("Received command: " + command.getName());
            System.out.println("Received parameter: " + command.getParameter());
//            if (Configuration.DebugMode)
//                System.out.println("Received command: " + command.getName());

            //Use PreparedStatements to help prevent SQL Injection
            //https://www.owasp.org/index.php/SQL_Injection_Prevention_Cheat_Sheet

            String commandName = command.getName();
            //This is the query we want to execute based on the command received
            String query = null;
            if(commandName.equals("getUserInfo")){
                query = "SELECT * FROM Devices WHERE user = ? ";
            }
            else if(commandName.equals("getGroupList")){
                query = "SELECT DISTINCT Port, Group FROM Devices WHERE Company = ? ";
            }
            else if(commandName.equals("getUsers")){
                query = "SELECT * FROM Devices WHERE Company_id = ? ";
            }

            //So prepare a statement for it so that we know anything else we add is to be interpreted as data, not SQL commands
            PreparedStatement pstmt = dbconn.prepareStatement(query);
            //Set the prepared statement parameter
            pstmt.setString(1, command.getParameter());
            //And execute the SQL query
            ResultSet rs = pstmt.executeQuery();

//  printing result
//            ResultSetMetaData rsmd = rs.getMetaData();
//            int columnsNumber = rsmd.getColumnCount();
//            while (rs.next()) {
//                for (int i = 1; i <= columnsNumber; i++) {
//                    if (i > 1) System.out.print(",  ");
//                    String columnValue = rs.getString(i);
//                    System.out.print(columnValue + " " + rsmd.getColumnName(i));
//                }
//                System.out.println("");
//            }

            if(rs.next()){
                System.out.println("There was a match in the ptt database");
               Map<String, Object> result = resultSet2Map(rs);

//              Physically send the results back to the client
                output.writeObject(result);
                System.out.println("Replied back with a Map<String, Object>> of " + result.size() + " elements");
//                Tear down the connection
//                Even though, without this, it manages to tear itself down anyway by trying to read a second parameter and failing
                cleanup();
            }
            else {
                System.out.println("There was not a match in the ptt database");
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            this.cleanup();
        }
    }

    private static Map<String, Object> resultSet2Map(ResultSet rs) {
        Map<String, Object> map = null;
        if (rs != null) {
            map = new HashMap<String, Object>();
            try {
                ResultSetMetaData rsMetaData = rs.getMetaData();
                int columnCount = rsMetaData.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    String cName = rsMetaData.getColumnLabel(i);
                    if (!Strings.isNullOrEmpty(cName)) {
                        map.put(cName, rs.getString(cName));
                    }//from   w  ww.  j a  v a2  s. c o m
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    /**
     * Convert the ResultSet to a List of Maps, where each Map represents a row with columnNames and columValues
     * @param rs - > ResultSet
     * @return rows - > List<Map<String, Object>>
     * @throws SQLException
     */
    private List<Map<String, Object>> resultSetToList(ResultSet rs) throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        int columns = md.getColumnCount();
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        while (rs.next()){
            Map<String, Object> row = new HashMap<String, Object>(columns);
            for(int i = 1; i <= columns; ++i){
                row.put(md.getColumnName(i), rs.getObject(i));
            }
            rows.add(row);
        }
        for (Map<String, Object> row:rows) {
            for (Map.Entry<String, Object> rowEntry : row.entrySet()) {
                System.out.print(rowEntry.getKey() + " = " + rowEntry.getValue() + ", ");
            }

        } // OUTER FOR-LOOP ENDs
        return rows;
    }

    // Closes all the resources of a ClientHandler and logs a message.
    // It is called from every place that a fatal error occurs in ClientHandler (the catch blocks that
    // you can't recover from).
    private void cleanup() {
        try {
            this.input.close();
            this.output.close();
            //DON'T close dbconn as it is the server's connection to the database itself.
            System.out.println("Closed connection!");
            if (Configuration.DebugMode)
                System.out.println("Closed connection!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
