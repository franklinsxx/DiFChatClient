package chatAppClient;

import chatAppClient.view.CommonSettings;
import java.awt.Dialog;
import java.awt.TextField;
import java.awt.BorderLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.GridLayout;
import java.awt.Button;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.Choice;
import java.awt.Checkbox;
import java.util.Properties;
import java.io.File;
import java.io.FileOutputStream;
/**
 *
 * @author franklinsxx
 */
public class InformationDialog extends Dialog implements ActionListener, CommonSettings{

    ChatAppClient chatclient;
    public TextField TxtUserName,TxtServerName,TxtServerPort,TxtProxyHost,TxtProxyPort;
    protected Button CmdOk,CmdCancel;
    protected Choice groupchoice;
    public Checkbox isProxyCheckBox;
    public boolean isConnect;
    Properties properties;

    public InformationDialog(ChatAppClient Parent) {
	super(Parent,PRODUCT_NAME+" - Login",true);
        chatclient = Parent;
        setFont(chatclient.TextFont);
        setLayout(new BorderLayout());
        isConnect = false;

        properties = new Properties();
        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream("chatAppClient.conf"));
            }catch(java.io.IOException exc)  { }
            catch(java.lang.NullPointerException NExc)  { }

            addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {setVisible(false);}});

            Panel ButtonPanel = new Panel(new GridLayout(7,2,15,30));
            ButtonPanel.setBackground(chatclient.ColorMap[3]);

            Label LblUserName = new Label("User Name: ");
            TxtUserName = new TextField(properties.getProperty("DiFChatAppUserName"));
            ButtonPanel.add(LblUserName);
            ButtonPanel.add(TxtUserName);

            Label LblServerName = new Label("Server Name: ");

            TxtServerName = new TextField();
            if (properties.getProperty("DiFChatAppServerName") != null)
                TxtServerName.setText(properties.getProperty("DiFChatAppServerName"));
            else
                TxtServerName.setText("www.franklinsong.com");

            ButtonPanel.add(LblServerName);
            ButtonPanel.add(TxtServerName);

            Label LblServerPort = new Label("Server Port: ");
            TxtServerPort = new TextField();
            if (properties.getProperty("DiFChatAppServerPort") != null)
                TxtServerPort.setText(properties.getProperty("DiFChatAppServerPort"));
            else
                TxtServerPort.setText("1436");

            ButtonPanel.add(LblServerPort);
            ButtonPanel.add(TxtServerPort);

            Label LblProxy = new Label("Proxy :");
            isProxyCheckBox = new Checkbox();

            isProxyCheckBox.setState(Boolean.valueOf(properties.getProperty("DiFChatAppProxyState")).booleanValue());

            ButtonPanel.add(LblProxy);
            ButtonPanel.add(isProxyCheckBox);

            Label LblProxyHost = new Label("Proxy Host (Socks): ");
            TxtProxyHost = new TextField();
            TxtProxyHost.setText(properties.getProperty("DiFChatAppProxyHost"));
            ButtonPanel.add(LblProxyHost);
            ButtonPanel.add(TxtProxyHost);

            Label LblProxyPort = new Label("Proxy Port (Socks): ");
            TxtProxyPort = new TextField();
            TxtProxyPort.setText(properties.getProperty("DiFChatAppProxyPort"));
            ButtonPanel.add(LblProxyPort);
            ButtonPanel.add(TxtProxyPort);

            CmdOk = new Button("Connect");
            CmdOk.addActionListener(this);
            CmdCancel = new Button("Quit");
            CmdCancel.addActionListener(this);
            ButtonPanel.add(CmdOk);
            ButtonPanel.add(CmdCancel);

            add("Center",ButtonPanel);

            Panel EmptyNorthPanel = new Panel();
            EmptyNorthPanel.setBackground(chatclient.ColorMap[3]);
            add("North",EmptyNorthPanel);

            Panel EmptySouthPanel = new Panel();
            EmptySouthPanel.setBackground(chatclient.ColorMap[3]);
            add("South",EmptySouthPanel);

            Panel EmptyEastPanel = new Panel();
            EmptyEastPanel.setBackground(chatclient.ColorMap[3]);
            add("East",EmptyEastPanel);

            Panel EmptyWestPanel = new Panel();
            EmptyWestPanel.setBackground(chatclient.ColorMap[3]);
            add("West",EmptyWestPanel);

            setSize(250,400);
            chatclient.show();
            show();

    }

    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource().equals(CmdOk)){
            isConnect = true;
            FileOutputStream fout=null;
            try {
                fout = new FileOutputStream(new File("chatAppClient.conf"));
            }catch(java.io.IOException exc) { }
            if(isProxyCheckBox.getState() == true)
                properties.setProperty("DiFChatAppProxyState","true");
            else
                properties.setProperty("DiFChatAppProxyState","false");
                properties.setProperty("DiFChatAppUserName",TxtUserName.getText());
                properties.setProperty("DiFChatAppServerName",TxtServerName.getText());
                properties.setProperty("DiFChatAppServerPort",TxtServerPort.getText());
                properties.setProperty("DiFChatAppProxyHost",TxtProxyHost.getText());
                properties.setProperty("DiFChatAppProxyPort",TxtProxyPort.getText());
                properties.save(fout,PRODUCT_NAME);
            dispose();
            }

            if (evt.getSource().equals(CmdCancel))
            {
                    isConnect = false;
                    dispose();
            }
    }

}
