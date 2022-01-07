package visualizer;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import javax.swing.plaf.basic.*;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class Toolbar extends JPanel {
    private static final ImageIcon OPEN = new ImageIcon(new ImageIcon("src/main/resources/icons/open.png")
            .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
    private static final ImageIcon SAVE = new ImageIcon(new ImageIcon("src/main/resources/icons/save.png")
            .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
    private static final ImageIcon PREV = new ImageIcon(new ImageIcon("src/main/resources/icons/prev.png")
            .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
    private static final ImageIcon NEXT = new ImageIcon(new ImageIcon("src/main/resources/icons/next.png")
            .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
    private static final ImageIcon UNDO = new ImageIcon(new ImageIcon("src/main/resources/icons/undo.png")
            .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
    private static final ImageIcon REDO = new ImageIcon(new ImageIcon("src/main/resources/icons/redo.png")
            .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
    private static final ImageIcon REFRESH = new ImageIcon(new ImageIcon("src/main/resources/icons/refresh.png")
            .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
    private static final ImageIcon CLOSE = new ImageIcon(new ImageIcon("src/main/resources/icons/close.png")
            .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
    private static final ImageIcon INFO = new ImageIcon(new ImageIcon("src/main/resources/icons/info.png")
            .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
    private static final ImageIcon CHAT = new ImageIcon(new ImageIcon("src/main/resources/icons/chat.png")
            .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
    private static Map<Vertex, List<Edge>> graphData = new ConcurrentHashMap<>();
    private final JFileChooser fileChooser;
    private final JComboBox<String> algModeComboBox;
    private final JComboBox<String> graphModeComboBox;
    private final JPanel buttonsPanel;
    private final JLabel infoLabelTwo;
    @Setter
    private GraphService service;

    public Toolbar(JFileChooser fileChooser) {
        setLayout(new GridLayout(3, 1));
        setPreferredSize(new Dimension(1000, 100));
        setSize(getPreferredSize());
        setBackground(Color.BLACK);
        this.fileChooser = fileChooser;

        buttonsPanel = new JPanel(new GridLayout(1, 10));
        buttonsPanel.setPreferredSize(new Dimension(1000, 40));
        buttonsPanel.setSize(getPreferredSize());
        buttonsPanel.setBackground(Color.BLACK);
        add(buttonsPanel);

        JPanel modePanelOne = new JPanel(new GridLayout(1, 4));
        modePanelOne.setPreferredSize(new Dimension(960, 20));
        modePanelOne.setSize(getPreferredSize());
        modePanelOne.setBackground(Color.BLACK);
        modePanelOne.setOpaque(true);

        JLabel infoLabelOne = new JLabel("", SwingConstants.CENTER);
        infoLabelOne.setBackground(new Color(12, 12, 12, 255));
        infoLabelOne.setForeground(Color.WHITE);
        infoLabelOne.setOpaque(true);

        JLabel emptyLabelOne = new JLabel("", SwingConstants.CENTER);
        emptyLabelOne.setBackground(new Color(12, 12, 12, 255));
        emptyLabelOne.setForeground(Color.WHITE);
        emptyLabelOne.setOpaque(true);

        JLabel algorithmModeLabel = addLabel("ALGORITHM MODE - ");

        algModeComboBox = addComboBox();
        Arrays.stream(AlgMode.values())
                .map(algMode -> algMode.current.toUpperCase())
                .forEach(algModeComboBox::addItem);
        algModeComboBox.setSelectedIndex(4);

        modePanelOne.add(infoLabelOne);
        modePanelOne.add(emptyLabelOne);
        modePanelOne.add(algorithmModeLabel);
        modePanelOne.add(algModeComboBox);
        add(modePanelOne);

        JPanel modePanelTwo = new JPanel(new GridLayout(1, 4));
        modePanelTwo.setPreferredSize(new Dimension(960, 20));
        modePanelTwo.setSize(getPreferredSize());
        modePanelTwo.setBackground(Color.BLACK);
        modePanelTwo.setOpaque(true);

        infoLabelTwo = new JLabel("", SwingConstants.CENTER);
        infoLabelTwo.setBackground(new Color(12, 12, 12, 255));
        infoLabelTwo.setForeground(Color.WHITE);
        infoLabelTwo.setOpaque(true);

        JLabel emptyLabelTwo = new JLabel("", SwingConstants.CENTER);
        emptyLabelTwo.setBackground(new Color(12, 12, 12, 255));
        emptyLabelTwo.setForeground(Color.WHITE);
        emptyLabelTwo.setOpaque(true);

        JLabel graphModeLabel = addLabel("GRAPH MODE - ");

        graphModeComboBox = addComboBox();
        Arrays.stream(GraphMode.values())
                .map(graphMode -> graphMode.current.toUpperCase())
                .forEach(graphModeComboBox::addItem);
        graphModeComboBox.setSelectedIndex(0);

        modePanelTwo.add(infoLabelTwo);
        modePanelTwo.add(emptyLabelTwo);
        modePanelTwo.add(graphModeLabel);
        modePanelTwo.add(graphModeComboBox);
        add(modePanelTwo);

        addListeners();
    }

    @SuppressWarnings("unchecked")
    private void addListeners() {
        algModeComboBox.addActionListener(event -> {
            service.setCurrentModes(Arrays.stream(AlgMode.values())
                    .filter(algMode -> algMode.current.equalsIgnoreCase((String) algModeComboBox.getSelectedItem()))
                    .findFirst().orElse(AlgMode.NONE), GraphMode.NONE);
            infoLabelTwo.setText("Please choose a starting vertex");
            algModeComboBox.setSelectedIndex(Arrays.asList(AlgMode.values()).indexOf(service.getAlgorithmMode()));
        });

        graphModeComboBox.addActionListener(event -> {
            service.setCurrentModes(AlgMode.NONE, Arrays.stream(GraphMode.values())
                    .filter(graphMode -> graphMode.current.equalsIgnoreCase((String) graphModeComboBox.getSelectedItem()))
                    .findFirst().orElse(GraphMode.NONE));
            infoLabelTwo.setText("");
            graphModeComboBox.setSelectedIndex(Arrays.asList(GraphMode.values()).indexOf(service.getGraphMode()));
        });

        JButton openButton = addButton(OPEN, "OpenButton");
        openButton.setToolTipText("load graph data from file");
        openButton.addActionListener(event -> {
            fileChooser.setDialogTitle("Select graph data file");
            int returnValue = fileChooser.showOpenDialog(service.getGraph());
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                graphData = (ConcurrentHashMap<Vertex, List<Edge>>)
                        Storage.deserialize(String.valueOf(fileChooser.getSelectedFile()));
                service.clearGraph();
                graphData.forEach((key, value) -> {
                    service.getMouseHandler().addComponent(key);
                    service.getVertices().add(key);
                    service.getEdges().addAll(value);
                    service.getGraph().add(key);
                    value.forEach(edge -> service.getGraph().add(edge));
                });
                service.getGraph().repaint();
                graphData.clear();
            }
        });

        JButton saveButton = addButton(SAVE, "SaveButton");
        saveButton.setToolTipText("save graph data to file");
        saveButton.addActionListener(event -> {
            fileChooser.setDialogTitle("Save graph data file");
            int returnValue = fileChooser.showSaveDialog(service.getGraph());
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                service.getVertices().forEach(vertex -> graphData.put(vertex, vertex.connectedEdges));
                Storage.serialize(graphData, String.valueOf(fileChooser.getSelectedFile()));
            }
        });

        JButton prevButton = addButton(PREV, "PrevButton");
        prevButton.setToolTipText("prev button - implementation soon");
        JButton nextButton = addButton(NEXT, "NextButton");
        nextButton.setToolTipText("next button - implementation soon");
        JButton undoButton = addButton(UNDO, "UndoButton");
        undoButton.setToolTipText("undo button - implementation soon");
        JButton redoButton = addButton(REDO, "RedoButton");
        redoButton.setToolTipText("redo button - implementation soon");
        JButton refreshButton = addButton(REFRESH, "RefreshButton");
        refreshButton.setToolTipText("refresh button - implementation soon");
        JButton closeButton = addButton(CLOSE, "CloseButton");
        closeButton.setToolTipText("close button - implementation soon");
        JButton infoButton = addButton(INFO, "InfoButton");
        infoButton.setToolTipText("info button - implementation soon");
        JButton chatButton = addButton(CHAT, "ChatButton");
        chatButton.setToolTipText("chat button - implementation soon");
    }

    private <T> JComboBox<T> addComboBox() {
        var comboBox = new JComboBox<T>();
        comboBox.setUI(new BasicComboBoxUI());
        comboBox.setBackground(Color.BLACK);
        comboBox.setForeground(Color.WHITE);
        comboBox.setFont(new Font("Courier", Font.ITALIC, 15));
        comboBox.setFocusable(false);
        return comboBox;
    }

    private JLabel addLabel(String mode) {
        var label = new JLabel(String.format("<html><font color=gray>%s", mode), SwingConstants.RIGHT);
        label.setBackground(Color.BLACK);
        label.setOpaque(true);
        return label;
    }

    private JButton addButton(Icon icon, String name) {
        var button = new JButton(icon);
        button.setUI(new BasicButtonUI());
        button.setPreferredSize(new Dimension(30, 30));
        button.setBackground(Color.BLACK);
        button.setForeground(Color.BLACK);
        button.setOpaque(true);
        button.setName(name);
        button.setIcon(icon);
        button.setFocusPainted(false);
        buttonsPanel.add(button);
        return button;
    }
}