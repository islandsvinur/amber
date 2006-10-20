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

import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import amber.common.Polar2d;
import amber.common.Story;

public class Particle {

    private Polar2d accel = null, velocity = null, location = null;

    private int state = STATE_NEW;

    private Double mass = 0.0;

    private EarthViewStory story;

    private final static Double DAMPING_FACTOR = 0.99975;

    private final static Double ORBITAL_HEIGHT = 200.0;

    private final static Double CRASH_HEIGHT = 20.0;

    private final static int STATE_NEW = 0;

    private final static int STATE_LAUNCH = 1;

    private final static int STATE_ORBITING = 2;

    private final static int STATE_BOOSTED = 3;

    private final static int STATE_CRASHED = 4;

    public Particle(EarthViewStory s) {
        story = s;
        location = new Polar2d(0, 2 * Math.random() * Math.PI);
        velocity = new Polar2d(0, 0);
        accel = new Polar2d(0, 0);
    }

    public void boost(Double f) {
        state = STATE_BOOSTED;
        accel.r = f;
    }

    public Point2d getLocation() {
        return location.toCartesianPoint();
    }

    private Polar2d displaceParticle() {
        Set<Entry<String, Attractor>> attr = EarthView.attractors.entrySet();
        Iterator<Entry<String, Attractor>> i = attr.iterator();
        Polar2d p = location.clone();

        while (i.hasNext()) {
            Entry<String, Attractor> e = i.next();
            String topic = e.getKey();
            Attractor a = e.getValue();
            Double w = story.getWeight(topic);

            if (w != null) {
                Vector2d v1 = a.location.toCartesianVector();
                Vector2d v2 = location.toCartesianVector();
                Vector2d v = new Vector2d();
                v.scaleAdd(w, v1, v2);
                p.addCartesianVector(v);
            }
        }
        return p;
    }

    public Double getMass() {
        return mass;
    }

    public Story getStory() {
        return story;
    }

    public void setStory(EarthViewStory s) {
        story = s;
    }

    public boolean isLaunched() {
        return state >= STATE_LAUNCH;
    }

    public void launch() {
        velocity.theta = -3.0;
        velocity.r = 100.0;

        accel.theta = 1.25;
        accel.r = -20.0;
        state = STATE_LAUNCH;
    }

    public void setMass(Double m) {
        mass = m;
    }

    public void calculate(Double time) {

        switch (state) {
        case STATE_LAUNCH:
            // Bring particle to speed and altitude
            location.r = location.r + velocity.r * time + accel.r * time * time;
            location.theta = location.theta + velocity.theta * time
                    + accel.theta * time * time;

            velocity.r = velocity.r + accel.r * time;
            velocity.theta = velocity.theta + accel.theta * time;

            accel.r = accel.r * DAMPING_FACTOR;
            accel.theta = accel.theta * DAMPING_FACTOR;

            if (location.r > ORBITAL_HEIGHT) {
                state = STATE_ORBITING;
                accel.r = 0;
                velocity.r = 0;
            }
            break;
        case STATE_BOOSTED:
            // Particle must be boosted into higher orbit
            accel.r = accel.r * 0.75 * DAMPING_FACTOR;

            if (accel.r < 0.005) {
                accel.r = 0;
                velocity.r = 0;
                state = STATE_ORBITING;
            }
        // No break here: Only acceleration is set now, also do others
        case STATE_ORBITING:
            // Keep particle in orbit, very slowly slowing and falling down
            location.theta = location.theta + velocity.theta * time
                    + accel.theta * time * time;
            location.r = (location.r + velocity.r * time + accel.r * time
                    * time)
                    * DAMPING_FACTOR;

            velocity.theta = (1 / Math.sqrt(location.r * location.r
                    * location.r)) * 500.0;
            velocity.r = velocity.r + accel.r * time;

            if (location.r < CRASH_HEIGHT) {
                // System.out.println("Particle crashed! Location (" +
                // location.r + ", " + location.theta + "), velocity (" +
                // velocity.r + ", " + velocity.theta + ")");
                state = STATE_CRASHED;
                location.r = 0;
                location.theta = 0;
            }
            break;
        default:
            break;
        }

        if (Math.abs(accel.theta) < 0.01)
            accel.theta = 0;
        if (Math.abs(accel.r) < 0.01)
            accel.r = 0;
        if (Math.abs(velocity.theta) < 0.01)
            velocity.theta = 0;
        if (Math.abs(velocity.r) < 0.01)
            velocity.r = 0;

        displaceParticle();
    }

    public boolean crashed() {
        return (state == STATE_CRASHED);
    }

    public Vector2d getVelocity() {
        return velocity.toCartesianVector();
    }

}
