import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Int4_1_Graph {
    private static final String EMPTY_SYMBOL = "-";

    private final String filename;
    private final List<Int4_1_Vertex> vertices;
    public Int4_1_Graph(String filepath) {

        try{
            filename = filepath;
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filepath), StandardCharsets.UTF_8));

            // Each line of the file is a vertex
            String line;
            vertices = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                vertices.add(new Int4_1_Vertex(line));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Create source vertex
        vertices.add(new Int4_1_Vertex(0,0));

        // For vertices with no predecessors, add 0 (alpha) as predecessor except for source vertex itself
        for (Int4_1_Vertex v : vertices) {
            if (v.predecessors.isEmpty() && v.id != 0) {
                v.addPredecessor(0);
            }
        }

        // Create sink vertex (omega)
        Int4_1_Vertex sink = new Int4_1_Vertex(vertices.size(), 0);

        // Check which vertices are predecessors of the sink
        for (Int4_1_Vertex v : vertices) {
            boolean isPredecessor = false;
            for (Int4_1_Vertex v2 : vertices) {
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
    private Int4_1_Graph(Int4_1_Graph graph) {
        filename = graph.filename;

        vertices = new ArrayList<>();
        for (Int4_1_Vertex v : graph.vertices) {
            vertices.add(new Int4_1_Vertex(v));
        }

    }

    public boolean hasCycle(boolean log) {
        StringBuilder cycleLog = new StringBuilder();
        cycleLog.append(Int4_1_TextColor.YELLOW + "Checking for cycles by successive deletion of entry points (i.e. no predecessors)" + Int4_1_TextColor.RESET).append("\n");

        // Apply successive removal of vertices with no predecessors to a copy of the graph
        Int4_1_Graph graph = new Int4_1_Graph(this);

        // While the graph has vertices
        while (!graph.vertices.isEmpty()) {
            // Find vertices with no predecessors
            List<Int4_1_Vertex> noPredecessors = new ArrayList<>();

            for (Int4_1_Vertex v : graph.vertices) {
                if (v.predecessors.isEmpty()) {
                    noPredecessors.add(v);
                }
            }

            cycleLog.append("Entry points: ");
            if (noPredecessors.isEmpty()) {
                cycleLog.append(Int4_1_TextColor.RED + "None" + Int4_1_TextColor.RESET + "\n");
            } else {
                for (Int4_1_Vertex v : noPredecessors) {
                    cycleLog.append(Int4_1_TextColor.CYAN).append(v.id).append(" ").append(Int4_1_TextColor.RESET);
                }
                cycleLog.append("\n");
            }

            // If there are no vertices with no predecessors, there is a cycle
            if (noPredecessors.isEmpty()) {
                cycleLog.append(Int4_1_TextColor.YELLOW + "No entry points, graph has a cycle" + Int4_1_TextColor.RESET);
                if (log) { System.out.println(cycleLog); }
                return true;
            }

            // Remove vertices with no predecessors
            for (Int4_1_Vertex v : noPredecessors) {
                graph.removeVertex(v);
            }

        }
        cycleLog.append(Int4_1_TextColor.YELLOW + "Graph empty, no cycles detected" + Int4_1_TextColor.RESET + "\n");
        if (log) { System.out.println(cycleLog); }
        return false;
    }

    public boolean hasNegativeDuration(boolean log){
        // Check if any vertex has a negative weight
        if (log) { System.out.println(Int4_1_TextColor.YELLOW + "Checking for negative durations" + Int4_1_TextColor.RESET); }
        for (Int4_1_Vertex v : vertices) {
            if (v.duration < 0) {
                if (log) { System.out.println(Int4_1_TextColor.RED + "Vertex " + v.id + " has a negative duration" + Int4_1_TextColor.RESET + "\n"); }
                return true;
            }
        }
        if (log) { System.out.println(Int4_1_TextColor.YELLOW + "No negative durations detected" + Int4_1_TextColor.RESET + "\n"); }
        return false;
    }

    public void computeRanks(boolean log){
        if (hasCycle(false)) {
            if (log) { System.out.println(Int4_1_TextColor.RED + "Graph has a cycle, cannot compute ranks" + Int4_1_TextColor.RESET); }
            return;
        }

        Int4_1_Graph graph = new Int4_1_Graph(this);
        int rank = 0;

        while (!graph.vertices.isEmpty()) {
            // Find vertices with no predecessors
            List<Int4_1_Vertex> noPredecessors = new ArrayList<>();

            for (Int4_1_Vertex v : graph.vertices) {
                if (v.predecessors.isEmpty()) {
                    noPredecessors.add(v);
                }
            }

            // Remove vertices with no predecessors from copy and set the rank to original graph
            for (Int4_1_Vertex v : noPredecessors) {
                graph.removeVertex(v);
                this.getVertex(v.id).setRank(rank);
                if (log) { System.out.println("Removing vertex " + Int4_1_TextColor.CYAN + v.id + Int4_1_TextColor.RESET + " with rank " + Int4_1_TextColor.PURPLE + rank + Int4_1_TextColor.RESET); }
            }

            rank++;
        }

    }
    public void computeEarliestTime(boolean log){
        StringBuilder earlTLog = new StringBuilder();

        if (hasCycle(false)) {
            if (log) { System.out.println(Int4_1_TextColor.RED + "Graph has a cycle, cannot compute earliest time" + Int4_1_TextColor.RESET); }
            return;
        }

        // Sort vertices by rank in ascending order
        vertices.sort(Comparator.comparingInt(Int4_1_Vertex::getRank));

        for (Int4_1_Vertex v : vertices){
            // If vertex has no predecessors (i.e. source), set earliest time to 0
            if (v.predecessors.isEmpty()){
                getVertex(v.id).earliestTime = 0;

                earlTLog.append("Vertex " + Int4_1_TextColor.CYAN).append(v.id).append(Int4_1_TextColor.RESET).append(" is the source, setting earliest time to ").append(Int4_1_TextColor.YELLOW).append(0).append(Int4_1_TextColor.RESET).append("\n");
            } else {
                // Else, set the earliest time as the max of the predecessors' earliest time + duration
                int max = 0;

                earlTLog.append("Vertex " + Int4_1_TextColor.CYAN).append(v.id).append(Int4_1_TextColor.RESET).append(", duration ").append(Int4_1_TextColor.RED).append(v.duration).append(Int4_1_TextColor.RESET).append(" predecessors : ");

                for (int predecessor : v.predecessors){
                    earlTLog.append(Int4_1_TextColor.CYAN).append(predecessor).append(Int4_1_TextColor.RESET).append(" (").append(Int4_1_TextColor.YELLOW).append(getVertex(predecessor).earliestTime).append(Int4_1_TextColor.RESET).append("), ");

                    int time = getVertex(predecessor).earliestTime + getVertex(predecessor).duration;
                    if (time > max){
                        max = time;
                    }
                }
                getVertex(v.id).earliestTime = max;

                if (log) {
                    // Remove trailing comma
                    earlTLog.deleteCharAt(earlTLog.length() - 2);
                    earlTLog.append("-> " + Int4_1_TextColor.YELLOW).append(v.earliestTime).append(Int4_1_TextColor.RESET).append("\n");
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
            if (log) { System.out.println(Int4_1_TextColor.RED + "Graph has a cycle, cannot compute latest time" + Int4_1_TextColor.RESET); }
            return;
        }

        // Sort vertices by rank in descending order
        vertices.sort(Comparator.comparingInt(Int4_1_Vertex::getRank).reversed());

        for (Int4_1_Vertex v : vertices){
            // If vertex has no successors (i.e. sink), set the latest time to the earliest time
            if (getSuccessors(v).isEmpty()){
                getVertex(v.id).latestTime = getVertex(v.id).earliestTime;
                lateTLog.append("Vertex " + Int4_1_TextColor.CYAN).append(v.id).append(Int4_1_TextColor.RESET).append(" is the destination, setting earliest time to its earliest time ").append(Int4_1_TextColor.YELLOW).append(v.earliestTime).append(Int4_1_TextColor.RESET).append("\n");
            } else {
                // Else, set the latest time as the min of the successors' latest time - duration
                int min = Integer.MAX_VALUE;

                lateTLog.append("Vertex " + Int4_1_TextColor.CYAN).append(v.id).append(Int4_1_TextColor.RESET).append(", duration ").append(Int4_1_TextColor.RED).append(v.duration).append(Int4_1_TextColor.RESET).append(" successors : ");

                for (Int4_1_Vertex successor : getSuccessors(v)){
                    lateTLog.append(Int4_1_TextColor.CYAN).append(successor.id).append(Int4_1_TextColor.RESET).append(" (").append(Int4_1_TextColor.GREEN).append(successor.latestTime).append(Int4_1_TextColor.RESET).append("), ");

                    int time = getVertex(successor.id).latestTime;
                    if (time < min){
                        min = time;
                    }
                }
                getVertex(v.id).latestTime = min - getVertex(v.id).duration;

                if (log) {
                    // Remove trailing comma
                    lateTLog.deleteCharAt(lateTLog.length() - 2);
                    lateTLog.append("-> " + Int4_1_TextColor.GREEN).append(v.latestTime).append(Int4_1_TextColor.RESET).append("\n");
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

        for (Int4_1_Vertex v : this.vertices){
            duration.append(v.duration).append("\t");
            task.append(v.id).append("\t");
            earliest.append(v.earliestTime).append("\t");
            latest.append(v.latestTime).append("\t");
            total_float.append(v.latestTime - v.earliestTime).append("\t");
        }
        System.out.println("Task\t\t" + Int4_1_TextColor.CYAN + task + Int4_1_TextColor.RESET);
        System.out.println("Duration\t" + Int4_1_TextColor.RED + duration + Int4_1_TextColor.RESET);
        System.out.println("Earliest\t" + Int4_1_TextColor.YELLOW + earliest + Int4_1_TextColor.RESET);
        System.out.println("Latest\t\t" + Int4_1_TextColor.GREEN + latest + Int4_1_TextColor.RESET);
        System.out.println("Float\t\t" + Int4_1_TextColor.PURPLE + total_float + Int4_1_TextColor.RESET);
    }

    public void displayCriticalPath(){
        // Path with vertices with 0 total float
        StringBuilder path = new StringBuilder();
        path.append("Critical path : ");

        // Start at vertex 0, add to path
        Int4_1_Vertex current = getVertex(0);
        path.append(Int4_1_TextColor.CYAN).append(current.id).append(Int4_1_TextColor.RESET).append(" -> ");

        while (true){
            List<Int4_1_Vertex> successors = getSuccessors(current);
            if (successors.size() == 1 && successors.get(0).latestTime - successors.get(0).earliestTime == 0){
                current = successors.get(0);
                path.append(Int4_1_TextColor.CYAN).append(current.id).append(Int4_1_TextColor.RESET).append(" -> ");
            } else if (successors.size() > 1) {
                // If multiple successors with 0 total float, choose the one with the smallest rank (we want the longest path)
                // If multiple successors with the same rank, display that it is one of the critical paths

                Int4_1_Vertex min = null;
                for (Int4_1_Vertex v : successors){
                    if (v.latestTime - v.earliestTime == 0){
                        if (min == null){
                            min = v;
                        } else {
                            if (v.getRank() < min.getRank()){
                                min = v;
                            }
                        }
                    }
                }
                if (min != null){
                    current = min;
                    path.append(Int4_1_TextColor.CYAN).append(current.id).append(Int4_1_TextColor.RESET).append(" -> ");
                } else {
                    break;
                }

            } else {
                break;
            }
        }

        // Remove trailing arrow
        path.delete(path.length() - 4, path.length());
        System.out.println(path);
    }

    private List<Int4_1_Vertex> getSuccessors(Int4_1_Vertex vertex){
        List<Int4_1_Vertex> successors = new ArrayList<>();

        for (Int4_1_Vertex v : vertices) {
            if (v.predecessors.contains(vertex.id)) {
                successors.add(v);
            }
        }

        return successors;
    }

    private Int4_1_Vertex getVertex(int id){
        Int4_1_Vertex vertex = null;
        for (Int4_1_Vertex v : vertices) {
            if (v.id == id) {
                vertex = v;
                break;
            }
        }
        return vertex;
    }

    private void removeVertex(Int4_1_Vertex vertex){
        // Remove vertex from predecessors
        for (Int4_1_Vertex v : vertices) {
            v.predecessors.remove((Integer) vertex.id);
        }
        // Remove vertex
        vertices.remove(vertex);
    }

    public void displayTriplets(){
        StringBuilder sb = new StringBuilder();
        sb.append(Int4_1_TextColor.PURPLE).append(vertices.size()).append(Int4_1_TextColor.RESET).append(" vertices").append("\n");

        int edges = 0;
        for (Int4_1_Vertex v : vertices) {
            edges += v.predecessors.size();
        }
        sb.append(Int4_1_TextColor.PURPLE).append(edges).append(Int4_1_TextColor.RESET).append(" edges").append("\n");

        for (Int4_1_Vertex v : vertices) {
            if (!getSuccessors(v).isEmpty()) {
                for (Int4_1_Vertex successor : getSuccessors(v)) {
                    // id -> successor = duration
                    sb.append(Int4_1_TextColor.CYAN).append(v.id).append(Int4_1_TextColor.RESET).append(" -> ")
                            .append(Int4_1_TextColor.GREEN).append(successor.id).append(Int4_1_TextColor.RESET).append(" = ")
                            .append(Int4_1_TextColor.YELLOW).append(v.duration).append(Int4_1_TextColor.RESET).append("\n");
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
        for (Int4_1_Vertex v : vertices) {
            for (Int4_1_Vertex successor : getSuccessors(v)) {
                valueMatrix[v.id][successor.id] = Int4_1_TextColor.YELLOW + v.duration + Int4_1_TextColor.RESET;
            }
        }

        // Print column headers
        System.out.print(Int4_1_TextColor.GREEN + "\t");
        for (int i = 0; i < vertices.size(); i++) {
            System.out.print(i + "\t");
        }
        System.out.println(Int4_1_TextColor.RESET);

        // Print row headers and values
        for (int i = 0; i < vertices.size(); i++){
            System.out.print(Int4_1_TextColor.CYAN + i + Int4_1_TextColor.RESET + "\t");
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
        for (Int4_1_Vertex v : vertices) {
            sb.append(v.toString()).append("\n");
        }
        return sb.toString();
    }
}
