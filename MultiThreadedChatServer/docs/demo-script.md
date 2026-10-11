# Demonstration Script (for your screen recording)

Total time: about 3 minutes. All commands are for **Windows PowerShell inside VS Code**.

## Before you record

1. Open the project folder in VS Code: `File > Open Folder...` and choose `MultiThreadedChatServer`.
2. Open the terminal: `Terminal > New Terminal` (or press ``Ctrl+` ``). Make sure it is PowerShell and the path ends in `MultiThreadedChatServer`.
3. Compile once:

```powershell
New-Item -ItemType Directory -Force out | Out-Null
javac -d out src\ChatServer.java src\ClientHandler.java src\ChatClient.java
```

No output means success.

4. Create **four terminals**. Click the `+` in the terminal panel three more times, then drag a terminal tab into the editor area or use the split button (`Ctrl+Shift+5`) so that all four are visible at once in a 2x2 grid. Rename them (right-click tab > Rename): `Server`, `Client A`, `Client B`, `Client C`.

Layout on screen:

| Server | Client A |
|---|---|
| **Client B** | **Client C** |

Zoom the terminal font up (`Ctrl+=`) so the text is readable in the recording.

## Recording steps

| Step | Terminal | Command / action | What should appear |
|---|---|---|---|
| 1 | Server | `java -cp out ChatServer` | `[SERVER] Chat server started on port 5000. Waiting for clients...` |
| 2 | Client A | `java -cp out ChatClient localhost 5000 Alice` | `Connected ... as 'Alice'` and a welcome line |
| 3 | Client B | `java -cp out ChatClient localhost 5000 Bob` | Welcome line for Bob. **Client A** shows `[Server] Bob joined the chat.` |
| 4 | Client C | `java -cp out ChatClient localhost 5000 Carol` | Welcome line for Carol. **A and B** show `Carol joined the chat.` Server log shows 3 active clients |
| 5 | Client A | type `Hello from Alice` + Enter | appears in **B and C** as `Alice: Hello from Alice` |
| 6 | Client B | type `Bob here, hi everyone` + Enter | appears in **A and C** |
| 7 | Client C | type `Carol joining the conversation` + Enter | appears in **A and B** |
| 8 | Client B | type `/quit` + Enter | B prints `Disconnected from server.`; **A and C** show `[Server] Bob left the chat.`; the Server log shows `Removed 'Bob'. Active clients: 2` |
| 9 | Client A | type `Bob left, can you still see me?` | appears in **C** |
| 10 | Client C | type `Yes, I can!` | appears in **A** |
| 11 | Client A, then C | `/quit` | Server log shows active clients reaching 0 |
| 12 | Server | press `Ctrl+C` | server stops |

Tip: the client prompt has no `>` marker; just type and press Enter. Messages you send are not echoed back to you; you will see them on the other clients.

## Optional extra scenes

- **Abrupt disconnect:** with clients running, close one client terminal using the trash-can icon. The server log should show `Removed '<name>'` and the others should see "left the chat".
- **Busy port:** start a second server with `java -cp out ChatServer` while the first is running. You should see `Address already in use ... Try another port.` Then use another port:

```powershell
java -cp out ChatServer 6000
java -cp out ChatClient localhost 6000 Alice
```

- **No server:** start `java -cp out ChatClient localhost 5999 Test` with nothing on 5999; you should see `Could not connect`.

## Windows notes

- On the first server start, Windows Defender Firewall may pop up. Allow **Private networks**. Connections to `localhost` normally work either way.
- To see who is using port 5000: `netstat -ano | findstr :5000`. To stop that process: `taskkill /PID <pid> /F`.
- If `javac` is not recognized, install a JDK (11 or newer) and reopen VS Code so the PATH refreshes. Check with `java -version` and `javac -version`.
- If you do not pass a name, the client asks `Enter your name:`.
