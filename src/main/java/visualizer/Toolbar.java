package visualizer;

import lombok.*;

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
    private final JComboBox<String> algModeComboBox, graphModeComboBox;
    private final ToolButton openButton, saveButton, refreshButton, closeButton, undoButton, redoButton;
    private final ToolButton prevButton, nextButton, infoButton, messageButton;//, linkedButton, githubButton;
    private final ModeButton algModeButton, graphModeButton;
    private final JPanel buttonsPanel;
    private final JPanel infoPanel;
    private final JLabel infoLabel;
    @Setter
    private GraphService service;

    private JPanel addNewPanel(LayoutManager layout, JComponent parent) {
        var panel = new JPanel(layout);
//        panel.setPreferredSize(new Dimension(this.getWidth(), 50));
//        panel.setSize(panel.getPreferredSize());
        panel.setBackground(Color.BLACK);
        parent.add(panel);
        return panel;
    }

    private JLabel addNewLabel(String text, int alignment, int width) {
        var label = new JLabel(text, alignment);
        label.setPreferredSize(new Dimension(width, 50));
        label.setSize(label.getPreferredSize());
//        label.setBackground(new Color(10, 10, 10, 255));
        label.setBackground(Color.BLACK);
        label.setForeground(Color.WHITE);
        label.setOpaque(true);
        label.setVisible(true);
        return label;
    }

    public Toolbar(UndoManager manager) {
        this.fileChooser = new JFileChooser(new File("src/main/java/visualizer/data"));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.BLACK);
        setOpaque(true);

        var modePanel = addNewPanel(new GridLayout(1, 2), this);
        modePanel.setPreferredSize(new Dimension(984, 80));
        modePanel.setSize(modePanel.getPreferredSize());
        var graphModePanel = addNewPanel(new GridBagLayout(), modePanel);
        graphModePanel.setPreferredSize(new Dimension(modePanel.getWidth() / 2, 80));
        graphModePanel.setSize(graphModePanel.getPreferredSize());
        var algModePanel = addNewPanel(new GridBagLayout(), modePanel);
        algModePanel.setPreferredSize(new Dimension(modePanel.getWidth() / 2, 80));
        algModePanel.setSize(algModePanel.getPreferredSize());

        buttonsPanel = new JPanel(new GridLayout(1, 0));
        buttonsPanel.setPreferredSize(new Dimension(984, 50));
        buttonsPanel.setSize(buttonsPanel.getPreferredSize());
        buttonsPanel.setBackground(Color.BLACK);
        add(buttonsPanel);

        infoPanel = addNewPanel(new BorderLayout(), this);
        infoLabel = addNewLabel("", SwingConstants.CENTER, this.getWidth());
        infoPanel.add(infoLabel, BorderLayout.CENTER);

        openButton = new ToolButton("open", "LOAD GRAPH", buttonsPanel);
        saveButton = new ToolButton("save", "SAVE GRAPH", buttonsPanel);
        closeButton = new ToolButton("exit", "EXIT AN APP", buttonsPanel);
        refreshButton = new ToolButton("new", "NEW GRAPH", buttonsPanel);
        undoButton = new ToolButton("undo", "UNDO", buttonsPanel);
        redoButton = new ToolButton("redo", "REDO", buttonsPanel);
        prevButton = new ToolButton("prev", "PREV STEP", buttonsPanel);
        nextButton = new ToolButton("next", "NEXT STEP", buttonsPanel);
        infoButton = new ToolButton("info", "INFO", buttonsPanel);
        messageButton = new ToolButton("message", "MESSAGE", buttonsPanel);
//        linkedButton = new ToolButton("linked", "CONTACT ME", buttonsPanel);
//        githubButton = new ToolButton("github", "CONTACT ME", buttonsPanel);

        var gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        var graphFillLabel = addNewLabel("temp label", SwingConstants.RIGHT, this.getWidth() / 2);
        var algFillLabel = addNewLabel("temp label2", SwingConstants.LEFT, this.getWidth() / 2);
        graphModeComboBox = new ModeComboBox<>(GraphMode.values());
        graphModeButton = new ModeButton("graph", "GRAPH MODE", graphModeComboBox);
        algModeComboBox = new ModeComboBox<>(AlgMode.values());
        algModeButton = new ModeButton("algorithm", "ALGORITHM MODE", algModeComboBox);
        graphModeComboBox.setSelectedIndex(0);                          // graph mode: add a vertex
        algModeComboBox.setSelectedIndex(4);                            // algorithm mode: none

        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        graphModePanel.add(graphFillLabel, gbc);
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.gridx = 1;
        gbc.gridy = 0;
        graphModePanel.add(graphModeComboBox, gbc);
        gbc.gridx = 2;
        gbc.gridy = 0;
        graphModePanel.add(graphModeButton, gbc);

        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.gridx = 0;
        gbc.gridy = 0;
        algModePanel.add(algModeButton, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        algModePanel.add(algModeComboBox, gbc);
        gbc.weightx = 1;
        gbc.gridx = 2;
        gbc.gridy = 0;
        algModePanel.add(algFillLabel, gbc);

        addListeners(manager);
    }

    @SuppressWarnings("unchecked")
    private void addListeners(UndoManager manager) {
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
                    new ImageIcon(new ImageIcon("src/main/resources/icons/buttons/reload_dialog.png").getImage().
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