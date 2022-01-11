package visualizer;

import java.awt.*;

public class ApplicationRunner {
    public static void main(String[] args) {
        EventQueue.invokeLater(Graph::new);
    }
}