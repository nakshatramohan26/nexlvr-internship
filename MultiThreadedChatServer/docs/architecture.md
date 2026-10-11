# Architecture

![Architecture diagram](architecture.png)

*Figure 1: Thread-per-client architecture. Vector version: [architecture.svg](architecture.svg). Graphviz source: [architecture.dot](architecture.dot).*

## Components

| Component | File | Runs on | Role |
|---|---|---|---|
| Chat Client | `src/ChatClient.java` | its own JVM; main thread + `receiver` thread | connects, sends typed lines, prints incoming lines |
| Chat Server (accept loop) | `src/ChatServer.java` | the server's **main thread** | `accept()` connections, start one handler thread each |
| Client handler | `src/ClientHandler.java` | **one thread per client** | read lines from its client, register / broadcast / clean up |
| Shared client list | `ChatServer.clients` + `clientsLock` | accessed by all handler threads | the set of connected clients, always accessed inside `synchronized (clientsLock)` |

## Message flow

1. **Connection request:** a client opens a TCP connection to the server port. The main thread's `accept()` returns a `Socket`.
2. **Spawn:** the main thread creates a `ClientHandler` for that socket, starts a new thread for it and returns to `accept()`.
3. **Chat traffic:** the same TCP connection carries lines from the client to its handler thread and broadcast text from the server back to the client (both directions).
4. **Shared state:** the handler registers its client in the shared list, and later broadcasts each received line and removes the client when it leaves (`finally` block, `cleanup()`).
5. **Broadcast:** `broadcast()` copies the list under the lock, then calls `sendMessage()` on every other client's handler, which writes to that client's socket.

## Mermaid source (editable, renders on GitHub)

```mermaid
flowchart LR
    subgraph Clients["Clients (ChatClient.java)"]
        A["Chat Client A"]
        B["Chat Client B"]
        C["Chat Client C"]
    end

    subgraph Server["Chat Server process (JVM), default port 5000"]
        Accept["MAIN THREAD<br/>accept loop: serverSocket.accept()<br/>then new Thread(handler).start()"]
        subgraph Handlers["Client-handler threads (ClientHandler.java)"]
            HA["Handler thread A<br/>readLine() loop"]
            HB["Handler thread B<br/>readLine() loop"]
            HC["Handler thread C<br/>readLine() loop"]
        end
        Shared[("SHARED STATE<br/>List of ClientHandler<br/>guarded by synchronized(clientsLock)<br/>registerClient / removeClient / broadcast")]
    end

    A -->|"1. connect request"| Accept
    B -->|"1. connect request"| Accept
    C -->|"1. connect request"| Accept

    Accept -.->|"2. spawn thread"| HA
    Accept -.->|"2. spawn thread"| HB
    Accept -.->|"2. spawn thread"| HC

    A <-->|"3. TCP socket: lines in, broadcasts out"| HA
    B <-->|"3. TCP socket"| HB
    C <-->|"3. TCP socket"| HC

    HA -->|"4. register / broadcast / remove (cleanup)"| Shared
    HB -->|"4. register / broadcast / remove (cleanup)"| Shared
    HC -->|"4. register / broadcast / remove (cleanup)"| Shared
```

## Disconnection sequence

```mermaid
sequenceDiagram
    participant C as Client B
    participant H as Handler thread B
    participant S as ChatServer (shared list)
    participant O as Other clients

    C->>H: "/quit" (or socket closes / IOException)
    H->>H: leave readLine() loop
    H->>S: removeClient(B) under clientsLock
    H->>O: broadcast "B left the chat."
    H->>C: socket.close()
    Note over H: handler thread ends
```

## Note on accuracy

The diagram shows what the code does: one main thread, one handler thread per client, and one shared list guarded by one lock. A handler delivers a broadcast by calling `sendMessage()` on the other handlers directly; there is no separate "broadcaster" thread or message queue.
