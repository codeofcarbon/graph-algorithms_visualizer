package visualizer;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicComboPopup;
import java.awt.*;
import java.awt.event.*;

public class MenuButton extends JButton {
    //    private final JLabel pressedLabel = new JLabel(loadIcon("layered", 30, false));   // todo fix it
    private static boolean popping = false;
    private static Popup popup;
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

//        add(pressedLabel);                              // todo fix it
//        pressedLabel.setVisible(false);

        setOpaque(false);
        setVisible(true);

//        if (target != null) {
//            addActionListener(e -> {
//                if (target instanceof JComboBox) {
//                    if (popping) popup.hide();
//                    else {
//                        var root = container.getRootPane();
////                        var pop = target.getComponentPopupMenu();
////                        System.err.println(pop);
////                        pop.setVisible(true);
//
//                        PopupFactory factory = PopupFactory.getSharedInstance();
//                        var point = container.getLocationOnScreen();
//                        popup = factory.getPopup(root, target.getComponentPopupMenu(),
//                                point.x + /*container.getLocation().x */+container.getWidth() - 182, //- comboBox.getWidth(),
//                                point.y + container.getHeight());
////                        PopupFactory factory = PopupFactory.getSharedInstance();
////                        Point point = container.getLocationOnScreen();
////                        popup.setLocation(point.x + container.getWidth() - 182, point.y + container.getHeight());
////                        popup = factory.getPopup(tField, panel, point.x, point.y + tField.getHeight());
//                        popup.show();
//                    }
//                    popping = !popping;
//                } else target.setVisible(!target.isVisible());
//            });
//        }
        if (target != null) {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    super.mousePressed(e);
                    if (target instanceof JComboBox) {
                        pop = target.getComponentPopupMenu();
                        var invoker = e.getComponent();
                        pop.setVisible(!pop.isVisible());
                        pop.show(invoker, invoker.getWidth(), invoker.getHeight());
                    } else target.setVisible(!target.isVisible());
                    System.err.println(container.getSize());
                }
                // todo sometimes null here - catch when and fix that
//            @Override
//            public void mousePressed(MouseEvent e) {                                 // todo fix it
//                super.mousePressed(e);
//                if (target == null) {
//                    pressedLabel.setLocation(MenuButton.this.getLocation());
//                    pressedLabel.setVisible(true);
//                }
//            }
//
//            @Override
//            public void mouseReleased(MouseEvent e) {                                   // todo fix it
//                super.mouseReleased(e);
//                if (target == null) pressedLabel.setVisible(false);
//            }
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