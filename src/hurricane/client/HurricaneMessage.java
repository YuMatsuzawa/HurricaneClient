/**
 * 
 */
package hurricane.client;


/** Hurricane TextMessage must be the form of "<an alphabetic character><space><optional numeric number up to 8 digits>".
 * Below we listed preset message types.
 * @author Romancer
 *
 */
public class HurricaneMessage {
	
	protected static final String SOCK_USER = "u ";
	protected static final String SOCK_HURRICANE = "h ";
	protected static final String SOCK_GO = "g ";
	protected static final String SOCK_NO = "n ";
	protected static final String SOCK_NUMERIC = "x ";

	protected static final String SCREEN_HURRICANE = "Hurricane";
	protected static final String SCREEN_GO = "Go";
	protected static final String SCREEN_NO = "No";

	private static final String[] ALIASES_HURRICANE = {"hurricane","hurry","hurri","hurr","hur","hu","h"};
	private static final String[] ALIASES_GO = {"go","goes","going","goin","gonna","gone","g"};
	private static final String[] ALIASES_NO = {"no","nop","nope","nay","na","nah","neg","negative","n"};
	
	private static String[][] ALIASES_ARRAY = {ALIASES_HURRICANE, ALIASES_GO, ALIASES_NO};
		
	protected static String getSockMessage(String word) {
		String inputLower = word.toLowerCase();
		for (String[] ALIASES : ALIASES_ARRAY) {
			for (String alias : ALIASES) {
				if (inputLower.equals(alias)) {
					if (ALIASES == ALIASES_HURRICANE) {
						return SOCK_HURRICANE;
					}
					else if (ALIASES == ALIASES_GO) {
						return SOCK_GO;
					}
					else if (ALIASES == ALIASES_NO) {
						return SOCK_NO;
					}
				}
			}
		}
		return SOCK_NUMERIC;
	}

}
