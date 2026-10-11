# StudentCRUDConsoleApp

A pure Java console application that manages student records using an `ArrayList`.
It performs full **CRUD** (Create, Read, Update, Delete) through a menu-driven interface and demonstrates
object-oriented design, input validation and exception handling.

> **Important:** records are stored **in memory only** (in an `ArrayList<Student>`).
> **Nothing is saved to a file or database** – all records are lost when the program exits.

## 1. Project objective

**Task 1 – Student CRUD Console App (NexLevr Java Development Internship).**
Build a beginner-friendly Java application with separate `Student` and `StudentManager` classes, validated input,
safe error handling and a clear console menu – using plain Java only (no Spring Boot, database, web API, GUI or external libraries).

## 2. Student and internship details

| Item | Details |
|---|---|
| Student | Nakshatra M |
| Degree | B.Tech. Information Technology |
| Year | II Year |
| College | R.M.K. Engineering College |
| Internship organization | NexLevr |
| Internship track | Java Development |
| Task | Task 1 – Student CRUD Console App |
| IDE / OS | Visual Studio Code on Windows |

## 3. Features

- Add a student (ID, name, email, department, year of study).
- View all students in an aligned table.
- Search a student by ID.
- Update an existing student (the ID itself cannot be changed).
- Delete a student by ID.
- Duplicate student IDs are rejected.
- Validation for ID, name, email, department and year, identical for Add and Update.
- Friendly `[SUCCESS]`, `[ERROR]` and `[INFO]` messages – no stack traces shown to the user.
- Safe input: invalid menu choices and non-numeric input never crash the program.
- Clear messages when the list is empty or an ID does not exist.
- Dependency-free automated tests (81 checks) plus a manual testing guide.

## 4. Technologies used

- Java (standard library only: `ArrayList`, `Scanner`, `regex`)
- Visual Studio Code, Windows PowerShell
- Git and GitHub
- Mermaid (architecture diagram)

No Maven, Gradle, Spring Boot, database or external libraries.

## 5. Project structure

```text
StudentCRUDConsoleApp/
├── src/
│   ├── Student.java          # one student record + validation rules
│   ├── StudentManager.java   # ArrayList storage + CRUD operations
│   └── Main.java             # console menu and safe input
├── test/
│   └── StudentTests.java     # dependency-free automated tests (81 checks)
├── docs/
│   ├── architecture.md       # Mermaid diagram + component explanation
│   ├── architecture-diagram.png / .svg / .mmd
│   ├── testing-guide.md      # step-by-step manual demonstration
│   ├── sample-session.txt    # real recorded console session
│   ├── sample-input.txt      # the keystrokes used for that session
│   └── linkedin-post.md      # LinkedIn post draft
├── screenshots/
│   └── README.md             # checklist of screenshots you must capture yourself
├── README.md
└── .gitignore
```

The `out/` folder (compiled `.class` files) is created when you compile and is ignored by Git.
There are no `package` declarations, so all files sit directly in `src` and the commands stay simple.
The `test` folder is an addition to the suggested structure so the automated tests have a home.

## 6. Prerequisites

- JDK installed (the project was built and tested with OpenJDK 21; it only uses basic features that exist in older JDKs too).
- Visual Studio Code (the *Extension Pack for Java* is optional but helpful).
- Windows PowerShell (the integrated terminal in VS Code).

Check Java in the terminal:

```powershell
java -version
javac -version
```

Both commands must print a version number. If `javac` is not recognized, see *Troubleshooting* below.

## 7. Setup and execution (VS Code on Windows)

### 7.1 Open the project in VS Code

