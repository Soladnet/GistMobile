/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gist.connector;

import java.io.IOException;
import javax.microedition.io.Connector;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.TextMessage;

/** Sends an SMS message */
public class SendSMS implements Runnable {
  private String smsReceiverPort;
  private String message;
  private String phoneNumber;

  public SendSMS(String smsReceiverPort) {
    this.smsReceiverPort = smsReceiverPort;
  }

  public void run() {
    StringBuffer addr = new StringBuffer(20);
    addr.append("sms://+");
//    if (phoneNumber.length() == 11)
//      addr.append("86");//  china

    addr.append(phoneNumber);
    // String address = "sms://+8613641301055";
    String address = addr.toString().concat(":5000");

    MessageConnection smsconn = null;
    try {
      // Open the message connection.
      smsconn = (MessageConnection) Connector.open(address);
      // Create the message.
      TextMessage txtmessage = (TextMessage) smsconn
          .newMessage(MessageConnection.TEXT_MESSAGE);
      txtmessage.setAddress(address);// !!
      txtmessage.setPayloadText(message);
      smsconn.send(txtmessage);
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (smsconn != null) {
      try {
        smsconn.close();
      } catch (IOException ioe) {
        ioe.printStackTrace();
      }
    }
  }

  public void send(String message, String phoneNumber) {
    this.message = message;
    this.phoneNumber = phoneNumber;
    Thread t = new Thread(this);
    t.start();
  }

}

