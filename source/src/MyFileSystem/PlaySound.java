/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package MyFileSystem;

import java.io.IOException;
import java.io.InputStream;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.control.ToneControl;

/**
 *
 * @author Soladnet Software Corp. <soladnet@gmail.com>
 */
public class PlaySound {
    private Player p1, p2,p3;
    public PlaySound(){
        
        realizeP1();
        realizeP2();
        realizeP3();
    }
    private void realizeP1(){
        try {

            p1 = Manager.createPlayer(getClass().getResourceAsStream("/received.wav"), "audio/x-wav");
            p1.realize();
            p1.prefetch();
            
        }catch(IOException ioe){}catch(MediaException me){}
    }
    private void realizeP2(){
        try {
            p1 = Manager.createPlayer(getClass().getResourceAsStream("/group.wav"), "audio/x-wav");
            p1.realize();
            p2.prefetch();
        }catch(IOException ioe){}catch(MediaException me){}
    }
    private void realizeP3(){
        try {
            p3 = Manager.createPlayer(getClass().getResourceAsStream("/click.wav"), "audio/x-wav");
            p3.realize();
            p3.prefetch();
        }catch(IOException ioe){}catch(MediaException me){}
    }
    public void startP1(){
        try {
            p1.start();
        } catch (MediaException ex) {}
    }
    public void startP2(){
        try {
            p2.start();
        } catch (MediaException ex) {}
    }
    public void startP3(){
        try {
            p3.start();
        } catch (MediaException ex) {}
    }
}
