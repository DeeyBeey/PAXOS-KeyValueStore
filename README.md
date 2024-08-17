# Distributed Systems Project #4

This project implements a multithreaded key-value store using RPC (Java RMI) along with replication across mutliple distinct servers ensuring fault tolerance using PAXOS consensus algorithm. The project includes the following components:

1. `Proposer` - Interface for the role of Proposer in PAXOS.
2. `ProposerImpl` - Implementation of the `Proposer` interface.
3. `Acceptor` - Interface for the role of Acceptor in PAXOS.
4. `AcceptorImpl` - Implementation of the `Acceptor` interface.
5. `Learner` - Interface for the role of Learner in PAXOS.
6. `LearnerImpl` - Implementation of the `Learner` interface.
7. `FailureSimulator` - Utility class to simulate failure and restart RMI objects.
8. `LoggerConfig` - Logger configuration for the PAXOS roles.
9. `RMI Client` - RMI Client implementation to interact with the coordinator.  

## Prerequisites

Ensure that you have the Java Development Kit (JDK) installed on your system. You can download it from [here](https://www.oracle.com/java/technologies/javase-downloads.html).

## Compilation
To compile the Java files, open a terminal or command prompt and navigate to the directory containing the source files. Use the following command to compile each file:

```
javac *.java
```

## Running the Server
To start the RMI server, use the following command. Replace `<port>` with the port number you wish to use (e.g., 32000).

```
java RMIServer <port>
```

## Running the Client
To start the RMI client, use the following command. Replace `<hostname>` with the server's hostname or IP address (e.g., localhost), and `<port>` with the same port number used for the server.

``` 
java RMIClient <hostname> <port>
```

## Example Usage
Here is an example of how to run the server and the client.

### Starting the RMI Server
```
java RMIServer 32000
```

### Running the RMI Client
```
java RMIClient localhost 32000
```

>Note: Ensure that the server is running first and then start the client. Use separate terminals or consoles for each client/server.