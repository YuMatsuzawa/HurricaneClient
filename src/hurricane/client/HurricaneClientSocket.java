/**
 * 
 */
package hurricane.client;

import hurricane.message.HurricaneMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

/**WebSocket object on client side. Defined by annotations.
 * @author Romancer
 *
 */
@WebSocket(maxTextMessageSize = 128)
public class HurricaneClientSocket {

	private static final String SCREEN_DELIM = ": ";
	
	private Session session;
	private CountDownLatch closeLatch;
	protected String nickname;
	private String sendTo;
	
	private BufferedReader globalReader = null;

	private boolean incoming = false;
	
	public HurricaneClientSocket(String nickname, CountDownLatch closeLatch) {
		this.nickname = nickname;
		this.closeLatch = closeLatch;
		this.globalReader = new BufferedReader(new InputStreamReader(System.in));
	}

	/**Handler of onConnect event. Run the first input waiting loop and accept a stdin input as destination username.<br>
	 * Then send it to the server by "u" method. 
	 * @param session
	 */
	@OnWebSocketConnect
	public void onConnect(Session session) {
		System.out.printf("Connected: %s\n", session.getRemoteAddress().getHostString());
		this.session = session;
		this.sendToPrompt();
		
//		Future<Void> fut = null;
//		BufferedReader br = null;
//		try {
//			//sending target name.
//			br = new BufferedReader(new InputStreamReader(System.in));
//			System.out.print("Send to: ");
//			String stdin;
//			while((stdin = br.readLine()) != null) {
//				fut = this.session.getRemote().sendStringByFuture(HurricaneMessage.SOCK_USER + stdin);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				br.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
	}
	
	/**Event loop which waits for input indicating destination username.<br>
	 * Designed to be called in the onConnect event handler, or in the case of destination user not found.<br>
	 * 
	 */
	private void sendToPrompt() {
		Future<Void> fut = null;
//		BufferedReader br = null;
		try {
			//sending target name.
//			br = new BufferedReader(new InputStreamReader(System.in));
			System.out.print("Send to: ");
			String stdin;
//			while((stdin = br.readLine()) != null) {
			while (true) {
//				stdin = br.readLine();
				stdin = this.globalReader.readLine();
				fut = this.session.getRemote().sendStringByFuture(HurricaneMessage.SOCK_USER
						+ HurricaneMessage.TEXT_DELIM + stdin);
				if (fut.isDone()) break;
			}
//			}
		} catch (IOException e) {
//			System.err.println(e.toString());
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
//			try {
//				br.close();
//			} catch (IOException e) {
//				System.err.println(e.toString());
////				e.printStackTrace();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
		}
	}
	
	/**Handler of onClose event. This event could be thrown in one of two ways: closed by server, or closed by client.<br>
	 * If the session was closed by the server, there must be StatusCode or Reason got passed, read them and react appropriately.<br>
	 * If closed by the client, same treatment, but you may close the socket or the client as you wish (no need to retry etc.)<br>
	 * @param session
	 */
	@OnWebSocketClose
	public void onClose(Session session, int statusCode, String reason) {
		switch(statusCode) {
		case StatusCode.NORMAL :
			//to be implemented
			this.session = null;
			System.out.printf("Connection closed. [%d]", statusCode);
			this.closeLatch.countDown();
//			try {
//				this.globalReader.close();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
			return;
		case StatusCode.SERVER_ERROR :
			//to be implemented
			this.session = null;
			System.out.printf("Connection closed. [%d]", statusCode);
			this.closeLatch.countDown();
//			try {
//				this.globalReader.close();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
			return;
		default :
			//to be implemented
			this.session = null;
			System.out.printf("Connection closed. [%d]", statusCode);
			this.closeLatch.countDown();
//			try {
//				this.globalReader.close();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
			return;
		}
	}
	
	/**Primary text transaction should be defined here.<br>
	 * Hurricane transactions are done by "methods", which are defined in HurricaneMessage.
	 * @param session
	 * @param text
	 */
	@OnWebSocketMessage
	public void onMessage(Session session, String text) {
		this.assortTextMessage(text);
	}
	
