/**
 * 
 */
package hurricane.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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
	private String nickname;
	private String sendTo;
	
	public HurricaneClientSocket(String nickname) {
		this.nickname = nickname;
	}

	/**Handler of onConnect event. Run the first input waiting loop and accept a stdin input as destination username.<br>
	 * Then send it to the server by "u" method. 
	 * @param session
	 */
	@OnWebSocketConnect
	public void onConnect(Session session) {
		System.out.printf("Connected: %s\n", session);
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
				fut.get(100, TimeUnit.MILLISECONDS);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**Handler of onClose event. This event could be thrown in one of two ways: closed by server, or closed by client.<br>
	 * If the session was closed by the server, there must be StatusCode or Reason got passed, read them and react appropriately.<br>
	 * If closed by the client, same treatment, but you may prompt the socket or the client as you wish (no need to retry etc.)<br>
	 * @param session
	 */
	@OnWebSocketClose
	public void onClose(Session session, int statusCode, String reason) {
		switch(statusCode) {
		case StatusCode.NORMAL :
			//to be implemented
			return;
		case StatusCode.SERVER_ERROR :
			//to be implemented
			return;
		default :
			//to be implemented
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
		System.out.println();
	}
	
	/**Convert themantic input to Hurricane TextMessage.<br>
	 * @param input
	 * @return
	 */
	private String inputToTextMessage(String input) {
		String ret = new String();
		String text = "", optNum = "";
		String words[] = input.split("\\s");
		if (words.length > 1) {
			for (String word : words) {
				if (word.matches("\\d+")) {
					optNum = word.substring(0, 8);
				} else {
					text = HurricaneMessage.getSockMessage(word);
				}
			}
		}
		ret = text + optNum;
		return ret;
	}

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
