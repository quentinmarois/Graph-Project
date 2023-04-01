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
        computeEarliestTime(false);
        computeLatestTime(false);

    }
    private Graph(Graph graph) {
        filename = graph.filename;

        vertices = new ArrayList<>();
        for (Vertex v : graph.vertices) {
            vertices.add(new Vertex(v));
        }

    }

    public boolean hasCycle(boolean log) {
        StringBuilder cycleLog = new StringBuilder();
        cycleLog.append(TextColor.YELLOW + "Checking for cycles by successive deletion of entry points (i.e. no predecessors)" + TextColor.RESET).append("\n");

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

            cycleLog.append("Entry points: ");
            if (noPredecessors.isEmpty()) {
                cycleLog.append(TextColor.RED + "None" + TextColor.RESET + "\n");
            } else {
                for (Vertex v : noPredecessors) {
                    cycleLog.append(TextColor.CYAN).append(v.id).append(" ").append(TextColor.RESET);
                }
                cycleLog.append("\n");
            }

            // If there are no vertices with no predecessors, there is a cycle
            if (noPredecessors.isEmpty()) {
                cycleLog.append(TextColor.YELLOW + "No entry points, graph has a cycle" + TextColor.RESET);
                if (log) { System.out.println(cycleLog); }
                return true;
            }

            // Remove vertices with no predecessors
            for (Vertex v : noPredecessors) {
                graph.removeVertex(v);
            }

        }
        cycleLog.append(TextColor.YELLOW + "Graph empty, no cycles detected" + TextColor.RESET + "\n");
        if (log) { System.out.println(cycleLog); }
        return false;
    }

    public boolean hasNegativeDuration(boolean log){
        // Check if any vertex has a negative weight
        if (log) { System.out.println(TextColor.YELLOW + "Checking for negative durations" + TextColor.RESET); }
        for (Vertex v : vertices) {
            if (v.duration < 0) {
                if (log) { System.out.println(TextColor.RED + "Vertex " + v.id + " has a negative duration" + TextColor.RESET + "\n"); }
                return true;
            }
        }
        if (log) { System.out.println(TextColor.YELLOW + "No negative durations detected" + TextColor.RESET + "\n"); }
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
                if (log) { System.out.println("Removing vertex " + TextColor.CYAN + v.id + TextColor.RESET + " with rank " + TextColor.PURPLE + rank + TextColor.RESET); }
            }

            rank++;
        }

    }
    public void computeEarliestTime(boolean log){
        StringBuilder earlTLog = new StringBuilder();

        if (hasCycle(false)) {
            if (log) { System.out.println(TextColor.RED + "Graph has a cycle, cannot compute earliest time" + TextColor.RESET); }
            return;
        }

        // Sort vertices by rank in ascending order
        vertices.sort(Comparator.comparingInt(Vertex::getRank));

        for (Vertex v : vertices){
            // If vertex has no predecessors (i.e. source), set earliest time to 0
            if (v.predecessors.isEmpty()){
                getVertex(v.id).earliestTime = 0;

                earlTLog.append("Vertex " + TextColor.CYAN).append(v.id).append(TextColor.RESET).append(" is the source, setting earliest time to ").append(TextColor.YELLOW).append(0).append(TextColor.RESET).append("\n");
            } else {
                // Else, set the earliest time as the max of the predecessors' earliest time + duration
                int max = 0;

                earlTLog.append("Vertex " + TextColor.CYAN).append(v.id).append(TextColor.RESET).append(", duration ").append(TextColor.RED).append(v.duration).append(TextColor.RESET).append(" predecessors : ");

                for (int predecessor : v.predecessors){
                    earlTLog.append(TextColor.CYAN).append(predecessor).append(TextColor.RESET).append(" (").append(TextColor.YELLOW).append(getVertex(predecessor).earliestTime).append(TextColor.RESET).append("), ");

                    int time = getVertex(predecessor).earliestTime + getVertex(predecessor).duration;
                    if (time > max){
                        max = time;
                    }
                }
                getVertex(v.id).earliestTime = max;

                if (log) {
                    // Remove trailing comma
                    earlTLog.deleteCharAt(earlTLog.length() - 2);
                    earlTLog.append("-> " + TextColor.YELLOW).append(v.earliestTime).append(TextColor.RESET).append("\n");
                }
            }
        }
        // Sort back to ascending order of id
        vertices.sort(Comparator.comparingInt(v -> v.id));
        if (log) { System.out.println(earlTLog); }
    }

    public void computeLatestTime(boolean log){
        StringBuilder lateTLog = new StringBuilder();

        if (hasCycle(false)) {
            if (log) { System.out.println(TextColor.RED + "Graph has a cycle, cannot compute latest time" + TextColor.RESET); }
            return;
        }

        // Sort vertices by rank in descending order
        vertices.sort(Comparator.comparingInt(Vertex::getRank).reversed());

        for (Vertex v : vertices){
            // If vertex has no successors (i.e. sink), set the latest time to the earliest time
            if (getSuccessors(v).isEmpty()){
                getVertex(v.id).latestTime = getVertex(v.id).earliestTime;
                lateTLog.append("Vertex " + TextColor.CYAN).append(v.id).append(TextColor.RESET).append(" is the destination, setting earliest time to its earliest time ").append(TextColor.YELLOW).append(v.earliestTime).append(TextColor.RESET).append("\n");
            } else {
                // Else, set the latest time as the min of the successors' latest time - duration
                int min = Integer.MAX_VALUE;

                lateTLog.append("Vertex " + TextColor.CYAN).append(v.id).append(TextColor.RESET).append(", duration ").append(TextColor.RED).append(v.duration).append(TextColor.RESET).append(" successors : ");

                for (Vertex successor : getSuccessors(v)){
                    lateTLog.append(TextColor.CYAN).append(successor.id).append(TextColor.RESET).append(" (").append(TextColor.GREEN).append(successor.latestTime).append(TextColor.RESET).append("), ");

                    int time = getVertex(successor.id).latestTime;
                    if (time < min){
                        min = time;
                    }
                }
                getVertex(v.id).latestTime = min - getVertex(v.id).duration;

                if (log) {
                    // Remove trailing comma
                    lateTLog.deleteCharAt(lateTLog.length() - 2);
                    lateTLog.append("-> " + TextColor.GREEN).append(v.latestTime).append(TextColor.RESET).append("\n");
                }
            }
        }
        // Sort back to ascending order of id
        vertices.sort(Comparator.comparingInt(v -> v.id));
        if (log) { System.out.println(lateTLog); }
    }

    public void displayTimes() {
        StringBuilder duration = new StringBuilder();
        StringBuilder task = new StringBuilder();
        StringBuilder earliest = new StringBuilder();
        StringBuilder latest = new StringBuilder();
        StringBuilder total_float = new StringBuilder();

        for (Vertex v : this.vertices){
            duration.append(v.duration).append("\t");
            task.append(v.id).append("\t");
            earliest.append(v.earliestTime).append("\t");
            latest.append(v.latestTime).append("\t");
            total_float.append(v.latestTime - v.earliestTime).append("\t");
        }
        System.out.println("Task\t\t" + TextColor.CYAN + task + TextColor.RESET);
        System.out.println("Duration\t" + TextColor.RED + duration + TextColor.RESET);
        System.out.println("Earliest\t" + TextColor.YELLOW + earliest + TextColor.RESET);
        System.out.println("Latest\t\t" + TextColor.GREEN + latest + TextColor.RESET);
        System.out.println("Float\t\t" + TextColor.PURPLE + total_float + TextColor.RESET);
    }

    public void displayCriticalPath(){
        // Path with vertices with 0 total float
        StringBuilder path = new StringBuilder();
        path.append("Critical path : ");

        for (Vertex v : this.vertices){
            if (v.latestTime - v.earliestTime == 0){
                path.append(TextColor.CYAN).append(v.id).append(TextColor.RESET).append(" -> ");
            }
        }
        // Remove trailing arrow
        path.delete(path.length() - 4, path.length());
        System.out.println(path);

    }

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
        System.out.println();
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Vertex v : vertices) {
            sb.append(v.toString()).append("\n");
        }
        return sb.toString();
    }
}
