# Multi-threaded Java Chat Server

**NexLevr Java Development Internship, Task 3**
Author: Nakshatra M, B.Tech. Information Technology (II Year), R.M.K. Engineering College

> Educational demonstration project. It is **not production-ready**: there is no authentication and no encryption, and messages travel as plain text.

## Objective

Build a console chat application using Java TCP sockets and multithreading, where many clients connect to one server at the same time and exchange messages, and document the concurrency model.

## Features

- Server listening on a configurable port (default 5000)
- Many clients connected concurrently
- Thread-per-client design: one `ClientHandler` thread for each client
- Broadcast: a message from one client is delivered to all the others
- Join and leave notifications
- Graceful `/quit` and handling of abrupt disconnects
- Duplicate-name rejection
- Resource cleanup (sockets, readers, writers) with try-with-resources and `finally`
- Shared client list protected with `synchronized`

## Technologies

Java (compiles with `--release 11`, tested on JDK 21), `java.net` sockets, `java.lang.Thread`, `synchronized`. No frameworks, no external dependencies.

## Project structure

```
MultiThreadedChatServer/
├── src/
│   ├── ChatServer.java        main thread: accept loop + shared client list
│   ├── ClientHandler.java     one thread per client
│   └── ChatClient.java        console client (2 threads)
├── test/
│   └── ChatServerTest.java    automated test, no external libraries
├── docs/
│   ├── architecture.md        diagram + Mermaid source
│   ├── architecture.png / .svg / .dot
│   ├── concurrency-model.md   beginner-friendly concurrency explanation
│   ├── demo-script.md         step-by-step recording script
│   └── test-results.md        test plan and recorded results
├── screenshots/README.md      screenshot checklist (add your own images)
├── README.md
└── .gitignore
```

Changes from the suggested layout: added `test/` and extra files in `docs/`.

## Architecture overview

![Architecture](docs/architecture.png)

The server's **main thread** only calls `accept()`. Each accepted connection gets its own **client-handler thread** that reads that client's messages. All handlers share one list of connected clients, protected by a lock. Details: [docs/architecture.md](docs/architecture.md).

## Concurrency model (summary)

- **Main thread:** `serverSocket.accept()` in a loop; starts a new thread per connection.
- **Handler threads:** block on `readLine()`, so a silent client never stops others.
- **Shared data:** `List<ClientHandler> clients`, accessed only inside `synchronized (clientsLock)`.
- **Broadcast:** copy the list under the lock, then send to each client outside the lock (no `ConcurrentModificationException`, and a slow client cannot block joins or leaves).
- **Cleanup:** a `finally` block removes the client from the list, announces the departure and closes the socket.

Full explanation: [docs/concurrency-model.md](docs/concurrency-model.md).

## Prerequisites

- JDK 11 or newer (check with `java -version` and `javac -version`)
- VS Code (any terminal works)
- Git (only for the GitHub step)

## Build and run (Windows PowerShell)

Open the folder in VS Code (`File > Open Folder...`), then open a terminal (``Ctrl+` ``) in the project root.

```powershell
# compile
New-Item -ItemType Directory -Force out | Out-Null
javac -d out src\ChatServer.java src\ClientHandler.java src\ChatClient.java

# terminal 1: server (default port 5000)
java -cp out ChatServer

# terminals 2, 3, 4: clients
java -cp out ChatClient localhost 5000 Alice
java -cp out ChatClient localhost 5000 Bob
java -cp out ChatClient localhost 5000 Carol
```

Leave out the name (`java -cp out ChatClient`) and the client asks for it. Arguments are `[host] [port] [name]`.

### Changing the port

```powershell
java -cp out ChatServer 6000
java -cp out ChatClient localhost 6000 Alice
```

If port 5000 is busy the server prints `Address already in use`. Find the process with `netstat -ano | findstr :5000`.

### Running multiple clients

Open one VS Code terminal per client (click `+` in the terminal panel, or split with `Ctrl+Shift+5`) and run the client command in each. Type a message and press Enter; type `/quit` to leave. To connect from another computer on the same network, start clients with the server machine's IP address instead of `localhost` (you may need to allow Java through the Windows firewall).

## Sample usage (illustrative; derived from a real run on Linux, see docs/test-results.md)

```
Connected to localhost:5000 as 'Alice'.
[Server] Welcome, Alice! There are 1 user(s) online. Type /quit to leave.
[Server] Bob joined the chat.
Bob: Bob here, hello!
[Server] Bob left the chat.
```

## Testing

Automated test (starts a real server and real sockets):

```powershell
javac -d out src\ChatServer.java src\ClientHandler.java src\ChatClient.java test\ChatServerTest.java
java -cp out ChatServerTest
```

Expected final line: `Result: 21 passed, 0 failed`. In the sandbox where this project was built, that result was observed on OpenJDK 21; it has not been run on Windows by the author of the code, so run it yourself. The manual test checklist and recorded results are in [docs/test-results.md](docs/test-results.md). The demo is in [docs/demo-script.md](docs/demo-script.md).

## Limitations

- Thread-per-client does not scale to very many clients; the number of threads is unbounded.
- A client that stops reading can block the thread that is sending to it.
- No authentication, encryption, message history, private messages or rate limiting.
- No read timeouts; no limit on message length; names are not validated beyond being unique.
- Plain-text protocol: the first line is the name, `/quit` leaves.

## Future improvements

Thread pool or virtual threads, per-client outgoing queues, socket timeouts and heartbeats, TLS and login, private messages and a `/users` command, `java.nio` for large scale. See [docs/concurrency-model.md](docs/concurrency-model.md).

## Screenshots

Add your own screenshots to `screenshots/` (checklist in [screenshots/README.md](screenshots/README.md)):

| Screenshot | Placeholder |
|---|---|
| Server started | `screenshots/03-server-started.png` |
| Three clients connected | `screenshots/04-three-clients-connected.png` |
| Broadcast messages | `screenshots/06-broadcast-messages.png` |
| Client disconnect | `screenshots/07-client-quit.png` |

## Preparing the GitHub repository

```powershell
git init
git add .
git status          # confirm out/ and .vscode/ are NOT listed
git commit -m "Add multi-threaded Java chat server"
git branch -M main
# create an empty repository on github.com first (no README), then:
git remote add origin https://github.com/<your-username>/MultiThreadedChatServer.git
git push -u origin main
```

The `.gitignore` excludes compiled `.class` files, `out/` and `.vscode/`. Do not commit passwords or tokens. If Git asks for credentials, sign in through the browser prompt or use a personal access token, and never put it in a file in the project.

## License / Use

Created for educational purposes during the NexLevr internship.
