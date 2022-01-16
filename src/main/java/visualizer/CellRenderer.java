package visualizer;

import javax.swing.*;
import java.awt.*;

class CellRenderer<T> extends JLabel implements ListCellRenderer<T> {

    public Component getListCellRendererComponent(JList<? extends T> list, T value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        setText(value.toString());
        setHorizontalAlignment(SwingConstants.RIGHT);
        setBackground(Color.BLACK);
        setForeground(isSelected ? Color.CYAN : Color.LIGHT_GRAY.darker());
        setBorder(null);
        setOpaque(true);
        return this;
    }
}