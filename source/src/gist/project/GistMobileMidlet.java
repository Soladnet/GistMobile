package gist.project;

import MyFileSystem.FetchFiles;
import MyFileSystem.HttpMultipartRequest;
import MyFileSystem.PhoneContact;
import de.enough.polish.io.RmsStorage;
import de.enough.polish.ui.ChoiceGroup;
import de.enough.polish.ui.Command;
import de.enough.polish.ui.Form;
import de.enough.polish.ui.Gauge;
import de.enough.polish.ui.ImageItem;
import de.enough.polish.ui.List;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.TextField;
import de.enough.polish.ui.Ticker;
import de.enough.polish.ui.*;
import de.enough.polish.util.Locale;
import de.enough.polish.util.TextUtil;
import gist.commands.CommandBuilder;
import gist.connector.SendSMS;
import gist.connector.SendToServer;
import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.ContentConnection;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

/**
 * <p>Demonstrates the usage of the J2ME Polish GistMobile screen.</p>
 *
 * <p>Copyright Soladnet Software Corporation 2012</p>
 *
 * @author Soladoye Abdulrasheed 2012
 */
public class GistMobileMidlet extends MIDlet implements CommandListener {

    private FramedForm chatScreen, flickrScreen, profile;
    private TextField chatTextField, inviteTxt, inviteNum, usernameField, passwordField;
    private Form invite, formLogin, transferForm;
    private Hashtable groupTrack, chatTrack, dataRMS;
    private Vector groupIds;
    private String currentPal = "", state;
    private List list;
    private String currentRoot = "";
    int friendState = 0;
//    private PlaySound soundPlayer;
    private FilteredList friendList;
    private ChoiceGroup cg;

    protected void startApp() throws MIDletStateChangeException {
//          soundPlayer = new PlaySound();
        init();
    }

    private void init() {
        groupTrack = new Hashtable();
        chatTrack = new Hashtable();
        Hashtable ht = new Hashtable();
        ht.put(new Integer(1), "One");
        ht.put(new Integer(3), "Three");
        ht.put(new Integer(2), "Two");
        Enumeration enu = ht.elements();
        while(enu.hasMoreElements()){
            System.out.println(enu.nextElement().toString());
        }
        openingScreen();
    }

    private void resetUserValues() {
        chatScreen = null;
        flickrScreen = null;
        profile = null;
        chatTextField = null;
        inviteTxt = null;
        inviteNum = null;
        usernameField = null;
        passwordField = null;
        invite = null;
        formLogin = null;
        transferForm = null;
        currentPal = null;
        state = null;
        list = null;
        friendList = null;
        cg = null;
    }

    private Displayable getScreen(Displayable screen, int screenNum, String msg) {
        if (screenNum == 0) {
            //#style mainMenuScreen
            this.formLogin = new Form("9gist");
            Image img;
            try {
                img = Image.createImage("/9gist.png");
            } catch (IOException ex) {
                img = null;
            }
            //#style imageAlign
            ImageItem imgitm = new ImageItem("", img, ImageItem.LAYOUT_CENTER, "9GIST");

            //#style textFields
            usernameField = new TextField(Locale.get("txt.username"), "soladnet", 40, TextField.PLAIN);
            usernameField.setNoNewLine(true);
            UiAccess.setTextfieldHelp(usernameField, Locale.get("input.help.username"));
            //#style textFields
            passwordField = new TextField(Locale.get("txt.password"), "12345", 40, TextField.PASSWORD);
            passwordField.setNoNewLine(true);
            UiAccess.setTextfieldHelp(passwordField, Locale.get("input.help.password"));
            //#style button
            StringItem loginButt = new StringItem("", Locale.get("cmd.login"), StringItem.BUTTON);
            formLogin.insert(0, imgitm);
            formLogin.insert(1, usernameField);
            formLogin.insert(2, passwordField);
            formLogin.insert(3, loginButt);
            loginButt.setDefaultCommand(CommandBuilder.getLoginCmd());
            formLogin.addCommand(CommandBuilder.getExitCmd());
            formLogin.setCommandListener(this);

            state = "login";
            screen = formLogin;
        } else if (screenNum == 1) {
            String name = Locale.get("main.title");
            String listMenu[] = new String[]{Locale.get("list.chat"), Locale.get("list.gsetting")};
            list = createMenuList(listMenu, List.IMPLICIT);
            list.setTitle(name);
            list.removeAllCommands();
            list.addCommand(List.SELECT_COMMAND);
            list.setCommandListener(this);
            list.addCommand(CommandBuilder.getExitCmd());
            list.addCommand(CommandBuilder.getLogoutCmd());
            //#style mailTicker
            list.setTicker(new Ticker(getflckr()));
            screen = list;
            state = "gistHome";
        } else if (screenNum == 2) {
            //View to show list after clicking chat
            List menu = createMenuList(getListNames(), List.IMPLICIT);
            menu.setTitle(Locale.get("list.chat"));
            list.removeAllCommands();
            list.addCommand(List.SELECT_COMMAND);
            menu.addCommand(CommandBuilder.getBackCmd());
            menu.addCommand(CommandBuilder.getLogoutCmd());
            menu.addCommand(CommandBuilder.getExitCmd());
            
            menu.setCommandListener(this);
            state = "Chat";
            //#style mailTicker
            menu.setTicker(new Ticker(getflckr()));
            screen = menu;

        } else if (screenNum == 3) {
            String[] mylist = new String[]{Locale.get("list.username"), Locale.get("list.mobileNumber")};
            List menu = createMenuList(mylist, List.IMPLICIT);
            menu.setTitle("Invite Friends");
            list.removeAllCommands();
            list.addCommand(List.SELECT_COMMAND);
            menu.addCommand(CommandBuilder.getBackCmd());
            menu.addCommand(CommandBuilder.getLogoutCmd());
            menu.addCommand(CommandBuilder.getExitCmd());
            menu.setCommandListener(this);
            //#style mailTicker
            menu.setTicker(new Ticker(getflckr()));
            state = "Invite Friends";
            screen = menu;
        } else if (screenNum == 4) {

            String[] mylist = new String[]{Locale.get("list.profile"), Locale.get("list.ppsetting")};
            List menu = createMenuList(mylist, List.IMPLICIT);
            menu.setTitle(Locale.get("list.setting"));
            list.removeAllCommands();
            list.addCommand(List.SELECT_COMMAND);
            menu.addCommand(CommandBuilder.getBackCmd());
            menu.addCommand(CommandBuilder.getLogoutCmd());
            menu.addCommand(CommandBuilder.getExitCmd());
            menu.setCommandListener(this);
            //#style mailTicker
            menu.setTicker(new Ticker(getflckr()));
            state = "Setting";
            screen = menu;
        } else if (screenNum == 6) {
            //#style myLabel
            profile = new FramedForm(Locale.get("list.profile"));
            //#style mailTicker
            profile.setTicker(new Ticker(getflckr()));
            profile.addCommand(CommandBuilder.getBackCmd());
            profile.setCommandListener(this);
            //#style notif
            profile.append(FramedForm.FRAME_CENTER, new StringItem("", Locale.get("txt.loadingContent")));
            if (state.equals("groupmembers")) {
                profile.setTitle(msg);
                state = "groupmembers";
            } else {
                state = "profile";
            }

            screen = profile;
        } else if (screenNum == 8) {
            String[] mylist = new String[]{Locale.get("list.changePass"), Locale.get("list.gistcredit"), Locale.get("list.flickrSetting")};
            List menu = createMenuList(mylist, List.IMPLICIT);
            menu.setTitle(Locale.get("list.gsetting"));
            list.removeAllCommands();
            list.addCommand(List.SELECT_COMMAND);
            menu.addCommand(CommandBuilder.getBackCmd());
            menu.addCommand(CommandBuilder.getLogoutCmd());
            menu.addCommand(CommandBuilder.getExitCmd());
            menu.setCommandListener(this);
            //#style mailTicker
            menu.setTicker(new Ticker(getflckr()));
            state = "Chat2";
            screen = menu;
        } else if (screenNum == 9) {//loads all friends to the screen
            if (!"".equals(currentPal)) {
                currentPal = "";
            }
            Hashtable offlineMsgSenders = new Hashtable();
            Hashtable contact = (Hashtable) getContactMap(false);
            Vector vc = getOflineMsgSenders();//read from inbox i.e offline msgs
            Enumeration elements = vc.elements();

            while (elements.hasMoreElements()) {//reading all phone for offline senders
                String offlineSender = elements.nextElement().toString();
                if (contact.containsKey(offlineSender)) {//if the user that sends the message is ur friend or is among ur contacts
                    offlineMsgSenders.put(offlineSender, "");
//                mylist[i++] = replaceString(replaceString(replaceString(replaceString(cont.getName()+"\n"+cont.getPersonalMsg()+"\n"+cont.getPhone(), "gd[str]", "|"),"gd[til]","~"),"gd[att]","@"),"gd[col]",":");
                }
            }
            if (state.equals("groupmembers")) {
                friendList = (FilteredList) getFriendList(msg, offlineMsgSenders,contact, FilteredList.MULTIPLE);
                if (friendState < friendList.size()) {
                    friendList.focus(friendState);
                }
                state = "addFriendstoGroup";
                friendList.addCommand(CommandBuilder.getAddMemberCmd());
                friendList.addCommand(CommandBuilder.getBackCmd());
                friendList.addCommand(CommandBuilder.getLogoutCmd());
                friendList.addCommand(CommandBuilder.getExitCmd());
            } else {
                friendList = (FilteredList) getFriendList(Locale.get("txt.friend"), offlineMsgSenders,contact, FilteredList.IMPLICIT);
                state = "Friends";
                friendList.addCommand(CommandBuilder.getBackCmd());
                friendList.addCommand(CommandBuilder.getViewProfile());
                friendList.addCommand(CommandBuilder.getCreditTransfer());
                friendList.addCommand(CommandBuilder.getClearHistoryCmd());
                friendList.addCommand(CommandBuilder.getRemoveFriendCmd());
                friendList.addCommand(CommandBuilder.getLogoutCmd());
                friendList.addCommand(CommandBuilder.getExitCmd());
            }
//            //#style mailTicker
//            friendList.setTicker(new Ticker(bdayNotice+" "+flickrMsg));
            screen = friendList;
        } else if (screenNum == 10) {
            if (chatScreen == null) {//create the chat screen if its being accessed for the first time
                //#style mainMenuScreenChat
                this.chatScreen = new FramedForm(screen.getTitle());
                //#style chatText
                this.chatTextField = new TextField(null, "", 140, TextField.PLAIN);
                UiAccess.setTextfieldHelp(chatTextField, Locale.get("input.help.chat"));
                chatScreen.append(FramedForm.FRAME_BOTTOM, chatTextField);
                chatTextField.setDefaultCommand(CommandBuilder.getSendCmd());
                chatScreen.addCommand(CommandBuilder.getBackCmd());
                chatScreen.addCommand(CommandBuilder.getSendCmd());
                chatScreen.addCommand(CommandBuilder.getLogoutCmd());
                chatScreen.addCommand(CommandBuilder.getExitCmd());

                chatScreen.setCommandListener(this);
            } else {
                chatScreen.deleteAll(FramedForm.FRAME_CENTER);
            }
            chatScreen.setTitle(screen.getTitle());
            if (!state.equals("chatmember")) {//chat with friends
                String[] currentPalUsername = TextUtil.splitAndTrim(currentPal, '\n');
                if(readOfflineMsgFrom(currentPalUsername[1])){
                    chatScreen.setTitle(screen.getTitle().substring(5));
                }
                state = "listFriends";
            } else {//chat with groups
                readOfflineMsgFrom((String) groupIds.elementAt(0));
                state = "groupChat";
            }

            displayChat();
            screen = chatScreen;
        } else if (screenNum == 11) {
        } else if (screenNum == 12) {
            if (this.invite == null) {
                //#style mainMenuScreen
                Form form = new Form(screen.getTitle());
                inviteTxt = new TextField("", "", 40, TextField.PLAIN);
                UiAccess.setTextfieldHelp(inviteTxt, Locale.get("input.help.invite"));
                //#style textFields
                form.append(inviteTxt);
                //#style notif
                StringItem tips = new StringItem("", Locale.get("txt.inviteuser"));
                //#style button
                StringItem invite = new StringItem("", Locale.get("cmd.send"));
                invite.setDefaultCommand(CommandBuilder.getSendCmd());
                form.append(invite);
                form.append(tips);

                form.addCommand(CommandBuilder.getBackCmd());
                form.addCommand(CommandBuilder.getSendCmd());
                form.addCommand(CommandBuilder.getLogoutCmd());
                form.addCommand(CommandBuilder.getExitCmd());
                form.setCommandListener(this);
                this.invite = form;
            } else {
                //#style textFields
                inviteTxt = new TextField("", "", 40, TextField.PLAIN);
                UiAccess.setTextfieldHelp(inviteTxt, Locale.get("input.help.invite"));
                invite.set(0, inviteTxt);
                //#style notif
                StringItem tips = new StringItem("", Locale.get("txt.inviteuser"));
                invite.set(2, tips);
                invite.setTitle(screen.getTitle());
            }
            //#style mailTicker
            this.invite.setTicker(new Ticker(getflckr()));
            state = "Username";
            screen = this.invite;
        } else if (screenNum == 13) {
        } else if (screenNum == 14) {
            String[] mylist = new String[]{};
            executeOperation("flickr", mylist, 0, 0, 5);
            //#style notif
            cg = new ChoiceGroup(Locale.get("txt.loadingContent"), ChoiceGroup.BUTTON, mylist, null);

            //#style mainMenuScreenTop
            flickrScreen = new FramedForm(Locale.get("txt.category"));
            //#style notif
            flickrScreen.append(FramedForm.FRAME_CENTER, cg);

            flickrScreen.setCommandListener(this);
            flickrScreen.addCommand(CommandBuilder.getBackCmd());
            flickrScreen.addCommand(CommandBuilder.getSaveCmd());
            flickrScreen.addCommand(CommandBuilder.getLogoutCmd());
            flickrScreen.addCommand(CommandBuilder.getExitCmd());

            //#style mailTicker
            flickrScreen.setTicker(new Ticker(getflckr()));
            state = "General Settings";
            screen = flickrScreen;
        } else if (screenNum == 15) {
            if (this.invite == null) {
                //#style mainMenuScreen
                Form form = new Form(screen.getTitle());
                inviteNum = new TextField("", "", 40, TextField.NUMERIC);
                UiAccess.setTextfieldHelp(inviteNum, Locale.get("input.help.invite"));
                //#style textFields
                form.append(inviteNum);
                //#style notif
                StringItem tips = new StringItem("", Locale.get("txt.invitephone"));
                //#style button
                StringItem invite = new StringItem("", Locale.get("cmd.send"));
                invite.setDefaultCommand(CommandBuilder.getSendCmd());
                form.append(invite);
                form.append(tips);

                form.addCommand(CommandBuilder.getBackCmd());
                form.addCommand(CommandBuilder.getSendCmd());
                form.addCommand(CommandBuilder.getLogoutCmd());
                form.addCommand(CommandBuilder.getExitCmd());
                form.setCommandListener(this);
                this.invite = form;
            } else {
                //#style textFields
                inviteNum = new TextField("", "", 40, TextField.NUMERIC);
                UiAccess.setTextfieldHelp(inviteNum, Locale.get("input.help.invite"));
                invite.set(0, inviteNum);
                //#style notif
                StringItem tips = new StringItem("", Locale.get("txt.invitephone"));
                invite.set(2, tips);
                invite.setTitle(screen.getTitle());
            }
            //#style mailTicker
            this.invite.setTicker(new Ticker(getflckr()));
            state = "Username";
            screen = this.invite;
        } else if (screenNum == 16) {
        } else if (screenNum == 17) {
            //#style popAlert
            transferForm = new Form(screen.getTitle());
            String[] currentPalUsername = TextUtil.splitAndTrim(screen.getTitle(), '\n');
            //#style textFields
            transferForm.append(new TextField(Locale.get("txt.beneficiary"), currentPalUsername[0], 20, TextField.UNEDITABLE));
            //#style textFields
            TextField crdt = new TextField(Locale.get("txt.ammount"), "", 20, TextField.NUMERIC);
            UiAccess.setTextfieldHelp(crdt, Locale.get("input.help.credit"));
            crdt.setDefaultCommand(CommandBuilder.getSendCmd());
            //#style textFields
            transferForm.append(crdt);
            //#style button
            StringItem tc = new StringItem("", Locale.get("txt.startTransfer"), StringItem.BUTTON);
            tc.setDefaultCommand(CommandBuilder.getSendCmd());
            //#style button
            transferForm.append(tc);

            transferForm.addCommand(CommandBuilder.getBackCmd());
            transferForm.addCommand(CommandBuilder.getSendCmd());
            transferForm.addCommand(CommandBuilder.getLogoutCmd());
            transferForm.addCommand(CommandBuilder.getExitCmd());
            transferForm.setCommandListener(this);

            state = "transferCredit";
            screen = this.transferForm;
        } else if (screenNum == 18) {
            //#style mainMenuScreen
            List menu = new List("", List.IMPLICIT);
            Object obj = readRs("groups");
            Hashtable ht;
            if (obj == null) {
                ht = new Hashtable();
            } else {
                ht = (Hashtable) obj;
            }
            if (ht.isEmpty()) {
                //#style mainMenuItemAnimated
                menu.append(Locale.get("txt.nogrpMsg"), null);
            } else {
                Hashtable offlineMsgSenders = new Hashtable();
                groupIds = new Vector();
                Enumeration keys = ht.keys();
                
                Vector vc = getOflineMsgSenders();//read from inbox i.e offline msgs
                Enumeration elements = vc.elements();

                while (elements.hasMoreElements()) {//reading all phone for offline senders
                    String offlineSender = elements.nextElement().toString();
                    if (ht.containsKey(offlineSender)) {//if the user that sends the message is ur friend or is among ur contacts
                        offlineMsgSenders.put(offlineSender, "");
//                mylist[i++] = replaceString(replaceString(replaceString(replaceString(cont.getName()+"\n"+cont.getPersonalMsg()+"\n"+cont.getPhone(), "gd[str]", "|"),"gd[til]","~"),"gd[att]","@"),"gd[col]",":");
                    }
                }

                while (keys.hasMoreElements()) {
                    String key = (String) keys.nextElement();
                    groupIds.addElement(key);
                    
                    String groupName = ((Groups) ht.get(key)).getName();
                    try {
                        if (offlineMsgSenders.containsKey(key)) {
                            //#style mainMenuItemAnimated
                            menu.append("nwMSG "+replaceString(replaceString(replaceString(replaceString(groupName, "gd[str]", "|"), "gd[til]", "~"), "gd[att]", "@"), "gd[col]", ":"), Image.createImage("/group.png"));
                        }else{
                            //#style mainMenuItemAnimated
                            menu.append(replaceString(replaceString(replaceString(replaceString(groupName, "gd[str]", "|"), "gd[til]", "~"), "gd[att]", "@"), "gd[col]", ":"), Image.createImage("/group.png"));
                        }
                        
                    } catch (IOException ex) {
                        if (offlineMsgSenders.containsKey(key)) {
                            //#style mainMenuItemAnimated
                            menu.append("nwMSG "+replaceString(replaceString(replaceString(replaceString(groupName, "gd[str]", "|"), "gd[til]", "~"), "gd[att]", "@"), "gd[col]", ":"), null);
                        }else{
                            //#style mainMenuItemAnimated
                            menu.append(replaceString(replaceString(replaceString(replaceString(groupName, "gd[str]", "|"), "gd[til]", "~"), "gd[att]", "@"), "gd[col]", ":"), null);
                        }
                    }
                }
            }

            menu.setTitle(Locale.get("list.groupchat"));
            list.removeAllCommands();
            list.addCommand(List.SELECT_COMMAND);
            list.addCommand(CommandBuilder.getCreateCmd());
            menu.addCommand(CommandBuilder.getBackCmd());
            menu.addCommand(CommandBuilder.getLogoutCmd());
            menu.addCommand(CommandBuilder.getExitCmd());
            menu.setCommandListener(this);
            //#style mailTicker
            menu.setTicker(new Ticker(getflckr()));
            list = menu;
            state = "group";
            screen = list;
        } else if (screenNum == 19) {
            List menu;
            String[] item = new String[]{Locale.get("list.conversation"), Locale.get("list.members")};
            menu = createMenuList(item, List.IMPLICIT);
            
            menu.setTitle(screen.getTitle());
            list.removeAllCommands();
            list.addCommand(List.SELECT_COMMAND);
            menu.addCommand(CommandBuilder.getBackCmd());
            menu.addCommand(CommandBuilder.getLeaveGroupCmd());
            menu.addCommand(CommandBuilder.getLogoutCmd());
            menu.addCommand(CommandBuilder.getExitCmd());
            menu.setCommandListener(this);

            //#style mailTicker
            menu.setTicker(new Ticker(getflckr()));
            list = menu;
            screen = list;
            state = "chatmember";
        } else if (screenNum == 20) {
            Object obj = readRs("groups");
            Hashtable ht;
            if (obj == null) {
                ht = new Hashtable();
            } else {
                ht = (Hashtable) obj;
            }
            //#style mainMenuScreen
            List menu = new List("", List.IMPLICIT);
            int k = 0;
            Groups group = (Groups) ht.get(groupIds.elementAt(0).toString());
            Hashtable mht = group.getMembers();
            Enumeration memberInfo = mht.keys();
            while (memberInfo.hasMoreElements()) {
                k++;
                String member = memberInfo.nextElement().toString();
                Contacts cont = (Contacts) mht.get(member);
                if (cont.getGender().equals("M")) {
                    try {
                        //#style mainMenuItemAnimated
                        menu.append(replaceString(replaceString(replaceString(replaceString(cont.getName() + "\n" + cont.getUsername(), "gd[str]", "|"), "gd[til]", "~"), "gd[att]", "@"), "gd[col]", ":"), Image.createImage("/dummyimgmal.png"));
                    } catch (IOException ex) {
                        //#style mainMenuItemAnimated
                        menu.append(replaceString(replaceString(replaceString(replaceString(cont.getName() + "\n" + cont.getUsername(), "gd[str]", "|"), "gd[til]", "~"), "gd[att]", "@"), "gd[col]", ":"), null);
                    }
                } else {
                    try {
                        //#style mainMenuItemAnimated
                        menu.append(replaceString(replaceString(replaceString(replaceString(cont.getName() + "\n" + cont.getUsername(), "gd[str]", "|"), "gd[til]", "~"), "gd[att]", "@"), "gd[col]", ":"), Image.createImage("/dummyimgfem.png"));
                    } catch (IOException ex) {
                        //#style mainMenuItemAnimated
                        menu.append(replaceString(replaceString(replaceString(replaceString(cont.getName() + "\n" + cont.getUsername(), "gd[str]", "|"), "gd[til]", "~"), "gd[att]", "@"), "gd[col]", ":"), null);
                    }
                }

            }

            if (k == 0) {
                //#style mainMenuItemAnimated
                menu.append(Locale.get("txt.nogrpmem"), null);
            }
            menu.setTitle(screen.getTitle());
            list.removeAllCommands();
            list.addCommand(List.SELECT_COMMAND);
            menu.addCommand(CommandBuilder.getAddMemberCmd());
            menu.addCommand(CommandBuilder.getBackCmd());
            if (group.isAdmin()) {
                menu.addCommand(CommandBuilder.getBarnMemberCmd());
            }
            menu.addCommand(CommandBuilder.getLogoutCmd());
            menu.addCommand(CommandBuilder.getExitCmd());
            menu.setCommandListener(this);
            //#style mailTicker
            menu.setTicker(new Ticker(getflckr()));
            list = menu;
            state = "groupmembers";
            screen = list;
        } else if (screenNum == 21) {
            List rootFiles = getRootFiles();
            state = "files";
            list.removeAllCommands();
            list = rootFiles;
            screen = list;
        } else if (screenNum == 22) {
            String[] mylist = new String[]{Locale.get("list.gistmin"), Locale.get("list.gistmed"), Locale.get("list.gistmax")};
            List menu = createMenuList(mylist, List.IMPLICIT);
            menu.setTitle(screen.getTitle());
            list.removeAllCommands();
            list.addCommand(List.SELECT_COMMAND);
            menu.addCommand(CommandBuilder.getBackCmd());
            menu.addCommand(CommandBuilder.getLogoutCmd());
            menu.addCommand(CommandBuilder.getExitCmd());
            list.setCommandListener(this);
            //#style mailTicker
            menu.setTicker(new Ticker(getflckr()));
            list = menu;
            state = "gistcredit";
            screen = list;
        }
        return screen;
    }

