/**
 * 
 */
package hurricane.client;

import java.net.URI;
import java.util.concurrent.CountDownLatch;

import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

/**
 * @author Romancer
 *
 */
public class HurricaneCUIClient {
	public static final int PORT = 80;
	private static CountDownLatch closeLatch;
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String dest = new String(), nickname = new String();
		closeLatch = new CountDownLatch(1);
		
		if(args.length < 2) {
			System.out.println("Usage: <server host> <nickname>");
			System.exit(1);
		} else {
			dest = "ws://" + args[0] + ":" + PORT;
			nickname = args[1];
		}
		
		WebSocketClient client = new WebSocketClient();
		HurricaneClientSocket socket = new HurricaneClientSocket(nickname,closeLatch);
		try {
			client.start();
			URI hurricaneUri = new URI(dest);
			ClientUpgradeRequest request = new ClientUpgradeRequest();
			request.setHeader("user", nickname);							//sending user nickname <<here>>
			client.connect(socket, hurricaneUri, request);
			System.out.printf("Connecting to %s\n", hurricaneUri);
			closeLatch.await();
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
}
