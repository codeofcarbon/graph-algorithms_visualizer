package visualizer;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class ModeComboBox<T> extends JComboBox<String> {

    public ModeComboBox(T[] array) {
        Arrays.stream(array)
                .forEach(mode -> {
                    if (mode instanceof AlgMode) addItem(((AlgMode) mode).current.toUpperCase());
                    if (mode instanceof GraphMode) addItem(((GraphMode) mode).current.toUpperCase());
                });
//        setUI(new BasicComboBoxUI() {
//            @Override
//            protected JButton createArrowButton() {
//                return new JButton() {
//                    @Override
//                    public int getWidth() {
//                        return 0;
//                    }
//                };
//            }
//        });
        setRenderer(new CellRenderer<>());
        setFocusable(false);
        setVisible(true);
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
//        System.err.println(Arrays.toString(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
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