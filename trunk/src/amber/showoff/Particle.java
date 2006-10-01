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

    private Vector2d accel = null;

    private boolean launched = false;

    private Point2d location = null;

    private Double mass = null;

    private Double density = null;

    private Story story;

    private Vector2d velocity = null;

    private Double timeSinceCreation = 0.0;

    public Particle(Story s) {
        story = s;
    }

    public void boost(Double f) {
        velocity.scale(f);
    }

    private void checkLaunched() throws ParticleAlreadyLaunchedException {
        if (launched) {
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
        return location;
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
        return launched;
    }

    public void launch() {
        launched = true;
    }

    public void setAccelleration(Double x, Double y) throws ParticleException {
        setAccelleration(new Vector2d(x, y));
    }

    private void setAccelleration(Vector2d a) throws ParticleException {
        checkLaunched();
        accel = a;
    }

    public void setLocation(Double x, Double y) throws ParticleException {
        setLocation(new Point2d(x, y));
    }

    private void setLocation(Point2d l) throws ParticleException {
        checkLaunched();
        location = l;
    }

    public void setMass(Double m) {
        mass = m;
    }

    public void setNewValuesAfter(Double time) {
        timeSinceCreation += time;

        location.x = Math.sin(timeSinceCreation);
        location.y = Math.cos(timeSinceCreation);
        velocity.x = -1 * Math.cos(timeSinceCreation);
        velocity.y = Math.sin(timeSinceCreation);
        accel.x = -1 * Math.sin(timeSinceCreation);
        accel.y = -1 * Math.cos(timeSinceCreation);
    }

    public void setVelocity(Double x, Double y) throws ParticleException {
        setVelocity(new Vector2d(x, y));
    }

    private void setVelocity(Vector2d v) throws ParticleException {
        checkLaunched();
        velocity = v;
    }

    public boolean crashed() {
        // TODO Auto-generated method stub
        return false;
    }

}
