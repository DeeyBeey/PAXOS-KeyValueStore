import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

/**
 * Implementation of the Proposer Interface. It manages the key-value store and is
 * the most important entity in the PAXOS algorithm in our implementation.
 */
public class ProposerImpl extends UnicastRemoteObject implements Proposer {
    private final Map<String, String> keyValueStore;
    private final ExecutorService executor;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern
    ("yyyy-MM-dd HH:mm:ss.SSS");
    private final Map<Integer, Acceptor> acceptors;
    private final Map<Integer, Learner> learners;
    private int proposalNumber;
    private static final Logger logger = LoggerConfig.createLogger("Proposer");

    /**
     * Constructs a ProposerImpl instance.
     * 
     * @param executor The executor service to perform asynchronus tasks.
     * @param acceptors This is the map of the acceptor nodes in our system.
     * @param learners This is the map of the learner nodes in our system. 
     * @throws RemoteException If an error occurs during remote communication.
     */
    protected ProposerImpl(ExecutorService executor, Map<Integer, Acceptor> acceptors, 
    Map<Integer, Learner> learners) throws RemoteException {
        super();
        this.keyValueStore = new ConcurrentHashMap<>();
        this.executor = executor;
        this.acceptors = acceptors;
        this.learners = learners;
        this.proposalNumber = 0;
    }

    /**
     * A command that is proposed to the Distributed System by clients, using PAXOS. 
     * 
     * @param command The command that has been proposed (Example: 'GET', 'PUT', 'DELETE')
     * @param args Arguments for the command, if any.
     * @return A String depicting the result of execution of the command proposed.
     * @throws RemoteException If an error occurs during remote communication.
     */
    @Override
    public String propose(String command, String[] args) throws RemoteException {
        logger.info("Received command: " + command + " with args: " + String.join(" ", args));
        Callable<String> task = () -> {
            logClientRequest(command, args);
            String result;
            switch (command) {
                case "PUT":
                    result = proposePut(args);
                    break;
                case "GET":
                    result = get(args);
                    break;
                case "DELETE":
                    result = proposeDelete(args);
                    break;
                default:
                    result = "Invalid Command. (Use: GET/PUT/DELETE)";
            }
            logClientResponse(result);
            return result;
        };

        Future<String> future = executor.submit(task);

        try {
            return future.get();
        } catch (Exception e) {
            throw new RemoteException("Error executing command", e);
        }
    }

    /**
     * Handles the proposal of the PUT command by the client.
     * 
     * @param args The arguments used with the PUT command.
     * @return A result depicting whether the operation was successful or not.
     * @throws RemoteException If an error occurs during remote communication.
     */
    private String proposePut(String[] args) throws RemoteException {
        if (args.length < 2) return "Example Usage: PUT <key> <value>";
        String key = args[0];
        String value = args[1];
        String proposalID = generateProposalID();
        String proposalValue = "PUT " + key + " " + value;

        int preparedCount = 0;
        for (Acceptor acceptor : acceptors.values()) {
            String response = acceptor.prepare(proposalID);
            if (response.equals("PROMISE")) {
                preparedCount++;
            }
        }

        if (preparedCount > acceptors.size() / 2) {
            int acceptedCount = 0;
            for (Acceptor acceptor : acceptors.values()) {
                String response = acceptor.accept(proposalID, proposalValue);
                if (response.equals("ACCEPTED")) {
                    acceptedCount++;
                }
            }

            if (acceptedCount > acceptors.size() / 2) {
                for (Learner learner : learners.values()) {
                    learner.learn(proposalValue);
                }
                keyValueStore.put(key, value);
                return "Operation successful.";
            }
        }
        return "Operation failed.";
    }

    /**
     * Handles the proposal of the DELETE command by the client.
     * 
     * @param args The arguments used with the DELETE command.
     * @return A result depicting whether the operation was successful or not.
     * @throws RemoteException If an error occurs during remote communication.
     */
    private String proposeDelete(String[] args) throws RemoteException {
        if (args.length < 1) return "Example Usage: DELETE <key>";
        String key = args[0];
        String proposalID = generateProposalID();
        String proposalValue = "DELETE " + key;

        int preparedCount = 0;
        for (Acceptor acceptor : acceptors.values()) {
            String response = acceptor.prepare(proposalID);
            if (response.equals("PROMISE")) {
                preparedCount++;
            }
        }

        if (preparedCount > acceptors.size() / 2) {
            int acceptedCount = 0;
            for (Acceptor acceptor : acceptors.values()) {
                String response = acceptor.accept(proposalID, proposalValue);
                if (response.equals("ACCEPTED")) {
                    acceptedCount++;
                }
            }

            if (acceptedCount > acceptors.size() / 2) {
                for (Learner learner : learners.values()) {
                    learner.learn(proposalValue);
                }
                keyValueStore.remove(key);
                return "Operation successful.";
            }
        }
        return "Operation failed.";
    }

    /**
     * Handles the GET command issued by the client.
     * 
     * @param args The arguments used with the GET command.
     * @return The value associated with the key of the specified argument else 
     *         indicating no match found.
     */
    private String get(String[] args) {
        if (args.length < 1) return "Example Usage: GET <key>";
        String key = args[0];
        String value = keyValueStore.get(key);
        return value != null ? value : "No record found.";
    }

    /**
     * Generates a unique ID for proposals by the Proposer.
     * 
     * @return A unique ID for the proposal.
     */
    private String generateProposalID() {
        proposalNumber++;
        return "PROPOSAL-" + proposalNumber;
    }

    /**
     * Logs the requests received from the client.
     * 
     * @param command The command received by the client.
     * @param args The arguments for the command. 
     */
    private void logClientRequest(String command, String[] args) {
        String argsString = String.join(" ", args);
        String message = "Received command: " + command + " " + argsString;
        printWithTimestamp(message);
    }

    /**
     * Logs the response to be sent to the client.
     * 
     * @param response The response sent to the client.
     */
    private void logClientResponse(String response) {
        String message = "Response: " + response;
        printWithTimestamp(message);
    }

    /**
     * Helper method to print the message with a timestamp.
     * 
     * @param message The message to be printed along with the timestamp on the console.
     */
    private static void printWithTimestamp(String message) {
        System.out.println("[" + LocalDateTime.now().format(formatter) + "] " + message);
    }
}
