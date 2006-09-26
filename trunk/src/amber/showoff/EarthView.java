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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.JPanel;
import javax.vecmath.Point2d;

import amber.common.Story;
import amber.showoff.Particle.ParticleException;

public class EarthView extends JPanel implements Runnable {

    private static final long serialVersionUID = -3882808552754900513L;

    Graphics offGraphics;

    Image offImage;

    Dimension offDimension;

    private int frameDelay = 40;

    private Double scalingFactor = 1.0;

    private Thread animator;

    private int frame = 0;

    private LinkedList<Particle> particles;

    private Queue<Story> storyQueue;

    public void start() {
	setBackground(Color.black);
	setForeground(Color.white);
	particles = new LinkedList<Particle>();
	animator = new Thread(this);
	animator.start();
    }

    public void paintComponent(Graphics g) {
	int earthRadius = 50;
	int orbitRadius = 100;
	Dimension d = getSize();

	g.setColor(getBackground());
	g.fillRect(0, 0, d.width, d.height);

	g.setColor(getForeground());
	g.fillOval((d.width - earthRadius) / 2, (d.height - earthRadius) / 2,
		earthRadius, earthRadius);

	if (particles != null) {
	    Iterator<Particle> i = particles.iterator();
	    while (i.hasNext()) {
		drawParticle(g, i.next());
	    }
	}

	g.fillOval((int) ((d.width - earthRadius) / 2 + orbitRadius
		* Math.sin(((float) frame) / 25.)),
		(int) ((d.height - earthRadius) / 2 + orbitRadius
			* Math.cos(((float) frame) / 25.)), 10, 10);

	g.drawString("Frame " + frame, 30, 30);
    }

    public void drawParticle(Graphics g, Particle p) {
	int diameter = (int) (Math.cbrt(p.getMass() / p.getDensity()) * scalingFactor);
	Point2d loc = (Point2d) p.getLocation().clone();
	loc.scale(scalingFactor);

	g.fillOval((int) (loc.x - diameter / 2), (int) (loc.y - diameter / 2),
		diameter, diameter);
    }

    public void run() {
	long tm = System.currentTimeMillis();
	while (Thread.currentThread() == animator) {
	    repaint();

	    try {
		Particle p;
		tm += frameDelay;
		Iterator<Particle> i = particles.iterator();
		while (i.hasNext()) {
		    p = i.next();
		    p.setNewValuesAfter((double) (frameDelay) / 1000.0);
		    if (p.crashed()) {
			particles.remove(p);
		    }
		}
		if (!storyQueue.isEmpty()) {
		    Story s = storyQueue.remove();
		    p = new Particle(s);
		    calculateLaunchParameters(p, s);
		    p.launch();
		    particles.add(p);
		}

		Thread.sleep(Math.max(0, tm - System.currentTimeMillis()));
	    } catch (InterruptedException e) {
		break;
	    }
	    frame++;
	}

    }

    private void calculateLaunchParameters(Particle p, Story s) {
	// TODO Auto-generated method stub
	try {
	    p.setLocation(10.0, 10.0);
	    p.setVelocity(20.0, 20.0);
	    p.setAccelleration(1.0, 1.0);
	} catch (ParticleException pe) {

	}
    }

    public void setParticleCollection(LinkedList<Particle> pc) {
	particles = pc;
    }

    public void setStoryQueue(Queue<Story> sq) {
	storyQueue = sq;
    }
}
