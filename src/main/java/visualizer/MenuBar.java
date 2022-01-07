package visualizer;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.Arrays;

public class MenuBar extends JMenuBar {
    private final Toolbar toolbar;

    public MenuBar(GraphService service, Toolbar toolbar) {
        this.toolbar = toolbar;
        setName("MenuBar");

        // ======================================================================= file menu =====
        JMenu fileMenu = addMenu("File", KeyEvent.VK_F);

        JMenuItem clearGraph = addMenuItem("New", KeyEvent.VK_N, fileMenu);
        clearGraph.addActionListener(event -> service.clearGraph());

        JMenuItem exit = addMenuItem("Exit", KeyEvent.VK_E, fileMenu);
        exit.addActionListener(event -> System.exit(0));

        // ================================================================= graph mode menu =====
        JMenu modeMenu = addMenu("Mode", KeyEvent.VK_M);

        JMenuItem addVertex = addMenuItem("Add a Vertex", KeyEvent.VK_A, modeMenu);
        addVertex.addActionListener(event -> setGraphMode(GraphMode.ADD_A_VERTEX));

        JMenuItem addEdge = addMenuItem("Add an Edge", KeyEvent.VK_E, modeMenu);
        addEdge.addActionListener(event -> setGraphMode(GraphMode.ADD_AN_EDGE));

        JMenuItem removeVertex = addMenuItem("Remove a Vertex", KeyEvent.VK_X, modeMenu);
        removeVertex.addActionListener(event -> setGraphMode(GraphMode.REMOVE_A_VERTEX));

        JMenuItem removeEdge = addMenuItem("Remove an Edge", KeyEvent.VK_R, modeMenu);
        removeEdge.addActionListener(event -> setGraphMode(GraphMode.REMOVE_AN_EDGE));

        modeMenu.addSeparator();

        JMenuItem none = addMenuItem("None", KeyEvent.VK_N, modeMenu);
        none.addActionListener(event -> setGraphMode(GraphMode.NONE));

        // ============================================================= algorithm mode menu =====
        JMenu algorithmMenu = addMenu("Algorithms", KeyEvent.VK_A);

        JMenuItem dfsAlgorithm = addMenuItem("Depth-First Search", KeyEvent.VK_F, algorithmMenu);
        dfsAlgorithm.addActionListener(event -> setAlgorithmMode(AlgMode.DEPTH_FIRST_SEARCH));

        JMenuItem bfsAlgorithm = addMenuItem("Breadth-First Search", KeyEvent.VK_B, algorithmMenu);
        bfsAlgorithm.addActionListener(event -> setAlgorithmMode(AlgMode.BREADTH_FIRST_SEARCH));

        JMenuItem dijkstraAlgorithm = addMenuItem("Dijkstra's Algorithm", KeyEvent.VK_D, algorithmMenu);
        dijkstraAlgorithm.addActionListener(event -> setAlgorithmMode(AlgMode.DIJKSTRA_ALGORITHM));

        JMenuItem primAlgorithm = addMenuItem("Prim's Algorithm", KeyEvent.VK_P, algorithmMenu);
        primAlgorithm.addActionListener(event -> setAlgorithmMode(AlgMode.PRIM_ALGORITHM));
    }

    private void setAlgorithmMode(AlgMode algMode) {
        toolbar.getAlgModeComboBox().setSelectedIndex(Arrays.asList(AlgMode.values()).indexOf(algMode));
    }

    private void setGraphMode(GraphMode graphMode) {
        toolbar.getGraphModeComboBox().setSelectedIndex(Arrays.asList(GraphMode.values()).indexOf(graphMode));
    }

    private JMenu addMenu(String text, int mnemonic) {
        var menu = new JMenu(text);
        menu.setName(text);
        menu.setMnemonic(mnemonic);
        add(menu);
        return menu;
    }

    private JMenuItem addMenuItem(String text, int mnemonic, JMenu menu) {
        var menuItem = new JMenuItem(text);
        menuItem.setName(text);
        menuItem.setMnemonic(mnemonic);
        menu.add(menuItem);
        return menuItem;
    }
}