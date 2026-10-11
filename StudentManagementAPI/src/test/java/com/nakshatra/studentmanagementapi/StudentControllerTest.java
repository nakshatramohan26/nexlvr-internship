package com.nakshatra.studentmanagementapi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nakshatra.studentmanagementapi.model.Student;
import com.nakshatra.studentmanagementapi.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * End-to-end tests of the REST API: each test sends a fake HTTP request through the real
 * controller, validation, service and exception handler using MockMvc (no real server port needed).
 *
 * Tests are independent: @BeforeEach empties the in-memory store, and every test creates
 * the data it needs itself, so the order in which tests run does not matter.
 */
@SpringBootTest
@AutoConfigureMockMvc
class StudentControllerTest {

    private static final String VALID_STUDENT_JSON = """
            {"name":"Nakshatra M","email":"nakshatra@example.com","department":"Information Technology","year":2}
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentService studentService;

    @BeforeEach
    void resetStore() {
        studentService.deleteAll();
    }

    /** Helper: puts a student straight into the service and returns it (with its generated id). */
    private Student givenStudent(String name, String email) {
        return studentService.create(new Student(name, email, "Information Technology", 2));
    }

    // ---------- 1. Create ----------

    @Test
    @DisplayName("POST /api/students with valid data returns 201 and the created student")
    void createStudent_validData_returns201() throws Exception {
        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_STUDENT_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/students/")))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Nakshatra M"))
                .andExpect(jsonPath("$.email").value("nakshatra@example.com"))
                .andExpect(jsonPath("$.department").value("Information Technology"))
                .andExpect(jsonPath("$.year").value(2));
    }

    @Test
    @DisplayName("POST ignores an id sent by the client and generates its own")
    void createStudent_clientSuppliedId_isIgnored() throws Exception {
        String body = """
                {"id":999,"name":"Asha K","email":"asha@example.com","department":"CSE","year":3}
                """;
        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(not(999)));
    }

    @Test
    @DisplayName("Two created students receive different ids")
    void createStudent_twice_idsAreUnique() throws Exception {
        Student first = givenStudent("First", "first@example.com");
        Student second = givenStudent("Second", "second@example.com");

        assertThat(first.getId()).isNotEqualTo(second.getId());
    }

    // ---------- 2. Retrieve all ----------

    @Test
    @DisplayName("GET /api/students returns all students")
    void getAllStudents_returnsAllCreatedStudents() throws Exception {
        givenStudent("Student One", "one@example.com");
        givenStudent("Student Two", "two@example.com");

        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Student One"))
                .andExpect(jsonPath("$[1].name").value("Student Two"));
    }

    @Test
    @DisplayName("GET /api/students returns an empty list (200) when there are no students")
    void getAllStudents_whenEmpty_returnsEmptyList() throws Exception {
        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ---------- 3 & 4. Retrieve one ----------

    @Test
    @DisplayName("GET /api/students/{id} returns the student when it exists")
    void getStudentById_existing_returns200() throws Exception {
        Student saved = givenStudent("Existing Student", "existing@example.com");

        mockMvc.perform(get("/api/students/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("Existing Student"))
                .andExpect(jsonPath("$.email").value("existing@example.com"));
    }

    @Test
    @DisplayName("GET /api/students/{id} returns 404 with an error body when the id does not exist")
    void getStudentById_missing_returns404() throws Exception {
        mockMvc.perform(get("/api/students/{id}", 424242))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Student with id 424242 was not found"));
    }

    @Test
    @DisplayName("GET /api/students/abc (non-numeric id) returns 400")
    void getStudentById_nonNumericId_returns400() throws Exception {
        mockMvc.perform(get("/api/students/{id}", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(containsString("'id'")));
    }

    @Test
    @DisplayName("GET /api/students/-1 (negative id) returns 404 because no such student exists")
    void getStudentById_negativeId_returns404() throws Exception {
        mockMvc.perform(get("/api/students/{id}", -1))
                .andExpect(status().isNotFound());
    }

    // ---------- 5. Update ----------

    @Test
    @DisplayName("PUT /api/students/{id} updates an existing student and keeps its id")
    void updateStudent_existing_returns200() throws Exception {
        Student saved = givenStudent("Old Name", "old@example.com");
        String updateJson = """
                {"name":"New Name","email":"new@example.com","department":"Computer Science","year":4}
                """;

        mockMvc.perform(put("/api/students/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("New Name"))
                .andExpect(jsonPath("$.email").value("new@example.com"))
                .andExpect(jsonPath("$.department").value("Computer Science"))
                .andExpect(jsonPath("$.year").value(4));

        // The change must really be stored, not only echoed back.
        mockMvc.perform(get("/api/students/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"));
    }

    @Test
    @DisplayName("PUT /api/students/{id} returns 404 when the student does not exist")
    void updateStudent_missing_returns404() throws Exception {
        mockMvc.perform(put("/api/students/{id}", 424242)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_STUDENT_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Student with id 424242 was not found"));
    }

    @Test
    @DisplayName("PUT with invalid data returns 400 and does not change the stored student")
    void updateStudent_invalidData_returns400() throws Exception {
        Student saved = givenStudent("Keep Me", "keep@example.com");
        String invalidJson = """
                {"name":"","email":"keep@example.com","department":"IT","year":2}
                """;

        mockMvc.perform(put("/api/students/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.name").value("Name must not be blank"));

        mockMvc.perform(get("/api/students/{id}", saved.getId()))
                .andExpect(jsonPath("$.name").value("Keep Me"));
    }

    // ---------- 6. Delete ----------

    @Test
    @DisplayName("DELETE /api/students/{id} returns 204 and the student is gone afterwards")
    void deleteStudent_existing_returns204() throws Exception {
        Student saved = givenStudent("To Delete", "delete@example.com");

        mockMvc.perform(delete("/api/students/{id}", saved.getId()))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        mockMvc.perform(get("/api/students/{id}", saved.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/students/{id} returns 404 when the student does not exist")
    void deleteStudent_missing_returns404() throws Exception {
        mockMvc.perform(delete("/api/students/{id}", 424242))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/students/abc (non-numeric id) returns 400")
    void deleteStudent_nonNumericId_returns400() throws Exception {
        mockMvc.perform(delete("/api/students/{id}", "abc"))
                .andExpect(status().isBadRequest());
    }

    // ---------- 7. Invalid input ----------

    @Test
    @DisplayName("POST with blank name, invalid email and year 0 returns 400 with a message per field")
    void createStudent_invalidData_returns400WithFieldErrors() throws Exception {
        String invalidJson = """
                {"name":"  ","email":"not-an-email","department":"Information Technology","year":0}
                """;

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.fieldErrors.name").value("Name must not be blank"))
                .andExpect(jsonPath("$.fieldErrors.email").value("Email must be a valid email address"))
                .andExpect(jsonPath("$.fieldErrors.year").value("Year must be between 1 and 4"));
    }

    @Test
    @DisplayName("POST with missing fields returns 400")
    void createStudent_missingFields_returns400() throws Exception {
        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.name").exists())
                .andExpect(jsonPath("$.fieldErrors.email").exists())
                .andExpect(jsonPath("$.fieldErrors.department").exists())
                .andExpect(jsonPath("$.fieldErrors.year").value("Year is required"));
    }

    @Test
    @DisplayName("POST with year 5 (above the allowed range) returns 400")
    void createStudent_yearTooHigh_returns400() throws Exception {
        String json = """
                {"name":"Asha K","email":"asha@example.com","department":"CSE","year":5}
                """;

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.year").value("Year must be between 1 and 4"));
    }

    @Test
    @DisplayName("POST with malformed JSON returns 400 without exposing internal details")
    void createStudent_malformedJson_returns400() throws Exception {
        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ this is not json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(not(containsString("Exception"))));
    }

    @Test
    @DisplayName("POST with a wrong field type (year as text) returns 400")
    void createStudent_wrongFieldType_returns400() throws Exception {
        String json = """
                {"name":"Asha K","email":"asha@example.com","department":"CSE","year":"two"}
                """;

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST with no body returns 400")
    void createStudent_noBody_returns400() throws Exception {
        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    // ---------- Swagger / OpenAPI ----------

    @Test
    @DisplayName("GET /v3/api-docs returns the OpenAPI document describing the student endpoints")
    void openApiDocument_isAvailableAndDescribesEndpoints() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("Student Management API"))
                .andExpect(jsonPath("$.paths['/api/students']").exists())
                .andExpect(jsonPath("$.paths['/api/students/{id}']").exists());
    }
}
