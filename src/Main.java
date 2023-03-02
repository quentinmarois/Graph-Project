public class Main {
    public static void main(String[] args) {
        String testPath = "src/tests/subject_table.txt";
        Graph graph = new Graph(testPath);
        System.out.println(graph);
        graph.displayTriplets();
        graph.displayValueMatrix();

    }
}