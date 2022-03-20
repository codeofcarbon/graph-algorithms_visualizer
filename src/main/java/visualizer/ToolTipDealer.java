package visualizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ToolTipDealer extends JToolTip {

    public ToolTipDealer() {
        setFont(new Font("Tempus Sans ITC", Font.PLAIN, 17));
        setPreferredSize(new Dimension(290, 25));
        setMinimumSize(getPreferredSize());
        setSize(getPreferredSize());
        setOpaque(false);
        setBorder(null);
        setBorder(BorderFactory.createLineBorder(new Color(120, 120, 120, 50), 1));
    }

    public static Point getFixedToolTipLocation(MouseEvent e) {
        var pane = SwingUtilities.getRootPane(e.getComponent());
        var paneLocation = pane.getLocationOnScreen();
        var sourceLocation = e.getComponent().getLocationOnScreen();
        return new Point(
                pane.getWidth() - (sourceLocation.x - paneLocation.x) - 290,
                -(sourceLocation.y - (paneLocation.y + 25)));
    }
}