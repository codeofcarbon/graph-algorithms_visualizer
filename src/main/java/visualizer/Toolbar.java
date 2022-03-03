package visualizer;

import lombok.Getter;

import javax.swing.*;
import javax.swing.plaf.basic.BasicPopupMenuUI;
import javax.swing.undo.*;
import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class Toolbar extends JPanel {
    private static Map<Node, List<Edge>> graphData = new ConcurrentHashMap<>();
    private final JFileChooser fileChooser;
    private final ToolButton openButton, saveButton, refreshButton, closeButton, undoButton, redoButton;
    private final ToolButton prevButton, nextButton;
    private final JLabel leftInfoLabel, rightInfoLabel, graphModeLabel, algorithmModeLabel;
    private final ButtonPanel buttonPanel;
    private final JPanel toolsPanel;
    private final GraphService service;

    public Toolbar(GraphService service) {
        this.service = service;
        this.fileChooser = new JFileChooser(new File("src/main/java/visualizer/data"));
        setPreferredSize(new Dimension(1000, 70));
        setMinimumSize(getPreferredSize());
        setSize(getPreferredSize());
        setBackground(Color.BLACK);
        setLayout(new GridBagLayout());

        toolsPanel = new JPanel() {
            final JPopupMenu popup = new JPopupMenu();

            @Override
            public JPopupMenu getComponentPopupMenu() {
                popup.setUI(new BasicPopupMenuUI());
                popup.setPopupSize(280, 40);
                Arrays.stream(toolsPanel.getComponents()).forEach(popup::add);
                popup.setLayout(new GridLayout(1, 0));
                popup.setBorder(BorderFactory.createEmptyBorder());
                popup.setOpaque(false);
                return popup;
            }
        };

        openButton = new ToolButton("open", "LOAD GRAPH", toolsPanel);
        saveButton = new ToolButton("save", "SAVE GRAPH", toolsPanel);
        undoButton = new ToolButton("undo", "UNDO", toolsPanel);
        redoButton = new ToolButton("redo", "REDO", toolsPanel);
        prevButton = new ToolButton("prev", "PREV STEP (soon)", toolsPanel);
        nextButton = new ToolButton("next", "NEXT STEP (soon)", toolsPanel);
        refreshButton = new ToolButton("new", "NEW GRAPH", toolsPanel);
        closeButton = new ToolButton("exit", "EXIT AN APP", toolsPanel);

        buttonPanel = new ButtonPanel(service, toolsPanel);

        leftInfoLabel = addNewLabel(SwingConstants.CENTER, new Dimension(260, 70), false);
        rightInfoLabel = addNewLabel(SwingConstants.CENTER, new Dimension(260, 70), false);
        graphModeLabel = addNewLabel(SwingConstants.TRAILING, new Dimension(140, 70), true);
        algorithmModeLabel = addNewLabel(SwingConstants.LEADING, new Dimension(140, 70), true);
        updateModeLabels("ADD A VERTEX", "NONE");

        var gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        add(leftInfoLabel, gbc);
        gbc.weightx = 0.0;
        add(graphModeLabel, gbc);
        add(buttonPanel, gbc);
        add(algorithmModeLabel, gbc);
        gbc.weightx = 1.0;
        add(rightInfoLabel, gbc);

        addListeners();
    }

    void updateModeLabels(String graphMode, String algMode) {
        var htmlStyle = "<html><font size=4 color=rgb(40,162,212)><b>%s MODE</b></font><br>" +
                        "<font size=3 color=rgb(204,204,204)>%s</font>";
        graphModeLabel.setText(String.format(htmlStyle, "GRAPH", graphMode));
        algorithmModeLabel.setText(String.format(htmlStyle, "ALGORITHM", algMode));
    }

    private JLabel addNewLabel(int alignment, Dimension dimension, boolean htmlStyle) {
        var label = new JLabel("", alignment);
        label.setFont(htmlStyle ? new Font("Tempus Sans ITC", Font.PLAIN, 15)
                : new Font("Tempus Sans ITC", Font.PLAIN, 20));
        label.setPreferredSize(dimension);
        label.setMinimumSize(dimension);
        label.setSize(label.getPreferredSize());
        label.setForeground(Color.WHITE);
        label.setBackground(Color.BLACK);
        label.setOpaque(true);
        return label;
    }

    @SuppressWarnings("unchecked")
    private void addListeners() {                                      
        openButton.addActionListener(event -> {
            toolsPanel.getComponentPopupMenu().setVisible(false);
            fileChooser.setDialogTitle("Select graph data file");
            if (fileChooser.showOpenDialog(service.getGraph()) == JFileChooser.APPROVE_OPTION) {
                try (var inStream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(
                        String.valueOf(fileChooser.getSelectedFile()))))) {
                    graphData = (ConcurrentHashMap<Node, List<Edge>>) inStream.readObject();
                    service.clearGraph();
                    graphData.forEach((node, nodeEdges) -> {
                        service.getMouseHandler().addComponent(node);
                        service.getGraph().add(node);
                        service.getNodes().add(node);
                        nodeEdges.forEach(edge -> service.getGraph().add(edge));
                    });
                    graphData.clear();
                    service.getGraph().repaint();
                } catch (IOException | ClassNotFoundException e) {
                    System.err.println("Graph loading error: " + e.getMessage());
                }
            }
        });
        saveButton.addActionListener(event -> {
            toolsPanel.getComponentPopupMenu().setVisible(false);
            fileChooser.setDialogTitle("Save graph data file");
            if (fileChooser.showSaveDialog(service.getGraph()) == JFileChooser.APPROVE_OPTION) {
                try (var outStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(
                        String.valueOf(fileChooser.getSelectedFile()))))) {
                    service.getNodes().forEach(node -> graphData.put(node, node.connectedEdges));
                    outStream.writeObject(graphData);
                } catch (IOException e) {
                    System.err.println("Graph saving error: " + e.getMessage());
                }
            }
        });
        closeButton.addActionListener(event -> {
            toolsPanel.getComponentPopupMenu().setVisible(false);
            var exitDialogButton = new JButton("Exit");
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
            toolsPanel.getComponentPopupMenu().setVisible(false);
            var clearDialogButton = new JButton("Start new graph");
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
                service.resetComponentLists();
            } catch (CannotUndoException e) {
                toolsPanel.getComponentPopupMenu().setVisible(false);
                JOptionPane.showMessageDialog(                                         
                        service.getGraph(), "Nothing else to undo", "Info", JOptionPane.WARNING_MESSAGE,
                        new ImageIcon(new ImageIcon("src/main/resources/icons/buttons/warn_dialog.png").getImage().
                                getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
            }
        });
        redoButton.addActionListener(event -> {
            try {
                service.getManager().redo();
                service.resetComponentLists();
            } catch (CannotRedoException e) {
                toolsPanel.getComponentPopupMenu().setVisible(false);
                JOptionPane.showMessageDialog(                                   
                        service.getGraph(), "Nothing else to redo", "Info", JOptionPane.WARNING_MESSAGE,
                        new ImageIcon(new ImageIcon("src/main/resources/icons/buttons/warn_dialog.png").getImage().
                                getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
            }
        });
    }
}