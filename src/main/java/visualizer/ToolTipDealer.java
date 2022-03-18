package visualizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class ToolTipDealer extends JToolTip {

    public ToolTipDealer() {
        setBackground(new Color(0, 0, 0, 0));
        setBorder(null);
        setOpaque(false);
        setFont(new Font("Tempus Sans ITC", Font.PLAIN, 17));
        setPreferredSize(new Dimension(290, 25));
        setMinimumSize(getPreferredSize());
        setSize(getPreferredSize());
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
    }

    public static Point getFixedToolTipLocation(MouseEvent e, JComponent hovered) {
        var frameLocation = hovered.getTopLevelAncestor().getLocationOnScreen();
        var frameWidth = hovered.getTopLevelAncestor().getWidth();
        var source = e.getComponent().getLocationOnScreen();
//        System.err.println(e.getComponent());
//        System.err.println(new Point(frameWidth - (source.x - frameLocation.x) - 703, -(source.y - (frameLocation.y + 155))));
        // right align toolbar
//        return new Point(frameWidth - (source.x - frameLocation.x) - 300, -(source.y - (frameLocation.y + 55)));
        // center align under buttonPanel
        return new Point(frameWidth - (source.x - frameLocation.x) - 703, -(source.y - (frameLocation.y + 155)));
    }
}