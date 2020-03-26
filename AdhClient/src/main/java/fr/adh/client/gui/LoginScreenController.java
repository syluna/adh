package fr.adh.client.gui;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.lessvoid.nifty.EndNotify;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import fr.adh.client.AdhClient;

public class LoginScreenController implements ScreenController {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoginScreenController.class);

	@Nullable
	private Nifty nifty;
	@Nullable
	private Screen screen;

	@Override
	public void bind(Nifty nifty, Screen screen) {
		this.nifty = nifty;
		this.screen = screen;
	}

	@Override
	public void onStartScreen() {

	}

	@Override
	public void onEndScreen() {

	}

	@NiftyEventSubscriber(id = "loginButton")
	public void onLoggin(final String id, @Nonnull final ButtonClickedEvent event) {
		String playerName = screen.findNiftyControl("loginInput", TextField.class).getRealText();
		String password = screen.findNiftyControl("passwordInput", TextField.class).getRealText();
		LOGGER.info("Login button clicked [{}] Login [{}], password [{}]", id, playerName, password);
		AdhClient.getInstance().createConnection(playerName, password);
	}

	public void onLoginSuccessMessageReceived(@Nonnull String playerName, @Nonnull String message) {
		nifty.getCurrentScreen().endScreen(new EndNotify() {

			@Override
			public void perform() {

			}
		});
		AdhClient.getInstance().initLandscape(playerName);
		nifty.fromXml("Interface/start/start.xml", "start");

		try {
			Thread.sleep(2000l);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		((StartScreenController) AdhClient.getScreenController("start")).onSystemMessageReceived(message);
	}

}
