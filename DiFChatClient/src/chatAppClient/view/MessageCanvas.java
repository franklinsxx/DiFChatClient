package chatAppClient.view;

import chatAppClient.ChatAppClient;
import java.awt.Dimension;
import java.awt.Canvas;
import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.Event;
import java.awt.Image;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.util.StringTokenizer;

public class MessageCanvas extends Canvas implements CommonSettings {

    Dimension offDimension,dimension;
    Image offImage;
    Graphics offGraphics;
    ChatAppClient chatclient;
    ArrayList MessageArray;
    int gLoop,xOffset,yOffset,HorizantalSpace;
    MessageObject messageobject;
    public ScrollView scrollview;
    FontMetrics fontmetrics;
    int TotalWidth,MessageCount,TotalHeight;
    Font UserNameFont,TextFont;
    StringTokenizer tokenizer;
    String TokenString;

    public MessageCanvas(ChatAppClient Parent) {
        chatclient = Parent;
        dimension = chatclient.getSize();
        MessageArray = new ArrayList();
        MessageCount  = 0;
        TotalWidth = 0;
        HorizantalSpace = 2;
        UserNameFont = chatclient.getFont();
        TextFont = chatclient.TextFont;
        setFont(chatclient.getFont());
        fontmetrics = chatclient.getFontMetrics(chatclient.getFont());
    }

    //ListAarray items clear
    public void ClearAll(){
        MessageArray.clear();
        TotalHeight = 0;
        TotalWidth = 0;
        MessageCount = 0;
        scrollview.setValues(TotalWidth, TotalHeight);
    }

    public void AddMessageToMessageObject(String Message, int MessageType){
        System.out.println("|INFO| Message: "+Message);
        System.out.println("|INFO| Type "+MessageType);

        String m_Message="";
        if(Message != null){
            tokenizer = new StringTokenizer(Message," ");
        }
        else{
            tokenizer = new StringTokenizer(" ");
        }

        System.out.println("|INFO| Data...");
        while(tokenizer.hasMoreTokens()) {
            TokenString = tokenizer.nextToken();
            if(fontmetrics.stringWidth(m_Message+TokenString) < dimension.width) {
                    m_Message = m_Message + TokenString+" ";
            }
            else {
                    AddMessage(m_Message,MessageType);
                    m_Message="";
            }
        }

        AddMessage(m_Message,MessageType);
    }


    private void AddMessage(String Message,int MessageType){
        int startY = DEFAULT_MESSAGE_CANVAS_POSITION;
        if(MessageArray.size() > 0) {
                messageobject = (MessageObject) MessageArray.get(MessageArray.size()-1);
                startY = messageobject.StartY + messageobject.Height;
        }

        messageobject = new MessageObject();
        messageobject.Message = Message;
        messageobject.StartY = startY;
        messageobject.MessageType = MessageType;

        //Has image
        if(Message.indexOf("~~") >= 0) {
                messageobject.isImage = true;
                // additional feature to set withdth n height
        }
        else {
                messageobject.isImage = false;
                messageobject.Width = fontmetrics.stringWidth(Message);
                messageobject.Height =  fontmetrics.getHeight()+fontmetrics.getDescent();
        }
        MessageArray.add(messageobject);
        MessageCount++;
        TotalWidth = Math.max(TotalWidth,messageobject.Width);
        TotalHeight = startY+messageobject.Height;
        scrollview.setValues(TotalWidth,TotalHeight);

        int mHeight = TotalHeight - yOffset;
        if(mHeight > dimension.height) {
                yOffset = TotalHeight - dimension.height;
        }
        scrollview.setScrollPos(2,2);
        scrollview.setScrollSteps(2, 1, DEFAULT_SCROLLING_HEIGHT);
        repaint();
	}

    private void PaintFrame(Graphics graphics){
        if(MessageCount < 1) return;
        int yPos = yOffset + dimension.height;
        int startPos = 0;
        int m_listArraySize = MessageArray.size();
        for(gLoop = 0; gLoop < MessageCount && startPos < yPos; gLoop++){
                if(m_listArraySize < gLoop) return;
                messageobject = (MessageObject) MessageArray.get(gLoop);
                if(messageobject.StartY >= yOffset) {
                        PaintMessageIntoCanvas(graphics,messageobject);
                        startPos = messageobject.StartY;
                }
        }

        if(gLoop < MessageCount) {
                messageobject = (MessageObject) MessageArray.get(gLoop);
                PaintMessageIntoCanvas(graphics,messageobject);
        }
    }

    private void PaintMessageIntoCanvas(Graphics graphics, MessageObject messageObject){
        graphics.setColor(Color.black);
        int yPos = messageobject.StartY - yOffset;
        int xPos = 5 - xOffset;
        int CustomWidth = 0;
        String Message = messageobject.Message;
        //Print user name
        if(Message.indexOf(":") >= 0) {
                graphics.setFont(UserNameFont);
                chatclient.getGraphics().setFont(UserNameFont);
                fontmetrics = chatclient.getGraphics().getFontMetrics();
                String m_UserName = Message.substring(0,Message.indexOf(":")+1);
                graphics.drawString(m_UserName,xPos+CustomWidth,yPos);
                CustomWidth+=fontmetrics.stringWidth(m_UserName)+HorizantalSpace;
                Message = Message.substring(Message.indexOf(":")+1);
        }
        //Set text font
        chatclient.getGraphics().setFont(TextFont);
        graphics.setFont(TextFont);
        fontmetrics =  chatclient.getGraphics().getFontMetrics();

        //Print image area
        if(messageobject.isImage == true) {
            // additional feature
        }
        //Not image
        else{
                switch (messageobject.MessageType){
                        case MESSAGE_TYPE_DEFAULT:
                                {
                                        graphics.setColor(Color.black);
                                        break;
                                }
                        case MESSAGE_TYPE_JOIN:
                                {
                                        graphics.setColor(Color.blue);
                                        break;
                                }
                        case MESSAGE_TYPE_LEAVE:
                                {
                                        graphics.setColor(Color.red);
                                        break;
                                }
                        case MESSAGE_TYPE_ADMIN:
                                {
                                        graphics.setColor(Color.gray);
                                        break;
                                }
                }
                graphics.drawString(Message, xPos+CustomWidth, yPos);
        }

        graphics.setFont(UserNameFont);
        chatclient.getGraphics().setFont(UserNameFont);
        fontmetrics = chatclient.getGraphics().getFontMetrics();
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

    public boolean mouseDown(Event event, int i, int j) {
        return true;
    }

    public void paint(Graphics graphics) {
        //double buffers
        dimension = size();

        //create the offscreen graphics context
        if ((offGraphics == null) || (dimension.width != offDimension.width)|| (dimension.height != offDimension.height))
        {
        offDimension = dimension;
        offImage = createImage(dimension.width, dimension.height);
        offGraphics = offImage.getGraphics();
        }

        //Delete previous image
        offGraphics.setColor(Color.white);
        offGraphics.fillRect(0, 0, dimension.width, dimension.height);

        //Frame paint into image
        PaintFrame(offGraphics);

        //Image paint to screen
        graphics.drawImage(offImage, 0, 0, null);
    }

    public void update(Graphics graphics){
        paint(graphics);
    }

}
