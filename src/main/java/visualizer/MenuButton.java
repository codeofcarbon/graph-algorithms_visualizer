package visualizer;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.*;

public class MenuButton extends JButton {
    private static JPopupMenu popup;
    private final JComponent target;
//    @Getter @Setter private
    float alpha = 0.5f;

    public MenuButton(String iconFilename, String toolTipText, JComponent container,
                      JComponent target, MouseHandler handler) {
        this.target = target;
        setUI(new BasicButtonUI());
        setToolTipText(String.format("<html><font color=gray>%s", toolTipText));
        var size = target == null ? 30 : 70;
//        alpha = target == null ? 0.4f : 0.5f;
        setIcon(loadIcon(iconFilename, size, false));
        setRolloverIcon(loadIcon(iconFilename, size, true));
        setPreferredSize(new Dimension(size, size));
        container.add(this);
        setOpaque(false);
        setVisible(true);
        addMouseListener(handler);
        if (target != null)
            addActionListener(event -> {
            popup = target.getComponentPopupMenu();
            popup.setVisible(!popup.isVisible());
            if (!(target instanceof JComboBox))
                popup.show(this, this.getWidth() / 2 - popup.getWidth() / 2, this.getHeight() - 5);
            else {
                if (((ModeComboBox<?>) this.target).clazz.equals(GraphMode.class)) {
                    popup.show(this, -popup.getWidth(), this.getY());
                } else {
                    popup.show(this, this.getWidth(), this.getY());
                }
            }
        });
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

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2D = (Graphics2D) g;
        g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        super.paintComponent(g2D);
    }
}