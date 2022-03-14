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
    private final ToolButton openButton, saveButton, refreshButton, closeButton;
    private final ToolButton undoButton, redoButton, prevButton, nextButton;
    private final JPanel toolsPanel, rightInfoPanel/*, leftInfoLabel*/;
    private final JLabel leftInfoLabel, graphModeLabel, algorithmModeLabel;
    private final ButtonPanel buttonPanel;
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

        var leftInfoPanel = addNewPanel(new Dimension(260, 70));
        rightInfoPanel = addNewPanel(new Dimension(260, 70));
        graphModeLabel = addNewLabel(SwingConstants.TRAILING, new Dimension(120, 70));
        algorithmModeLabel = addNewLabel(SwingConstants.LEADING, new Dimension(160, 70));
        updateModeLabels("ADD A NODE", "NONE");

        leftInfoLabel = addNewLabel(SwingConstants.TRAILING, new Dimension(260, 70));
        leftInfoPanel.add(leftInfoLabel, BorderLayout.NORTH);

        var gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        add(leftInfoPanel, gbc);
        gbc.weightx = 0.0;
        add(graphModeLabel, gbc);
        add(buttonPanel, gbc);
        add(algorithmModeLabel, gbc);
        gbc.weightx = 1.0;
        add(rightInfoPanel, gbc);

        addListeners();
    }

    void updateModeLabels(String graphMode, String algMode) {
        var htmlStyle = "<html><div align=%s><font color=rgb(40,162,212)><b>%s MODE</b></font><br>" +
                        "<font size=4 color=rgb(204,204,204)>%s</font>";
        graphModeLabel.setText(String.format(htmlStyle, "right", "GRAPH", graphMode));
        algorithmModeLabel.setText(String.format(htmlStyle, "left", "ALGORITHM", algMode));
    }

    private JPanel addNewPanel(Dimension dimension) {
        var panel = new JPanel();
        panel.setFont(new Font("Tempus Sans ITC", Font.PLAIN, 20));
        panel.setPreferredSize(dimension);
        panel.setMinimumSize(dimension);
        panel.setSize(panel.getPreferredSize());
        panel.setForeground(Color.WHITE);
        panel.setBackground(Color.BLACK);
        panel.setOpaque(true);
        return panel;
    }

    private JLabel addNewLabel(int alignment, Dimension dimension) {
        var label = new JLabel("", alignment);
        label.setFont(new Font("Stylus BT", Font.PLAIN, 15));
        label.setPreferredSize(dimension);
        label.setMinimumSize(dimension);
        label.setSize(label.getPreferredSize());
        label.setForeground(Color.WHITE);
        label.setBackground(Color.BLACK);
        label.setOpaque(true);
        return label;
    }

    private static ImageIcon loadIcon(String iconFilename) {
        return new ImageIcon(new ImageIcon(String.format("src/main/resources/icons/buttons/%s.png", iconFilename))
                .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
    }

    @SuppressWarnings("unchecked")
    private void addListeners() {
        openButton.addActionListener(event -> {
            toolsPanel.getComponentPopupMenu().setVisible(false);
            fileChooser.setDialogTitle("Open graph data file");
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
                    service.getNodes().forEach(node -> graphData.put(node, node.getConnectedEdges()));
                    outStream.writeObject(graphData);
                } catch (IOException e) {
                    System.err.println("Graph saving error: " + e.getMessage());
                }
            }
        });

        closeButton.addActionListener(event -> {
            toolsPanel.getComponentPopupMenu().setVisible(false);
            var confirm = JOptionPane.showOptionDialog(service.getGraph(), "Are you sure you want to exit?",
                    "Exit an app", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                    loadIcon("exit_dialog"), new Object[]{"Exit", "Cancel"}, null);
            if (confirm == JFileChooser.APPROVE_OPTION) {
                System.exit(0);
            }
        });

        refreshButton.addActionListener(event -> {
            toolsPanel.getComponentPopupMenu().setVisible(false);
            var confirm = JOptionPane.showOptionDialog(service.getGraph(), "Clear the board and start new graph?",
                    "Reset a graph", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                    loadIcon("new_dialog"), new Object[]{"New graph", "Cancel"}, null);
            if (confirm == JFileChooser.APPROVE_OPTION) {
                service.clearGraph();
            }
        });

        undoButton.addActionListener(event -> {
            try {
                service.getManager().undo();
                service.resetComponentsLists();
            } catch (CannotUndoException e) {
                toolsPanel.getComponentPopupMenu().setVisible(false);
                JOptionPane.showMessageDialog(service.getGraph(), "Nothing else to undo", "Info",
                        JOptionPane.WARNING_MESSAGE, loadIcon("warn_dialog"));
            }
        });

        redoButton.addActionListener(event -> {
            try {
                service.getManager().redo();
                service.resetComponentsLists();
            } catch (CannotRedoException e) {
                toolsPanel.getComponentPopupMenu().setVisible(false);
                JOptionPane.showMessageDialog(service.getGraph(), "Nothing else to redo", "Info",
                        JOptionPane.WARNING_MESSAGE, loadIcon("warn_dialog"));
            }
        });
    }
}
