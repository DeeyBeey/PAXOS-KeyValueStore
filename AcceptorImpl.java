import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * The AcceptorImpl class implements the Acceptor interface, dealing with prepare and accept
 * requests from the Proposer in the PAXOS algorithm.
 */
public class AcceptorImpl extends UnicastRemoteObject implements Acceptor {
    private final ConcurrentHashMap<String, String> promises;
    private final ConcurrentHashMap<String, String> acceptedValues;
    private static final Logger logger = LoggerConfig.createLogger("Acceptor");

    /**
     * Constrtucts an AcceptorImpl instance, initializing promises and acceptedValues maps.
     * 
     * @throws RemoteException If an error occurs during remote communication.
     */
    protected AcceptorImpl() throws RemoteException {
        super();
        this.promises = new ConcurrentHashMap<>();
        this.acceptedValues = new ConcurrentHashMap<>();
    }

    /**
     * Deals with accepting a request for any received proposal.
     * 
     * @param proposalID A proposal's unique identifier.
     * @param value The value associated with the proposal.
     * @return A String depicting the response from the Acceptor, either "ACCEPTED" or "REJECTED".
     * @throws RemoteException If an error occurs during remote communication.
     */
    @Override
    public String prepare(String proposalID) throws RemoteException {
        logger.info("Received 'prepare' request for proposalID: " + proposalID);
        if (promises.containsKey(proposalID) || acceptedValues.containsKey(proposalID)) {
            return "PROMISE";
        } else {
            promises.put(proposalID, "");
            return "PROMISE";
        }
    }

     /**
     * Deals with preparing a request for any received proposal.
     * @param proposalID A proposal's unique identifier.
     * @return A String depicting the response from the Acceptor: "PROMISE".
     * @throws RemoteException If an error occurs during remote communication.
     */
    @Override
    public String accept(String proposalID, String value) throws RemoteException {
        logger.info("Received 'accept' request for proposalID: " + proposalID + " with value: " + value);
        if (promises.containsKey(proposalID)) {
            acceptedValues.put(proposalID, value);
            return "ACCEPTED";
        }
        return "REJECTED";
    }
}
