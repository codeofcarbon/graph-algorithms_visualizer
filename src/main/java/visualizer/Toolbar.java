package visualizer;

import lombok.*;
import visualizer.temp.PopdownButton;

import javax.swing.*;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class Toolbar extends JPanel {
    private static Map<Vertex, List<Edge>> graphData = new ConcurrentHashMap<>();
    private final JFileChooser fileChooser;
    private final JComboBox<String> algModeComboBox, graphModeComboBox, buttonsComboBox;
    private final MenuButton algModeButton, graphModeButton, menuButton;
    private final MenuButton openButton, saveButton, refreshButton, closeButton, undoButton, redoButton;
    private final MenuButton prevButton, nextButton, infoButton, messageButton, linkedButton, githubButton;
    private final JPanel toolsPanel, buttonsPanel;
    private final JLabel infoLabelTwo;
    @Setter
    private GraphService service;

    public Toolbar(UndoManager manager) {
        this.fileChooser = new JFileChooser(new File("src/main/java/visualizer/data"));
        setPreferredSize(new Dimension(1000, 70));
        setSize(getPreferredSize());
        setBackground(Color.BLACK);
        setLayout(new BorderLayout());
//        setOpaque(true);
//        setVisible(true);                                                   // todo remove?

        buttonsPanel = new JPanel(new GridLayout(1, 3));
        buttonsPanel.setPreferredSize(new Dimension(190, 70));
//        buttonsPanel.setSize(buttonsPanel.getPreferredSize());
        buttonsPanel.setBackground(Color.BLACK);

        toolsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        toolsPanel.setPreferredSize(new Dimension(480, 40));
//        toolsPanel.setSize(toolsPanel.getPreferredSize());
        toolsPanel.setBackground(Color.BLACK);
//        toolsPanel.setBackground(new Color(0, 0, 0, 0));
        toolsPanel.setOpaque(true);


//        var infoLabelOne = new JLabel("", SwingConstants.CENTER);
//        infoLabelOne.setPreferredSize(new Dimension(750, 30));
////        infoLabelOne.setPreferredSize(new Dimension(810, 30));
////        infoLabelOne.setSize(infoLabel.getPreferredSize());
////        infoLabelOne.setBackground(Color.DARK_GRAY.darker());
////        infoLabelOne.setBackground(new Color(0, 0, 0, 0));
//        infoLabelOne.setForeground(Color.WHITE);
//        infoLabelOne.setOpaque(false);
//        infoLabelOne.setVisible(false);

//        var infoPanel = new JPanel(new BorderLayout());
        infoLabelTwo = new JLabel("", SwingConstants.CENTER);
        infoLabelTwo.setPreferredSize(new Dimension(810, 30));
//        infoLabelTwo.setPreferredSize(new Dimension(810, 30));
//        infoLabelTwo.setSize(infoLabel.getPreferredSize());
//        infoLabelTwo.setBackground(Color.DARK_GRAY.darker());
//        infoLabelTwo.setBackground(new Color(0, 0, 0, 0));
        infoLabelTwo.setForeground(Color.WHITE);
//        infoLabelTwo.setOpaque(false);
//        infoLabelTwo.setOpaque(true);
        infoLabelTwo.setVisible(false);
//        infoPanel.add(infoLabel, BorderLayout.CENTER);

        graphModeComboBox = new ModeComboBox<>(GraphMode.values());
        algModeComboBox = new ModeComboBox<>(AlgMode.values());
        buttonsComboBox = new ModeComboBox<>(toolsPanel.getComponents());
        graphModeComboBox.setSelectedIndex(0);                          // graph mode: add a vertex
        algModeComboBox.setSelectedIndex(4);                            // algorithm mode: none

        openButton = new MenuButton("open", "LOAD GRAPH", toolsPanel, null);
        saveButton = new MenuButton("save", "SAVE GRAPH", toolsPanel, null);
        closeButton = new MenuButton("exit", "EXIT AN APP", toolsPanel, null);
        refreshButton = new MenuButton("new", "NEW GRAPH", toolsPanel, null);
        undoButton = new MenuButton("undo", "UNDO", toolsPanel, null);
        redoButton = new MenuButton("redo", "REDO", toolsPanel, null);
        prevButton = new MenuButton("prev", "PREV STEP", toolsPanel, null);
        nextButton = new MenuButton("next", "NEXT STEP", toolsPanel, null);
        infoButton = new MenuButton("info", "INFO", toolsPanel, null);
        messageButton = new MenuButton("message", "MESSAGE", toolsPanel, null);
        linkedButton = new MenuButton("linked", "CONTACT ME", toolsPanel, null);
        githubButton = new MenuButton("github", "CONTACT ME", toolsPanel, null);

        menuButton = new MenuButton("menu", "TOOLS", buttonsPanel, toolsPanel);
        graphModeButton = new MenuButton("graph", "GRAPH MODE", buttonsPanel, graphModeComboBox);
        algModeButton = new MenuButton("algorithm", "ALGORITHM MODE", buttonsPanel, algModeComboBox);

        add(toolsPanel, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.EAST);
//        add(infoLabelOne, BorderLayout.LINE_START);
        add(infoLabelTwo, BorderLayout.LINE_START);
//        add(graphModeComboBox);
//        add(algModeComboBox);
//        add(buttonsComboBox);

//        add(toolsPanel, BorderLayout.CENTER);
//        add(buttonsPanel, BorderLayout.EAST);
//        add(infoLabelTwo, BorderLayout.LINE_START);
//        graphModeButton.add(graphModeComboBox);
//        algModeButton.add(algModeComboBox);
//        menuButton.add(buttonsComboBox);

        addListeners(manager);
    }

    @SuppressWarnings("unchecked")
    private void addListeners(UndoManager manager) {
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

        openButton.addActionListener(event -> {
            fileChooser.setDialogTitle("Select graph data file");
            if (fileChooser.showOpenDialog(service.getGraph()) == JFileChooser.APPROVE_OPTION) {
                try (var inStream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(
                        String.valueOf(fileChooser.getSelectedFile()))))) {
                    graphData = (ConcurrentHashMap<Vertex, List<Edge>>) inStream.readObject();

                    // todo resetting undo manager

                    service.clearGraph();
                    graphData.forEach((key, value) -> {
                        service.getMouseHandler().addComponent(key);
                        service.getNodes().add(key);
                        service.getEdges().addAll(value);
                        service.getGraph().add(key);
                        value.forEach(edge -> service.getGraph().add(edge));
                    });
                    graphData.clear();
                    service.getGraph().repaint();
                } catch (IOException | ClassNotFoundException e) {
                    System.err.println("Graph loading error: " + e.getMessage());
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
                    System.err.println("Graph saving error: " + e.getMessage());
                }
            }
        });

        closeButton.addActionListener(event -> {
            var exitDialogButton = new JButton("Exit");
            exitDialogButton.setFocusPainted(false);
            var confirm = JOptionPane.showOptionDialog(service.getGraph(), "Are you sure you want to exit?",
                    "Exit an app", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                    new ImageIcon(new ImageIcon("src/main/resources/icons/buttons/exit_dialog.png").getImage().
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
                    "Reset a graph", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                    new ImageIcon(new ImageIcon("src/main/resources/icons/buttons/new_dialog.png").getImage().
                            getScaledInstance(30, 30, Image.SCALE_SMOOTH)),
                    new Object[]{clearDialogButton.getText(), "Cancel"}, clearDialogButton);
            if (confirm == JFileChooser.APPROVE_OPTION) {
                service.clearGraph();
            }
        });

        undoButton.addActionListener(event -> {
            try {
                manager.undo();
            } catch (CannotUndoException e) {
                JOptionPane.showMessageDialog(
                        service.getGraph(), "Nothing else to undo", "Info", JOptionPane.WARNING_MESSAGE,
                        new ImageIcon(new ImageIcon("src/main/resources/icons/buttons/warn_dialog.png").getImage().
                                getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
            }
        });

        redoButton.addActionListener(event -> {
            try {
                manager.redo();
            } catch (CannotRedoException e) {
                JOptionPane.showMessageDialog(
                        service.getGraph(), "Nothing else to redo", "Info", JOptionPane.WARNING_MESSAGE,
                        new ImageIcon(new ImageIcon("src/main/resources/icons/buttons/warn_dialog.png").getImage().
                                getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
            }
        });
    }
}