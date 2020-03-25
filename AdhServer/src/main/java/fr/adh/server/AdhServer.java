package fr.adh.server;

import com.jme3.app.SimpleApplication;
import com.jme3.network.ConnectionListener;
import com.jme3.network.HostedConnection;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.network.serializing.Serializer;
import com.jme3.system.JmeContext;
import fr.adh.common.ChatMessage;
import fr.adh.common.ShutdownServerMessage;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

public class AdhServer extends SimpleApplication implements ConnectionListener  {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdhServer.class);

    private Server server;

    public static void main(String[] args) throws Exception {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        AdhServer adhServer = new AdhServer();
        adhServer.start(JmeContext.Type.Headless);
    }

    @Override
    public void simpleInitApp() {
        try {
            LOGGER.info("Starting server...");
            server = Network.createServer("Aube des heros", 1, 8080, 8080);
            server.addConnectionListener(this);
            Serializer.registerClass(ShutdownServerMessage.class);
            Serializer.registerClass(ChatMessage.class);
            
            server.addMessageListener(new ServerListener(server), ChatMessage.class);
            
            server.start();
        } catch (IOException ex) {
            LOGGER.error("Server error", ex);
        }
    }

    @Override
    public void simpleUpdate(float tpf) {
        super.simpleUpdate(tpf);
    }

    @Override
    public void destroy() {
        LOGGER.info("Shutdowning server.");
        server.close();
        super.destroy();
    }

    @Override
    public void connectionAdded(Server server, HostedConnection con) {
        LOGGER.info("Client id [{}] from [{}] is now connected.", con.getId(), con.getAddress());
        server.broadcast(new ShutdownServerMessage("Closing for maintenance in few minutes."));
    }

    @Override
    public void connectionRemoved(Server server, HostedConnection con) {
        LOGGER.info("Client id [{}] leaving.", con.getId());
    }

}
