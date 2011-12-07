package chatAppClient.view;

public class MessageObject {
    String Message;
    int StartX;
    int StartY;
    int Width;
    int Height;
    boolean isImage;
    boolean Selected;
    boolean isIgnored;
    boolean isSNSFriend;
    int MessageType;

    MessageObject() {
        Width   = 0;
        Height  = 0;
        StartX  = 0;
        StartY  = 0;
        Message = null;
        isImage = false;
        Selected = false;
        isIgnored = false;
        isSNSFriend = false;
    }



}
