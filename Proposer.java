import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The Proposer interface is the role of a Proposer in the PAXOS algorithm. The role of 
 * Proposer is to allow remote clients to propose commands to issue on the Distributed
 * System simulation.
 */
public interface Proposer extends Remote {

    /**
     * A command that is proposed to the Distributed System by clients, using PAXOS. 
     * 
     * @param command The command that has been proposed (Example: 'GET', 'PUT', 'DELETE')
     * @param args Arguments for the command, if any.
     * @return A String depicting the result of execution of the command proposed.
     * @throws RemoteException If an error occurs during remote communication.
     */
    String propose(String command, String[] args) throws RemoteException;
}