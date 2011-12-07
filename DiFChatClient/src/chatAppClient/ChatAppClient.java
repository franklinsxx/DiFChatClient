package chatAppClient;

import chatAppClient.view.*;
import chatAppClient.database.DatabaseConnector;
import chatAppClient.socket.SocksSocket;
import chatAppClient.socket.SocksSocketImplFactory;
import java.awt.Panel;
import java.awt.Label;
import java.awt.TextField;
import java.awt.Frame;
import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.SocketException;
import java.util.StringTokenizer;
import java.io.Serializable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.DataOutputStream;
import java.net.Socket;
import java.awt.Toolkit;
import java.awt.MenuBar;
import java.awt.Menu;
import java.awt.MenuItem;
import java.io.BufferedInputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author franklinsxx
 */
public class ChatAppClient extends Frame implements Serializable, Runnable, KeyListener, ActionListener, CommonSettings{

    //Global variable declarations
    String AppletStatus,ChatLogo,BannerName,ProxyHost,ServerData,GroupList,SplitString, hostIP;
    public String SNSId,UserName,UserGroup,ServerName;
    public int ServerPort,ProxyPort,TotalUserCount,gLoop;
    boolean StartFlag,IsProxy;
    Socket socket;
    DataInputStream datainputstream;
    DataOutputStream dataoutputstream;
    public Color[] ColorMap;
    Dimension dimension;
    Label InformationLabel;
    StringBuffer stringbuffer;
    public MessageCanvas messagecanvas;
    ScrollView MessageScrollView;
    Thread thread;
    StringTokenizer Tokenizer;
    public TapPanel tappanel;
    TextField TextMsg;
    Button CmdSend,CmdExit;
    public Font TextFont;
    public PrivateChat[] privatewindow;
    public int PrivateWindowCount;
    InformationDialog dialog;
    Toolkit toolkit;
    MenuItem loginitem;
    MenuItem disconnectitem;
    MenuItem seperatoritem;
    MenuItem fbloginitem;
    MenuItem fblogoffitem;
    MenuItem quititem,aboutitem;
    DatabaseConnector dbConnector;

    public static void main(String[] args) throws SocketException, SQLException {
        ChatAppClient mainFrame = new ChatAppClient();
    }

