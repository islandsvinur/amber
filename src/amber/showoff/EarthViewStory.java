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

import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import amber.common.Analysis;
import amber.common.Story;

public class EarthViewStory extends Story {

    private List<Analysis> analyses;
    private Hashtable<String, Double> weights;

    private Particle particle;

    private void init() {
        analyses = Collections.synchronizedList(new LinkedList<Analysis>());
        weights = new Hashtable<String, Double>();
    }

    public EarthViewStory() {
        super();
        init();
        // TODO Auto-generated constructor stub
    }

    public EarthViewStory(String identifier) {
        super(identifier);
        init();
        // TODO Auto-generated constructor stub
    }

    public void addAnalysis(Analysis a) {
        // TODO: Check whether analysis is indeed for this story
        analyses.add(a);
        calculateWeights();

        // TODO: Boost the particle (get nice value for this)
        particle.boost(1.0);
    }

    public void calculateWeights() {
        if (particle != null) {
            synchronized(analyses) {
                ListIterator<Analysis> i = analyses.listIterator();
                Hashtable<String, Integer> topicCount = new Hashtable<String, Integer>();
                while (i.hasNext()) {
                    // FIXME: look at this again
                    Analysis a = i.next();
                    String topic = a.getTopic();
                    Integer count = topicCount.get(topic);
                    
                    Double value = a.getTopicRelevance();
                    Double oldValue = weights.get(topic);
                    
                    weights.put(topic, (oldValue * count + value) / (count + 1));
                }
            }
            // set findings in particle
        }
    }

    public void setParticle(Particle p) {
        // It is assumed that the particle isn't already coupled to another
        // story! Values in the particle are overwritten every time an analysis
        // is added...
        particle = p;
        p.setStory(this);
        calculateWeights();
    }
}