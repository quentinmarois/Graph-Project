import java.util.ArrayList;
import java.util.List;

public class Int4_1_Vertex {
    public int id;
    public int duration;
    public List<Integer> predecessors;

    public int earliestTime;
    public int latestTime;

    private int rank = -1;

    public Int4_1_Vertex(String tableLine) {
        String[] line = tableLine.split(" ");
        this.id = Integer.parseInt(line[0]);
        this.duration = Integer.parseInt(line[1]);

        this.predecessors = new ArrayList<>();
        for (int i = 2; i < line.length; i++) {
            this.predecessors.add(Integer.parseInt(line[i]));
        }
    }

    public Int4_1_Vertex(int id, int duration) {
        this.id = id;
        this.duration = duration;
        this.predecessors = new ArrayList<>();
    }

    public Int4_1_Vertex(Int4_1_Vertex vertex) {
        this.id = vertex.id;
        this.duration = vertex.duration;
        this.rank = vertex.rank;
        this.predecessors = new ArrayList<>(vertex.predecessors);
        this.earliestTime = vertex.earliestTime;
        this.latestTime = vertex.latestTime;
    }

    public void addPredecessor(int predecessor) {
        if (!this.predecessors.contains(predecessor)) {
            this.predecessors.add(predecessor);
        }
    }

    public void setRank(int rank) {
        if (rank < 0) {
            throw new IllegalArgumentException("Rank must be positive");
        }
        this.rank = rank;
    }

    public int getRank() {
        return this.rank;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(Int4_1_TextColor.CYAN).append(this.id).append(Int4_1_TextColor.RESET);


        if (this.rank != -1) {
            sb.append(" Rank: ").append(Int4_1_TextColor.PURPLE).append(this.rank).append(Int4_1_TextColor.RESET);
        }

        sb.append(" Duration: ").append(Int4_1_TextColor.YELLOW).append(this.duration).append(Int4_1_TextColor.RESET);

        if (this.predecessors.size() > 0) {
            sb.append(" Predecessors: ");
        }

        sb.append(Int4_1_TextColor.GREEN);

        for (int i = 0; i < this.predecessors.size(); i++) {
            sb.append(this.predecessors.get(i));
            if (i != this.predecessors.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(Int4_1_TextColor.RESET);
        return sb.toString();
    }
}
