package chatAppClient.view;

import chatAppClient.ChatAppClient;
import java.awt.Button;

public class CustomButton extends Button{
    ChatAppClient chatclient;
    public CustomButton(ChatAppClient parent, String label) {
        chatclient = parent;
        setLabel(label);
        setBackground(chatclient.ColorMap[3]);
        setForeground(chatclient.ColorMap[2]);
    }
}
