package fr.adh.server;

import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Server;
import fr.adh.common.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerListener implements MessageListener<HostedConnection> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerListener.class);
    
    private final Server server;
    
    public ServerListener(Server server){
        this.server = server;
    }
    
    @Override
    public void messageReceived(HostedConnection source, Message message) {
        if (message instanceof ChatMessage) {
            // do something with the message
            ChatMessage chatMsg = (ChatMessage) message;
            LOGGER.info("Chat message received from [{}] and broadcast content [{}].", source.getId(), chatMsg.getMessage());
            server.broadcast(chatMsg);
        } 
    }

}
