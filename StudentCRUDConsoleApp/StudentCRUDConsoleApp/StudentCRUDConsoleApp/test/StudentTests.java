import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

/**
 * A tiny test runner with NO external libraries (no JUnit needed).
 *
 * Part A tests Student and StudentManager directly.
 * Part B runs the real Main console menu with scripted keyboard input and checks the output.
 *
 * Compile (from the project root, after compiling src):  javac -cp out -d out test\StudentTests.java
 * Run:                                                    java -cp out StudentTests
 */
public class StudentTests {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        partAStudentAndManager();
        partBConsoleScenarios();

        System.out.println();
        System.out.println("=====================================");
        System.out.println("Tests passed: " + passed + "   Tests failed: " + failed);
        System.out.println("=====================================");
        if (failed > 0) {
            System.exit(1);
        }
    }

    // ------------------------------------------------------------------
    // Part A: direct tests of Student and StudentManager
    // ------------------------------------------------------------------
    private static void partAStudentAndManager() {
        System.out.println("--- Part A: Student and StudentManager ---");

        StudentManager m = new StudentManager();

        // Empty list behaviour
        check("A01 new manager is empty", m.isEmpty() && m.getStudentCount() == 0);
        check("A02 find on empty list returns null", m.findStudentById(1) == null);
        check("A03 update on empty list returns false", !m.updateStudent(1, "Asha Rao", "a@example.com", "IT", 2));
        check("A04 delete on empty list returns false", !m.deleteStudent(1));

        // Create
        check("A05 add valid student", m.addStudent(new Student(101, "Asha Rao", "asha@example.com", "Information Technology", 2)));
        check("A06 add second student", m.addStudent(new Student(102, "Ravi Kumar", "ravi@example.com", "Computer Science", 3)));
        check("A07 add third student", m.addStudent(new Student(103, "Meena Iyer", "meena@example.com", "Electronics", 1)));
        check("A08 count is 3", m.getStudentCount() == 3);

        // Duplicate ID
        check("A09 duplicate ID rejected", !m.addStudent(new Student(101, "Other Person", "other@example.com", "IT", 1)));
        check("A10 count still 3 after duplicate", m.getStudentCount() == 3);
        check("A11 idExists true / false", m.idExists(102) && !m.idExists(999));

        // Read
        Student found = m.findStudentById(102);
        check("A12 find by ID returns correct student", found != null && "Ravi Kumar".equals(found.getName()));
        check("A13 find missing ID returns null", m.findStudentById(555) == null);
        List<Student> all = m.getAllStudents();
        check("A14 getAllStudents keeps insertion order", all.size() == 3 && all.get(0).getStudentId() == 101 && all.get(2).getStudentId() == 103);

        String table = captureOutput(() -> m.displayAllStudents());
        check("A15 display shows all students", table.contains("Asha Rao") && table.contains("Ravi Kumar")
                && table.contains("Meena Iyer") && table.contains("Total students: 3"));

        // Update affects the correct student only
        check("A16 update existing student", m.updateStudent(102, "Ravi K", "ravi.k@example.com", "Mechanical", 4));
        Student updated = m.findStudentById(102);
        check("A17 updated values stored", "Ravi K".equals(updated.getName()) && "ravi.k@example.com".equals(updated.getEmail())
                && "Mechanical".equals(updated.getDepartment()) && updated.getYear() == 4);
        check("A18 other students untouched by update", "Asha Rao".equals(m.findStudentById(101).getName())
                && "Meena Iyer".equals(m.findStudentById(103).getName()));
        check("A19 update keeps list position", m.getAllStudents().get(1).getStudentId() == 102);
        check("A20 update missing ID returns false", !m.updateStudent(777, "Some One", "s@example.com", "IT", 1));

        // Invalid update leaves the old record unchanged
        boolean threw = false;
        try {
            m.updateStudent(101, "Asha Rao", "not-an-email", "Information Technology", 2);
        } catch (IllegalArgumentException e) {
            threw = true;
        }
        check("A21 invalid update throws", threw);
        check("A22 invalid update leaves record unchanged", "asha@example.com".equals(m.findStudentById(101).getEmail()));

        // Delete affects the correct student only
        check("A23 delete existing student", m.deleteStudent(102));
        check("A24 deleted student is gone", m.findStudentById(102) == null && m.getStudentCount() == 2);
        check("A25 other students remain after delete", m.idExists(101) && m.idExists(103));
        check("A26 delete missing ID returns false", !m.deleteStudent(102));

        // Validation: names
        check("A27 blank name rejected", throwsIAE(() -> new Student(1, "   ", "a@example.com", "IT", 1)));
        check("A28 empty name rejected", throwsIAE(() -> new Student(1, "", "a@example.com", "IT", 1)));
        check("A29 null name rejected", throwsIAE(() -> new Student(1, null, "a@example.com", "IT", 1)));
        check("A30 name with digits rejected", throwsIAE(() -> new Student(1, "Asha123", "a@example.com", "IT", 1)));
        check("A31 name is trimmed", "Asha Rao".equals(new Student(1, "  Asha Rao  ", "a@example.com", "IT", 1).getName()));

        // Validation: emails
        String[] badEmails = {"", "plainaddress", "no-at-sign.com", "a@b", "a@@example.com", "a b@example.com", "a@example..com", "@example.com", "a@.com"};
        boolean allBadRejected = true;
        for (String e : badEmails) {
            final String email = e;
            if (!throwsIAE(() -> new Student(1, "Asha Rao", email, "IT", 1))) {
                allBadRejected = false;
                System.out.println("      (email wrongly accepted: '" + e + "')");
            }
        }
        check("A32 invalid emails rejected", allBadRejected);
        check("A33 valid emails accepted", !throwsIAE(() -> new Student(1, "Asha Rao", "first.last+tag@mail.example.com", "IT", 1)));

        // Validation: year
        check("A34 year 0 rejected", throwsIAE(() -> new Student(1, "Asha Rao", "a@example.com", "IT", 0)));
        check("A35 year 5 rejected", throwsIAE(() -> new Student(1, "Asha Rao", "a@example.com", "IT", 5)));
        check("A36 negative year rejected", throwsIAE(() -> new Student(1, "Asha Rao", "a@example.com", "IT", -1)));
        check("A37 years 1 to 4 accepted", !throwsIAE(() -> {
            for (int y = 1; y <= 4; y++) {
                new Student(1, "Asha Rao", "a@example.com", "IT", y);
            }
        }));

        // Validation: department and ID
        check("A38 blank department rejected", throwsIAE(() -> new Student(1, "Asha Rao", "a@example.com", "  ", 1)));
        check("A39 ID 0 rejected", throwsIAE(() -> new Student(0, "Asha Rao", "a@example.com", "IT", 1)));
        check("A40 negative ID rejected", throwsIAE(() -> new Student(-5, "Asha Rao", "a@example.com", "IT", 1)));
        check("A41 too-large ID rejected", throwsIAE(() -> new Student(1000000, "Asha Rao", "a@example.com", "IT", 1)));

        // Parsing text input
        check("A42 parseId accepts '101'", Student.parseId(" 101 ") == 101);
        check("A43 parseId rejects 'abc'", throwsIAE(() -> Student.parseId("abc")));
        check("A44 parseId rejects '12.5'", throwsIAE(() -> Student.parseId("12.5")));
        check("A45 parseId rejects blank", throwsIAE(() -> Student.parseId("   ")));
        check("A46 parseId rejects null", throwsIAE(() -> Student.parseId(null)));
        check("A47 parseId rejects huge number", throwsIAE(() -> Student.parseId("99999999999999")));
        check("A48 parseYear rejects 'two'", throwsIAE(() -> Student.parseYear("two")));
        check("A49 parseYear accepts '3'", Student.parseYear("3") == 3);

        // Setters validate too
        Student s = new Student(5, "Asha Rao", "a@example.com", "IT", 1);
        check("A50 setter rejects invalid email", throwsIAE(() -> s.setEmail("bad")));
        check("A51 setter keeps old value after rejection", "a@example.com".equals(s.getEmail()));
        s.setYear(3);
        check("A52 setter accepts valid year", s.getYear() == 3);

        // Identity
        check("A53 students with same ID are equal", new Student(7, "A One", "a@example.com", "IT", 1)
                .equals(new Student(7, "B Two", "b@example.com", "CSE", 2)));
    }

    // ------------------------------------------------------------------
    // Part B: run the real console menu with scripted input
    // ------------------------------------------------------------------
    private static void partBConsoleScenarios() {
        System.out.println();
        System.out.println("--- Part B: console scenarios (real Main, scripted input) ---");

        // B01: full happy path, exits via option 6
        String out = runMain(
                "1", "101", "Asha Rao", "asha@example.com", "Information Technology", "2",
                "1", "102", "Ravi Kumar", "ravi@example.com", "Computer Science", "3",
                "2",
                "3", "102",
                "4", "101", "Asha R", "asha.r@example.com", "IT", "3",
                "5", "102",
                "2",
                "6");
        check("B01 add student shows success", out.contains("[SUCCESS] Student added: Asha Rao (ID 101)"));
        check("B02 view all lists both students", out.contains("Ravi Kumar") && out.contains("Total students: 2"));
        check("B03 search finds student", out.contains("[SUCCESS] Student found:") && out.contains("ravi@example.com"));
        check("B04 update shows success", out.contains("[SUCCESS] Student with ID 101 updated:") && out.contains("asha.r@example.com"));
        check("B05 delete shows success", out.contains("[SUCCESS] Deleted student: Ravi Kumar (ID 102)"));
        check("B06 view after delete shows 1 student", out.contains("Total students: 1"));
        check("B07 exit is clean", out.contains("[INFO] Exiting the application...") && out.contains("Goodbye!"));
        check("B08 no stack trace in normal run", noStackTrace(out));

        // Invalid menu choices
        out = runMain("abc", "9", "", "0", "1.5", "6");
        check("B09 invalid menu choices handled", count(out, "[ERROR] Invalid choice") == 5 && out.contains("Goodbye!"));
        check("B10 no stack trace for bad menu input", noStackTrace(out));

        // Duplicate ID
        out = runMain(
                "1", "101", "Asha Rao", "asha@example.com", "IT", "2",
                "1", "101",
                "6");
        check("B11 duplicate ID rejected", out.contains("[ERROR] A student with ID 101 already exists"));

        // Blank name, invalid email, invalid year, non-numeric ID and year (re-prompting)
        out = runMain(
                "1", "abc", "-4", "0", "201",
                "   ", "Nila S",
                "nila-at-example.com", "nila@example.com",
                "IT",
                "7", "two", "", "2",
                "2",
                "6");
        check("B12 non-numeric ID handled", out.contains("[ERROR] Student ID must be a whole number between 1 and 999999."));
        check("B13 blank name rejected", out.contains("[ERROR] Name cannot be empty or blank."));
        check("B14 invalid email rejected", out.contains("[ERROR] Invalid email format."));
        check("B15 invalid year rejected", out.contains("[ERROR] Year of study must be a whole number between 1 and 4."));
        check("B16 empty year rejected", out.contains("[ERROR] Year of study cannot be empty."));
        check("B17 student saved after corrections", out.contains("[SUCCESS] Student added: Nila S (ID 201)") && out.contains("Total students: 1"));
        check("B18 no stack trace for bad field input", noStackTrace(out));

        // Missing ID in search / update / delete (list not empty)
        out = runMain(
                "1", "101", "Asha Rao", "asha@example.com", "IT", "2",
                "3", "999",
                "4", "999",
                "5", "999",
                "6");
        check("B19 search missing ID message", out.contains("[ERROR] No student found with ID 999."));
        check("B20 update missing ID message", out.contains("[ERROR] No student found with ID 999. Nothing was updated."));
        check("B21 delete missing ID message", out.contains("[ERROR] No student found with ID 999. Nothing was deleted."));

        // Empty list behaviour
        out = runMain("2", "3", "4", "5", "6");
        check("B22 view on empty list is clear", out.contains("[INFO] No student records found. The list is empty."));
        check("B23 search on empty list is clear", out.contains("[INFO] The student list is empty. Add a student first."));
        check("B24 update on empty list is clear", out.contains("There is nothing to update."));
        check("B25 delete on empty list is clear", out.contains("There is nothing to delete."));

        // Input ends without choosing Exit (for example Ctrl+Z / Ctrl+D): must not crash
        out = runMain("1", "101", "Asha Rao");
        check("B26 input ending mid-way exits cleanly", out.contains("[INFO] Input ended. Exiting the program.") && out.contains("Goodbye!"));
        check("B27 no stack trace when input ends", noStackTrace(out));

        // Records do not survive a restart (in-memory only)
        runMain("1", "101", "Asha Rao", "asha@example.com", "IT", "2", "6");
        out = runMain("2", "6");
        check("B28 data is NOT kept between runs (in-memory only)", out.contains("The list is empty."));
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------
    private static void check(String name, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("PASS  " + name);
        } else {
            failed++;
            System.out.println("FAIL  " + name);
        }
    }

    private static boolean throwsIAE(Runnable action) {
        try {
            action.run();
            return false;
        } catch (IllegalArgumentException e) {
            return true;
        }
    }

    /** Feeds the given lines to Main as keyboard input and returns everything Main printed. */
    private static String runMain(String... lines) {
        StringBuilder input = new StringBuilder();
        for (String line : lines) {
            input.append(line).append('\n');
        }
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            System.setIn(new ByteArrayInputStream(input.toString().getBytes()));
            System.setOut(new PrintStream(buffer, true));
            Main.main(new String[0]);
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
        return buffer.toString();
    }

    private static String captureOutput(Runnable action) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(buffer, true));
            action.run();
        } finally {
            System.setOut(originalOut);
        }
        return buffer.toString();
    }

    private static boolean noStackTrace(String text) {
        return !text.contains("Exception") && !text.contains("\tat ") && !text.contains("Error:  ");
    }

    private static int count(String text, String part) {
        int count = 0;
        int index = text.indexOf(part);
        while (index != -1) {
            count++;
            index = text.indexOf(part, index + part.length());
        }
        return count;
    }
}
