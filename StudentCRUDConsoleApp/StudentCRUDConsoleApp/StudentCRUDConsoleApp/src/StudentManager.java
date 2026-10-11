import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * StudentManager holds all student records in an ArrayList and performs the CRUD operations.
 *
 * IMPORTANT: the data lives only in memory (RAM). When the program exits, every record is lost.
 *
 * Return-value convention used in this class (easy to explain and test):
 *  - methods that can "fail normally" (duplicate ID, ID not found) return true/false or null
 *  - invalid data (blank name, bad email, ...) throws IllegalArgumentException from Student
 */
public class StudentManager {

    // The in-memory data store. 'final' means the list variable always points to the same list.
    private final ArrayList<Student> students = new ArrayList<>();

    // ---------- CREATE ----------

    /**
     * Adds a student. Returns true if added, or false if a student with the same ID already exists.
     */
    public boolean addStudent(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null.");
        }
        if (idExists(student.getStudentId())) {
            return false;               // duplicate IDs are not allowed
        }
        students.add(student);
        return true;
    }

    // ---------- READ ----------

    /** Prints all students as a neat table, or a clear message when the list is empty. */
    public void displayAllStudents() {
        if (students.isEmpty()) {
            System.out.println("[INFO] No student records found. The list is empty.");
            return;
        }

        // Work out the column widths from the data so the table always lines up.
        int idWidth = "ID".length();
        int nameWidth = "Name".length();
        int emailWidth = "Email".length();
        int deptWidth = "Department".length();
        int yearWidth = "Year".length();
        for (Student s : students) {
            idWidth = Math.max(idWidth, String.valueOf(s.getStudentId()).length());
            nameWidth = Math.max(nameWidth, s.getName().length());
            emailWidth = Math.max(emailWidth, s.getEmail().length());
            deptWidth = Math.max(deptWidth, s.getDepartment().length());
        }

        String format = "%-" + idWidth + "s  %-" + nameWidth + "s  %-" + emailWidth
                + "s  %-" + deptWidth + "s  %s%n";   // last column is not padded (no trailing spaces)
        int totalWidth = idWidth + nameWidth + emailWidth + deptWidth + yearWidth + 8;

        System.out.println();
        System.out.printf(format, "ID", "Name", "Email", "Department", "Year");
        System.out.println(repeat('-', totalWidth));
        for (Student s : students) {
            System.out.printf(format, String.valueOf(s.getStudentId()), s.getName(),
                    s.getEmail(), s.getDepartment(), String.valueOf(s.getYear()));
        }
        System.out.println(repeat('-', totalWidth));
        System.out.println("Total students: " + students.size());
    }

    /** Returns a read-only copy of all students (used by tests and other code). */
    public List<Student> getAllStudents() {
        return Collections.unmodifiableList(new ArrayList<>(students));
    }

    /** Finds a student by ID. Returns the Student, or null if the ID does not exist. */
    public Student findStudentById(int id) {
        int index = findIndexById(id);
        return (index == -1) ? null : students.get(index);
    }

    /** Returns true if a student with this ID is already stored. */
    public boolean idExists(int id) {
        return findIndexById(id) != -1;
    }

    public boolean isEmpty() {
        return students.isEmpty();
    }

    public int getStudentCount() {
        return students.size();
    }

    // ---------- UPDATE ----------

    /**
     * Replaces the details of the student with the given ID. The ID itself never changes.
     * Returns true if updated, or false if the ID does not exist.
     * If any new value is invalid, IllegalArgumentException is thrown and the old record stays unchanged.
     */
    public boolean updateStudent(int id, String name, String email, String department, int year) {
        int index = findIndexById(id);
        if (index == -1) {
            return false;
        }
        // Building the new Student validates ALL values first, so a bad value can never leave
        // the old record half-updated.
        Student updated = new Student(id, name, email, department, year);
        students.set(index, updated);   // put the new record in the SAME position
        return true;
    }

    // ---------- DELETE ----------

    /** Deletes the student with the given ID. Returns true if deleted, false if the ID does not exist. */
    public boolean deleteStudent(int id) {
        int index = findIndexById(id);
        if (index == -1) {
            return false;
        }
        students.remove(index);
        return true;
    }

    // ---------- Helpers ----------

    /** Linear search through the list. Returns the position of the student, or -1 if not found. */
    private int findIndexById(int id) {
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getStudentId() == id) {
                return i;
            }
        }
        return -1;
    }

    private static String repeat(char c, int times) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) {
            sb.append(c);
        }
        return sb.toString();
    }
}
