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
import amber.showoff.EarthViewStory;
import amber.showoff.ObservableList;

import com.cmlabs.air.Message;
import com.cmlabs.air.ObjectCollection;

/**
 * Displays the information available in the whiteboards. They combine Story
 * messages with Analysis messages. It is dependent on the type of visualization
 * one wants to accomplish what will be displayed and in what manner.
 * 
 * @author christian
 * 
 */
public abstract class ShowOff extends Module {

    /**
     * In this list incoming stories are stored before they are handled.
     */
    protected ObservableList<EarthViewStory> storyQueue;

    /**
     * In this list incoming analyses are stored before they are handled.
     */
    protected ObservableList<Analysis> analysisQueue;

    private int STORY_LIFETIME;

    /**
     * @param moduleName
     *            the name of the module to start
     * @param hostname
     *            hostname of the psyclone server
     * @param port
     *            port of the psyclone server
     */
    public ShowOff(String moduleName, String hostname, Integer port) {
        super("ShowOff." + moduleName, hostname, port);
        
        int time;
        if (airBrush.plug.hasParameter("StoryLifetimeInHours")) {
            time = airBrush.plug.getParameterInteger("StoryLifetimeInHours") * 60000;
        } else {
            time = 14 * 24 * 60 * 60 * 1000;
        }
        STORY_LIFETIME = time;

        if (!airBrush.openWhiteboard("WB.Stories"))
            System.err
                    .println("Couldn't open two-way connection to whiteboard WB.Stories");
        else
            System.err.println("Successfully opened WB.Stories");

        if (!airBrush.openWhiteboard("WB.Analyses"))
            System.err
                    .println("Couldn't open two-way connection to whiteboard WB.Analyses");
        else
            System.err.println("Successfully opened WB.Analyses");

        storyQueue = new ObservableList<EarthViewStory>();
        analysisQueue = new ObservableList<Analysis>();
    }

    @Override
    public void start() {
        super.start();

        ObjectCollection stories = airBrush.plug
                .retrieveMessages("<retrieve from=\"WB.Stories\" type=\"Story\"><lastmsec>"
                        + STORY_LIFETIME + "</lastmsec></retrieve>");
        if (stories != null) {
            System.out.println("Number of stories: " + stories.getCount());
            while (stories.getCount() > 0) {
                airBrushReceiveMessage((Message) stories.getFirst());
                stories.remove(0);
            }
        }

        ObjectCollection analyses = airBrush.plug
                .retrieveMessages("<retrieve from=\"WB.Analyses\" type=\"Analysis.*\"><lastmsec>"
                        + STORY_LIFETIME + "</lastmsec></retrieve>");
        if (analyses != null) {
            System.out.println("Number of analyses: " + analyses.getCount());
            while (analyses.getCount() > 0) {
                airBrushReceiveMessage((Message) analyses.getFirst());
                analyses.remove(0);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see amber.common.Module#airBrushReceiveMessage(com.cmlabs.air.Message)
     */
    public boolean airBrushReceiveMessage(Message msg) {
        if (super.airBrushReceiveMessage(msg))
            return true;

        // System.out.println("Receiving " + msg.type + " from AirBrush.");

        if (msg.type.equals("Story")) {
            EarthViewStory story;
            // Parse the XML into the Story object
            story = EarthViewStory.createFromYAML(msg.content);

            // Print some information
            /*
             * System.out.println("Received story written by " +
             * story.getAuthor() + " on " + story.getPublicationDate() + ", with
             * id: " + story.getID());
             */
            // Now add this story to the queue
            storyQueue.add(story);
            return true;
        } else if (msg.type.matches("Analysis.*")) {
            Analysis a;
            a = Analysis.createFromYAML(msg.content);
            /*
             * System.out.println("Received analysis for story " + a.getID() + "
             * topic: " + a.getTopic());
             */

            analysisQueue.add(a);
            return true;
        }

        return false;
    }
}
