package amber.showoff;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class FullScreen {
    
    private JFrame mainUI = null;

    public FullScreen() {
        System.out.println("Hallo wereld");

        mainUI = new JFrame();
        mainUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel text = new JLabel();
        text.setText("Hello world");

        mainUI.getContentPane().add(text);

        mainUI.setSize(300,300);
        mainUI.setVisible(true);

    }

}
