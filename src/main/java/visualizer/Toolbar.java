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
    private final JPanel buttonsPanel;
    GraphService service;
    JLabel infoPanel;
    JLabel algorithmModeLabel;
    JLabel modeLabel;

    public Toolbar(JFileChooser fileChooser) {
        setLayout(new GridLayout(2, 1));
        setPreferredSize(new Dimension(this.getWidth(), 60));
        this.fileChooser = fileChooser;

        JPanel toolPanel = new JPanel(new GridLayout(1, 3));
        algorithmModeLabel = addLabel("ALGORITHM MODE - ", "NONE", "AlgorithmMode");
        buttonsPanel = new JPanel(new GridLayout(1, 4));
        modeLabel = addLabel("GRAPH MODE - ", "ADD A VERTEX", "Mode");
        toolPanel.add(algorithmModeLabel);
        toolPanel.add(buttonsPanel);
        toolPanel.add(modeLabel);
        add(toolPanel);
        infoPanel = new JLabel("", SwingConstants.CENTER);
        infoPanel.setBackground(new Color(12, 12, 12, 255));
        infoPanel.setForeground(Color.WHITE);
        infoPanel.setOpaque(true);
        add(infoPanel);

        initListeners();
        setVisible(true);
    }

    @SuppressWarnings("unchecked")
    private void initListeners() {
        JButton openButton = addButton(OPEN, "OpenButton");
        openButton.addActionListener(event -> {
            fileChooser.setDialogTitle("Select graph data file");
            int returnValue = fileChooser.showOpenDialog(service.getGraphWindow());
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                graphData = (ConcurrentHashMap<Vertex, List<Edge>>)
                        Storage.deserialize(String.valueOf(fileChooser.getSelectedFile()));
                service.getGraphWindow().getVertices().clear();
                service.getGraphWindow().getEdges().clear();
                graphData.forEach((key, value) -> {
                    service.getGraphWindow().getVertices().add(key);
                    service.getGraphWindow().getEdges().addAll(value);
                });
                service.getGraphWindow().repaint();
                graphData.clear();
            }
        });

        JButton saveButton = addButton(SAVE, "SaveButton");
        saveButton.addActionListener(event -> {
            fileChooser.setDialogTitle("Save graph data file");
            int returnValue = fileChooser.showSaveDialog(service.getGraphWindow());
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                service.getGraphWindow().getVertices().forEach(vertex -> graphData.put(vertex, vertex.connectedEdges));
                Storage.serialize(graphData, String.valueOf(fileChooser.getSelectedFile()));
            }
        });

        JButton undoButton = addButton(UNDO, "UndoButton");
        undoButton.addActionListener(event -> {

        }/*service.undo()*/);

        JButton redoButton = addButton(REDO, "RedoButton");
        redoButton.addActionListener(event -> {

        }/*service.redo()*/);
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