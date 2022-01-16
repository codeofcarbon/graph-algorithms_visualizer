package visualizer;

import java.awt.*;
import java.awt.event.*;

public class MouseHandler extends MouseAdapter {
    private final GraphService service;
    private Point location, pressed;
    private Component source;

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
        source = event.getComponent();
        pressed = event.getLocationOnScreen();
        location = source.getLocation();
    }

    public void mouseDragged(MouseEvent event) {
        if (source instanceof Graph) return;
        if (source instanceof Vertex) {
            var drag = event.getLocationOnScreen();
            int x = (int) (location.x + drag.getX() - pressed.getX());
            int y = (int) (location.y + drag.getY() - pressed.getY());
            if (x < source.getParent().getWidth() - 10
                && y < source.getParent().getHeight() - 10
                && x > -40 && y > -40) {
                source.setLocation(x, y);
            }
            source.getParent().repaint();
        }
    }
}