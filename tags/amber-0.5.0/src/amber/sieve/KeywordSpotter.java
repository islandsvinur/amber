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

package amber.sieve;

import java.util.regex.Pattern;

import com.cmlabs.air.Message;

import amber.Sieve;
import amber.common.Analysis;
import amber.common.Story;

/**
 * Sieve module which matches a Story with a regular expression. If it matches,
 * an Analysis object is created and a relevancy value is set and it is sent to
 * the Psyclone whiteboard.
 * 
 * @see Pattern
 * @author christian
 * 
 */
public class KeywordSpotter extends Sieve {

    private Pattern contentPattern;

    private Pattern authorPattern;

    /**
     * @param name
     * @param hostname
     * @param port
     */
    public KeywordSpotter(String name, String hostname, Integer port) {
        super("KeywordSpotter." + name, hostname, port);
        if (airBrush.hasParameter("QueryString.Content")) {
            contentPattern = Pattern.compile(airBrush
                    .getParameterString("QueryString.Content"),
                    Pattern.CASE_INSENSITIVE);
            System.out.println("Content pattern set to: " + contentPattern);
        }
        if (airBrush.hasParameter("QueryString.Author")) {
            authorPattern = Pattern.compile(airBrush
                    .getParameterString("QueryString.Author"),
                    Pattern.CASE_INSENSITIVE);
            System.out.println("Author pattern set to: " + authorPattern);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see amber.Sieve#airBrushReceiveMessage(com.cmlabs.air.Message)
     */
    @Override
    public boolean airBrushReceiveMessage(Message msg) {
        return super.airBrushReceiveMessage(msg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see amber.Sieve#doAnalysis(amber.common.Story)
     */
    @Override
    public Analysis doAnalysis(Story story) {
        Analysis a = new Analysis();
        a.setID(story.getID());
        a.setTopic(topicString);

        try {
            if (contentPattern != null) {
                String content = story.getContent();
                String title = story.getTitle();
                Double relevance = 0.0;
                if (contentPattern.matcher(content).matches()
                        || contentPattern.matcher(title).matches()) {
                    // FIXME: The AI department may come up with a more
                    // intelligent algorithm here
                    relevance += 0.5 + Math.random() * 0.5;
                }
                if (relevance > 0) {
                    a.setTopicRelevance(relevance);
                    System.out.println("Analysed story (" + relevance + "): "
                            + story.getTitle());
                }
            }

            // It is not entirely clear what the meaning of the author relevance
            // would be, but at least it is stored.
            if (authorPattern != null) {
                String author = story.getAuthor();
                if (authorPattern.matcher(author).matches()) {
                    a.setAuthorRelevance(Math.random() * 0.5 + 0.5);
                }
            }
        } catch (Exception e) {
            System.err.println("There was an error while analysing: "
                    + e.getMessage());
        }

        return a;
    }

    /*
     * (non-Javadoc)
     * 
     * @see amber.common.Module#start()
     */
    @Override
    public void start() {
        super.start();
    }

    /*
     * (non-Javadoc)
     * 
     * @see amber.common.Module#stop()
     */
    @Override
    public void stop() {
        super.stop();
    }
}
