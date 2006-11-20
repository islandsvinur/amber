package amber.showoff;

import java.awt.Color;
import java.awt.Container;

import javax.swing.JApplet;

/**
 * Implements an applet (!) containing the visualization module. Can thus be
 * used to display on a website.
 * 
 * @author christian
 * 
 */
public class Applet extends JApplet {

    private static final long serialVersionUID = -7090111642724235796L;
    
    private EarthViewWrapper evw;

    public Applet() {
        evw = new EarthViewWrapper("Applet", "luijten.local",
                10000);

        Container mainpane = this.getContentPane();

        mainpane.setBackground(Color.black);

        mainpane.add(evw.earthView);
        this.setVisible(true);
    }
    
    public void init() {
        evw.start();
    }
    
    public void destroy() {
        evw.stop();
    }

}
