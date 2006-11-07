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

/**
 * Holds information about a single Story object. Currently it is mainly focused
 * on the relevance of a story within a certain topic, but it can be easily
 * extended.
 * 
 * @author christian
 * 
 */
public class Analysis extends AmberMessage {

    final static private String keyIdentifier = "URI";

    final static private String keyTopicRelevance = "Topic-Relevance";

    final static private String keyAuthorRelevance = "Author-Relevance";

    final static private String keyTopic = "Topic";

    private boolean relevant = false;

    /**
     * 
     */
    public Analysis() {
        super();
    }

    /**
     * @param identifier
     */
    public Analysis(String identifier) {
        this();
        setProperty(keyIdentifier, identifier);
    }

    /**
     * @param in
     *            a YAML string representing the contents of an Analysis object
     * @return a newly created Analysis object, initialized with the contents of
     *         the YAML string
     */
    public static Analysis createFromYAML(String in) {
        Analysis a = new Analysis();
        a.fromYAML(in);
        return a;
    }

    /**
     * @return true if the analysis finds itself relevant
     */
    public boolean isRelevant() {
        return relevant;
    }

    public Double getTopicRelevance() {
        return getDoubleProperty(keyTopicRelevance);
    }

    public Double getAuthorRelevance() {
        return getDoubleProperty(keyAuthorRelevance);
    }

    public String getID() {
        return getStringProperty(keyIdentifier);
    }

    public String getTopic() {
        return getStringProperty(keyTopic);
    }

    /**
     * @param key
     * @param value
     */
    protected void setRelevance(String key, Double value) {
        Double curr = getDoubleProperty(key);
        if (curr != null) {
            setProperty(key, Math.max(curr, value));
        } else {
            setProperty(key, value);
        }
        relevant = true;
    }

    /**
     * @param value
     */
    public void setTopicRelevance(Double value) {
        setRelevance(keyTopicRelevance, value);
    }

    /**
     * @param value
     */
    public void setAuthorRelevance(Double value) {
        setRelevance(keyAuthorRelevance, value);
    }

    /**
     * @param value
     */
    public void setID(String value) {
        setProperty(keyIdentifier, value);
    }

    /**
     * @param value
     */
    public void setTopic(String value) {
        setProperty(keyTopic, value);
    }

}
