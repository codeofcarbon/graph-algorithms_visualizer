package visualizer;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Toolbar extends JPanel {
    private static final ImageIcon OPEN = new ImageIcon(new ImageIcon("src/main/resources/icons/open.png")
            .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
    private static final ImageIcon SAVE = new ImageIcon(new ImageIcon("src/main/resources/icons/save.png")
            .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
    private Map<Vertex, List<Edge>> graphData = new ConcurrentHashMap<>();
    private final JFileChooser fileChooser;
    private final JPanel buttonsPanel;
    GraphService service;
    JLabel infoPanel;
    JLabel algorithmModeLabel;
    JLabel modeLabel;
    JComboBox<AlgMode> algModeComboBox;
    JComboBox<GraphMode> graphModeComboBox;

    public Toolbar(JFileChooser fileChooser) {
        setLayout(new GridLayout(2, 1));
        setPreferredSize(new Dimension(this.getWidth(), 60));
        setBackground(Color.BLACK);
        this.fileChooser = fileChooser;

        JPanel toolPanel = new JPanel(new GridLayout(1, 5));

        algorithmModeLabel = addLabel("ALGORITHM MODE - ", "NONE", "AlgorithmMode");
        algModeComboBox = addComboBox("AlgorithmModesList");
        Arrays.stream(AlgMode.values()).forEach(algModeComboBox::addItem);

        buttonsPanel = new JPanel(new GridLayout(1, 4));

        modeLabel = addLabel("GRAPH MODE - ", "ADD A VERTEX", "Mode");
        graphModeComboBox = addComboBox("GraphModesList");
        Arrays.stream(GraphMode.values()).forEach(graphModeComboBox::addItem);

        toolPanel.add(algorithmModeLabel);
        toolPanel.add(algModeComboBox);
        toolPanel.add(buttonsPanel);
        toolPanel.add(modeLabel);
        toolPanel.add(graphModeComboBox);
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
        algModeComboBox.addActionListener(event ->
                service.switchAlgorithmMode((AlgMode) algModeComboBox.getSelectedItem()));
        graphModeComboBox.addActionListener(event ->
                service.switchMode((GraphMode) graphModeComboBox.getSelectedItem()));

        JButton openButton = addButton(OPEN, "OpenButton");
        openButton.addActionListener(event -> {
            fileChooser.setDialogTitle("Select graph data file");
            int returnValue = fileChooser.showOpenDialog(service.getGraph());
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                graphData = (ConcurrentHashMap<Vertex, List<Edge>>)
                        Storage.deserialize(String.valueOf(fileChooser.getSelectedFile()));
                service.clearGraph();
                graphData.forEach((key, value) -> {
                    service.getVertices().add(key);
                    service.getEdges().addAll(value);
                });
                service.getGraph().repaint();
                graphData.clear();
            }
        });

        JButton saveButton = addButton(SAVE, "SaveButton");
        saveButton.addActionListener(event -> {
            fileChooser.setDialogTitle("Save graph data file");
            int returnValue = fileChooser.showSaveDialog(service.getGraph());
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                service.getVertices().forEach(vertex -> graphData.put(vertex, vertex.connectedEdges));
                Storage.serialize(graphData, String.valueOf(fileChooser.getSelectedFile()));
            }
        });
    }

    private <T> JComboBox<T> addComboBox(String name) {
        var newComboBox = new JComboBox<T>();
        newComboBox.setName(name);
        newComboBox.setBackground(Color.BLACK);
        newComboBox.setForeground(Color.WHITE);
        newComboBox.setFocusable(false);
        return newComboBox;
    }

    private JLabel addLabel(String partOne, String partTwo, String name) {
        var label = new JLabel(String.format("<html><font color=gray>%s", partOne),
//                                             + "<font size=+1 color=white><i>%s</i>",
//                partOne, partTwo),
                SwingConstants.CENTER);
        label.setBackground(Color.BLACK);
        label.setOpaque(true);
        label.setName(name);
        return label;
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
}