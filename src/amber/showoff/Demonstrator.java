package amber.showoff;

import java.awt.Color;
import java.awt.Container;

import javax.swing.JApplet;

import amber.common.Story;

public class Demonstrator extends JApplet implements Runnable {

    private static final long serialVersionUID = -6492789321739444369L;
    static StoryQueue storyQueue;
    static Thread thread;
    
    public Demonstrator() {
        Container mainpane = getContentPane();

        mainpane.setBackground(Color.black);
        
        storyQueue = new StoryQueue();
        Story s = new Story();
        storyQueue.add(s);
        
        EarthView earthView = new EarthView(storyQueue);
        earthView.setVisible(true);
                
        mainpane.add(earthView);
        setVisible(true);
        
        thread = new Thread(this);
        thread.start();
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        new Demonstrator();
    }

    public void run() {
        
        while (Thread.currentThread() == thread) {
            Story s = new Story();
            storyQueue.add(s);
            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
    }

}
