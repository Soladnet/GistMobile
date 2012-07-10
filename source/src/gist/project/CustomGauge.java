package gist.project;

import java.io.IOException;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

class CustomGauge 
	extends Canvas
	implements Runnable {


    Thread thread;
    Image[] frames;  //the array of frames
    int nextFrame;  //the running thread updates the next Frame
    
    public CustomGauge() {
    	frames = new Image[12];
        nextFrame = 0;
        // The array of frames is filled up from the resource png files
        for(int i = 0; i < 12; i++) {     	
	        try {
				frames[i] = Image.createImage("/frame" + (i+1) + ".png");				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        //The thread moves from one frame to the next
        thread = new Thread(this);
        thread.start();
    }
	public void run() {
		
		while (true) {
			try {
				//start over if the last frame is active
				if(nextFrame == 11) {
					nextFrame = 0;
				}
				//otherwise increse frame number
				else {
					nextFrame++;
				}
				repaint();
				//animation speed
				Thread.sleep(70);
			} 
			catch (InterruptedException e) {
				break;
			}
		}
	}

	protected void paint(Graphics g) {
    	//draws the next frame at the center of screen. The images have width 133 and height 135 pixels
    	g.drawImage(frames[nextFrame], (getWidth() / 2) - 67, getHeight()/2 - 67, Graphics.TOP|Graphics.LEFT);
	}
}
