package visualizer;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Toolbar extends JPanel {
    private final JFileChooser fileChooser;
    JLabel infoPanel;
    JLabel algorithmModeLabel;
    JLabel modeLabel;
    JPanel buttonsPanel;
    JButton undoButton;
    JButton redoButton;
    JButton openButton;
    JButton saveButton;
    GraphService service;                                    
    Map<Vertex, List<Edge>> connects = new ConcurrentHashMap<>();

    private static final ImageIcon OPEN = new ImageIcon(new ImageIcon("src/main/resources/icons/open.png")
            .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
    private static final ImageIcon SAVE = new ImageIcon(new ImageIcon("src/main/resources/icons/save.png")
            .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
    private static final ImageIcon UNDO = new ImageIcon(new ImageIcon("src/main/resources/icons/undo.png")
            .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
    private static final ImageIcon REDO = new ImageIcon(new ImageIcon("src/main/resources/icons/redo.png")
            .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));

    public Toolbar(JFileChooser fileChooser) {
        this.fileChooser = fileChooser;
        setLayout(new GridLayout(2, 3));
        setPreferredSize(new Dimension(this.getWidth(), 50));
        algorithmModeLabel = addLabel("<html><font color=gray>ALGORITHM MODE - " +
                                      "<font size=+1 color=white><i>NONE</i>", "AlgorithmMode");
        add(algorithmModeLabel);
        buttonsPanel = new JPanel(new GridLayout(1, 4));
        add(buttonsPanel);
        modeLabel = addLabel("<html><font color=gray>GRAPH MODE - " +
                             "<font size=+1 color=white><i>ADD A VERTEX</i>", "Mode");
        add(modeLabel);

        infoPanel = addLabel("", "InfoPanel");
        add(infoPanel, BorderLayout.SOUTH, SwingConstants.CENTER);

        initComponents();
        setVisible(true);
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
                service.vertices.clear();
                service.edges.clear();
                connects.forEach((key, value) -> {
                    service.vertices.add(key);
                    service.edges.addAll(value);
                });
                service.getGraph().repaint();
            }
        });

        saveButton = addButton(SAVE, "SaveButton");
        saveButton.addActionListener(event -> {
            fileChooser.setDialogTitle("Save graph data file");
            int returnValue = fileChooser.showSaveDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                connects.clear();
                service.vertices.forEach(vertex -> connects.put(vertex, vertex.connectedEdges));
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
        button.setPreferredSize(new Dimension(40, 30));
        button.setBackground(new Color(25, 25, 25, 255));
        button.setOpaque(true);
        button.setName(name);
        button.setIcon(icon);
        button.setFocusPainted(false);
        buttonsPanel.add(button);
        return button;
    }

    private JLabel addLabel(String text, String name) {
        var label = new JLabel(text, SwingConstants.CENTER);
        label.setName(name);
        label.setBackground(new Color(25, 25, 25, 255));
        label.setOpaque(true);
        return label;
    }
}