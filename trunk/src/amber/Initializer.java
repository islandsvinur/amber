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

import com.cmlabs.air.Message;

import amber.common.AirBrush;
import amber.common.AirBrushCallable;

public class Initializer implements AirBrushCallable {
    private AirBrush ab;

    /**
     * @param args
     */
    public Initializer(String[] args) {
        try {
            ab = new AirBrush("Initializer", "localhost", 10000);
            ab.setCallbackObject(this);
            ab.openWhiteboard("WB.Control");
            ab.startListening();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public boolean airBrushReceiveMessage(Message msg) {
        return false;
    }

    private void sendMessages() {
        Message msg;

        /* msg = new Message();
        msg.to = "WB.Control";
        msg.cc = "Module.Sieve.KeywordSpotter.Anonymous";
        msg.type = "Keywords.Author";
        msg.content = "Christian";
        ab.postMessage(msg);
        System.out.println(" Initializing Sieve.KeywordSpotter (2)"); */
        
        msg = new Message();
        msg.to = "WB.Control";
        msg.cc = "Module.Sieve.KeywordSpotter.MacBook";
        msg.type = "Sieve.Topic";
        msg.content = "macbook";
        ab.postMessage(msg);
        System.out.println(" Initializing Sieve.KeywordSpotter (MacBook)");

        msg = new Message();
        msg.to = "WB.Control";
        msg.cc = "Module.Sieve.KeywordSpotter.MacBook";
        msg.type = "Keywords.Contents";
        msg.content = ".*macbook.*";
        ab.postMessage(msg);
        System.out.println(" Initializing Sieve.KeywordSpotter (MacBook)");
        
        msg = new Message();
        msg.to = "WB.Control";
        msg.cc = "Module.Sieve.KeywordSpotter.iPod";
        msg.type = "Sieve.Topic";
        msg.content = "ipod";
        ab.postMessage(msg);
        System.out.println(" Initializing Sieve.KeywordSpotter (iPod)");

        msg = new Message();
        msg.to = "WB.Control";
        msg.cc = "Module.Sieve.KeywordSpotter.iPod";
        msg.type = "Keywords.Contents";
        msg.content = ".*ipod.*";
        ab.postMessage(msg);
        System.out.println(" Initializing Sieve.KeywordSpotter (iPod)");
        
        msg = new Message();
        msg.to = "WB.Control";
        msg.cc = "Module.Crawler.RSS.Anonymous";
        msg.type = "Feed.RSS";
        msg.content = "http://gathering.tweakers.net/rss.php/list_topics/76";
        // msg.content = "http://forum.macosx.nl/rss/";
        ab.postMessage(msg);
        System.out.println(" Initializing Crawler.RSS");

        msg = new Message();
        msg.to = "WB.Control";
        msg.type = "All.Start";
        ab.postMessage(msg);
        System.out.println(" Sending start signal!");

    }
    
    public void stop() {
        ab.stopListening();
    }
    

    public static void main(String[] args) {
        System.out.println("Starting up...");
        Initializer i = new Initializer(args);
        System.out.println("Sending messages...");
        i.sendMessages();
        System.out.println("Shutting down...");
        i.stop();
        System.out.println("Done!");
    }

}
