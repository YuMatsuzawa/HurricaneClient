/**
 * 
 */
package hurricane.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

/**
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
//			fut = this.session.getRemote().sendStringByFuture(HurricaneMessage.SOCK_USER + this.nickname);
//			fut.get(2, TimeUnit.SECONDS);
//			if (!fut.isDone()) {
//				if (this.session.isOpen()) this.session.close(StatusCode.SERVER_ERROR, "Server does not respond.");
//			}
//			else {
//				//successfully sent target name
//				
//				//sending "Hurricane 99999999" to server as an example.
//				fut = this.session.getRemote().sendStringByFuture(this.inputToTextMessage("Hurricane 99999999"));
//				fut.get(2, TimeUnit.SECONDS);
//			
//				this.session.close(StatusCode.NORMAL, "Done.");
//			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
//			try {
//				if (this.session.isOpen()) this.session.close(StatusCode.SHUTDOWN, "Client shutdown.");
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
		}
	}
	
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
