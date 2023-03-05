import java.util.ArrayList;
import java.util.List;

public class Vertex {
    public int id;
    public int duration;
    public List<Integer> predecessors;

    private int rank = -1;

    public Vertex(String tableLine) {
        String[] line = tableLine.split(" ");
        this.id = Integer.parseInt(line[0]);
        this.duration = Integer.parseInt(line[1]);

        this.predecessors = new ArrayList<>();
        for (int i = 2; i < line.length; i++) {
            this.predecessors.add(Integer.parseInt(line[i]));
        }
    }

    public Vertex(int id, int duration) {
        this.id = id;
        this.duration = duration;
        this.predecessors = new ArrayList<>();
    }

    public Vertex(Vertex vertex) {
        this.id = vertex.id;
        this.duration = vertex.duration;
        this.predecessors = new ArrayList<>(vertex.predecessors);
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
        sb.append("ID: ").append(TextColor.CYAN).append(this.id).append(TextColor.RESET);


        if (this.rank != -1) {
            sb.append(" Rank: ").append(TextColor.PURPLE).append(this.rank).append(TextColor.RESET);
        }

        sb.append(" Duration: ").append(TextColor.YELLOW).append(this.duration).append(TextColor.RESET);

        if (this.predecessors.size() > 0) {
            sb.append(" Predecessors: ");
        }

        sb.append(TextColor.GREEN);

        for (int i = 0; i < this.predecessors.size(); i++) {
            sb.append(this.predecessors.get(i));
            if (i != this.predecessors.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(TextColor.RESET);
        return sb.toString();
    }
}
