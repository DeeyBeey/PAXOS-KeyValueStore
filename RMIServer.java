import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.HashMap;
import java.util.Map;
import java.rmi.Remote;

/**
 * The RMIServer class is responsible for starting the RMI Server, creating and registering 
 * Proposer, Acceptor and Learner objects and starting the FailureSimulator to simulate random
 * failures and restarting the PAXOS entities.
 */
public class RMIServer {
    /**
     * Main method for the RMI Server.
     * 
     * @param args Command Line Arguments to run the server: port number of the server to run.
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Example Usage: java RMIServer <port>");
            return;
        }

        int port = Integer.parseInt(args[0]);
        try {
            ExecutorService executor = Executors.newCachedThreadPool();
            Map<Integer, Acceptor> acceptors = new HashMap<>();
            Map<Integer, Learner> learners = new HashMap<>();
            Map<String, String> keyValueStore = new HashMap<>();

            for (int i = 1; i <= 3; i++) {
                acceptors.put(i, new AcceptorImpl());
                learners.put(i, new LearnerImpl(keyValueStore));
            }

            ProposerImpl proposer = new ProposerImpl(executor, acceptors, learners);

            Registry registry = LocateRegistry.createRegistry(port);
            registry.bind("Proposer", proposer);

            for (Map.Entry<Integer, Acceptor> entry : acceptors.entrySet()) {
                registry.bind("Acceptor" + entry.getKey(), entry.getValue());
            }

            for (Map.Entry<Integer, Learner> entry : learners.entrySet()) {
                registry.bind("Learner" + entry.getKey(), entry.getValue());
            }

            System.out.println("Server is ready.");

            Map<String, Remote> remoteObjects = new HashMap<>();
            remoteObjects.put("Proposer", proposer);

            for (Map.Entry<Integer, Acceptor> entry : acceptors.entrySet()) {
                remoteObjects.put("Acceptor" + entry.getKey(), entry.getValue());
            }

            for (Map.Entry<Integer, Learner> entry : learners.entrySet()) {
                remoteObjects.put("Learner" + entry.getKey(), entry.getValue());
            }

            FailureSimulator simulator = new FailureSimulator(remoteObjects, registry);
            simulator.start();
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
