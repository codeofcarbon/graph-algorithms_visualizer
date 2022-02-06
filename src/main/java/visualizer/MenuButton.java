package visualizer;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.*;

public class MenuButton extends JButton {
    private static JPopupMenu pop;

    public MenuButton(String iconFilename, String toolTipText, JComponent container, JComponent target) {
        setUI(new BasicButtonUI());
        setToolTipText(String.format("<html><font color=gray>%s", toolTipText));
        var size = target == null ? 30 : 70;
        setIcon(loadIcon(iconFilename, size, false));
        setRolloverIcon(loadIcon(iconFilename, size, true));
        setPreferredSize(new Dimension(size, size));
        setSize(getPreferredSize());
        container.add(this);
        setOpaque(false);
        setVisible(true);
        if (target != null) {
            addMouseListener(new MouseAdapter() {                               // todo change to action listener?
                @Override
                public void mousePressed(MouseEvent e) {
                    super.mousePressed(e);
                    if (target instanceof JComboBox) {
                        pop = target.getComponentPopupMenu();
                        var invoker = e.getComponent().getParent();
                        pop.setVisible(!pop.isVisible());
                        pop.show(invoker, 5, invoker.getHeight());
                    } else target.setVisible(!target.isVisible());
                }
            });
        }
    }

    private static ImageIcon loadIcon(String iconFilename, int size, boolean rolloverIcon) {
        return new ImageIcon(new ImageIcon(
                String.format("src/main/resources/icons/buttons/%s.png", iconFilename + (rolloverIcon ? " blue" : "")))
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