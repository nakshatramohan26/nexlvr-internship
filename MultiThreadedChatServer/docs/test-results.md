# Test Plan and Recorded Results

## Honest scope statement

The results below were produced by actually running the code in a **Linux sandbox with OpenJDK 21.0.12**, compiled with `javac --release 11`. They have **not** been run on Windows, PowerShell or VS Code. You must repeat the tests on your own laptop (steps below) and keep your own screenshots as evidence.

## A. Automated test (`test/ChatServerTest.java`)

It starts a real `ChatServer` on a free port and connects real TCP clients. No external libraries are needed.

```powershell
javac -d out src\ChatServer.java src\ClientHandler.java src\ChatClient.java test\ChatServerTest.java
java -cp out ChatServerTest
```

Result observed in the sandbox: **21 passed, 0 failed** (exit code 0).

| # | Requirement | Automated check | Observed |
|---|---|---|---|
| 1 | Server starts, one client connects | welcome message received | PASS |
| 2 | Multiple clients connect; join notification | Bob and Carol join; Alice (and Bob) are told | PASS (6 checks) |
| 3 | Broadcast | Alice's message reaches Bob and Carol | PASS |
| 4 | Disconnected client removed | after `/quit`, others told, active count 3 -> 2 | PASS |
| 5 | Others keep talking after a disconnect | Carol <-> Alice messages | PASS |
| 6 | Abrupt disconnect (socket closed, no `/quit`) | others told, count 2 -> 1, server alive | PASS |
| 7 | Duplicate name | rejected, count unchanged | PASS |
| 8 | Server survives failures | new client accepted afterwards; count returns to 0 | PASS |
| 9 | Connection failure | connecting to a closed port gives `ConnectException` | PASS |

Compilation without errors (requirement 9) was also confirmed: `javac --release 11 -Xlint:all` produced no warnings or errors.

## B. Live run with the real programs

One `ChatServer` and three real `ChatClient` processes (Alice, Bob, Carol) were started, with input fed from pipes. The sequence was: all three connect, each sends one message, Bob types `/quit`, Alice and Carol exchange another message. Then a second server was started on the same port, and a client was started against a port with no server.

Server console (actual output):

```
[SERVER] Chat server started on port 5077. Waiting for clients...
[SERVER] New connection from /127.0.0.1:47208
[SERVER] Registered 'Alice'. Active clients: 1
[SERVER] New connection from /127.0.0.1:47222
[SERVER] Registered 'Bob'. Active clients: 2
[SERVER] New connection from /127.0.0.1:47228
[SERVER] Registered 'Carol'. Active clients: 3
[SERVER] Alice: Hi from Alice
[SERVER] Bob: Bob here, hello!
[SERVER] Carol: Carol checking in
[SERVER] Removed 'Bob'. Active clients: 2
[SERVER] Bob disconnected.
[SERVER] Alice: Alice: Bob left, can you still see me?
[SERVER] Carol: Carol: yes, I can
[SERVER] Removed 'Alice'. Active clients: 1
[SERVER] Alice disconnected.
[SERVER] Removed 'Carol'. Active clients: 0
[SERVER] Carol disconnected.
```

(The doubled "Alice: Alice:" is only because the test input itself started with "Alice:".)

Alice's console (actual output):

```
Connected to localhost:5077 as 'Alice'.
[Server] Welcome, Alice! There are 1 user(s) online. Type /quit to leave.
[Server] Bob joined the chat.
[Server] Carol joined the chat.
Bob: Bob here, hello!
Carol: Carol checking in
[Server] Bob left the chat.
Carol: Carol: yes, I can
Disconnected from server.
```

Port already in use (second server on the same port):

```
[SERVER] Server error: Address already in use (is port 5077 already in use? Try another port.)
```

Client with no server running:

```
Could not connect to localhost:5999 - Connection refused
Make sure the server is running and the port is correct.
```

## C. Manual test on your laptop (what you must run and screenshot)

Follow [demo-script.md](demo-script.md). Tick each item:

- [ ] Project compiles with no errors.
- [ ] Server prints "Chat server started on port 5000".
- [ ] Client A, B and C each connect and see a welcome message.
- [ ] Existing clients see "X joined the chat."
- [ ] A message typed in one client appears in the other two (but not echoed to the sender).
- [ ] Client B types `/quit`; the others see "Bob left the chat." and the server logs "Removed 'Bob'".
- [ ] Remaining clients still exchange messages.
- [ ] Closing a client window abruptly (click the trash-can icon on its terminal) does not crash the server; the server logs the removal.
- [ ] Starting the server on a busy port prints a clear error; a different port works.
- [ ] Starting a client when no server is running prints "Could not connect".
- [ ] Run the automated test (`ChatServerTest`) and see "21 passed, 0 failed".

## Not covered by any test

Heavy load (hundreds of clients), slow or stalled clients, very long messages, and network failures other than a closed connection are untested.
