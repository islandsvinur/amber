package amber.showoff;

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
        FullScreen mod = new FullScreen("Applet", "localhost", 10000);
        mod.start();
    }

}
