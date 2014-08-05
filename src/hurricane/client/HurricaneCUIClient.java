/**
 * 
 */
package hurricane.client;

import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

/**
 * @author Romancer
 *
 */
public class HurricaneCUIClient {
	public static final int PORT = 8080;
	public static final String END_POINT = "/hurricane";
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
			dest = "ws://" + args[0] + ":" + PORT + END_POINT;
			nickname = args[1];
		}
		
		WebSocketClient client = new WebSocketClient();
		HurricaneClientSocket socket = new HurricaneClientSocket(nickname,closeLatch);
		try {
			client.start();
			URI hurricaneUri = new URI(dest);
			ClientUpgradeRequest request = new ClientUpgradeRequest();
			request.setSubProtocols("text");
			request.setHeader("user", socket.nickname);							//sending user nickname <<here>>
			Future<Session> fut = null;
			fut = client.connect(socket, hurricaneUri, request);
			System.out.printf("Connecting to %s\n", hurricaneUri);
			try {
//				fut.get(10, TimeUnit.SECONDS);
//				if (!fut.isDone()) {
//					System.err.printf("Could not connect to %s\n", hurricaneUri);
//				}
				closeLatch.await();
			} catch (Exception e) {
				System.err.printf("Could not connect to %s (%s)\n", hurricaneUri, e.getCause().getMessage());
				//					e.printStackTrace();
			}
		} catch (Exception e){
//			e.printStackTrace();
		} finally {
			try {
//				System.out.println("Latch down.");
				client.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
}
