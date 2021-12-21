import javax.swing.*;
import java.awt.event.KeyEvent;

public class ModeMenu extends JMenuBar {

    public ModeMenu(Graph graph) {
        setName("MenuBar");
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
        var none = new JMenuItem("None");
        none.setName("None");
        none.setMnemonic(KeyEvent.VK_N);
        none.addActionListener(e -> graph.switchMode(Mode.NONE));
        modeMenu.add(none);
        setVisible(true);
    }
}