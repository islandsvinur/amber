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

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.vecmath.Point2d;

import com.sun.opengl.util.Animator;

import amber.common.Analysis;
import amber.common.Polar2d;

public class EarthView implements Runnable, Observer, GLEventListener {

    private static final long serialVersionUID = -3882808552754900513L;

    Graphics offGraphics;

    Image previousImage;

    Dimension offDimension;

    private int frameDelay = 40;

    private Thread thread;

    private int frame = 0;

    static Hashtable<String, Attractor> attractors;

    static Hashtable<String, EarthViewStory> stories;

    static Hashtable<String, Particle> particles;

    private ObservableList<EarthViewStory> storyQueue;

    private ObservableList<Analysis> analysisQueue;
    
    final public GLCanvas panel;

    public EarthView(ObservableList<EarthViewStory> sq,
            ObservableList<Analysis> aq) {
        storyQueue = sq;
        analysisQueue = aq;
        attractors = new Hashtable<String, Attractor>();
        particles = new Hashtable<String, Particle>();
        stories = new Hashtable<String, EarthViewStory>();
        panel = new GLCanvas();
        panel.addGLEventListener(this);
        sq.addObserver(this);
        
        start();
    }

    public void start() {
        panel.setBackground(Color.black);
        panel.setForeground(Color.white);
        
        final Animator animator = new Animator(panel);
        animator.start();
        
        thread = new Thread(this);
        thread.start();
    }

    public void drawParticle(GLAutoDrawable drawable, Particle p) {
        double diameter = 0.01;
        Point2d loc = p.getLocation();
        
        GL gl = drawable.getGL();

        if (!p.crashed()) {
            gl.glBegin(GL.GL_POLYGON);
            drawCircle(drawable, loc.x, loc.y, diameter, 6);
            gl.glEnd();
        }
    }

    public void run() {
        long tm = System.currentTimeMillis();
        while (Thread.currentThread() == thread) {

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

    public void getNewStories() {
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

    public void init(GLAutoDrawable drawable) {
        // TODO Auto-generated method stub
    }
    
    public void drawCircle(GLAutoDrawable drawable, double x, double y, double radius, int granularity)
    {
       GL gl = drawable.getGL();
       for (int i=0; i < 360; i += 360 / granularity)
       {
          double degInRad = i*(Math.PI/180);
          gl.glVertex2d(x + Math.cos(degInRad)*radius, y + Math.sin(degInRad)*radius);
       }
    }
    
    public void display(GLAutoDrawable drawable) {
        // TODO Auto-generated method stub
        GL gl = drawable.getGL();

        double earthRadius = 0.05;
        
        gl.glLoadIdentity();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);        
        
        /* gl.glBegin(GL.GL_QUADS);
        gl.glColor4f(0F, 0F, 0F, 0.1F);
        gl.glVertex2d(-1, 1);
        gl.glVertex2d(-1, -1);
        gl.glVertex2d(1, -1);
        gl.glVertex2d(1, 1);
        gl.glEnd(); */

        // Draw "Earth"
        gl.glColor3f(1F, 1F, 1F);
        gl.glBegin(GL.GL_LINE_LOOP);
        drawCircle(drawable, 0D, 0D, earthRadius, 30);
        gl.glEnd();

        /* for (int i = earthRadius * 2; i < d.width; i += 25) {
            g.drawOval((d.width - i) / 2, (d.height - i) / 2, i, i);
        } */

        synchronized (particles) {
            Iterator<Entry<String, Particle>> i = particles.entrySet()
                    .iterator();
            while (i.hasNext()) {
                Entry<String, Particle> e = i.next();
                Particle p = e.getValue();
                if (!p.crashed())
                    drawParticle(drawable, p);
            }
        }

        Iterator<Entry<String, Attractor>> j = attractors.entrySet().iterator();
        
        gl.glColor3f(1F, 1F, 1F);
        while (j.hasNext()) {
            Entry<String, Attractor> e = j.next();
            Point2d p = e.getValue().location.toCartesianPoint();
            
            gl.glBegin(GL.GL_LINE_LOOP);
            drawCircle(drawable, p.x, p.y, 0.01, 12);
            gl.glEnd();
        }

        // g.drawString("Number of particles: " + particles.size(), 15, 15);
        // g.drawString("Frame: " + frame, 15, 30);
    }

    public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {
        // TODO Auto-generated method stub
        
    }

    public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2) {
        // TODO Auto-generated method stub
        
    }

    public void setVisible(boolean b) {
        panel.setVisible(b);
    }

}
