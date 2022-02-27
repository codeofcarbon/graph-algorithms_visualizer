package visualizer;

import visualizer.temp.transPop.TranslucentPopupMenu;

import javax.swing.*;
import javax.swing.plaf.basic.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class MenuBar extends JMenuBar {
    private final Toolbar toolbar;

    public MenuBar(Toolbar toolbar) {
        this.toolbar = toolbar;

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
        addMenuItem("Add a Vertex", KeyEvent.VK_A, graphMenu, e -> setGraphMode(GraphMode.ADD_A_VERTEX), "");
        addMenuItem("Add an Edge", KeyEvent.VK_E, graphMenu, e -> setGraphMode(GraphMode.ADD_AN_EDGE), "");
        addMenuItem("Remove a Vertex", KeyEvent.VK_X, graphMenu, e -> setGraphMode(GraphMode.REMOVE_A_VERTEX), "");
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

        // ================================================================================= contact menu =====
        add(Box.createHorizontalGlue());
        addContactMeLabel();
        addLinkMenu("github", "https://github.com/codeofcarbon");
        addLinkMenu("linked", "https://www.linkedin.com/in/krzysztof-karbownik");
    }

    private void setAlgMode(AlgMode algMode) {
        toolbar.getButtonPanel().getAlgModeComboBox()
                .setSelectedIndex(Arrays.asList(AlgMode.values()).indexOf(algMode));
    }

    private void setGraphMode(GraphMode graphMode) {
        toolbar.getButtonPanel().getGraphModeComboBox()
                .setSelectedIndex(Arrays.asList(GraphMode.values()).indexOf(graphMode));
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
        setMenuComponentDefaults(menu, text.toLowerCase(), 22, mnemonic, menuParent);
        return menu;
    }

    private void addMenuItem(String text, int mnemonic, JMenu menuParent,
                             ActionListener listener, String iconFilename) {
        var menuItem = new JMenuItem(text);
        setMenuComponentDefaults(menuItem, iconFilename, 18, mnemonic, menuParent);
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
        menu.setIcon(loadIcon(String.format("%s blue", iconFilename), 18));
        add(menu);
    }

    private void addContactMeLabel() {
        var label = new JLabel("Contact me:");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        label.setEnabled(false);
        add(label);
    }

    private ImageIcon loadIcon(String iconFilename, int size) {
        return new ImageIcon(new ImageIcon(
                String.format("src/main/resources/icons/buttons/%s.png", iconFilename))
                .getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH));
    }

    private void setMenuComponentDefaults(JMenuItem comp, String iconFilename,
                                          int iconSize, int mnemonic, JMenu menuParent) {
        comp.setBorder(BorderFactory.createEmptyBorder());
        comp.setMnemonic(mnemonic);
        if (menuParent != null) {
            var icon = loadIcon(iconFilename + " blue", iconSize);
            var rolloverIcon = loadIcon(iconFilename, iconSize);
            comp.setIcon(icon);
            comp.addChangeListener(e -> {
                comp.setIcon(comp.isSelected() || comp.isArmed() ? rolloverIcon : icon);
                comp.setFont(new Font("Segoe UI", comp.isSelected() || comp.isArmed() ? Font.BOLD : Font.PLAIN, 12));
            });
            comp.setForeground(Color.WHITE);
            menuParent.add(comp);
        } else add(comp);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Arrays.stream(getComponents()).filter(c -> c instanceof JPopupMenu).forEach(popup -> {
//            if (g instanceof Graphics2D) {
                final int R = 240;
                final int G = 240;
                final int B = 240;
                var point = popup.getLocation();
                Paint p =
                        new GradientPaint(point.x,point.y, new Color(R, G, B, 0),
                                point.x, point.y + popup.getHeight(), new Color(R, G, B, 255), true);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setPaint(p);
                g2d.fillRect(point.x, point.y, point.x + popup.getWidth(), point.y +  popup.getHeight());
//            }
        });
    }
}