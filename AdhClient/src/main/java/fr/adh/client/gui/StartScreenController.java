package fr.adh.client.gui;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import fr.adh.client.AdHClient;
import fr.adh.client.gui.chat.AdhChatControl;
import fr.adh.client.gui.chat.event.AdhChatSendTextEvent;

public class StartScreenController implements ScreenController {

	private static final Logger LOGGER = LoggerFactory.getLogger(StartScreenController.class);

	@Nullable
	private Screen screen;

	@Override
	public void bind(Nifty nifty, Screen screen) {
		this.screen = screen;

	}

	@Override
	public void onStartScreen() {

	}

	@Override
	public void onEndScreen() {

	}

	@NiftyEventSubscriber(id = "chatMainId")
	public void onMainChatMessage(final String id, @Nonnull final AdhChatSendTextEvent chatEvent) {
		LOGGER.info("New event received [{}]", id);
		if (screen == null) {
			return;
		}
		AdHClient.sendMessage(chatEvent.getMessage());
	}

	public void onMessageReceived(String playerName, String message) {
		LOGGER.info("New message received from server");
		AdhChatControl chat = screen.findNiftyControl("chatMainId", AdhChatControl.class);
		if (chat != null) {
			chat.receivedChatLine(playerName, message);
		}
	}

	public void onSystemMessageReceived(String message) {
		LOGGER.info("New system message received from server");
		@SuppressWarnings("unchecked")
		ListBox<String> lb = screen.findNiftyControl("chatSystemId", ListBox.class);
		if (lb != null) {
			lb.addItem("[System] " + message);
		}
	}
}
