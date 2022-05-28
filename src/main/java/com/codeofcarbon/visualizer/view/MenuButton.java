package com.codeofcarbon.visualizer.view;

import javax.swing.*;
import javax.swing.plaf.basic.BasicToggleButtonUI;
import java.awt.*;
import java.awt.event.*;

public class MenuButton extends JToggleButton {

    public MenuButton(String iconFilename, String toolTipText, ButtonPanel panel, JComponent target) {
        setUI(new BasicToggleButtonUI());
        TipManager.setToolTipText(this, String.format("<html><font color=rgb(128,128,128)>%s", toolTipText));
        int size = 70;
        setPreferredSize(new Dimension(size, size));
        var icon = IconMaker.loadIcon(iconFilename + " ring", "buttons", size, size);
        var rolloverIcon = IconMaker.loadIcon(iconFilename + " blue", "buttons", size + 4, size + 4);
        setIcon(icon);
        setRolloverEnabled(true);
        setSelectedIcon(rolloverIcon);
        new RolloverAnimator(this, icon, rolloverIcon);
        panel.add(this);
        setOpaque(false);
        setVisible(true);
        setBorder(BorderFactory.createEmptyBorder());

        addActionListener(e -> {
            if (isSelected()) {
                var popup = target.getComponentPopupMenu();
                popup.setVisible(true);
                if (target instanceof JComboBox) {
                    if (((ModeList<?>) target).getClazz().equals(AlgMode.class))
                        popup.show(this, getWidth(), getY());
                    else popup.show(this, -popup.getWidth(), getY());
                } else popup.show(this, getWidth() / 2 - popup.getWidth() / 2, getHeight() - 5);
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (!isSelected() && getModel().isRollover()) {
                    panel.getButtonGroup().clearSelection();
                    doClick();
                }
            }
        });
    }
}