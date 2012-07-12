/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gist.connector;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

/**
 *
 * @author ACER USER
 */
public class SendToServer {

//    public static String URL = "http://127.0.0.1/server/ver1.1/post_sender.php";
    public static String URL = "http://9gistmobile.gossout.com/ver1.1/post_sender.php";
    //public static String URL = "http://gistchat.9gist.com/post_sender.php";
    private static String USERNAME = "";
    private static String PASSWORD = "";
    private static String PHONE_CODE = "";
    private static String PHONE = "";
    
    public static String getUsername() {
        return USERNAME;
    }

    public static String getPassword() {
        return PASSWORD;
    }
    public static String getPhoneCode(){
        return PHONE_CODE;
    }
    public static void setPhoneCode(String phone){
        PHONE_CODE = phone;
    }
    public static String getPhone(){
        return PHONE;
    }
    public static void setPhone(String phone){
        PHONE = phone;
    }
    public static void setUsername(String x) {
        USERNAME = x;
    }

    public static void setPassword(String x) {
        PASSWORD = x;
    }

    public static String getUrl() {
        return URL;
    }

    public static String sendContactToServer(String phone) {
        String url = getUrl();
        String usermsg = "at=bd&bw=" + getUsername() + "&bn=" + phone;
        return OpenConnection(url, usermsg);
    }

    public static String sflkr(String choice) {
        String url = getUrl();
        String usermsg = "at=br&bw=" + getUsername() + "&aj=" + choice;
        return OpenConnection(url, usermsg);
    }

    public static String changePassword(String newP) {
        String url = getUrl();
        String usermsg = "at=ak&bw=" + getUsername() + "&bl=" + newP;
        return OpenConnection(url, usermsg);
    }

    public static String removeMember(String user, String grpid) {
        String url = getUrl();
        String usermsg = "at=bo&al=" + getUsername() + "&bw=" + user + "&ba=" + grpid;
        return OpenConnection(url, usermsg);
    }

    public static String defriend(String user) {
        String url = getUrl();
        String usermsg = "at=an&bw=" + user + "&al=" + getUsername();
        return OpenConnection(url, usermsg);
    }

    public static String makeAdmin(String user, String grpid) {
        String url = getUrl();
        String usermsg = "at=bi&bw=" + user + "&ba=" + grpid;
        return OpenConnection(url, usermsg);
    }

    public static String leaveGroup(String grpid) {
        String url = getUrl();
        String usermsg = "at=bg&bw=" + getUsername() + "&ba=" + grpid;
        return OpenConnection(url, usermsg);
    }

    public static String sPrivacy(String choice) {
        String url = getUrl();
        String usermsg = "at=bs&bw=" + getUsername() + "&aj=" + choice;
        return OpenConnection(url, usermsg);
    }

    public static String getPublicSettings() {
        String url = getUrl();
        String usermsg = "at=bm&bw=" + getUsername();
        return OpenConnection(url, usermsg);
    }

    public static String createGroupChat(String grpName) {
        String url = getUrl();
        String usermsg = "at=ai&bw=" + getUsername() + "&az=" + grpName;
        return OpenConnection(url, usermsg);
    }

    public static String getGroupRooms(String user) {
        String url = getUrl();
        String usermsg = "at=bc&bw=" + user;
        return OpenConnection(url, usermsg);
    }

    public static String getGroupMembers(String id) {
        String url = getUrl();
        String usermsg = "at=bb&bw=" + getUsername() + "&id=" + id;
        return OpenConnection(url, usermsg);
    }

    public static String addToGroup(String user, String grpid) {
        String url = getUrl();
        String usermsg = "at=ab&bw=" + user + "&ba=" + grpid;
        return OpenConnection(url, usermsg);
    }

    public static String transferCredit(String credit, String beneficiary) {
        String url = getUrl();
        String usermsg = "at=bv&bw=" + getUsername() + "&ag=" + beneficiary + "&ad=" + credit;
        return OpenConnection(url, usermsg);
    }

    public static String sStatus(String user, String status) {
        String url = getUrl();
        String usermsg = "at=bt&bw=" + user + "&bu=" + status.trim();
        return OpenConnection(url, usermsg);
    }

    public static String sendChat(String receiver, String msg) {
        String url = getUrl();
        String usermsg = "at=bp&bw=" + getUsername() + "&bn=" + receiver + "&bj=" + msg;
        return OpenConnection(url, usermsg);
    }

    public static String sendGroupChat(String group, String msg) {
        String url = getUrl();
        String usermsg = "at=au&bw=" + getUsername() + "&az=" + group + "&bj=" + msg;
        return OpenConnection(url, usermsg);
    }

    public static String flkr() {
        String url = getUrl();
        String usermsg = "at=ar&bw=" + getUsername();
        return OpenConnection(url, usermsg);
    }

    public static String gflkr(String ver) {
        String url = getUrl();
        String usermsg = "at=aw&bw=" + getUsername() + "&bx=" + ver;
        return OpenConnection(url, usermsg);
    }

