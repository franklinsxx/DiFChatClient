package chatAppClient.view;

import chatAppClient.ChatAppClient;
import java.awt.*;
import java.awt.event.*;

public class TapPanel extends Panel implements CommonSettings,ActionListener{

    ChatAppClient chatclient;
    public TextField TxtUserCount;
    ScrollView UserScrollView,GroupScrollView;
    public ListViewCanvas UserCanvas,GroupCanvas;
    Button CmdChangeGroup,CmdIgnoreUser,CmdSendDirect ;

    public TapPanel(ChatAppClient parent){

      chatclient = parent;
      Panel Tappanel = new Panel(new BorderLayout());
      CardLayout cardlayout = new CardLayout();
      Panel MainPanel = new Panel(cardlayout);

      //User panel
      Panel UserPanel = new Panel(new BorderLayout());
      UserCanvas = new ListViewCanvas(chatclient,USER_CANVAS);
      UserScrollView = new ScrollView(UserCanvas,true,true,TAPPANEL_CANVAS_WIDTH,TAPPANEL_CANVAS_HEIGHT,SCROLL_BAR_SIZE);
      UserCanvas.scrollview = UserScrollView;
      UserPanel.add("Center",UserScrollView);
      Panel UserButtonPanel = new Panel(new BorderLayout());
      CmdSendDirect = new CustomButton(chatclient,"Send Direct Message");
      CmdSendDirect.addActionListener(this);
      UserButtonPanel.add("North",CmdSendDirect);
      CmdIgnoreUser = new CustomButton(chatclient,"Block User");
      CmdIgnoreUser.addActionListener(this);
      UserButtonPanel.add("Center",CmdIgnoreUser);
      UserPanel.add("South",UserButtonPanel);

      //Group panel
      Panel GroupPanel = new Panel(new BorderLayout());
      GroupCanvas = new ListViewCanvas(chatclient, GROUP_CANVAS);
      GroupScrollView = new ScrollView(GroupCanvas,true,true,TAPPANEL_CANVAS_WIDTH,TAPPANEL_CANVAS_HEIGHT,SCROLL_BAR_SIZE);
      GroupCanvas.scrollview = GroupScrollView;
      GroupPanel.add("Center",GroupScrollView);

      Panel GroupButtonPanel = new Panel(new BorderLayout());
      Panel GroupCountPanel = new Panel(new BorderLayout());
      Label LblCaption = new Label("GROUP STATUS",1);
      GroupCountPanel.add("North",LblCaption);
      TxtUserCount = new TextField();
      TxtUserCount.setEditable(false);
      GroupCountPanel.add("Center",TxtUserCount);
      GroupButtonPanel.add("Center",GroupCountPanel);
      CmdChangeGroup = new CustomButton(chatclient,"Change Group");
      CmdChangeGroup.addActionListener(this);
      GroupButtonPanel.add("South",CmdChangeGroup);
      GroupPanel.add("South",GroupButtonPanel);

      //Main panel
      MainPanel.add("UserPanel",UserPanel);
      MainPanel.add("GroupPanel",GroupPanel);
      cardlayout.show(MainPanel,"UserPanel");
      BorderPanel borderpanel = new BorderPanel(this,chatclient,cardlayout,MainPanel,TAPPANEL_WIDTH,TAPPANEL_HEIGHT);

      borderpanel.addTab("USERS","UserPanel");
      borderpanel.addTab("GROUPS","GroupPanel");

      Tappanel.add(borderpanel);
      add("Center",Tappanel);
    }


    public void actionPerformed(ActionEvent evt){
        if(evt.getSource().equals(CmdChangeGroup)){
            //Change Group
            chatclient.ChangeGroup();
        }

        if(evt.getSource().equals(CmdIgnoreUser)){
            if(evt.getActionCommand().equals("Block User")){
                UserCanvas.IgnoreUser(true);
            }
            else{
                UserCanvas.IgnoreUser(false);
            }
        }

        if(evt.getSource().equals(CmdSendDirect)){
            UserCanvas.SendDirectMessage();
        }
    }

}
