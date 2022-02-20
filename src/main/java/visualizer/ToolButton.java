package visualizer;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.*;

public class ToolButton extends JButton {

    public ToolButton(String iconFilename, String toolTipText, JComponent container) {
        setUI(new BasicButtonUI());
        setToolTipText(String.format("<html><font color=gray>%s", toolTipText));
        int size = 30;
        setPreferredSize(new Dimension(size, size));
        var icon = loadIcon(iconFilename, size, false);
        var rolloverIcon = loadIcon(iconFilename, size, true);
        setIcon(icon);
        setRolloverEnabled(true);
        new RolloverAnimator(this, icon, rolloverIcon);             // todo refactor
        container.add(this);
        setOpaque(false);
        setVisible(true);
    }

    private static ImageIcon loadIcon(String iconFilename, int size, boolean rolloverIcon) {
        return new ImageIcon(new ImageIcon(
                String.format("src/main/resources/icons/buttons/%s.png",
                        iconFilename + (rolloverIcon ? " blue" : "")))
                .getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH));
    }

    @Override
    public JToolTip createToolTip() {
        var tip = new JToolTip();
        tip.setBackground(new Color(0, 0, 0, 0));
        tip.setBorder(null);
        tip.setOpaque(false);
        return tip;
    }

    @Override
    public Point getToolTipLocation(MouseEvent e) {
        var point = e.getPoint();
        point.translate(-50, 20);
        return point;
    }
}