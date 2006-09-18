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

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

public class EarthView extends Canvas implements Runnable {
	
	Graphics offGraphics;
	Image offImage;
	Dimension offDimension;
	private int delay = 100;
	private Thread animator;
	private int frame = 0;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void start() {
		animator = new Thread(this);
		animator.start();
	}

	public void paintFrame(Graphics g) {
		Dimension d = getSize();
				
		int earthRadius = 50;
		int orbitRadius = 300;
			
		g.setColor(Color.white);
		g.fillOval((d.width - earthRadius) / 2, 
				   (d.height - earthRadius) / 2, 
				   earthRadius, 
				   earthRadius);
		
		g.fillOval((int) ((d.width - earthRadius) / 2 + orbitRadius * Math.sin(((float) frame) / 25.)), 
				   (int) ((d.height - earthRadius) / 2 + orbitRadius * Math.cos(((float) frame) / 25.)), 
				   10, 
				   10);
	
	}
	
	public void paint(Graphics g) {
		update(g);
	}
	
	public void update(Graphics g) {
		Dimension d = getSize();
		
		if (offGraphics == null) {
			offDimension = d;
			offImage = createImage(d.width, d.height);
			offGraphics = offImage.getGraphics();
		}
		
		offGraphics.setColor(getBackground());
		offGraphics.fillRect(0,0,d.width,d.height);
		offGraphics.setColor(Color.black);
		
		paintFrame(offGraphics);
		g.drawImage(offImage, 0, 0, null);
	}

	public void run() {
		long tm = System.currentTimeMillis();
		while (Thread.currentThread() == animator) {
			repaint();
			
			try {
				tm += delay;
				Thread.sleep(Math.max(0, tm - System.currentTimeMillis()));
			} catch (InterruptedException e) {
				break;
			}
			frame++;
		}
		
	}
}