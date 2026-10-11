# Testing and Demonstration Guide

All sample data is fictional and uses the placeholder domain `example.com`.

> **About the sample output in this guide**
> The output shown below was captured from a **real run** of this program in the build environment
> (Linux, OpenJDK 21), not on your laptop. Your Windows output should match it. When you run it yourself,
> compare your screen with these examples and take your own screenshots for your report.

Run every command from the **project root** (the folder that contains `src`, `docs` and `README.md`).

---

## Part 1 – Compile (scenario 1)

```powershell
javac -d out src\*.java
```

**Expected:** no output at all. A `out` folder appears containing `Main.class`, `Student.class`, `StudentManager.class` and `Main$InputClosedException.class`.
If you see errors, read the troubleshooting section in `README.md`.

If `src\*.java` is not accepted by your shell, list the files explicitly:

```powershell
javac -d out src\Student.java src\StudentManager.java src\Main.java
```

---

## Part 2 – Automated tests (no external libraries)

```powershell
javac -cp out -d out test\StudentTests.java
java -cp out StudentTests
```

**Expected:** every line starts with `PASS`, and the last lines are:

```text
=====================================
Tests passed: 81   Tests failed: 0
=====================================
```

*(That is the result observed in the build environment. Record your own result from your laptop.)*

The tests cover Part A (direct tests of `Student` and `StudentManager`: add, find, update, delete, duplicates, invalid name/email/year/ID, empty list, update/delete affect the correct student) and Part B (the real `Main` menu driven by scripted keyboard input: success messages, invalid menu choices, re-prompting, missing IDs, empty-list messages, input ending, no stack traces, data not kept between runs).

---

## Part 3 – Manual demonstration (step by step)

Start the app:

```powershell
java -cp out Main
```

Type the **bold input values**; press Enter after each one.

### Step 1 – Start the application (scenario 14 starts here)

You should see the title, the in-memory note and the menu:

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
Enter your choice (1-6):
```

### Step 2 – Empty-list behaviour (scenario 13)

Choose **2**, then **3**.

```text
Enter your choice (1-6): 2
[INFO] No student records found. The list is empty.
...
Enter your choice (1-6): 3

--- Search Student by ID ---
[INFO] The student list is empty. Add a student first.
```

Options 4 (Update) and 5 (Delete) also give clear messages on an empty list:
`[INFO] The student list is empty. There is nothing to update.` and `... There is nothing to delete.`

### Step 3 – Add two or more students (scenario 2)

Choose **1** and enter:

| Prompt | Student 1 | Student 2 | Student 3 |
|---|---|---|---|
| Student ID | **101** | **102** | **103** |
| Name | **Asha Rao** | **Ravi Kumar** | **Meena Iyer** |
| Email | **asha@example.com** | **ravi@example.com** | **meena@example.com** |
| Department | **Information Technology** | **Computer Science** | **Electronics** |
| Year | **2** | **3** | **1** |

```text
Enter your choice (1-6): 1

--- Add Student ---
Enter Student ID (1-999999): 101
Enter Name: Asha Rao
Enter Email: asha@example.com
Enter Department: Information Technology
Enter Year of Study (1-4): 2
[SUCCESS] Student added: Asha Rao (ID 101)
```

### Step 4 – View all students (scenario 3)

Choose **2**.

```text
ID   Name        Email              Department              Year
101  Asha Rao    asha@example.com   Information Technology  2
102  Ravi Kumar  ravi@example.com   Computer Science        3
103  Meena Iyer  meena@example.com  Electronics             1
Total students: 3
```

(A dashed line separates the header, the rows and the total in the real output.)

### Step 5 – Search by ID (scenario 4)

Choose **3**, enter **102**.

```text
Enter Student ID to search: 102
[SUCCESS] Student found:
  ID         : 102
  Name       : Ravi Kumar
  Email      : ravi@example.com
  Department : Computer Science
  Year       : 3
