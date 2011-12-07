package chatAppClient.view;

import chatAppClient.ChatAppClient;
import java.awt.Panel;
import java.awt.Label;
import java.awt.Frame;
import java.awt.TextField;
import java.awt.Button;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.BorderLayout;

public class PrivateChat extends Frame implements CommonSettings, KeyListener, ActionListener{
    ChatAppClient chatclient;
    public String UserName;
    public MessageCanvas messagecanvas;
    ScrollView MessageScrollView;
    TextField TxtMessage;
    Button CmdSend,CmdClose,CmdIgnore,CmdClear,CmdEmoticons;
    ScrollView EmotionScrollView;
    boolean EmotionFlag;
    Panel EmotionPanel;

    public PrivateChat(ChatAppClient Parent, String ToUserName){
        chatclient = Parent;
        UserName = ToUserName;
        setTitle("Private Chat With " + UserName);
        setBackground(chatclient.ColorMap[0]);
        setFont(chatclient.getFont());
        EmotionFlag = false;
        InitializeComponents();
        //Window close event listener
        addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent evt) {ExitPrivateWindow(); }});
    }

    //Initialize all components
    private void InitializeComponents(){
        setLayout(null);
        Label LblConversation = new Label("Conversation with "+UserName);
        LblConversation.setForeground(chatclient.ColorMap[5]);
        LblConversation.setBounds(5, 30, 400, 20);
        add(LblConversation);

        Panel MessagePanel = new Panel(new BorderLayout());
        messagecanvas = new MessageCanvas(chatclient);
        MessageScrollView = new ScrollView(messagecanvas,true,true,TAPPANEL_CANVAS_WIDTH,TAPPANEL_CANVAS_HEIGHT,SCROLL_BAR_SIZE);
        messagecanvas.scrollview = MessageScrollView;
        MessagePanel.add("Center", MessageScrollView);
        MessagePanel.setBounds(5, 50, 400, 200);
        add(MessagePanel);

        TxtMessage = new TextField();
        TxtMessage.addKeyListener(this);
        TxtMessage.setFont(chatclient.TextFont);
        TxtMessage.setBounds(5, 260, 320, 20);
        add(TxtMessage);

        CmdSend = new CustomButton(chatclient,"Send");
        CmdSend.addActionListener(this);
        CmdSend.setBounds(335, 260, 70, 20);
        add(CmdSend);

        CmdClear = new CustomButton(chatclient,"Clear");
        CmdClear.addActionListener(this);
        CmdClear.setBounds(5, 290, 80, 20);

        CmdIgnore = new CustomButton(chatclient,"Block User");
        CmdIgnore.addActionListener(this);
        CmdIgnore.setBounds(105, 290, 80, 20);

        CmdClose = new CustomButton(chatclient,"Close");
        CmdClose.addActionListener(this);
        CmdClose.setBounds(205, 290, 80, 20);

        add(CmdClear);
        add(CmdIgnore);
        add(CmdClose);

        setSize(PRIVATE_WINDOW_WIDTH,PRIVATE_WINDOW_HEIGHT);
        setResizable(false);
        show();
        this.requestFocus();
    }

    //Action listener
    public void actionPerformed(ActionEvent evt){
        //Msg send
        if(evt.getSource().equals(CmdSend)){
            if (!(TxtMessage.getText().trim().equals("")))
                SendMessage();
        }

        //Button close
        if(evt.getSource().equals(CmdClose)){
            ExitPrivateWindow();
        }

        //Button clear
        if(evt.getSource().equals(CmdClear)){
            messagecanvas.ClearAll();
        }

        //Ignore
        if(evt.getSource().equals(CmdIgnore)){
            if(evt.getActionCommand().equals("Block User")){
                chatclient.tappanel.UserCanvas.IgnoreUser(true,UserName);
                messagecanvas.AddMessageToMessageObject(UserName +" has been ignored!",MESSAGE_TYPE_ADMIN);
                CmdIgnore.setLabel("Allow User");
            }
            else{
                messagecanvas.AddMessageToMessageObject(UserName +" has been removed from ignored list!",MESSAGE_TYPE_ADMIN);
                chatclient.tappanel.UserCanvas.IgnoreUser(false,UserName);
                CmdIgnore.setLabel("Block User");
            }
        }
    }

    //Key listener
    public void keyPressed(KeyEvent evt){
        if((evt.getKeyCode() == 10) && (!(TxtMessage.getText().trim().equals("")))){
                SendMessage();
        }
    }

    public void keyTyped(KeyEvent evt){}
    public void keyReleased(KeyEvent evt){}

    private void SendMessage(){
        messagecanvas.AddMessageToMessageObject(chatclient.UserName+": "+TxtMessage.getText(),MESSAGE_TYPE_DEFAULT);
        chatclient.SentPrivateMessageToServer(TxtMessage.getText(),UserName);
        TxtMessage.setText("");
        TxtMessage.requestFocus();
    }

    //Msg tp msgCanvas
    public void AddMessageToMessageCanvas(String Message){
            messagecanvas.AddMessageToMessageObject(Message,MESSAGE_TYPE_DEFAULT);
    }

    public void DisableAll(){
            TxtMessage.setEnabled(false);
            CmdSend.setEnabled(false);
    }

    public void EnableAll(){
            TxtMessage.setEnabled(true);
            CmdSend.setEnabled(true);
    }

    //Exit
    private void ExitPrivateWindow() {
    	chatclient.RemovePrivateWindow(UserName);
        setVisible(false);
    }

}
