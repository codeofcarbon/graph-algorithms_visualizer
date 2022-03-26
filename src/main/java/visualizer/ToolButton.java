package visualizer;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;

public class ToolButton extends JButton {

    public ToolButton(String iconFilename, String toolTipText, JComponent container) {
        setUI(new BasicButtonUI());
        ToolTipDealer.setToolTipText(this, String.format("<html><font color=rgb(128,128,128)>%s", toolTipText));
        int size = 30;
        setPreferredSize(new Dimension(size, size));
        var icon = IconMaker.loadIcon(iconFilename, "buttons", size, size);
        var rolloverIcon = IconMaker.loadIcon(iconFilename + " blue", "buttons", size + 4, size + 4);
        setIcon(icon);
        setRolloverEnabled(true);
        setSelectedIcon(rolloverIcon);
        new RolloverAnimator(this, icon, rolloverIcon);        
        container.add(this);
        setOpaque(false);
        setVisible(true);
    }
}