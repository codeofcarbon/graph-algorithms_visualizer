package com.codeofcarbon.visualizer;

import lombok.Getter;

import javax.swing.*;
import javax.swing.plaf.basic.*;
import java.awt.*;
import java.util.Arrays;

@Getter
public class ModeList<T> extends JComboBox<String> {
    private final Class<?> clazz;

    public ModeList(T[] array, GraphService service, int alignment) {
        setRenderer(new CellRenderer<>());
        ((CellRenderer<?>) getRenderer()).setHorizontalAlignment(alignment);
        clazz = array[0] instanceof GraphMode ? GraphMode.class : AlgMode.class;
        Arrays.stream(array).forEach(mode -> addItem(clazz.equals(GraphMode.class)
                ? ((GraphMode) mode).current.toUpperCase()
                : ((AlgMode) mode).current.toUpperCase()));
        setSelectedIndex(clazz.equals(GraphMode.class) ? 0 /* graph - add new node */ : 4 /* algorithm - none */);

        addActionListener(event -> {
            var algorithmMode = clazz.equals(GraphMode.class) ? AlgMode.NONE : Arrays.stream(AlgMode.values())
                    .filter(algMode -> algMode.current.equalsIgnoreCase((String) getSelectedItem()))
                    .findFirst().orElse(AlgMode.NONE);
            var graphMode = clazz.equals(GraphMode.class) ? Arrays.stream(GraphMode.values())
                    .filter(gMode -> gMode.current.equalsIgnoreCase((String) getSelectedItem()))
                    .findFirst().orElse(GraphMode.NONE) : GraphMode.NONE;
            service.setCurrentModes(algorithmMode, graphMode);
        });
    }

    @Override
    public JPopupMenu getComponentPopupMenu() {
        var popup = (JPopupMenu) getAccessibleContext().getAccessibleChild(0);
        popup.setUI(new BasicPopupMenuUI());
        popup.setPopupSize(200, getItemCount() * 14);
        popup.setBorder(BorderFactory.createEmptyBorder());
        return popup;
    }
}

class CellRenderer<T> extends JLabel implements ListCellRenderer<T> {

    @Override
    public Component getListCellRendererComponent(JList<? extends T> list, T value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        setText(value.toString());
        setPreferredSize(new Dimension(200, 14));
        setSize(getPreferredSize());
        setForeground(isSelected ? new Color(40, 162, 212, 255) : new Color(204, 204, 204, 255));
        setFont(new Font("Stylus BT", Font.PLAIN, isSelected ? 17 : 15));
        setBorder(BorderFactory.createEmptyBorder());
        setBackground(Color.BLACK);
        setOpaque(true);
        return this;
    }
}

enum GraphMode {
    ADD_NODE("Add Node"),
    ADD_AN_EDGE("Add an Edge"),
    REMOVE_NODE("Remove Node"),
    REMOVE_AN_EDGE("Remove an Edge"),
    NONE("None");

    final String current;

    GraphMode(String current) {
        this.current = current;
    }
}

enum AlgMode {
    DEPTH_FIRST_SEARCH("Depth-First Search"),
    BREADTH_FIRST_SEARCH("Breadth-First Search"),
    DIJKSTRA_ALGORITHM("Dijkstra's Algorithm"),
    PRIM_ALGORITHM("Prim's Algorithm"),
    NONE("None");

    final String current;

    AlgMode(String current) {
        this.current = current;
    }
}
