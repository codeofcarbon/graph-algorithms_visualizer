import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Graph extends JPanel {

    public Graph() {
        setName("Graph");
        setBackground(Color.BLACK);
        setLayout(null);
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                createNewVertex(e);
            }
        });
    }

    void createNewVertex(MouseEvent e) {
        var out = JOptionPane.showInputDialog(this, "Enter the Vertex ID (Should be 1 char):", "Vertex",
                JOptionPane.INFORMATION_MESSAGE, null, null, null);
        if (out == null) return;
        String id = out.toString();
        if (id.length() == 1 && !id.isBlank()) {
            Vertex vertex = new Vertex(id);
            vertex.setLocation(e.getX() - 25, e.getY() - 25);
            add(vertex);
            repaint();
            revalidate();
        } else {
            createNewVertex(e);
        }
    }
}