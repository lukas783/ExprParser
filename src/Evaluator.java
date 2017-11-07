import java.util.ArrayList;

/**
 *  <h1><u>Evaluator.java</u></h1>
 *  This class is used to evaluate whether or not a given string is an expression
 *  using an EBNF grammar shown below
 *  <pre>
 *  {@code
 *  <expr> -> <term> { <addop> <term> }
 *  <term> -> <factor> { <mulop> <factor> }
 *  <factor> -> <integer> | <float> | <id> | '(' <expr> ')' | [-] <factor>
 *  <integer> -> <digit> { <digit> }
 *  <float> -> <integer> . <integer>
 *  <id> -> <letter> { <letter> | <digit> }
 *  <letter> -> A | B | C | D | E | F | G | H | I | J | K | L | M |
 *              N | O | P | Q | R | S | T | U | V | W | X | Y | Z |
 *              a | b | c | d | e | f | g | h | i | j | k | l | m |
 *              n | o | p | q | r | s | t | u | v | w | x | y | z | _
 *  <digit> -> 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
 *  <addop> -> + | -
 *  <mulop> -> * | / | %
 *  }
 *  </pre>
 *  This is achieved by splitting each input string into tokens and verifying each split token
 *  satisfies the requirements of the EBNF grammar, if at any point the grammar isn't followed
 *  then a false flag will propagate up recursively to the original evalExpr statement and return
 *  a final false out, if no false is ever returned then the statement will be considered true.
 *
 * @author Lucas Carpenter
 * @version {@value Parser#MAJOR}.{@value Parser#MINOR}
 * | Date: 10/21/2017
 */

class Evaluator {

    /** A list of all tokens **/
    private ArrayList<String> tokenList;
    /** The string we will evaluate **/
    private String evalString;

    /**
     * Class-Constructor, used to create an instance of this class
     */
    Evaluator() {  }

    void printTokens() {
        System.out.print("Tokens: ");
        for(int i = 0; i < tokenList.size(); i++) {
            System.out.print(tokenList.get(i));
            if(i+1 < tokenList.size())
                System.out.print(", ");
        }
        System.out.println("");
    }

    /**
     * evalExpr is the only visible function in this class, the function accepts an input string and splits the
     * string into tokens to evaluate if the input string is a valid expression that satisfies the EBNF grammar
     * {@code <expr> -> <term> { <addop> <term> }}
     *
     * @param s an input string to tokenize and evaluate as an expression
     * @return Whether the evaluated string is a valid expression or not
     **/
    boolean evaluate(String s) {
        //Initialize a new token list and assign our evalString, then evaluate the string as an expression
        tokenList = new ArrayList<String>();
        s = s.trim();
        evalString = s;
        return evaluateExpression();
    }

	/**
	 * evaluateExpression evaluates the input string as an expression or not. To be an expression, the function must
	 * follow the form {@code <expr> -> <term> { <addop> <term> }}
	 *
	 * @return Whether or not evalString is a valid expression
	 **/
    private boolean evaluateExpression() {
        //Handle an empty expression '()' and evaluate our initial term
        evalString = evalString.trim();
        if(!evaluateTerm()) {
            return false;
        }
        evalString = evalString.trim();
        //If an addop exists, add it and evaluate the next term, looping till no more addops exist
        while(!evalString.equals("") && peek(evalString, "+-") ) {
            tokenize(0, 1, true);
            evalString = evalString.trim();
            if(evalString.startsWith("()")) {
                tokenize(0, 1, true);
                tokenize(0, 1, true);
                return false;
            }
            if(evalString.equals("") || !evaluateTerm()) {
                return false;
            }
            evalString = evalString.trim();
        }
        //If we hit here and there's still a string remaining, our expression doesn't follow <term>{<addop><term>}
        if(!evalString.equals("")) {
            return false;
        }
        return true;
    }

    /**
	 * evaluateTerm evaluates whether evalString contains a valid term from position 0 onwards. 
	 * To be a term, the function must follow the form {@code <term> -> <factor> { <mulop> <factor> }}
	 *
	 * @return Whether or not the next portion of evalString contains a valid term
	 **/
    private boolean evaluateTerm() {
        evalString = evalString.trim();
        //Evaluate the factor if a string exists
        if(evalString.equals("") || !evaluateFactor()) {
            return false;
        }
        evalString = evalString.trim();
        //If a mulop token exists, add it and find the next factor, looping till no more mulop tokens
        while(!evalString.equals("") && peek(evalString, "*/%")) {
            tokenize(0, 1, true); // This is a mulop token (guaranteed)
            evalString = evalString.trim();
            if(evalString.equals("") || !evaluateFactor()) {
                return false;
            }
            evalString = evalString.trim();
        }
        return true;
    }

