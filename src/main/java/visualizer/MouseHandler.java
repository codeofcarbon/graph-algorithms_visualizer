package visualizer;

import javax.swing.undo.StateEdit;
import java.awt.*;
import java.awt.event.*;

public class MouseHandler extends MouseAdapter {
    private final GraphService service;
    private Point location, pressed;
    private Component source;
    private StateEdit moveStateEdit;

    public MouseHandler(GraphService service) {
        this.service = service;
    }

    public void addComponent(Component component) {
        component.addMouseListener(this);
        component.addMouseMotionListener(this);
    }

    @Override
    public void mousePressed(MouseEvent event) {
        source = event.getComponent();
        pressed = event.getLocationOnScreen();
        location = source.getLocation();
        if (source instanceof Vertex)
            moveStateEdit = new StateEdit((Vertex) source);
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        if (source instanceof Vertex || source instanceof Graph) {
            StateEdit stateEdit = new StateEdit(service);
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
                    break;
            }
            stateEdit.end();
            service.getUndoableEditSupport().postEdit(stateEdit);
        }
    }

    @Override
    public void mouseDragged(MouseEvent event) {
        var drag = event.getLocationOnScreen();
        if (source instanceof Vertex) {
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

    @Override
    public void mouseReleased(MouseEvent event) {
        if (source instanceof Vertex && !source.getLocation().equals(location)) {
            moveStateEdit.end();
            service.getUndoableEditSupport().postEdit(moveStateEdit);
        }
    }
}