/**
 * 
 */
package hurricane.client;

import hurricane.message.HurricaneMessage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

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

	private Session session;
	private CountDownLatch closeLatch;
	private String nickname;
	private String sendTo;
	
	public HurricaneClientSocket(String nickname, CountDownLatch closeLatch) {
		this.nickname = nickname;
		this.closeLatch = closeLatch;
	}

	/**Handler of onConnect event. Run the first input waiting loop and accept a stdin input as destination username.<br>
	 * Then send it to the server by "u" method. 
	 * @param session
	 */
	@OnWebSocketConnect
	public void onConnect(Session session) {
		System.out.printf("Connected: %s\n", session.getRemoteAddress().getHostString());
		this.session = session;
		Future<Void> fut = null;
		BufferedReader br = null;
		try {
			//sending target name.
			br = new BufferedReader(new InputStreamReader(System.in));
			System.out.print("Send to: ");
			String stdin;
			while((stdin = br.readLine()) != null) {
				fut = this.session.getRemote().sendStringByFuture(HurricaneMessage.SOCK_USER + stdin);
//				fut.get(100, TimeUnit.MILLISECONDS);
			}
		} catch (Exception e) {
			e.printStackTrace();
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
			this.closeLatch.countDown();
			return;
		case StatusCode.SERVER_ERROR :
			//to be implemented
			this.closeLatch.countDown();
			return;
		default :
			//to be implemented
			this.closeLatch.countDown();
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
		
	}
	
	/**Convert themantic input to Hurricane Text Message.<br>
	 * If the input words do not matches any of available aliases of Hurricane methods, it always returns "x ".<br>
	 * Be careful that Hurricane Server only translates, and react to, "u " method. Any other message will be simply passed to target user's socket, or passed to Push Notification service.<br>
	 * This means, practically, message translation ({@link #textMessageToOutput(String)}) only needs to be defined on the client.<br>
	 * (Or Logging server, if the server decided to log hurricane transactions in themantic message.
	 * @param input
	 * @return method-indicating String.<br>
	 * <li>Special String "x " means the message only contains numeric text, or, contains nothing (empty String).
	 * "x " won't be translated to themantic word as in case of, for example, "g " to "Go".
	 * <li>Another special String is "u " which is used for destination user declaration. Usage would be "u [username]".
	 * When sent TO a SERVER, the server searches its database for the specified user. If found, it echoes the same message TO THE CLIENT.
	 * If not found, the server returns "u 404" which indicates "User not found" obviously.
	 * <li>The other Strings can be translated to themantic words by {@link #textMessageToOutput(String)} method.
	 * <li>Special Strings may be implemented in future to expand the service.
	 */
	@SuppressWarnings("unused")
	private String inputToTextMessage(String input) {
		String ret = new String();
		String text = "", optNum = "";
		String words[] = input.split("\\s");
		if (words.length > 1) {
			for (String word : words) {
				if (word.matches("\\d+")) { //
					optNum = word.substring(0, 8);
				} else {
					text = HurricaneMessage.getSockMessage(word);
				}
			}
		} else {
			for (String word : words) {
				text = HurricaneMessage.getSockMessage(word);
			}
		}
		ret = text + optNum;
		return ret;
	}

	/**Translator of HurricaneMessage to themantic words. Should be called when the Message is not "u " method.<br>
	 * "u " method should be handled separately.
	 * @param textMessage
	 * @return
	 */
	private String textMessageToOutput(String textMessage) {
		String ret = new String();
		String text = "", optNum = "";
		String words[] = textMessage.split("\\s");
		if (words.length != 2) {
			//TODO textMessage parsing
		}
		return ret;
	}
}
