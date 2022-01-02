package visualizer;


public class MoveCommand implements Command {
    private final Vertex vertex;
//    private Graph graph;
    private int startX, startY;
    private int endX, endY;

    public MoveCommand(/*Graph graph, */Vertex vertex) {
//        this.graph = graph;
        this.vertex = vertex;
    }

    public void start(int x, int y) {
        startX = x;
        startY = y;
        vertex.drag();
    }

    public void move(int x, int y) {
        vertex.moveTo(x - startX, y - startY);
    }

    public void stop(int x, int y) {
        endX = x;
        endY = y;
        vertex.drop();
    }

    @Override
    public void execute() {
        vertex.moveBy(endX - startX, endY - startY);
    }

    @Override
    public String getName() {
        return "Move by X:" + (endX - startX) + " Y:" + (endY - startY);
    }
}
