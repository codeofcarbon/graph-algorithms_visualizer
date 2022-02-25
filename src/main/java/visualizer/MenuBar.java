package visualizer;

import javax.swing.*;
import javax.swing.plaf.basic.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

public class MenuBar extends JMenuBar {
    private final Toolbar toolbar;

    public MenuBar(Toolbar toolbar) {
        this.toolbar = toolbar;

        // ==================================================================================== file menu =====
        JMenu fileMenu = addMenu("File", KeyEvent.VK_F);
        addMenuItem("New", KeyEvent.VK_N, fileMenu, e -> toolbar.getRefreshButton().doClick());
        addMenuItem("Open", KeyEvent.VK_O, fileMenu, e -> toolbar.getOpenButton().doClick());
        addMenuItem("Save", KeyEvent.VK_S, fileMenu, e -> toolbar.getSaveButton().doClick());
        addMenuItem("Exit", KeyEvent.VK_E, fileMenu, e -> toolbar.getCloseButton().doClick());

        // ============================================================================== graph mode menu =====
        JMenu graphMenu = addMenu("Mode", KeyEvent.VK_M);
        addMenuItem("Add a Vertex", KeyEvent.VK_A, graphMenu, e -> setGraphMode(GraphMode.ADD_A_VERTEX));
        addMenuItem("Add an Edge", KeyEvent.VK_E, graphMenu, e -> setGraphMode(GraphMode.ADD_AN_EDGE));
        addMenuItem("Remove a Vertex", KeyEvent.VK_X, graphMenu, e -> setGraphMode(GraphMode.REMOVE_A_VERTEX));
        addMenuItem("Remove an Edge", KeyEvent.VK_R, graphMenu, e -> setGraphMode(GraphMode.REMOVE_AN_EDGE));
        graphMenu.addSeparator();
        addMenuItem("None", KeyEvent.VK_N, graphMenu, e -> setGraphMode(GraphMode.NONE));

        // ========================================================================== algorithm mode menu =====
        JMenu algMenu = addMenu("Algorithms", 'A');
        addMenuItem("Depth-First Search", KeyEvent.VK_F, algMenu, e -> setAlgorithmMode(AlgMode.DEPTH_FIRST_SEARCH));
        addMenuItem("Breadth-First Search", KeyEvent.VK_B, algMenu, e -> setAlgorithmMode(AlgMode.BREADTH_FIRST_SEARCH));
        addMenuItem("Dijkstra's Algorithm", KeyEvent.VK_D, algMenu, e -> setAlgorithmMode(AlgMode.DIJKSTRA_ALGORITHM));
        addMenuItem("Prim's Algorithm", KeyEvent.VK_P, algMenu, e -> setAlgorithmMode(AlgMode.PRIM_ALGORITHM));
        algMenu.addSeparator();
        addMenuItem("None", KeyEvent.VK_N, algMenu, e -> setAlgorithmMode(AlgMode.NONE));

        // =================================================================================== tools menu =====
        JMenu toolsMenu = addMenu("Tools", KeyEvent.VK_T);
        addMenuItem("Undo", KeyEvent.VK_U, toolsMenu, e -> toolbar.getUndoButton().doClick());
        addMenuItem("Redo", KeyEvent.VK_R, toolsMenu, e -> toolbar.getRedoButton().doClick());
        addMenuItem("Prev step (soon)", KeyEvent.VK_P, toolsMenu, e -> toolbar.getPrevButton().doClick());
        addMenuItem("Next step (soon)", KeyEvent.VK_N, toolsMenu, e -> toolbar.getNextButton().doClick());

        // ================================================================================= contact menu =====
        add(Box.createHorizontalGlue());
        JMenu contactMenu = new JMenu("Contact") {
            @Override
            public JPopupMenu getPopupMenu() {
                var popup = super.getPopupMenu();
                popup.setUI(new BasicPopupMenuUI());
                popup.setPopupSize(50, 70);
                popup.setLayout(new GridLayout(0, 1));
                popup.setBorder(BorderFactory.createEmptyBorder());
                popup.setBackground(new Color(0, 0, 0, 0));
                popup.setOpaque(false);
                popup.setVisible(true);
                popup.show(this, 0, 25);
                return popup;
            }
        };
        addIconMenuItem("github", contactMenu, e -> {
            try {
                Desktop.getDesktop().browse(new URI("https://github.com/codeofcarbon"));
            } catch (URISyntaxException | IOException ex) {
                // todo error message or dialog
            }
        });
        addIconMenuItem("linked", contactMenu, e -> {
            try {
                Desktop.getDesktop().browse(new URI("https://www.linkedin.com/in/krzysztof-karbownik"));
            } catch (URISyntaxException | IOException ex) {
                // todo error message or dialog
            }
        });
        add(contactMenu);
    }

    private void setAlgorithmMode(AlgMode algMode) {
        toolbar.getButtonPanel().getAlgModeComboBox()
                .setSelectedIndex(Arrays.asList(AlgMode.values()).indexOf(algMode));
    }

    private void setGraphMode(GraphMode graphMode) {
        toolbar.getButtonPanel().getGraphModeComboBox()
                .setSelectedIndex(Arrays.asList(GraphMode.values()).indexOf(graphMode));
    }

    private JMenu addMenu(String text, int mnemonic) {
        var menu = new JMenu(text);
        menu.setMnemonic(mnemonic);
        add(menu);
        return menu;
    }

    private void addMenuItem(String text, int mnemonic, JMenu menu, ActionListener listener) {
        var menuItem = new JMenuItem(text);
        menuItem.setUI(new BasicMenuItemUI());
        menuItem.setMnemonic(mnemonic);
        menuItem.addActionListener(listener);
        menu.add(menuItem);
    }

    private void addIconMenuItem(String iconFilename, JMenu menu, ActionListener listener) {
        var icon = loadIcon(iconFilename, 30, false);
        var menuItem = new JMenuItem(icon);
//        menuItem.setIcon(icon);
        menuItem.setUI(new BasicButtonUI());
        menuItem.setBackground(new Color(0, 0, 0, 0));
        menuItem.setOpaque(false);
        menuItem.setRolloverEnabled(true);
        menuItem.addActionListener(listener);
        new RolloverAnimator(menuItem, icon, loadIcon(iconFilename, 34, true));             // todo refactor?
        menu.add(menuItem);
    }

    private static ImageIcon loadIcon(String iconFilename, int size, boolean hovered) {
        return new ImageIcon(new ImageIcon(
                String.format("src/main/resources/icons/buttons/%s.png", iconFilename + (hovered ? " blue" : "")))
                .getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH));
    }
}