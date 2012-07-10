/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gist.project;

import de.enough.polish.io.Serializable;
import java.util.Hashtable;

/**
 *
 * @author Soladnet
 */
public class Groups implements Serializable{
    String id, name,i_am_admin,desc,createdDate;
    Hashtable creator;
    Hashtable members;
    public Groups(String id,String name,Hashtable creator,String i_am_admin,String createdDate){
        this.id = id;
        this.name = name;
        this.creator = creator;
        this.members = new Hashtable();
        this.i_am_admin = i_am_admin;
        this.createdDate = createdDate;
        this.desc = "No Description";
    }
    public String getName(){
        return name;
    }
    public String getId(){
        return id;
    }
    public Hashtable getCreator(){
        return creator;
    }
    public String setId(String newId){
        String oldId = this.id;
        this.id = newId;
        return oldId;
    }
    public Hashtable setCreator(Hashtable newCreator){
        Hashtable oldCretor = this.creator;
        this.creator = newCreator;
        return oldCretor;
    }
    public String setName(String newName){
        String oldName = this.name;
        this.name = newName;
        return oldName;
    }
    public Hashtable addMember(Contacts cont){
        this.members.put(cont.getUsername(), cont);
        return this.members;
    }
    public Hashtable getMembers(){
        return this.members;
    }
    public String getAdminSatus(){
        return this.i_am_admin;
    }
    public String getCreatedDate(){
        return this.createdDate;
    }
    public boolean isAdmin(){
        return (getAdminSatus().equals("A") || getAdminSatus().equals("S"));
    }
}
