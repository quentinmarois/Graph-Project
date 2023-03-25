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

                graph.displayValueMatrix();

                if (!graph.hasCycle(true) && !graph.hasNegativeDuration(true)){
                    System.out.println(TextColor.GREEN + "This graph is a scheduling graph" + TextColor.RESET);
                    System.out.println("Ranks:");
                    System.out.println(graph);

                    graph.displayTimes();
                    System.out.println();
                    graph.displayCriticalPath();

                }else{
                    System.out.println(TextColor.RED + "This graph is not a scheduling graph" + TextColor.RESET);
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