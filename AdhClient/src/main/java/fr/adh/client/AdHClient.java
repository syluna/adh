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
import fr.adh.client.gui.StartScreenController;
import fr.adh.common.ChatMessage;
import fr.adh.common.ShutdownServerMessage;
import lombok.Getter;

public class AdHClient extends SimpleApplication implements ClientStateListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(AdHClient.class);

	private LandscapeManager landscapeManager;

	private Client client;
	@Getter
	private StartScreenController startScreenController;

	private static AdHClient adhClient;

	public static final AdHClient getInstance() {
		if (adhClient == null) {
			adhClient = new AdHClient();
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

	public static final void sendMessage(String message) {
		getInstance().client.send(new ChatMessage("Player", message));
	}

	@Override
	public void simpleInitApp() {
		getFlyByCamera().setEnabled(false);

		landscapeManager = new LandscapeManager(this);
		landscapeManager.create(rootNode, guiFont);

		createGUI();
		createConnection();

	}

	@Override
	public void simpleUpdate(float tpf) {
		super.simpleUpdate(tpf);
		landscapeManager.update(tpf);
	}

	@Override
	public void simpleRender(RenderManager rm) {
		// TODO: add render code
	}

	private void createGUI() {
		NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
		Nifty nifty = niftyDisplay.getNifty();
		nifty.fromXml("Interface/start/start.xml", "start");

		Screen startScreen = nifty.getScreen("start");
		if (startScreen == null) {
			return;
		}
		startScreenController = (StartScreenController) startScreen.getScreenController();
		LOGGER.info("StartScreenController [{}]", startScreenController);

		guiViewPort.addProcessor(niftyDisplay);
		inputManager.setCursorVisible(true);
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

	private void createConnection() {
		try {
			client = Network.connectToServer("Aube des heros", 1, "localhost", 8080);
			client.addClientStateListener(this);
			client.addMessageListener(new ClientMessageListener(), ShutdownServerMessage.class, ChatMessage.class);
			client.start();
		} catch (IOException ex) {
			LOGGER.error("Connection to server error", ex);
		}
	}
}
