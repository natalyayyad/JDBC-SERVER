/**
 * Created by Joshua on 12/1/2016.
 * And also taken by some code written by the TAs (i.e. Jeremy) in CSC 335
 */

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.*;

import static java.lang.System.exit;

public class JDBC_Server {

    private static ServerSocket sock;
    private static List<ObjectOutputStream> clients = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) throws IOException {

        //Initialize the database connection
        //The account used for development is "admin";"adm9n@Z0ne"
        String username;
        String password;

        Connection dbconn = null;

        //Try to load the driver to connect to the SQL Database server
        //Basically, calling Class.forName("str") initializes/loads a given class in memory with the default ClassLoader
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            exit(-1);
        }

        while (dbconn == null) {
//            Scanner myScanner = new Scanner(System.in);
//
//            System.out.print("Username: ");
//            username = myScanner.next();
//            //TODO: If eventually using this as a true commandline application, migrate this to a System.console.readPassword() function call to hide user input
//            //System.console.readPassword() doesn't work in most IDE command lines, so that's why I'm not using it now
//            System.out.print("Password: ");
//            password = myScanner.next();
//            //TODO: If readPassword is infeasible on the destination system, at least put some kind of platform-dependent print command here that clears the screen.

            //Try to actually connect to the SQL Database server
            try {
                dbconn = DriverManager.getConnection(Configuration.JdbcUrl, Configuration.userName, Configuration.password);
            } catch (SQLException e) {
                System.out.println("Login to database failed...\n");
                System.out.flush();
            }
        }

        //So if we've made it this far, we've connected to the database. Now, just startup the remaining server pieces
        //Open the ServerSocket
        sock = new ServerSocket(Configuration.SERVER_PORT);

        System.out.println("Server started on port " + Configuration.SERVER_PORT);

        while (true) {
            // Accept incoming connections from the ServerSocket.
            Socket s = sock.accept();

            //Print out the IP of our new client
            System.out.println("Accepted a new connection from " + s.getInetAddress());

            //Note: Make sure that if the order is "InputStream OutputStream" on the server, they're initialized as "OutputStream InputStream" on the client
            //These are blocking operations that require the other side to be essentially requesting the opposite pair for each one to proceed
            ObjectInputStream is = new ObjectInputStream(s.getInputStream());
            ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream());

            // Save the output stream to our clients list, so we can broadcast to this client later
            // Pretty sure this really isn't needed here (leftover from when this was a chat server), but I'll remove it later
            clients.add(os);
            if (Configuration.DebugMode)
                System.out.println("Added it to list of clients!");

            // Start a new ClientHandler thread for this client.
            ClientHandler c = new ClientHandler(is, os, dbconn);
            c.start();
            if (Configuration.DebugMode)
                System.out.println("Started ClientHandler!");
        }
    }

}



