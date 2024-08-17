import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.logging.Logger;

/**
 * LearnerImpl class is the implementation of the Learner interface. It receives the value
 * decided after consensus and updates the key-value store accordingly.
 */
public class LearnerImpl extends UnicastRemoteObject implements Learner {
    private final Map<String, String> keyValueStore;
    private static final Logger logger = LoggerConfig.createLogger("Learner");

    /**
     * Contructs a LearnerImpl with mentioned key-value store.
     * 
     * @param keyValueStore The key-value store that the learner updates.
     * @throws RemoteException If an error occurs during remote communication.
     */
    protected LearnerImpl(Map<String, String> keyValueStore) throws RemoteException {
        super();
        this.keyValueStore = keyValueStore;
    }

    /**
     * The learn method is called to notify the Learner that has been chosen once consensus
     * is achieved.
     * 
     * @param value The final value decided after gaining consensus.
     * @throws RemoteException If an error occurs during remote communication.
     */
    @Override
    public void learn(String value) throws RemoteException {
        logger.info("Value learned: " + value);
        String[] parts = value.split(" ", 3);
        String command = parts[0];
        String key = parts[1];
        if (command.equals("PUT")) {
            String val = parts[2];
            keyValueStore.put(key, val);
            logger.info("Stored PUT key: " + key + " value: " + val);
        } else if (command.equals("DELETE")) {
            keyValueStore.remove(key);
            logger.info("Stored DELETE key: " + key);
        }
    }
}
