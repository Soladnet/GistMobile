/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MyFileSystem;

import de.enough.polish.io.Serializable;
import gist.connector.SendToServer;
import gist.project.Contacts;
import gist.project.GistMobileMidlet;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.microedition.lcdui.AlertType;
import javax.microedition.pim.Contact;
import javax.microedition.pim.PIM;
import javax.microedition.pim.PIMList;

/**
 *
 * @author Soladnet Software Corp. <soladnet@gmail.com>
 */
public class PhoneContact implements Serializable{

    private Hashtable contactMap;

    public PhoneContact() {
        contactMap = new Hashtable();
    }

    /**
     * <p>This method takes a ContactList Object and extract all phone numbers
     * and FORMATTD_NAME in the Contact of the device.</p> <p>This is only
     * applicable to contacts on the device<p> <p><strong>returns</strong> a
     * Hashtable with FORMATED_NAME to TELL <p>Copyright Soladnet Software Corp
     * 2012</p>
     *
     * @author Soladnet Soft. Corp, development@soladnet.com
     */
    public Hashtable readContacts() {
        PIM pim = PIM.getInstance();
        PIMList pimlist;
        Contact contact;
        String contactName = "";
        //String[] lists = pim.listPIMLists(PIM.CONTACT_LIST);

        try {
            pimlist = pim.openPIMList(PIM.CONTACT_LIST, PIM.READ_WRITE);
            Enumeration enumeration = pimlist.items();
            //for (int i = 0; i < lists.length; i++){
            while (enumeration.hasMoreElements()) {
                contact = (Contact) enumeration.nextElement();
                try {
                    if (pimlist.isSupportedField(Contact.FORMATTED_NAME)) {
                        if ((contact.countValues(Contact.FORMATTED_NAME) > 0)) {
                            contactName = contact.getString(Contact.FORMATTED_NAME, 0);
                        }
                    } else if (pimlist.isSupportedField(Contact.NAME)) {
                        StringBuffer name = new StringBuffer();
                        //Contact contact = (Contact)contactEnum.nextElement();
                        String[] names = contact.getStringArray(Contact.NAME, 0);
                        boolean found = false;
                        String nameseg;
                        if ((nameseg = names[Contact.NAME_PREFIX]) != null) {
                            name.append(nameseg);
                            found = true;
                        }
                        if ((nameseg = names[Contact.NAME_GIVEN]) != null) {
                            if (found) {
                                name.append(' ');
                            }
                            name.append(nameseg);
                            found = true;
                        }
                        if ((nameseg = names[Contact.NAME_FAMILY]) != null) {
                            if (found) {
                                name.append(' ');
                            }
                            name.append(nameseg);
                        }
                        contactName = name.toString();
                    }
                } catch (Exception e) {
                }
                int phoneNos = contact.countValues(Contact.TEL);
                try {
                    if (pimlist.isSupportedField(Contact.TEL) && (contact.countValues(Contact.TEL) > 0)) {
                        for (int j = 0; j < phoneNos; j++) {
                            String phn = contact.getString(Contact.TEL, j);
                            if (phn.startsWith("+")) {
                                phn = phn.substring(1);
                            } else {
                                if (phn.startsWith("0")) {
                                    phn = SendToServer.getPhoneCode() + phn.substring(1);
                                } else {
                                    phn = SendToServer.getPhoneCode() + phn;
                                }
                            }
                            if (phn.length() > 10) {
                                contactMap.put(phn, new Contacts(contactName, phn));
                            }
                        }
                    }
                } catch (Exception e) {
                }


            }
            //}
        } catch (SecurityException se){
            GistMobileMidlet gm = null;
            gm.displayAlert("Security Exception", "Please enable neccesary permission for this application to run smoothly", null, AlertType.ALARM, -1);
        }catch (Exception e) {
        }

        return contactMap;
    }
}
