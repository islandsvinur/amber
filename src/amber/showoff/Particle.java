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

import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import amber.common.Polar2d;
import amber.common.Story;

/**
 * Representation of a Story in the EarthView visualization. When initialized,
 * the particle will get some parameters to be launched. Upon launch, the
 * particle will move according to the equations of motion (v = v + a * t, s =
 * 1/2 a * t^2 etc.).
 * 
 * If the story gets an analysis, the particle will be attracted to at least one
 * of the attractors around the center. If it doesn't get an analysis, the
 * particle will quickly fall down, crash and disappear. Relevant particles
 * (i.e. the ones with analysis) will stay around for about 2 days (which is a
 * constant to be chosen freely).
 * 
 * @author christian
 * 
 */
public class Particle {

    private Polar2d accel = null, velocity = null, location = null;

    private Point2d locationCartesian = null;

    private State state = State.NEW;

    private Double mass = 0.0;

    private EarthViewStory story;

    private boolean bound = false;

    private final static Double CRASH_HEIGHT = 30.0;

    private final static int MAXIMUM_ORBITS_UNBOUND = 25;

    /** Particles of relevant stories should crash 48 hours after launch */
    private final static int LIFE_LENGTH_IN_MS = 2 * 24 * 60 * 60 * 1000;

    public enum State {
        NEW, LAUNCH, ORBITING, CRASHING, CRASHED
    };

    private Date crashTime;

    /**
     * @param s
     *            the story which the particle should visualize
     */
    public Particle(EarthViewStory s) {
        story = s;
        locationCartesian = new Point2d(0, 0);
        location = new Polar2d(0, 2 * Math.random() * Math.PI);
        velocity = new Polar2d(0, 0);
        accel = new Polar2d(0, 0);

        s.setParticle(this);
        crashTime = new Date();

    }

    /** Indicate that a particle is bound to at least one analysis */
    public void bind() {
        bound = true;
    }

    /**
     * @return the cartesian location of the particle
     */
    public Point2d getLocation() {
        return locationCartesian;
        // return location.toCartesianPoint();
    }

    /**
     * Particles get displaced by the attractors. This method calculates the
     * exact displacement.
     * 
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
     * Method to check whether the particle has been launched
     * 
     * @return true if the particle has been launched
     */
    public boolean isLaunched() {
        return state.compareTo(State.LAUNCH) > 0;
    }

    public boolean isBound() {
        return bound;
    }

    /** Launch the particle */
    public void launch() {
        velocity.r = 30.0 + Math.random();
        velocity.theta = -2.0;

        accel.theta = 0.75;
        accel.r = -7.5 - Math.random();

        state = State.LAUNCH;

        // particle should crash some certain amount time after publication of
        // its story
        if (story.getPublicationDate() != null) {
            crashTime.setTime(story.getPublicationDate().getTime()
                    + LIFE_LENGTH_IN_MS);
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

        if (state == State.LAUNCH)
            velocity.theta = velocity.theta + accel.theta * time;
        else
            velocity.theta = keplerRotation(location.r);

        if (state == State.LAUNCH && velocity.r < 0.1) {
            state = State.ORBITING;
            accel.r = 0;
            accel.theta = 0;
            velocity.r = 0;
        }

        if (state == State.ORBITING) {
            if ((!bound && location.theta > MAXIMUM_ORBITS_UNBOUND)
                    || crashTime.before(new Date())) {
                accel.r = -5.0;
                state = State.CRASHING;
            }
        }

        if (state == State.CRASHING) {
            // Here, fade-out can be implemented instead of falling down
            if (location.r < CRASH_HEIGHT) {
                location.r = 0;
                location.theta = 0;
                state = State.CRASHED;
            }
        }

        if (state.compareTo(State.LAUNCH) > 0) {
            if (Math.abs(accel.theta) < 0.1)
                accel.theta = 0;
            if (Math.abs(accel.r) < 0.1)
                accel.r = 0;
            if (Math.abs(velocity.theta) < 0.1)
                velocity.theta = 0;
            if (Math.abs(velocity.r) < 0.1)
                velocity.r = 0;
        }

        if (state == State.ORBITING)
            locationCartesian = displaceParticle();
        else
            locationCartesian = location.toCartesianPoint();

    }

    /**
     * @return true when the particle has crashed, false otherwise
     */
    public boolean crashed() {
        return (state == State.CRASHED);
    }

    /**
     * @return the cartesian velocity of the particle
     */
    public Vector2d getVelocity() {
        return velocity.toCartesianVector();
    }

}
