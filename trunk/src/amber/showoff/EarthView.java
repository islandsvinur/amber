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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Map.Entry;

import javax.swing.JPanel;
import javax.vecmath.Point2d;

import amber.common.Analysis;
import amber.common.Polar2d;

/**
 * @author christian
 *
 */
public class EarthView extends JPanel implements Runnable, Observer {

    private static final long serialVersionUID = -3882808552754900513L;

    Graphics offGraphics;

    Image previousImage;

    Dimension offDimension;

    private int frameDelay = 40;

    private Thread animator;

    private int frame = 0;

    static Hashtable<String, Attractor> attractors;

    static Hashtable<String, EarthViewStory> stories;

    static Hashtable<String, Particle> particles;

    private ObservableList<EarthViewStory> storyQueue;

    private ObservableList<Analysis> analysisQueue;

    private boolean firstFrame = true;

    /**
     * @param sq
     * @param aq
     */
    public EarthView(ObservableList<EarthViewStory> sq,
            ObservableList<Analysis> aq) {
        storyQueue = sq;
        analysisQueue = aq;
        attractors = new Hashtable<String, Attractor>();
        particles = new Hashtable<String, Particle>();
        stories = new Hashtable<String, EarthViewStory>();
        sq.addObserver(this);
    }

    /**
     * 
     */
    public void start() {
        setForeground(Color.white);
        setBackground(Color.black);
        animator = new Thread(this);
        animator.start();
    }

    /* (non-Javadoc)
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    public void paintComponent(Graphics g) {
        int earthRadius = 50;
        Dimension d = getSize();

        if (firstFrame) {
            firstFrame = false;
            g.setColor(getBackground());
            g.fillRect(0, 0, d.width, d.height);
        }

        g.setColor(new Color((float) 0.0, (float) 0.0, (float) 0.0,
                (float) 0.01));
        // g.setColor(Color.black);
        g.fillRect(0, 0, d.width, d.height);

        g.setColor(getForeground());
        g.drawOval((d.width - earthRadius) / 2, (d.height - earthRadius) / 2,
                earthRadius, earthRadius);

        /*
         * for (int i = earthRadius * 2; i < d.width; i += 25) {
         * g.drawOval((d.width - i) / 2, (d.height - i) / 2, i, i); }
         */

        synchronized (particles) {
            Iterator<Entry<String, Particle>> i = particles.entrySet()
                    .iterator();
            while (i.hasNext()) {
                Entry<String, Particle> e = i.next();
                Particle p = e.getValue();
                if (!p.crashed())
                    drawParticle(g, p);
            }
        }
        Iterator<Entry<String, Attractor>> j = attractors.entrySet().iterator();
        g.setColor(getForeground());

        while (j.hasNext()) {
            Entry<String, Attractor> e = j.next();
            Point2d p = e.getValue().location.toCartesianPoint();
            p.add(new Point2d(d.width / 2, d.height / 2));
            int x = new Double(p.x).intValue();
            int y = new Double(p.y).intValue();
            g.drawOval(x - 5, y - 5, 10, 10);
            g.drawString(e.getKey(), x + 15, y);
        }

        g.setColor(Color.black);
        g.fillRect(0, 0, 200, 45);
        g.setColor(getForeground());
        g.drawString("Number of particles: " + particles.size(), 15, 15);
        g.drawString("Frame: " + frame, 15, 30);
    }

    /**
     * @param g
     * @param p
     */
    private void drawParticle(Graphics g, Particle p) {
        int diameter = 5;
        Dimension d = getSize();
        Point2d loc = p.getLocation();
        // loc.scale(scalingFactor);

        if (!p.crashed()) {
            g.setColor(p.color);
            g.fillOval((int) (loc.x - diameter / 2) + (d.width / 2),
                    (int) (loc.y - diameter / 2) + (d.height / 2), diameter,
                    diameter);
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
        long tm = System.currentTimeMillis();
        while (Thread.currentThread() == animator) {
            repaint();

            try {
                tm += frameDelay;
                Particle p;
                synchronized (particles) {
                    Iterator<Entry<String, Particle>> i = particles.entrySet()
                            .iterator();
                    while (i.hasNext()) {
                        Entry<String, Particle> e = i.next();
                        p = e.getValue();
                        p.calculate((double) (frameDelay) / 1000.0);
                        if (p.crashed()) {
                            i.remove();
                        }
                    }
                }

                Thread.sleep(Math.max(0, tm - System.currentTimeMillis()));
            } catch (InterruptedException e) {
                break;
            }
            frame++;

            if ((frame % 50) == 0) {
                getNewStories();
            }
        }

    }

    /**
     * 
     */
    private void getNewStories() {
        EarthViewStory s;
        Particle p;

        synchronized (storyQueue) {
            Iterator<EarthViewStory> storyIterator = storyQueue.listIterator();
            while (storyIterator.hasNext()) {
                s = storyIterator.next();
                storyIterator.remove();

                stories.put(s.getID(), s);

                if (!s.hasParticle()) {
                    p = new Particle(s);
                    p.launch();
                    particles.put(s.getID(), p);
                }
            }
        }
    }

    /**
     * 
     */
    private void getNewAnalyses() {
        Analysis a;

        synchronized (analysisQueue) {
            ListIterator<Analysis> ai = analysisQueue.listIterator();
            while (ai.hasNext()) {
                a = ai.next();
                ai.remove();

                a.getID();
                EarthViewStory s = stories.get(a.getID());
                // System.out.println("New analysis incoming: " + a.getID());
                if (s != null) {
                    // System.out.println("Adding analysis");
                    s.addAnalysis(a);
                }
            }
        }

    }

    /* (non-Javadoc)
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable updater, Object message) {
        if (updater instanceof ObservableList<?>) {
            if (message.equals("offer") || message.equals("add")
                    || message.equals("addAll")) {
                // There is a new story added to the list, get all newest
                // stories
                getNewStories();
                getNewAnalyses();
            }
        }
    }

    /**
     * @param location
     * @param force
     * @param topic
     * @return the newly created attractor
     */
    public Attractor addAttractor(Polar2d location, Double force, String topic) {
        Attractor a = new Attractor();
        a.location = location;
        a.force = force;
        a.topic = topic;
        attractors.put(topic, a);
        return a;
    }

}
