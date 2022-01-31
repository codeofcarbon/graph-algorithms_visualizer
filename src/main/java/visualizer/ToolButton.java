package visualizer;

import lombok.Getter;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ToolButton extends JButton {
    static final List<ToolButton> buttons = new ArrayList<>();
    JPanel buttonsPanel;
    boolean hovered, pressed;
//    /*static*/ ButtonBar buttonsPanel;
//    private final JLabel buttonLabel;

    public ToolButton(String iconFilename, String toolTipText, JPanel buttonsPanel/*, ButtonBar buttonsPanel*/) {
        this.buttonsPanel = buttonsPanel;
        System.err.println(iconFilename); //  todo remove
        setUI(new BasicButtonUI());
        setBackground(Color.BLACK);
        setForeground(Color.BLACK);
        setPreferredSize(new Dimension(50, 50));
//        setSize(getPreferredSize());
        setToolTipText(String.format("<html><font color=gray>%s", toolTipText));
        setIcon(new ImageIcon(new ImageIcon(
                String.format("src/main/resources/icons/buttons/%s.png", iconFilename))
                .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
        setRolloverEnabled(true);
        setRolloverIcon(new ImageIcon(new ImageIcon(
                String.format("src/main/resources/icons/buttons/%s blue.png", iconFilename))
                .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
        setPressedIcon(new ImageIcon(new ImageIcon("src/main/resources/icons/buttons/temp/layered.png") // todo=====
                .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
//        if (iconFilename.equals("github")) this.setVerticalAlignment(TOP);
        setName(iconFilename);
        buttonsPanel.add(this);
//        addListeners();
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