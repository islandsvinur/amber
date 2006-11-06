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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Pattern;

import amber.Crawler;
import amber.common.Story;

import com.cmlabs.air.Message;

import de.nava.informa.core.ChannelIF;
import de.nava.informa.core.FeedIF;
import de.nava.informa.core.ItemIF;
import de.nava.informa.core.ParseException;
import de.nava.informa.impl.basic.ChannelBuilder;
import de.nava.informa.parsers.FeedParser;
import de.nava.informa.parsers.OPMLParser;
import de.nava.informa.utils.poller.Poller;
import de.nava.informa.utils.poller.PollerObserverIF;

/**
 * @author christian
 * 
 */
public class RSS extends Crawler implements PollerObserverIF {
    private ChannelIF channel = null;

    private Poller poller;

    /**
     * @param name
     * @param hostname
     * @param port
     * @throws MalformedURLException
     */
    public RSS(String name, String hostname, Integer port)
            throws MalformedURLException {
        super("RSS." + name, hostname, port);
        System.out.println("Creating Poller.");
        poller = new Poller(10);
        switchFeed();
    }

    /**
     * @return the URL as stored in the Psyclone parameter
     * @throws MalformedURLException
     */
    private URL getURL() throws MalformedURLException {
        final String paramname = "FeedURI";
        if (airBrush.hasParameter(paramname)) {
            String urlstring = airBrush.getParameterString(paramname);
            URL ret = null;
            try {
                ret = new URL(urlstring);
            } catch (MalformedURLException e) {
                throw new MalformedURLException("The " + paramname
                        + " parameter is not well formed (" + urlstring + ").");
            }
            return ret;
        } else {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see amber.common.Module#airBrushReceiveMessage(com.cmlabs.air.Message)
     */
    public boolean airBrushReceiveMessage(Message msg) {
        return super.airBrushReceiveMessage(msg);
    }

    private void switchFeedRSS() throws MalformedURLException {
        URL url = getURL();
        System.out.println("Switching feed to: " + url.toExternalForm());

        if (channel != null) {
            poller.unregisterChannel(channel);
        }
        
        registerChannel(getURL());
    }
    
    private void registerChannel(URL url) {
        ChannelIF c;
        try {
            c = FeedParser.parse(new ChannelBuilder(), url);
            readAllItemsIn(c);
            poller.registerChannel(c);
        } catch (Exception e) {
            System.err.println("Error parsing feed: " + e.getMessage());
        }
    }
    
    private void readAllItemsIn(ChannelIF c) {
        if (c != null) {
            Collection items = c.getItems();
            System.out.println("Found " + items.size()
                    + " items in initial feed.");
            for (Iterator it = items.iterator(); it.hasNext();) {
                handleItem((ItemIF) it.next());
                it.remove();
            }
        }
        
    }

    private void switchFeedOPML() throws MalformedURLException {
        URL url;
        url = new URL(airBrush.getParameterString("OPML"));
        try {
            Collection feeds = OPMLParser.parse(url);
            for (Iterator i = feeds.iterator(); i.hasNext(); ) {
                FeedIF f = (FeedIF) i.next();
                System.out.println("Creating feed for " + f.getLocation());
                registerChannel(f.getLocation());
            }
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * 
     */
    private void switchFeed() {
        try {
            if (airBrush.hasParameter("OPML")) {
                switchFeedOPML();
            } else if (airBrush.hasParameter("FeedURI")) {
                switchFeedRSS();
            }
        } catch (MalformedURLException e) {
            System.err.println("Could not switch to feed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see amber.common.Module#start()
     */
    public void start() {
        Integer refresh;
        super.start();

        System.out.println("Starting poller.");
        poller.addObserver(this);

        if (airBrush.hasParameter("RefreshTime"))
            refresh = airBrush.getParameterInteger("RefreshTime");
        else
            refresh = 60;

        System.out.println("Setting refresh time to " + refresh + "s.");
        poller.setPeriod(refresh * 1000);

    }

    private String convertToPrintable(String input) {
        final Pattern p = Pattern.compile("([^\\p{Print}]|')");
        if (input != null)
            return p.matcher(input).replaceAll("");
        else
            return "-";
    }

    /**
     * Creates a Story object with the contents of the item and posts it to
     * Psyclone
     * 
     * @param item
     */
    private void handleItem(ItemIF item) {
        Story s = new Story();

        // Optional
        s.setAuthor(convertToPrintable(item.getCreator()));
        s.setTitle(convertToPrintable(item.getTitle()));
        s.setContent(convertToPrintable(item.getDescription()));

        // Required
        s.setID(convertToPrintable(item.getGuid().getLocation()));
        s.setPublicationDate(item.getDate());

        Message msg = new Message();
        msg.to = "WB.Stories";
        msg.type = "Story";
        msg.content = s.toYAML();

        airBrush.postMessage(msg);
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

    /*
     * (non-Javadoc)
     * 
     * @see de.nava.informa.utils.poller.PollerObserverIF#channelChanged(de.nava.informa.core.ChannelIF)
     */
    public void channelChanged(ChannelIF arg0) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see de.nava.informa.utils.poller.PollerObserverIF#channelErrored(de.nava.informa.core.ChannelIF,
     *      java.lang.Exception)
     */
    public void channelErrored(ChannelIF arg0, Exception arg1) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see de.nava.informa.utils.poller.PollerObserverIF#itemFound(de.nava.informa.core.ItemIF,
     *      de.nava.informa.core.ChannelIF)
     */
    public void itemFound(ItemIF item, ChannelIF channel) {
        System.out.println("New item: " + item.getTitle());
        handleItem(item);
        channel.addItem(item);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.nava.informa.utils.poller.PollerObserverIF#pollFinished(de.nava.informa.core.ChannelIF)
     */
    public void pollFinished(ChannelIF arg0) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.nava.informa.utils.poller.PollerObserverIF#pollStarted(de.nava.informa.core.ChannelIF)
     */
    public void pollStarted(ChannelIF arg0) {
    }

}
