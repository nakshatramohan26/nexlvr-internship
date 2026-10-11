# Concurrency Model

This document explains how the chat server handles many clients at the same time. Everything here describes the code in `src/ChatServer.java`, `src/ClientHandler.java` and `src/ChatClient.java`. The server uses **plain `synchronized` blocks on one lock object** and nothing else (no `ConcurrentHashMap`, no `ExecutorService`, no `java.util.concurrent` locks).

## 1. What concurrency means in this project

Concurrency means several things are "in progress" at once. In this project:

- the server is waiting for new people to connect,
- the server is waiting for Alice to type something,
- the server is waiting for Bob to type something,
- the server is delivering a message to Carol.

A single thread executes one instruction at a time, so we give the server several threads. Each thread follows its own path through the code, and the Java scheduler switches between them (or runs them truly in parallel on different CPU cores).

## 2. Why the server must handle clients simultaneously

Reading from a socket (`BufferedReader.readLine()`) is a **blocking** call: the thread stops and waits until the client sends a line. If the server had only one thread, it would wait for Alice forever and could never accept Bob or read Carol's messages. A chat server therefore needs to wait for every client at the same time.

## 3. The main thread: accepting connections

`ChatServer.start()` runs on the program's **main thread**. It does only two things in an endless loop:

```java
Socket clientSocket = serverSocket.accept();      // blocks until a client connects
new Thread(new ClientHandler(clientSocket, this), "client-handler-" + port).start();
```

The main thread never reads chat messages. Because it hands each connection off immediately and goes back to `accept()`, new clients are never kept waiting by a slow or silent existing client.

## 4. Client-handler threads: one per client

This is the **thread-per-client** design. For every accepted connection a new `Thread` runs `ClientHandler.run()`. That thread:

1. reads the client's name (first line),
2. registers the client in the shared list (`registerClient`),
3. sends a welcome message and announces the join to the others,
4. loops on `readLine()`, broadcasting every line it reads,
5. in a `finally` block, runs `cleanup()` when the client leaves or the connection breaks.

| | Main thread | Client-handler thread |
|---|---|---|
| Count | exactly 1 | 1 per connected client |
| Created by | the JVM | the main thread (`new Thread(...).start()`) |
| Blocks on | `serverSocket.accept()` | `in.readLine()` |
| Job | accept connections | serve one client |
| Touches shared list? | no (it only creates handlers) | yes (register, broadcast, remove) |

The client program (`ChatClient`) also uses two threads: its main thread reads the keyboard and sends, and a `receiver` thread prints what the server sends. They are separate because both operations block.

## 5. How shared client connections are stored

All connected clients live in one list inside `ChatServer`:

```java
private final List<ClientHandler> clients = new ArrayList<>();
private final Object clientsLock = new Object();
```

`ArrayList` is **not thread-safe** by itself. Many handler threads use this one list, so it is **shared mutable state**. Rule used in the code: *every* read or write of `clients` happens inside `synchronized (clientsLock) { ... }`. This is done in `registerClient`, `removeClient`, `getClientCount` and `broadcast`.

## 6. How synchronization prevents conflicts

`synchronized (clientsLock)` allows only **one thread at a time** to run the block. A second thread that reaches it waits until the first one leaves the block. Therefore:

- two clients joining at the same instant cannot corrupt the list,
- a join and a leave cannot overlap,
- the duplicate-name check and the `add` in `registerClient` happen as **one indivisible step**, so two clients cannot both claim the name "Alice" (test 7 in `test/ChatServerTest.java` checks the duplicate case, though it does not create the exact simultaneous race).

The lock is held only for short operations (looking through or changing the list), never while doing network I/O.

## 7. How broadcasting works

`ChatServer.broadcast(message, sender)` is called by a handler thread when its client sends a line:

```java
List<ClientHandler> snapshot;
synchronized (clientsLock) {
    snapshot = new ArrayList<>(clients);   // 1. copy the list while holding the lock
}
for (ClientHandler client : snapshot) {    // 2. send OUTSIDE the lock
    if (client != sender) client.sendMessage(message);
}
```

Why copy first? If we iterated over `clients` directly while another thread removed a client, Java would throw `ConcurrentModificationException`. Iterating over a private copy cannot fail that way. Sending outside the lock means one slow connection cannot stop other threads from joining or leaving.

`ClientHandler.sendMessage` calls `PrintWriter.println`, and `PrintWriter` synchronizes its methods internally, so two threads writing to the same client at the same time do not mix the characters of a single line. (Two messages can still arrive in either order; there is no global message ordering.)

The sender is skipped, so you do not see your own message echoed back. Join and leave notices use the same method.

## 8. Disconnection and cleanup

A client can leave in three ways:

1. **Graceful:** types `/quit`. The handler's loop ends.
2. **Client closes or is killed:** `readLine()` returns `null`. The loop ends.
3. **Network error:** `readLine()` throws `IOException`; it is caught and logged.

In all cases the `finally` block runs `cleanup()` on that client's own thread:

1. `server.removeClient(this)`: removes the entry from the shared list under the lock (safe to call twice, because `List.remove` simply returns false the second time).
2. If the client had registered, broadcast "`<name> left the chat.`" to the others.
3. `socket.close()`.

The reader and writer are closed by try-with-resources. Because removal is in `finally`, a handler cannot exit without trying to remove its entry, which prevents stale entries from accumulating. If a *write* to a dead client fails, `PrintWriter` does not throw; `sendMessage` checks `checkError()` and logs. The dead client is then removed when its own handler thread notices the broken connection.

## 9. What can go wrong without protection

If the `synchronized` blocks were removed:

- **`ConcurrentModificationException`:** one thread iterates the list while another adds or removes a client; the broadcasting thread crashes.
- **Lost or corrupted updates:** two threads modify an `ArrayList` simultaneously, so a client could be silently missing, or the internal array could be left in an inconsistent state.
- **Duplicate names:** two clients check "is the name free?" at the same moment, both see yes, both are added.
- **Stale entries / wrong counts:** the "users online" number could be wrong, and messages would be sent to disconnected clients.

These bugs are **race conditions**: they depend on timing, so they appear rarely, which makes them hard to reproduce and debug. That is why the project uses one consistent rule (always lock `clientsLock` before touching `clients`).

## 10. Limitations of thread-per-client and future improvements

Limitations of this implementation:

- **One thread per client:** each thread uses memory (stack). Thousands of clients would exhaust resources; there is no limit on the number of threads.
- **Blocking writes:** if a client stops reading, its TCP buffer can fill and `println` may block the thread that is broadcasting to it, delaying that sender.
- **No read timeouts or heartbeats:** a client that vanishes without closing the connection may only be noticed much later.
- **No message-length limit and no input validation:** a client could send a very long line. A client can also pick a name like "Server".
- **No authentication or encryption:** anyone who can reach the port can join, and traffic is plain text.
- **No message history or ordering guarantee** between different senders.
- The server has no graceful shutdown command; stop it with `Ctrl+C`.

Possible improvements (not implemented):

- an `ExecutorService` thread pool, or Java 21 virtual threads, to cap or cheapen threads,
- a per-client outgoing message queue with its own writer thread, so slow clients cannot block broadcasts,
- `CopyOnWriteArrayList` or `ConcurrentHashMap` for the client collection,
- socket read timeouts (`setSoTimeout`) and heartbeat messages,
- `java.nio` (selectors) or a framework such as Netty for very large numbers of clients,
- TLS (`SSLServerSocket`) and a login step.
