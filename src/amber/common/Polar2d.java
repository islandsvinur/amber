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

package amber.common;

import javax.vecmath.Point2d;
import javax.vecmath.Tuple2d;
import javax.vecmath.Vector2d;

public class Polar2d {

    public double theta = 0.0;

    public double r = 0.0;

    public Polar2d(double r, double theta) {
        this.r = r;
        this.theta = theta;
    }

    public Polar2d(double[] v) {
        r = v[0];
        theta = v[1];
    }

    public Polar2d() {

    }
    
    public void scale(double f) {
        r = r * f;
    }

    static public Polar2d fromCartesianTuple(Tuple2d t) {
        return new Polar2d(Math.atan2(t.y, t.x), Math.sqrt(t.x * t.x + t.y
                * t.y));
    }

    private void toCartesian(Tuple2d t) {
        t.x = r * Math.cos(theta);
        t.y = r * Math.sin(theta);
    }

    public Point2d toCartesianPoint() {
        Point2d p = new Point2d();
        toCartesian(p);
        return p;
    }

    public Vector2d toCartesianVector() {
        Vector2d v = new Vector2d();
        toCartesian(v);
        return v;
    }
    
    public Polar2d add(Polar2d p2) {
        Polar2d p1 = new Polar2d();
        
        p1.theta = theta + p2.theta;
        p2.r = r + p2.r;
        
        return p1;
    }
    
    public Polar2d minus(Polar2d p2) {
        Polar2d p1 = new Polar2d();
        
        p1.theta = theta - p2.theta;
        p2.r = r - p2.r;
        
        return p1;
    }
    
    public Polar2d clone() {
        return new Polar2d(r, theta);
    }

    public void addCartesianVector(Vector2d v) {
        // TODO Auto-generated method stub
        
    }

}
