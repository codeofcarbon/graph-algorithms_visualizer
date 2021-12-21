import javax.swing.*;
import java.awt.*;

public class Vertex extends JPanel {
    protected final String id;
    protected final JLabel vertexID;
    protected boolean visited = false;

    public Vertex(String id) {
        this.id = id;
        setName("Vertex " + id);
        setBackground(Color.BLACK);
        setSize(new Dimension(50, 50));
        this.vertexID = new JLabel(id);
        vertexID.setName("VertexLabel " + id);
        vertexID.setFont(new Font("Courier", Font.BOLD, 30));
        add(vertexID);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);
        g.fillOval(1, 1, 48, 48);
    }
}