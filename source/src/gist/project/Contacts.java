/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gist.project;

import de.enough.polish.io.Serializable;

/**
 *
 * @author Soladnet Software Corporation
 */
public class Contacts implements Serializable{

    String username, name, gender, personalMsg, phone, online;

    public Contacts(String username, String name, String gender, String phone, String personalMsg) {
        this.username = username;
        this.name = name;
        this.gender = gender;
        this.phone = phone;
        this.online = "Y";
        this.personalMsg = personalMsg;
    }

    public Contacts(String name, String phone) {
        this.username = "";
        this.name = name;
        this.gender = "";
        this.personalMsg = "nwfrq";
        this.phone = phone;
        this.online = "N";
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public String getPhone() {
        return this.phone;
    }

    public String setPhone(String phone) {
        this.phone = phone;
        return this.phone;
    }

    public String getPersonalMsg() {
        return personalMsg;
    }

    public String getOnlineStatus() {
        return this.online;
    }

    public String setName(String name) {
        this.name = name;
        return getName();
    }

    public String setUsername(String username) {
        this.username = username;
        return getUsername();
    }

    public String setGender(String gender) {
        this.gender = gender;
        return getGender();
    }

    public String setPersonalMsg(String status) {
        this.personalMsg = status;
        return getPersonalMsg();
    }

    public boolean setOnGistStatus(String status) {
        this.online = status;
        return isOnGist();
    }

    public boolean isOnGist() {
        return online.equals("Y");
    }
}
