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
