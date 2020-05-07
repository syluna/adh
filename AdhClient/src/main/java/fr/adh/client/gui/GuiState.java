package fr.adh.client.gui;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.simsilica.lemur.OptionPanelState;

import fr.adh.client.AdhClient;

public class GuiState extends BaseAppState {

    private static final Logger LOGGER = LoggerFactory.getLogger(GuiState.class);

    private final Map<Class<? extends BaseAppState>, BaseAppState> gui = new HashMap<>();

    @Override
    protected void initialize(final Application application) {
        getStateManager().attach(new OptionPanelState());
    }

    @Override
    protected void cleanup(final Application application) {

    }

    @Override
    protected void onEnable() {
        gui.put(LoginState.class, new LoginState());
        gui.put(ChatState.class, new ChatState());
        open(LoginState.class);
    }

    @Override
    protected void onDisable() {
        gui.keySet().forEach(appState -> close(appState));
    }

    public void showError(String title, Throwable error) {
        getState(OptionPanelState.class).showError(title, error);
    }

    private void open(Class<? extends BaseAppState> appState) {
        BaseAppState app = gui.get(appState);
        if (!getStateManager().hasState(app)) {
            getStateManager().attach(app);
        }
    }

    private void close(Class<? extends BaseAppState> appState) {
        BaseAppState app = gui.get(appState);
        if (getStateManager().hasState(app)) {
            getStateManager().detach(app);
        }
    }

    public void startGuiGame(@NotNull String playerName, @NotNull String message) {
        LOGGER.info("[{}] successfuly login with message [{}].", playerName, message);
        close(LoginState.class);
        open(ChatState.class);
        AdhClient.getInstance().initLandscape(playerName);
        onSystemMessageReceived(message);
    }

    public void onMessageReceived(final String playerName, final String message) {
        onSystemMessageReceived(new StringBuilder("[").append(playerName).append("] ").append(message).toString());
    }

    public void onSystemMessageReceived(final String message) {
        ((ChatState) gui.get(ChatState.class)).addMessage(message);
    }
}
