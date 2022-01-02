package visualizer;

import java.awt.*;
import java.io.Serializable;

public interface MovableShape extends Serializable {
    int getX();

    int getY();

    int getWidth();

    int getHeight();

    void drag();

    void drop();

    void moveTo(int x, int y);

    void moveBy(int x, int y);

//    boolean isInsideBounds(int x, int y);
//
//    void select();
//
//    void unSelect();
//
//    boolean isSelected();
//
    void paint(Graphics graphics);
}
