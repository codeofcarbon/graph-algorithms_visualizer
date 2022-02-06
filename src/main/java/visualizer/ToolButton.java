//package visualizer;
//
//import lombok.Getter;
//
//import javax.swing.*;
//import javax.swing.plaf.basic.BasicButtonUI;
//import java.awt.*;
//import java.awt.event.*;
//import java.util.ArrayList;
//import java.util.List;
//
//@Getter
//public class ToolButton extends JButton {
//    private static final JLabel pressedLabel = new JLabel(
//            new ImageIcon(new ImageIcon("src/main/resources/icons/buttons/layered.png")
//                    .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
//    static final List<ToolButton> buttons = new ArrayList<>();
//
//    public ToolButton(String iconFilename, String toolTipText, JPanel buttonsPanel) {
//        setUI(new BasicButtonUI());
//        setPreferredSize(new Dimension(50, 50));
//        setSize(getPreferredSize());
//        setOpaque(false);
//        setToolTipText(String.format("<html><font color=gray>%s", toolTipText));
//        setIcon(new ImageIcon(new ImageIcon(
//                String.format("src/main/resources/icons/buttons/%s.png", iconFilename))
//                .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
//        setRolloverEnabled(true);
//        setRolloverIcon(new ImageIcon(new ImageIcon(
//                String.format("src/main/resources/icons/buttons/%s blue.png", iconFilename))
//                .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
//        buttonsPanel.add(this);
//        addMouseListener(new MouseAdapter() {
//            @Override
//            public void mousePressed(MouseEvent e) {
//                super.mousePressed(e);
//                pressedLabel.setVisible(true);
//            }
//
//            @Override
//            public void mouseReleased(MouseEvent e) {
//                super.mouseReleased(e);
//                pressedLabel.setVisible(false);
//            }
//        });
//
////        setPressedIcon(new ImageIcon(new ImageIcon("src/main/resources/icons/buttons/layered.png")
////                .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
////        if (iconFilename.equals("github")) this.setVerticalAlignment(TOP);
//        setName(iconFilename);
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