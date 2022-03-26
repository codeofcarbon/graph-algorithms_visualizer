package visualizer;

import javax.swing.*;
import java.awt.*;

public class IconMaker {

    static ImageIcon loadIcon(String iconFilename, String type, int width, int height) {
        return new ImageIcon(new ImageIcon(
                String.format("src/main/resources/icons/%s/%s.png", type, iconFilename))
                .getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
    }

    static Image loadBackgroundImage() {
        return new ImageIcon("src/main/resources/icons/special/background.png").getImage();
    }
}
