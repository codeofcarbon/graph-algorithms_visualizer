import javax.swing.*;
import java.awt.event.KeyEvent;

public class MenuBar extends JMenuBar {

    public MenuBar(GraphService service) {
        setName("MenuBar");

        // ===================================================================== window menu =====
        JMenu fileMenu = new JMenu("File");
        fileMenu.setName("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        add(fileMenu);

        var clearGraph = new JMenuItem("New");
        clearGraph.setName("New");
        clearGraph.setMnemonic(KeyEvent.VK_N);
        clearGraph.addActionListener(e -> service.clearGraph());
        fileMenu.add(clearGraph);
        var exit = new JMenuItem("Exit");
        exit.setName("Exit");
        exit.setMnemonic(KeyEvent.VK_E);
        exit.addActionListener(e -> System.exit(0));
        fileMenu.add(exit);

        // ================================================================= graph mode menu =====
        JMenu modeMenu = new JMenu("Mode");
        modeMenu.setName("Mode");
        modeMenu.setMnemonic(KeyEvent.VK_M);
        add(modeMenu);

        var addVertex = new JMenuItem("Add a Vertex");
        addVertex.setName("Add a Vertex");
        addVertex.setMnemonic(KeyEvent.VK_V);
        addVertex.addActionListener(e -> service.switchMode(Mode.ADD_A_VERTEX));
        modeMenu.add(addVertex);
        var addEdge = new JMenuItem("Add an Edge");
        addEdge.setName("Add an Edge");
        addEdge.setMnemonic(KeyEvent.VK_E);
        addEdge.addActionListener(e -> service.switchMode(Mode.ADD_AN_EDGE));
        modeMenu.add(addEdge);
        var removeVertex = new JMenuItem("Remove a Vertex");
        removeVertex.setName("Remove a Vertex");
        removeVertex.setMnemonic(KeyEvent.VK_V);
        removeVertex.addActionListener(e -> service.switchMode(Mode.REMOVE_A_VERTEX));
        modeMenu.add(removeVertex);
        var removeEdge = new JMenuItem("Remove an Edge");
        removeEdge.setName("Remove an Edge");
        removeEdge.setMnemonic(KeyEvent.VK_E);
        removeEdge.addActionListener(e -> service.switchMode(Mode.REMOVE_AN_EDGE));
        modeMenu.add(removeEdge);
        var none = new JMenuItem("None");
        none.setName("None");
        none.setMnemonic(KeyEvent.VK_N);
        none.addActionListener(e -> service.switchMode(Mode.NONE));
        modeMenu.add(none);

        // ============================================================= algorithm mode menu =====
        JMenu algorithmMenu = new JMenu("Algorithms");
        algorithmMenu.setName("Algorithms");
        algorithmMenu.setMnemonic(KeyEvent.VK_A);
        add(algorithmMenu);

        var dfsAlgorithm = new JMenuItem("Depth-First Search");
        dfsAlgorithm.setName("Depth-First Search");
        dfsAlgorithm.setMnemonic(KeyEvent.VK_F);
        dfsAlgorithm.addActionListener(e -> service.switchAlgorithmMode(AlgorithmMode.DEPTH_FIRST_SEARCH));
        algorithmMenu.add(dfsAlgorithm);
        var bfsAlgorithm = new JMenuItem("Breadth-First Search");
        bfsAlgorithm.setName("Breadth-First Search");
        bfsAlgorithm.setMnemonic(KeyEvent.VK_B);
        bfsAlgorithm.addActionListener(e -> service.switchAlgorithmMode(AlgorithmMode.BREADTH_FIRST_SEARCH));
        algorithmMenu.add(bfsAlgorithm);
        var dijkstraAlgorithm = new JMenuItem("Dijkstra's Algorithm");
        dijkstraAlgorithm.setName("Dijkstra's Algorithm");
        dijkstraAlgorithm.setMnemonic(KeyEvent.VK_D);
        dijkstraAlgorithm.addActionListener(e -> service.switchAlgorithmMode(AlgorithmMode.DIJKSTRA_ALGORITHM));
        algorithmMenu.add(dijkstraAlgorithm);
        var primAlgorithm = new JMenuItem("Prim's Algorithm");
        primAlgorithm.setName("Prim's Algorithm");
        primAlgorithm.setMnemonic(KeyEvent.VK_P);
        primAlgorithm.addActionListener(e -> service.switchAlgorithmMode(AlgorithmMode.PRIM_ALGORITHM));
        algorithmMenu.add(primAlgorithm);
    }
}