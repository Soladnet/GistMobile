/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MyFileSystem;

import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.io.file.FileSystemRegistry;

/**
 *
 * @author Soladnet Software Corp. <soladnet@gmail.com>
 */
public class FetchFiles {
    public static Vector getRoot(){
        Vector vec = new Vector();
        Enumeration rootDirectories = FileSystemRegistry.listRoots();
        while (rootDirectories.hasMoreElements())
            vec.addElement((String)rootDirectories.nextElement());
        
        return vec;
    }
}
