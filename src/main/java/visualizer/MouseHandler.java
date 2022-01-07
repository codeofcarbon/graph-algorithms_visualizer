package visualizer;

import java.awt.*;
import java.awt.event.*;

public class MouseHandler extends MouseAdapter {
    private final GraphService service;
    private Point location, pressed;
    private Component component;

    MouseHandler(GraphService service) {
        this.service = service;
    }

    public void addComponent(Component component) {
        component.addMouseListener(this);
        component.addMouseMotionListener(this);
    }

    public void mouseClicked(MouseEvent event) {
        switch (service.getGraphMode()) {
            case ADD_A_VERTEX:
                service.createNewVertex(event);
                break;
            case ADD_AN_EDGE:
                service.createNewEdge(event);
                break;
            case REMOVE_A_VERTEX:
                service.removeVertex(event);
                break;
            case REMOVE_AN_EDGE:
                service.removeEdge(event);
                break;
            case NONE:
                if (service.getAlgorithmMode() != AlgMode.NONE) service.startAlgorithm(event);
        }
    }

    public void mousePressed(MouseEvent event) {
        component = event.getComponent();
        pressed = event.getLocationOnScreen();
        location = component.getLocation();
    }

    public void mouseDragged(MouseEvent event) {
        if (component instanceof Graph) return;
        Point dragged = event.getLocationOnScreen();
        int x = (int) (location.x + dragged.getX() - pressed.getX());
        int y = (int) (location.y + dragged.getY() - pressed.getY());
        component.setLocation(x, y);
        component.getParent().repaint();
    }
}