/**
 * 
 */
package hurricane.client;

import java.net.URI;

import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

/**
 * @author Romancer
 *
 */
public class HurricaneCUIClient {
	public static final int PORT = 80;
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String dest = new String(), nickname = new String();
		if(args.length < 2) {
			System.out.println("Usage: <server host> <nickname>");
		} else {
			dest = "ws://" + args[0];
			nickname = args[1];
		}
		
		WebSocketClient client = new WebSocketClient();
		HurricaneClientSocket socket = new HurricaneClientSocket(nickname);
		try {
			client.start();
			URI hurricaneUri = new URI(dest);
			ClientUpgradeRequest request = new ClientUpgradeRequest();
			client.connect(socket, hurricaneUri, request);
			System.out.printf("Connecting to %s\n", hurricaneUri);
			
			
			
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			try {
				client.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	public void onWebSocketConnect() {
		
	}
}
