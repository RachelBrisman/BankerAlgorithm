import java.util.ArrayList;
import java.util.Random;

public class Program
{

    final static int NUM_PROCS = 6; // How many concurrent processes
    final static int TOTAL_RESOURCES = 30; // Total resources in the system
    final static int MAX_PROC_RESOURCES = 13; // Highest amount of resources any process could need
    final static int ITERATIONS = 30; // How long to run the program
    static Random rand = new Random();
    static int totalHeldResources;
    static boolean found;

    public static void main(String[] args)
    {

        // The list of processes:
        ArrayList<Proc> processes = new ArrayList<>();
        for (int i = 0; i < NUM_PROCS; i++)
        {
            processes.add(new Proc(MAX_PROC_RESOURCES - rand.nextInt(3))); // Initialize to a new Proc, with some small range for its max
        }

        // Run the simulation:
        for (int i = 0; i < ITERATIONS; i++)
        {
            // loop through the processes and for each one get its request
            for (int j = 0; j < processes.size(); j++)
            {

                // Get the request
                int currRequest = processes.get(j).resourceRequest(TOTAL_RESOURCES - totalHeldResources);

                // just ignore processes that don't ask for resources
                if (currRequest == 0)
                {
                    System.out.println("\nProcess " + j + " did not request anything.\n");
                    continue;
                }
                // if the process finished, note it then jump down to print the status
                else if (currRequest < 0)
                {
                    System.out.println("\nProcess " + j + " finished, returned " + processes.get(j).getMaxResources() + ".");
                }
                // otherwise, figure out if it is safe to grant the request
                else
                {
                    System.out.println("\nProcess " + j + " requested " + currRequest + ". ");
                    // Here you have to enter code to determine whether or not the request can be granted,
                    // and then grant the request if possible. Remember to give output to the console
                    // this indicates what the request is, and whether or not its granted.
                    //an arraylist to test the 'what if' without meddling with the processes

                    // create an identical list of processes to test on
                    ArrayList<Proc> substitutes = new ArrayList<>();
                    for (int m = 0; m < processes.size(); m++) {
                        Proc p = new Proc(processes.get(m).getMaxResources());
                        p.addResources(processes.get(m).getHeldResources());
                        substitutes.add(p);
                    }

                    //grant the request to the processes clone
                    substitutes.get(j).addResources(currRequest);

                    //add that to the total resources
                    totalHeldResources += currRequest;

                    System.out.println("Checking if it will be safe...");

                    // check if this substitute arrayList will be safe now that the request was granted
                    boolean isSafe = checkIfSafe(substitutes);

                    // if it is safe, grant the resources to the process
                    if (isSafe) {
                        System.out.println("SAFE - request granted.");
                        processes.get(j).addResources(currRequest);
                    } else {
                        System.out.println("UNSAFE - request denied.");
                    }
                }

                // get the actual number of held resources
                totalHeldResources = 0;
                for (int k = 0; k < processes.size(); k++)
                {
                    totalHeldResources += processes.get(k).getHeldResources();
                }

                // At the end of each iteration, give a summary of the current status:
                System.out.println("\n***** STATUS *****");
                System.out.println("Total Available: " + (TOTAL_RESOURCES - totalHeldResources));
                for (int p = 0; p < processes.size(); p++)
                {
                    System.out.println("Process " + p + " holds: " + processes.get(p).getHeldResources() + ", max: " +
                            processes.get(p).getMaxResources() + ", claim: " +
                            (processes.get(p).getMaxResources() - processes.get(p).getHeldResources()));
                }
                System.out.println("***** STATUS *****\n");
            }
        }

    }

    // this method checks if the arrayList of processes is in a safe state
    public static boolean checkIfSafe(ArrayList<Proc> sub)
    {
        // while there are still processes
        while (sub.size() != 0) {
            found = false;

            // loop through the processes
            for (int r = 0; r < sub.size(); r++) {

                // how many resources the process needs
                int iNeed = sub.get(r).getMaxResources() - sub.get(r).getHeldResources();
                // if it needs less than the amount available
                if (iNeed <= TOTAL_RESOURCES - totalHeldResources)
                {
                    // grants the requests - finishing the process and returning its resources
                    totalHeldResources -= sub.get(r).getHeldResources();

                    //removes the process from the simulation
                    sub.remove(r);
                    found = true;
                }
            }
            if (!found)
            {
                return false;
            }
        }
        return true;
    }

}