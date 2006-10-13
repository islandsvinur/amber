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

package amber.crawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Pattern;

import amber.Crawler;
import amber.common.Story;

import com.cmlabs.air.Message;

import de.nava.informa.core.ChannelIF;
import de.nava.informa.core.ItemIF;
import de.nava.informa.impl.basic.ChannelBuilder;
import de.nava.informa.parsers.FeedParser;
import de.nava.informa.utils.poller.Poller;
import de.nava.informa.utils.poller.PollerObserverIF;

public class RSS extends Crawler implements PollerObserverIF {
    private ChannelIF channel = null;

    private Poller poller;

    public RSS(String name, String hostname, Integer port) {
        super("RSS." + name, hostname, port);
        poller = new Poller(1);
    }

    public boolean airBrushReceiveMessage(Message msg) {
        if (super.airBrushReceiveMessage(msg))
            return true;

        if (msg.to.equals(moduleName)) {
            if (msg.type.equals("Feed.RSS")) {
                try {
                    switchFeed(new URL(msg.content));
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return true;
            }
        }
        return false;
    }

    private void switchFeed(URL url) {
        System.out.println("Switching feed to: " + url.toExternalForm());

        if (channel != null) {
            poller.unregisterChannel(channel);
        }

        try {
            channel = FeedParser.parse(new ChannelBuilder(), url);
        } catch (Exception e) {
            System.err.println("Fout!");
        }
        Collection items = channel.getItems();
        for (Iterator it = items.iterator(); it.hasNext();) {
            itemFound((ItemIF) it.next(), channel);
        }

        poller.registerChannel(channel);
    }

    public void start() {
        super.start();
        poller.addObserver(this);
        poller.setPeriod(1 * 60 * 1000);
    }

    private void handleItem(ItemIF item) {
        Story s = new Story();
        Pattern p = Pattern.compile("([^\\p{Print}]|')");
        
        try {
            s.setID(p.matcher(item.getGuid().getLocation()).replaceAll(""));
        } catch (Exception e) {
            System.out.println("Something went wicked.");
        }
        try {
            s.setAuthor(p.matcher(item.getCreator()).replaceAll(""));
        } catch (Exception e) {
            System.out.println("Something went wicked.");
        }
        try {
            s.setTitle(p.matcher(item.getTitle()).replaceAll(""));
        } catch (Exception e) {
            System.out.println("Something went wicked.");
        }
        try {
            s.setContent(p.matcher(item.getDescription()).replaceAll(""));
        } catch (Exception e) {
            System.out.println("Something went wicked.");
        }
        try {
            s.setPublicationDate(item.getDate());
        } catch (Exception e) {
            System.out.println("Something went wicked.");
        }

        Message msg = new Message();
        msg.to = "WB.Stories";
        msg.type = "Story";
        msg.content = s.toYAML();

        airBrush.postMessage(msg);
    }

    @Override
    public void stop() {
        super.stop();
        // TODO Auto-generated method stub

    }

    public void channelChanged(ChannelIF arg0) {
        // TODO Auto-generated method stub

    }

    public void channelErrored(ChannelIF arg0, Exception arg1) {
        // TODO Auto-generated method stub

    }

    public void itemFound(ItemIF item, ChannelIF channel) {
        System.out.println("Found item: " + item.getTitle());
        handleItem(item);
    }

    public void pollFinished(ChannelIF arg0) {
        System.out.println("Polling ended.");
    }

    public void pollStarted(ChannelIF arg0) {
        System.out.println("Polling started.");
    }

}
