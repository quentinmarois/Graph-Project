import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Graph {
    private static final String EMPTY_SYMBOL = "-";

    private String filename;
    private final List<Vertex> vertices;
    public Graph(String filepath) {

        try{
            filename = filepath;
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filepath), StandardCharsets.UTF_8));

            // Each line of the file is a vertex
            String line;
            vertices = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                vertices.add(new Vertex(line));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Create source vertex
        vertices.add(new Vertex(0,0));

        // For vertices with no predecessors, add 0 as predecessor except for source vertex (id 0)
        for (Vertex v : vertices) {
            if (v.predecessors.isEmpty() && v.id != 0) {
                v.addPredecessor(0);
            }
        }

        // Create sink vertex
        Vertex sink = new Vertex(vertices.size(), 0);

        // Check which vertices are not in any other vertex's predecessors
        for (Vertex v : vertices) {
            boolean isPredecessor = false;
            for (Vertex v2 : vertices) {
                if (v2.predecessors.contains(v.id)) {
                    isPredecessor = true;
                    break;
                }
            }
            if (!isPredecessor) {
                sink.addPredecessor(v.id);
            }
        }

        vertices.add(sink);

        // Sort vertices by id
        vertices.sort(Comparator.comparingInt(v -> v.id));
    }
    private Graph(Graph graph) {
        this.filename = graph.filename;
        this.vertices = new ArrayList<>(graph.vertices);
    }

    public boolean hasCycle(boolean log) {
        Graph graph = new Graph(this);

        // While the graph has vertices
        while (!graph.vertices.isEmpty()) {
            // Find vertices with no predecessors
            List<Vertex> noPredecessors = new ArrayList<>();

            for (Vertex v : graph.vertices) {
                if (v.predecessors.isEmpty()) {
                    noPredecessors.add(v);
                }
            }

            if (log) {
                System.out.print("Entry points: ");

                if (noPredecessors.isEmpty()) {
                    System.out.println(TextColor.RED + "None" + TextColor.RESET);
                } else {
                    for (Vertex v : noPredecessors) {
                        System.out.print(TextColor.CYAN + v.id + " " + TextColor.RESET);
                    }
                    System.out.println();
                }
            }



            // If there are no vertices with no predecessors, there is a cycle
            if (noPredecessors.isEmpty()) {
                if (log) { System.out.println(TextColor.YELLOW + "No entry points, graph has a cycle" + TextColor.RESET); }
                return true;
            }
            // Remove vertices with no predecessors
            for (Vertex v : noPredecessors) {
                graph.removeVertex(v);
            }

        }
        if (log) { System.out.println(TextColor.YELLOW + "Graph empty, no cycles detected" + TextColor.RESET); }
        return false;
    }
    // TODO : Compute ranks of all vertices (only if no cycles)
    // TODO : Compute earliest start time of all vertices
    // TODO : Compute latest start time of all vertices
    // TODO : Compute critical path

    private List<Integer> getSuccessors(Vertex vertex){
        List<Integer> successors = new ArrayList<>();

        for (Vertex v : vertices) {
            if (v.predecessors.contains(vertex.id)) {
                successors.add(v.id);
            }
        }

        return successors;
    }

    private void removeVertex(Vertex vertex){
        // Remove vertex from predecessors
        for (Vertex v : vertices) {
            v.predecessors.remove((Integer) vertex.id);
        }
        // Remove vertex
        vertices.remove(vertex);
    }

    public void displayTriplets(){
        StringBuilder sb = new StringBuilder();
        sb.append(TextColor.PURPLE).append(vertices.size()).append(TextColor.RESET).append(" vertices").append("\n");

        int edges = 0;
        for (Vertex v : vertices) {
            edges += v.predecessors.size();
        }
        sb.append(TextColor.PURPLE).append(edges).append(TextColor.RESET).append(" edges").append("\n");

        for (Vertex v : vertices) {
            if (!getSuccessors(v).isEmpty()) {
                for (int successor : getSuccessors(v)) {
                    sb.append(TextColor.CYAN).append(v.id).append(TextColor.RESET).append(" -> ")
                            .append(TextColor.GREEN).append(successor).append(TextColor.RESET).append(" = ")
                            .append(TextColor.YELLOW).append(v.duration).append(TextColor.RESET).append("\n");
                }
            }
        }
        System.out.println(sb);
    }

    public void displayValueMatrix(){
        System.out.println("Value matrix");

        String[][] valueMatrix = new String[vertices.size()][vertices.size()];

        // Fill matrix with *
        for (int i = 0; i < vertices.size(); i++) {
            for (int j = 0; j < vertices.size(); j++) {
                valueMatrix[i][j] = EMPTY_SYMBOL;
            }
        }

        // Fill matrix with values
        for (Vertex v : vertices) {
            for (int successor : getSuccessors(v)) {
                valueMatrix[v.id][successor] = TextColor.YELLOW + v.duration + TextColor.RESET;
            }
        }

        // Print column headers
        System.out.print(TextColor.GREEN + "\t");
        for (int i = 0; i < vertices.size(); i++) {
            System.out.print(i + "\t");
        }
        System.out.println(TextColor.RESET);

        // Print row headers and values
        for (int i = 0; i < vertices.size(); i++){
            System.out.print(TextColor.CYAN + i + TextColor.RESET + "\t");
            for (int j = 0; j < vertices.size(); j++){
                System.out.print(valueMatrix[i][j] + "\t");
            }
            System.out.println();
        }
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Vertex v : vertices) {
            sb.append(v.toString()).append("\n");
        }
        return sb.toString();
    }

    public String getFilename() {
        return filename;
    }
}
