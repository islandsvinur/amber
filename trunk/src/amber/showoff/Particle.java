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

import javax.vecmath.Point2d;
import javax.vecmath.Tuple2d;
import javax.vecmath.Vector2d;

import amber.common.Story;

public class Particle {
    @SuppressWarnings("serial")
    public class ParticleAlreadyLaunchedException extends ParticleException {

    }

    @SuppressWarnings("serial")
    public class ParticleException extends Exception {

    }

    private Vector2d accel = null, velocity = null;

    private Point2d location = null;

    private int state = STATE_NEW;

    private Double mass = 0.0, density = 0.0;

    private Story story;

    private Double timeSinceCreation = 0.0;
    
    private final static Double DAMPING_FACTOR = 0.999;
    
    private final static Double ORBITAL_HEIGHT = 2.0;
   
    private final static int STATE_NEW = 0;
    private final static int STATE_LAUNCH = 1;
    private final static int STATE_ORBITING = 2;
    private final static int STATE_BOOSTED = 3;
    private final static int STATE_CRASHED = 4;

    public Particle(Story s) {
        story = s;
        location = new Point2d(2 * Math.random() * Math.PI,0);
        velocity = new Vector2d(0,0);
        accel = new Vector2d(1,1);
    }

    public void boost(Double f) {
        //velocity.scale(f);
    }

    private void checkLaunched() throws ParticleAlreadyLaunchedException {
        if (isLaunched()) {
            throw new ParticleAlreadyLaunchedException();
        }
    }

    public Double getDensity() {
        return density;
    }

    public void setDensity(Double d) throws ParticleException {
        checkLaunched();
        density = d;
    }

    public Tuple2d getAccelleration() {
        return accel;
    }

    public Point2d getLocation() {
        Point2d loc = new Point2d();
        loc.x = location.y * Math.cos(location.x);
        loc.y = location.y * Math.sin(location.x);
        return loc;
    }

    public Double getMass() {
        return mass;
    }

    public Story getStory() {
        return story;
    }

    public Vector2d getVelocity() {
        return velocity;
    }

    public boolean isLaunched() {
        return state >= STATE_LAUNCH;
    }

    
    public void launch() {
        accel.x = 2;
        accel.y = 1;
        state = STATE_LAUNCH;
    }

    public void setMass(Double m) {
        mass = m;
    }
    
    public void calculate(Double time) {
        switch (state) {
            case STATE_LAUNCH:
                // Bring particle to speed and altitude
                location.x = location.x + velocity.x * time + accel.x * time * time;
                location.y = location.y + velocity.y * time + accel.y * time * time;
                
                velocity.x = velocity.x + accel.x * time;
                velocity.y = velocity.y + accel.y * time;
                
                accel.x = accel.x * DAMPING_FACTOR;
                accel.y = accel.y * DAMPING_FACTOR;
                
                if (location.y > ORBITAL_HEIGHT) {
                    state = STATE_ORBITING;
                    accel.y = 0;
                    velocity.y = 0;
                }
                break;
            case STATE_ORBITING: 
                // Keep particle in orbit, very slowly slowing and falling down
                location.x = location.x + velocity.x * time;
                location.y = location.y * DAMPING_FACTOR;
                velocity.x = velocity.x * DAMPING_FACTOR;
                
                if (location.y < ORBITAL_HEIGHT / 4) {
                    System.out.println("Particle crashed! " + location.x + ", " + location.y);
                    state = STATE_CRASHED;
                }
                break;
            case STATE_BOOSTED:
                // Particle must be boosted into higher orbit
                break;
            default: break;
        }
    }

    public void setNewValuesAfter(Double time) {
        timeSinceCreation += time;


    }

    public boolean crashed() {
        // TODO Auto-generated method stub
        return false;
    }

}
