package visualizer;

public class MoveCommand {
    private final Vertex vertex;
    private int startX, startY;
    private int endX, endY;

    public MoveCommand(Vertex vertex) {
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

    public void execute() {
        vertex.moveBy(endX - startX, endY - startY);
    }
}