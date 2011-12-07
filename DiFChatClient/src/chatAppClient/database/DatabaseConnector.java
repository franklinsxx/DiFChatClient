package chatAppClient.database;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author franklinsxx
 */
public class DatabaseConnector {
    ///////////////////////////////////////////////////////////////////////////////////////////
    // Get friend's ip from DB;
    String driverName;
    String userName;
    String userPasswd;
    String dbName;
    String tableUser;
    String tableFollow;
    String url;
    boolean flag;

    private Statement statement;
    java.sql.Connection connection;
    ResultSet rs;

    public DatabaseConnector(){
        driverName="com.mysql.jdbc.Driver";
        userName="admin";
        userPasswd="dissentfacebook";
        dbName="p2psnsDB";
        tableUser="user";
        tableFollow="follow";
        url="jdbc:mysql://www.franklinsong.com:3306/"+dbName+"?user="+userName+"&password="+userPasswd;
        flag = false;
    }

    public void initConnection() throws SQLException{
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://www.franklinsong.com:3306/p2psnsDB", userName, userPasswd);
            statement = connection.createStatement();

         }catch(ClassNotFoundException e) {
              System.out.println("Driver not found!");
     }
    }

    public void closeConnection() throws SQLException {
        rs.close();
        statement.close();
        connection.close();
    }

    public boolean isFacebookFriend(String friendID, String UserID) throws SQLException{
        System.out.println("|INFO| REMO Test DB_1 friendID and UserID are: " + friendID + " " + UserID);

        initConnection();

        System.out.println("|INFO| REMO Test DB_2 friendID and UserID are: " + friendID + " " + UserID);

        String sql="SELECT * FROM "+tableFollow + " where followerid = " + UserID + " and followeeid = " + friendID + ";";

        rs = statement.executeQuery(sql);

        System.out.println("|INFO| REMO Test DB_3 friendID and UserID are: " + friendID + " " + UserID);

        while(rs.next()) {
            System.out.println("|INFO| REMO Test pre-close connection!");
            closeConnection();
            System.out.println("|INFO| REMO Test connection closed!");
            return true;
        }

        System.out.println("|INFO| REMO Test DB_4 friendID and UserID are: " + friendID + " " + UserID);

    closeConnection();

    System.out.println("|INFO| REMO Test DB_5 friendID and UserID are: " + friendID + " " + UserID);
    return false;
    }

    public boolean isOnChat(String UserID) throws SQLException{
        initConnection();

        String sql="SELECT * FROM "+ tableUser + " where sns_id = " + UserID + ";";
        rs = statement.executeQuery(sql);
        while(rs.next()) {
             if(Integer.parseInt(rs.getString(7))==1){
             closeConnection();
             return true;
            }
        }

        closeConnection();
        return false;

    }

    // uid | name | time | sns_no | sns_id | ip | status
    // sns_no: 0: null; 1: facebook;
    public String getFacebookNameWithSNSID(String snsID, int snsNO) throws SQLException{
        initConnection();
        String userName = null;
        String sql="SELECT * FROM "+tableUser + " where sns_id = " + snsID + ";";

        rs = statement.executeQuery(sql);
        java.sql.ResultSetMetaData rmeta = rs.getMetaData();

        //int numColumns=rmeta.getColumnCount();

        while(rs.next()) {
            userName = rs.getString(2);
        }

        closeConnection();
        return userName;
    }

    public String getFacebookIDWithIP(String IP, int isOnChat) throws SQLException{
        initConnection();
        String userSNSID = "";
        String sql="SELECT * FROM "+tableUser + " where ip = " +  "\"" + IP + "\""  + " and status = 1;";
        if(isOnChat !=1){
            sql="SELECT * FROM "+tableUser + " where ip = " + "\"" + IP + "\"" + " and status = 0;";
        }

        rs = statement.executeQuery(sql);

        //int numColumns=rmeta.getColumnCount();

        while(rs.next()) {
            userSNSID = rs.getString(5);
        }

        closeConnection();
        return userSNSID;
    }

}

