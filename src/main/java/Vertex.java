import javax.swing.*;
import java.awt.*;

public class Vertex extends JPanel {

    public Vertex(int id) {
        setName("Vertex " + id);
        setBackground(Color.BLACK);
        setSize(new Dimension(50, 50));
        setLocation(id == 0 ? new Point(0, 0) : id == 1 ? new Point(734, 0) :
                id == 2 ? new Point(0, 511) : new Point(734, 511));
        var vertexID = new JLabel("" + id);
        vertexID.setName("VertexLabel " + id);
        vertexID.setFont(new Font("Courier", Font.ITALIC, 30));
        vertexID.setForeground(Color.CYAN);
        add(vertexID);
        setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.DARK_GRAY.darker());
        g.fillOval(0, 0, 50, 50);
    }
}