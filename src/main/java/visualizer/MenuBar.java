package visualizer;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Arrays;

public class MenuBar extends JMenuBar {
    private final Toolbar toolbar;

    public MenuBar(Toolbar toolbar) {
        this.toolbar = toolbar;

        // ==================================================================================== file menu =====
        JMenu fileMenu = addMenu("File", KeyEvent.VK_F);
        addMenuItem("Save", KeyEvent.VK_S, fileMenu, e -> toolbar.getSaveButton().doClick());
        addMenuItem("New", KeyEvent.VK_N, fileMenu, e -> toolbar.getRefreshButton().doClick());
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
        JMenu algMenu = addMenu("Algorithms", KeyEvent.VK_A);
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
        JMenu contactMenu = addMenu("Contact", KeyEvent.VK_C);
        addMenuItem("Github", KeyEvent.VK_G, contactMenu, e -> toolbar.getGithubButton().doClick());
        addMenuItem("LinkedIn", KeyEvent.VK_L, contactMenu, e -> toolbar.getLinkedButton().doClick());
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
        menu.setName(text);
        menu.setMnemonic(mnemonic);
        add(menu);
        return menu;
    }

    private void addMenuItem(String text, int mnemonic, JMenu menu, ActionListener listener) {
        var menuItem = new JMenuItem(text);
        menuItem.setName(text);
        menuItem.setMnemonic(mnemonic);
        menuItem.addActionListener(listener);
        menu.add(menuItem);
    }
}