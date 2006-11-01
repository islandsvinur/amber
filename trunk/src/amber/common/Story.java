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

import java.util.Date;

/**
 * @author christian
 *
 */
public class Story extends AmberMessage {

    final static private String keyIdentifier = "URI";

    final static private String keyAuthor = "Author";

    final static private String keyTitle = "Title";

    final static private String keyContent = "Story-Content";

    final static private String keyPublicationDate = "Publication-Date";

    /**
     * 
     */
    public Story() {
        super();
    }

    /**
     * @param identifier
     */
    public Story(String identifier) {
        this();
        setProperty(keyIdentifier, identifier);
    }

    /**
     * @param in
     * @return
     */
    public static Story createFromYAML(String in) {
        Story story = new Story();
        story.fromYAML(in);
        return story;
    }

    public String getID() {
        return (String) getProperty(keyIdentifier);
    }

    public String getTitle() {
        return (String) getProperty(keyTitle);
    }

    public String getContent() {
        return (String) getProperty(keyContent);
    }

    public String getAuthor() {
        return (String) getProperty(keyAuthor);
    }

    public Date getPublicationDate() {
        return (Date) getProperty(keyPublicationDate);
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
    public void setTitle(String value) {
        setProperty(keyTitle, value);
    }

    /**
     * @param value
     */
    public void setContent(String value) {
        setProperty(keyContent, value);
    }

    /**
     * @param value
     */
    public void setAuthor(String value) {
        setProperty(keyAuthor, value);
    }

    /**
     * @param value
     */
    public void setPublicationDate(Date value) {
        setProperty(keyPublicationDate, value);
    }

}
