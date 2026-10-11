# Screenshots

This folder is for **your own real screenshots** taken while running the project on your laptop. No screenshots are included, and none should be edited or invented.

Save them as PNG with the file names below, then reference them in `README.md` and your internship report.

## Checklist

| # | File name | What it must show |
|---|---|---|
| 1 | `01-project-structure.png` | VS Code Explorer showing `src/`, `docs/`, `README.md` |
| 2 | `02-compile-success.png` | the `javac` command run with no errors (and `java -version` if possible) |
| 3 | `03-server-started.png` | Server terminal: "Chat server started on port 5000" |
| 4 | `04-three-clients-connected.png` | all four terminals visible: Server log with 3 registered clients, and the three welcome messages |
| 5 | `05-join-notifications.png` | Client A showing "Bob joined" and "Carol joined" |
| 6 | `06-broadcast-messages.png` | a message from each client appearing in the other clients' consoles |
| 7 | `07-client-quit.png` | Client B after `/quit`, others showing "Bob left the chat." |
| 8 | `08-server-cleanup-log.png` | Server log lines `Removed 'Bob'. Active clients: 2` |
| 9 | `09-continued-chat.png` | remaining clients still exchanging messages after Bob left |
| 10 | `10-port-in-use-error.png` | (optional) second server showing the "already in use" message |
| 11 | `11-connection-refused.png` | (optional) client showing "Could not connect" with no server |
| 12 | `12-automated-test.png` | `java -cp out ChatServerTest` ending in "21 passed, 0 failed" (only if it really does on your machine) |
| 13 | `13-architecture-diagram.png` | `docs/architecture.png` opened (the file is already provided) |
| 14 | `14-github-repo.png` | your GitHub repository page after pushing |

## Tips

- Use `Win + Shift + S` (Snipping Tool) to capture.
- Make the terminal font large; make sure the terminal tab names (`Server`, `Client A`...) are visible.
- Also record a screen video following `docs/demo-script.md`.
