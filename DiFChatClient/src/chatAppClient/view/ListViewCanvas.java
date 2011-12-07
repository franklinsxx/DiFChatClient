package chatAppClient.view;


import chatAppClient.ChatAppClient;
import chatAppClient.database.DatabaseConnector;
import java.awt.Dimension;
import java.awt.Canvas;
import java.sql.SQLException;
import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.Event;
import java.awt.Image;
import java.awt.Color;
import java.awt.FontMetrics;

public class ListViewCanvas extends Canvas implements CommonSettings {

    Dimension offDimension, dimension;
    Image offImage;
    Graphics offGraphics;
    ChatAppClient chatclient;
    ArrayList<MessageObject> ListArray;
    int xOffset, yOffset;
    MessageObject messageobject;
    ScrollView scrollview;
    FontMetrics fontmetrics;
    int CanvasType, TotalWidth, TotalHeight;
    public String SelectedUser;
    DatabaseConnector dbCon;
    String mySNSId;
    String realName;
    String friendSNSId;

    //Image canvas
    ListViewCanvas(ChatAppClient Parent,int canvastype) {
        chatclient = Parent;
        dimension = size();
        ListArray = new ArrayList();
        SelectedUser = "";
        realName = "";
        friendSNSId = "";
        CanvasType = canvastype;
        setFont(chatclient.getFont());
        fontmetrics = chatclient.getFontMetrics(chatclient.getFont());
        dbCon = new DatabaseConnector();

    }

    public void AddListItemToMessageObject(String ListItem) throws SQLException {
        int startY = DEFAULT_LIST_CANVAS_POSITION;
        if(ListArray.size() > 0){
            //messageobject = (MessageObject) ListArray.get(ListArray.size()-1);
            messageobject = ListArray.get(ListArray.size()-1);
            startY = messageobject.StartY + DEFAULT_LIST_CANVAS_INCREMENT;
        }
        System.out.println("|INFO| ListItem check: " + ListItem);
        // Check if the new comer is friend or not
        //String friendSNSId = "";
        mySNSId = chatclient.SNSId;

        /*
        if(ListItem.contains("HasSNSID")){
            String temp[];
            temp = ListItem.split("HasSNSID");
            realName = temp[0];
            friendSNSId = temp[1];

            System.out.println("|INFO| relationship test: " + ListItem + " friend: " + friendSNSId + " my: " + mySNSId);
        }
         */

        //System.out.println("|INFO| REMO Test 4");

        messageobject = new MessageObject();
        messageobject.Message = ListItem;
        //System.out.println("|INFO| REMO Test ListItem check_2 " + ListItem);
        messageobject.StartY  = startY;
        messageobject.Selected = false;
        messageobject.Width = fontmetrics.stringWidth(ListItem) + 3 +DEFAULT_LIST_CANVAS_INCREMENT;
        ListArray.add(messageobject);

        //System.out.println("|INFO| REMO Test 41");
        if(!mySNSId.equals("")){
            System.out.println("|INFO| REMO Test 42");
                    //System.out.println("|INFO| REMO Test list size: " + ListArray.size());
                for(int gLoop = 0 ; gLoop < ListArray.size(); gLoop++){
                    System.out.println("|INFO| REMO Test gLoop num: " + gLoop + " " + ListArray.size());
                    messageobject = ListArray.get(gLoop);
                            System.out.println("|INFO| REMO Test Msg name " + messageobject.Message);
                            System.out.println("|INFO| REMO Test gLoop num_1: " + gLoop);
                    if(messageobject.Message.contains("HasSNSID")){
                    //System.out.println("|INFO| REMO Test gLoop num_2: " + gLoop);
                    String temp[];
                    temp = messageobject.Message.split("HasSNSID");
                    //realName = temp[0];
                    friendSNSId = temp[1];
                    System.out.println("|INFO| REMO Test friendSNS ID: " + temp[1]);

                    // Database check problem
                    System.out.println("|INFO| REMO Test gLoop num_3: " + gLoop);
                    //if(chatclient.dbConnector.isFacebookFriend(friendSNSId, mySNSId)){
                    if(dbCon.isFacebookFriend(friendSNSId, mySNSId)){
                        System.out.println("|INFO| REMO Test_isSNS_1...");
                            System.out.println("|INFO| REMO Test gLoop num_4: " + gLoop);
                        ListArray.get(gLoop).isSNSFriend = true;

                        System.out.println("|INFO| REMO Test_isSNS_2...");
                    }
                        System.out.println("|INFO| REMO Test gLoop num_5: " + gLoop);
                    }
                    System.out.println("|INFO| REMO Test 43 " + gLoop);
                //ListItem = ListItem + "(Friend)";
            }
        }
        System.out.println("|INFO| REMO Test 44");
        TotalWidth = Math.max(TotalWidth,messageobject.Width);
        scrollview.setValues(TotalWidth, startY+DEFAULT_LIST_CANVAS_HEIGHT);
        scrollview.setScrollPos(1,1);
        scrollview.setScrollSteps(2,1,DEFAULT_SCROLLING_HEIGHT);
        repaint();
    }