    protected void pauseApp() {
    }

    public void commandAction(Command cmd, Displayable disp) {
        if (cmd.getLabel().equals(Locale.get("cmd.logout"))) {
            logout();
        } else if (cmd.getLabel().equals(Locale.get("cmd.exit"))) {
            notifyDestroyed();
        } else if (cmd.getLabel().equals(Locale.get("cmd.back"))) {
            if (state.equalsIgnoreCase("Friends") || state.equals("Invite Friends") || state.equals("Chat Room") || state.equals("Setting") || state.equals("Request") || state.equals("group")) {
                if (state.equals("group")) {
                }
                getDisplay().setCurrent(getScreen(new List(null, List.IMPLICIT), 2, ""));
            } else if (state.equalsIgnoreCase("Alert Setting") || state.equalsIgnoreCase("profile") || state.equalsIgnoreCase("publicsettings") || state.equalsIgnoreCase("files") || state.equals("dpEnlarge")) {

                if (state.equals("profile")) {
                } else if (state.equals("publicsettings")) {
                } else if (state.equals("dpEnlarge")) {
                }


                currentRoot = "";
                getDisplay().setCurrent(getScreen(new List(null, List.IMPLICIT), 4, ""));
            } else if (state.equals("Chat") || state.equals("Chat2")) {
                getDisplay().setCurrent(getScreen(new List(null, List.IMPLICIT), 1, ""));
            } else if (state.equalsIgnoreCase("listFriends") || state.equals("transferCredit")) {
                getDisplay().setCurrent(getScreen(new List(null, List.IMPLICIT), 9, ""));
            } else if (state.equalsIgnoreCase("Username") || state.equals("Phonebook") || state.equals("Mobile Number")) {
                getDisplay().setCurrent(getScreen(new List(null, List.IMPLICIT), 3, ""));
            } else if (state.equalsIgnoreCase("General Settings") || state.equalsIgnoreCase("changePassword") || state.equals("gistcredit")) {
                if (state.equals("General Settings")) {
                }
                getDisplay().setCurrent(getScreen(new List(null, List.IMPLICIT), 8, ""));
            } else if (state.equalsIgnoreCase("create group") || state.equalsIgnoreCase("chatmember")) {
                if (state.equals("groupmembers")) {
                }
                getDisplay().setCurrent(getScreen(new List(null, List.IMPLICIT), 18, ""));
            } else if (state.equalsIgnoreCase("groupChat") || state.equalsIgnoreCase("groupmembers") || state.equals("addFriendstoGroup")) {
                getDisplay().setCurrent(getScreen(new List(list.getTitle().trim(), List.IMPLICIT), 19, ""));
            }
        } else if (cmd.getLabel().equals(Locale.get("polish.command.cancel"))) {
        } else if (disp == this.list) {
            if (cmd == List.SELECT_COMMAND) {
                int x = list.getSelectedIndex();
                String selected = list.getString(x);
                if (selected.equals(Locale.get("list.chat"))) {
                    getDisplay().setCurrent(getScreen(new List("", List.IMPLICIT), 2, ""));
                } else if (selected.equals(Locale.get("list.gsetting"))) {
                    getDisplay().setCurrent(getScreen(new List("", List.POPUP), 8, ""));
                } else if (checkString(selected, Locale.get("txt.friend"))) {
                    getDisplay().setCurrent(getScreen(new FilteredList(Locale.get("txt.friend"), FilteredList.IMPLICIT), 9, ""));
                } else if (selected.equalsIgnoreCase(Locale.get("list.groupchat"))) {
                    getDisplay().setCurrent(getScreen(new List("", List.IMPLICIT), 18, ""));
                } else if (selected.equalsIgnoreCase(Locale.get("list.username"))) {
                    getDisplay().setCurrent(getScreen(new Form(selected), 12, ""));
                } else if (selected.equalsIgnoreCase(Locale.get("list.mobileNumber"))) {
                    getDisplay().setCurrent(getScreen(new Form(selected), 15, ""));
                } else if (selected.equalsIgnoreCase(Locale.get("list.changePass"))) {
                    formLogin = null;
                    //#style popAlert
                    formLogin = new Form(Locale.get("list.changePass"));

                    //#style textFields
                    formLogin.append(new TextField(Locale.get("txt.username"), SendToServer.getUsername(), 25, TextField.UNEDITABLE));
                    TextField oldPass = new TextField(Locale.get("txt.oldPass"), "", 25, TextField.PASSWORD);
                    TextField newPass = new TextField(Locale.get("txt.newPass"), "", 25, TextField.PASSWORD);
                    TextField cPass = new TextField(Locale.get("txt.cnfrmNewPass"), "", 25, TextField.PASSWORD);
                    UiAccess.setTextfieldHelp(oldPass, Locale.get("txt.oldPassHint"));
                    UiAccess.setTextfieldHelp(newPass, Locale.get("txt.newPassHint"));
                    UiAccess.setTextfieldHelp(cPass, Locale.get("txt.confrmPassHint"));
                    //#style textFields
                    formLogin.append(oldPass);
                    //#style textFields
                    formLogin.append(newPass);
                    //#style textFields
                    formLogin.append(cPass);

                    //#style button 
                    StringItem chnP = new StringItem("", Locale.get("txt.change"), StringItem.BUTTON);
                    chnP.setDefaultCommand(CommandBuilder.getSaveCmd());
                    formLogin.append(chnP);
                    formLogin.addCommand(CommandBuilder.getSaveCmd());
                    formLogin.addCommand(CommandBuilder.getBackCmd());
                    state = "changePassword";
                    formLogin.setCommandListener(this);
                    getDisplay().setCurrent(formLogin);
                } else if (selected.equalsIgnoreCase(Locale.get("list.gistcredit"))) {
                    getDisplay().setCurrent(getScreen(new List(Locale.get("list.gistcredit"), List.IMPLICIT), 22, ""));
                } else if (selected.equalsIgnoreCase(Locale.get("list.gistmin"))) {
                    String msg = "gistmin " + SendToServer.getUsername();
                    SendSMS sms = new SendSMS(null);
                    sms.send(msg, "32810");
                    //displayAlert(Locale.get("alert.successT"), Locale.get("alert.buyR"), null, AlertType.ALARM, x);
                } else if (selected.equalsIgnoreCase(Locale.get("list.gistmed"))) {
                    String msg = "gistmed " + SendToServer.getUsername();
                    SendSMS sms = new SendSMS(null);
                    sms.send(msg, "33810");
                } else if (selected.equalsIgnoreCase(Locale.get("list.gistmax"))) {
                    String msg = "gistmax " + SendToServer.getUsername();
                    SendSMS sms = new SendSMS(null);
                    sms.send(msg, "35811");
                } else if (selected.equalsIgnoreCase(Locale.get("list.flickrSetting"))) {
                    getDisplay().setCurrent(getScreen(new List("", List.IMPLICIT), 14, ""));
                } else if (selected.equalsIgnoreCase(Locale.get("list.invitefrnd"))) {
                    getDisplay().setCurrent(getScreen(new List("", List.IMPLICIT), 3, ""));
                } else if (selected.equalsIgnoreCase(Locale.get("list.setting"))) {
                    getDisplay().setCurrent(getScreen(new List("", List.IMPLICIT), 4, ""));
                } else if (selected.equalsIgnoreCase(Locale.get("list.profile"))) {
                    getDisplay().setCurrent(getScreen(new Form(""), 6, ""));
                    executeOperation("getUserprofile", new String[]{SendToServer.getUsername()}, 0, 0, 10);
                } else if (selected.equalsIgnoreCase(Locale.get("list.ppsetting"))) {
                    String[] mylist = new String[]{};

                    //#style notif
                    cg = new ChoiceGroup(Locale.get("txt.loadingContent"), ChoiceGroup.BUTTON, mylist, null);

                    //#style mainMenuScreenTop
                    flickrScreen = new FramedForm(Locale.get("txt.privacysetting"));
                    //#style notif
                    flickrScreen.append(FramedForm.FRAME_CENTER, cg);

                    flickrScreen.setCommandListener(this);
                    flickrScreen.addCommand(CommandBuilder.getBackCmd());
                    flickrScreen.addCommand(CommandBuilder.getSaveCmd());
                    flickrScreen.addCommand(CommandBuilder.getLogoutCmd());
                    flickrScreen.addCommand(CommandBuilder.getExitCmd());

                    state = "publicsettings";
                    executeOperation("publicsettings", null, 0, 0, 5);

                    getDisplay().setCurrent(flickrScreen);

                } else if (selected.equalsIgnoreCase(Locale.get("txt.friendRq"))) {
                    getDisplay().setCurrent(getScreen(new List(null, List.IMPLICIT), 13, ""));
                } else if (state.equals("Request")) {
                    executeOperation("acceptReq", new String[]{selected, x + ""}, 0, 0, 0);
                } else if (state.equals("group") && !selected.equals(Locale.get("txt.nogrpMsg"))) {
                    String id = (String) groupIds.elementAt(x);
                    groupIds.removeAllElements();
                    groupIds.addElement(id);
                    boolean val = false;
                    if(readOfflineMsgFrom(id)){
                        selected = selected.substring(5);
                    }
                    groupIds.trimToSize();
                    getDisplay().setCurrent(getScreen(new List(selected, List.IMPLICIT), 19, ""));
                } else if (state.equals("chatmember")) {//group chat
                    if (selected.equals(Locale.get("list.conversation"))) {//selected conversation
                        currentPal = list.getTitle();
                        getDisplay().setCurrent(getScreen(new FramedForm(currentPal), 10, ""));
                    } else if (selected.equals(Locale.get("list.members"))) {//selected members
                        getDisplay().setCurrent(getScreen(new Form(disp.getTitle()), 20, ""));
                    }
                } else if (state.equals("groupmembers") && !selected.equals(Locale.get("txt.loadingroupmem")) && !selected.startsWith(Locale.get("txt.nogrpmem"))) {
                    getDisplay().setCurrent(getScreen(new Form(null), 6, list.getTitle()));
                    String[] user = TextUtil.splitAndTrim(selected, '\n');
                    executeOperation("getUserprofile", new String[]{user[1]}, 0, 0, 10);
                } else if (state.equals("files")) {
                    if (cmd == List.SELECT_COMMAND) {
                        String fil = list.getString(list.getSelectedIndex());
                        if (fil.equals("..")) {
                            String myroot = currentRoot;
                            Character c = new Character('/');
                            myroot = myroot.substring(0, myroot.lastIndexOf(c.charValue()));
                            myroot = myroot.substring(0, myroot.lastIndexOf(c.charValue()) + 1);
                            if (myroot.equals("")) {
                                List rootFiles = getRootFiles();
                                currentRoot = "";
                                list.removeAllCommands();
                                list = rootFiles;
                                getDisplay().setCurrent(list);
                            } else {
                                try {
                                    FileConnection fileConn = (FileConnection) Connector.open("file:///" + myroot);
                                    currentRoot = myroot;
                                    listFolderContents(fileConn);
                                } catch (IOException ex) {
                                }
                            }
                        } else {
                            try {
                                FileConnection fileConn = (FileConnection) Connector.open("file:///" + currentRoot + fil);
                                if (fileConn.isDirectory()) {
                                    currentRoot = currentRoot + fil;
                                    listFolderContents(fileConn);
                                } else {
                                    executeOperation("upload", new String[]{fil}, 0, 0, 0);
                                }
                            } catch (IOException ex) {
                            }
                        }
                    }
                }
            } else if (cmd.getLabel().equals(Locale.get("cmd.accept"))) {
                int x = list.getSelectedIndex();
                String selected = list.getString(x);
                executeOperation("acceptReq", new String[]{selected, x + ""}, 0, 0, 0);
            } else if (cmd.getLabel().equals(Locale.get("cmd.decline"))) {
                int x = list.getSelectedIndex();
                String selected = list.getString(x);
                executeOperation("declineReq", new String[]{selected, x + ""}, 0, 0, 0);
            } else if (cmd.getLabel().equals(Locale.get("cmd.declineBlock"))) {
                int x = list.getSelectedIndex();
                String selected = list.getString(x);
                executeOperation("blockReq", new String[]{selected, x + ""}, 0, 0, 0);
            } else if (cmd.getLabel().equals(Locale.get("cmd.createGrp"))) {
                formLogin = null;
                //#style popAlert
                formLogin = new Form(Locale.get("cmd.createGrp"));
                TextField grpName = new TextField(Locale.get("txt.grpName"), "", 25, TextField.ANY);
                UiAccess.setTextfieldHelp(grpName, Locale.get("txt.grpName"));
                //#style textFields
                formLogin.append(grpName);
                //#style button 
                StringItem create = new StringItem("", Locale.get("cmd.createGrp"), StringItem.BUTTON);
                create.setDefaultCommand(CommandBuilder.getCreateCmd());
                formLogin.append(create);
                formLogin.addCommand(CommandBuilder.getCreateCmd());
                formLogin.addCommand(CommandBuilder.getBackCmd());
                state = "create group";
                formLogin.setCommandListener(this);
                getDisplay().setCurrent(formLogin);
            } else if (cmd.getLabel().equals(Locale.get("cmd.addFriend"))) {
                getDisplay().setCurrent(getScreen(new FilteredList(null, FilteredList.MULTIPLE), 9, list.getTitle()));
            } else if (cmd.getLabel().equals(Locale.get("cmd.removeMem"))) {
                String selected = list.getString(list.getSelectedIndex());
                executeOperation("removemember", new String[]{selected, groupIds.elementAt(0).toString()}, 0, 0, 0);
            } else if (cmd.getLabel().equals(Locale.get("cmd.leaveGroup"))) {
                executeOperation("leavegroup", new String[]{groupIds.elementAt(0).toString()}, 0, 0, 0);
            }

        } else if (disp == this.friendList) {
            if (cmd == FilteredList.SELECT_COMMAND) {
                friendState = friendList.getSelectedIndex();
                currentPal = friendList.getString(friendState);
                String[] pal = TextUtil.splitAndTrim(currentPal, '\n');
                getDisplay().setCurrent(getScreen(new Form(pal[0]), 10, ""));
            } else if (cmd.getLabel().equals(Locale.get("cmd.viewProfile"))) {
                String usr[] = TextUtil.splitAndTrim(friendList.getString(friendList.getSelectedIndex()), '\n');
                getDisplay().setCurrent(getScreen(new Form(null), 6, ""));
                Hashtable ht = (Hashtable)getContactMap(false);
                if(ht.containsKey(usr[1])){
                    Contacts cont = (Contacts)ht.get(usr[1]);
                    executeOperation("getUserprofile", new String[]{cont.getUsername()}, 0, 0, 10);
                }
                
            } else if (cmd.getLabel().equals(Locale.get("cmd.transferCrdt"))) {
                String user = friendList.getString(friendList.getSelectedIndex());
                user = user.substring(0, user.indexOf("\n"));
                getDisplay().setCurrent(getScreen(new Form(user), 17, ""));
            } else if (cmd.getLabel().equals(Locale.get("cmd.addFriend"))) {
                for (int i = 0; i < friendList.size(); i++) {
                    if (friendList.isSelected(i)) {
                        String label = friendList.getString(i);
                        String[] labelarr = TextUtil.splitAndTrim(label, '\n');
                        executeOperation("addtogroup", new String[]{labelarr[0].trim(), groupIds.elementAt(0).toString(), friendList.getTitle()}, 0, 0, 0);
                    }
                }
            } else if (cmd.getLabel().equals(Locale.get("cmd.defriend"))) {
                executeOperation("defriend", new String[]{friendList.getString(friendList.getSelectedIndex())}, 0, 0, 0);
            } else if (cmd.getLabel().equals(Locale.get("cmd.clearHist"))) {
                String [] userIn = TextUtil.splitAndTrim(friendList.getString(friendList.getSelectedIndex()), '\n');
                try {
                    System.out.println("removing "+SendToServer.getUsername() + userIn[1]);
                    removeRs("history",SendToServer.getUsername() + userIn[1]);
                    displayAlert(Locale.get("alert.successT"), Locale.get("alert.clrHist"), Image.createImage("/v.png"), AlertType.CONFIRMATION, 5000);
                } catch (IOException ex) {
                }
            }

        } else if (disp == this.profile) {
            if (cmd.getLabel().equals(Locale.get("cmd.save"))) {
                profile.setActiveFrame(FramedForm.FRAME_CENTER);

                String status = ((TextField) profile.get(3)).getString();
                String user = ((StringItem) profile.get(0)).getText();

                executeOperation("saveStatus", new String[]{user, status}, 0, 0, 0);

            } else if (cmd.getLabel().equals(Locale.get("cmd.save"))) {
                profile.setActiveFrame(FramedForm.FRAME_CENTER);
                profile.focus(0, true);
                executeOperation("invite", new String[]{profile.getTitle().trim()}, 0, 0, 0);
            } else if (cmd.getLabel().equals(Locale.get("cmd.chngImg"))) {
                getDisplay().setCurrent(getScreen(new List("", List.IMPLICIT), 21, ""));
            } else if (cmd.getLabel().equals(Locale.get("cmd.makeAdmin"))) {
                executeOperation("madmin", new String[]{profile.getTitle().substring(profile.getTitle().indexOf(":") + 1), groupIds.elementAt(0).toString()}, 0, 0, 0);
            } else if (cmd.getLabel().equals(Locale.get("cmd.enlgImg"))) {
                profile.setActiveFrame(FramedForm.FRAME_CENTER);
                String user = ((StringItem) profile.get(0)).getText();

                formLogin.deleteAll();
                formLogin.removeAllCommands();
                //#style popAlert
                formLogin = new Form(user);
                StringItem loading = new StringItem("", Locale.get("txt.loadingContent"));
                //#style notif
                formLogin.append(loading);
                ImageItem imgItm = new ImageItem("", null, ImageItem.LAYOUT_CENTER, "");
                executeOperation("getImage", new String[]{user, "px"}, 0, 0, 1);
                formLogin.append(imgItm);
//                formLogin.addCommand(CommandBuilder.getSaveCmd());
                formLogin.addCommand(CommandBuilder.getBackCmd());
                formLogin.setCommandListener(this);
                state = "dpEnlarge";
                getDisplay().setCurrent(formLogin);

            }
        } else if (disp == this.formLogin) {
            if (cmd.getLabel().equals(Locale.get("cmd.login"))) {
                executeOperation("login", new String[]{usernameField.getString().toLowerCase(), passwordField.getString()}, 1, 0, 3);
            } else if (cmd.getLabel().equals(Locale.get("cmd.logout"))) {
                logout();
            } else if (cmd.getLabel().equals(Locale.get("cmd.createGrp"))) {
                String grp = ((TextField) formLogin.get(0)).getString();
                if (!grp.trim().equals("")) {
                    executeOperation("create group", new String[]{grp}, 0, 0, 3);
                }
            } else if (cmd.getLabel().equals(Locale.get("cmd.save")) && state.equals("files")) {
                ImageItem imgItm = (ImageItem) formLogin.get(1);
                Image img = imgItm.getImage();
                Vector vec = FetchFiles.getRoot();
                vec.trimToSize();
                if (vec.size() > 1) {
                    String mem = vec.elementAt(1).toString();
                    try {
                        FileConnection fc = (FileConnection) Connector.open("file:///" + mem);
                        if (fc.canWrite() && fc.availableSize() > 1000000) {
                            displayAlert("Unimplemented Method", "Error", Image.createImage("/x.png"), AlertType.ERROR, 10000);
                        }
                    } catch (IOException ex) {
                    }
                }
            } else if (cmd.getLabel().equals(Locale.get("cmd.save")) && state.equals("changePassword")) {
                String old = ((TextField) formLogin.get(1)).getString();
                String newP = ((TextField) formLogin.get(2)).getString();
                String cNewP = ((TextField) formLogin.get(3)).getString();
                if (SendToServer.getPassword().equals(old) && newP.equals(cNewP)) {
                    SendToServer.setPassword(newP);
                    //#style notif
                    formLogin.append(new StringItem("", Locale.get("txt.updating")));
                    executeOperation("changePassword", new String[]{newP}, 0, 0, 5);
                } else {
                    try {
                        displayAlert(Locale.get("alert.error"), Locale.get("alert.passmiss"), Image.createImage("/x.png"), AlertType.ERROR, 10000);
                    } catch (IOException ex) {
                    }
                }
            } else if (cmd.getLabel().equals(Locale.get("polish.command.cancel"))) {
            }
        } else if (disp == chatScreen) {
            if (cmd.getLabel().equals(Locale.get("cmd.send"))) {
                String msg = chatTextField.getString();
                chatTextField.setString("");
                if (!(msg.trim().equals(""))) {
                    if (!state.equals("groupChat")) {
                        String[] currentPalUsername = TextUtil.splitAndTrim(currentPal, '\n');
                        if (currentPalUsername[1] != null) {
                            executeOperation("sendchat", new String[]{currentPalUsername[1], msg, "-1"}, 0, 0, 5);
                        }
                    } else {
                        executeOperation("sendchat", new String[]{"groupChat", msg, "-1"}, 0, 0, 5);
                    }

                }

            }
        } else if (disp == this.invite) {
            if (cmd.getLabel().equals(Locale.get("cmd.send"))) {
                if (invite.getTitle().equals(Locale.get("list.username"))) {
                    String palUser = inviteTxt.getString();
                    if (!palUser.equals("")) {
                        executeOperation("invite", new String[]{palUser}, 0, 0, 0);
                    }
                } else if (invite.getTitle().equals(Locale.get("list.mobileNumber"))) {
                    String palUser = inviteNum.getString();
                    if (!palUser.equals("")) {
                        executeOperation("inviteP", new String[]{palUser}, 0, 0, 0);
                    }
                }
            }
        } else if (disp == flickrScreen) {
            if (state.equals("publicsettings")) {
                if (cmd.getLabel().equals(Locale.get("cmd.save"))) {
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < cg.size(); i++) {
                        if (cg.getItem(i).isSelected()) {
                            sb.append(cg.getString(i)).append(",");
                        }
                    }
                    String choice = sb.toString();
                    if (choice.length() > 0) {
                        choice = choice.substring(0, choice.lastIndexOf(','));
                        executeOperation("sPrivacySettings", new String[]{choice}, 0, 0, 0);
                    } else {
                        executeOperation("sPrivacySettings", new String[]{""}, 0, 0, 0);
                    }
                    cg.setLabel(Locale.get("txt.ppsettingLd"));
                }
            } else if (state.equals(Locale.get("list.gsetting"))) {
                if (cmd.getLabel().equals(Locale.get("cmd.save"))) {
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < cg.size(); i++) {
                        if (cg.getItem(i).isSelected()) {
                            sb.append(cg.getString(i)).append(",");
                        }
                    }
                    String choice = sb.toString();
                    if (choice.length() > 0) {
                        choice = choice.substring(0, choice.lastIndexOf(','));
                        executeOperation("sflickr", new String[]{choice}, 0, 0, 0);
                    } else {
                        executeOperation("sflickr", new String[]{""}, 0, 0, 0);
                    }
                    cg.setLabel(Locale.get("txt.ppsettingLd"));
                }
            }
        } else if (disp == transferForm) {
            if (cmd.getLabel().equals(Locale.get("cmd.send"))) {
                String beneficiary = ((TextField) transferForm.get(0)).getString();
                String credit = ((TextField) transferForm.get(1)).getString();
                if (!credit.equals("")) {
                    executeOperation("tranferCredit", new String[]{credit, beneficiary}, 0, 0, 3);
                }
            }
        }
    }

    private List createMenuList(String[] menuList, int listType) {
        if (this.list == null) {
            //#style mainMenuScreen
            this.list = new List("", listType);
        }
        this.list.deleteAll();
        for (int i = 0; i < menuList.length; i++) {
            //#style mainMenuItemAnimated
            this.list.append(menuList[i], null);
        }
        this.list.setCommandListener(this);

        return this.list;
    }

    private boolean checkString(String str, String wt) {
        if (str.length() < wt.length()) {
            return false;
        } else {
            if (str.substring(0, 7).equalsIgnoreCase(wt)) {
                return true;
            } else {
                return false;
            }
        }
    }

    private void listFolderContents(FileConnection folder) {
        list.deleteAll();
        try {
            Enumeration folderContents = folder.list();
            //#style mainMenuItemAnimated
            list.append("..", null);
            while (folderContents.hasMoreElements()) {
                String element = folderContents.nextElement().toString();
                try {
                    FileConnection fc = (FileConnection) Connector.open("file:///" + currentRoot + element);
                    if (fc.isDirectory()) {
                        //#style mainMenuItemAnimated
                        list.append(element, Image.createImage("/Folder.png"));
                    } else {
                        //#style mainMenuItemAnimated
                        list.append(element, Image.createImage("/Pic.png"));
                    }
                } catch (IOException ex) {
                }
            }
        } catch (IOException e) {
        }
        state = "files";
        getDisplay().setCurrent(list);
    }

    private void sendMsg(String[] msg, int testCount, int attempt) {
        String responsee;
        String recvr ;
        String cpal = currentPal;
        String cState = state;
        int pos = Integer.parseInt(msg[2]);

        if (msg[0].equals("groupChat")) {
            //String currentGroup[] = TextUtil.splitAndTrim(currentPal, ':');
            if (msg.length > 3) {
                recvr = msg[3];
            } else {
                recvr = groupIds.elementAt(0).toString();
            }
        } else {
            recvr = msg[0];
        }
        //String[] reply = TextUtil.splitAndTrim(responsee, '~');
        MyCalendar cal = new MyCalendar();
        String[] timedate = cal.getDeviceTime();//device tim    
        int size = -1;
        if (testCount == 0) {
            Object obj = readRs("history");
            Hashtable hist ;
            if (obj == null) {
                hist = new Hashtable();
            } else {
                hist = (Hashtable) obj;
            }
            
            Object o;
            String userDB;
            if(msg[0].equals("groupChat")){
                userDB = SendToServer.getUsername() +"_G_"+ recvr;
                
            }else{
                userDB = SendToServer.getUsername() + recvr;
            }
            o = hist.get(userDB);
            Hashtable userhist;
            if(o==null){
                userhist = new Hashtable();
            }else{
                userhist = (Hashtable)o;
            }
            Conversation conversation = new Conversation(userhist.size()+1 + "", SendToServer.getPhone(), timedate[0] + "@" + timedate[1], "[SNDN]", msg[1]);
            userhist.put(new Integer(userhist.size()+1), conversation);
            hist.put(userDB,userhist);
            saveToRms(hist, "history");

            MessageItem si;
            if (!msg[0].equals("groupChat")) {//send to contact
                //#style message
                si = new MessageItem(cal.format(conversation.getDateTime()) + " [SNDN] ", msg[1]);//format time to standard and display with msg - [SNDN] rep sending icon
                chatScreen.append(FramedForm.FRAME_CENTER, si);//display content on chatscreen
                size = chatScreen.size(FramedForm.FRAME_CENTER) - 1;//get position of current item
                chatTrack.put(SendToServer.getUsername() + recvr + size, conversation);//keeps track of item and there 

            } else {//senf to group
                //#style Gmessage
                si = new MessageItem(Locale.get("txt.me") + " | " + cal.format(conversation.getDateTime()) + " [SNDN] ", msg[1]);//format time to standard and display with msg - [SNDN] rep sending icon
                chatScreen.append(FramedForm.FRAME_CENTER, si);//display content on chatscreen
                size = chatScreen.size(FramedForm.FRAME_CENTER) - 1;//get position of current item
                groupTrack.put(SendToServer.getUsername() + recvr + size, conversation);//keeps track of item and there 
            }
            if (chatScreen.size(FramedForm.FRAME_CENTER) != 0) {
                chatScreen.setActiveFrame(FramedForm.FRAME_CENTER);
                chatScreen.focus(chatScreen.size(FramedForm.FRAME_CENTER) - 1);
                chatScreen.setActiveFrame(FramedForm.FRAME_BOTTOM);
            }
        }
        if (msg[0].equals("groupChat")) {
            responsee = SendToServer.sendGroupChat(recvr, msg[1]);
        } else {
            responsee = SendToServer.sendChat(recvr, msg[1]);
        }

        if (responsee.equals("-2") || responsee.trim().equals("-1") || responsee.trim().equals("")) {
            testCount++;
            if (testCount < attempt) {
                if (testCount < 2) {
                    executeOperation("sendchat", new String[]{msg[0], msg[1], String.valueOf(size), recvr, cpal}, testCount * 1000, testCount, attempt);
                } else {
                    executeOperation("sendchat", msg, testCount * 1000, testCount, attempt);
                }
            } else {
                MessageItem si;
                int position;
                String pal;
                    if(pos<0){
                        position = size;
                        pal = cpal;
                    }else{
                        position = pos;
                        pal = msg[4];
                    }
                    Conversation conv ;
                    String userDB;
                    if(msg[0].equals("groupChat")){
                        conv= (Conversation) groupTrack.get(SendToServer.getUsername() + recvr + position);
                        conv.setStatus("[SNDERR]");
                        userDB = SendToServer.getUsername() +"_G_"+ recvr;
                        if(chatScreen!=null)
                        if (TextUtil.splitAndTrim(chatScreen.getTitle(), '\n')[0].equals(pal)) {
                            //#style Gmessage
                            si = new MessageItem(Locale.get("txt.me") + " | " + cal.format(conv.getDateTime()) + " " + conv.getStatus(), conv.getMessage());
                            chatScreen.set(FramedForm.FRAME_CENTER, position, si);//format time to standard and display with msg - [SNDN] rep sending icon
                        }
                    }else{
                        conv = (Conversation) chatTrack.get(SendToServer.getUsername() + recvr + position);
                        conv.setStatus("[SNDERR]");
                        userDB = SendToServer.getUsername() + recvr;
                        if(chatScreen!=null)
                        if (TextUtil.splitAndTrim(pal, '\n')[1].equals(recvr)) {
                            //#style message
                            si = new MessageItem(cal.format(conv.getDateTime()) + " " + conv.getStatus(), conv.getMessage());
                            chatScreen.set(FramedForm.FRAME_CENTER, position, si);//format time to standard and display with msg - [SNDN] rep sending icon
                        }
                    }
                    
                    Object obj = readRs("history");
                    Hashtable hist;
                    if (obj == null) {
                        hist = new Hashtable();
                    } else {
                        hist = (Hashtable) obj;
                    }
                    Object o = hist.get(userDB);
                    Hashtable userhist;
                    if (o == null) {
                        userhist = new Hashtable();
                    } else {
                        userhist = (Hashtable) o;
                    }

                    userhist.put(new Integer(userhist.size()), conv);
                    hist.put(userDB, userhist);
                    saveToRms(hist, "history");
            }
        } else {//msg was sent successfully
            MessageItem si;
            int position;
            String pal;
            if (pos < 0) {
                position = size;
                pal = cpal;
            } else {
                position = pos;
                pal = msg[4];
            }

            Conversation conv;
            String userDB;
            if (msg[0].equals("groupChat")) {
                conv = (Conversation) groupTrack.get(SendToServer.getUsername() + recvr + position);
                conv.setStatus("[SNT]");
                userDB = SendToServer.getUsername() + "_G_" + recvr;
                if (chatScreen != null) {
                    if (TextUtil.splitAndTrim(chatScreen.getTitle(), '\n')[0].equals(pal)) {
                        //#style Gmessage
                        si = new MessageItem(Locale.get("txt.me") + " | " + cal.format(conv.getDateTime()) + " " + conv.getStatus(), conv.getMessage());
                        chatScreen.set(FramedForm.FRAME_CENTER, position, si);//format time to standard and display with msg - [SNDN] rep sending icon
                    }
                }
            } else {
                conv = (Conversation) chatTrack.get(SendToServer.getUsername() + recvr + position);
                conv.setStatus("[SNT]");
                userDB = SendToServer.getUsername() + recvr;
                if (chatScreen != null) {
                    if (TextUtil.splitAndTrim(pal, '\n')[1].equals(recvr)) {
                        //#style message
                        si = new MessageItem(cal.format(conv.getDateTime()) + " " + conv.getStatus(), conv.getMessage());
                        chatScreen.set(FramedForm.FRAME_CENTER, position, si);//format time to standard and display with msg - [SNDN] rep sending icon
                    }
                }
            }
            Object obj = readRs("history");
            Hashtable hist;
            if (obj == null) {
                hist = new Hashtable();
            } else {
                hist = (Hashtable) obj;
            }
            Object o = hist.get(userDB);
            Hashtable userhist;
            if (o == null) {
                userhist = new Hashtable();
            } else {
                userhist = (Hashtable) o;
            }

            userhist.put(new Integer(userhist.size()), conv);
            hist.put(userDB, userhist);
            saveToRms(hist, "history");

        }
        System.out.println("End of sending mesaage attempt " + testCount);
    }

    public void sendFeedBack(String feedback, int testCount, int attempt) {
        String response = SendToServer.fBack(feedback);
        if (response.equals("-2") || response.trim().equals("")) {
            testCount++;
            if (testCount < attempt) {
                sendFeedBack(feedback, testCount, attempt);
            }
        }
    }

    private void logout() {
        resetUserValues();
        getDisplay().setCurrent(getScreen(new List(null, List.IMPLICIT), 0, ""));
        displayUserLoginInfo();
        removeRs("phoneContact");
        removeRs("flickr");
        removeRs("groups");
        removeRs("inbox");
        removeRs("userLogin");
    }

    private void executeOperation(final String action, final String[] data, long delay, final int testCount, final int attempt) {
        class MyClass extends Thread {

            public void run() {
                if (action.equals("login")) {
                    //#style notif
                    StringItem si = new StringItem("", Locale.get("txt.logginloadin"));
                    try {
                        formLogin.set(4, si);
                    } catch (IndexOutOfBoundsException e) {
                        formLogin.insert(4, si);
                    }
                    CustomGauge customGauge = new CustomGauge();
                    customGauge.setTitle("Loading...");
                    login(data, customGauge, testCount, attempt);
                } else if (action.equals("invite")) {
                    invite(data);
                } else if (action.equals("inviteP")) {
                    inviteP(data);
                } else if (action.equals("sendchat")) {
                    sendMsg(data, testCount, attempt);
                } else if (action.equals("fBack")) {
                    sendFeedBack(data[0], testCount, attempt);
                } else if (action.equals("acceptReq")) {
//                    if(acceptRequest(data[0])){
//                        list.delete(Integer.parseInt(data[1]));
//                        requestList.removeElementAt(Integer.parseInt(data[1]));
//                        if(!requestList.isEmpty())
//                            getDisplay().setCurrent( getScreen(new List(null, List.IMPLICIT), 13,"") );
//                        else
//                            getDisplay().setCurrent( getScreen(new List(null, List.IMPLICIT), 2,"") );
//                    }
                } else if (action.equals("declineReq")) {
//                    if(declineRequest(new String[]{data[0]})){
//                        list.delete(Integer.parseInt(data[1]));
//                        requestList.removeElementAt(Integer.parseInt(data[1]));
//                        if(!requestList.isEmpty())
//                            getDisplay().setCurrent( getScreen(new List(null, List.IMPLICIT), 13,"") );
//                        else
//                            getDisplay().setCurrent( getScreen(new List(null, List.IMPLICIT), 2,"") );
//                    }
                } else if (action.equals("blockReq")) {
//                    if(blockRequest(new String[]{data[0]})){
//                        list.delete(Integer.parseInt(data[1]));
//                        requestList.removeElementAt(Integer.parseInt(data[1]));
//                        if(!requestList.isEmpty())
//                            getDisplay().setCurrent( getScreen(new List(null, List.IMPLICIT), 13,"") );
//                        else
//                            getDisplay().setCurrent( getScreen(new List(null, List.IMPLICIT), 2,"") );
//                    }
                } else if (action.equals("fChat")) {
                    if (!state.equals("login")) {
                        fetchChat();
                    }
                    if (!state.equals("login")) {
                        executeOperation("fChat", new String[]{}, 10000, 0, 0);
                    }
                } else if (action.equals("fGChat")) {
                    if (!state.equals("login")) {
                        fetchGroupChat();
                    }
                    if (!state.equals("login")) {
                        executeOperation("fGChat", new String[]{}, 10000, 0, 0);
                    }
                } else if (action.equals("checkPals")) {
                    if (!state.equals("login")) {
                        getPals("");
                    }
                    if (!state.equals("login")) {
                        executeOperation("checkPals", new String[]{}, 1800000, 0, 0);//go again to check after 30min
                    }
                } else if (action.equals("flickr")) {
                    String[] flickr;
                    if (!state.equals("login")) {
                        flickr = getFlickrCat(data, testCount, attempt);
                    } else {
                        flickr = new String[]{};
                    }
                    cg.deleteAll();
                    if (state.equals("General Settings")) {
                        cg.setLabel(Locale.get("txt.flickerSubscription"));
                        for (int i = 0; i < flickr.length; i++) {
                            if (flickr[i].charAt(0) == '@') {//is users settings
                                //#style mainMenuItemAnimated
                                int index = cg.append(replaceString(replaceString(replaceString(replaceString(flickr[i].substring(1), "gd[str]", "|"), "gd[til]", "~"), "gd[att]", "@"), "gd[col]", ":"), null);
                                cg.setSelectedIndex(index, true);
                            } else {
                                //#style mainMenuItemAnimated
                                cg.append(replaceString(replaceString(replaceString(replaceString(flickr[i], "gd[str]", "|"), "gd[til]", "~"), "gd[att]", "@"), "gd[col]", ":"), null);
                            }
                        }
                    }
                } else if (action.equals("sflickr")) {
                    saveSelectedFlickr(data);
                } else if (action.equals("sPrivacySettings")) {
                    saveSelectedPrivacy(data);
                } else if (action.equals("gflickr")) {
                    if (!state.equals("login")) {
                        getFlick(testCount, attempt);
                    }
                    if (!state.equals("login")) {
                        executeOperation("gflickr", null, 500000, 0, 10);
                    }
                } else if (action.equals("getUsersWithBDFor")) {
                    getUsersWithBDFor(testCount, attempt);
                } else if (action.equals("getUserprofile")) {
                    if (!state.equals("login")) {
                        getUserProfile(data[0], testCount, attempt);
                    }
                } else if (action.equals("saveStatus")) {
                    saveStatus(data);
                } else if (action.equals("tranferCredit")) {
                    if (!state.equals("login")) {
                        tranferCredit(data, testCount, attempt);
                    }
                } else if (action.equals("groupRooms")) {
                    if (!state.equals("login")) {
                        getGroupRooms(data[0], testCount, attempt);
                    }
                    if (!state.equals("login")) {
                        executeOperation("groupRooms", new String[]{SendToServer.getUsername()}, 600000, 0, 0);
                    }
                } else if (action.equals("create group")) {
                    createGroupChat(data[0], testCount, attempt);
                } else if (action.equals("publicsettings")) {
                    String dat[];
                    if (!state.equals("login")) {
                        dat = getPublicProfileSettings(testCount, attempt);
                    } else {
                        dat = new String[]{};
                    }
                    cg.deleteAll();
                    if (state.equals("publicsettings")) {
                        if (dat.length > 0) {
                            cg.setLabel(Locale.get("txt.ppsettingav"));
                        } else {
                            cg.setLabel(Locale.get("txt.ppsettingun"));
                        }
                        for (int i = 0; i < dat.length; i++) {
                            if (dat[i].charAt(0) == '@') {//is users settings
                                //#style mainMenuItemAnimated
                                int index = cg.append(dat[i].substring(1), null);
                                cg.setSelectedIndex(index, true);
                            } else {
                                //#style mainMenuItemAnimated
                                cg.append(dat[i], null);
                            }
                        }
                    }
                } else if (action.equals("addtogroup")) {
                    if (!state.equals("login")) {
                        addToGroup(data, testCount, attempt);
                    }
                } else if (action.equals("removemember")) {
                    if (!state.equals("login")) {
                        removeMember(data, testCount, attempt);
                    }
                } else if (action.equals("leavegroup")) {
                    if (!state.equals("login")) {
                        leaveGroup(data[0], testCount, attempt);
                    }
                } else if (action.equals("defriend")) {
                    if (!state.equals("login")) {
                        defriend(data[0], testCount, attempt);
                    }
                } else if (action.equals("madmin")) {
                    if (!state.equals("login")) {
                        makeAdmin(data, testCount, attempt);
                    }
                } else if (action.equals("upload")) {
                    if (!state.equals("login")) {
                        uploadImg(data[0], testCount, attempt);
                    }
                } else if (action.equals("changePassword")) {
                    if (!state.equals("login")) {
                        changePassword(data[0], testCount, attempt);
                    }
                } else if (action.equals("getImage")) {
                    if (!state.equals("login")) {
                        displayImage(data, "&px=");
                    }
                } else if (action.equals("sendContact")) {
                    if (!state.equals("login")) {
                        sendContactToServer(testCount, attempt);
                    }
                }

            }
        }
        if (action.equals("login") || action.equals("upload") || action.equals("getImage") || action.equals("changePassword")
                || action.equals("madmin") || action.equals("fGChat") || action.equals("defriend")
                || action.equals("addtogroup") || action.equals("leavegroup") || action.equals("removemember")
                || action.equals("sPrivacySettings") || action.equals("publicsettings")
                || action.equals("create group") || action.equals("groupRooms") || action.equals("checkPals")
                || action.equals("invite") || action.equals("inviteP") || action.equals("sendchat")
                || action.equals("acceptReq") || action.equals("declineReq") || action.equals("blockReq")
                || action.equals("flickr") || action.equals("sflickr") || action.equals("getUsersWithBDFor")
                || action.equals("gflickr") || action.equals("fChat") || action.equals("fBack")
                || action.equals("getUserprofile") || action.equals("saveStatus") || action.equals("tranferCredit")
                || action.equals("sendContact")) {
            MyClass tt = new MyClass();
            try {
                MyClass.sleep(delay);
                tt.start();
            } catch (InterruptedException ex) {
            }
        } else {
            try {
                displayAlert(Locale.get("alert.error"), Locale.get("alert.error404"), Image.createImage("/x.png"), AlertType.ERROR, Alert.FOREVER);
            } catch (IOException ex) {
                displayAlert(Locale.get("alert.error"), Locale.get("alert.error404"), null, AlertType.ERROR, Alert.FOREVER);
            }
        }

    }

    public void displayUserLoginInfo() {
        String user, pass;
        Object obj = readRs("userLogin");
        Vector userLogin;
        if (obj != null) {
            userLogin = (Vector) obj;
        } else {
            userLogin = new Vector();
        }

        if (!userLogin.isEmpty()) {
            user = (String) userLogin.elementAt(0);
            pass = (String) userLogin.elementAt(1);
            ((TextField) formLogin.get(1)).setString(user);
            ((TextField) formLogin.get(2)).setString(pass);
        }
    }

    private void displayChat() {
        chatScreen.deleteAll(FramedForm.FRAME_CENTER);
        String[] currentPalUsername = TextUtil.splitAndTrim(currentPal, '\n');
        System.out.println("displayChat               1");
        Object obj = readRs("history");
        Hashtable hist;
        if (obj == null) {
            hist = new Hashtable();
        } else {
            hist = (Hashtable) obj;
        }
        Object o;
        String userDB;
        if (!state.equals("groupChat")) {
            userDB = SendToServer.getUsername() + currentPalUsername[1];
        } else {
            userDB = SendToServer.getUsername() + "_G_" + groupIds.elementAt(0);
        }
        o = hist.get(userDB);
        Hashtable userhist;
        if (o == null) {
            userhist = new Hashtable();
        } else {
            userhist = (Hashtable) o;
        }
        MyCalendar cal = new MyCalendar();
        for(int i = 1;i<=userhist.size();i++){
            Integer key = new Integer(i);
            Conversation conver = (Conversation) userhist.get(key);
            System.out.println(i+"    PING!!!"+userhist.get(key));
            //Hashtable ht = (Hashtable)getContactMap(false);
            
            //Contacts cont = (Contacts)ht.get(conver.getSender());
            System.out.println(i+"    PING!!!");
            MessageItem si;
            if (conver.getSender().equalsIgnoreCase(SendToServer.getPhone())) {//the sender is  me    
                System.out.println(i+"    PING!!!");
                if(state.equals("groupChat")){
                    System.out.println(cal.format(conver.getDateTime())+"??????????????????????");
                    //#style Gmessage
                    si = new MessageItem(Locale.get("txt.me") + " " + cal.format(conver.getDateTime()) + " " + conver.getStatus(), conver.getMessage());
                }else{
                    //#style message
                    si = new MessageItem(cal.format(conver.getDateTime()) + " " + conver.getStatus(), conver.getMessage());
                }
                
            } else {
                System.out.println(i+"    PING!!!2");
                if (state.equals("groupChat")) {
                    System.out.println(cal.format(conver.getDateTime())+"??????????????????????");
                    //#style GmessagePal
                    si = new MessageItem(conver.getSenderName() + " " + cal.format(conver.getDateTime()), conver.getMessage());
                } else {
                    //#style messagePal
                    si = new MessageItem(cal.format(conver.getDateTime()), conver.getMessage());
                }
            }
            chatScreen.append(FramedForm.FRAME_CENTER, si);
        }
        System.out.println("displayChat               7");
        if (chatScreen.size(FramedForm.FRAME_CENTER) != 0) {
            chatScreen.setActiveFrame(FramedForm.FRAME_CENTER);
            chatScreen.focus(chatScreen.size(FramedForm.FRAME_CENTER) - 1);
        }
        chatScreen.setActiveFrame(FramedForm.FRAME_BOTTOM);
    }

    private void fetchChat() {
        System.out.println("Fetching chat....");
        String cPal = currentPal;
        String response = SendToServer.fChat();
        if (response.equals("0") || response.trim().equals("")) {
        } else if (response.equals("-2")) {
        } else {
            if (response.endsWith("|")) {
                response = response.substring(0, response.length() - 1);
            }
            System.out.println(response+" is the response for fethhat");
            String[] messages = TextUtil.splitAndTrim(response, '|');
            for (int i = 0; i < messages.length; i++) {
                String[] msg = TextUtil.splitAndTrim(replaceString(replaceString(replaceString(messages[i], "gd[str]", "|"), "gd[col]", ":"), "gd[att]", "@"), '~');
                executeOperation("fBack", new String[]{msg[3]}, 0, 0, 5);
                Object obj = readRs("history");
                Hashtable hist;
                if (obj == null) {
                    hist = new Hashtable();
                } else {
                    System.out.println("gothistory now converting....");
                    hist = (Hashtable) obj;
                }
                Object o = hist.get(SendToServer.getUsername() + msg[0]);
                Hashtable userhist;
                if (o == null) {
                    userhist = new Hashtable();
                } else {
                    userhist = (Hashtable) o;
                }
                System.out.println(userhist.size()+" <============"+i);
                String[] val = TextUtil.splitAndTrim(msg[2], ' ');
                int locateItem = userhist.size()+1;
                Conversation conversation = new Conversation(locateItem + "", msg[0], val[0] + "@" + val[1], "", replaceString(msg[1], "gd[til]", "~"));
                userhist.put(new Integer(locateItem), conversation);
                
                System.out.println(userhist.size()+" <============>");
                hist.put(SendToServer.getUsername() + msg[0], userhist);
                saveToRms(hist, "history");
                System.out.println(userhist.size()+" <============>"+i);
                if (cPal.equals(currentPal) && !cPal.equals("") && chatScreen!=null) {
                    if (msg.length > 1) {
                        //alert the server that message with id number msg[3] has been dilivered successfully
                        String[] currentPalUsername = TextUtil.splitAndTrim(cPal, '\n');
                        
                        if (conversation.getSender().equalsIgnoreCase(currentPalUsername[1])) {
                            MyCalendar cal = new MyCalendar();
                            //#style messagePal
                            MessageItem si = new MessageItem(cal.format(conversation.getDateTime()), conversation.getMessage());
//                            //#style messagePal
                            chatScreen.append(FramedForm.FRAME_CENTER, si);
                            if (chatScreen.size(FramedForm.FRAME_CENTER) != 0) {
                                chatScreen.setActiveFrame(FramedForm.FRAME_CENTER);
                                chatScreen.focus(chatScreen.size(FramedForm.FRAME_CENTER) - 1);
                                chatScreen.setActiveFrame(FramedForm.FRAME_BOTTOM);
                            }
                        } else {
                            offlineMsgStore(conversation.getSender());
                            //soundPlayer.startP1();
                            //AlertType.CONFIRMATION.playSound(display);
                        }
                    }
                } else {
                    System.out.println(conversation.getSender()+"::::::::::::::"+i);
                    offlineMsgStore(conversation.getSender());
                    //soundPlayer.startP1();
                }
            }
        }
    }

    private void fetchGroupChat() {
        String response = SendToServer.fGroupChat();
        if (response.equals("0") || response.trim().equals("")) {
        } else if (response.equals("-2")) {
        } else {
            if (response.endsWith("|")) {
                response = response.substring(0, response.length() - 1);
            }
            String[] messages = TextUtil.splitAndTrim(response, '|');
            try {
                for (int i = 0; i < messages.length; i++) {
                    String[] msg = TextUtil.splitAndTrim(replaceString(replaceString(replaceString(messages[i], "gd[str]", "|"), "gd[col]", ":"), "gd[att]", "@"), '~');
                    Object obj = readRs("history");
                    Hashtable hist;
                    if (obj == null) {
                        hist = new Hashtable();
                    } else {
                        hist = (Hashtable) obj;
                    }
                    String userDB = SendToServer.getUsername() + "_G_" + msg[5];
                    Object o = hist.get(userDB);
                    Hashtable userGhist;
                    if (o == null) {
                        userGhist = new Hashtable();
                    } else {
                        userGhist = (Hashtable) o;
                    }
                    String[] val = TextUtil.splitAndTrim(msg[2], ' ');
                    int locateItem = 1;
                    if (chatScreen != null) {
                        locateItem = chatScreen.size(FramedForm.FRAME_CENTER);
                        if(locateItem==0){
                            locateItem++;
                        }
                    }
                    Conversation conversation = new Conversation(locateItem + "", msg[0], val[0] + "@" + val[1], "", replaceString(msg[1], "gd[til]", "~"));
                    conversation.setGroupId(msg[5]);
                    conversation.setSenderName(msg[4]);
                    userGhist.put(new Integer(locateItem), conversation);
                    hist.put(userDB, userGhist);
                    saveToRms(hist, "history");
                    if (msg.length > 1) {
                        if (state.equals("groupChat")) {
                            if (chatScreen.getTitle().equals(msg[3])) {
                                
                                MyCalendar cal = new MyCalendar();
                                //#style GmessagePal
                                MessageItem si = new MessageItem(conversation.getSender() + " " + cal.format(conversation.getDateTime()), conversation.getMessage());
                                chatScreen.append(FramedForm.FRAME_CENTER, si);
                                if (chatScreen.size(FramedForm.FRAME_CENTER) != 0) {
                                    chatScreen.setActiveFrame(FramedForm.FRAME_CENTER);
                                    chatScreen.focus(chatScreen.size(FramedForm.FRAME_CENTER) - 1);
                                    chatScreen.setActiveFrame(FramedForm.FRAME_BOTTOM);
                                }
                            } else {
                                offlineMsgStore(conversation.getGroupId());
//                                 soundPlayer.startP2();
                                //AlertType.CONFIRMATION.playSound(display);
                            }
                        } else {
                            offlineMsgStore(conversation.getGroupId());
//                               soundPlayer.startP2();
                            //AlertType.CONFIRMATION.playSound(display);
                        }
                    }
                }
            } catch (ArrayIndexOutOfBoundsException ex) {
            }
        }
    }

    public byte[] get_Byte_Array(Image img) {
        int[] imgRgbData = new int[img.getWidth() * img.getHeight()];
        byte[] imageData = null;
        try {
            img.getRGB(imgRgbData, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());
        } catch (Exception e) {
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            for (int i = 0; i < imgRgbData.length; i++) {
                dos.writeInt(imgRgbData[i]);
            }
            imageData = baos.toByteArray();
            baos.close();
            dos.close();
        } catch (Exception e) {
        }
        return imageData;
    }

    private void displayImage(String[] data, String str) {
        if (data.length > 1) {
            getImage("http://9gistmobile.gossout.com/ver1.1/get_dp.php?user=" + data[0] + str);
        } else {
            getImage("http://9gistmobile.gossout.com/ver1.1/get_dp.php?user=" + data[0]);
        }
    }

    private void getImage(final String url) {
        Image img;
        ContentConnection connection;
        try {
            connection = (ContentConnection) Connector.open(url);
            DataInputStream iStrm = connection.openDataInputStream();
            Image im = null;
            try {
                byte imageData[];
                ByteArrayOutputStream bStrm = new ByteArrayOutputStream();
                int ch;
                while ((ch = iStrm.read()) != -1) {
                    bStrm.write(ch);
                }
                imageData = bStrm.toByteArray();
                bStrm.close();
                if (new String(imageData).equals("null")) {
                    im = null;
                } else {
                    im = Image.createImage(imageData, 0, imageData.length);
                }

            } finally {
                if (iStrm != null) {
                    iStrm.close();
                }
                if (connection != null) {
                    connection.close();
                }
            }
            img = (im == null ? null : im);

            if (state.equals("dpEnlarge")) {
                try {
                    formLogin.delete(0);
                } catch (IndexOutOfBoundsException e) {
                }
                //#style label
                formLogin.append(new ImageItem("", img, ImageItem.BUTTON, "No Image Found"));
            } else if (state.equals("profile") || state.equals("groupmembers") || state.equals("listFriends")) {
                profile.delete(FramedForm.FRAME_TOP, 0);
                //#style myLabelItemsPic
                profile.append(FramedForm.FRAME_TOP, new ImageItem("", img, ImageItem.BUTTON, "No Image Found"));
                //javax.microedition.io.
            }
        } catch (IOException ex) {
            try {
                formLogin.delete(0);
                //#style notifR
                formLogin.insert(0, new StringItem("", Locale.get("alert.grpNtwrErr")));
            } catch (IndexOutOfBoundsException e) {
            }

        }
    }

    private void uploadImg(String filename, int testCount, int attemp) {
        byte[] b = null;

        String fileType;
        if (filename.endsWith(".jpg")) {
            fileType = "image/jpg";
        } else if (filename.endsWith(".png")) {
            fileType = "image/png";
        } else if (filename.endsWith(".gif")) {
            fileType = "image/gif";
        } else {
            fileType = "";
            try {
                displayAlert(Locale.get("alert.error"), Locale.get("alert.filenotsupported"), Image.createImage("/x.png"), AlertType.ERROR, 10000);
            } catch (IOException ex) {
            }
            return;
        }
        try {
            FileConnection fc = (FileConnection) Connector.open("file:///" + currentRoot + filename);
            currentRoot = currentRoot + "/";
            int fileSize = (int) (fc.fileSize());
            //Build a form that it will displays a progress bar and
            //the amount of data that haw been sent
            formLogin.deleteAll();
            formLogin.removeAllCommands();
            //#style popAlert
            formLogin = new Form(Locale.get("txt.uploading"));

            Gauge gauge = new Gauge(Locale.get("txt.progress"), false, fileSize, 0);
            ////#style notif
            StringItem stringitem = new StringItem("", "0 / " + fileSize);
            formLogin.append(gauge);
            formLogin.append(stringitem);
            getDisplay().setCurrent(formLogin);
            if (!fc.exists()) {
                displayAlert(Locale.get("alert.error"), Locale.get("alert.filenotexist"), Image.createImage("/x.png"), AlertType.ERROR, 10000);
            }
            InputStream is = fc.openInputStream();
            b = new byte[(int) fc.fileSize()];
            int length = is.read(b, 0, (int) fc.fileSize());
            Image temp_img = Image.createImage(b, 0, b.length);
            Hashtable params = new Hashtable();
            params.put("user", SendToServer.getUsername());
            params.put("width", Integer.toString(temp_img.getWidth()));
            params.put("height", Integer.toString(temp_img.getHeight()));
//            HttpMultipartRequest req = new HttpMultipartRequest("http://localhost/server/uploadimage.php",params,"upload_field", filename, fileType, b);
            HttpMultipartRequest req = new HttpMultipartRequest("http://gistchat.9gist.com/uploadimage.php", params, "upload_field", filename, fileType, b);
            byte[] resp = req.send(gauge, stringitem, fileSize);
            String response = new String(resp);
            if (response.equals("-1")) {
                displayAlert(Locale.get("alert.error"), Locale.get("alert.uploadErr"), Image.createImage("/x.png"), AlertType.ERROR, 10000);
            } else if (response.equals("1")) {
                displayAlert(Locale.get("alert.successT"), Locale.get("alert.success"), Image.createImage("/v.png"), AlertType.ERROR, 10000);
            }
            //Flushing the output
            stringitem.setText(Locale.get("txt.loadingContent"));
            stringitem.setText(Locale.get("alert.opsuccess"));
            formLogin.addCommand(CommandBuilder.getBackCmd());
            formLogin.setCommandListener(this);
        } catch (Exception e) {
        }
    }

    private void getPals(String value) {
        String pal[];
        String response = SendToServer.getPals(value);
        if (response.equals("-1") || response.trim().equals("")) {
            // No friend found
        } else if (response.equals("-2")) {
            if (!state.equals("login")) {
                getPals(value);
            }
        } else {
            if (response.endsWith("|")) {
                response = response.substring(0, response.length() - 1);
            }
            pal = TextUtil.splitAndTrim(response, '|');
            Hashtable contact = (Hashtable) getContactMap(false);
            for (int i = 0; i < pal.length; i++) {
                String x[] = TextUtil.splitAndTrim(replaceString(replaceString(replaceString(pal[i], "gd[str]", "|"), "gd[att]", "@"), "gd[col]", ":"), '~');
                if (x.length > 3) {
                    if (contact.containsKey(x[3])) {
                        Contacts cnt;
                        cnt = (Contacts) contact.get(x[3]);
                        cnt.setUsername(x[0]);
                        cnt.setGender(x[2]);
                        cnt.setOnGistStatus("Y");
                        cnt.setPersonalMsg(x[4]);
                        contact.put(x[3], cnt);
                        saveToRms(contact, "phoneContact");
                    } else {
                        contact.put(x[3], new Contacts(x[0].toLowerCase().trim(), x[1], x[2], x[3], x[4]));
                        saveToRms(contact, "phoneContact");
                    }
                }
            }
        }
    }

    private String[] getListNames() {//get lists displayed after clicking chat option
        String[] mylist;

        mylist = new String[]{Locale.get("txt.friend"), Locale.get("list.groupchat"), Locale.get("list.invitefrnd"), Locale.get("list.setting")};
        return mylist;
    }

    private void changePassword(String newP, int testCount, int attempt) {
        try {
            String response = SendToServer.changePassword(newP);
            if (response.equals("-2")) {
                testCount++;
                if (testCount < attempt) {
                    changePassword(newP, testCount, attempt);
                } else {
                    displayAlert(Locale.get("alert.error"), Locale.get("alert.ntwrkErrFrq"), Image.createImage("/x.png"), AlertType.ERROR, 10000);
                }
            } else {
                SendToServer.setPassword(newP);
                if (state.equals("changePassword")) {
                    try {
                        formLogin.delete(5);
                    } catch (IndexOutOfBoundsException e) {
                    }
                }
                displayAlert(Locale.get("alert.successT"), Locale.get("alert.chngPass"), Image.createImage("/v.png"), AlertType.ERROR, 10000);
            }
        } catch (IOException ex) {
        }
    }

    private void login(String[] data, CustomGauge customGauge, int testCount, int attempt) {
        if (data[0].trim().equals("") && data[1].trim().equals("")) {
            try {
                displayAlert(Locale.get("alert.error"), Locale.get("alert.wrngPass"), Image.createImage("/x.png"), AlertType.ERROR, 10000);
            } catch (IOException ex) {
                displayAlert(Locale.get("alert.error"), Locale.get("alert.wrngPass"), null, AlertType.ERROR, 10000);
            }
            return;
        }
        customGauge.setTitle(Locale.get("txt.logginloadin"));
        SendToServer.setUsername(data[0]);
        SendToServer.setPassword(data[1]);
        getDisplay().setCurrent(customGauge);

        String response = SendToServer.login(getAppProperty("MIDlet-Version"));

        if (response.equals("-2") || response.trim().equals("")) {
            testCount++;
            if (testCount < attempt) {
                customGauge.setTitle(Locale.get("alert.retryLogin"));
                login(data, customGauge, testCount, attempt);
            } else {
                getDisplay().setCurrent(formLogin);
                formLogin.requestRepaint();
                //#style notifR
                formLogin.set(4, new StringItem("", Locale.get("alert.grpNtwrErr")));
            }
        } else if (response.equals("-10")) {
            getDisplay().setCurrent(formLogin);
            formLogin.requestRepaint();
            //#style notifR
            formLogin.set(4, new StringItem("", Locale.get("alert.criticalUpdate")));
        } else if (response.equals("-1")) {
            getDisplay().setCurrent(formLogin);
            formLogin.requestRepaint();
            //#style notifR
            formLogin.set(4, new StringItem("", Locale.get("alert.wrngPass")));
        } else if (response.equals("0")) {
            getDisplay().setCurrent(formLogin);
            formLogin.requestRepaint();
            //#style notifR
            formLogin.set(4, new StringItem("", Locale.get("alert.userNonActive")));
        } else {
            //login was successful and user is activated
            try {
                formLogin.delete(4);
            } catch (IndexOutOfBoundsException e) {
            }

            String[] info = TextUtil.splitAndTrim(response, '~');
            if (info.length > 1) {
                Hashtable ht = new Hashtable();
                ht.put("username", data[0]);
                ht.put("password", data[1]);
                ht.put("phonecode", info[0]);
                ht.put("phone", info[1]);

                SendToServer.setPhone(info[1]);
                SendToServer.setPhoneCode(info[0]);

                getContactMap(true);
                saveToRms(ht, "userLogin");
                getDisplay().setCurrent(getScreen(new List("", List.IMPLICIT), 1, ""));
                executeOperation("checkPals", new String[]{"1"}, 1000, 0, 0);
                executeOperation("sendContact", new String[]{}, 1000, 0, 5);
                executeOperation("fChat", new String[]{}, 3000, 0, 0);
                executeOperation("gflickr", new String[]{}, 4000, 0, 0);
                executeOperation("groupRooms", new String[]{SendToServer.getUsername()}, 0, 0, 5);
                executeOperation("fGChat", new String[]{}, 5000, 0, 0);
                executeOperation("getUsersWithBDFor", new String[]{}, 6000, 0, 50);

                Object obj = readRs("inbox");
                Vector offline;
                if (obj != null) {
                    offline = (Vector) obj;
                } else {
                    offline = new Vector();
                }
                offline.trimToSize();
                if (!offline.isEmpty()) {
                    setNewMsgStatus("/newMSG.png");
                }
            }

        }
    }

    private void invite(String[] data) {
        try {
            String response = SendToServer.palReq(data[0], "palReq");
            if (response.equals("1")) {
                displayAlert(Locale.get("alert.successT"), Locale.get("alert.success"), Image.createImage("/v.png"), AlertType.INFO, 10000);
                inviteTxt.setString("");
            } else if (response.equals("-1")) {
                displayAlert(Locale.get("alert.error"), Locale.get("alert.usernameNotExist"), Image.createImage("/x.png"), AlertType.INFO, 10000);
            } else if (response.equals("-2") || response.trim().equals("")) {
                displayAlert(Locale.get("alert.error"), Locale.get("alert.ntwrkErrFrq"), Image.createImage("/x.png"), AlertType.ERROR, 10000);
            } else {
                displayAlert(Locale.get("alert.error"), Locale.get("alert.alreadyFrnW") + data[0], Image.createImage("/x.png"), AlertType.INFO, 10000);
                inviteTxt.setString("");
            }
        } catch (IOException ex) {
            try {
                displayAlert(Locale.get("alert.error"), ex.getMessage() + " \nDebug: invite(Method)", Image.createImage("/x.png"), AlertType.ERROR, -1);
            } catch (IOException ex1) {
            }
//--logout();
        }
    }

    private void inviteP(String[] data) {
        try {
            String response = SendToServer.palReq(data[0], "phReq");
            if (response.equals("1")) {
                displayAlert(Locale.get("alert.successT"), Locale.get("alert.success"), Image.createImage("/v.png"), AlertType.INFO, 10000);
                inviteNum.setString("");
            } else if (response.equals("-1")) {
                displayAlert(Locale.get("alert.error"), Locale.get("alert.unregPhone"), Image.createImage("/x.png"), AlertType.INFO, 10000);
            } else if (response.equals("-2")) {
                displayAlert(Locale.get("alert.error"), Locale.get("alert.ntwrkErrFrq"), Image.createImage("/x.png"), AlertType.ERROR, 10000);
            } else {
                displayAlert(Locale.get("alert.error"), Locale.get("alert.ntwrkErrFrq") + data[0], Image.createImage("/x.png"), AlertType.INFO, 10000);
                inviteNum.setString("");
            }
        } catch (IOException ex) {
        }
    }

    private Displayable getFriendList(String title, Hashtable offlineSender,Hashtable contact, int listType) {
        //#style mainMenuScreenTop
        FilteredList mylist = new FilteredList(title, listType);
        Enumeration off = offlineSender.keys();
        Enumeration c = contact.keys();
        //Dispay all those who left ofline message
        while (off.hasMoreElements()) {
            String key = off.nextElement().toString();
            Contacts cont = (Contacts) contact.get(key);
            String disp = cont.getName() + "\n" + cont.getPhone();
            try {
                if (cont.getGender().equals("M")) {
                    //#style mainMenuItemEmphasis
                    mylist.append("nwMSG " + disp, Image.createImage("/dummyimgmal.png"));
                } else {
                    //#style mainMenuItemEmphasis
                    mylist.append("nwMSG " + disp, Image.createImage("/dummyimgfem.png"));
                }
            } catch (IOException ex) {
                //#style mainMenuItemEmphasis
                mylist.append("nwMSG " + disp, null);
            }
        }
        //Dispay all those who are online
        while (c.hasMoreElements()) {
            String key = c.nextElement().toString();
            if (!offlineSender.containsKey(key)) {
                Contacts cont = (Contacts) contact.get(key);
                String disp = cont.getName() + "\n" + cont.getPhone();
                if (cont.isOnGist()) {
                    try {
                        if (cont.getGender().equals("M")) {
                            //#style mainMenuItemEmphasis
                            mylist.append(disp, Image.createImage("/dummyimgmal.png"));
                        } else {
                            //#style mainMenuItemEmphasis
                            mylist.append(disp, Image.createImage("/dummyimgfem.png"));
                        }
                    } catch (IOException ex) {
                        //ex.printStackTrace();
                    }
                }
            }
        }
        c = contact.keys();
        //Dispay all those who are not yet registered
        while (c.hasMoreElements()) {
            String key = c.nextElement().toString();
            if (!offlineSender.containsKey(key)) {
                Contacts cont = (Contacts) contact.get(key);
                String disp = cont.getName() + "\n" + cont.getPhone();
                if (!cont.isOnGist()) {
                    try {
                        //#style mainMenuItemOffline
                        mylist.append(disp, Image.createImage("/dummyimgunk.png"));
                    } catch (IOException ex) {
                        //#style mainMenuItemOffline
                        mylist.append(disp, null);
                    }
                }
            }
        }
        mylist.setCommandListener(this);

        friendList = mylist;
        return friendList;
    }

    private String[] getFlickrCat(String[] data, int testCount, int attempt) {
        String response = SendToServer.flkr();
        if (response.equals("0")) {
            data = new String[]{};
        } else if (response.equals("-2") || response.trim().equals("")) {
            testCount++;
            if (testCount < attempt && !state.equals("login")) {
                flickrScreen.delete(FramedForm.FRAME_CENTER, 0);
                //#style notif
                flickrScreen.append(FramedForm.FRAME_CENTER, new StringItem("", Locale.get("alert.loadinCatDelay")));
                if (!state.equals("login")) {
                    getFlickrCat(data, testCount, attempt);
                }
            } else {
                flickrScreen.delete(FramedForm.FRAME_CENTER, 0);
                //#style notifR
                flickrScreen.append(FramedForm.FRAME_CENTER, new StringItem("", Locale.get("alert.loadinCatDelay")));
                displayAlert(Locale.get("alert.error"), Locale.get("alert.flickrCatErr"), null, AlertType.CONFIRMATION, 10000);
            }

        } else {
            if (!state.equals("General Settings")) {
                return new String[]{};
            }
            if (response.endsWith("|")) {
                response = response.substring(0, response.length() - 1);
            }
            data = TextUtil.splitAndTrim(response, '|');
            //#style button
            StringItem save = new StringItem("", Locale.get("cmd.save"), StringItem.BUTTON);
            save.setDefaultCommand(CommandBuilder.getSaveCmd());
            flickrScreen.append(FramedForm.FRAME_BOTTOM, save);
        }
        return data;
    }

    private void getFlick(int testCount, int attempt) {
        String response = SendToServer.gflkr(getAppProperty("MIDlet-Version"));
        if (response.equals("-1")) {
        } else if (response.equals("-2")) {
            testCount++;
            if (testCount < attempt) {
                if (!state.equals("login")) {
                    getFlick(testCount, attempt);
                }
            }
        } else if (response.equals("0") || response.trim().equals("")) {
        } else {
            Object obj = readRs("flickr");
            Hashtable ht;
            if (obj != null) {
                ht = (Hashtable) obj;
            } else {
                ht = new Hashtable();
            }
            ht.put(Locale.formatDate(System.currentTimeMillis(), "yyyy-MM-DD"), replaceString(replaceString(replaceString(replaceString(response, "gd[str]", "|"), "gd[til]", "~"), "gd[att]", "@"), "gd[col]", ":"));
            saveToRms(ht, "flickr");
        }
    }

    private void getUsersWithBDFor(int testCount, int attempt) {
        String response = SendToServer.getBirthDay();
        if (response.equals("0") || response.trim().equals("")) {
        } else if (response.equals("-2")) {
            testCount++;
            if (testCount < attempt) {
                if (!state.equals("login")) {
                    getUsersWithBDFor(testCount, attempt);
                }
            }
        } else {
            Object obj = readRs("bday");
            Hashtable ht;
            if (obj != null) {
                ht = (Hashtable) obj;
            } else {
                ht = new Hashtable();
            }
            ht.put(Locale.formatDate(System.currentTimeMillis(), "MM-DD"), replaceString(replaceString(replaceString(replaceString(response, "gd[str]", "|"), "gd[til]", "~"), "gd[att]", "@"), "gd[col]", ":"));
            saveToRms(ht, SendToServer.getUsername() + "bday");
        }
    }

    private void saveSelectedFlickr(String[] data) {
        try {
            String response = SendToServer.sflkr(data[0]);
            if (response.equals("0")) {
                displayAlert(Locale.get("alert.error"), Locale.get("alert.settingErr"), Image.createImage("/x.png"), AlertType.WARNING, 10000);
            } else if (response.equals("-2")) {
                displayAlert(Locale.get("alert.error"), Locale.get("alert.ntwrkErrFrq"), Image.createImage("/x.png"), AlertType.ERROR, 10000);
            } else {
                displayAlert(Locale.get("alert.successT"), Locale.get("alert.setting"), Image.createImage("/v.png"), AlertType.CONFIRMATION, 10000);
            }
        } catch (IOException ex) {
        }
        cg.setLabel(Locale.get("txt.flickerSubscription"));
    }

    private void saveSelectedPrivacy(String[] data) {
        String response = SendToServer.sPrivacy(data[0]);
        try {
            if (response.equals("-1")) {
                displayAlert(Locale.get("alert.error"), Locale.get("alert.settingErr"), Image.createImage("/x.png"), AlertType.WARNING, 10000);
            } else if (response.equals("-2")) {
                displayAlert(Locale.get("alert.error"), Locale.get("alert.ntwrkErrFrq"), Image.createImage("/x.png"), AlertType.ERROR, 10000);
            } else {
                displayAlert(Locale.get("alert.successT"), Locale.get("alert.setting"), Image.createImage("/v.png"), AlertType.CONFIRMATION, 10000);
            }
        } catch (IOException ex) {
        }
        if (state.equals("publicsettings")) {
            cg.setLabel(Locale.get("txt.ppsettingav"));
        }
    }

    private void saveStatus(String[] data) {
        try {
            String response = SendToServer.sStatus(data[0], data[1]);
            if (response.equals("0")) {
                displayAlert(Locale.get("alert.error"), Locale.get("alert.settingErr"), Image.createImage("/x.png"), AlertType.WARNING, 10000);
            } else if (response.equals("-2")) {
                displayAlert(Locale.get("alert.error"), Locale.get("alert.ntwrkErrFrq"), Image.createImage("/x.png"), AlertType.ERROR, 10000);
            } else {
                displayAlert(Locale.get("alert.successT"), Locale.get("alert.opsuccess"), Image.createImage("/v.png"), AlertType.CONFIRMATION, 10000);
            }
        } catch (IOException ex) {
        }
    }

    public void tranferCredit(String[] data, int testCount, int attempt) {
        try {
            String response = "";
            if (Integer.parseInt(data[0]) > 1) {
                response = SendToServer.transferCredit(data[0], data[1]);
            } else {
                displayAlert(Locale.get("alert.error"), Locale.get("alert.transferErr"), Image.createImage("/x.png"), AlertType.CONFIRMATION, 10000);
                return;
            }
            if (response.equals("0")) {
                displayAlert(Locale.get("alert.error"), Locale.get("alert.transferErr"), Image.createImage("/x.png"), AlertType.CONFIRMATION, 10000);
            } else if (response.equals("-2")) {
                testCount++;
                if (testCount < attempt) {
                    if (!state.equals("login")) {
                        tranferCredit(data, testCount, attempt);
                    }
                } else {
                    displayAlert(Locale.get("alert.error"), Locale.get("alert.ntwrkErrFrq"), null, AlertType.CONFIRMATION, 10000);
                }
            } else {
                displayAlert(Locale.get("alert.error"), Locale.get("alert.transfer"), Image.createImage("/v.png"), AlertType.CONFIRMATION, 10000);
            }
        } catch (IOException ex) {
        }
    }

    private void createGroupChat(String grpName, int testCount, int attempt) {
        try {
            String response = SendToServer.createGroupChat(grpName);
            if (response.equals("-1")) {
                displayAlert(Locale.get("alert.error"), Locale.get("alert.grpDup"), Image.createImage("/x.png"), AlertType.ERROR, 10000);
            } else if (response.equals("-2")) {
                testCount++;
                if (testCount < attempt) {
                    if (!state.equals("login")) {
                        createGroupChat(grpName, testCount, attempt);
                    }
                } else {
                    displayAlert(Locale.get("alert.error"), Locale.get("alert.ntwrkErrFrq"), Image.createImage("/x.png"), AlertType.CONFIRMATION, 10000);
                }
            } else {
                displayAlert(Locale.get("alert.successT"), Locale.get("alert.grpCrt"), Image.createImage("/v.png"), AlertType.CONFIRMATION, 10000);
                getDisplay().setCurrent(getScreen(new List(null, List.IMPLICIT), 18, ""));
            }
        } catch (IOException ex) {
        }
    }

    private void getGroupRooms(String user, int testCount, int attempt) {
        String response = SendToServer.getGroupRooms(user);
        if (response.equals("0") || response.trim().equals("")) {
        } else if (response.equals("-2")) {
            testCount++;
            if (testCount < attempt) {
                if (!state.equals("login")) {
                    getGroupRooms(user, testCount, attempt);
                }
            }
        } else {
            Hashtable ht = new Hashtable();
            if (response.endsWith("|")) {
                response = response.substring(0, response.length() - 1);
            }
            String[] grp = TextUtil.splitAndTrim(response, '|');
            for (int i = 0; i < grp.length; i++) {
                String[] grpdetails = TextUtil.splitAndTrim(grp[i], '~');
                Hashtable crt = new Hashtable();
                crt.put("username", grpdetails[3]);
                crt.put("name", grpdetails[4]);
                ht.put(grpdetails[0], new Groups(grpdetails[0], grpdetails[1], crt, grpdetails[2],grpdetails[5]));
            }
            if (!ht.isEmpty()) {
                saveToRms(ht, "groups");
                Enumeration keys = ht.keys();
                while (keys.hasMoreElements()) {
                    String key = keys.nextElement().toString();
                    getGroupMembers(key, testCount, attempt);
                }
            }

        }
    }

    private void getGroupMembers(String groupid, int testCount, int attempt) {
        String response = SendToServer.getGroupMembers(groupid);
        if (response.equals("0") || response.trim().equals("")) {
        } else if (response.equals("-2")) {
            testCount++;
            if (testCount < attempt) {
                if (!state.equals("login")) {
                    getGroupMembers(groupid, testCount, attempt);
                }

            }
        } else {
            if (response.endsWith("|")) {
                response = response.substring(0, response.length() - 1);
            }
            String[] grpM = TextUtil.splitAndTrim(response, '|');
            Object obj = readRs("groups");
            Hashtable ht;
            if (obj != null) {
                ht = (Hashtable) obj;
                for (int i = 0; i < grpM.length; i++) {
                    String[] mem = TextUtil.splitAndTrim(grpM[i], '~');
                    Groups group = (Groups) ht.get(groupid);
                    group.addMember(new Contacts(mem[0], mem[1], mem[2], mem[3], "NAN"));
                    ht.put(groupid, group);
                    saveToRms(ht, "groups");
                }
            }
        }
    }

    private void removeMember(String[] data, int testCount, int attempt) {
        try {
            String response = SendToServer.removeMember(data[0], data[1]);
            if (response.equals("0")) {
                displayAlert(Locale.get("alert.error"), data[0] + Locale.get("alert.grpMemRemNotFound"), Image.createImage("/x.png"), AlertType.ALARM, 10000);
            } else if (response.equals("-2")) {
                testCount++;
                if (testCount < attempt) {
                    if (!state.equals("login")) {
                        removeMember(data, testCount, attempt);
                    }
                } else {
                    displayAlert(Locale.get("alert.error"), Locale.get("alert.ntwrkErrFrq"), Image.createImage("/x.png"), AlertType.CONFIRMATION, 10000);
                }
            } else {
                String[] grp = TextUtil.splitAndTrim(response, '~');
                if (state.equals("groupmembers")) {
                    list.deleteAll();
                    for (int i = 0; i < grp.length; i++) {
                        //#style mainMenuItemAnimated
                        list.append(replaceString(replaceString(replaceString(replaceString(grp[i], "gd[str]", "|"), "gd[til]", "~"), "gd[att]", "@"), "gd[col]", ":"), null);
                    }
                }
            }
        } catch (IOException ex) {
        }
    }

    private void leaveGroup(String grpid, int testCount, int attempt) {
        try {
            String response = SendToServer.leaveGroup(grpid);
            if (response.equals("0")) {
                displayAlert(Locale.get("alert.error"), Locale.get("alert.notInGrpErr"), Image.createImage("/x.png"), AlertType.ALARM, 10000);
            } else if (response.equals("-1")) {
                displayAlert(Locale.get("alert.error"), Locale.get("alert.leaveGrpErrUnkown"), Image.createImage("/x.png"), AlertType.ALARM, attempt);
            } else if (response.equals("-2")) {
                testCount++;
                if (testCount < attempt) {
                    if (!state.equals("login")) {
                        leaveGroup(grpid, testCount, attempt);
                    }
                } else {
                    displayAlert(Locale.get("alert.error"), Locale.get("alert.ntwrkErrFrq"), Image.createImage("/x.png"), AlertType.CONFIRMATION, 10000);
                }
            } else {
                if (state.equals("groupmembers")) {
                    getDisplay().setCurrent(getScreen(new List("", List.IMPLICIT), 18, ""));
                }
            }
        } catch (IOException ex) {
        }
    }

    private void defriend(String user, int testCount, int attempt) {
        try {
            String response = SendToServer.defriend(user.substring(0, user.indexOf("\n")));
            if (response.equals("0")) {
                displayAlert(Locale.get("alert.error"), user.substring(0, user.indexOf("\n")) + Locale.get("alert.defriendAlready"), Image.createImage("/x.png"), AlertType.ALARM, 10000);
            } else if (response.equals("-2")) {
                testCount++;
                if (testCount < attempt) {
                    if (!state.equals("login")) {
                        defriend(user, testCount, attempt);
                    }
                } else {
                    displayAlert(Locale.get("alert.error"), Locale.get("alert.ntwrkErrFrq"), Image.createImage("/x.png"), AlertType.CONFIRMATION, 10000);
                }
            } else {
                //contact = new Hashtable();
                displayAlert(Locale.get("alert.error"), user.substring(0, user.indexOf("\n")) + Locale.get("alert.defriendSuccess"), Image.createImage("/v.png"), AlertType.CONFIRMATION, 10000);
            }
        } catch (IOException ex) {
        }
    }

    private void makeAdmin(String[] data, int testCount, int attempt) {
        try {
            String response = SendToServer.makeAdmin(data[0], data[1]);
            if (response.equals("0")) {
                displayAlert(Locale.get("alert.error"), data[0] + Locale.get("alert.adminAlready"), Image.createImage("/x.png"), AlertType.ALARM, 10000);
            } else if (response.equals("-1")) {
                displayAlert(Locale.get("alert.error"), data[0] + Locale.get("alert.userNotMem"), Image.createImage("/x.png"), AlertType.ALARM, 10000);
            } else if (response.equals("-2")) {
                testCount++;
                if (testCount < attempt) {
                    if (!state.equals("login")) {
                        makeAdmin(data, testCount, attempt);
                    }
                } else {
                    displayAlert(Locale.get("alert.error"), Locale.get("alert.ntwrkErrFrq"), Image.createImage("/x.png"), AlertType.CONFIRMATION, 10000);
                }
            } else {
                displayAlert(Locale.get("alert.successT"), Locale.get("alert.success"), Image.createImage("/v.png"), AlertType.CONFIRMATION, 10000);
            }
        } catch (IOException ex) {
        }
    }

    private void addToGroup(String[] data, int testCount, int attempt) {
        try {
            String response = SendToServer.addToGroup(data[0], data[1]);
            if (response.equals("0")) {
                displayAlert(Locale.get("alert.error"), data[0] + Locale.get("alert.alreadyMem"), Image.createImage("/x.png"), AlertType.CONFIRMATION, 10000);
            } else if (response.equals("-2")) {
                testCount++;
                if (testCount < attempt) {
                    if (!state.equals("login")) {
                        addToGroup(data, testCount, attempt);
                    }
                } else {
                    displayAlert(Locale.get("alert.error"), Locale.get("alert.ntwrkErrFrq"), Image.createImage("/x.png"), AlertType.CONFIRMATION, 10000);
                }
            } else {
                displayAlert(Locale.get("alert.successT"), data[0] + Locale.get("alert.addedsucc"), Image.createImage("/v.png"), AlertType.CONFIRMATION, 10000);
                getDisplay().setCurrent(getScreen(new List(data[2], List.IMPLICIT), 20, ""));
            }
        } catch (IOException ex) {
        }
    }

    public String[] getPublicProfileSettings(int testCount, int attempt) {
        String[] data = new String[]{};
        try {
            String response = SendToServer.getPublicSettings();
            if (response.equals("-1")) {
                //an error occured
            } else if (response.equals("-2") || response.trim().equals("")) {
                testCount++;
                if (testCount < attempt) {
                    if (!state.equals("login")) {
                        getPublicProfileSettings(testCount, attempt);
                    }
                } else {
                    displayAlert(Locale.get("alert.error"), Locale.get("alert.ntwrkErrFrq"), Image.createImage("/x.png"), AlertType.CONFIRMATION, 10000);
                }
            } else {
                data = TextUtil.splitAndTrim(response, '~');
                //#style button
                StringItem save = new StringItem("", Locale.get("cmd.save"), StringItem.BUTTON);
                save.setDefaultCommand(CommandBuilder.getSaveCmd());
                flickrScreen.append(FramedForm.FRAME_BOTTOM, save);
            }
        } catch (IOException ex) {
        }
        return data;
    }

    private void sendContactToServer(int testCount, int attempt) {
        boolean success = false;
        Hashtable contact = (Hashtable) getContactMap(false);
        Enumeration elements = contact.keys();
        while (elements.hasMoreElements()) {
            String key = elements.nextElement().toString();
            Contacts cont = (Contacts) contact.get(key);
            String response = "";
            if (!cont.isOnGist() ) {
                response = SendToServer.sendContactToServer(key);
            }

            if (response.equals("-1") || response.equals("")) {
            } else if (response.equals("-2")) {
                if (testCount < attempt) {
                    if (!state.equals("login")) {
                        sendContactToServer(testCount, attempt);
                    }
                }
            } else if (response.equals("1")) {
                success = true;
                cont = (Contacts) contact.get(key);
                cont.setOnGistStatus("Y");
                contact.put(key, cont);
            } else if (response.equals("P")) {
                success = true;
                cont = (Contacts) contact.get(key);
                cont.setOnGistStatus("P");
                contact.put(key, cont);
            }
        }

        if (success) {
            saveToRms(contact, "phoneContact");
        }
    }

    private void getUserProfile(String user, int testCount, int attempt) {
        if (!user.equals(SendToServer.getUsername()) && !user.equals(SendToServer.getPhone()) || state.equals("groupmembers")) {
            if (!state.equals("groupmembers")) {
                state = "listFriends";
            }
        } else {
            state = "profile";
        }
        try {
            String response = SendToServer.getUserProfile(user);
            if (response.equals("0")) {
                //profile
            } else if (response.equals("-2") || response.trim().equals("")) {
                testCount++;
                if (testCount < attempt) {
                    profile.delete(FramedForm.FRAME_CENTER, 0);
                    //#style notif
                    profile.append(FramedForm.FRAME_CENTER, new StringItem("", testCount + Locale.get("alert.attempt")));
                    if (!state.equals("login") && (state.equals("listFriends") || state.equals("profile"))) {
                        getUserProfile(user, testCount, attempt);
                    }
                } else {
                    profile.delete(FramedForm.FRAME_CENTER, 0);
                    //#style notifR
                    profile.append(FramedForm.FRAME_CENTER, new StringItem("", Locale.get("alert.ntwrkErrFrq")));
                    displayAlert(Locale.get("alert.error"), Locale.get("alert.ntwrkErrFrq"), Image.createImage("/x.png"), AlertType.CONFIRMATION, 10000);
                }
            } else {
                if (!state.equals("profile") && !state.equals("listFriends") && !state.equals("groupmembers")) {
                    return;
                }
                executeOperation("getImage", new String[]{user}, 0, 0, 1);

                String profileinfo[] = TextUtil.splitAndTrim(response, '~');
                profile.setTitle(profile.getTitle() + ": " + profileinfo[0].substring(profileinfo[0].indexOf(":") + 1).trim());
                String[] gender = TextUtil.splitAndTrim(profileinfo[2], ':');
                profile.delete(FramedForm.FRAME_CENTER, 0);
                ImageItem imgItem;

                Image img;
                String gend;
                if (gender[1].trim().equals("M")) {
                    img = Image.createImage("/dummyimgmal.png");
                    gend = Locale.get("txt.genderM");
                } else {
                    img = Image.createImage("/dummyimgfem.png");
                    gend = Locale.get("txt.genderF");
                }

                imgItem = new ImageItem("", img, ImageItem.BUTTON, "");
                //#style myLabelItemsPic
                profile.append(FramedForm.FRAME_TOP, imgItem);

                profile.addCommand(CommandBuilder.getEnlargeImageCmd());

                if (state.equals("groupmembers")) {
                    if (((Groups) ((Hashtable) readRs("groups")).get(groupIds.elementAt(0).toString())).isAdmin()) {
                        //#style button
                        StringItem adm = new StringItem("", Locale.get("txt.makeAdmin"), StringItem.BUTTON);
                        adm.setDefaultCommand(CommandBuilder.getMakeAdminCmd());
                        profile.append(FramedForm.FRAME_BOTTOM, adm);
                    }
                }
                for (int i = 0; i < profileinfo.length; i++) {
                    String[] value = null;
                    value = TextUtil.splitAndTrim(profileinfo[i], ':');
                    if (i == 2) {
                        //#style myLabelItems
                        profile.append(FramedForm.FRAME_CENTER, new StringItem(value[0], gend, StringItem.BUTTON));
                    } else if (i == 3) {
                        if (state.equals("listFriends") || state.equals("groupmembers")) {
                            //#style myLabelItems
                            profile.append(FramedForm.FRAME_CENTER, new StringItem(value[0], replaceString(replaceString(replaceString(replaceString(value[1], "gd[str]", "|"), "gd[til]", "~"), "gd[att]", "@"), "gd[col]", ":"), StringItem.BUTTON));
                        } else {
                            //#style textFields
                            TextField tf = new TextField(value[0], replaceString(replaceString(replaceString(replaceString(value[1], "gd[str]", "|"), "gd[til]", "~"), "gd[att]", "@"), "gd[col]", ":"), 96, TextField.ANY);
                            UiAccess.setTextfieldHelp(tf, Locale.get("txt.statusMsg"));
                            tf.setDefaultCommand(CommandBuilder.getSaveCmd());
                            profile.append(FramedForm.FRAME_CENTER, tf);
                            profile.addCommand(CommandBuilder.getChangePictureCmd());
                        }
                    } else {
                        try {
                            //#style myLabelItems
                            profile.append(FramedForm.FRAME_CENTER, new StringItem(value[0], replaceString(replaceString(replaceString(replaceString(value[1], "gd[str]", "|"), "gd[til]", "~"), "gd[att]", "@"), "gd[col]", ":"), StringItem.BUTTON));
                        } catch (ArrayIndexOutOfBoundsException e) {
                            return;
                        }
                    }
                }
                profile.repaint();
            }
        } catch (IOException ex) {
        }
    }

    private boolean isInContact(Hashtable obj, String user) {
        boolean result = false;
        String palinfo[] = TextUtil.splitAndTrim(user, '~');
        Enumeration elements = obj.elements();
        Hashtable contact = (Hashtable) getContactMap(false);
        while (elements.hasMoreElements()) {
            if (((Contacts) elements.nextElement()).getUsername().equals(palinfo[0])) {
                if (palinfo[4].equals("Y")) {
                    contact.put(palinfo[0], new Contacts(palinfo[0], palinfo[1], palinfo[2], palinfo[3], "Y"));
                } else {
                    contact.put(palinfo[0], new Contacts(palinfo[0], palinfo[1], palinfo[2], palinfo[3], ""));
                }
                result = true;
                break;
            }
        }

        return result;
    }

    public void displayAlert(String title, String msg, Image img, AlertType msgType, int timeToDisplay) {

        //#style popupAlert
        Alert alert = new Alert(title, msg, img, msgType);

        if (!(timeToDisplay < 0)) {
            if (!msg.equals("") || msg != null) {
                alert.setTimeout(timeToDisplay);
            }
        }
        getDisplay().setCurrent(alert);
    }

    private void offlineMsgStore(String user) {
        Object obj = readRs("inbox");
        Vector vec;
        if (obj != null) {
            vec = (Vector) obj;
        } else {
            vec = new Vector();
        }
        if (!vec.contains(user)) {
            vec.addElement(user);
            saveToRms(vec, "inbox");
            setNewMsgStatus("/newMSG.png");
        }
    }

    private boolean readOfflineMsgFrom(String user) {
        boolean value = false;
        Object obj = readRs("inbox");
        Vector vec;
        if (obj != null) {
            vec = (Vector) obj;
        } else {
            vec = new Vector();
        }
        vec.trimToSize();
        if (!vec.isEmpty()) {
            if(vec.contains(user)){
                value = true;
            }
            vec.removeElement(user);
            if (vec.isEmpty()) {
                removeRs("inbox");
                //#if polish.ScreenInfo.enable
//#                 ScreenInfo.setImage(null);
                //#endif
            } else {
                saveToRms(vec, "inbox");
            }
        }
        return value;
    }

    private Vector getOflineMsgSenders(/*
             * Hashtable contact
             */) {
        Object obj = readRs("inbox");
        Vector vec;
        if (obj != null) {
            vec = (Vector) obj;
        } else {
            vec = new Vector();
        }
        return vec;
    }

    private void setNewMsgStatus(String url) {
        //#if polish.ScreenInfo.enable
//#     try {
//#         Image img = Image.createImage(url);
//#         ScreenInfo.setImage( img );
//#     } catch (IOException e) {
//# 
//#     }
        //#endif
    }

    public String replaceString(String str, String search, String ch) {
        String mystr = str;
        StringBuffer sb = new StringBuffer();
        int stop = mystr.indexOf(search);
        while (mystr.indexOf(search) >= 0) {
            try {
                sb.append(mystr.substring(0, stop));
                sb.append(ch);
                mystr = mystr.substring(stop + 7);
                stop = mystr.indexOf(search);
            } catch (StringIndexOutOfBoundsException e) {
            }
        }
        sb.append(mystr);
        return sb.toString();
    }

    public Object readRs(String name) {
        Object obj;
        String database = "gistdata";
        try {
            Object read = getStorage().read(database);
            Hashtable db;
            if (read == null) {
                db = new Hashtable();
            } else {
                db = (Hashtable) read;
            }
            obj = db.get(name);
            System.out.println(name +"is reading....");
        } catch (IOException ex) {
            obj = null;
        }
        return obj;
    }

    public RmsStorage getStorage() {
        RmsStorage storage = new RmsStorage();
        return storage;
    }

    public Object saveToRms(Object obj, String name) {
        Object read;
        String database = "gistdata";
        Hashtable db;
        try {
            read = getStorage().read(database);
            if (read == null) {
                db = new Hashtable();
            } else {
                db = (Hashtable) read;
            }
            db.put(name, obj);
        } catch (IOException ex) {
            db = new Hashtable();
            db.put(name, obj);
        }
        try {
            getStorage().save(db, database);
        } catch (IOException ex) {
        }
        return obj;
    }

    public boolean rsIsAvailable(String name) {
        return getStorage().exists(name);
    }

    public boolean rsIsAvailable(String name, String database) {
        Object read;
        Hashtable db;
        boolean d = true;
        try {
            read = getStorage().read(database);
            if (read == null) {
                db = new Hashtable();
            } else {
                db = (Hashtable) read;
            }
            if (!db.containsKey(name)) {
                d = false;
            }

        } catch (IOException ex) {
            d = false;
        }
        return d;
    }

    public void removeRs(String name) {
        Object obj;
        String database = "gistdata";
        Hashtable ht;
        try {
            obj = getStorage().read(database);
            if (obj != null) {
                ht = (Hashtable) obj;
                ht.remove(name);
            } else {
                ht = new Hashtable();
            }
        } catch (IOException ex) {
            ht = new Hashtable();
        }
        try {
            getStorage().save(ht, database);
        } catch (IOException ex) {
        }
    }
    public void removeRs(String main,String sub) {
        Object obj;
        String database = "gistdata";
        Hashtable ht;
        try {
            obj = getStorage().read(database);
            if (obj != null) {
                ht = (Hashtable) obj;
                Object o = ht.get(main);
                if(o != null){
                    Hashtable hist = (Hashtable) o;
                    hist.remove(sub);
                    saveToRms(hist, "history");
                }
                ht.remove(main);
            } else {
                ht = new Hashtable();
            }
        } catch (IOException ex) {
            ht = new Hashtable();
        }
        try {
            getStorage().save(ht, database);
        } catch (IOException ex) {
        }
    }
    public void commandAction(javax.microedition.lcdui.Command c, Displayable d) {
    }

    protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
    }

    private List getRootFiles() {
        //#style mainMenuScreen
        List rootFiles = new List("Files", List.IMPLICIT);
        Vector vec = FetchFiles.getRoot();
        //rootFiles.append(upper_dir,null);
        Enumeration elements = vec.elements();
        while (elements.hasMoreElements()) {
            String element = elements.nextElement().toString();
            try {
                FileConnection fc = (FileConnection) Connector.open("file:///" + element);
                if (fc.isDirectory()) {
                    //#style mainMenuItemAnimated
                    rootFiles.append(element, Image.createImage("/Folder.png"));
                } else {
                    //#style mainMenuItemAnimated
                    rootFiles.append(element, Image.createImage("/Pic.png"));
                }
            } catch (IOException ex) {
            }
        }
        rootFiles.addCommand(List.SELECT_COMMAND);
        rootFiles.addCommand(CommandBuilder.getBackCmd());
        rootFiles.addCommand(CommandBuilder.getExitCmd());
        rootFiles.setCommandListener(this);
        return rootFiles;
    }

    public Display getDisplay() {
        return Display.getDisplay(this);
    }

    public Object getContactMap(boolean fulCheck) {
        Object obj;
        if (!rsIsAvailable("phoneContact", "gistdata") && fulCheck) {
            PhoneContact pc = new PhoneContact();
            obj = pc.readContacts();
            if (((Hashtable) obj).isEmpty()) {
                displayAlert(Locale.get("alert.phonecontact"), Locale.get("txt.contactMissingPhone"), null, AlertType.ALARM, -1);
            } else {
                saveToRms(obj, "phoneContact");
            }
        } else {
            obj = readRs("phoneContact");
            if (obj == null) {
                obj = new Hashtable();
                displayAlert(Locale.get("alert.phonecontact") + " null", Locale.get("txt.contactMissingPhone"), null, AlertType.ALARM, -1);
            } else if (((Hashtable) obj).isEmpty()) {
                displayAlert(Locale.get("alert.phonecontact") + " empty", Locale.get("txt.contactMissingPhone"), null, AlertType.ALARM, -1);
            }
        }
        return obj;
    }

    private String getflckr() {
        String flkr = "";
        Object obj = readRs("flickr");
        Object obj2 = readRs(SendToServer.getUsername() + "bday");
        Hashtable flicker, bday;
        if (obj != null) {
            flicker = (Hashtable) obj;
        } else {
            flicker = new Hashtable();
        }

        if (obj2 != null) {
            bday = (Hashtable) obj2;
        } else {
            bday = new Hashtable();
        }
        if (flicker.containsKey(Locale.formatDate(System.currentTimeMillis(), "yyyy-MM-DD"))) {
            flkr += (String) flicker.get(Locale.formatDate(System.currentTimeMillis(), "yyyy-MM-DD")) + " ";
        }
        if (bday.containsKey(Locale.formatDate(System.currentTimeMillis(), "MM-DD"))) {
            flkr += Locale.get("flckr.append") + (String) bday.get(Locale.formatDate(System.currentTimeMillis(), "MM-DD")) + " ";
        }
        return flkr;
    }

    private void openingScreen() {
        try {
            getStorage().deleteAll();
        } catch (IOException ex) {
            System.out.println(ex.getMessage()+" when deleting all rms");
        }
        if (rsIsAvailable("gistdata")) {
            Object obj = readRs("userLogin");

            Hashtable ht;
            if (obj == null) {
                ht = new Hashtable();
            } else {
                ht = (Hashtable) obj;
            }

            if (!ht.isEmpty()) {
                SendToServer.setUsername(ht.get("username").toString());
                SendToServer.setPassword(ht.get("password").toString());
                SendToServer.setPhone(ht.get("phone").toString());
                SendToServer.setPhoneCode(ht.get("phonecode").toString());

                getDisplay().setCurrent(getScreen(new List("", List.IMPLICIT), 1, ""));

                executeOperation("fChat", new String[]{}, 1000, 0, 0);
                executeOperation("fGChat", new String[]{}, 2000, 0, 0);
                executeOperation("gflickr", new String[]{}, 3000, 0, 0);
                executeOperation("groupRooms", new String[]{SendToServer.getUsername()}, 10000, 0, 5);
                executeOperation("getUsersWithBDFor", new String[]{}, 60000, 0, 50);

                executeOperation("checkPals", new String[]{"1"}, 10000, 0, 0);
                executeOperation("sendContact", new String[]{}, 300000, 0, 5);

            } else {
                getDisplay().setCurrent(getScreen(new Form(""), 0, ""));
            }
        } else {
            getDisplay().setCurrent(getScreen(new Form(""), 0, ""));
        }
    }
}
