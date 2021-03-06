//_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/
//_/_/
//_/_/  Ambient Earth
//_/_/  Christian Luijten <christian@luijten.org>
//_/_/
//_/_/  Copyright(c)2006 Center for Analysis and Design of Intelligent Agents
//_/_/                   Reykjavik University
//_/_/                   All rights reserved
//_/_/
//_/_/                   http://cadia.ru.is/
//_/_/
//_/_/  Redistribution and use in source and binary forms, with or without
//_/_/  modification, is permitted provided that the following conditions 
//_/_/  are met:
//_/_/
//_/_/  - Redistributions of source code must retain the above copyright notice,
//_/_/    this list of conditions and the following disclaimer.
//_/_/
//_/_/  - Redistributions in binary form must reproduce the above copyright 
//_/_/    notice, this list of conditions and the following disclaimer in the 
//_/_/    documentation and/or other materials provided with the distribution.
//_/_/
//_/_/  - Neither the name of its copyright holders nor the names of its 
//_/_/    contributors may be used to endorse or promote products derived from 
//_/_/    this software without specific prior written permission.
//_/_/
//_/_/  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 
//_/_/  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT 
//_/_/  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A 
//_/_/  PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
//_/_/  OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
//_/_/  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
//_/_/  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
//_/_/  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
//_/_/  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING 
//_/_/  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
//_/_/  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//_/_/
//_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/

package amber.common;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * To be called directly from the commandline and is in fact the preferred way
 * to start any Amber module. Start with -h command line switch to get help.
 * 
 * @author christian
 * 
 */
public abstract class Launcher {

    /**
     * @param args
     * @return an CommandLine object containing the parsed command line string
     * @throws ParseException
     */
    private static CommandLine parseCommandLine(String[] args) {
        Options o = new Options();

        o.addOption("m", "module", true, "The module to be launched.");
        o.addOption("h", "help", false, "Show help.");
        o.addOption("s", "host", true, "Hostname of the Psyclone server.");
        o.addOption("p", "port", true, "Port number of the Psyclone server.");
        o
                .addOption(
                        "i",
                        "identifier",
                        true,
                        "Gives an identifier to the module to be started."
                                + " Allows controlling applications to send direct messages.");

        BasicParser parser = new BasicParser();
        CommandLine cl;
        try {
            cl = parser.parse(o, args);
            if (cl.hasOption('h')) {
                printHelp(o);
            }
            return cl;
        } catch (ParseException e) {
            printHelp(o);
        }

        return null;
    }

    /**
     * Prints help about the valid command line switches and options
     * 
     * @param o
     *            the options object
     */
    private static void printHelp(Options o) {
        HelpFormatter f = new HelpFormatter();
        f.printHelp("Launcher", o);
    }

    /**
     * Launch method
     * 
     * @param args
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static void main(String args[]) {
        CommandLine cl;
        cl = parseCommandLine(args);
        if (cl == null) {
            System.exit(-1);
        }
        String id, hostname;
        Integer port;

        id = cl.getOptionValue("i", "Anonymous");

        hostname = cl.getOptionValue("s", "localhost");
        port = Integer.valueOf(cl.getOptionValue("p", "10000"));

        System.out.println("Going to start some module");

        if (cl.hasOption("m")) {
            String modName = cl.getOptionValue("m");

            Module app = null;
            if (modName.equals("RSS")) {
                app = new amber.crawler.RSS(id, hostname, port);
            } else if (modName.equals("KeywordSpotter")) {
                app = new amber.sieve.KeywordSpotter(id, hostname, port);
            } else if (modName.equals("FullScreen")) {
                app = new amber.showoff.FullScreen(id, hostname, port);
            }

            if (app != null)
                app.start();

        }

        System.out.println("Module started");

    }
}