    //Clear ListArray
    public void ClearAll(){
        ListArray.clear();
        TotalWidth = 0;
        TotalHeight = 0;
        scrollview.setValues(TotalWidth,TotalHeight);
    }

    //Index
    private int GetIndexOf(String Message){
        int m_listSize = ListArray.size();
        for(int gLoop = 0 ; gLoop < m_listSize; gLoop++){
            messageobject = (MessageObject) ListArray.get(gLoop);
            if(messageobject.Message.equals(Message))
            return gLoop;
        }

        return -1;
    }

    protected void IgnoreUser(boolean IsIgnore, String IgnoreUserName){
        int m_listIndex = GetIndexOf(IgnoreUserName);
        if (m_listIndex >= 0){
            messageobject = (MessageObject) ListArray.get(m_listIndex);
            messageobject.isIgnored = IsIgnore;
            ListArray.set(m_listIndex,messageobject);

        if(IsIgnore){
            chatclient.tappanel.CmdIgnoreUser.setLabel("Allow User");
            chatclient.messagecanvas.AddMessageToMessageObject(IgnoreUserName + " has been blocked!",MESSAGE_TYPE_LEAVE);
        }
        else{
            chatclient.tappanel.CmdIgnoreUser.setLabel("Block User");
            chatclient.messagecanvas.AddMessageToMessageObject(IgnoreUserName + " has been removed from black list!",MESSAGE_TYPE_JOIN);
        }}
    }

    //Ignore list removal
    protected void IgnoreUser(boolean IsIgnore) {
        if (SelectedUser.equals("")){
            chatclient.messagecanvas.AddMessageToMessageObject("Invalid User Selection!",MESSAGE_TYPE_ADMIN);
            return;
        }
        if (SelectedUser.equals(chatclient.UserName)){
            chatclient.messagecanvas.AddMessageToMessageObject("You can not ignored yourself!",MESSAGE_TYPE_ADMIN);
            return;
        }

        IgnoreUser(IsIgnore,SelectedUser);
    }

    protected void SendDirectMessage(){
        if (SelectedUser.equals("")){
                chatclient.messagecanvas.AddMessageToMessageObject("Invalid User Selection!",MESSAGE_TYPE_ADMIN);
                return;
        }
        if (SelectedUser.equals(chatclient.UserName)){
                chatclient.messagecanvas.AddMessageToMessageObject("You can not chat with yourself!",MESSAGE_TYPE_ADMIN);
                return;
        }

        CreatePrivateWindow();
    }

    /********** Check Whether the User ignored or not *********/
    public boolean IsIgnoredUser(String UserName){
        int m_listIndex = GetIndexOf(UserName);
        if (m_listIndex >= 0){
                messageobject = (MessageObject) ListArray.get(m_listIndex);
                return messageobject.isIgnored;
        }

        //Fault
        return false;

    }

    //Remove given item from the list
    public void RemoveListItem(String ListItem){
        System.out.println("|INFO| REMO TEST3");
        int ListIndex = GetIndexOf(ListItem);
        if( ListIndex >= 0){
            messageobject = (MessageObject) ListArray.get(ListIndex);
            int m_StartY = messageobject.StartY;
            ListArray.remove(ListIndex);
            int m_listSize = ListArray.size();
            int m_nextStartY;
            for(int gLoop = ListIndex; gLoop < m_listSize; gLoop++){
                messageobject = (MessageObject) ListArray.get(gLoop);
                m_nextStartY = messageobject.StartY;
                messageobject.StartY = m_StartY;
                m_StartY = m_nextStartY;
            }
        }
        repaint();
    }

    private void PaintFrame(Graphics graphics){
        int m_listArraySize = ListArray.size();
        for(int gLoop = 0; gLoop < m_listArraySize; gLoop++){
            messageobject = (MessageObject) ListArray.get(gLoop);
            if((messageobject.StartY + messageobject.Height) >= yOffset)
            {
                PaintListItemIntoCanvas(graphics,messageobject);
            }
        }
    }

