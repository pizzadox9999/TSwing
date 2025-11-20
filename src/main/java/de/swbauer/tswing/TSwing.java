package de.swbauer.tswing;

import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;

public class TSwing {

    public static void main(String[] args) {
        Frame frame = new Frame("TEST");
        
        Panel p = new Panel(new FlowLayout());
        frame.add(p);
        
        Button b = new Button("test");
        p.add(b);
        
        p.add(new Label("LABEL_TEST"));
        
        frame.setVisible(true);
    }
}
