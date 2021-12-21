import javax.swing.*;
import java.awt.*;

public class ModeLabel extends JLabel {

    public ModeLabel() {
        super("Current Mode -> Add a Vertex", SwingConstants.RIGHT);
        setName("Mode");
        setBounds(580, 0, 200, 30);
        setForeground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);
        g.drawString(getText(), getX(), getY());
    }
}