package fr.adh.client;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.app.Application;
import com.jme3.network.Client;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;

import fr.adh.client.gui.GuiState;
import fr.adh.common.ChatMessage;
import fr.adh.common.ShutdownServerMessage;
import fr.adh.common.SpawnEntityMessage;
import fr.adh.common.WelcomeMessage;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ClientMessageListener implements MessageListener<Client> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientMessageListener.class);

    private Application application;

    @Override
    public void messageReceived(Client client, Message message) {
        if (message instanceof ShutdownServerMessage) {
            ShutdownServerMessage msg = (ShutdownServerMessage) message;
            LOGGER.warn("Shutdown server message receive [{}]", msg.getReason());
            application.getStateManager().getState(GuiState.class).onSystemMessageReceived(msg.getReason());
        } else if (message instanceof WelcomeMessage) {
            WelcomeMessage msg = (WelcomeMessage) message;
            LOGGER.warn("Welcome message receive for [{}]", msg.getPlayerName());
            application.getStateManager().getState(GuiState.class).startGuiGame(msg.getPlayerName(), msg.getMessage());
        } else if (message instanceof ChatMessage) {
            ChatMessage msg = (ChatMessage) message;
            application.getStateManager().getState(GuiState.class).onMessageReceived(msg.getPlayerName(),
                    msg.getMessage());
        } else if (message instanceof SpawnEntityMessage) {
            SpawnEntityMessage spawnEntity = (SpawnEntityMessage) message;
            LOGGER.info("{} entity with id [{}].", spawnEntity.isSpawnOrDie() ? "Spawn" : "Remove",
                    spawnEntity.getIds());
            if (spawnEntity.isSpawnOrDie()) {
                Arrays.asList(spawnEntity.getIds()).forEach(AdhClient.getInstance()::addEntity);
            } else {
                Arrays.asList(spawnEntity.getIds()).forEach(AdhClient.getInstance()::removeEntity);
            }
        }
    }
}
