import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The Learner interface is the role of a Learner in the PAXOS algorithm. The role of 
 * Learner is to learn the value that has been decided once consensus is received.
 */
public interface Learner extends Remote {

    /**
     * The learn method is called to notify the Learner that has been chosen once consensus
     * is achieved.
     * 
     * @param value The final value decided after gaining consensus.
     * @throws RemoteException If an error occurs during remote communication.
     */
    void learn(String value) throws RemoteException;
}