    ChatAppClient() throws SocketException, SQLException{

        hostIP = "localhost";

        Enumeration<NetworkInterface> netInterfaces = null;
        netInterfaces = NetworkInterface.getNetworkInterfaces();
        NetworkInterface ni = netInterfaces.nextElement();
        Enumeration<InetAddress> ips = ni.getInetAddresses();
        while (ips.hasMoreElements()) {
            hostIP = ips.nextElement().getHostAddress();
            System.out.println(hostIP);
            if(hostIP.length() <=15 && hostIP.length()>=7)
                break;
            //System.out.println(hostIP);
        }

        dbConnector = new DatabaseConnector();

        System.out.println("|INFO| " + hostIP);

        toolkit = Toolkit.getDefaultToolkit();
        if(toolkit.getScreenSize().getWidth() > 678){
            setSize(678, 575);
        }
        else
            setSize((int)toolkit.getScreenSize().getWidth(), (int)toolkit.getScreenSize().getHeight()-20);
        setResizable(false);
        dimension = getSize();
        setLayout(new BorderLayout());

        setTitle(PRODUCT_NAME);
        addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent evt) { DisconnectChat(); System.exit(0);}});

        //Menu bar loading
        MenuBar menubar = new MenuBar();
        Menu loginmenu = new Menu("Login");
        loginitem = new MenuItem("Login");
        loginitem.addActionListener(this);
        disconnectitem = new MenuItem("Disconnect");
        disconnectitem.addActionListener(this);
        seperatoritem = new MenuItem("-");
        quititem = new MenuItem("Quit");
        quititem.addActionListener(this);
        loginmenu.add(loginitem);
        loginmenu.add(disconnectitem);

        Menu fbloginmenu = new Menu("Facebook");
        fbloginitem = new MenuItem("App Login");
        fblogoffitem = new MenuItem("App Logoff");
        fbloginitem.addActionListener(this);
        fblogoffitem.addActionListener(this);
        fbloginmenu.add(fbloginitem);
        fbloginmenu.add(fblogoffitem);

        Menu aboutmenu = new Menu("About");
        aboutitem = new MenuItem("About " + PRODUCT_NAME);
        aboutitem.addActionListener(this);
        aboutmenu.add(aboutitem);

        menubar.add(loginmenu);
        menubar.add(fbloginmenu);
        menubar.add(aboutmenu);
        setMenuBar(menubar);


        //Set default values
        UserName = "";
        UserGroup = "";
        SNSId = "";
        ChatLogo  = "";
        //BannerName
        GroupList = "";
        IsProxy = false;
        //Colors
        ColorMap = new Color[MAX_COLOR];
        //Background color
        ColorMap[0] = new Color(224, 236, 254);
        //Info panel background
        ColorMap[1] = new Color(59, 89, 152);
        //Button foreground
        ColorMap[2] = Color.BLACK;
        //Button background
        ColorMap[3] = new Color(224, 236, 254);
        //Tab button
        ColorMap[4] = new Color(59, 89, 152);
        //Msg canvas
        ColorMap[5] = Color.BLACK;
        //Top panel background
        ColorMap[6] = Color.WHITE;
        //Label text colors
        ColorMap[7] = Color.WHITE;

        //Private window initializtion
        privatewindow = new PrivateChat[MAX_PRIVATE_WINDOW];
        PrivateWindowCount = 0;

        SetAppletStatus("");
        //Initializing all the compoents
        InitializeAppletCompnents();

    }

    private void ConnectToServer(){
        //Socket initialize
        messagecanvas.ClearAll();
        messagecanvas.AddMessageToMessageObject("Connecting to chat server, please wait...", MESSAGE_TYPE_ADMIN);
        try{
            if(IsProxy){
                //Proxy
                SocksSocketImplFactory factory = new SocksSocketImplFactory(ProxyHost, ProxyPort);
                SocksSocket.setSocketImplFactory(factory);
                socket = new SocksSocket(ServerName, ServerPort);
                socket.setSoTimeout(0);
            }
         else{
                //Not proxy
                socket = new Socket(ServerName, ServerPort);
         }
            System.out.println("|INFO| " + socket.getInetAddress() + " " + socket.getPort());
            dataoutputstream = new DataOutputStream(socket.getOutputStream());
            //Send HELO to server
            SendMessageToServer("HELO " + UserName);

            System.out.println("|INFO| Data snet HELO + " + UserName);

            datainputstream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            StartFlag = true;
            thread = new Thread(this);
            thread.start();
            EnableAll();

        }catch(IOException _IoExc) { QuitConnection(QUIT_TYPE_NULL);}
    }

    //Chat disconnect
    public void DisconnectChat() {
        if(socket != null)
        {
                messagecanvas.AddMessageToMessageObject("CONNECTION TO THE SERVER CLOSED",MESSAGE_TYPE_ADMIN);
                QuitConnection(QUIT_TYPE_DEFAULT);
        }
    }

     private void fblogoff() throws SQLException, SocketException{
        String driverName="com.mysql.jdbc.Driver";
        String userName="admin";
        String userPasswd="dissentfacebook";
        String dbName="p2psnsDB";
        String tableName="user";
        String url="jdbc:mysql://www.franklinsong.com:3306/"+dbName+"?user="+userName+"&password="+userPasswd;
        /*
        String hostIP = "localhost";
        Enumeration<NetworkInterface> netInterfaces = null;
        netInterfaces = NetworkInterface.getNetworkInterfaces();
        NetworkInterface ni = netInterfaces.nextElement();
        Enumeration<InetAddress> ips = ni.getInetAddresses();

        while (ips.hasMoreElements()) {
            hostIP = ips.nextElement().getHostAddress();
        }*/

        try {
              Class.forName("com.mysql.jdbc.Driver");
              java.sql.Connection connection = DriverManager.getConnection("jdbc:mysql://www.franklinsong.com:3306/p2psnsDB", "admin", "dissentfacebook");

        java.sql.Statement statement = connection.createStatement();
        String sql="SELECT * FROM "+tableName + " where ip=" + "\"" + hostIP + "\";";

        ResultSet rs = statement.executeQuery(sql);

        rs = statement.executeQuery(sql);
        while(rs.next()){
            sql="update " + tableName +" set status= 0 WHERE uid=\"" + rs.getString(1) + "\";";
            java.sql.Statement statementTemp = connection.createStatement();
            statementTemp.executeUpdate(sql);
            statementTemp.close();
            System.out.println("|INFO| DB user status set to 0");
        }

        rs.close();
        statement.close();
        connection.close();
         }
             catch(ClassNotFoundException e) {
              System.out.println("Driver not found!");
         }
    }


    private void SetAppletStatus(String Message){
        if (messagecanvas != null)
        messagecanvas.AddMessageToMessageObject(Message,MESSAGE_TYPE_ADMIN);
    }

    private void InitializeAppletCompnents() throws SQLException {

        //Common settings
        setBackground(ColorMap[0]);
        Font font = new Font("Dialog", Font.BOLD, 11);
        TextFont = new Font("Dialog", 0, 11);
        setFont(font);

        //Top Panel
        Panel TopPanel = new Panel(new BorderLayout());
        TopPanel.setBackground(ColorMap[6]);

        //Info label panel
        Panel CenterPanel = new Panel(new BorderLayout());
        Panel InformationPanel = new Panel(new BorderLayout());
        InformationPanel.setBackground(ColorMap[1]);
        InformationLabel = new Label();
        InformationLabel.setAlignment(1);
        UpdateInformationLabel();
        InformationPanel.setForeground(ColorMap[7]);
        InformationPanel.add("Center", InformationLabel);
        CenterPanel.add("North", InformationPanel);

        //Msg canvas
        Panel MsgPanel = new Panel(new BorderLayout());
        messagecanvas = new MessageCanvas(this);
        MessageScrollView = new ScrollView(messagecanvas, true, true, TAPPANEL_CANVAS_WIDTH,TAPPANEL_CANVAS_HEIGHT,SCROLL_BAR_SIZE);
        messagecanvas.scrollview = MessageScrollView;
        MsgPanel.add("Center", MessageScrollView);

        tappanel = new TapPanel(this);
        MsgPanel.add("East", tappanel);
        CenterPanel.add("Center", MsgPanel);

        //Input panel
        Panel InputPanel = new Panel(new BorderLayout());
        Panel TextBoxPanel = new Panel(new BorderLayout());
        Label LblGeneral = new Label("Enter Message:");
        TextMsg = new TextField();
        TextMsg.addKeyListener(this);
        TextMsg.setFont(TextFont);
        CmdSend = new CustomButton(this, " Send Message ");
        CmdSend.addActionListener(this);
        TextBoxPanel.add("West", LblGeneral);
        TextBoxPanel.add("Center", TextMsg);
        TextBoxPanel.add("East", CmdSend);
        InputPanel.add("Center", TextBoxPanel);

        Panel InputButtonPanel = new Panel(new BorderLayout());
        CmdExit = new CustomButton(this, "      Exit Chat      ");
        CmdExit.addActionListener(this);
        InputButtonPanel.add("Center", CmdExit);
        InputPanel.add("East", InputButtonPanel);
        Panel EmptyPanel = new Panel();
        InputPanel.add("South", EmptyPanel);
        CenterPanel.add("South", InputPanel);
        add("Center", CenterPanel);
        DisableAll();
        LoginToChat();

    }

    private void SendMessageToServer(String Msg) {
        try{
            dataoutputstream.writeBytes(Msg + "\r\n");
        }catch(IOException _IoExc) {
            QuitConnection(QUIT_TYPE_DEFAULT);
        }
    }

    //Enable all chat components
    private void EnableAll() {
        TextMsg.setEnabled(true);
        CmdSend.setEnabled(true);
        tappanel.enable(true);
        disconnectitem.setEnabled(true);
        loginitem.setEnabled(false);
        fbloginitem.setEnabled(true);
        fblogoffitem.setEnabled(true);
    }

    //Connection quit
    private void QuitConnection(int QuitType) {
        if(socket != null){
            try{
                if(QuitType == QUIT_TYPE_DEFAULT)
                    SendMessageToServer("QUIT " + UserName + "~" + UserGroup);
                if(QuitType == QUIT_TYPE_KICK)
                    SendMessageToServer("KICK " + UserName + "~" + UserGroup);
                socket.close();
                socket = null;
                tappanel.UserCanvas.ClearAll();
            }catch(IOException _ToExc) {}
        }
        if(thread != null){
            thread.stop();
            thread = null;
        }
        DisableAll();
        StartFlag = false;
        SetAppletStatus("ADMIN: CONNECTION TO THE SERVER CLOSED.");
    }

    //Disable and reset all chat components
    private void DisableAll() {
        TextMsg.setEnabled(false);
        CmdSend.setEnabled(false);
        tappanel.enable(false);
        disconnectitem.setEnabled(false);
        loginitem.setEnabled(true);
        fbloginitem.setEnabled(true);
        fblogoffitem.setEnabled(true);
        UserName = "";
        UserGroup = "";
        TotalUserCount = 0;
    }

    private void LoginToChat() throws SQLException {
        //Dialog open
        SNSId = dbConnector.getFacebookIDWithIP(hostIP, 1);
        System.out.println("|INFO| Facebook id: " + SNSId);
        dialog = new InformationDialog(this);
        if(dialog.isConnect == true){
            if(!SNSId.equals("")){
                UserName = dialog.TxtUserName.getText() + "HasSNSID" + SNSId;
            }
            else{
                UserName = dialog.TxtUserName.getText();
            }
            ServerName = dialog.TxtServerName.getText();
            ServerPort = Integer.parseInt(dialog.TxtServerPort.getText());
            if(dialog.isProxyCheckBox.getState()==true){
                IsProxy = true;
                ProxyHost = dialog.TxtProxyHost.getText();
                ProxyPort = Integer.parseInt(dialog.TxtProxyPort.getText());
            }
            else {
                IsProxy = false;
            }
            ConnectToServer();
        }
    }

    //Events

    //Button
    public void actionPerformed(ActionEvent evt) {
        if(evt.getSource().equals(CmdSend)){
            if(!(TextMsg.getText().trim().equals("")));
            SendMessage();
        }
        if((evt.getSource().equals(CmdExit)) || (evt.getSource().equals(quititem))){
            try {
                fblogoff();
            } catch (SQLException ex) {
                Logger.getLogger(ChatAppClient.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SocketException ex) {
                Logger.getLogger(ChatAppClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            DisconnectChat();
            System.exit(0);
        }
        if(evt.getSource().equals(loginitem)){
            try {
                LoginToChat();
            } catch (SQLException ex) {
                Logger.getLogger(ChatAppClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if(evt.getSource().equals(disconnectitem)){
            DisconnectChat();
        }
        if(evt.getSource().equals(fbloginitem)){
            try{
                Runtime.getRuntime().exec("xdg-open" + " http://apps.facebook.com/ptopsnsyale");
            }catch (Exception ex){
            }
        }

        if(evt.getSource().equals(fblogoffitem)){
            try{
                fblogoff();
            }catch(SQLException ex){
                Logger.getLogger(ChatAppClient.class.getName()).log(Level.SEVERE, null, ex);
            }catch(SocketException ex){
                Logger.getLogger(ChatAppClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if(evt.getSource().equals(aboutitem)){
            MessageBox msgbox = new MessageBox(this, false);
            msgbox.AddMessage("~~13 "+PRODUCT_NAME);
            msgbox.AddMessage("Developed by Franklin Song");
            msgbox.AddMessage(COMPANY_NAME);
            msgbox.AddMessage("Thanks to Jeeva S");
        }
    }

    //Key listener
    public void keyPressed(KeyEvent evt) {
    	if((evt.getKeyCode() == 10) && (!(TextMsg.getText().trim().equals("")))){
            SendMessage();
        }
    }

    //
    public void keyTyped(KeyEvent ke) {
    }

    public void keyReleased(KeyEvent ke) {
    }

    private void SendMessage() {
    //Send msg to server
        SendMessageToServer("MESS " + UserGroup + "~" + UserName + ": " + TextMsg.getText());
        messagecanvas.AddMessageToMessageObject(UserName + ": " + TextMsg.getText(), MESSAGE_TYPE_DEFAULT);
        TextMsg.setText("");
        TextMsg.requestFocus();
    }

    //Information label update functions
    private void UpdateInformationLabel() {
        stringbuffer = new StringBuffer();
        stringbuffer.append("User Name: ");
        stringbuffer.append(UserName);
        stringbuffer.append("       ");
        stringbuffer.append("Current Group : ");
        stringbuffer.append(UserGroup);
        stringbuffer.append("       ");
        stringbuffer.append("Total Users: ");
        stringbuffer.append(TotalUserCount);
        stringbuffer.append("       ");

        //stringbuffer.append("User Name: ").append(UserName).append("       " + "Current Group : ").append(UserGroup).append("       " + "Total Users: ").append(TotalUserCount).append("       ");
        InformationLabel.setText(stringbuffer.toString());
     }

    //Thread
    public void run() {
        while(thread != null){
            try{
                ServerData = datainputstream.readLine();
                System.out.println("|INFO| Data received: " + ServerData.toString());
                //List UserName rfc
                if(ServerData.startsWith("LIST")){
                    Tokenizer = new StringTokenizer(ServerData.substring(5), ";");
                    //Information label update
                    TotalUserCount = Tokenizer.countTokens();
                    UpdateInformationLabel();
                    //Add user item into user canvas
                    tappanel.UserCanvas.ClearAll();

                    while(Tokenizer.hasMoreTokens()){
                        System.out.println("|INFO| Temp Data test");
                        String temp = Tokenizer.nextToken();
                        System.out.println("|INFO| Temp Data received: " + temp);
                        //tappanel.UserCanvas.AddListItemToMessageObject(Tokenizer.nextToken());
                        tappanel.UserCanvas.AddListItemToMessageObject(temp);
                    }
                    messagecanvas.ClearAll();
                    messagecanvas.AddMessageToMessageObject("Welcome to the " + UserGroup + " Group!", MESSAGE_TYPE_JOIN);
                    //Facebook app info
                    /*
                    String hostIP = "localhost";
                    Enumeration<NetworkInterface> netInterfaces = null;
                    netInterfaces = NetworkInterface.getNetworkInterfaces();
                    NetworkInterface ni = netInterfaces.nextElement();
                    Enumeration<InetAddress> ips = ni.getInetAddresses();
                    while (ips.hasMoreElements()) {
                        hostIP = ips.nextElement().getHostAddress();
                    }*/

                    ///////////////////////////////////////////////////////////////////////////////////////////
                    // Get Users social meta information from DB. I will develop a new server for handle such kinds of query so that client side app won't have interact with server DB
                    String driverName="com.mysql.jdbc.Driver";
                    String userName="admin";
                    String userPasswd="dissentfacebook";
                    String dbName="p2psnsDB";
                    String tableName="user";
                    String url="jdbc:mysql://www.franklinsong.com:3306/"+dbName+"?user="+userName+"&password="+userPasswd;
                    boolean flag = false;

                    try {
                        Class.forName("com.mysql.jdbc.Driver");
                        java.sql.Connection connection = DriverManager.getConnection("jdbc:mysql://www.franklinsong.com:3306/p2psnsDB", "admin", "dissentfacebook");

                        java.sql.Statement statement = connection.createStatement();
                        String sql="SELECT * FROM "+tableName;

                        ResultSet rs = statement.executeQuery(sql);
                        java.sql.ResultSetMetaData rmeta = rs.getMetaData();

                        int numColumns=rmeta.getColumnCount();
                        // uid | name | time | sns_no | sns_id | ip | status
                        // sns_no: 0: null; 1: facebook;
                        // appendToChatBox("uid | name | IP address | status\n");
                        messagecanvas.AddMessageToMessageObject("|INFO| Facebook Metadata: | SNS_ID | Name | IP address | Status |\n", MESSAGE_TYPE_JOIN);

                        while(rs.next()) {
                           //appendToChatBox(rs.getString(1)+ " | " + rs.getString(2) + " | " + rs.getString(3) + " | " + rs.getString(4) + "\n");
                            messagecanvas.AddMessageToMessageObject(rs.getString(5)+ " | "  + rs.getString(2) + " | " + rs.getString(6) + " | " + rs.getString(7)+ "\n", MESSAGE_TYPE_JOIN);
                        }

                        sql = "SELECT * FROM "+tableName + " where ip=" + "\"" + hostIP + "\";";
                        rs = statement.executeQuery(sql);
                        while(rs.next()){
                             if(Integer.parseInt(rs.getString(7))==1){
                                 flag = true;
                                 messagecanvas.AddMessageToMessageObject("Your current login Facebook id is:" + rs.getString(5) +  "\n", MESSAGE_TYPE_JOIN);
                                 SNSId = rs.getString(5);
                            }
                        }
                        if(!flag){
                            messagecanvas.AddMessageToMessageObject("You are not currently logged in Facebook."  +  "\n", MESSAGE_TYPE_JOIN);
                        }
                        messagecanvas.AddMessageToMessageObject("Your current login IP address is:" + hostIP +  "\n", MESSAGE_TYPE_JOIN);
                        rs.close();
                        statement.close();
                        connection.close();
                     }catch(ClassNotFoundException e) {
                          System.out.println("Driver not found!");
                 }
                }

                //RFC
                //Group
                if(ServerData.startsWith("GROUP")){
                    //Group canvas loading
                    Tokenizer = new StringTokenizer(ServerData.substring(5), ";");
                    UserGroup = Tokenizer.nextToken();
                    UpdateInformationLabel();
                    //User canvas adding
                    tappanel.GroupCanvas.ClearAll();
                    tappanel.GroupCanvas.AddListItemToMessageObject(UserGroup);
                    while(Tokenizer.hasMoreTokens()){
                        tappanel.GroupCanvas.AddListItemToMessageObject(Tokenizer.nextToken());
                    }
                }

                //Add
                if(ServerData.startsWith("ADD")){
                    //Information label update
                    TotalUserCount ++;
                    UpdateInformationLabel();
                    //User canvas adding
                    SplitString = ServerData.substring(5);
                    EnablePrivateWindow(SplitString);
                    //Add new comer
                    tappanel.UserCanvas.AddListItemToMessageObject(SplitString);
                    messagecanvas.AddMessageToMessageObject(SplitString + " joins chat...", MESSAGE_TYPE_JOIN);
                }

                //User name conflicts
                if(ServerData.startsWith("EXIS")){
                    messagecanvas.AddMessageToMessageObject("Sorry, this user name already exists. Please use other name!", MESSAGE_TYPE_ADMIN);
                    thread = null;
                    QuitConnection(QUIT_TYPE_NULL);
                }

                //Remove
                if(ServerData.startsWith("REMO")){
                    //System.out.println("|INFO| REMO test_2 " + ServerData);
                    SplitString = ServerData.substring(5);
                    tappanel.UserCanvas.RemoveListItem(SplitString);
                    RemoveUserFromPrivateChat(SplitString);
                    messagecanvas.AddMessageToMessageObject(SplitString + " has been logged out from chat!", MESSAGE_TYPE_LEAVE);

                    //Info label update
                    TotalUserCount --;
                    UpdateInformationLabel();
                }

                //Mess
                if(ServerData.startsWith("MESS")){
                    //Check whether ignored user
                    if(!(tappanel.UserCanvas.IsIgnoredUser(ServerData.substring(5, ServerData.indexOf(":"))))){
                        messagecanvas.AddMessageToMessageObject(ServerData.substring(5), MESSAGE_TYPE_DEFAULT);
                    }
                }

                //Kick out
                if(ServerData.startsWith("KICK")){
                    messagecanvas.AddMessageToMessageObject("You are kicked out from chat for flooding message", MESSAGE_TYPE_ADMIN);
                    thread = null;
                    QuitConnection(QUIT_TYPE_KICK);
                }

                //Kick off user info
                if(ServerData.startsWith("INKI")){
                    SplitString = ServerData.substring(5);
                    tappanel.UserCanvas.RemoveListItem(SplitString);
                    RemoveUserFromPrivateChat(SplitString);
                    messagecanvas.AddMessageToMessageObject(SplitString + " has been kicked out by the Admin", MESSAGE_TYPE_ADMIN);

                    //Info label update
                    TotalUserCount --;
                    UpdateInformationLabel();
                }

                //Change group
                if(ServerData.startsWith("CHGO")){
                    UserGroup = ServerData.substring(5);
                }

                //Join group
                if(ServerData.startsWith("JOGO")){
                    SplitString = ServerData.substring(5);
                    tappanel.UserCanvas.AddListItemToMessageObject(SplitString);
                    //Info label update
                    TotalUserCount ++;
                    UpdateInformationLabel();
                    messagecanvas.AddMessageToMessageObject(SplitString + " joins chat...", MESSAGE_TYPE_JOIN);
                }

                //Leave group
                if(ServerData.startsWith("LEGO")){
                    SplitString = ServerData.substring(5, ServerData.indexOf("~"));
                    tappanel.UserCanvas.RemoveListItem(SplitString);
                    messagecanvas.AddMessageToMessageObject(SplitString + " has left " + UserGroup + " group and joined into "+ServerData.substring(ServerData.indexOf("~")+1)+" group",MESSAGE_TYPE_ADMIN);
                    TotalUserCount --;
                    UpdateInformationLabel();
                }

                //Group count
                if(ServerData.startsWith("GOCO")){
                    SplitString = ServerData.substring(5, ServerData.indexOf("~"));
                    tappanel.TxtUserCount.setText("Total users in " + SplitString + " : " + ServerData.substring(ServerData.indexOf("~")+1));
                }

                //Private msg
                if(ServerData.startsWith("PRIV")){
                    SplitString = ServerData.substring(5, ServerData.indexOf(":"));
                    //Ignored user check
                    if(!(tappanel.UserCanvas.IsIgnoredUser(SplitString))){
                        boolean PrivateFlag = false;
                        for(gLoop = 0; gLoop < PrivateWindowCount; gLoop++){
                            if(
                                privatewindow[gLoop].UserName.equals(SplitString)){
                                privatewindow[gLoop].AddMessageToMessageCanvas(ServerData.substring(5));
                                privatewindow[gLoop].show();
                                privatewindow[gLoop].requestFocus();
                                PrivateFlag = true;
                                break;
                            }
                        }
                        if(!(PrivateFlag)){
                            if(PrivateWindowCount >= MAX_PRIVATE_WINDOW){
                                messagecanvas.AddMessageToMessageObject("You have opened too many private chat windows!", MESSAGE_TYPE_ADMIN);
                            }
                            else{
                                privatewindow[PrivateWindowCount++] = new PrivateChat(this,SplitString);
                                privatewindow[PrivateWindowCount-1].AddMessageToMessageCanvas(ServerData.substring(5));
                                privatewindow[PrivateWindowCount-1].show();
                                privatewindow[PrivateWindowCount-1].requestFocus();
                            }
                        }
                    }
                }
            }catch(Exception _Exc) {
                messagecanvas.AddMessageToMessageObject(_Exc.getMessage(),MESSAGE_TYPE_ADMIN);QuitConnection(QUIT_TYPE_DEFAULT);
            }
        }
    }

    //Enable the private chat when the end user looged out
    private void EnablePrivateWindow(String ToUserName) {
        for(gLoop = 0; gLoop < PrivateWindowCount; gLoop++){
            if(privatewindow[gLoop].UserName.equals(ToUserName)){
                privatewindow[gLoop].messagecanvas.AddMessageToMessageObject(ToUserName + " is currently online.", MESSAGE_TYPE_ADMIN);
                privatewindow[gLoop].EnableAll();
                return;
            }
        }
    }

    private void RemoveUserFromPrivateChat(String ToUserName) {
        for(gLoop = 0; gLoop < PrivateWindowCount; gLoop ++){
            if(privatewindow[gLoop].UserName.equals(ToUserName)){
                privatewindow[gLoop].messagecanvas.AddMessageToMessageObject(ToUserName + "is currently offline", MESSAGE_TYPE_ADMIN);
                privatewindow[gLoop].DisableAll();
                return;
            }
        }
    }

    //Msg private sender from client to server
    public void SentPrivateMessageToServer(String Message, String ToUserName){
        SendMessageToServer("PRIV " + ToUserName + "~" + UserName + ": " + Message);
    }

    //Private window remove
    public void RemovePrivateWindow(String ToUserName){
        int userIndex = 0;
        for(gLoop = 0; gLoop < PrivateWindowCount; gLoop ++){
            userIndex ++;
            if(privatewindow[gLoop].UserName.equals(ToUserName)) break;
        }
        for(int iLoop = userIndex; iLoop < PrivateWindowCount; iLoop++){
            privatewindow[iLoop] = privatewindow[iLoop+1];
        }
        PrivateWindowCount --;
    }

    //Group change
    public void ChangeGroup(){
        if(tappanel.GroupCanvas.SelectedUser.equals("")){
            messagecanvas.AddMessageToMessageObject("Invalid Group Selection!", MESSAGE_TYPE_ADMIN);
            return;
        }
        if(tappanel.GroupCanvas.SelectedUser.equals(UserGroup)){
            messagecanvas.AddMessageToMessageObject("You are already in the group!", MESSAGE_TYPE_ADMIN);
            return;
        }
        SendMessageToServer("CHGO " + UserName + "~" + tappanel.GroupCanvas.SelectedUser);
    }

    //Number of users in same group request
    public void GetGroupUserNumber(String GroupName){
        SendMessageToServer("GOCO " + GroupName);
    }

    //Chat image name adding
    public void AddImageToTextField(String ImageName){
        // addtional feature
    }


}
