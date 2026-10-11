import java.util.regex.Pattern;

/**
 * Student represents ONE student record (one "row" of data).
 *
 * Responsibilities of this class:
 *  - hold the data of a single student (ID, name, email, department, year)
 *  - protect that data (private fields = encapsulation)
 *  - validate the data, so an invalid Student can never be created
 *
 * It does NOT read from the keyboard and does NOT store the list of students.
 * Those jobs belong to Main and StudentManager.
 */
public class Student {

    // ---------- Validation rules (kept in ONE place so create and update behave the same) ----------
    public static final int MIN_ID = 1;
    public static final int MAX_ID = 999999;
    public static final int MIN_YEAR = 1;   // B.Tech is a 4-year programme, so valid years are 1 to 4
    public static final int MAX_YEAR = 4;
    public static final int MIN_TEXT_LENGTH = 2;
    public static final int MAX_NAME_LENGTH = 50;
    public static final int MAX_DEPARTMENT_LENGTH = 50;
    public static final int MAX_EMAIL_LENGTH = 100;

    // Name: starts with a letter, then letters, spaces, dots, apostrophes or hyphens (e.g. "Nakshatra M")
    private static final Pattern NAME_PATTERN = Pattern.compile("^\\p{L}[\\p{L} .'-]*$");
    // Department: starts with a letter, then letters, spaces, dots, '&', apostrophes or hyphens
    private static final Pattern DEPARTMENT_PATTERN = Pattern.compile("^\\p{L}[\\p{L} .&'-]*$");
    // Basic email check: something@domain.tld  (a simple rule, good enough for this project)
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$");

    // ---------- Fields (private = encapsulation) ----------
    private final int studentId;   // unique identifier; 'final' because an ID never changes after creation
    private String name;           // full name of the student
    private String email;          // contact email
    private String department;     // e.g. "Information Technology"
    private int year;              // year of study (1 to 4)

    /**
     * Creates a student. Every value is validated; an invalid value throws IllegalArgumentException.
     */
    public Student(int studentId, String name, String email, String department, int year) {
        this.studentId = validateId(studentId);
        this.name = validateName(name);
        this.email = validateEmail(email);
        this.department = validateDepartment(department);
        this.year = validateYear(year);
    }

    // ---------- Getters ----------
    public int getStudentId() { return studentId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getDepartment() { return department; }
    public int getYear() { return year; }

    // ---------- Setters (validate before changing). There is no setStudentId on purpose. ----------
    public void setName(String name) { this.name = validateName(name); }
    public void setEmail(String email) { this.email = validateEmail(email); }
    public void setDepartment(String department) { this.department = validateDepartment(department); }
    public void setYear(int year) { this.year = validateYear(year); }

    // ---------- Validation helpers (static: they can be used without creating a Student) ----------

    /** Converts text typed by the user into a valid student ID, or throws IllegalArgumentException. */
    public static int parseId(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID cannot be empty.");
        }
        try {
            return validateId(Integer.parseInt(text.trim()));
        } catch (NumberFormatException e) {
            throw idError();
        }
    }

    /** Converts text typed by the user into a valid year of study, or throws IllegalArgumentException. */
    public static int parseYear(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Year of study cannot be empty.");
        }
        try {
            return validateYear(Integer.parseInt(text.trim()));
        } catch (NumberFormatException e) {
            throw yearError();
        }
    }

    public static int validateId(int id) {
        if (id < MIN_ID || id > MAX_ID) {
            throw idError();
        }
        return id;
    }

    public static int validateYear(int year) {
        if (year < MIN_YEAR || year > MAX_YEAR) {
            throw yearError();
        }
        return year;
    }

    /** Returns the trimmed name if valid; otherwise throws IllegalArgumentException. */
    public static String validateName(String name) {
        String value = (name == null) ? "" : name.trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty or blank.");
        }
        if (value.length() < MIN_TEXT_LENGTH || value.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException(
                    "Name must be " + MIN_TEXT_LENGTH + " to " + MAX_NAME_LENGTH + " characters long.");
        }
        if (!NAME_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException(
                    "Name must start with a letter and contain only letters, spaces, dots, hyphens or apostrophes.");
        }
        return value;
    }

    /** Returns the trimmed email if valid; otherwise throws IllegalArgumentException. */
    public static String validateEmail(String email) {
        String value = (email == null) ? "" : email.trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty.");
        }
        if (value.length() > MAX_EMAIL_LENGTH || !EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException(
                    "Invalid email format. Use something like name@example.com (no spaces).");
        }
        return value;
    }

    /** Returns the trimmed department if valid; otherwise throws IllegalArgumentException. */
    public static String validateDepartment(String department) {
        String value = (department == null) ? "" : department.trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException("Department cannot be empty or blank.");
        }
        if (value.length() < MIN_TEXT_LENGTH || value.length() > MAX_DEPARTMENT_LENGTH) {
            throw new IllegalArgumentException(
                    "Department must be " + MIN_TEXT_LENGTH + " to " + MAX_DEPARTMENT_LENGTH + " characters long.");
        }
        if (!DEPARTMENT_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException(
                    "Department must start with a letter and contain only letters, spaces, dots, '&' or hyphens.");
        }
        return value;
    }

    private static IllegalArgumentException idError() {
        return new IllegalArgumentException(
                "Student ID must be a whole number between " + MIN_ID + " and " + MAX_ID + ".");
    }

    private static IllegalArgumentException yearError() {
        return new IllegalArgumentException(
                "Year of study must be a whole number between " + MIN_YEAR + " and " + MAX_YEAR + ".");
    }

    // ---------- Display and identity ----------

    @Override
    public String toString() {
        return "ID: " + studentId + " | Name: " + name + " | Email: " + email
                + " | Department: " + department + " | Year: " + year;
    }

    /** Two Student objects are "the same record" when their IDs match. */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Student)) {
            return false;
        }
        return studentId == ((Student) other).studentId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(studentId);
    }
}
