package visualizer;

import lombok.*;

import javax.swing.*;
import javax.swing.plaf.basic.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class Toolbar extends JPanel {
    private static Map<Vertex, List<Edge>> graphData = new ConcurrentHashMap<>();
    private final JFileChooser fileChooser;
    private final JComboBox<String> algModeComboBox;
    private final JComboBox<String> graphModeComboBox;
    private final JPanel buttonsPanel;
    private final JButton openButton;
    private final JButton saveButton;
    private final JButton refreshButton;
    private final JButton closeButton;
    private final JButton algModeButton;
    private final JButton graphModeButton;
    private final JLabel infoLabelTwo;
    @Setter
    private GraphService service;

    // todo =========================================================
    public Toolbar(JFileChooser fileChooser) {
        setLayout(new GridLayout(3, 1));
        setPreferredSize(new Dimension(1000, 100));
        setSize(getPreferredSize());
        setBackground(Color.BLACK);
        this.fileChooser = fileChooser;

        buttonsPanel = new JPanel(new GridLayout(1, 10));
        buttonsPanel.setPreferredSize(new Dimension(1000, 30));
        buttonsPanel.setSize(buttonsPanel.getPreferredSize());
        buttonsPanel.setBackground(Color.BLACK);

        buttonsPanel.add(openButton = addButton("open", "LOAD GRAPH"));
        buttonsPanel.add(saveButton = addButton("save", "SAVE GRAPH"));
        buttonsPanel.add(closeButton = addButton("exit", "EXIT AN APP"));
        buttonsPanel.add(refreshButton = addButton("new", "NEW GRAPH"));
        // todo =========================================================
        buttonsPanel.add(addButton("prev", "PREV STEP"));
        buttonsPanel.add(addButton("next", "NEXT STEP"));
        // todo =========================================================
        buttonsPanel.add(addButton("undo", "UNDO"));
        buttonsPanel.add(addButton("redo", "REDO"));
        // todo =========================================================
        buttonsPanel.add(addButton("info", "INFO"));
        buttonsPanel.add(addButton("message", "MESSAGE"));
        buttonsPanel.add(addButton("linked", "CONTACT"));

        add(buttonsPanel);

        JPanel modePanelOne = new JPanel(new GridLayout(1, 4));
        modePanelOne.setPreferredSize(new Dimension(1000, 50));
        modePanelOne.setSize(modePanelOne.getPreferredSize());
        modePanelOne.setBackground(Color.BLACK);
        modePanelOne.setOpaque(true);

        // todo =========================================================
        JLabel infoLabelOne = new JLabel("", SwingConstants.CENTER);
        infoLabelOne.setBackground(new Color(12, 12, 12, 255));
        infoLabelOne.setForeground(Color.WHITE);
        infoLabelOne.setOpaque(true);

        JLabel emptyLabelOne = new JLabel("", SwingConstants.CENTER);
        emptyLabelOne.setBackground(new Color(12, 12, 12, 255));
        emptyLabelOne.setForeground(Color.WHITE);
        emptyLabelOne.setOpaque(true);

        algModeComboBox = addComboBox(AlgMode.values());
        algModeComboBox.setSelectedIndex(4);

        algModeButton = addButton("algorithm", "ALGORITHM MODE");
        algModeButton.setPreferredSize(new Dimension(50, 50));
        algModeButton.setSize(algModeButton.getPreferredSize());
        algModeButton.addActionListener(event -> algModeComboBox.showPopup());

        modePanelOne.add(infoLabelOne);
        modePanelOne.add(emptyLabelOne);
        modePanelOne.add(algModeComboBox);
        modePanelOne.add(algModeButton);
        add(modePanelOne);

        JPanel modePanelTwo = new JPanel(new GridLayout(1, 4));
        modePanelTwo.setPreferredSize(new Dimension(1000, 50));
        modePanelTwo.setSize(modePanelTwo.getPreferredSize());
        modePanelTwo.setBackground(Color.BLACK);
        modePanelTwo.setOpaque(true);

        // todo =========================================================
        infoLabelTwo = new JLabel("", SwingConstants.CENTER);
        infoLabelTwo.setBackground(new Color(12, 12, 12, 255));
        infoLabelTwo.setForeground(Color.WHITE);
        infoLabelTwo.setOpaque(true);

        JLabel emptyLabelTwo = new JLabel("", SwingConstants.CENTER);
        emptyLabelTwo.setBackground(new Color(12, 12, 12, 255));
        emptyLabelTwo.setForeground(Color.WHITE);
        emptyLabelTwo.setOpaque(true);

        graphModeComboBox = addComboBox(GraphMode.values());
        graphModeComboBox.setSelectedIndex(0);

        graphModeButton = addButton("graph", "GRAPH MODE");
        graphModeButton.setPreferredSize(new Dimension(50, 50));
        graphModeButton.setSize(graphModeButton.getPreferredSize());
        graphModeButton.addActionListener(event -> graphModeComboBox.showPopup());

        modePanelTwo.add(infoLabelTwo);
        modePanelTwo.add(emptyLabelTwo);
        modePanelTwo.add(graphModeComboBox);
        modePanelTwo.add(graphModeButton);
        add(modePanelTwo);

        addListeners();
    }

    @SuppressWarnings("unchecked")
    private void addListeners() {
        // todo =========================================================
        algModeComboBox.addActionListener(event -> {
            service.setCurrentModes(Arrays.stream(AlgMode.values())
                    .filter(algMode -> algMode.current.equalsIgnoreCase((String) algModeComboBox.getSelectedItem()))
                    .findFirst().orElse(AlgMode.NONE), GraphMode.NONE);
            infoLabelTwo.setText("Please choose a starting vertex");
            algModeComboBox.setSelectedIndex(Arrays.asList(AlgMode.values()).indexOf(service.getAlgorithmMode()));
        });

        // todo =========================================================
        graphModeComboBox.addActionListener(event -> {
            service.setCurrentModes(AlgMode.NONE, Arrays.stream(GraphMode.values())
                    .filter(graphMode -> graphMode.current.equalsIgnoreCase((String) graphModeComboBox.getSelectedItem()))
                    .findFirst().orElse(GraphMode.NONE));
            infoLabelTwo.setText("");
            graphModeComboBox.setSelectedIndex(Arrays.asList(GraphMode.values()).indexOf(service.getGraphMode()));
        });

        // todo =========================================================
        openButton.addActionListener(event -> {
            fileChooser.setDialogTitle("Select graph data file");
            if (fileChooser.showOpenDialog(service.getGraph()) == JFileChooser.APPROVE_OPTION) {
                try (var inStream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(
                        String.valueOf(fileChooser.getSelectedFile()))))) {
                    graphData = (ConcurrentHashMap<Vertex, List<Edge>>) inStream.readObject();
                    service.clearGraph();
                    graphData.forEach((key, value) -> {
                        service.getMouseHandler().addComponent(key);
                        service.getNodes().add(key);
                        service.getEdges().addAll(value);
                        service.getGraph().add(key);
                        value.forEach(edge -> service.getGraph().add(edge));
                    });
                    service.getGraph().repaint();
                    graphData.clear();
                } catch (IOException | ClassNotFoundException e) {
                    System.err.println("Graph loading error" + e.getMessage());
                }
            }
        });

        saveButton.addActionListener(event -> {
            fileChooser.setDialogTitle("Save graph data file");
            if (fileChooser.showSaveDialog(service.getGraph()) == JFileChooser.APPROVE_OPTION) {
                try (var outStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(
                        String.valueOf(fileChooser.getSelectedFile()))))) {
                    service.getNodes().forEach(vertex -> graphData.put(vertex, vertex.connectedEdges));
                    outStream.writeObject(graphData);
                } catch (IOException e) {
                    System.err.println("Graph saving error" + e.getMessage());
                }
            }
        });

        closeButton.addActionListener(event -> {
            var exitDialogButton = new JButton("Exit");
            exitDialogButton.setFocusPainted(false);
            var confirm = JOptionPane.showOptionDialog(service.getGraph(), "Are you sure you want to exit?",
                    "Exit an app", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                    new ImageIcon(new ImageIcon("src/main/resources/icons/exit_dialog.png").getImage().
                            getScaledInstance(30, 30, Image.SCALE_SMOOTH)),
                    new Object[]{exitDialogButton.getText(), "Cancel"}, exitDialogButton);
            if (confirm == JFileChooser.APPROVE_OPTION) {
                System.exit(0);
            }
        });

        refreshButton.addActionListener(event -> {
            var clearDialogButton = new JButton("Start new graph");
            clearDialogButton.setFocusable(false);
            var confirm = JOptionPane.showOptionDialog(service.getGraph(), "Clear the board and start new graph?",
                    "Graph reset", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                    new ImageIcon(new ImageIcon("src/main/resources/icons/new_dialog.png").getImage().
                            getScaledInstance(30, 30, Image.SCALE_SMOOTH)),
                    new Object[]{clearDialogButton.getText(), "Cancel"}, clearDialogButton);
// todo =========================================================
            if (confirm == JFileChooser.APPROVE_OPTION) {
                service.clearGraph();
            }
        });
    }

    private <T> JComboBox<String> addComboBox(T[] array) {
        var comboBox = new JComboBox<String>();
        Arrays.stream(array).forEach(mode -> {
            if (mode instanceof AlgMode) comboBox.addItem(((AlgMode) mode).current.toUpperCase());
            if (mode instanceof GraphMode) comboBox.addItem(((GraphMode) mode).current.toUpperCase());
        });
        comboBox.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                return new JButton() {
                    @Override
                    public int getWidth() {
                        return 0;
                    }
                };
            }
        });
        comboBox.remove(comboBox.getComponent(0)); // removing an arrowButton
        comboBox.setBackground(Color.BLACK);
        comboBox.setForeground(Color.LIGHT_GRAY.darker());
        comboBox.setOpaque(true);
        comboBox.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 15));
        comboBox.setFocusable(false);
        comboBox.setRenderer(new CellRenderer<>());
        return comboBox;
    }

    private JButton addButton(String iconFilename, String toolTipText) {
        var button = new JButton() {
            @Override
            public JToolTip createToolTip() {
                var tip = new JToolTip();
                tip.setBackground(new Color(0, 0, 0, 0));
//                                  tip.setForeground(Color.CYAN);
                tip.setBorder(null);
                tip.setOpaque(false);
                return tip;
            }

            @Override
            public Point getToolTipLocation(MouseEvent e) {
                Point point = e.getPoint();
                point.translate(-10, 20);
                return point;
            }
        };
        button.setUI(new BasicButtonUI());
        button.setPreferredSize(new Dimension(30, 30));
        button.setSize(button.getPreferredSize());
        button.setBackground(Color.BLACK);
        button.setForeground(Color.BLACK);
        button.setIcon(new ImageIcon(new ImageIcon(String.format("src/main/resources/icons/%s.png", iconFilename))
                .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
        button.setToolTipText(String.format("<html><font color=gray>%s", toolTipText));
        return button;
    }
}