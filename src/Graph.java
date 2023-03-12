import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Graph {
    private static final String EMPTY_SYMBOL = "-";

    private final String filename;
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

        // For vertices with no predecessors, add 0 (alpha) as predecessor except for source vertex itself
        for (Vertex v : vertices) {
            if (v.predecessors.isEmpty() && v.id != 0) {
                v.addPredecessor(0);
            }
        }

        // Create sink vertex (omega)
        Vertex sink = new Vertex(vertices.size(), 0);

        // Check which vertices are predecessors of the sink
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

        computeRanks(false);
        computeEarliestTime();
        computeLatestTime();

    }
    private Graph(Graph graph) {
        filename = graph.filename;

        vertices = new ArrayList<>();
        for (Vertex v : graph.vertices) {
            vertices.add(new Vertex(v));
        }

    }

    public boolean hasCycle(boolean log) {
        // Apply successive removal of vertices with no predecessors to a copy of the graph
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

    public void computeRanks(boolean log){
        if (hasCycle(false)) {
            if (log) { System.out.println(TextColor.RED + "Graph has a cycle, cannot compute ranks" + TextColor.RESET); }
            return;
        }

        Graph graph = new Graph(this);
        int rank = 0;

        while (!graph.vertices.isEmpty()) {
            // Find vertices with no predecessors
            List<Vertex> noPredecessors = new ArrayList<>();

            for (Vertex v : graph.vertices) {
                if (v.predecessors.isEmpty()) {
                    noPredecessors.add(v);
                }
            }

            // Remove vertices with no predecessors from copy and set the rank to original graph
            for (Vertex v : noPredecessors) {
                graph.removeVertex(v);
                this.getVertex(v.id).setRank(rank);
            }

            rank++;
        }

        if (log) {
            System.out.println(TextColor.YELLOW + "Ranks computed" + TextColor.RESET);
            System.out.println(this);
        }

    }
    public void computeEarliestTime(){
        if (hasCycle(false)) {
            System.out.println(TextColor.RED + "Graph has a cycle, cannot compute earliest time" + TextColor.RESET);
            return;
        }

        // Sort vertices by rank in ascending order
        vertices.sort(Comparator.comparingInt(Vertex::getRank));

        for (Vertex v : vertices){
            // If vertex has no predecessors (i.e. source), set earliest time to 0
            if (v.predecessors.isEmpty()){
                getVertex(v.id).earliestTime = 0;
            } else {
                // Else, set the earliest time as the max of the predecessors' earliest time + duration
                int max = 0;
                for (int predecessor : v.predecessors){
                    int time = getVertex(predecessor).earliestTime + getVertex(predecessor).duration;
                    if (time > max){
                        max = time;
                    }
                }
                getVertex(v.id).earliestTime = max;
            }
        }
        // Sort back to ascending order of id
        vertices.sort(Comparator.comparingInt(v -> v.id));
    }

    public void computeLatestTime(){
        if (hasCycle(false)) {
            System.out.println(TextColor.RED + "Graph has a cycle, cannot compute latest time" + TextColor.RESET);
            return;
        }

        // Sort vertices by rank in descending order
        vertices.sort(Comparator.comparingInt(Vertex::getRank).reversed());

        for (Vertex v : vertices){
            // If vertex has no successors (i.e. sink), set the latest time to the earliest time
            if (getSuccessors(v).isEmpty()){
                getVertex(v.id).latestTime = getVertex(v.id).earliestTime;
            } else {
                // Else, set the latest time as the min of the successors' latest time - duration
                int min = Integer.MAX_VALUE;
                for (Vertex successor : getSuccessors(v)){
                    int time = getVertex(successor.id).latestTime;
                    if (time < min){
                        min = time;
                    }
                }
                getVertex(v.id).latestTime = min - getVertex(v.id).duration;
            }
        }
        // Sort back to ascending order of id
        vertices.sort(Comparator.comparingInt(v -> v.id));
    }

    public void displayTimes() {
        StringBuilder duration = new StringBuilder();
        StringBuilder task = new StringBuilder();
        StringBuilder earliest = new StringBuilder();
        StringBuilder latest = new StringBuilder();

        for (Vertex v : this.vertices){
            duration.append(v.duration).append("\t");
            task.append(v.id).append("\t");
            earliest.append(v.earliestTime).append("\t");
            latest.append(v.latestTime).append("\t");
        }
        System.out.println("Task\t" + TextColor.CYAN + task + TextColor.RESET);
        System.out.println("Dur.\t" + TextColor.RED + duration + TextColor.RESET);
        System.out.println("Earl.\t" + TextColor.YELLOW + earliest + TextColor.RESET);
        System.out.println("Late.\t" + TextColor.GREEN + latest + TextColor.RESET);
    }

    // TODO : Compute critical path : add to displayTimes()
    // TODO : Remove redundant comments
    // TODO : Refactor
    // TODO : displayTimes() : more beautiful

    private List<Vertex> getSuccessors(Vertex vertex){
        List<Vertex> successors = new ArrayList<>();

        for (Vertex v : vertices) {
            if (v.predecessors.contains(vertex.id)) {
                successors.add(v);
            }
        }

        return successors;
    }

    private Vertex getVertex(int id){
        Vertex vertex = null;
        for (Vertex v : vertices) {
            if (v.id == id) {
                vertex = v;
                break;
            }
        }
        return vertex;
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
                for (Vertex successor : getSuccessors(v)) {
                    // id -> successor = duration
                    sb.append(TextColor.CYAN).append(v.id).append(TextColor.RESET).append(" -> ")
                            .append(TextColor.GREEN).append(successor.id).append(TextColor.RESET).append(" = ")
                            .append(TextColor.YELLOW).append(v.duration).append(TextColor.RESET).append("\n");
                }
            }
        }
        System.out.println(sb);
    }

    public void displayValueMatrix(){
        System.out.println("Value matrix");

        String[][] valueMatrix = new String[vertices.size()][vertices.size()];

        // Fill matrix with EMPTY_SYMBOL
        for (int i = 0; i < vertices.size(); i++) {
            for (int j = 0; j < vertices.size(); j++) {
                valueMatrix[i][j] = EMPTY_SYMBOL;
            }
        }

        // Fill matrix with values
        for (Vertex v : vertices) {
            for (Vertex successor : getSuccessors(v)) {
                valueMatrix[v.id][successor.id] = TextColor.YELLOW + v.duration + TextColor.RESET;
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
