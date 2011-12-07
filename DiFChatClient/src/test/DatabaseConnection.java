/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import chatAppClient.database.DatabaseConnector;
import java.net.SocketException;
import java.sql.SQLException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 *
 * @author franklinsxx
 */
public class DatabaseConnection {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SQLException, SocketException {
        DatabaseConnector dbTester = new DatabaseConnector();
        String name = null;
        String sns_id = "100002190007389";

        name = dbTester.getFacebookNameWithSNSID(sns_id, 1);
        System.out.println(name);

        String follower = "1390695761";
        String followee = "317471";

        if(dbTester.isFacebookFriend(followee, follower)){
            System.out.println("|INFO| they are friends!!!");
        }
        else
            System.out.println("|INFO| they are not friends!!!");

        String hostIP = "localhost";

        Enumeration<NetworkInterface> netInterfaces = null;
        netInterfaces = NetworkInterface.getNetworkInterfaces();
        NetworkInterface ni = netInterfaces.nextElement();
        Enumeration<InetAddress> ips = ni.getInetAddresses();
        while (ips.hasMoreElements()) {
            hostIP = ips.nextElement().getHostAddress();
        }
        System.out.println(hostIP);
        System.out.println(dbTester.getFacebookIDWithIP(hostIP, 1));

    }

}
