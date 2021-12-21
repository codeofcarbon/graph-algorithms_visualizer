import javax.swing.*;
import java.awt.*;

public class Vertex extends JPanel {

    public Vertex(String id) {
        setName("Vertex " + id);
        setBackground(Color.BLACK);
        setSize(new Dimension(50, 50));
        var vertexID = new JLabel(id);
        vertexID.setName("VertexLabel " + id);
        vertexID.setFont(new Font("Courier", Font.ITALIC, 30));
        vertexID.setForeground(Color.CYAN);
        add(vertexID);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.DARK_GRAY.darker());
        g.fillOval(0, 0, 50, 50);
    }
}