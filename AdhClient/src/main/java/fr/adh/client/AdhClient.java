package fr.adh.client;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.jme3.app.SimpleApplication;
import com.jme3.network.Client;
import com.jme3.network.ClientStateListener;
import com.jme3.network.Network;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import fr.adh.common.ChatMessage;
import fr.adh.common.LoginMessage;
import fr.adh.common.WelcomeMessage;
import lombok.Getter;

public class AdhClient extends SimpleApplication implements ClientStateListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(AdhClient.class);

	private LandscapeManager landscapeManager;

	private Client client;

	private String playerName;
	@Getter
	private Nifty nifty;

	private static AdhClient adhClient;

	public static final AdhClient getInstance() {
		if (adhClient == null) {
			adhClient = new AdhClient();
		}
		return adhClient;
	}

	public static void main(String[] args) {
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();

		LOGGER.info("Starting application...");

		AppSettings settings = new AppSettings(true);
		settings.setTitle("L'Aube des Heros - v1.0.0");
		settings.setResolution(800, 600);
		settings.setVSync(true);

		getInstance().setSettings(settings);
		getInstance().setShowSettings(false);
		getInstance().setDisplayFps(false);
		getInstance().setDisplayStatView(false);
		getInstance().start();
	}

	public final void sendMessage(String message) {
		if (client == null || !client.isConnected()) {
			LOGGER.warn("Unable to send message because client is not connected.");
			return;
		}
		client.send(new ChatMessage(playerName, message));
	}

	public void initLandscape(String playerName) {
		landscapeManager = new LandscapeManager(this);
		landscapeManager.create(rootNode, guiFont, playerName);
	}

	@Override
	public void simpleInitApp() {
		getFlyByCamera().setEnabled(false);

		NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
		nifty = niftyDisplay.getNifty();

		nifty.fromXml("Interface/start/login.xml", "login");

		guiViewPort.addProcessor(niftyDisplay);
		inputManager.setCursorVisible(true);
	}

	@Override
	public void simpleUpdate(float tpf) {
		super.simpleUpdate(tpf);
		if (landscapeManager != null) {
			landscapeManager.update(tpf);
		}
	}

	@Override
	public void simpleRender(RenderManager rm) {
		// TODO: add render code
	}

	@Override
	public void clientConnected(Client client) {
		LOGGER.info("Client [{}] is now connected on [{}] in version [{}].", client.getId(), client.getGameName(),
				client.getVersion());
	}

	@Override
	public void clientDisconnected(Client client, DisconnectInfo arg1) {
		LOGGER.info("Client now disconnecting.");
	}

	public void createConnection(String login, String password) {
		playerName = login;
		try {
			client = Network.connectToServer("Aube des heros", 1, "localhost", 8080);
			client.addClientStateListener(this);
			client.addMessageListener(new ClientMessageListener(), ChatMessage.class, WelcomeMessage.class);
			client.start();
			while (!client.isConnected()) {
				try {
					Thread.sleep(1000l);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			client.send(new LoginMessage(login, password));
		} catch (IOException ex) {
			LOGGER.error("Connection to server error", ex);
		}
	}

	@SuppressWarnings("unchecked")
	public static final <T extends ScreenController> T getScreenController(String screenName) {
		if (AdhClient.getInstance().getNifty() == null) {
			return null;
		}
		Screen startScreen = AdhClient.getInstance().getNifty().getScreen(screenName);
		if (startScreen == null) {
			return null;
		}
		return (T) startScreen.getScreenController();
	}
}
