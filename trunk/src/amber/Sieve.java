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

package amber;

import com.cmlabs.air.*;

/* Starts the parser */
public class Sieve extends amber.common.Object {

    public static void main(String args[]) {
        JavaAIRPlug plug;
        Message triggerMsg;
        Message msg;

        plug = new JavaAIRPlug("Sieve", "localhost", 10000);

        if (!plug.init()) {
            System.out.println("Could not connect to the Server ...");
            System.exit(0);
        }

        while (true) {
            if ((triggerMsg = plug.waitForNewMessage(50)) != null) {
                System.out.println(triggerMsg.postedTime.printTime() + ": "
                        + "Reader received message " + triggerMsg.type
                        + " from " + triggerMsg.from);
                msg = new Message("", "", "SelectedStory");
                plug.addOutputMessage(msg);
                msg = new Message("", "", "NextStory");
                plug.addOutputMessage(msg);
                plug.sendOutputMessages();
            } else {
                if (!plug.isServerAvailable())
                    System.out.println("Psyclone is avail, but sleeping.");
                else
                    System.out.println("Psyclone is not available.");
            }
        }

        /*
         * if (args.length == 1) {
         * 
         * try {
         * 
         * FileInputStream fp = new FileInputStream(args[0]); BufferedReader in =
         * new BufferedReader(new InputStreamReader(fp));
         * 
         * KeywordSpotter spotter = new KeywordSpotter();
         * spotter.setInputStream(in); spotter.sieve(); spotter.destroy(); }
         * catch (Exception e) { System.err.println("File input error"); }
         *  } else { System.err.println("Argument invalid"); }
         */
    }
}
