import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * LoggerConfig is a helper class to implement loggers for the Proposer, Acceptor and
 * Learner.
 */
public class LoggerConfig {

    /**
     * A Logger instance is created along with a file handler. 
     * 
     * @param name The name of the log file.
     * @return The instance of the configured logger.
     */
    public static Logger createLogger(String name) {
        Logger logger = Logger.getLogger(name);
        try {
            FileHandler fileHandler = new FileHandler(name + ".log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setUseParentHandlers(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return logger;
    }
}
