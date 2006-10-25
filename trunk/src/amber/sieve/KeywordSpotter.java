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

public class KeywordSpotter extends Sieve {
    
    private Pattern contentPattern;

    private Pattern authorPattern;

    public KeywordSpotter(String name, String hostname, Integer port) {
        super("KeywordSpotter." + name, hostname, port);
    }

    @Override
    public boolean airBrushReceiveMessage(Message msg) {
        if (super.airBrushReceiveMessage(msg))
            return true;

        if (msg.to.equals(moduleName)) {
            if (msg.type.equals("Keywords.Contents")) {
                contentPattern = Pattern.compile(msg.content, Pattern.CASE_INSENSITIVE);
                System.out.println("Content match string: "
                        + msg.content);
                return true;
            } else if (msg.type.equals("Keywords.Author")) {
                authorPattern = Pattern.compile(msg.content, Pattern.CASE_INSENSITIVE);
                System.out.println("Author match string: " + msg.content);
                return true;
            }

        }
        return false;
    }

    @Override
    public Analysis doAnalysis(Story story, String topic) {
        Analysis a = new Analysis();
        a.setID(story.getID());
        a.setTopic(topic);

        if (contentPattern != null) {
            String content = story.getContent();
            String title = story.getTitle();
            if (contentPattern.matcher(content).matches()) {
                a.setTopicRelevance(1.0);
            }
            if (contentPattern.matcher(title).matches()) {
                a.setTopicRelevance(1.0);
            }
        }

        if (authorPattern != null) {
            String author = story.getAuthor();
            if (authorPattern.matcher(author).matches()) {
                a.setAuthorRelevance(1.0);
            }
        }

        return a;
    }

    @Override
    public void start() {
        super.start();
        // TODO Auto-generated method stub

    }

    @Override
    public void stop() {
        super.stop();
        // TODO Auto-generated method stub
    }
}
