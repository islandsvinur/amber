/*
 * Author: Christian Luijten <christian@luijten.org>
 * Description: 
 */

import amber.sieve.KeywordSpotter;
import java.io.*;

/* Starts the parser */
public class Sieve {
    public static void main(String args[]) {
        if (args.length == 1) {
            
            try {

                FileInputStream fp = new FileInputStream(args[0]);
                DataInputStream in = new DataInputStream(fp);

                while (in.available() != 0) {
                    System.out.println(in.readLine());
                }

                // KeywordSpotter spotter = new KeywordSpotter();
            } catch (Exception e) {
                System.err.println("File input error");
            }

        } else {
            System.err.println("Argument invalid");
        }
    }
}
