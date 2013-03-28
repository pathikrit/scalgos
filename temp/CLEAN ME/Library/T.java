import java.awt.*;
import java.math.*;
import java.util.regex.*;
import static java.util.Arrays.*;
import static java.util.Collections.*;
import static java.lang.Integer.parseInt;
import static java.lang.System.*;
import static java.lang.Math.*; //careful when overloading std api methods
import java.awt.geom.*;
import java.util.*;
import java.text.*;
import java.io.*;

@SuppressWarnings("unused")
public class T {

	static void realMain() {
	}

    /***************************************************************************/
    public static void main(String cmdLine[]) throws IOException {
        for(Scanner input = getInput(); input.hasNext(); ) {
            int x = input.nextInt();
            realMain();
        }
	}

   	final static int INF = (int)1E9;
    final static double EPS = 1E-9;

	static boolean debugMode = true;
	static void debug(Object ...o) { if(debugMode) err.println("DEBUG: " + deepToString(o)); }
	static void print(Object ...o) {out.println(deepToString(o));}

	static Scanner getInput() {
	    try { return new Scanner(new File(Thread.currentThread().getStackTrace()[1].getClassName() + ".in")); }
    	catch(Throwable t) { debugMode = false; return new Scanner(System.in); }
    }
}