    // Draw online user list
    private void PaintListItemIntoCanvas(Graphics graphics, MessageObject messageObject){
        int m_StartY = messageObject.StartY - yOffset;
        int m_imageIndex = GROUP_CANVAS_ICON;
        String name;
        name = messageObject.Message;

        if(messageObject.isSNSFriend){
            String tempName[];
            tempName = messageObject.Message.split("HasSNSID");
            name = tempName[0] + "(SNS_Friend)";

        }
        else if(messageObject.Message.contains("HasSNSID")&&!messageObject.isSNSFriend){
                String temp[] = messageObject.Message.split("HasSNSID");
                name = temp[0];
        }
        else{
            name = messageObject.Message;
        }

        switch (CanvasType){
                case USER_CANVAS:
                        {
                                if(messageobject.isIgnored==true)
                                        m_imageIndex = USER_CANVAS_IGNORE_ICON;
                                else
                                        m_imageIndex = USER_CANVAS_NORMAL_ICON;
                                break;
                        }
        }

        if(messageobject.Selected == true){
                graphics.setColor(Color.blue);
                graphics.fillRect(5-xOffset+DEFAULT_LIST_CANVAS_HEIGHT,m_StartY,TotalWidth,DEFAULT_LIST_CANVAS_INCREMENT);
                graphics.setColor(Color.white);
                graphics.drawString(name,5-xOffset+DEFAULT_LIST_CANVAS_INCREMENT,m_StartY+DEFAULT_LIST_CANVAS_HEIGHT);
        }
        else{
                graphics.setColor(Color.white);
                graphics.fillRect(5-xOffset+DEFAULT_LIST_CANVAS_HEIGHT,m_StartY,TotalWidth,DEFAULT_LIST_CANVAS_INCREMENT);
                graphics.setColor(Color.black);
                graphics.drawString(name,5-xOffset+DEFAULT_LIST_CANVAS_INCREMENT,m_StartY+DEFAULT_LIST_CANVAS_HEIGHT);
        }
    }

    public boolean handleEvent(Event event){
        if(event.id == 1001 && event.arg == scrollview){
            if(event.modifiers == 1)
                xOffset = event.key;
            else
                yOffset = event.key;
            repaint();
            return true;
        }
        else{
            return super.handleEvent(event);
        }
    }

    public boolean mouseDown(Event event, int i, int j){
        int CurrentY = j + yOffset;
        int m_listArraySize = ListArray.size();
        boolean SelectedFlag=false;
        chatclient.tappanel.TxtUserCount.setText("");
        chatclient.tappanel.CmdIgnoreUser.setLabel("Block User");
        for(int gLoop = 0; gLoop <  m_listArraySize; gLoop++){
            messageobject = (MessageObject) ListArray.get(gLoop);
            if((CurrentY >= messageobject.StartY) && (CurrentY <= (messageobject.StartY+DEFAULT_LIST_CANVAS_HEIGHT))){
                messageobject.Selected=true;
                SelectedUser = messageobject.Message;
                SelectedFlag = true;

                if(CanvasType == GROUP_CANVAS)
                        chatclient.GetGroupUserNumber(SelectedUser);

                if(CanvasType == USER_CANVAS){
                        if (IsIgnoredUser(SelectedUser))
                                chatclient.tappanel.CmdIgnoreUser.setLabel("Allow User");
                        else
                                chatclient.tappanel.CmdIgnoreUser.setLabel("Block User");
                }
            }
            else{
                    messageobject.Selected=false;
            }
        }
        repaint();
        if ((!SelectedFlag))
                SelectedUser="";

        if((event.clickCount == 2) && (CanvasType == USER_CANVAS) && (!(SelectedUser.equals(""))) && (!(SelectedUser.equals(chatclient.UserName)))){
                CreatePrivateWindow();
        }

        return true;
    }

    private void CreatePrivateWindow(){
        //Is ignored user?
        if(!(IsIgnoredUser(SelectedUser)))
        {
            boolean PrivateFlag = false;
            for(int gLoop = 0; gLoop < chatclient.PrivateWindowCount;gLoop++){
                    if(chatclient.privatewindow[gLoop].UserName.equals(SelectedUser))
                    {
                        chatclient.privatewindow[gLoop].show();
                        chatclient.privatewindow[gLoop].requestFocus();
                        PrivateFlag = true;
                        break;
                    }
            }

            if(!(PrivateFlag)){
                    if(chatclient.PrivateWindowCount >= MAX_PRIVATE_WINDOW){
                            chatclient.messagecanvas.AddMessageToMessageObject("You are Exceeding private window limit! So you may lose some message from your friends!",MESSAGE_TYPE_ADMIN);
                    }
                    else{
                            chatclient.privatewindow[chatclient.PrivateWindowCount++] = new PrivateChat(chatclient,SelectedUser);
                            chatclient.privatewindow[chatclient.PrivateWindowCount-1].show();
                            chatclient.privatewindow[chatclient.PrivateWindowCount-1].requestFocus();
                    }
            }
        }
    }

    public void paint(Graphics graphics){
        //Duble buffering
        dimension = size();

        //Offscreen graphics context create
        if ((offGraphics == null) || (dimension.width != offDimension.width)|| (dimension.height != offDimension.height)){
            offDimension = dimension;
            offImage = createImage(dimension.width, dimension.height);
            offGraphics = offImage.getGraphics();
        }

        //Previous image erase
        offGraphics.setColor(Color.white);
        offGraphics.fillRect(0, 0, dimension.width, dimension.height);

        //Paint frame into image
        PaintFrame(offGraphics);

        //Paint image into screen
        graphics.drawImage(offImage, 0, 0, null);
    }

    public void update(Graphics graphics){
        paint(graphics);
    }

}
