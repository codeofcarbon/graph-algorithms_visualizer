package visualizer;

import javax.swing.*;
import javax.swing.plaf.basic.BasicToggleButtonUI;
import java.awt.*;
import java.awt.event.*;

public class MenuButton extends JToggleButton {
    private final JPopupMenu popup;

    public MenuButton(String iconFilename, String toolTipText, ButtonPanel panel, JComponent target) {
        setUI(new BasicToggleButtonUI());
        setToolTipText(String.format("<html><font color=gray>%s", toolTipText));
        int size = 70;
        setPreferredSize(new Dimension(size, size));
        var icon = loadIcon(iconFilename, size, false);
        var rolloverIcon = loadIcon(iconFilename, size, true);
        setIcon(icon);
        setBorder(BorderFactory.createEmptyBorder());
        setSelectedIcon(rolloverIcon);
        panel.add(this);
        setOpaque(false);
        setVisible(true);
        popup = target.getComponentPopupMenu();
        popup.setInvoker(this);

        addActionListener(e -> {
            if (isSelected()) {
                popup.setVisible(true);
                if (target instanceof JComboBox) {
                    if (((ModeComboBox<?>) target).clazz.equals(AlgMode.class))
                        popup.show(MenuButton.this, getWidth(), getY());
                    else popup.show(MenuButton.this, -popup.getWidth(), getY());
                } else popup.show(MenuButton.this, getWidth() / 2 - popup.getWidth() / 2, getHeight() - 5);
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (!isSelected() && isFocusOwner()) {
                    panel.getButtonGroup().clearSelection();
                    doClick();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                requestFocusInWindow();
            }
        });
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