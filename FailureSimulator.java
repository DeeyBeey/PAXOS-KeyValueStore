import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Map;
import java.rmi.Remote;
import java.rmi.registry.Registry;

/**
 * The FailureSimulator class simulates failures in Proposers, Acceptors and Learners and
 * restarts them.
 */
public class FailureSimulator {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Map<String, Remote> remoteObjects;
    private final Registry registry;

    /**
     * Constructs a FailureSimulator with mentioned remote objects and RMI registries.
     * 
     * @param remoteObjects The map of remote objects that is to be managed by the simulator.
     * @param registry The RMI registry where remote objects are bound.
     */
    public FailureSimulator(Map<String, Remote> remoteObjects, Registry registry) {
        this.remoteObjects = remoteObjects;
        this.registry = registry;
    }

    /**
     * Induces a failure and restarts the simulation. 
     */
    public void start() {
        scheduler.scheduleAtFixedRate(this::simulateFailure, 10, 30, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(this::simulateRestart, 20, 30, TimeUnit.SECONDS);
    }

    /**
     * Simulates failure in a randomly chosen remote object by unbinding it from the RMI registry.
     */
    private void simulateFailure() {
        try {
            Random random = new Random();
            int index = random.nextInt(remoteObjects.size());
            String key = (String) remoteObjects.keySet().toArray()[index];
            registry.unbind(key);
            System.out.println(key + " has failed.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Simulates restart in a randomly chosen remote object by rebinding it from the RMI registry.
     */
    private void simulateRestart() {
        try {
            Random random = new Random();
            int index = random.nextInt(remoteObjects.size());
            String key = (String) remoteObjects.keySet().toArray()[index];
            Remote remoteObject = remoteObjects.get(key);
            registry.rebind(key, remoteObject);
            System.out.println(key + " has restarted.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
