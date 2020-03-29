package fr.adh.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.jme3.app.SimpleApplication;
import com.jme3.network.ConnectionListener;
import com.jme3.network.Filters;
import com.jme3.network.HostedConnection;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.network.serializing.Serializer;
import com.jme3.system.JmeContext;

import fr.adh.common.ChatMessage;
import fr.adh.common.LoginMessage;
import fr.adh.common.ShutdownServerMessage;
import fr.adh.common.SpawnEntityMessage;
import fr.adh.common.WelcomeMessage;
import lombok.Getter;

public class AdhServer extends SimpleApplication implements ConnectionListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(AdhServer.class);

	@Getter
	private static Map<Integer, HostedConnection> players = new HashMap<>();

	private Server server;
	private static BufferedReader cin;

	public static void main(String[] args) throws Exception {
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();

		try {
			cin = new BufferedReader(new InputStreamReader(System.in, Charset.defaultCharset().name()));
		} catch (final UnsupportedEncodingException e) {
			e.printStackTrace();
			cin = new BufferedReader(new InputStreamReader(System.in));
		}

		AdhServer adhServer = new AdhServer();
		adhServer.start(JmeContext.Type.Headless);
	}

	@Override
	public void simpleInitApp() {
		try {
			LOGGER.info("Starting server...");
			server = Network.createServer("Aube des heros", 1, 8080, 8080);
			server.addConnectionListener(this);
			Serializer.registerClass(ShutdownServerMessage.class);
			Serializer.registerClass(ChatMessage.class);
			Serializer.registerClass(LoginMessage.class);
			Serializer.registerClass(WelcomeMessage.class);
			Serializer.registerClass(SpawnEntityMessage.class);

			server.addMessageListener(new ServerListener(server), ChatMessage.class, LoginMessage.class);

			server.start();
		} catch (IOException ex) {
			LOGGER.error("Server error", ex);
		}
	}

	@Override
	public void simpleUpdate(float tpf) {
		super.simpleUpdate(tpf);
		consoleCommands();
	}

	@Override
	public void destroy() {
		LOGGER.info("Shutdowning server. Kick [{}] client.", server.getConnections().size());
		server.getConnections().forEach(c -> c.close("Server stopping."));
		server.close();
		LOGGER.info("Server shutdown.");
		super.destroy();
	}

	@Override
	public void connectionAdded(Server server, HostedConnection con) {
		LOGGER.info("Client id [{}] from [{}] is now connected.", con.getId(), con.getAddress());
		players.put(con.getId(), con);
		server.broadcast(Filters.notEqualTo(con), new SpawnEntityMessage(con.getId(), true));
	}

	@Override
	public void connectionRemoved(Server server, HostedConnection con) {
		LOGGER.info("Client id [{}] leaving.", con.getId());
		players.remove(con.getId());
		server.broadcast(Filters.notEqualTo(con), new SpawnEntityMessage(con.getId(), false));
	}

	private void consoleCommands() {
		try {
			if (cin.ready()) {
				final String line = cin.readLine().trim();
				if (line.length() > 0) {
					String[] command = split(line);
					LOGGER.info("Command [{}] args [{}] [{}]", command[0], command.length - 1,
							String.join(", ", Arrays.copyOfRange(command, 1, command.length)));
					if ("stop".equalsIgnoreCase(command[0]) && server.isRunning()) {
						stop();
					}
				}
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private String[] split(final String commandLine) {
		ArrayList<String> parts = new ArrayList<String>();

		Reader r = new StringReader(commandLine);
		StreamTokenizer st = new StreamTokenizer(r);
		st.resetSyntax();
		st.wordChars(32, 255);
		st.whitespaceChars(0, ' ');
		st.quoteChar('"');
		st.quoteChar('\'');

		int token;
		try {
			while ((token = st.nextToken()) != StreamTokenizer.TT_EOF) {
				switch (token) {
				case StreamTokenizer.TT_WORD:
					parts.add(st.sval);
					break;
				case '\'':
				case '"':
					parts.add(st.sval);
					break;
				default:
				}
			}
			return parts.toArray(new String[parts.size()]);
		} catch (IOException e) {
			LOGGER.warn("exception whild parsing '{}'", commandLine);
			return new String[] { commandLine };
		}
	}
}
