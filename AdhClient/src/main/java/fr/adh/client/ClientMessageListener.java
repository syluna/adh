package fr.adh.client;

import com.jme3.network.Client;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import fr.adh.common.ChatMessage;
import fr.adh.common.ShutdownServerMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientMessageListener implements MessageListener<Client> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientMessageListener.class);
    
    @Override
    public void messageReceived(Client client, Message message) {
        if(message instanceof ShutdownServerMessage){
            ShutdownServerMessage msg = (ShutdownServerMessage) message;
            LOGGER.warn("Shutdown server message receive [{}]", msg.getReason());
            AdHClient.getInstance().getStartScreenController().onSystemMessageReceived(msg.getReason());
        } else if(message instanceof ChatMessage){
            ChatMessage msg = (ChatMessage) message;
            AdHClient.getInstance().getStartScreenController().onMessageReceived(msg.getPlayerName(), msg.getMessage());
        }
    }
    
}
