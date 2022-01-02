package visualizer;

import lombok.Getter;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

@Getter
public class GraphWindow extends JPanel {
    private final List<Vertex> vertices = new ArrayList<>();
    private final List<Edge> edges = new ArrayList<>();
    private Component[] nodes;
    GraphMode graphMode = GraphMode.ADD_A_VERTEX;
    GraphService service;
    private Timer timer;

    public GraphWindow() {
        setDoubleBuffered(true);
        setName("Graph");
        setBackground(Color.BLACK);
        setSize(800, 600);
        setLayout(null);
        addListeners();
    }

    private void addListeners() {
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
//                if (graphMode == GraphMode.MOVE_VERTEX) return;

                if (graphMode == GraphMode.ADD_A_VERTEX) service.createNewVertex(e);
                if (graphMode == GraphMode.ADD_AN_EDGE) service.createNewEdge(e);
                if (graphMode == GraphMode.REMOVE_A_VERTEX) service.removeVertex(e);
                if (graphMode == GraphMode.REMOVE_AN_EDGE) service.removeEdge(e);
                if (graphMode == GraphMode.NONE) {
                    if (service.getAlgorithmMode() != AlgMode.NONE) service.startAlgorithm(e);
                }
            }
        });

        addMouseListener(new ComponentMover() {
            Vertex node;
            Component window;
            @Override
            public void mousePressed(MouseEvent e) {
                if (graphMode != GraphMode.MOVE_VERTEX) return;
                super.mousePressed(e);
                setAutoLayout(true);

                node = service.moveVertex(e);
                if (node != null) {
                    registerComponent(node);
//                System.err.println(node == null);
//                System.err.println(e.getComponent().getName());
//                Arrays.stream(((Graph) e.getComponent()).getComponents()).map(Component::getName).forEach(System.err::println);
//                if (node != null) {
//                    registerComponent(node);
//                    Arrays.stream(((Graph) e.getComponent()).getComponents())
//                            .filter(c -> c instanceof Vertex)
//                            .filter(component -> ((Vertex) component).id.equals(node.id))
//                            .peek(v -> System.err.println(v.getName()))
//                            .findFirst().ifPresent(this::registerComponent);
//                                    targetVertex.center
                }
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setStroke(new BasicStroke(3f));
        for (var e : edges) {
            e.getState().coloring(g, g2d, e);
            g.setFont(new Font("Courier", Font.PLAIN, 20));
            boolean align = Math.abs(e.source.getX() - e.target.getX()) > Math.abs(e.source.getY() - e.target.getY());
            g.drawString(e.edgeLabel.getText(),
                    (e.source.getX() + e.target.getX()) / 2 + (align ? -10 : 10),
                    (e.source.getY() + e.target.getY()) / 2 + (align ? 25 : 10));
        }

        g2d.setStroke(new BasicStroke(1f));
        for (var v : vertices) {
            v.getState().coloring(g, v);
            g.setFont(new Font("Courier", Font.ITALIC, 30));
            g.drawString(v.id, v.center.x - 8, v.center.y + 12);
        }
    }
}

/*
//        MouseAdapter mouseDrag = new MouseAdapter() {
//            MoveCommand moveCommand;
//            Vertex target;
//
//            @Override
//            public void mouseDragged(MouseEvent e) {
//                if (graphMode != GraphMode.MOVE_VERTEX) return;
//                timer = new Timer(15, event -> {
//                    target.setLocation(target.getLocation());
//                    paintAll(target.getGraphics());
//                });
//                timer.start();
//                if (moveCommand == null) {
//                    target = service.moveVertex(e);
//                    if (target != null) {
//                        moveCommand = new MoveCommand(target);
//                        moveCommand.start(e.getX(), e.getY());
//                        moveCommand.move(e.getX(), e.getY());
//                        repaint();
//                    }
//                }
//            }
//
//            @Override
//            public void mouseReleased(MouseEvent e) {
//                if (e.getButton() != MouseEvent.BUTTON1 || moveCommand == null) {
//                    return;
//                }
//                moveCommand.stop(e.getX(), e.getY());
//                moveCommand.execute();
////                editor.execute(moveCommand);
//                this.moveCommand = null;
//                repaint();
//                timer.stop();
//            }
//        };
//        addMouseListener(mouseDrag);
//        addMouseMotionListener(mouseDrag);
 */

/*
//        MouseInputAdapter mia = new MouseInputAdapter() {
//            Point location;
//            Point pressed;
//            Vertex target;
//
//            public void mousePressed(MouseEvent me) {
//                pressed = me.getLocationOnScreen();
//                target = service.moveVertex(me);
//                System.err.println(target == null);
//                if (target != null) {
//                    Window window = SwingUtilities.windowForComponent(me.getComponent());
//                    location = window.getLocation();
//                }
//            }
//
//            public void mouseDragged(MouseEvent me) {
//                Point dragged = me.getLocationOnScreen();
//                int x = (int) (location.x + dragged.getX() - pressed.getX());
//                int y = (int) (location.y + dragged.getY() - pressed.getY());
//                Window window = SwingUtilities.windowForComponent(me.getComponent());
//                window.setLocation(x, y);
//            }
//        };
//
//        addMouseListener(mia);
//        addMouseMotionListener(mia);
 */