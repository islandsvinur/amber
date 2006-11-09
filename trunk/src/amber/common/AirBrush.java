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

/**
 * A small layer between JavaOpenAIR and Amber. It holds state information about
 * the connection with the Psyclone server and such.
 * 
 * @author christian
 * 
 */
public class AirBrush implements Runnable {
    /**
     * the connection object
     */
    final private JavaAIRPlug plug;

    /**
     * thread handling incoming messages
     */
    private Thread thread;

    /**
     * the callback object
     */
    private AirBrushCallable callback;

    /**
     * the string containing the name of the running module
     */
    private String moduleName;

    /**
     * Creates and initializes AirBrush and connects to the Psyclone server.
     * 
     * @param module
     *            the name of the module to connect
     * @param hostname
     *            hostname of the Psyclone server
     * @param port
     *            port number of the Psyclone server
     */
    public AirBrush(String module, String hostname, Integer port) {
        moduleName = module;
        plug = new JavaAIRPlug(moduleName, hostname, port);
        if (plug.init()) {
            System.out.println("Successfully connected to Psyclone: "
                    + hostname + ":" + port);
            System.out.println("  module name: " + moduleName);
        } else {
            System.err.println("Could not connect to Psyclone!");
        }
    }

    /**
     * Start listening for incoming messages
     */
    public void startListening() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        } else
            System.err.println("Thread already started.");
    }

    /**
     * Stop listening and disconnect from Psyclone
     */
    public void stopListening() {
        thread = null;
        plug.disconnectNetworkConnections();
        plug.destroy();
    }

    /**
     * Open connection a two-way connection with the whiteboard
     * 
     * @param wb
     *            name of the whiteboard to connect with
     * @return true if the whiteboard was succesfully opened
     */
    public boolean openWhiteboard(String wb) {
        return plug.openTwoWayConnectionTo(wb);
    }

    /**
     * Post a message to Psyclone
     * 
     * @param msg
     */
    public void postMessage(Message msg) {
        plug.postOutputMessage(msg);
    }

    /**
     * Set the callback object for handling incoming messages
     * 
     * @param cb
     *            the callback object
     * @see AirBrushCallable
     */
    public void setCallbackObject(AirBrushCallable cb) {
        callback = cb;
    }

    /**
     * Set a parameter in Psyclone for the current module
     * 
     * @param key
     * @param value
     */
    public void setParameter(String key, String value) {
        plug.setParameterString(key, value);
    }

    /**
     * @param key
     * @return true when the parameter named key is present
     */
    public boolean hasParameter(String key) {
        return plug.hasParameter(key);
    }

    /**
     * Get the parameter with type String
     * 
     * @param key
     * @return the parameter stored under key
     */
    public String getParameterString(String key) {
        return plug.getParameterString(key);
    }

    /**
     * Set a parameter in Psyclone for the current module
     * 
     * @param key
     * @param value
     */
    public void setParameter(String key, Integer value) {
        plug.setParameterInteger(key, value);
    }

    /**
     * Get the parameter with type Integer
     * 
     * @param key
     * @return the parameter stored under key
     */
    public Integer getParameterInteger(String key) {
        return plug.getParameterInteger(key);
    }

    /**
     * Set a parameter in Psyclone for the current module
     * 
     * @param key
     * @param value
     */
    public void setParameter(String key, Double value) {
        plug.setParameterDouble(key, value);
    }

    /**
     * Get the parameter with type Double
     * 
     * @param key
     * @return the parameter stored under key
     */
    public Double getParameterDouble(String key) {
        return plug.getParameterDouble(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    public void run() {
        Message message;
        Thread current = Thread.currentThread();

        while (current == thread) {
            if ((message = plug.waitForNewMessage(100)) != null) {
                if (!callback.airBrushReceiveMessage(message)) {
                    System.out.println("Note: Unhandled callback for: "
                            + message.type + " to: " + message.to);
                }
            }
        }
    }
}