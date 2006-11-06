package amber.showoff;

import java.awt.Color;
import java.awt.Container;

import javax.swing.JApplet;

/**
 * @author christian
 *
 */
public class Applet extends JApplet {

    /**
     * 
     */
    private static final long serialVersionUID = -7090111642724235796L;

    /**
     * @param moduleName
     * @param hostname
     * @param port
     */
    public Applet() {
        EarthViewWrapper evw = new EarthViewWrapper("Applet", "localhost", 10000);

        Container mainpane = this.getContentPane();

        mainpane.setBackground(Color.black);

        mainpane.add(evw.earthView);
        this.setVisible(true);
        evw.start();
    }

}
