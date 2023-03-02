import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Graph> graphs = new ArrayList<>();

        for (int i = 1; i <= 12; i++){
            graphs.add(new Graph("src/tests/table " + i + ".txt"));
        }

        int table = 1;
        for (Graph graph : graphs) {

            System.out.println("Table " + table);
            graph.displayTriplets();
            table++;
        }

    }
}