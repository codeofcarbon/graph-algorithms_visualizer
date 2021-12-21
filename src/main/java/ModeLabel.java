import javax.swing.*;
import java.awt.*;

public class ModeLabel extends JLabel {

    public ModeLabel() {
        super("Current Mode -> Add a Vertex", SwingConstants.RIGHT);
        setName("Mode");
        setBounds(580, 0, 200, 30);
        setForeground(Color.WHITE);
        setVisible(true);
    }
}
