import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean run = true;
        while (run){
            try {
                System.out.print("Enter constraint table to use (ex: table 1): ");
                String table = scanner.nextLine();
                Graph graph = new Graph("src/tests/" + table + ".txt");

                System.out.println("Reading graph from table (adding alpha and omega):");
                graph.displayTriplets();
                graph.displayValueMatrix();

                if (!graph.hasCycle(true) && !graph.hasNegativeDuration(true)){
                    System.out.println(TextColor.GREEN + "This graph is a scheduling graph" + TextColor.RESET + "\n");

                    System.out.println("Calculating ranks by successively removing vertices with no predecessors:");
                    graph.computeRanks(true);
                    System.out.println();

                    System.out.println("Computing earliest times of each vertex:");
                    graph.computeEarliestTime(true);
                    System.out.println();

                    System.out.println("Computing latest times of each vertex:");
                    graph.computeLatestTime(true);
                    System.out.println();

                    graph.displayTimes();
                    System.out.println();

                    System.out.println("Displaying critical path:");
                    graph.displayCriticalPath();
                    System.out.println();
                }else{
                    System.out.println(TextColor.RED + "This graph is not a scheduling graph" + TextColor.RESET + "\n");
                }

            }catch (Exception e){
                System.out.println(TextColor.RED + "Error: " + e.getMessage() + TextColor.RESET);
            }
            finally {
                System.out.print("Do you want to continue? (y/n): ");
                String answer = scanner.nextLine();
                if (answer.equals("n")){
                    run = false;
                }
            }
        }
        System.exit(0);

    }
}