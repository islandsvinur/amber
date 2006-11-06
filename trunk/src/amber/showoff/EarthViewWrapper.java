package amber.showoff;

import amber.ShowOff;
import amber.common.Polar2d;

public class EarthViewWrapper extends ShowOff {

    final public EarthView earthView;
    
    public EarthViewWrapper(String moduleName, String hostname, Integer port) {
        super(moduleName, hostname, port);

        earthView = new EarthView(storyQueue, analysisQueue);
        earthView.setVisible(true);

        if (airBrush.hasParameter("Attractors")) {
            String att = airBrush.getParameterString("Attractors");
            String[] attr = att.split(",");
            int attrCount = attr.length;
            for (Integer i = 0; i < attrCount; i++) {
                String[] keyVal = attr[i].split(":");
                earthView.addAttractor(new Polar2d(150.0, 2
                        * (i.doubleValue() / attrCount) * Math.PI), Double
                        .parseDouble(keyVal[1]), keyVal[0]);
            }

        } else {
            System.err.println("No attractors defined! Are you sure this is what you wanted?");
        }
        
    }
    
    public void start() {
        super.start();
        earthView.start();
    }

}
