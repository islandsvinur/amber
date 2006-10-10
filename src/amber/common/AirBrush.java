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

import com.cmlabs.air.*;

public class AirBrush implements Runnable {
    private JavaAIRPlug plug = null;
    private Thread thread;

    private AirBrushCallable callback;

    // private Message inMsg;
    // private Message outMsg;

    private void checkConnection() throws Exception {
        if (plug == null) { throw new Exception("No connection with Psyclone."); }
        if (callback == null) { throw new Exception("No Callback object given."); }
    }
    
    public AirBrush() {
        
    }
    
    public AirBrush(String plugname, String hostname, Integer port) {
        connect(plugname, hostname, port);
    }
    
    public void connect(String plugname, String hostname, Integer port) {
        System.out.println("Creating connection with Psyclone at " + hostname + ":" + port + " with service name " + plugname);
        plug = new JavaAIRPlug(plugname, hostname, port);
    }

    public boolean connect() throws Exception {
        checkConnection();

        if (!plug.init()) {
            System.out.println("Could not connect to the Server ...");
            return false;
        }
        return true;
    }
    public void connectAndOpenWhiteboard(String wb) throws Exception {
        // Connect to Psyclone
        connect();
        // Try to open the whiteboard
        if (!openWhiteboard(wb)) {
            System.err
                    .println("Could not open callback connection to whiteboard.");
        } else {
            System.out.println("Connected to whiteboard.");
        }
        // Start listening for messages coming onto the whiteboard
        startListening();
    }
    
    public void startListening() throws Exception {
        if (thread != null) {
            throw new Exception("Already running!");
        }
        thread = new Thread(this);
        thread.start();
    }
    
    public void stopListening() {
        thread = null;
    }

    public boolean openWhiteboard(String wb) throws Exception {
        checkConnection();

        if (!plug.openTwoWayConnectionTo(wb)) {
            return false;
        }
        return true;
    }
    
    public void postMessage(Message msg) {
        plug.postOutputMessage(msg);
    }

    public void setCallbackObject(AirBrushCallable cb) {
        callback = cb;
    }

    public void run() {
        Message message;

        while (Thread.currentThread() == thread) {
            if ((message = plug.waitForNewMessage(100)) != null) {
                if (!callback.airBrushReceiveMessage(message)) {
                    System.out.println("Note: Unhandled callback for: " + message.type + " to: " + message.to);
                }
            }
        }
    }
}