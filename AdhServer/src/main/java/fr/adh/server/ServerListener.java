package fr.adh.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.network.Filters;
import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Server;

import fr.adh.common.ChatMessage;
import fr.adh.common.LoginMessage;
import fr.adh.common.WelcomeMessage;

public class ServerListener implements MessageListener<HostedConnection> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ServerListener.class);

	private final Server server;

	public ServerListener(Server server) {
		this.server = server;
	}

	@Override
	public void messageReceived(HostedConnection source, Message message) {
		if (message instanceof ChatMessage) {
			ChatMessage chatMsg = (ChatMessage) message;
			LOGGER.info("Chat message received from [{}] and broadcast content [{}].", source.getId(),
					chatMsg.getMessage());
			server.broadcast(chatMsg);
		} else if (message instanceof LoginMessage) {
			LoginMessage loginMsg = (LoginMessage) message;
			LOGGER.info("Login message received from [{}] with login [{}].", source.getId(), loginMsg.getLogin());
			server.broadcast(Filters.equalTo(source), new WelcomeMessage(loginMsg.getLogin(),
					"Welcome [" + loginMsg.getLogin() + "] to the Adh server."));
		}
	}

}
