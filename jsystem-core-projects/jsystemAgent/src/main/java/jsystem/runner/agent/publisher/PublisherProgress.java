/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.agent.publisher;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

public class PublisherProgress extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6714430673094192338L;
	private JProgressBar bar;
    private JLabel label;
    private int maxValue;
    private boolean stop;
	public PublisherProgress() {
		super();
		
        bar = new JProgressBar();
		bar.setStringPainted(true);
		bar.setBorder(BorderFactory.createRaisedBevelBorder());
		this.setBackground(new Color(0xf6, 0xf6, 0xf6));
        label=new JLabel("Publishing Progress",SwingConstants.CENTER);
        label.setBorder(BorderFactory.createRaisedBevelBorder());
        stop = false;
        add(bar);
	}
	public void setBarMinMax(int min, int max) {
		bar.setMinimum(min);
		bar.setMaximum(max);
		maxValue=max;
	}
	public int getMaxValue()
	{
		return maxValue;
	}
	public void setBarValue(int value) {
		bar.setValue(value);
	}
	 public static void main(String[] args) {
	    	new PublisherProgress();
	    }
    public void setLocation()
	{

	    Point p1 = getLocation();
	    Dimension d1 = Toolkit.getDefaultToolkit().getScreenSize();
	    Dimension d2 = getSize();
	 
	    int xx = p1.x+(d1.width-d2.width)/2;
	    int yy = p1.y+(d1.height-d2.height)/2;
	 
	    if (xx < 0) { xx = 0; }
	    if (yy < 0) { yy = 0; }
	 
	    setLocation(xx,yy);
	}

    public boolean stop(){
		return stop;
	}
    
    public void setStop(boolean stop){
    	this.stop = stop;
    }
    
}

