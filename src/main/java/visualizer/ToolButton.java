package visualizer;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.*;

public class ToolButton extends JButton {

    public ToolButton(String iconFilename, String toolTipText, JComponent container) {
        setUI(new BasicButtonUI());
        setToolTipText(String.format("<html><div align=right><font color=rgb(128,128,128)>%s</font>", toolTipText));
        int size = 30;
        setPreferredSize(new Dimension(size, size));
        var icon = loadIcon(iconFilename, size, false);
        var rolloverIcon = loadIcon(iconFilename, size + 4, true);
        setIcon(icon);
        setRolloverEnabled(true);
        setSelectedIcon(rolloverIcon);
        new RolloverAnimator(this, icon, rolloverIcon);        
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
        return new ToolTipDealer();
    }

    @Override
    public Point getToolTipLocation(MouseEvent e) {
        return ToolTipDealer.getFixedToolTipLocation(e);
    }
}