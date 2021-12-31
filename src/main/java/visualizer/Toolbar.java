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

    private Map<Vertex, List<Edge>> graphData = new ConcurrentHashMap<>();
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

    public Toolbar(JFileChooser fileChooser) {
        setLayout(new GridLayout(2, 1));
        setPreferredSize(new Dimension(this.getWidth(), 60));
        this.fileChooser = fileChooser;
        addComponents();
        initListeners();
        setVisible(true);
    }

    private void addComponents() {
        JPanel toolPanel = new JPanel(new GridLayout(1, 3));
        algorithmModeLabel = addLabel("ALGORITHM MODE - ", "NONE", "AlgorithmMode");
        buttonsPanel = new JPanel(new GridLayout(1, 4));
        modeLabel = addLabel("GRAPH MODE - ", "ADD A VERTEX", "Mode");

        infoPanel = new JLabel("", SwingConstants.CENTER);
        infoPanel.setBackground(new Color(12, 12, 12, 255));
        infoPanel.setForeground(Color.WHITE);
        infoPanel.setOpaque(true);

        toolPanel.add(algorithmModeLabel);
        toolPanel.add(buttonsPanel);
        toolPanel.add(modeLabel);
        add(toolPanel);
        add(infoPanel);
    }

    @SuppressWarnings("unchecked")
    private void initListeners() {
        openButton = addButton(OPEN, "OpenButton");
        openButton.addActionListener(event -> {
            fileChooser.setDialogTitle("Select graph data file");
            int returnValue = fileChooser.showOpenDialog(graph);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                graphData = (ConcurrentHashMap<Vertex, List<Edge>>)
                        Storage.deserialize(String.valueOf(fileChooser.getSelectedFile()));
                graph.vertices.clear();
                graph.edges.clear();
                graphData.forEach((key, value) -> {
                    graph.vertices.add(key);
                    graph.edges.addAll(value);
                });
                graph.repaint();
                graphData.clear();
            }
        });

        saveButton = addButton(SAVE, "SaveButton");
        saveButton.addActionListener(event -> {
            fileChooser.setDialogTitle("Save graph data file");
            int returnValue = fileChooser.showSaveDialog(graph);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                graph.vertices.forEach(vertex -> graphData.put(vertex, vertex.connectedEdges));
                Storage.serialize(graphData, String.valueOf(fileChooser.getSelectedFile()));
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
        button.setBackground(Color.BLACK);
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
        label.setBackground(Color.BLACK);
        label.setOpaque(true);
        label.setName(name);
        return label;
    }
}