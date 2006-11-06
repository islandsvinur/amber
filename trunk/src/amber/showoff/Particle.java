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
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import amber.common.Polar2d;
import amber.common.Story;

/**
 * @author christian
 * 
 */
public class Particle {

    private Polar2d accel = null, velocity = null, location = null;

    private Point2d locationCartesian = null;

    private int state = STATE_NEW;

    private Double mass = 0.0;

    private EarthViewStory story;

    Color color;

    private boolean bound = false;

    private final static Double CRASH_HEIGHT = 30.0;

    private final static int MAXIMUM_ORBITS_UNBOUND = 25;

    private final static int STATE_NEW = 0;

    private final static int STATE_LAUNCH = 1;

    private final static int STATE_ORBITING = 2;

    private final static int STATE_CRASHING = 3;

    private final static int STATE_CRASHED = 4;

    private Date crashTime;

    /**
     * @param s
     */
    public Particle(EarthViewStory s) {
        story = s;
        locationCartesian = new Point2d(0, 0);
        location = new Polar2d(0, 2 * Math.random() * Math.PI);
        velocity = new Polar2d(0, 0);
        accel = new Polar2d(0, 0);
        color = new Color(64, 64, 64);

        s.setParticle(this);
        crashTime = new Date();

    }

    /**
     * 
     */
    public void bind() {
        bound = true;
        color = new Color(255, 255, 127);
    }

    /**
     * @return the cartesian location of the particle
     */
    public Point2d getLocation() {
        return locationCartesian;
        // return location.toCartesianPoint();
    }

    /**
     * @return the cartesian coordinate point containing the actual location of
     *         a particle (after displacement by attractors)
     */
    private Point2d displaceParticle() {
        Polar2d v1 = location.clone();
        if (bound) {
            Set<Entry<String, Attractor>> attr = EarthView.attractors
                    .entrySet();
            Iterator<Entry<String, Attractor>> i = attr.iterator();

            while (i.hasNext()) {
                Entry<String, Attractor> e = i.next();
                String topic = e.getKey();
                Attractor a = e.getValue();
                Double w = story.getWeight(topic);
                Double f = a.force;

                if (w != null) {
                    Polar2d v2 = a.location;

                    Double t = Math.pow(Math.max(0, Math.cos(v1.theta
                            - v2.theta)), 10)
                            * w * f;

                    v1.r = (1 - t) * v1.r + t * v2.r;
                }

            }
            velocity.theta = keplerRotation(v1.r);
        }

        return v1.toCartesianPoint();
    }

    public Double getMass() {
        return mass;
    }

    public Story getStory() {
        return story;
    }

    /**
     * @param s
     */
    public void setStory(EarthViewStory s) {
        story = s;
    }

    /**
     * @return true if the particle has been launched
     */
    public boolean isLaunched() {
        return state >= STATE_LAUNCH;
    }

    /**
     * 
     */
    public void launch() {
        velocity.r = 30.0 + Math.random();
        velocity.theta = -2.0;

        accel.theta = 0.75;
        accel.r = -7.5 - Math.random();

        state = STATE_LAUNCH;

        // particle should crash 24 hours after publication of its story
        if (story.getPublicationDate() != null) {
            crashTime.setTime(story.getPublicationDate().getTime() + 24 * 60
                    * 60 * 1000);
        }

    }

    /**
     * @param m
     */
    public void setMass(Double m) {
        mass = m;
    }

    /**
     * @param r
     * @return the velocity according to Kepler's second law ("A line joining a
     *         planet and its star sweeps out equal areas during equal intervals
     *         of time")
     */
    private Double keplerRotation(Double r) {
        Double r2 = r * 1.5;
        return (1 / Math.sqrt(r2 * r2 * r2)) * 1000.0;
    }

    /**
     * @param time
     */
    public void calculate(Double time) {

        location.r = location.r + velocity.r * time + accel.r * time * time;
        location.theta = location.theta + velocity.theta * time + accel.theta
                * time * time;

        velocity.r = velocity.r + accel.r * time;

        if (state == STATE_LAUNCH)
            velocity.theta = velocity.theta + accel.theta * time;
        else
            velocity.theta = keplerRotation(location.r);

        if (state == STATE_LAUNCH && velocity.r < 0.1) {
            state = STATE_ORBITING;
            accel.r = 0;
            accel.theta = 0;
            velocity.r = 0;
        }

        if (state == STATE_ORBITING) {
            if ((!bound && location.theta > MAXIMUM_ORBITS_UNBOUND)
                    || crashTime.before(new Date())) {
                accel.r = -5.0;
                state = STATE_CRASHING;
            }
        }

        if (state == STATE_CRASHING) {
            // Here, fade-out can be implemented instead of falling down
            if (location.r < CRASH_HEIGHT) {
                location.r = 0;
                location.theta = 0;
                state = STATE_CRASHED;
            }
        }

        if (state > STATE_LAUNCH) {
            if (Math.abs(accel.theta) < 0.1)
                accel.theta = 0;
            if (Math.abs(accel.r) < 0.1)
                accel.r = 0;
            if (Math.abs(velocity.theta) < 0.1)
                velocity.theta = 0;
            if (Math.abs(velocity.r) < 0.1)
                velocity.r = 0;
        }

        if (state == STATE_ORBITING)
            locationCartesian = displaceParticle();
        else
            locationCartesian = location.toCartesianPoint();

    }

    /**
     * @return true when the particle has crashed, false otherwise
     */
    public boolean crashed() {
        return (state == STATE_CRASHED);
    }

    /**
     * @return the cartesian velocity of the particle
     */
    public Vector2d getVelocity() {
        return velocity.toCartesianVector();
    }

}
