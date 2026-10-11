# Screenshot Checklist

**No screenshots are included in this folder yet.** You must capture them yourself by running the program on your own laptop.
Do not use screenshots taken by anyone else, and do not edit them – they are evidence for your NexLevr internship report.

Save the images in this folder using the suggested file names. Use `Win + Shift + S` (Snipping Tool) or `PrtScn` to capture.

Tips: make the VS Code and terminal text large enough to read, and include the terminal prompt line so the commands are visible.

| # | Screenshot to capture | What it must show | Suggested file name |
|---|---|---|---|
| 1 | Source files in VS Code | Explorer panel with `src` (`Student.java`, `StudentManager.java`, `Main.java`), `docs`, `README.md`, and one source file open in the editor | `01-source-files.png` |
| 2 | Successful compilation | Terminal showing `javac -d out src\*.java` with no errors, ideally also `dir out` or the `out` folder in Explorer | `02-compilation.png` |
| 3 | Main menu displayed | The title banner and menu options 1–6 after `java -cp out Main` | `03-main-menu.png` |
| 4 | Adding a student successfully | Add Student prompts filled in and the `[SUCCESS] Student added...` message | `04-add-student.png` |
| 5 | Viewing all students | Option 2 table showing at least two students | `05-view-all.png` |
| 6 | Searching for a student | Option 3 with an ID and the `[SUCCESS] Student found:` details | `06-search.png` |
| 7 | Updating a student | Option 4 showing current details, the new values and `[SUCCESS] ... updated` | `07-update.png` |
| 8 | Deleting a student | Option 5 with `[SUCCESS] Deleted student...` (and a follow-up View All if possible) | `08-delete.png` |
| 9 | Validation / error handling | At least a few of: invalid menu choice, duplicate ID, blank name, invalid email, invalid year, missing ID | `09-validation-errors.png` |
| 10 | Clean exit | Option 6, the `Goodbye!` message and the terminal prompt returning | `10-clean-exit.png` |

## Extra evidence worth saving for your report

- Automated test run: `java -cp out StudentTests` ending with `Tests passed: ... Tests failed: 0`.
- `docs/architecture-diagram.png` (the architecture diagram).
- Your GitHub repository page showing the committed files and README.
- The output of `git log --oneline`.

## After capturing

Link the images from the main `README.md` (the placeholders are already listed in section 17).
