//package visualizer;
//
//import java.awt.*;
//
//public abstract class BaseMovableShape implements MovableShape {
//    int x, y;
//    private int dx = 0, dy = 0;
//    private boolean selected = false;
//
//    BaseMovableShape(int x, int y) {
//        this.x = x;
//        this.y = y;
//    }
//
//    @Override
//    public int getX() {
//        return x;
//    }
//
//    @Override
//    public int getY() {
//        return y;
//    }
//
//    @Override
//    public int getWidth() {
//        return 0;
//    }
//
//    @Override
//    public int getHeight() {
//        return 0;
//    }
//
//    @Override
//    public void drag() {
//        dx = x;
//        dy = y;
//    }
//
//    @Override
//    public void moveTo(int x, int y) {
//        this.x = dx + x;
//        this.y = dy + y;
//    }
//
//    @Override
//    public void moveBy(int x, int y) {
//        this.x += x;
//        this.y += y;
//    }
//
//    @Override
//    public void drop() {
//        this.x = dx;
//        this.y = dy;
//    }
//
//    @Override
//    public boolean isInsideBounds(int x, int y) {
//        return x > getX() && x < (getX() + getWidth()) &&
//               y > getY() && y < (getY() + getHeight());
//    }
//
//    @Override
//    public void select() {
//        selected = true;
//    }
//
//    @Override
//    public void unSelect() {
//        selected = false;
//    }
//
//    @Override
//    public boolean isSelected() {
//        return selected;
//    }
//
//
//    @Override
//    public void paint(Graphics graphics) {
//
//    }
//}
