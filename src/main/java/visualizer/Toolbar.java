package visualizer;

import lombok.*;

import javax.swing.*;
import javax.swing.undo.*;
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
    private final JPanel toolsPanel, mainPanel;
    private final JLabel infoLabel;
    @Setter
    private GraphService service;

    public Toolbar() {
        this.fileChooser = new JFileChooser(new File("src/main/java/visualizer/data"));
        setPreferredSize(new Dimension(1000, 70));
        setSize(getPreferredSize());
        setBackground(Color.BLACK);
        setLayout(new BorderLayout());

        mainPanel = new JPanel(new GridLayout(1, 3));
        mainPanel.setPreferredSize(new Dimension(190, 70));
        mainPanel.setBackground(Color.BLACK);

        toolsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        toolsPanel.setPreferredSize(new Dimension(480, 40));
        toolsPanel.setBackground(new Color(0, 0, 0, 0));
        toolsPanel.setOpaque(false);
        //        toolsPanel.setBackground(Color.BLACK);
        //        toolsPanel.setOpaque(true);

        infoLabel = new JLabel("", SwingConstants.CENTER);
        infoLabel.setFont(new Font("Tempus Sans ITC", Font.PLAIN, 20));
        infoLabel.setPreferredSize(new Dimension(810, 30));
        //        infoLabel.setSize(infoLabel.getPreferredSize());
        //        infoLabel.setBackground(new Color(0, 0, 0, 0));
        infoLabel.setBackground(Color.DARK_GRAY.darker());
        infoLabel.setForeground(Color.WHITE);
        infoLabel.setOpaque(true);
        infoLabel.setVisible(false);
        //        var infoPanel = new JPanel(new BorderLayout());
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

        menuButton = new MenuButton("menu", "TOOLS", mainPanel, buttonsComboBox);
//        menuButton = new MenuButton("menu", "TOOLS", mainPanel, toolsPanel);
        graphModeButton = new MenuButton("graph", "GRAPH MODE", mainPanel, graphModeComboBox);
        algModeButton = new MenuButton("algorithm", "ALGORITHM MODE", mainPanel, algModeComboBox);

        add(toolsPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.EAST);
        add(infoLabel, BorderLayout.LINE_START);

        addListeners();
    }

    @SuppressWarnings("unchecked")
    private void addListeners() {
        algModeComboBox.addActionListener(event -> {
            service.setCurrentModes(Arrays.stream(AlgMode.values())
                    .filter(algMode -> algMode.current.equalsIgnoreCase((String) algModeComboBox.getSelectedItem()))
                    .findFirst().orElse(AlgMode.NONE), GraphMode.NONE);
            infoLabel.setText("Please choose a starting vertex");
            algModeComboBox.setSelectedIndex(Arrays.asList(AlgMode.values()).indexOf(service.getAlgorithmMode()));
        });

        graphModeComboBox.addActionListener(event -> {
            service.setCurrentModes(AlgMode.NONE, Arrays.stream(GraphMode.values())
                    .filter(graphMode -> graphMode.current.equalsIgnoreCase((String) graphModeComboBox.getSelectedItem()))
                    .findFirst().orElse(GraphMode.NONE));
            infoLabel.setText("");
            graphModeComboBox.setSelectedIndex(Arrays.asList(GraphMode.values()).indexOf(service.getGraphMode()));
        });

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
                service.getManager().undo();
            } catch (CannotUndoException e) {
                JOptionPane.showMessageDialog(
                        service.getGraph(), "Nothing else to undo", "Info", JOptionPane.WARNING_MESSAGE,
                        new ImageIcon(new ImageIcon("src/main/resources/icons/buttons/warn_dialog.png").getImage().
                                getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
            }
        });

        redoButton.addActionListener(event -> {
            try {
                service.getManager().redo();
            } catch (CannotRedoException e) {
                JOptionPane.showMessageDialog(
                        service.getGraph(), "Nothing else to redo", "Info", JOptionPane.WARNING_MESSAGE,
                        new ImageIcon(new ImageIcon("src/main/resources/icons/buttons/warn_dialog.png").getImage().
                                getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
            }
        });
    }
}