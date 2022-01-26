package visualizer;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.util.Arrays;

public class ModeComboBox<T> extends JComboBox<String> {

    public ModeComboBox(T[] array) {
        Arrays.stream(array)
                .forEach(mode -> {
            if (mode instanceof AlgMode) addItem(((AlgMode) mode).current.toUpperCase());
            if (mode instanceof GraphMode) addItem(((GraphMode) mode).current.toUpperCase());
        });
        setUI(new BasicComboBoxUI() {
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
        remove(getComponent(0));                                        // removing an arrowButton
        setBackground(Color.BLACK);
        setForeground(new Color(40, 162, 212, 255));
        setFont(new Font("Tahoma", Font.PLAIN, 15));
        setRenderer(new CellRenderer<>());
        ((CellRenderer<?>) getRenderer()).setHorizontalAlignment(
                array[0] instanceof GraphMode ? SwingConstants.RIGHT : SwingConstants.LEFT);
        setOpaque(true);
        setFocusable(false);
        setVisible(true);                                        // todo in progress
    }
}

class CellRenderer<T> extends JLabel implements ListCellRenderer<T> {
    @Override
    public Component getListCellRendererComponent(JList<? extends T> list, T value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        setText(value.toString());
        setPreferredSize(new Dimension(180, 20));
        setSize(getPreferredSize());
        setBackground(Color.BLACK);
        setForeground(isSelected ? new Color(40, 162, 212, 255) : Color.LIGHT_GRAY.darker());
        setBorder(null);
        setOpaque(true);
        return this;
    }
}