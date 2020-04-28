package fr.adh.client.gui;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.FocusGainedEvent;
import de.lessvoid.nifty.controls.FocusLostEvent;
import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import fr.adh.client.AdhClient;
import fr.adh.client.gui.chat.AdhChatControl;
import fr.adh.client.gui.chat.event.AdhChatSendTextEvent;

public class GuiScreenController implements ScreenController {

	private static final Logger LOGGER = LoggerFactory.getLogger(GuiScreenController.class);

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
		AdhClient.getInstance().sendMessage(chatEvent.getMessage());
	}

	@NiftyEventSubscriber(id = "chatMainId#adh-chat-text-input")
	public void onTextFieldFocus(final String id, @Nonnull final FocusGainedEvent event) {
		LOGGER.info("Focus event on [{}]", id);
		AdhClient.getInstance().setInputEnable(false);
	}

	@NiftyEventSubscriber(id = "chatMainId#adh-chat-text-input")
	public void onTextFieldLostFocus(final String id, @Nonnull final FocusLostEvent event) {
		LOGGER.info("Focus lost event on [{}]", id);
		AdhClient.getInstance().setInputEnable(true);
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
