package visualizer;

import javax.swing.undo.StateEdit;
import java.awt.*;
import java.awt.event.*;

public class MouseHandler extends MouseAdapter {
    private final GraphService service;
    private Point location, pressed;
    private Component source;
    private StateEdit nodeMoveEdit;

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
        service.graphEdit = null;
        if (source instanceof Node) {
            nodeMoveEdit = new StateEdit((Node) source);
        }
    }

    @Override
    public void mouseReleased(MouseEvent event) {
        if (source instanceof Node && !source.getLocation().equals(location)) {
            if (service.graphEdit == null && pressed != null && nodeMoveEdit != null) {
                nodeMoveEdit.end();
                service.getUndoableEditSupport().postEdit(nodeMoveEdit);
                pressed = null;
            }
        }
        nodeMoveEdit = null;
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        if (source instanceof Node || source instanceof Graph) {
            switch (service.getGraphMode()) {
                case ADD_NODE:
                    service.createNewVertex(event);
                    break;
                case ADD_AN_EDGE:
                    service.createNewEdge(event);
                    break;
                case REMOVE_NODE:
                    service.removeVertex(event);
                    break;
                case REMOVE_AN_EDGE:
                    service.removeEdge(event);
                    break;
                case NONE:
                    if (service.getAlgorithmMode() != AlgMode.NONE) service.startAlgorithm(event);
                    return;
            }
            pressed = null;
            if (service.graphEdit != null) {
                service.getUndoableEditSupport().postEdit(service.graphEdit);
                service.graphEdit = null;
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent event) {
        if (source instanceof Graph || pressed == null) return;
        var drag = event.getLocationOnScreen();
        if (source instanceof Node) {
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