	/**Convert themantic input to Hurricane Text Message.<br>
	 * If the input words do not matches any of available aliases of Hurricane methods, it always returns "x ".<br>
	 * Be careful that Hurricane Server only translates, and react to, "u " method. Any other message will be simply passed to target user's socket, or passed to Push Notification service.<br>
	 * This means, practically, message translation ({@link #textMessageToOutput(String)}) only needs to be defined on the client.<br>
	 * (Or Logging server, if the server decided to log hurricane transactions in themantic message.)
	 * @param input
	 * @return method-indicating String.<br>
	 * <li>Special String "x " means the message only contains numeric text, or, contains nothing (empty String).
	 * "x " won't be translated to themantic word as in case of, for example, "g " to "Go".
	 * <li>Another special String is "u " which is used for destination user declaration. Usage would be "u [username]".
	 * When sent TO a SERVER, the server searches its database for the specified user. If found, it echoes the same message TO THE CLIENT.
	 * "u [username] [optNum]" form is also acceptable for future use. Basic server ignores optNum.
	 * If not found, the server returns "u 404" which indicates "User not found" obviously.
	 * <li>The other Strings can be translated to themantic words by {@link #textMessageToOutput(String)} method.
	 * <li>Special Strings may be implemented in future to expand the service.
	 */
	private String inputToTextMessage(String input) {
		String ret = new String();
		String text = "", optNum = "", sendTo = "";
		boolean isU = false;
		String words[] = input.split("\\s");
		for (String word : words) {
			// determine "u" method earlier
			if (HurricaneMessage.getSockMessage(word).equals(HurricaneMessage.SOCK_USER)) {
				isU = true;
			}
		}
		for (String word : words) {
			if (word.matches("\\d+")) { 							// If the word only contains number, they are trimmed into 8-digit number then attached.
				optNum = word;
				if (optNum.length() > 8) optNum = optNum.substring(0, 8);
			} else {
				String tmpText = HurricaneMessage.getSockMessage(word);
				if (isU && !tmpText.equals(HurricaneMessage.SOCK_USER)) {		// If "u" method already declared, unparsable input will be considered as username.
					sendTo = word;
				} else {
					text = tmpText;	// Get sockMessage by parsing a word into a method.
				}
			}
		}
		ret = text + sendTo + optNum;
		return ret;
	}

	/**Assort Hurricane Text Message into specials ("u") and normals. Process accordingly.<br>
	 * This private method is designed to be called in the onMessage event handler method.<br>
	 * @param text
	 */
	private void assortTextMessage(String text) {
		String[] splitText = text.split("\\s");
		if (splitText[0].equals(HurricaneMessage.SOCK_ACK)) {
			this.messagePrompt();
		} else if (splitText[0].equals(HurricaneMessage.SOCK_USER)) {
			this.setSendTo(splitText);
		} else {
			System.out.printf("[%s]%s%s\n", this.sendTo, SCREEN_DELIM, this.textMessageToOutput(splitText));
			this.messagePrompt();
		}
	}
	
	/**Set a target user of this Hurricane connection.<br>
	 * "u" method message should be in the form of "u [username] [optnum]". Thus, when split, index of 0 is always "u",
	 * 1 is username and 2 would be option numbers.<br>
	 * If optnum == 404, which means there is no such user with the specified username exists.
	 * @param splitText
	 */
	private void setSendTo(String[] splitTextMessageU) {
		if (splitTextMessageU.length > 2) {
			String optNum = splitTextMessageU[2];
			if (optNum.equals(String.valueOf(HttpStatus.NOT_FOUND_404))) {
				System.out.printf("User %s does not exist.\n", splitTextMessageU[1]);
				this.sendToPrompt();
			} else {
				// Ignore other optNum.
				this.sendTo = splitTextMessageU[1];
				System.out.printf("Hurricane with %s!\n", this.sendTo);
				this.messagePrompt();
			}
		} else {
			this.sendTo = splitTextMessageU[1];
			System.out.printf("Hurricane with %s!\n", this.sendTo);
			this.messagePrompt();
		}
	}

	/**Event loop which waits for input containing themantic messages.<br>
	 * Designed to be called when a destination user successfully established.<br>
	 * This loop won't break until onClose event triggered.
	 */
	private void messagePrompt() {
		Future<Void> fut = null;
//		BufferedReader br = null;
		try {
//			br = new BufferedReader(new InputStreamReader(System.in));
			String stdin;
//			while (this.closeLatch.getCount() > 0) { // message loop. never breaks.
//			while (!incoming) {
//				while((stdin = br.readLine()) != null) {
//				stdin = br.readLine();
				if ((stdin = this.globalReader.readLine()) != null && !stdin.isEmpty()) {
					fut = this.session.getRemote().sendStringByFuture(this.inputToTextMessage(stdin));
				}
//				}
//				if (fut.isCancelled()) System.err.println("Could not send a message.");
//			}
		} catch (IOException e) {
			System.err.println(e.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
//			try {
//				br.close();
//			} catch (IOException e) {
//				System.err.println(e.toString());
////				e.printStackTrace();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
		}
		
	}

	/**Translator of HurricaneMessage to themantic words. Should be called when the Message is not "u " method.<br>
	 * "u " method should be handled separately.
	 * @param textMessage
	 * @return
	 */
	private String textMessageToOutput(String[] splitTextMessageNotU) {
		String ret = new String();
		for (int i = 0; i < splitTextMessageNotU.length; i++) {
			if (splitTextMessageNotU[i].matches("\\d+")) {
				ret += splitTextMessageNotU[i];
			} else {
				ret += HurricaneMessage.getScreenMessage(splitTextMessageNotU[i]);	// Only non-numeric texts are passed 
			}
		}
		return ret;
	}
}
