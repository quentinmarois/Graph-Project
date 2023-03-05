import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Graph> graphs = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            graphs.add(new Graph("src/tests/table " + i + ".txt"));
        }

        for (Graph graph : graphs) {
            System.out.println("Graph " + graph.getFilename());
            System.out.println("Has cycle: " + graph.hasCycle(false));
        }

    }
}