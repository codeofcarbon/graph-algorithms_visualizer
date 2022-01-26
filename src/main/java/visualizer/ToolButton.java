package visualizer;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.*;

public class ToolButton extends JButton {

    public ToolButton(String iconFilename, String toolTipText) {
        setUI(new BasicButtonUI());
        setPreferredSize(new Dimension(50, 50));
        setSize(getPreferredSize());
        setBackground(Color.BLACK);
        setForeground(Color.BLACK);
        setToolTipText(String.format("<html><font color=gray>%s", toolTipText));
        setIcon(new ImageIcon(new ImageIcon(
                String.format("src/main/resources/icons/buttons/%s.png", iconFilename))
                .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
        var hoverLabel = addHoverImage();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hoverLabel.setVisible(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hoverLabel.setVisible(false);
            }
        });
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
        point.translate(-10, 20);
        return point;
    }

    public JLabel addHoverImage() {
        var hoverLabel = new JLabel(new ImageIcon(new ImageIcon("src/main/resources/icons/buttons/layered.png")
//        var hoverLabel = new JLabel(new ImageIcon(new ImageIcon("src/main/resources/icons/buttons/dark glass.png")
                .getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
        hoverLabel.setPreferredSize(new Dimension(50, 50));
        hoverLabel.setSize(hoverLabel.getPreferredSize());
        hoverLabel.setBackground(new Color(0, 0, 0, 0));
//        hoverLabel.setForeground(new Color(0, 0, 0, 0));                       // todo debugging
        hoverLabel.setLocation(this.getX() + 5, this.getY() + 5);
//        hoverLabel.setOpaque(true);                                            // todo debugging
        hoverLabel.setVisible(false);
        this.add(hoverLabel);
        return hoverLabel;
//        setRolloverEnabled(true);                                              // todo debugging
//        setRolloverIcon(new ImageIcon(new ImageIcon(
//                String.format("src/main/resources/icons/buttons/%s.png", iconFilename))
//                .getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
    }
}