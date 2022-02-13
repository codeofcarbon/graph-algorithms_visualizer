package visualizer;

import lombok.Getter;

import javax.swing.*;
import javax.swing.undo.*;
import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter                    // todo 5-6 getters needed for: buttons(refresh and close), comboBoxes, and infoLabel(s)
public class Toolbar extends JPanel {
    private static Map<Vertex, List<Edge>> graphData = new ConcurrentHashMap<>();
    private final JFileChooser fileChooser;
    private final JComboBox<String> algModeComboBox, graphModeComboBox;
    private final MenuButton algModeButton, graphModeButton, menuButton;
    private final MenuButton openButton, saveButton, refreshButton, closeButton, undoButton, redoButton;
    private final MenuButton prevButton, nextButton, infoButton, messageButton, linkedButton, githubButton;
    private final JPanel toolsPanel, buttonsPanel;
    private final JLabel leftInfoLabel, rightInfoLabel;
    private final GraphService service;

    public Toolbar(GraphService service) {
        this.service = service;
        this.fileChooser = new JFileChooser(new File("src/main/java/visualizer/data"));
        setPreferredSize(new Dimension(1000, 70));
        setMinimumSize(getPreferredSize());
        setSize(getPreferredSize());
        setBackground(Color.BLACK);
        setLayout(new GridBagLayout());

        leftInfoLabel = addNewLabel("", SwingConstants.CENTER, new Dimension(260, 70),
                new Font("Tempus Sans ITC", Font.PLAIN, 20));
        var graphModeLabel = addNewLabel("<html><font size=4 color=rgb(40,162,212)>" +
                                         "<b>GRAPH MODE</b></font><br>" +
                                         "<font font-family=tahoma size=3 color=rgb(204,204,204)>" +
                                         "REMOVE A VERTEX</font>",
                SwingConstants.TRAILING, new Dimension(140, 70), null);
//        graphModeLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
//        graphModeLabel.setAlignmentX(RIGHT_ALIGNMENT);

        buttonsPanel = new JPanel(new GridLayout(1, 0, 0, 0));
        buttonsPanel.setPreferredSize(new Dimension(200, 70));
        buttonsPanel.setMinimumSize(buttonsPanel.getPreferredSize());
        buttonsPanel.setSize(buttonsPanel.getPreferredSize());
        buttonsPanel.setOpaque(false);

        rightInfoLabel = addNewLabel("", SwingConstants.CENTER, new Dimension(260, 70),
                new Font("Tempus Sans ITC", Font.PLAIN, 20));
        var algorithmModeLabel = addNewLabel("<html><font size=4 color=rgb(40,162,212)>" +
                                             "<b>ALGORITHM MODE</b></font><br>" +
                                             "<font font-family=tahoma size=3 color=rgb(204,204,204)>" +
                                             "BREADTH-FIRST SEARCH</font>",
                SwingConstants.LEADING, new Dimension(140, 70), null);

        var gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        add(leftInfoLabel, gbc);
        gbc.weightx = 0.0;
        add(graphModeLabel, gbc);
        add(buttonsPanel, gbc);
        add(algorithmModeLabel, gbc);
        gbc.weightx = 1.0;
        add(rightInfoLabel, gbc);


        toolsPanel = new JPanel() {                                 // todo is any way to rid off of that JPanel?
            final JPopupMenu popup = new JPopupMenu();

            @Override
            public JPopupMenu getComponentPopupMenu() {
                popup.setUI(new javax.swing.plaf.basic.BasicPopupMenuUI());
                popup.setPopupSize(420, 40);
                Arrays.stream(toolsPanel.getComponents()).forEach(popup::add);
                popup.setLayout(new FlowLayout(FlowLayout.CENTER));
                popup.setBorder(BorderFactory.createEmptyBorder());
                popup.setOpaque(false);
                return popup;
            }
        };
        toolsPanel.setPreferredSize(new Dimension(420, 40));        // todo setSize only? or do i need that at least?
        toolsPanel.setSize(toolsPanel.getPreferredSize());

        graphModeComboBox = new ModeComboBox<>(GraphMode.values(), service, SwingConstants.RIGHT);
        algModeComboBox = new ModeComboBox<>(AlgMode.values(), service, SwingConstants.LEFT);

        openButton = new MenuButton("open", "LOAD GRAPH", toolsPanel, null, service.getMouseHandler());
        saveButton = new MenuButton("save", "SAVE GRAPH", toolsPanel, null, service.getMouseHandler());
        closeButton = new MenuButton("exit", "EXIT AN APP", toolsPanel, null, service.getMouseHandler());
        refreshButton = new MenuButton("new", "NEW GRAPH", toolsPanel, null, service.getMouseHandler());
        undoButton = new MenuButton("undo", "UNDO", toolsPanel, null, service.getMouseHandler());
        redoButton = new MenuButton("redo", "REDO", toolsPanel, null, service.getMouseHandler());
        prevButton = new MenuButton("prev", "PREV STEP", toolsPanel, null, service.getMouseHandler());
        nextButton = new MenuButton("next", "NEXT STEP", toolsPanel, null, service.getMouseHandler());
        infoButton = new MenuButton("info", "INFO", toolsPanel, null, service.getMouseHandler());
        messageButton = new MenuButton("message", "MESSAGE", toolsPanel, null, service.getMouseHandler());
        linkedButton = new MenuButton("linked", "CONTACT ME", toolsPanel, null, service.getMouseHandler());
        githubButton = new MenuButton("github", "CONTACT ME", toolsPanel, null, service.getMouseHandler());

        graphModeButton = new MenuButton("graph", "GRAPH MODE", buttonsPanel, graphModeComboBox, service.getMouseHandler());
        menuButton = new MenuButton("menu", "TOOLS", buttonsPanel, toolsPanel, service.getMouseHandler());
        algModeButton = new MenuButton("algorithm", "ALGORITHM MODE", buttonsPanel, algModeComboBox, service.getMouseHandler());

        addListeners();
    }

    private JLabel addNewLabel(String text, int alignment, Dimension dimension, Font font) {
        var label = new JLabel(text, alignment);
        label.setPreferredSize(dimension);
        label.setMinimumSize(dimension);
        label.setSize(label.getPreferredSize());
        if (font != null) {
            label.setFont(font);
            label.setForeground(Color.WHITE);
//            label.setBackground(Color.DARK_GRAY.darker());
            label.setBackground(Color.BLACK);
        } else label.setBackground(Color.BLACK);
        label.setOpaque(true);
//        label.setVisible(true);                      // todo check when needed and implement
        return label;
    }

    @SuppressWarnings("unchecked")
    private void addListeners() {            // todo action listenery wyslac do produkcji w klasie menu button?????
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