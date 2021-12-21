import javax.swing.*;
import java.awt.event.KeyEvent;

public class MenuBar extends JMenuBar {

    public MenuBar(Graph graph) {
        setName("MenuBar");

        var fileMenu = new JMenu("File");
        fileMenu.setName("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        add(fileMenu);
        var clearGraph = new JMenuItem("New");
        clearGraph.setName("New");
        clearGraph.setMnemonic(KeyEvent.VK_N);
        clearGraph.addActionListener(e -> graph.clearGraph());
        fileMenu.add(clearGraph);
        var exit = new JMenuItem("Exit");
        exit.setName("Exit");
        exit.setMnemonic(KeyEvent.VK_E);
        exit.addActionListener(e -> System.exit(0));
        fileMenu.add(exit);

        var modeMenu = new JMenu("Mode");
        modeMenu.setName("Mode");
        modeMenu.setMnemonic(KeyEvent.VK_M);
        add(modeMenu);
        var addVertex = new JMenuItem("Add a Vertex");
        addVertex.setName("Add a Vertex");
        addVertex.setMnemonic(KeyEvent.VK_V);
        addVertex.addActionListener(e -> graph.switchMode(Mode.ADD_A_VERTEX));
        modeMenu.add(addVertex);
        var addEdge = new JMenuItem("Add an Edge");
        addEdge.setName("Add an Edge");
        addEdge.setMnemonic(KeyEvent.VK_E);
        addEdge.addActionListener(e -> graph.switchMode(Mode.ADD_AN_EDGE));
        modeMenu.add(addEdge);
        var removeVertex = new JMenuItem("Remove a Vertex");
        removeVertex.setName("Remove a Vertex");
        removeVertex.setMnemonic(KeyEvent.VK_V);
        removeVertex.addActionListener(e -> graph.switchMode(Mode.REMOVE_A_VERTEX));
        modeMenu.add(removeVertex);
        var removeEdge = new JMenuItem("Remove an Edge");
        removeEdge.setName("Remove an Edge");
        removeEdge.setMnemonic(KeyEvent.VK_E);
        removeEdge.addActionListener(e -> graph.switchMode(Mode.REMOVE_AN_EDGE));
        modeMenu.add(removeEdge);
        var none = new JMenuItem("None");
        none.setName("None");
        none.setMnemonic(KeyEvent.VK_N);
        none.addActionListener(e -> graph.switchMode(Mode.NONE));
        modeMenu.add(none);
    }
}