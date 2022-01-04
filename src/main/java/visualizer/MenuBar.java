package visualizer;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class MenuBar extends JMenuBar {

    public MenuBar(GraphService service) {
        setName("MenuBar");

        // ======================================================================= file menu =====
        JMenu fileMenu = addMenu("File", KeyEvent.VK_F);

        JMenuItem clearGraph = addMenuItem("New", KeyEvent.VK_N, fileMenu);
        clearGraph.addActionListener(e -> service.clearGraph());

        JMenuItem exit = addMenuItem("Exit", KeyEvent.VK_E, fileMenu);
        exit.addActionListener(e -> System.exit(0));

        // ================================================================= graph mode menu =====
        JMenu modeMenu = addMenu("Mode", KeyEvent.VK_M);

        JMenuItem addVertex = addMenuItem("Add a Vertex", KeyEvent.VK_A, modeMenu);
        addVertex.addActionListener(e -> service.switchMode(GraphMode.ADD_A_VERTEX));

        JMenuItem addEdge = addMenuItem("Add an Edge", KeyEvent.VK_E, modeMenu);
        addEdge.addActionListener(e -> service.switchMode(GraphMode.ADD_AN_EDGE));

        JMenuItem removeVertex = addMenuItem("Remove a Vertex", KeyEvent.VK_X, modeMenu);
        removeVertex.addActionListener(e -> service.switchMode(GraphMode.REMOVE_A_VERTEX));

        JMenuItem removeEdge = addMenuItem("Remove an Edge", KeyEvent.VK_R, modeMenu);
        removeEdge.addActionListener(e -> service.switchMode(GraphMode.REMOVE_AN_EDGE));

        modeMenu.addSeparator();

        JMenuItem none = addMenuItem("None", KeyEvent.VK_N, modeMenu);
        none.addActionListener(e -> service.switchMode(GraphMode.NONE));

        // ============================================================= algorithm mode menu =====
        JMenu algorithmMenu = addMenu("Algorithms", KeyEvent.VK_A);

        JMenuItem dfsAlgorithm = addMenuItem("Depth-First Search", KeyEvent.VK_F, algorithmMenu);
        dfsAlgorithm.addActionListener(e -> service.switchAlgorithmMode(AlgMode.DEPTH_FIRST_SEARCH));

        JMenuItem bfsAlgorithm = addMenuItem("Breadth-First Search", KeyEvent.VK_B, algorithmMenu);
        bfsAlgorithm.addActionListener(e -> service.switchAlgorithmMode(AlgMode.BREADTH_FIRST_SEARCH));

        JMenuItem dijkstraAlgorithm = addMenuItem("Dijkstra's Algorithm", KeyEvent.VK_D, algorithmMenu);
        dijkstraAlgorithm.addActionListener(e -> service.switchAlgorithmMode(AlgMode.DIJKSTRA_ALGORITHM));

        JMenuItem primAlgorithm = addMenuItem("Prim's Algorithm", KeyEvent.VK_P, algorithmMenu);
        primAlgorithm.addActionListener(e -> service.switchAlgorithmMode(AlgMode.PRIM_ALGORITHM));
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