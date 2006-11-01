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

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import amber.ShowOff;
import amber.common.Polar2d;

/**
 * @author christian
 *
 */
public class FullScreen extends ShowOff {
    final private EarthView earthView;
    
    /**
     * @param moduleName
     * @param hostname
     * @param port
     */
    public FullScreen(String moduleName, String hostname, Integer port) {
        super("FullScreen." + moduleName, hostname, port);
        
        JFrame main = new JFrame();
        main.setSize(500, 500);
        /* JWindow main = new JWindow();
        GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(main); */

        Container mainpane = main.getContentPane();

        mainpane.setBackground(Color.black);

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.PAGE_AXIS));
        list.setBackground(Color.black);

        JButton b = new JButton();
        b.setText("Quit");
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            };
        });

        earthView = new EarthView(storyQueue, analysisQueue);
        earthView.setVisible(true);
        
        if (airBrush.hasParameter("Attractors")) {
            earthView.addAttractor(new Polar2d(150.0, 0.25 * Math.PI), 10.0, "MacBook");
            earthView.addAttractor(new Polar2d(150.0, 1.25 * Math.PI), 10.0, "iPod");
        }
        
        list.add(earthView);
        list.add(b);

        mainpane.add(list);
        main.setVisible(true);
    }

    public void start() {
        earthView.start();
        super.start();
    }

}
