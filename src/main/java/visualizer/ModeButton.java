package visualizer;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.*;

public class ModeButton extends JButton {

    public ModeButton(JComboBox<String> comboBox, String iconFilename, String toolTipText) {
        setUI(new BasicButtonUI());
        setPreferredSize(new Dimension(50, 50));
        setSize(getPreferredSize());
        setBackground(Color.BLACK);
        setForeground(Color.BLACK);
        setToolTipText(String.format("<html><font color=gray>%s", toolTipText));
        setIcon(new ImageIcon(new ImageIcon(
                String.format("src/main/resources/icons/buttons/%s.png", iconFilename))
                .getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
        setRolloverEnabled(true);
        setRolloverIcon(new ImageIcon(new ImageIcon(
                String.format("src/main/resources/icons/buttons/%s blue.png", iconFilename))
                .getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
//                comboBox.setLocation();                                                       // todo in progress
                comboBox.setVisible(!comboBox.isVisible());
                if (comboBox.isVisible()) {
                    comboBox.requestFocusInWindow();
                    comboBox.showPopup();
//                    comboBox.setPopupVisible(true);                           // todo debugging
                }
            }

//            @Override
//            public void mouseExited(MouseEvent e) {
//                if (comboBox.isVisible()) {
//                    Timer timer = new Timer(5000, event -> comboBox.setVisible(false));
//                    timer.start();
//                    timer.setRepeats(false);
//                }
//            }
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
}