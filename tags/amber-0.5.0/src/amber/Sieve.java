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

import amber.common.Analysis;
import amber.common.Module;
import amber.common.Story;

import com.cmlabs.air.Message;

/**
 * Provides a means of adding meaning to a story. When a Story message comes in,
 * the Sieve module will generate an Analysis based on it.
 * 
 * @author christian
 * 
 */
public abstract class Sieve extends Module {
    /**
     * topic this Sieve creates Analyses for
     */
    protected String topicString;

    /**
     * the suffix of messages to be sent to the whiteboard
     */
    final private String messageTypeSuffix;

    /**
     * @param name
     *            the name of the module to start
     * @param hostname
     *            hostname of the psyclone server
     * @param port
     *            port of the psyclone server
     */
    public Sieve(String name, String hostname, Integer port) {
        super("Sieve." + name, hostname, port);
        messageTypeSuffix = name;
        topicString = airBrush.getParameterString("Topic");
        System.out.println("Topic set to: " + topicString);
    }

    /*
     * (non-Javadoc)
     * 
     * @see amber.common.Module#airBrushReceiveMessage(com.cmlabs.air.Message)
     */
    public boolean airBrushReceiveMessage(Message msg) {
        if (super.airBrushReceiveMessage(msg))
            return true;

        if (msg.to.equals("WB.Stories")) {
            if (msg.type.equals("Story")) {
                handleIncomingStory(msg);
                return true;
            }
        } else if (msg.to.equals(moduleName)) {
            if (msg.type.equals("Sieve.Topic")) {
                topicString = msg.content;
                System.out.println("Topic set to: " + topicString);
                return true;
            }
        }
        return false;
    }

    /**
     * @param story
     *            the story to be analysed
     * @return a newly created Analysis object which contains information about
     *         the story
     */
    protected abstract Analysis doAnalysis(Story story);

    /**
     * an incoming message gets analysed, if it is deemed relevant the analysis
     * immediately sent on
     * 
     * @param msg
     */
    protected void handleIncomingStory(Message msg) {
        Story s = Story.createFromYAML(msg.content);
        if (s != null) {
            Analysis a = doAnalysis(s);

            if (a.isRelevant()) {
                Message m = new Message();
                m.to = "WB.Analyses";
                m.content = a.toYAML();
                m.type = "Analysis." + messageTypeSuffix;
                airBrush.postMessage(m);
            }
        }
    }
}