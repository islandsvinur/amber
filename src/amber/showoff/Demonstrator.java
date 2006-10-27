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

package amber.showoff;

import java.awt.Color;
import java.awt.Container;

import javax.swing.JApplet;

import amber.common.Analysis;
import amber.common.Polar2d;

public class Demonstrator extends JApplet implements Runnable {

    private static final long serialVersionUID = -6492789321739444369L;

    static ObservableList<EarthViewStory> storyQueue;

    static ObservableList<Analysis> analysisQueue;

    static Thread thread;

    int storyCounter = 0;

    public Demonstrator() {
        Container mainpane = getContentPane();

        mainpane.setBackground(Color.black);

        storyQueue = new ObservableList<EarthViewStory>();
        analysisQueue = new ObservableList<Analysis>();

        EarthView earthView = new EarthView(storyQueue, analysisQueue);
        earthView.setVisible(true);
        earthView.addAttractor(new Polar2d(0.9, 1.75 * Math.PI), 0.75,
                "spitsbergen");
        earthView.addAttractor(new Polar2d(0.9, 1.5 * Math.PI), 0.75,
                "ijsland");
        earthView.addAttractor(new Polar2d(0.9, 1.25 * Math.PI), 0.75,
                "groenland");
        earthView.addAttractor(new Polar2d(0.9, 1.0 * Math.PI), 0.75,
                "faeröer");
        earthView.addAttractor(new Polar2d(0.9, 0.75 * Math.PI), 0.75,
                "ierland");
        earthView.addAttractor(new Polar2d(0.9, 0.5 * Math.PI), 0.75,
                "nederland");
        earthView.addAttractor(new Polar2d(0.9, 0.25 * Math.PI), 0.75,
                "denemarken");
        earthView.addAttractor(new Polar2d(0.9, 0.0 * Math.PI), 0.75,
                "noorwegen");

        setVisible(true);
        mainpane.add(earthView.panel);

        thread = new Thread(this);
        thread.start();
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        new Demonstrator();
    }

    public void run() {

        while (Thread.currentThread() == thread) {
            EarthViewStory s = new EarthViewStory();
            s.setID("story-" + storyCounter++);
            storyQueue.add(s);

            // Generate analyses at random
            // if (Math.random() * 3 < 1) {
            if (true) {
                Analysis a = new Analysis();
                a.setID("story-" + (storyCounter - 2));
                switch ((int) (Math.random() * 8)) {
                case 0:
                    a.setTopic("ijsland");
                    break;
                case 1:
                    a.setTopic("nederland");
                    break;
                case 2:
                    a.setTopic("denemarken");
                    break;
                case 3:
                    a.setTopic("groenland");
                    break;
                case 4:
                    a.setTopic("noorwegen");
                    break;
                case 5:
                    a.setTopic("faeröer");
                    break;
                case 6:
                    a.setTopic("ierland");
                    break;
                case 7:
                    a.setTopic("spitsbergen");
                    break;
                }
                a.setTopicRelevance(Math.random());
                analysisQueue.add(a);

            }

            if (storyCounter == 1000) {
                thread = null;
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

}