	/**
	 * evaluateFactor evaluates whether the next portion of evalString contains a factor element
	 * from position 0 onwards. To be a valid factor, the evaluated portion must follow the form 
	 * {@code <factor> -> <integer> | <float> | <id> | '('<expr>')' | [-]<factor> }
	 *
	 * @return Whether or not the next portion of evalString contains a factor
	 **/
    private boolean evaluateFactor() {
        //handle if we get a factor with no value (failsafe for just a '-' input)
        if(evalString.equals("")) {// || evalString.startsWith("()")) {
            return false;
        }

        //Evaluate an int or float token
        if(peek(evalString, "0123456789")) {
            int endPoint = evalFloat();
            if (endPoint == -1) {
                // its an int
                endPoint = evalInt();
                tokenize(0, endPoint, true);
                return true;
            } else {
                // its a float
                tokenize(0, evalString.indexOf("."), false);
                tokenize(evalString.indexOf("."), evalString.indexOf(".")+1, false);
                tokenize(evalString.indexOf(".")+1, endPoint, true);
                return true;
            }
        }
        //Evaluate an id
        else if(peek(evalString.toLowerCase(), "abcdefghijklmnopqrstuvwxyz_")) {
            // handle if there's a char or a _ for the first pos, must be an id
            int endPoint = evalID();
            if(endPoint == -1) {
                //something wonky happened, just false our way outta thievalString..
                return false;
            } else {
                tokenize(0, endPoint, true);
                return true;
            }
        }
        //Evaluate a factor for '(' <expr> ')'
        else if(peek(evalString, "(")) {
            tokenize(0, 1, true);
            int parens = 1, endP = -1;
            for(int i = 0; i < evalString.length(); i++) {
                if(evalString.subSequence(i, i+1).equals("(")) {
                    parens += 1;
                }
                if(evalString.subSequence(i, i+1).equals(")")) {
                    parens -= 1;
                }
                if(parens == 0) {
                    endP = i;
                    break;
                }
            }
            if(endP == -1) {
                return false;
            }
            String tempStr = evalString;
            evalString = evalString.substring(0, endP);
            if(!evaluateExpression()) {
                return false;
            } else {
                evalString = tempStr.substring(endP, tempStr.length());
                tokenize(0, 1, true);
                return true;
            }
        }
        //Evaluate a [-] <factor>
        else if(peek(evalString, "-")) {
            tokenize(0, 1, true);
            evalString = evalString.trim();
            return evaluateFactor();
        }
        return false;
    }

    /**
     * Scan from left to right and find the length of an int
	 *
     * @return the length of the digit or a -1 for not an int
     */
    private int evalInt() {
        int i;
        for(i = 0; i < evalString.length(); i++) {
            if(!peek(evalString.substring(i, i+1), "0123456789")) {
                break;
            }
        }
        if(i == 0) {
            return -1;
        } else {
            return i;
        }
    }

    /**
     *  Find the leftmost '.' in our string, if it exists, then verify all values before are of int type
     *  and that at a minimum the first char to the right is of int type and find/return the length of the int
	 *
     * @return the length of the rightmost int, leftmost goes from 0 to indexOf('.'), rightmost goes from '.' till value
     */
    private int evalFloat() {
        int dotIndex = evalString.indexOf(".");
        int i;
        if(dotIndex == -1) {
            return -1;
        }
        //check for int up to the dot
        for(i = 0; i < dotIndex; i++) {
            if(!peek(evalString.substring(i, i+1), "0123456789")) {
                return -1;
            }
        }
        //check if immediate char after dot is a digit
        if(evalString.length() == dotIndex+1 ||  !peek(evalString.substring(dotIndex+1, dotIndex+2), "0123456789")) {
            return -1;
        }
        //evaluate till what point the float ends..
        for(i = dotIndex+1; i < evalString.length(); i++) {
            if(!peek(evalString.substring(i, i+1), "0123456789")) {
                return i;
            }
        }
        return i;
    }

    /**
     * Scan from left to right for appropriate ID values, when an invalid value is scanned, return the index
	 *
     * @return The length of our id
     */
    private int evalID() {
        if(!peek(evalString.toLowerCase(), "abcdefghijklmnopqrstuvwxyz_")) {
            return -1;
        } else {
            int i;
               for(i = 0; i < evalString.length();i++) {
                if(!peek(evalString.toLowerCase().substring(i, i+1), "0123456789abcdefghijklmnopqrstuvwxyz_")) {
                    return i;
                }
            }
            return i;
        }
    }

    /**
     * A helper function useful for peeking at the first character of an origin string to see if it matches
     * any characters in an input string. We scan through the contains string looking for a character match
     * @param orig our original string, we extract the first character of this string to evaluate
     * @param contains a string of characters to find
     * @return whether or not the first character of orig contains a character from the contains string
     */
    private boolean peek(String orig, String contains) {
        if(orig.isEmpty()) {
            return false;
        }
        char c = orig.charAt(0);
        for(int i = 0; i < contains.length();i++) {
            if(contains.charAt(i) == c)
                return true;
        }
        return false;
    }

    /**
     * A helper function useful for adding a token to our list and if the trim flag is true, trimming evalString
     * down from the beginning of the string to the given end value
     * @param beg the leftmost index of evalString to add as a token
     * @param end the rightmost index of evalString to add as a token
     * @param trim a value to determine if we will remove the tokenized string
     */
    private void tokenize(int beg, int end, boolean trim) {
        tokenList.add(evalString.substring(beg, end));
        if(trim)
            evalString = evalString.substring(end, evalString.length());
    }

}