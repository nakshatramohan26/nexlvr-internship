import java.util.Scanner;

/**
 * Main is the user interface: it shows the menu, reads what the user types,
 * and calls StudentManager to do the real work.
 *
 * Input rule used everywhere: read a WHOLE line with nextLine() and convert it ourselves.
 * We never use nextInt(), because it leaves the "Enter" key in the buffer and causes the
 * classic Scanner bug where the next nextLine() returns an empty string.
 *
 * Records are stored in memory only. They are lost when the program exits.
 */
public class Main {

    // Created inside main() so the program (and tests) start with a fresh Scanner and an empty list.
    private static Scanner scanner;
    private static StudentManager manager;

    /** Thrown when the input stream ends (for example Ctrl+Z on Windows) so we can exit cleanly. */
    private static class InputClosedException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        manager = new StudentManager();

        System.out.println("==============================================");
        System.out.println("      STUDENT CRUD CONSOLE APPLICATION");
        System.out.println("==============================================");
        System.out.println("Note: records are kept in memory only and are");
        System.out.println("lost when the program exits.");

        try {
            boolean running = true;
            while (running) {
                printMenu();
                String choice = prompt("Enter your choice (1-6): ");
                try {
                    running = handleMenuChoice(choice);
                } catch (InputClosedException e) {
                    throw e;   // handled below
                } catch (RuntimeException e) {
                    // Safety net: show a friendly message, never a stack trace, and keep running.
                    System.out.println("[ERROR] Something went wrong: " + e.getMessage());
                }
            }
        } catch (InputClosedException e) {
            System.out.println();
            System.out.println("[INFO] Input ended. Exiting the program.");
        } finally {
            scanner.close();
        }
        System.out.println("Goodbye!");
    }

    // ---------- Menu ----------

    private static void printMenu() {
        System.out.println();
        System.out.println("------------- MAIN MENU -------------");
        System.out.println("1. Add Student");
        System.out.println("2. View All Students");
        System.out.println("3. Search Student by ID");
        System.out.println("4. Update Student");
        System.out.println("5. Delete Student");
        System.out.println("6. Exit");
        System.out.println("-------------------------------------");
    }

    /** Runs the chosen option. Returns false when the user chose Exit, otherwise true. */
    private static boolean handleMenuChoice(String choice) {
        switch (choice.trim()) {
            case "1":
                addStudent();
                return true;
            case "2":
                manager.displayAllStudents();
                return true;
            case "3":
                searchStudent();
                return true;
            case "4":
                updateStudent();
                return true;
            case "5":
                deleteStudent();
                return true;
            case "6":
                System.out.println("[INFO] Exiting the application...");
                return false;
            default:
                System.out.println("[ERROR] Invalid choice '" + choice.trim()
                        + "'. Please enter a number from 1 to 6.");
                return true;
        }
    }

    // ---------- Menu actions ----------

    private static void addStudent() {
        System.out.println();
        System.out.println("--- Add Student ---");

        int id = readId("Enter Student ID (" + Student.MIN_ID + "-" + Student.MAX_ID + "): ");
        if (manager.idExists(id)) {
            System.out.println("[ERROR] A student with ID " + id + " already exists. Duplicate IDs are not allowed.");
            return;
        }
        String name = readName();
        String email = readEmail();
        String department = readDepartment();
        int year = readYear();

        Student student = new Student(id, name, email, department, year);
        if (manager.addStudent(student)) {
            System.out.println("[SUCCESS] Student added: " + student.getName() + " (ID " + id + ")");
        } else {
            System.out.println("[ERROR] Could not add the student. ID " + id + " already exists.");
        }
    }

    private static void searchStudent() {
        System.out.println();
        System.out.println("--- Search Student by ID ---");
        if (manager.isEmpty()) {
            System.out.println("[INFO] The student list is empty. Add a student first.");
            return;
        }
        int id = readId("Enter Student ID to search: ");
        Student student = manager.findStudentById(id);
        if (student == null) {
            System.out.println("[ERROR] No student found with ID " + id + ".");
        } else {
            System.out.println("[SUCCESS] Student found:");
            printStudentDetails(student);
        }
    }

    private static void updateStudent() {
        System.out.println();
        System.out.println("--- Update Student ---");
        if (manager.isEmpty()) {
            System.out.println("[INFO] The student list is empty. There is nothing to update.");
            return;
        }
        int id = readId("Enter Student ID to update: ");
        Student existing = manager.findStudentById(id);
        if (existing == null) {
            System.out.println("[ERROR] No student found with ID " + id + ". Nothing was updated.");
            return;
        }

        System.out.println("Current details:");
        printStudentDetails(existing);
        System.out.println("Enter the new details (the ID cannot be changed).");

        String name = readName();
        String email = readEmail();
        String department = readDepartment();
        int year = readYear();

        if (manager.updateStudent(id, name, email, department, year)) {
            System.out.println("[SUCCESS] Student with ID " + id + " updated:");
            printStudentDetails(manager.findStudentById(id));
        } else {
            System.out.println("[ERROR] Update failed. No student found with ID " + id + ".");
        }
    }

    private static void deleteStudent() {
        System.out.println();
        System.out.println("--- Delete Student ---");
        if (manager.isEmpty()) {
            System.out.println("[INFO] The student list is empty. There is nothing to delete.");
            return;
        }
        int id = readId("Enter Student ID to delete: ");
        Student student = manager.findStudentById(id);
        if (student == null) {
            System.out.println("[ERROR] No student found with ID " + id + ". Nothing was deleted.");
            return;
        }
        if (manager.deleteStudent(id)) {
            System.out.println("[SUCCESS] Deleted student: " + student.getName() + " (ID " + id + ")");
        } else {
            System.out.println("[ERROR] Delete failed. No student found with ID " + id + ".");
        }
    }

    private static void printStudentDetails(Student student) {
        System.out.println("  ID         : " + student.getStudentId());
        System.out.println("  Name       : " + student.getName());
        System.out.println("  Email      : " + student.getEmail());
        System.out.println("  Department : " + student.getDepartment());
        System.out.println("  Year       : " + student.getYear());
    }

    // ---------- Safe input helpers ----------
    // Each reader keeps asking until the value is valid. The rules live in Student,
    // so Add and Update are always validated the same way.

    /** Prints a prompt and reads one full line. Throws InputClosedException if no more input exists. */
    private static String prompt(String message) {
        System.out.print(message);
        if (!scanner.hasNextLine()) {
            throw new InputClosedException();
        }
        return scanner.nextLine();
    }

    private static int readId(String message) {
        while (true) {
            try {
                return Student.parseId(prompt(message));
            } catch (IllegalArgumentException e) {
                System.out.println("[ERROR] " + e.getMessage());
            }
        }
    }

    private static String readName() {
        while (true) {
            try {
                return Student.validateName(prompt("Enter Name: "));
            } catch (IllegalArgumentException e) {
                System.out.println("[ERROR] " + e.getMessage());
            }
        }
    }

    private static String readEmail() {
        while (true) {
            try {
                return Student.validateEmail(prompt("Enter Email: "));
            } catch (IllegalArgumentException e) {
                System.out.println("[ERROR] " + e.getMessage());
            }
        }
    }

    private static String readDepartment() {
        while (true) {
            try {
                return Student.validateDepartment(prompt("Enter Department: "));
            } catch (IllegalArgumentException e) {
                System.out.println("[ERROR] " + e.getMessage());
            }
        }
    }

    private static int readYear() {
        while (true) {
            try {
                return Student.parseYear(prompt("Enter Year of Study (" + Student.MIN_YEAR + "-" + Student.MAX_YEAR + "): "));
            } catch (IllegalArgumentException e) {
                System.out.println("[ERROR] " + e.getMessage());
            }
        }
    }
}
