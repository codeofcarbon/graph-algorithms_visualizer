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
        setRenderer(new CellRenderer<>());
        setUI(new BasicComboBoxUI() {                  // todo try to get transparent background in pop up
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
    }

    @Override
    public JPopupMenu getComponentPopupMenu() {
        var popup = (JPopupMenu) getAccessibleContext().getAccessibleChild(0);
        popup.setBorder(BorderFactory.createEmptyBorder());
        popup.setPopupSize(180, getItemCount() * 20);
        return popup;
    }
}

class CellRenderer<T> extends JLabel implements ListCellRenderer<T> {

    @Override
    public Component getListCellRendererComponent(JList<? extends T> list, T value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        setFont(new Font("Tahoma", Font.PLAIN, 15));
        setText(value.toString());
        setPreferredSize(new Dimension(180, 20));
        setHorizontalAlignment(SwingConstants.RIGHT);
        setSize(getPreferredSize());
        setBackground(Color.BLACK);
        setForeground(isSelected ? new Color(40, 162, 212, 255) : Color.LIGHT_GRAY.darker());
        setBorder(null);
        setOpaque(true);
        return this;
    }
}