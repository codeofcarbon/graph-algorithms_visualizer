package visualizer;

import javax.swing.*;
import java.awt.*;

public class Toolbar extends JPanel {
    JLabel algorithmModeLabel;
    JLabel modeLabel;
    JPanel buttonsPanel;
    JButton undoButton;
    JButton redoButton;
    JButton openButton;
    JButton saveButton;
    private final JFileChooser fileChooser;

    private static final ImageIcon OPEN = new ImageIcon(new ImageIcon("src/main/resources/icons/open.png")
            .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
    private static final ImageIcon SAVE = new ImageIcon(new ImageIcon("src/main/resources/icons/save.png")
            .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
    private static final ImageIcon UNDO = new ImageIcon(new ImageIcon("src/main/resources/icons/undo.png")
            .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
    private static final ImageIcon REDO = new ImageIcon(new ImageIcon("src/main/resources/icons/redo.png")
            .getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));

    public Toolbar(JFileChooser fileChooser) {
        setLayout(new GridLayout(1, 3));
        setBounds(0, 0, this.getWidth(), 50);
        this.fileChooser = fileChooser;
        algorithmModeLabel = addLabel("<html><font color=gray>ALGORITHM MODE - " +
                                      "<font size=+1 color=white><i>NONE</i>", "AlgorithmMode");
        add(algorithmModeLabel);
        buttonsPanel = new JPanel(new GridLayout(1, 4));
        add(buttonsPanel);
        modeLabel = addLabel("<html><font color=gray>GRAPH MODE - " +
                             "<font size=+1 color=white><i>ADD A VERTEX</i>", "Mode");
        add(modeLabel);
        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        openButton = addButton(OPEN, "OpenButton");
        openButton.addActionListener(event -> {
            fileChooser.setDialogTitle("Select graph data file");
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                Storage.deserialize(String.valueOf(fileChooser.getSelectedFile()));
            }
        });

        saveButton = addButton(SAVE,"SaveButton");
        saveButton.addActionListener(event -> {
            fileChooser.setDialogTitle("Save graph data file");
            int returnValue = fileChooser.showSaveDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                Storage.serialize(this, String.valueOf(fileChooser.getSelectedFile()));
            }
        });

        undoButton = addButton(UNDO,"UndoButton");
        undoButton.addActionListener(event -> {

        });

        redoButton = addButton(REDO,"RedoButton");
        redoButton.addActionListener(event -> {

        });
    }

    private JButton addButton(Icon icon, String name) {
        var button = new JButton(icon);
        button.setPreferredSize(new Dimension(40, 30));
        button.setName(name);
        button.setIcon(icon);
        button.setFocusPainted(false);
        button.setBackground(Color.DARK_GRAY.darker());
        buttonsPanel.add(button);
        return button;
    }

    private JLabel addLabel(String text, String name) {
        var label = new JLabel(text, SwingConstants.CENTER);
        label.setName(name);
        label.setBackground(Color.DARK_GRAY.darker());
        label.setOpaque(true);
        return label;
    }
}