    public static String getBirthDay() {
        String url = getUrl();
        String usermsg = "at=af&bw=" + getUsername();
        return OpenConnection(url, usermsg);
    }

    public static String getUserProfile(String fuser) {
        String url = getUrl();
        String usermsg;
        if (fuser.equalsIgnoreCase(getUsername())) {
            usermsg = "at=ax&bw=" + fuser + "&ac=";
        } else {
            usermsg = "at=ax&bw=" + fuser + "&al=" + getUsername();
        }
        return OpenConnection(url, usermsg);
    }

    public static String palReq(String pal, String type) {
        String url = getUrl();
        String usermsg = "at=" + type + "&bw=" + getUsername() + "&bk=" + pal;
        return OpenConnection(url, usermsg);
    }

    public static String fBack(String id) {
        String url = getUrl();
        String usermsg = "at=ao&bw=" + getUsername() + "&id=" + id;
        return OpenConnection(url, usermsg);
    }

    public static String fChat() {
        String url = getUrl();
        String usermsg = "at=ap&bw=" + getPhone();
        return OpenConnection(url, usermsg);
    }

    public static String fGroupChat() {
        String url = getUrl();
        String usermsg = "at=aq&bw=" + getUsername();
        return OpenConnection(url, usermsg);
    }

    public static String sendImageToServer(String imageString, String format) {
        String url = getUrl();
        String usermsg = "at=bf&bw=" + getUsername() + "&be=" + imageString + "&as=" + format;
        return OpenConnection(url, usermsg);
    }

    public static String getPals(String first) {
        String url = getUrl();
        String usermsg;
        if (first.equals("1")) {
            usermsg = "at=av&bw=" + getUsername() + "ft=";
        } else {
            usermsg = "at=av&bw=" + getUsername();
        }
        return OpenConnection(url, usermsg);
    }

    public static String login(String ver) {
        String url = getUrl();
        String usermsg = "at=ae&bw=" + getUsername() + "&bl=" + getPassword() + "&bx=" + ver;;
        return OpenConnection(url, usermsg);
    }

    public static String logout() {
        String url = getUrl();
        String usermsg = "at=bh&bw=" + getUsername();
        return OpenConnection(url, usermsg);
    }

    private static String EncodeURL(String URL) {
        StringBuffer urlOK = new StringBuffer();
        for (int i = 0; i < URL.length(); i++) {
            char ch = URL.charAt(i);
            switch (ch) {
                case '<':
                    urlOK.append("%3C");
                    break;
                case '>':
                    urlOK.append("%3E");
                    break;
                case '/':
                    urlOK.append("%2F");
                    break;
                case ' ':
                    urlOK.append("%20");
                    break;
                case ':':
                    urlOK.append("%3A");
                    break;
                case '-':
                    urlOK.append("%2D");
                    break;
                default:
                    urlOK.append(ch);
                    break;
            }
        }
        return urlOK.toString();
    }

    private static String replace(String source, String oldChar, String dest) {
        String ret = "";
        for (int i = 0; i < source.length(); i++) {
            if (!source.substring(i, i + 1).equals(oldChar)) {
                ret += source.charAt(i);
            } else {
                ret += dest;
            }

//            if (source.charAt(i) != oldChar)
//                ret += source.charAt(i);
//            else
//                ret += dest;
        }
        return ret;
    }

    private static String OpenConnection(String url, String msg) {
        HttpConnection http = null;
        try {
            byte[] data = null;
            InputStream istrm = null;
            String response;
            http = (HttpConnection) Connector.open(url);
            http.setRequestMethod(HttpConnection.POST);

            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            http.setRequestProperty("Content-length", String.valueOf(msg.getBytes().length));

            OutputStream out = http.openOutputStream();
            out.write(msg.getBytes());
            out.flush();
            if (http.getResponseCode() == HttpConnection.HTTP_OK) {
                int len = (int) http.getLength();
                istrm = http.openInputStream();
                if (istrm == null) {
                    System.out.println("Cannot open stream - aborting " + "Cannot open HTTP InputStream, aborting");
                }
                if (len != -1) {
                    data = new byte[len];
                    int bytesRead = istrm.read(data);
                    System.out.println("Read " + bytesRead + " bytes");
                } else {
                    ByteArrayOutputStream bo = new ByteArrayOutputStream();
                    int ch;
                    int count = 0;

                    while ((ch = istrm.read()) != -1) {
                        bo.write(ch);
                        count++;
                    }
                    data = bo.toByteArray();
                    bo.close();
                    System.out.println("Read " + count + " bytes");
                }
                response = new String(data);
            } else {
                response = "-2";
            }
            istrm.close();
            out.close();
            http.close();
            
            System.out.println(response+" is the response frm server");
            return response;
        } catch (IOException ex) {
            try {
                http.close();
            } catch (IOException ex1) {
                ex1.printStackTrace();
            }
            System.out.println(ex.getMessage() + "<<<<<<<<<<<<<<<<<<<<<<");
            return "-2";
        }
    }
}
