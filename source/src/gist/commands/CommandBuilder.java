package gist.commands;
import de.enough.polish.util.Locale;
import javax.microedition.lcdui.Command;


/**
 * <p>Defines all command needed for 9gist Mobile Application.</p>
 *
 * <p>Copyright Soladnet Software Corporation 2012</p>
 * @author Abdulrasheed, Soladnet Software Corp.
 */
public class CommandBuilder{
        /**
         *
         * <p>This method returns an instance of Command Class with string label "Select", commandType as Command.OK and priority 1 </p>
         * @author Robert Virkus, j2mepolish@enough.de
         */
	public static Command getCreateCmd()
	{
		return new Command(Locale.get("cmd.createGrp"), Command.OK, 4);
	}
        public static Command getCancelCmd(){
            return new Command(Locale.get("polish.command.cancel"),Command.CANCEL,1);
        }

        /**
         * <p>Accept Command with "Accept" as the label. The priority of this command is 1</p>
         *
         * <p>Copyright Soladnet Software Corp 2012</p>
         * @author Soladnet Soft. Corp, development@soladnet.com
         */
        public static Command getAcceptCmd(){
            return new Command(Locale.get("cmd.accept"), Command.SCREEN, 2);
        }
        public static Command getChangePictureCmd(){
            return new Command(Locale.get("cmd.chngImg"), Command.SCREEN, 2);
        }
        public static Command getEnlargeImageCmd(){
            return new Command(Locale.get("cmd.enlgImg"), Command.SCREEN, 2);
        }
        public static Command getSaveCmd(){
            return new Command(Locale.get("cmd.save"), Command.SCREEN, 2);
        }
        public static Command getAddMemberCmd(){
            return new Command(Locale.get("cmd.addFriend"), Command.SCREEN, 2);
        }
        public static Command getBarnMemberCmd(){
            return new Command(Locale.get("cmd.removeMem"), Command.SCREEN, 2);
        }
        
        public static Command getRemoveFriendCmd(){
            return new Command(Locale.get("cmd.defriend"), Command.SCREEN, 2);
        }
        public static Command getMakeAdminCmd(){
            return new Command(Locale.get("cmd.makeAdmin"), Command.SCREEN, 2);
        }
        public static Command getLeaveGroupCmd(){
            return new Command(Locale.get("cmd.leaveGroup"), Command.SCREEN, 2);
        }
        public static Command getViewProfile(){
            return new Command(Locale.get("cmd.viewProfile"), Command.SCREEN, 2);
        }
        public static Command getCreditTransfer(){
            return new Command(Locale.get("cmd.transferCrdt"), Command.SCREEN, 2);
        }
        public static Command getDeclineCmd(){
            return new Command(Locale.get("cmd.decline"), Command.SCREEN, 2);
        }
        public static Command getDeclineAndBlockCmd(){
            return new Command(Locale.get("cmd.declineBlock"), Command.SCREEN, 2);
        }
        public static Command getLogoutCmd(){
            return new Command(Locale.get("cmd.logout"),Command.OK,10);
        }
        public static Command getLoginCmd(){
            return new Command(Locale.get("cmd.login"),Command.OK,2);
        }
        public static Command getSendCmd(){
            return new Command(Locale.get("cmd.send"),Command.OK,1);
        }
	public static Command getExitCmd()
	{
		return new Command(Locale.get("cmd.exit"), Command.EXIT, 11);
	}

	public static Command getBackCmd()
	{
		return new Command(Locale.get("cmd.back"), Command.BACK, 1);
	}
        public static Command getClearHistoryCmd(){
            return new Command(Locale.get("cmd.clearHist"), Command.SCREEN, 2);
        }
}