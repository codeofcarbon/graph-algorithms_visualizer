package visualizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Hashtable;

public class TipManager extends MouseAdapter {
    private static final TipManager MANAGER = new TipManager();
    private static final Hashtable<JComponent, String> toolTips = new Hashtable<>();
    private final JLabel label = new JLabel("", SwingConstants.CENTER);
    private final JWindow window = new JWindow();

    private TipManager() {
        label.setFont(new Font("Tempus Sans ITC", Font.PLAIN, 17));
        window.setSize(new Dimension(290, 18));
        window.add(label);
    }

    public static void setToolTipText(JComponent component, String tip) {
        toolTips.put(component, tip);
        component.addMouseListener(MANAGER);
    }

    public void showToolTip(MouseEvent event) {
        var current = (JComponent) event.getSource();
        label.setText(toolTips.get(current));
        var pane = SwingUtilities.getRootPane(current);
        var paneLocation = pane.getLocationOnScreen();
        window.setLocation(new Point(paneLocation.x + pane.getWidth() / 2 - 146, paneLocation.y + 1));
        window.setVisible(true);
    }

    @Override
    public void mouseEntered(MouseEvent event) {
        showToolTip(event);
    }

    @Override
    public void mouseMoved(MouseEvent event) {
        System.err.println(event.getSource());
        if (event.getSource() instanceof Graph)
            showToolTip(event);
    }

    @Override
    public void mouseExited(MouseEvent event) {
        window.setVisible(false);
    }
}