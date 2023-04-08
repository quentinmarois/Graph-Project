import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

public class Int4_1_Main {

    private static final String TESTS_PATH = "tests/";
    private static final String TRACE_FILE = "trace.txt";
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        // Setup trace file and outputs
        FileOutputStream traceFile = new FileOutputStream(TRACE_FILE, true);
        traceFile.write("\n\n".getBytes());
        Int4_1_MultiOutputStream multiOut = new Int4_1_MultiOutputStream(System.out, traceFile);
        PrintStream printStream = new PrintStream(multiOut);
        System.setOut(printStream);

        boolean run = true;
        while (run){
            try {
                System.out.print("Enter constraint table to use (ex: table 1): ");
                String table = scanner.nextLine();
                traceFile.write((table + '\n').getBytes());

                Int4_1_Graph graph = new Int4_1_Graph( TESTS_PATH + table + ".txt");

                System.out.println("Reading graph from table (adding alpha and omega):");
                graph.displayTriplets();
                graph.displayValueMatrix();

                if (!graph.hasCycle(true) && !graph.hasNegativeDuration(true)){
                    System.out.println(Int4_1_TextColor.GREEN + "This graph is a scheduling graph" + Int4_1_TextColor.RESET + "\n");

                    System.out.println("Calculating ranks by successively removing vertices with no predecessors:");
                    graph.computeRanks(true);
                    System.out.println();

                    System.out.println("Computing earliest times of each vertex:");
                    graph.computeEarliestTime(true);

                    System.out.println("Computing latest times of each vertex:");
                    graph.computeLatestTime(true);

                    graph.displayTimes();
                    System.out.println();

                    System.out.println("Displaying critical path:");
                    graph.displayCriticalPath();
                    System.out.println();
                }else{
                    System.out.println(Int4_1_TextColor.RED + "This graph is not a scheduling graph" + Int4_1_TextColor.RESET + "\n");
                }

            }catch (Exception e){
                System.out.println(Int4_1_TextColor.RED + "Error: " + e.getMessage() + Int4_1_TextColor.RESET);
            }
            finally {
                System.out.print("Do you want to continue? (y/n): ");
                String answer = scanner.nextLine();
                traceFile.write((answer + '\n').getBytes());
                if (answer.equals("n")){
                    run = false;
                }
            }
        }
        System.exit(0);

    }
}