package chatAppClient.view;

import java.awt.Panel;
import java.awt.Dimension;

public class CustomPanel extends Panel{

    public Dimension dimension;

    public CustomPanel(int i, int j){
        dimension = new Dimension(i, j);
        resize(dimension);
        validate();
    }

    public Dimension minimumSize(){
        return dimension;
    }

    public Dimension preferredSize(){
        return size();
    }
}
