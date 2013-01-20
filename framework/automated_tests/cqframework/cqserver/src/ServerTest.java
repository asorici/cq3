import gnu.cajo.invoke.Remote;
import gnu.cajo.utils.ItemServer;

import org.aimas.craftingquest.core.Server0;


public class ServerTest {
	public static void main(String[] args) throws Exception {
		// args are servername, port, secrets file
		Server0 server = null;
		if(args == null || args.length < 3) {
			server = new Server0("CraftingQuest", 1198, "secrets.txt");
		}
		else {
			if (args != null && args.length == 3) {
				String serverName = args[0];
				int port = Integer.parseInt(args[1]);
				String secretsFileName = args[2];
				
				server = new Server0(serverName, port, secretsFileName);
			}
		}
		
		Remote.config(null, server.getPortNumber(), null, 0);
		ItemServer.bind(server, server.getServername());

		server.gameLoop();
	}
}
