package visualizer;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Toolbar extends JPanel {
    private static final ImageIcon OPEN = new ImageIcon(new ImageIcon("src/main/resources/icons/open.png")
            .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
    private static final ImageIcon SAVE = new ImageIcon(new ImageIcon("src/main/resources/icons/save.png")
            .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
    private static final ImageIcon UNDO = new ImageIcon(new ImageIcon("src/main/resources/icons/undo.png")
            .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
    private static final ImageIcon REDO = new ImageIcon(new ImageIcon("src/main/resources/icons/redo.png")
            .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));

    Map<Vertex, List<Edge>> connects = new ConcurrentHashMap<>();
    private final JFileChooser fileChooser;

    JLabel infoPanel;
    JLabel algorithmModeLabel;
    JLabel modeLabel;
    JPanel buttonsPanel;
    JButton undoButton;
    JButton redoButton;
    JButton openButton;
    JButton saveButton;

    Graph graph;                                       // todo change IT!!!
    JPanel toolPanel;

    public Toolbar(JFileChooser fileChooser, JLabel displayLabel) {
        setLayout(new GridLayout(2, 1));
        setPreferredSize(new Dimension(this.getWidth(), 60));
        this.fileChooser = fileChooser;
        this.infoPanel = displayLabel;
        addComponents();
        initComponents();
//        setOpaque(true);
        setVisible(true);
    }

    private void addComponents() {
        toolPanel = new JPanel(new GridLayout(1, 3));
        toolPanel.setPreferredSize(new Dimension(this.getWidth(), 25));
//        toolPanel.setBackground(new Color(25, 25, 25, 255));
        toolPanel.setBackground(Color.BLACK);
        toolPanel.setForeground(Color.WHITE);
        toolPanel.setOpaque(true);
        toolPanel.setVisible(true);
        add(toolPanel);

        add(infoPanel);

        algorithmModeLabel = addLabel("ALGORITHM MODE - ", "NONE", "AlgorithmMode");
        toolPanel.add(algorithmModeLabel);
//        add(algorithmModeLabel);

        buttonsPanel = new JPanel(new GridLayout(1, 4));
        toolPanel.add(buttonsPanel);
//        add(buttonsPanel);

        modeLabel = addLabel("GRAPH MODE - ", "ADD A VERTEX", "Mode");
        toolPanel.add(modeLabel);
//        add(modeLabel);



//        infoPanel = new JLabel("", SwingConstants.CENTER);
//        infoPanel.setPreferredSize(new Dimension(this.getWidth(), 25));
//        infoPanel.setBackground(new Color(25, 25, 25, 255));
//        infoPanel.setForeground(Color.WHITE);
//        infoPanel.setOpaque(true);
//        infoPanel.setVisible(true);
//        add(infoPanel, BorderLayout.SOUTH, SwingConstants.CENTER);
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        openButton = addButton(OPEN, "OpenButton");
        openButton.addActionListener(event -> {
            fileChooser.setDialogTitle("Select graph data file");
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                connects.clear();
                connects = (ConcurrentHashMap<Vertex, List<Edge>>)
                        Storage.deserialize(String.valueOf(fileChooser.getSelectedFile()));
                graph.vertices.clear();
                graph.edges.clear();
                connects.forEach((key, value) -> {
                    graph.vertices.add(key);
                    graph.edges.addAll(value);
                });
                graph.repaint();
            }
        });

        saveButton = addButton(SAVE, "SaveButton");
        saveButton.addActionListener(event -> {
            fileChooser.setDialogTitle("Save graph data file");
            int returnValue = fileChooser.showSaveDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                connects.clear();
                graph.vertices.forEach(vertex -> connects.put(vertex, vertex.connectedEdges));
                Storage.serialize(connects, String.valueOf(fileChooser.getSelectedFile()));
            }
        });

        undoButton = addButton(UNDO, "UndoButton");
        undoButton.addActionListener(event -> {

        });

        redoButton = addButton(REDO, "RedoButton");
        redoButton.addActionListener(event -> {

        });
    }

    private JButton addButton(Icon icon, String name) {
        var button = new JButton(icon);
        button.setPreferredSize(new Dimension(40, 25));
        button.setBackground(new Color(25, 25, 25, 255));
        button.setOpaque(true);
        button.setName(name);
        button.setIcon(icon);
        button.setFocusPainted(false);
        buttonsPanel.add(button);
        return button;
    }

    private JLabel addLabel(String partOne, String partTwo, String name) {
        var label = new JLabel(String.format(
                "<html><font color=gray>%s<font size=+1 color=white><i>%s</i>",
                partOne, partTwo), SwingConstants.CENTER);
        label.setName(name);
//        label.setBackground(new Color(25, 25, 25, 255));
        label.setBackground(Color.BLACK);
        label.setOpaque(true);
        return label;
    }
}