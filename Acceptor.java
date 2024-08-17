import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The Acceptor interface is the role of an Acceptor in the PAXOS algorithm. The role of 
 * Acceptor is to receive proposals from Proposers and responding with promises or acceptances.
 */
public interface Acceptor extends Remote {

    /**
     * Deals with accepting a request for any received proposal.
     * 
     * @param proposalID A proposal's unique identifier.
     * @param value The value associated with the proposal.
     * @return A String depicting the response from the Acceptor, either "ACCEPTED" or "REJECTED".
     * @throws RemoteException If an error occurs during remote communication.
     */
    String accept(String proposalID, String value) throws RemoteException;

    /**
     * Deals with preparing a request for any received proposal.
     * @param proposalID A proposal's unique identifier.
     * @return A String depicting the response from the Acceptor: "PROMISE".
     * @throws RemoteException If an error occurs during remote communication.
     */
    String prepare(String proposalID) throws RemoteException;
}
