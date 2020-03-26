package fr.adh.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.network.Client;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;

import fr.adh.client.gui.LoginScreenController;
import fr.adh.client.gui.StartScreenController;
import fr.adh.common.ChatMessage;
import fr.adh.common.ShutdownServerMessage;
import fr.adh.common.WelcomeMessage;

public class ClientMessageListener implements MessageListener<Client> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClientMessageListener.class);

	@Override
	public void messageReceived(Client client, Message message) {
		if (message instanceof ShutdownServerMessage) {
			ShutdownServerMessage msg = (ShutdownServerMessage) message;
			LOGGER.warn("Shutdown server message receive [{}]", msg.getReason());
			StartScreenController controller = ((StartScreenController) AdHClient.getScreenController("start"));
			if (controller != null) {
				controller.onSystemMessageReceived(msg.getReason());
			}
		} else if (message instanceof WelcomeMessage) {
			WelcomeMessage msg = (WelcomeMessage) message;
			LOGGER.warn("Welcome message receive for [{}]", msg.getPlayerName());
			((LoginScreenController) AdHClient.getScreenController("login"))
					.onLoginSuccessMessageReceived(msg.getPlayerName(), msg.getMessage());
		} else if (message instanceof ChatMessage) {
			ChatMessage msg = (ChatMessage) message;
			((StartScreenController) AdHClient.getScreenController("start")).onMessageReceived(msg.getPlayerName(),
					msg.getMessage());
		}
	}
}
