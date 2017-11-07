import java.util.Scanner;

/**
 *  <h1><u>Parser.java</u></h1>
 *  This class is used as the main file to start our program. It grabs input
 *  from the terminal after prompting and passes the input string to the
 *  Evaluator class to be evaluated. If the Evaluator's evalExpr function returns
 *  true, then the string is a valid expression (according to the EBNF grammar
 *  decided in Evaluator.java), the program will continuously ask for input until
 *  no input is given
 *
 * @author Lucas Carpenter
 * @version {@value Parser#MAJOR}.{@value Parser#MINOR}
 * | Date: 10/21/2017
 */

public class Parser {

    /** The major revision number of this build; 0 indicates pre-finished, 1 indicates released**/
    private static final int MAJOR = 1;
    /** The minor revision number of this build; increments with each commit, resets on a major increment **/
    private static final int MINOR = 0;

    /**
     * The entry point of our program, creates a Scanner and Evaluator, finds whether the -t flag is triggered and
     * prompts the command line for input, and evaluates the input through the Evaluator class till no input is given
     *
     * @param args The arguments passed through from the command line
     */
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        String str = "im not empty..";
        Evaluator e = new Evaluator();
        while(!str.isEmpty()) {
            System.out.print("Enter expression: ");
            str = s.nextLine();
            if(str.isEmpty()) {
                break;
            }
            System.out.print("\""+str+ "\" ");
            if(e.evaluate(str)) {
                System.out.println("is a valid expression");
            } else {
                System.out.println("is not a valid expression.");
            }
            if(args.length > 0 && args[0].equals("-t")) {
                e.printTokens();
            }
        }
    }
}
