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

import java.io.IOException;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import amber.common.AirBrush;
import amber.common.AirBrushCallable;
import amber.common.Story;
import amber.showoff.FullScreen;

import com.cmlabs.air.Message;

/* Starts the Display */
public class ShowOff implements AirBrushCallable {
    private ShowOffObject module;

    private AirBrush airbrush;

    @SuppressWarnings("unused")
    private LinkedList<Story> storyQueue;

    public ShowOff() {
        module = new FullScreen();
        airbrush = new AirBrush("ShowOff.FullScreen", "localhost", 10000);
        airbrush.setCallbackObject(this);
        storyQueue = new LinkedList<Story>();
    }

    public void start() {
        try {
            airbrush.connect();

            if (!airbrush.openWhiteboard("WB.Stories.Processed")) {
                System.err
                        .println("Could not open callback connection to whiteboard.");
            } else {
                System.out.println("Connected to whiteboard.");
            }
            airbrush.startListening();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        module.start();
        System.out.println("Started module");
    }

    public static void main(String args[]) {
        ShowOff so = new ShowOff();
        so.start();
    }

    public void airBrushReceiveMessage(Message msg) {
        System.out.println("Receiving Message from AirBrush.");

        if (msg.type == "Internal.Story") {
            Story story;
            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder builder;
            Document document = null;

            try {
                builder = factory.newDocumentBuilder();
                document = builder.parse(msg.content);
            } catch (ParserConfigurationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (SAXException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            Element content = document.getElementById("content");
            Element author = document.getElementById("author");
            Element publicationDate = document
                    .getElementById("publicationDate");

            story = new Story(content.getNodeValue(), author.getNodeValue(),
                    publicationDate.getNodeValue());

            // FIXME: Now add this story to the queue

        }

        // TODO Auto-generated method stub

    }
}
