package com.codeofcarbon.visualizer.view;

import com.codeofcarbon.visualizer.Toolbar;

import javax.swing.*;
import javax.swing.plaf.basic.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class MenuBar extends JMenuBar {
    private final ButtonPanel buttonPanel;
    private final JFrame frame;
    private boolean isFullScreen = false;
    private Rectangle appWindow;

    public MenuBar(Toolbar toolbar, JFrame frame) {
        this.buttonPanel = (ButtonPanel) toolbar.getButtonPanel();
        this.frame = frame;

        // ==================================================================================== file menu =====
        JMenu fileMenu = addMenu("File", KeyEvent.VK_F, null);
        addMenuItem("New", KeyEvent.VK_N, fileMenu, e -> toolbar.getRefreshButton().doClick(), "new");
        addMenuItem("Open", KeyEvent.VK_O, fileMenu, e -> toolbar.getOpenButton().doClick(), "open");
        addMenuItem("Save", KeyEvent.VK_S, fileMenu, e -> toolbar.getSaveButton().doClick(), "save");
        addMenuItem("Exit", KeyEvent.VK_E, fileMenu, e -> toolbar.getCloseButton().doClick(), "exit");

        // ==================================================================================== mode menu =====
        JMenu modeMenu = addMenu("Mode", KeyEvent.VK_M, null);

        // =========================================================================== graph mode submenu =====
        JMenu graphMenu = addMenu("Graph", KeyEvent.VK_G, modeMenu);
        addMenuItem("Add Node", KeyEvent.VK_A, graphMenu, e -> setGraphMode(GraphMode.ADD_NODE), "");
        addMenuItem("Add an Edge", KeyEvent.VK_E, graphMenu, e -> setGraphMode(GraphMode.ADD_AN_EDGE), "");
        addMenuItem("Remove Node", KeyEvent.VK_X, graphMenu, e -> setGraphMode(GraphMode.REMOVE_NODE), "");
        addMenuItem("Remove an Edge", KeyEvent.VK_R, graphMenu, e -> setGraphMode(GraphMode.REMOVE_AN_EDGE), "");
        graphMenu.addSeparator();
        addMenuItem("None", KeyEvent.VK_N, graphMenu, e -> setGraphMode(GraphMode.NONE), "");

        // ======================================================================= algorithm mode submenu =====
        JMenu algMenu = addMenu("Algorithm", KeyEvent.VK_A, modeMenu);
        addMenuItem("Depth-First Search", KeyEvent.VK_F, algMenu, e -> setAlgMode(AlgMode.DEPTH_FIRST_SEARCH), "");
        addMenuItem("Breadth-First Search", KeyEvent.VK_B, algMenu, e -> setAlgMode(AlgMode.BREADTH_FIRST_SEARCH), "");
        addMenuItem("Dijkstra's Algorithm", KeyEvent.VK_D, algMenu, e -> setAlgMode(AlgMode.DIJKSTRA_ALGORITHM), "");
        addMenuItem("Prim's Algorithm", KeyEvent.VK_P, algMenu, e -> setAlgMode(AlgMode.PRIM_ALGORITHM), "");
        algMenu.addSeparator();
        addMenuItem("None", KeyEvent.VK_N, algMenu, e -> setAlgMode(AlgMode.NONE), "");

        // =================================================================================== tools menu =====
        JMenu toolsMenu = addMenu("Tools", KeyEvent.VK_T, null);
        addMenuItem("Undo", KeyEvent.VK_U, toolsMenu, e -> toolbar.getUndoButton().doClick(), "undo");
        addMenuItem("Redo", KeyEvent.VK_R, toolsMenu, e -> toolbar.getRedoButton().doClick(), "redo");
        toolsMenu.addSeparator();
        addMenuItem("Prev step (soon)", KeyEvent.VK_P, toolsMenu, e -> toolbar.getPrevButton().doClick(), "prev");
        addMenuItem("Next step (soon)", KeyEvent.VK_N, toolsMenu, e -> toolbar.getNextButton().doClick(), "next");

        // ========================================================================= fullscreen / contact =====
        add(Box.createHorizontalGlue());
        addMenuLabel("Fullscreen");
        addFullScreenOption();
        addMenuLabel("Contact me:");
        addLinkMenu("github", "https://github.com/codeofcarbon");
        addLinkMenu("linked", "https://www.linkedin.com/in/krzysztof-karbownik");
    }

    private void setAlgMode(AlgMode algMode) {
        buttonPanel.getAlgModeComboBox().setSelectedIndex(Arrays.asList(AlgMode.values()).indexOf(algMode));
    }

    private void setGraphMode(GraphMode graphMode) {
        buttonPanel.getGraphModeComboBox().setSelectedIndex(Arrays.asList(GraphMode.values()).indexOf(graphMode));
    }

    private JMenu addMenu(String text, int mnemonic, JMenu menuParent) {
        var menu = new JMenu(text) {
            @Override
            public JPopupMenu getPopupMenu() {
                var popup = super.getPopupMenu();
                popup.setUI(new BasicPopupMenuUI());
                popup.setBorder(BorderFactory.createEmptyBorder());
                popup.setOpaque(false);
                if (menuParent == null) popup.show(this, 0, 23);
                return popup;
            }
        };
        setMenuComponentDefaults(menu, menuParent, text.toLowerCase(), 22, 22, mnemonic);
        return menu;
    }

    private void addMenuItem(String text, int mnemonic, JMenu menuParent,
                             ActionListener listener, String iconFilename) {
        var menuItem = new JMenuItem(text);
        setMenuComponentDefaults(menuItem, menuParent, iconFilename, 18, 18, mnemonic);
        menuItem.addActionListener(listener);
        if ("prev".equals(iconFilename) || "next".equals(iconFilename))      // todo - not yet implemented
            menuItem.setEnabled(false);
    }

    private void addLinkMenu(String iconFilename, String url) {
        var menu = new JMenu();
        menu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(url));
                } catch (URISyntaxException | IOException ignored) {
                }
            }
        });
        menu.setBorder(BorderFactory.createEmptyBorder());
        menu.setIcon(IconMaker.loadIcon(iconFilename + " blue", "buttons", 18, 18));
        add(menu);
    }

    private void addFullScreenOption() {
        var menu = new JMenu();
        menu.setBorder(BorderFactory.createEmptyBorder());
        menu.setPreferredSize(new Dimension(40, 20));
        var icon = IconMaker.loadIcon("toggle small", "buttons", 32, 18);
        var selectedIcon = IconMaker.loadIcon("toggle full", "buttons", 32, 18);
        menu.setIcon(icon);
        menu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!isFullScreen) {
                    menu.setIcon(selectedIcon);
                    appWindow = new Rectangle(frame.getBounds());
                    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                } else {
                    frame.setBounds(appWindow);
                    menu.setIcon(icon);
                }
                isFullScreen = !isFullScreen;
            }
        });
        add(menu);
    }

    private void addMenuLabel(String text) {
        var label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        label.setEnabled(false);
        add(label);
    }

    private void setMenuComponentDefaults(JMenuItem comp, JMenu menuParent, String iconFilename,
                                          int iconWidth, int iconHeight, int mnemonic) {
        comp.setBorder(BorderFactory.createEmptyBorder());
        comp.setMnemonic(mnemonic);
        if (menuParent != null) {
            var icon = IconMaker.loadIcon(iconFilename, "buttons", iconWidth, iconHeight);
            var rolloverIcon = IconMaker.loadIcon(iconFilename + " blue", "buttons", iconWidth, iconHeight);
            comp.setIcon(rolloverIcon);
            comp.addChangeListener(e -> {
                comp.setIcon(comp.isSelected() || comp.isArmed() ? icon : rolloverIcon);
                comp.setFont(new Font("Segoe UI", comp.isSelected() || comp.isArmed() ? Font.BOLD : Font.PLAIN, 12));
            });
            comp.setForeground(Color.WHITE);
            menuParent.add(comp);
        } else add(comp);
    }
}