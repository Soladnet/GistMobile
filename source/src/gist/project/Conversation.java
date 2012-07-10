/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gist.project;

import de.enough.polish.io.Serializable;

/**
 *
 * @author Soladnet
 */
public class Conversation implements Serializable{
    private String screenPosition,sender,dateTime,status,message,groupId="";
    private String senderName;
    
    public Conversation(String screenPosition,String sender,String dateTime,String status,String message){
        this.screenPosition = screenPosition;
        this.sender = sender;
        this.dateTime = dateTime;
        this.status = status;
        this.message = message;
    }
    public String getScreenPosition(){
        return screenPosition;
    }
    public String getSender(){
        return sender;
    }
    public String getDateTime(){
        return dateTime;
    }
    public String getStatus(){
        return status;
    }
    public String getMessage(){
        return message;
    }
    public void setGroupId(String id){
        groupId = id;
    }
    public String getGroupId(){
        return groupId;
    }
    public void setSenderName(String senderName){
        this.senderName = senderName;
    }
    public String getSenderName(){
        return this.senderName;
    }
    public void setStatus(String newStatus){
        this.status = newStatus;
    }
}
