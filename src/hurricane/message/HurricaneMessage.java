/**
 * 
 */
package hurricane.message;


/** Hurricane TextMessage must be the form of "[an alphabetic character]{space}[optional numeric number up to 8 digits]".
 * Below we listed preset message types.
 * @author Romancer
 *
 */
public class HurricaneMessage {
	
	public static final String TEXT_DELIM = " ";
	
	public static final String SOCK_USER = "u";
	public static final String SOCK_HURRICANE = "h";
	public static final String SOCK_GO = "g";
	public static final String SOCK_NO = "n";
	public static final String SOCK_NUMERIC = "x";
	
//	private static String[] SOCKS_ARRAY = {SOCK_USER, SOCK_HURRICANE, SOCK_GO, SOCK_NO, SOCK_NUMERIC};

	public static final String SCREEN_HURRICANE = "Hurricane";
	public static final String SCREEN_GO = "Go";
	public static final String SCREEN_NO = "No";

	private static final String[] ALIASES_USER = {"user", "target", "tar", "destination", "dest", "to", "sendto", "u"};
	private static final String[] ALIASES_HURRICANE = {"hurricane","hurry","hurri","hurr","hur","hu","h"};
	private static final String[] ALIASES_GO = {"go","goes","going","goin","gonna","gone","g"};
	private static final String[] ALIASES_NO = {"no","nop","nope","nay","na","nah","neg","negative","n"};
	
	private static String[][] ALIASES_ARRAY = {ALIASES_HURRICANE, ALIASES_GO, ALIASES_NO};
		
	public static String getSockMessage(String word) {
		String inputLower = word.toLowerCase();
		for (String[] ALIASES : ALIASES_ARRAY) {
			for (String alias : ALIASES) {
				if (inputLower.equals(alias)) {
					if (ALIASES == ALIASES_USER) {
						return SOCK_USER + TEXT_DELIM;
					}
					else if (ALIASES == ALIASES_HURRICANE) {
						return SOCK_HURRICANE + TEXT_DELIM;
					}
					else if (ALIASES == ALIASES_GO) {
						return SOCK_GO + TEXT_DELIM;
					}
					else if (ALIASES == ALIASES_NO) {
						return SOCK_NO + TEXT_DELIM;
					}
				}
			}
		}
		return SOCK_NUMERIC + TEXT_DELIM;
	}

	/**Returns translated screen message associated to the specific method Text Message.
	 * @param textMessageNotUNotNumber
	 * @return
	 */
	public static String getScreenMessage(String textMessageNotUNotNumber) {
		if (textMessageNotUNotNumber.equals(SOCK_GO)) {
			return SCREEN_GO + TEXT_DELIM;
		}
		else if (textMessageNotUNotNumber.equals(SOCK_HURRICANE)) {
			return SCREEN_HURRICANE + TEXT_DELIM;
		}
		else if (textMessageNotUNotNumber.equals(SOCK_NO)) {
			return SCREEN_NO + TEXT_DELIM;
		}
		else if (textMessageNotUNotNumber.equals(SOCK_NUMERIC)) {
			return "";
		}
		return "";
	}

}
