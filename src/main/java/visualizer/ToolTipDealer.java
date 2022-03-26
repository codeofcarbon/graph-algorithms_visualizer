package visualizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Hashtable;

public class ToolTipDealer extends MouseAdapter implements Runnable {
    private static final ToolTipDealer INSTANCE = new ToolTipDealer();
    private final Hashtable<JComponent, String> toolTips = new Hashtable<>();
    private final Thread showThread = new Thread(this);
    private final JLabel label = new JLabel("", SwingConstants.CENTER);
    private JComponent current;
    private JWindow window;

    private ToolTipDealer() {
        label.setFont(new Font("Tempus Sans ITC", Font.PLAIN, 17));
        showThread.start();
    }

    public static void setToolTipText(JComponent component, String tip) {
        INSTANCE.toolTips.put(component, tip);
        component.addMouseListener(INSTANCE);
    }

    private void createWindow() {
        window = new JWindow();
        window.setSize(new Dimension(290, 18));
        window.add(label);
    }

    @Override
    public synchronized void run() {
        while (true) {
            try {
                synchronized (this) {
                    wait();
                }
                label.setText(toolTips.get(current));
                if (window == null) createWindow();
                var pane = SwingUtilities.getRootPane(current);
                var paneLocation = pane.getLocationOnScreen();
                window.setLocation(new Point(paneLocation.x + pane.getWidth() / 2 - 146, paneLocation.y + 1));
                window.setVisible(true);
            } catch (InterruptedException ignored) {
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent event) {
        current = (JComponent) event.getSource();
        synchronized (this) {
            notify();
        }
    }

    @Override
    public void mouseExited(MouseEvent event) {
        if (event.getSource() == current) {
            showThread.interrupt();
            if (window != null) window.setVisible(false);
        }
    }
}