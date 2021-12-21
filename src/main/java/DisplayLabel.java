import javax.swing.*;
import java.awt.*;

public class DisplayLabel extends JLabel {

    public DisplayLabel() {
        super("", SwingConstants.CENTER);
        setName("Display");
        setBounds(0, 550, 800, 30);
        setForeground(Color.BLACK);
    }
}

