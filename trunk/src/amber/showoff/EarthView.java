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
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Map.Entry;

import javax.swing.JPanel;
import javax.vecmath.Point2d;

import amber.common.Analysis;
import amber.common.Polar2d;

public class EarthView extends JPanel implements Runnable, Observer {

    private static final long serialVersionUID = -3882808552754900513L;

    Graphics offGraphics;

    Image previousImage;

    Dimension offDimension;

    private int frameDelay = 40;

    private Double scalingFactor = 1.0;

    private Thread animator;

    private int frame = 0;

    static Hashtable<String, Attractor> attractors;
    static Hashtable<String, EarthViewStory> stories;
    static Hashtable<String, Particle> particles;

    private ObservableList<EarthViewStory> storyQueue;

    private ObservableList<Analysis> analysisQueue;

    public EarthView(ObservableList<EarthViewStory> sq) {
        storyQueue = sq;
        analysisQueue = new ObservableList<Analysis>();
        // particles = Collections.synchronizedList(new LinkedList<Particle>());
        attractors = new Hashtable<String, Attractor>();
        particles = new Hashtable<String, Particle>();
        stories = new Hashtable<String, EarthViewStory>();
        sq.addObserver(this);
        start();
    }

    public void start() {
        setBackground(Color.black);
        setForeground(Color.white);
        animator = new Thread(this);
        animator.start();
    }

    public void paintComponent(Graphics g) {
        int earthRadius = 50;
        Dimension d = getSize();

        g.setColor(getBackground());
        g.fillRect(0, 0, d.width, d.height);

        g.setColor(getForeground());
        g.drawOval((d.width - earthRadius) / 2, (d.height - earthRadius) / 2,
                earthRadius, earthRadius);

        g.drawOval(100, 100, 10, 10);
        g.drawOval(100, d.width - 100, 10, 10);
        g.drawOval(d.height - 100, d.width - 100, 10, 10);
        g.drawOval(d.height - 100, 100, 10, 10);
        /*
         * for (int i = earthRadius * 2; i < d.width; i += 50) {
         * g.drawOval((d.width - i) / 2, (d.height - i) / 2, i, i); }
         */

        synchronized (particles) {
            Iterator<Entry<String, Particle>> i = particles.entrySet().iterator();
            while (i.hasNext()) {
                Entry<String, Particle> e = i.next();
                Particle p = e.getValue();
                if (!p.crashed())
                    drawParticle(g, p);
            }
        }
        Iterator<Entry<String, Attractor>> j = attractors.entrySet().iterator();
        while (j.hasNext()) {
            Entry<String, Attractor> e = j.next();
            Point2d p = e.getValue().location.toCartesianPoint();
            p.add(new Point2d(d.width / 2, d.height / 2));
            g.drawString(e.getKey(), new Double(p.x).intValue(),
                    new Double(p.y).intValue());
        }

        g.setColor(Color.white);
        g.drawString("Number of particles: " + particles.size(), 15, 15);
        g.drawString("Frame: " + frame, 15, 30);
    }

    public void drawParticle(Graphics g, Particle p) {
        // int diameter = (int) (Math.cbrt(p.getMass() / p.getDensity()) *
        // scalingFactor);
        int diameter = 10;
        Dimension d = getSize();
        Point2d loc = p.getLocation();
        loc.scale(scalingFactor);

        if (!p.crashed()) {
            g.setColor(new Color(Color.HSBtoRGB(
                    (float) ((p.getVelocity().x) / 14.0), 1, 1)));
            g.fillOval((int) (loc.x - diameter / 2) + (d.width / 2),
                    (int) (loc.y - diameter / 2) + (d.height / 2), diameter,
                    diameter);
        }
        if (10000 * Math.random() < 1) {
            // System.out.println("Boost particle");
            p.boost(100.0);
        }
    }

    public void run() {
        long tm = System.currentTimeMillis();
        while (Thread.currentThread() == animator) {
            repaint();

            try {
                tm += frameDelay;
                Particle p;
                synchronized (particles) {
                    Iterator<Entry<String, Particle>> i = particles.entrySet().iterator();
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

    public void getNewStories() {
        EarthViewStory s;
        Particle p;

        synchronized (storyQueue) {
            Iterator<EarthViewStory> storyIterator = storyQueue.listIterator();
            while (storyIterator.hasNext()) {
                s = storyIterator.next();

                if (!s.hasParticle()) {
                    p = new Particle(s);
                    p.launch();
                    particles.put(s.getID(), p);
                }
            }
        }
    }

    private void getNewAnalyses() {
        Analysis a;

        synchronized (analysisQueue) {
            ListIterator<Analysis> ai = analysisQueue.listIterator();
            while (ai.hasNext()) {
                a = ai.next();
                ai.remove();

                a.getID();
            }
        }

    }

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

    public Attractor addAttractor(Polar2d location, Double force, String topic) {
        Attractor a = new Attractor();
        a.location = location;
        a.force = force;
        a.topic = topic;
        attractors.put(topic, a);
        return a;
    }

}
