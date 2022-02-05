//package visualizer;
//
//import javax.swing.*;
//import javax.swing.plaf.basic.BasicButtonUI;
//import java.awt.*;
//import java.awt.event.*;
//
//public class ModeButton extends JButton {
//
//    public ModeButton(String iconFilename, String toolTipText, JComboBox<String> comboBox) {
//        setUI(new BasicButtonUI());
//        setPreferredSize(new Dimension(70, 70));
//        setSize(getPreferredSize());
//        setOpaque(false);
//        setToolTipText(String.format("<html><font color=gray>%s", toolTipText));
//        setIcon(new ImageIcon(new ImageIcon(
//                String.format("src/main/resources/icons/buttons/%s.png", iconFilename))
//                .getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH)));
//        setRolloverEnabled(true);
//        setRolloverIcon(new ImageIcon(new ImageIcon(
//                String.format("src/main/resources/icons/buttons/%s blue.png", iconFilename))
//                .getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH)));
//        addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                comboBox.setVisible(!comboBox.isVisible());
//            }
//        });
//    }
//
//    @Override
//    public JToolTip createToolTip() {
//        var tip = new JToolTip();
//        tip.setBackground(new Color(0, 0, 0, 0));
//        tip.setBorder(null);
//        tip.setOpaque(false);
//        return tip;
//    }
//
//    @Override
//    public Point getToolTipLocation(MouseEvent e) {
//        var point = e.getPoint();
//        point.translate(-10, 20);
//        return point;
//    }
//}