```

### Step 6 – Update a student (scenario 5)

Choose **4**, enter ID **101**, then new values: Name **Asha Rao**, Email **asha.rao@example.com**, Department **Information Technology**, Year **3**.

```text
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
```

Check that only student 101 changed: choose **2** and confirm 102 and 103 are untouched.

### Step 7 – Delete a student (scenario 6)

Choose **5**, enter **103**.

```text
Enter Student ID to delete: 103
[SUCCESS] Deleted student: Meena Iyer (ID 103)
```

Choose **2** again: only 101 and 102 remain.

### Step 8 – Invalid menu choices (scenario 11 – non-numeric input)

Enter **abc**, then **9** at the menu prompt (a blank line and `1.5` are rejected the same way).

```text
Enter your choice (1-6): abc
[ERROR] Invalid choice 'abc'. Please enter a number from 1 to 6.
...
Enter your choice (1-6): 9
[ERROR] Invalid choice '9'. Please enter a number from 1 to 6.
```

The program shows the menu again – it does not crash.

### Step 9 – Duplicate ID (scenario 7)

Choose **1**, enter ID **101** (already used).

```text
Enter Student ID (1-999999): 101
[ERROR] A student with ID 101 already exists. Duplicate IDs are not allowed.
```

You are returned to the menu and nothing was added.

### Step 10 – Bad ID, blank name, invalid email, invalid year (scenarios 8, 9, 10, 11)

Choose **1** and enter the values below. Each bad value is rejected and the **same question is asked again**:

| Prompt | Type (in order) | Result |
|---|---|---|
| Student ID | **abc** | error – not a whole number |
| Student ID | **201** | accepted |
| Name | *(three spaces)* | error – blank name |
| Name | **Nila S** | accepted |
| Email | **nila-at-example.com** | error – invalid format |
| Email | **nila@example.com** | accepted |
| Department | **IT** | accepted |
| Year | **7** | error – out of range 1 to 4 |
| Year | **two** | error – not a number |
| Year | **2** | accepted |

```text
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
```

### Step 11 – Search, update and delete an ID that does not exist (scenario 12)

Choose **3**, **4** and **5** in turn, each time entering **999**.

```text
Enter Student ID to search: 999
[ERROR] No student found with ID 999.
...
Enter Student ID to update: 999
[ERROR] No student found with ID 999. Nothing was updated.
...
Enter Student ID to delete: 999
[ERROR] No student found with ID 999. Nothing was deleted.
```

### Step 12 – Exit cleanly (scenario 14)

Choose **6**.

```text
Enter your choice (1-6): 6
[INFO] Exiting the application...
Goodbye!
```

The terminal prompt returns. Restart the program and choose **2**: the list is empty again, because records are stored in memory only.

---

## Part 4 – Optional: replay the whole recorded session

`docs\sample-input.txt` contains every line typed in the recorded session. In PowerShell you can feed it to the program:

```powershell
Get-Content docs\sample-input.txt | java -cp out Main
```

Because the input is piped, you will not see the typed values echoed – only the program's output. The full recorded transcript (with typed input shown) is in `docs/sample-session.txt`.
*(I could not test this PowerShell command on Windows; if it behaves differently, use the manual steps above.)*

---

## Scenario checklist

| # | Scenario | Automated test IDs | Manual step |
|---|---|---|---|
| 1 | Application compiles | – | Part 1 |
| 2 | Add a student | A05–A08, B01 | Step 3 |
| 3 | Display all students | A14, A15, B02 | Step 4 |
| 4 | Find by ID | A12, A13, B03 | Step 5 |
| 5 | Update details | A16–A22, B04 | Step 6 |
| 6 | Delete by ID | A23–A26, B05, B06 | Step 7 |
| 7 | Duplicate IDs rejected | A09, A10, B11 | Step 9 |
| 8 | Blank names rejected | A27–A29, B13 | Step 10 |
| 9 | Invalid emails rejected | A32, A33, B14 | Step 10 |
| 10 | Invalid years rejected | A34–A37, B15, B16 | Step 10 |
| 11 | Non-numeric input handled | A43–A49, B09, B12 | Steps 8 and 10 |
| 12 | Missing ID message | A02–A04, A13, A20, A26, B19–B21 | Step 11 |
| 13 | Empty-list behaviour | A01–A04, B22–B25 | Step 2 |
| 14 | Clean exit | B07, B26, B27 | Step 12 |