1. Extract the project (or create the folder `StudentCRUDConsoleApp` yourself).
2. In VS Code: **File → Open Folder…** and choose `StudentCRUDConsoleApp`.
3. Open the integrated terminal: **Terminal → New Terminal** (shortcut `` Ctrl + ` ``).

Or from PowerShell:

```powershell
cd C:\path\to\StudentCRUDConsoleApp
code .
```

### 7.2 Creating the files yourself (only if you did not download the project)

```powershell
mkdir StudentCRUDConsoleApp
cd StudentCRUDConsoleApp
mkdir src, test, docs, screenshots
New-Item src\Student.java, src\StudentManager.java, src\Main.java -ItemType File
code .
```

Paste the contents of each file into the matching file and save (`Ctrl + S`).
File names must match the class names exactly (`Student.java` contains `public class Student`).

### 7.3 Compile (from the project root)

```powershell
javac -d out src\*.java
```

No output means success. If your shell does not expand `*.java`, use:

```powershell
javac -d out src\Student.java src\StudentManager.java src\Main.java
```

### 7.4 Run

```powershell
java -cp out Main
```

### 7.5 Run the automated tests

```powershell
javac -cp out -d out test\StudentTests.java
java -cp out StudentTests
```

Expected last lines: `Tests passed: 81   Tests failed: 0`.

### 7.6 Troubleshooting

| Problem | Cause and fix |
|---|---|
| `'javac' is not recognized` (or `javac : The term 'javac' is not recognized`) | Only a JRE is installed, or the JDK `bin` folder is not on `PATH`. Install a full **JDK**, add its `bin` folder to the `Path` environment variable, then **close and reopen VS Code** and run `javac -version` again. |
| `java -version` works but `javac -version` does not | You have a JRE (runtime only). Install a JDK. |
| `error: no source files` | You are in the wrong folder. Run `pwd` (or `Get-Location`); you must be in the folder that contains `src`. Use `cd` to move there. |
| `error: file not found: src\*.java` | Same cause – wrong directory, or the files are not in `src`. |
| `Error: Could not find or load main class Main` | You ran `java Main` from the wrong place, or you did not compile. Compile first, then run `java -cp out Main` **from the project root**. |
| `class Student is public, should be declared in a file named Student.java` | The file name and class name differ (check spelling and capital letters). |
| `cannot find symbol` | A file was not saved, or a method name is misspelled. Save all files (`Ctrl + K S`) and recompile. |
| Strange characters in output | The program only prints plain ASCII, so this should not happen. If it does, run `chcp 65001` in the terminal and retry. |

## 8. Menu options

| Option | Action |
|---|---|
| 1 | Add Student |
| 2 | View All Students |
| 3 | Search Student by ID |
| 4 | Update Student |
| 5 | Delete Student |
| 6 | Exit |

The menu is shown again after every action until you choose **6**.

## 9. How the CRUD operations work

| Operation | Menu | `StudentManager` method | What happens |
|---|---|---|---|
| **Create** | 1 | `addStudent(Student)` | Checks the ID is not already used (`idExists`), then `students.add(student)`. Returns `false` for a duplicate. |
| **Read (all)** | 2 | `displayAllStudents()` | Prints a table, or `[INFO] No student records found...` when empty. |
| **Read (one)** | 3 | `findStudentById(int)` | Searches the list for the ID. Returns the student, or `null` if missing. |
| **Update** | 4 | `updateStudent(id, name, email, dept, year)` | Finds the position of the ID, builds a **new validated** `Student` with the same ID, and replaces the old one in the same position with `students.set(index, updated)`. If a value is invalid, an exception is thrown **before** anything changes, so the old record stays intact. |
| **Delete** | 5 | `deleteStudent(int)` | Finds the position of the ID and calls `students.remove(index)`. Returns `false` if the ID is missing. |

Because every update and delete first looks up the position **by ID**, the correct student is always affected (verified by automated tests A16–A26).

## 10. OOP concepts used

### Class and object
A **class** is a blueprint; an **object** is one thing built from it. `Student` is the blueprint for a student record.
`new Student(101, "Asha Rao", "asha@example.com", "Information Technology", 2)` creates one object – one record.
`StudentManager` is also a class; the program creates one `StudentManager` object that owns the list.

### Encapsulation
The fields of `Student` are `private`, so other classes cannot change them directly:

```java
private final int studentId;
private String name;
private String email;
private String department;
private int year;
```

Outside code must use public methods (getters and setters), and those methods validate the data. That is how the class protects itself from bad values.

### Constructors
A constructor runs when you write `new Student(...)`. Ours validates every value, so an invalid `Student` object can never be created:

```java
public Student(int studentId, String name, String email, String department, int year) {
    this.studentId = validateId(studentId);
    this.name = validateName(name);
    ...
}
```

### Getters and setters
Getters (`getName()`, `getEmail()`, …) let other classes read a field. Setters (`setName(...)`, `setEmail(...)`, …) change a field **after validating it**.
There is deliberately **no `setStudentId`** and the ID is `final`: an ID identifies the record, so it must never change.

### Responsibility of `Student`
Represent **one** student and know what a valid student looks like (all validation rules live here). It does not read the keyboard and does not store lists.
Two `Student` objects are considered the same record when their IDs are equal (`equals` and `hashCode` use the ID).

### Responsibility of `StudentManager`
Own the `ArrayList<Student>` and perform Create, Read, Update and Delete. It rejects duplicate IDs and reports missing IDs. It does not read the keyboard.

### Why `ArrayList` is suitable here
- The data set is small, so a simple list is enough.
- It keeps students in the order they were added.
- It grows automatically, and `add`, `get`, `set` and `remove` are easy to understand and explain.
- Searching by ID is a simple loop over the list. That is fast for a small list; for thousands of records a `HashMap<Integer, Student>` or a database would be better (see *Future improvements*).

### How `Main` interacts with `StudentManager`
`Main` creates one `StudentManager` and calls its methods. For example, Add Student does this:

```java
int id = readId(...);                   // Main reads and validates the ID text
if (manager.idExists(id)) { ... }       // asks the manager about duplicates
...
Student student = new Student(id, name, email, department, year);
if (manager.addStudent(student)) { ... }  // manager stores it
```

`Main` talks to the user; `StudentManager` handles the data. That separation is the main design idea of the project.

## 11. Validation and exception handling

| Input | Rule | Where checked |
|---|---|---|
| Student ID | whole number from **1 to 999999**; empty, text, decimals, zero, negatives and huge numbers are rejected | `Student.parseId` / `validateId` |
| Duplicate ID | not allowed | `Main` (early check) and `StudentManager.addStudent` |
| Name | not blank; 2–50 characters; starts with a letter; letters, spaces, dots, hyphens, apostrophes only | `Student.validateName` |
| Email | basic format `something@domain.tld`, no spaces, max 100 characters | `Student.validateEmail` |
| Department | not blank; 2–50 characters; starts with a letter; letters, spaces, dots, `&`, hyphens | `Student.validateDepartment` |
| Year of study | whole number **1 to 4** (a B.Tech programme lasts 4 years) | `Student.parseYear` / `validateYear` |
| Menu choice | must be exactly `1` to `6` | `Main.handleMenuChoice` |
| Missing ID (search, update, delete) | clear `[ERROR] No student found...` message | `StudentManager` returns `null` / `false`; `Main` prints the message |
| Empty list | clear `[INFO]` message for view, search, update, delete | `StudentManager` and `Main` |
| Unexpected errors | friendly message, no stack trace | final `catch` in `Main` |
| Input stream ends (Ctrl+Z) | program exits cleanly with a message | `InputClosedException` in `Main` |

How exceptions are used: invalid **data** throws `IllegalArgumentException` (with a readable message) from `Student`;
`Main` catches it **at the input boundary**, prints the message and asks again. Normal "failures" such as a duplicate or missing ID use `true`/`false`/`null` return values instead of exceptions, which keeps the code easy to follow.
Validation rules are in one place (`Student`), so Add and Update are always consistent.

**Scanner note:** all input is read with `nextLine()` and converted by our own code. We never use `nextInt()`, which avoids the well-known problem where the leftover newline makes the next `nextLine()` return an empty string.

## 12. Architecture overview

Flow: **User → Main (Console Menu) → StudentManager → ArrayList&lt;Student&gt; → Student records**

![Architecture diagram](docs/architecture-diagram.png)

The Mermaid source and a plain-English explanation of each component are in [`docs/architecture.md`](docs/architecture.md).
There is no database, server, REST API or GUI.

## 13. Testing

- **Automated tests:** `test/StudentTests.java` (no libraries). Run the commands in section 7.5.
- **Manual demonstration:** follow [`docs/testing-guide.md`](docs/testing-guide.md).

| Check | How it is covered |
|---|---|
| Compiles without errors | `javac -d out src\*.java` |
| Add, display, find, update, delete | Tests A05–A26, B01–B07 and manual steps 3–7 |
| Duplicate IDs, blank names, invalid emails, invalid years | Tests A09, A27–A41, B11–B16 |
| Non-numeric input, missing IDs, empty list | Tests A42–A49, B09, B12, B19–B25 |
| Clean exit | Tests B07, B26, B27 |

**What was actually executed:** during development the project was compiled and all **81 automated checks passed (0 failed)**
using OpenJDK 21 in a Linux build environment. I also confirmed the tests *can* fail by temporarily breaking the code. The sample session below was recorded from a real run.
**What you must verify yourself:** compile and run on your own Windows laptop, and record your own test result and screenshots.
My result on my laptop: `____ passed / ____ failed` (fill in after running).

## 14. Sample console session

This is a **real recorded session** (run in the build environment, not on a laptop). Lines after a prompt are what was typed.
The complete file is `docs/sample-session.txt`.

```text
==============================================
      STUDENT CRUD CONSOLE APPLICATION
==============================================
Note: records are kept in memory only and are
lost when the program exits.

------------- MAIN MENU -------------
1. Add Student
2. View All Students
3. Search Student by ID
4. Update Student
5. Delete Student
6. Exit
-------------------------------------
Enter your choice (1-6): 2
[INFO] No student records found. The list is empty.

------------- MAIN MENU -------------
1. Add Student
2. View All Students
3. Search Student by ID
4. Update Student
5. Delete Student
6. Exit
-------------------------------------
Enter your choice (1-6): 3

--- Search Student by ID ---
[INFO] The student list is empty. Add a student first.

------------- MAIN MENU -------------
1. Add Student
2. View All Students
3. Search Student by ID
4. Update Student
5. Delete Student
6. Exit
-------------------------------------
Enter your choice (1-6): 1

--- Add Student ---
Enter Student ID (1-999999): 101
Enter Name: Asha Rao
Enter Email: asha@example.com
Enter Department: Information Technology
Enter Year of Study (1-4): 2
[SUCCESS] Student added: Asha Rao (ID 101)

------------- MAIN MENU -------------
1. Add Student
2. View All Students
3. Search Student by ID
4. Update Student
5. Delete Student
6. Exit
-------------------------------------
Enter your choice (1-6): 1

--- Add Student ---
Enter Student ID (1-999999): 102
Enter Name: Ravi Kumar
Enter Email: ravi@example.com
Enter Department: Computer Science
Enter Year of Study (1-4): 3
[SUCCESS] Student added: Ravi Kumar (ID 102)

------------- MAIN MENU -------------
1. Add Student
2. View All Students
3. Search Student by ID
4. Update Student
5. Delete Student
6. Exit
-------------------------------------
Enter your choice (1-6): 1

--- Add Student ---
Enter Student ID (1-999999): 103
Enter Name: Meena Iyer
Enter Email: meena@example.com
Enter Department: Electronics
Enter Year of Study (1-4): 1
[SUCCESS] Student added: Meena Iyer (ID 103)

------------- MAIN MENU -------------
1. Add Student
2. View All Students
3. Search Student by ID
4. Update Student
5. Delete Student
6. Exit
-------------------------------------
Enter your choice (1-6): 2

ID   Name        Email              Department              Year
----------------------------------------------------------------
101  Asha Rao    asha@example.com   Information Technology  2
102  Ravi Kumar  ravi@example.com   Computer Science        3
103  Meena Iyer  meena@example.com  Electronics             1
----------------------------------------------------------------
Total students: 3

------------- MAIN MENU -------------
1. Add Student
2. View All Students
3. Search Student by ID
4. Update Student
5. Delete Student
6. Exit
-------------------------------------
Enter your choice (1-6): 3

--- Search Student by ID ---
Enter Student ID to search: 102
[SUCCESS] Student found:
  ID         : 102
  Name       : Ravi Kumar
  Email      : ravi@example.com
  Department : Computer Science
  Year       : 3

------------- MAIN MENU -------------
1. Add Student
2. View All Students
3. Search Student by ID
4. Update Student
5. Delete Student
6. Exit
-------------------------------------
Enter your choice (1-6): 4

--- Update Student ---
Enter Student ID to update: 101
Current details:
  ID         : 101
  Name       : Asha Rao
  Email      : asha@example.com
  Department : Information Technology
  Year       : 2
Enter the new details (the ID cannot be changed).
Enter Name: Asha Rao
Enter Email: asha.rao@example.com
Enter Department: Information Technology
Enter Year of Study (1-4): 3
[SUCCESS] Student with ID 101 updated:
  ID         : 101
  Name       : Asha Rao
  Email      : asha.rao@example.com
  Department : Information Technology
  Year       : 3

------------- MAIN MENU -------------
1. Add Student
2. View All Students
3. Search Student by ID
4. Update Student
5. Delete Student
6. Exit
-------------------------------------
Enter your choice (1-6): 5

--- Delete Student ---
Enter Student ID to delete: 103
[SUCCESS] Deleted student: Meena Iyer (ID 103)

------------- MAIN MENU -------------
1. Add Student
2. View All Students
3. Search Student by ID
4. Update Student
5. Delete Student
6. Exit
-------------------------------------
Enter your choice (1-6): abc
[ERROR] Invalid choice 'abc'. Please enter a number from 1 to 6.

------------- MAIN MENU -------------
1. Add Student
2. View All Students
3. Search Student by ID
4. Update Student
5. Delete Student
6. Exit
-------------------------------------
Enter your choice (1-6): 9
[ERROR] Invalid choice '9'. Please enter a number from 1 to 6.

------------- MAIN MENU -------------
1. Add Student
2. View All Students
3. Search Student by ID
4. Update Student
5. Delete Student
6. Exit
-------------------------------------
Enter your choice (1-6): 1

--- Add Student ---
Enter Student ID (1-999999): 101
[ERROR] A student with ID 101 already exists. Duplicate IDs are not allowed.

------------- MAIN MENU -------------
1. Add Student
2. View All Students
3. Search Student by ID
4. Update Student
5. Delete Student
6. Exit
-------------------------------------
Enter your choice (1-6): 1

--- Add Student ---
Enter Student ID (1-999999): abc
[ERROR] Student ID must be a whole number between 1 and 999999.
Enter Student ID (1-999999): 201
Enter Name:    
[ERROR] Name cannot be empty or blank.
Enter Name: Nila S
Enter Email: nila-at-example.com
[ERROR] Invalid email format. Use something like name@example.com (no spaces).
Enter Email: nila@example.com
Enter Department: IT
Enter Year of Study (1-4): 7
[ERROR] Year of study must be a whole number between 1 and 4.
Enter Year of Study (1-4): two
[ERROR] Year of study must be a whole number between 1 and 4.
Enter Year of Study (1-4): 2
[SUCCESS] Student added: Nila S (ID 201)

------------- MAIN MENU -------------
1. Add Student
2. View All Students
3. Search Student by ID
4. Update Student
5. Delete Student
6. Exit
-------------------------------------
Enter your choice (1-6): 3

--- Search Student by ID ---
Enter Student ID to search: 999
[ERROR] No student found with ID 999.

------------- MAIN MENU -------------
1. Add Student
2. View All Students
3. Search Student by ID
4. Update Student
5. Delete Student
6. Exit
-------------------------------------
Enter your choice (1-6): 4

--- Update Student ---
Enter Student ID to update: 999
[ERROR] No student found with ID 999. Nothing was updated.

------------- MAIN MENU -------------
1. Add Student
2. View All Students
3. Search Student by ID
4. Update Student
5. Delete Student
6. Exit
-------------------------------------
Enter your choice (1-6): 5

--- Delete Student ---
Enter Student ID to delete: 999
[ERROR] No student found with ID 999. Nothing was deleted.

------------- MAIN MENU -------------
1. Add Student
2. View All Students
3. Search Student by ID
4. Update Student
5. Delete Student
6. Exit
-------------------------------------
Enter your choice (1-6): 2

ID   Name        Email                 Department              Year
-------------------------------------------------------------------
101  Asha Rao    asha.rao@example.com  Information Technology  3
102  Ravi Kumar  ravi@example.com      Computer Science        3
201  Nila S      nila@example.com      IT                      2
-------------------------------------------------------------------
Total students: 3

------------- MAIN MENU -------------
1. Add Student
2. View All Students
3. Search Student by ID
4. Update Student
5. Delete Student
6. Exit
-------------------------------------
Enter your choice (1-6): 6
[INFO] Exiting the application...
Goodbye!
```

## 15. Limitations

- Records are **not saved**; everything is lost when the program exits.
- No file or database persistence.
- Search is a linear scan (fine for small data, slow for very large lists).
- The email check is a basic format check; it does not confirm the address really exists.
- Names containing digits or unusual symbols are rejected.
- Year of study is limited to 1–4.
- Single user, text menu only, no delete confirmation prompt.
- Not production software – it is a learning project.

## 16. Future improvements

- Save and load records using a text/CSV file (file I/O).
- Replace the list search with a `HashMap<Integer, Student>` for faster lookup.
- Add a "keep current value" option on update (press Enter to skip a field).
- Add a delete confirmation (`y/n`) and sorting / searching by name.
- Convert the manual checks to JUnit tests.
- Later: connect to a database with JDBC.

## 17. Screenshot placeholders

Add your own screenshots to the `screenshots/` folder (see [`screenshots/README.md`](screenshots/README.md) for the full checklist) and link them here:

| Screenshot | Placeholder |
|---|---|
| Source files in VS Code | `![Source files](screenshots/01-source-files.png)` |
| Successful compilation | `![Compilation](screenshots/02-compilation.png)` |
| Main menu | `![Menu](screenshots/03-main-menu.png)` |
| Add student | `![Add](screenshots/04-add-student.png)` |
| View all | `![View all](screenshots/05-view-all.png)` |
| Search | `![Search](screenshots/06-search.png)` |
| Update | `![Update](screenshots/07-update.png)` |
| Delete | `![Delete](screenshots/08-delete.png)` |
| Validation / errors | `![Validation](screenshots/09-validation-errors.png)` |
| Clean exit | `![Exit](screenshots/10-clean-exit.png)` |

*(These are placeholders only – no screenshots have been added yet.)*

## 18. GitHub repository setup

Replace `<your-username>` with your own GitHub username. Run these commands in PowerShell from the project root.

```powershell
# 1. Initialize Git (once)
git init

# 2. Check which files Git sees (out/ should NOT appear because of .gitignore)
git status

# 3. Stage and commit
git add .
git commit -m "Add Student CRUD Console App (Task 1)"

# 4. Name the main branch
git branch -M main
```

If Git asks who you are (first time only):

```powershell
git config --global user.name "Your Name"
git config --global user.email "you@example.com"
```

Create the repository on GitHub: go to <https://github.com/new>, name it `StudentCRUDConsoleApp`, leave **"Add a README"** and **".gitignore"** unchecked (the project already has them), and click **Create repository**. Then connect and push:

```powershell
git remote add origin https://github.com/<your-username>/StudentCRUDConsoleApp.git
git push -u origin main
```

Repository URL (fill in after you create it): `https://github.com/<your-username>/StudentCRUDConsoleApp`

If GitHub asks you to sign in, use the browser sign-in window or a personal access token – GitHub no longer accepts account passwords for `git push`.
