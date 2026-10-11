package com.nakshatra.studentmanagementapi.service;

import com.nakshatra.studentmanagementapi.exception.StudentNotFoundException;
import com.nakshatra.studentmanagementapi.model.Student;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

/**
 * Business logic for student records.
 *
 * Storage is an IN-MEMORY map. There is no database, so all records are lost when the
 * application stops or restarts. (A database can be added later by replacing this map
 * with a repository.)
 *
 * The map is thread-safe because a web server handles several requests at the same time,
 * and ids come from an AtomicLong so two requests can never receive the same id.
 */
@Service
public class StudentService {

    /** id -> student, kept sorted by id. */
    private final ConcurrentSkipListMap<Long, Student> students = new ConcurrentSkipListMap<>();

    /** Generates 1, 2, 3 ... safely, even with concurrent requests. */
    private final AtomicLong idGenerator = new AtomicLong(0);

    /** Creates a student with a new unique id (any id in the input is overwritten). */
    public Student create(Student student) {
        long newId = idGenerator.incrementAndGet();
        student.setId(newId);
        students.put(newId, student);
        return student;
    }

    /** Returns all students, ordered by id. */
    public List<Student> findAll() {
        return new ArrayList<>(students.values());
    }

    /** Returns one student or throws StudentNotFoundException (-> HTTP 404). */
    public Student findById(Long id) {
        Student student = students.get(id);
        if (student == null) {
            throw new StudentNotFoundException(id);
        }
        return student;
    }

    /** Replaces the data of an existing student. The id never changes. */
    public Student update(Long id, Student updatedData) {
        // computeIfPresent is atomic: it only replaces the value if the id exists.
        Student result = students.computeIfPresent(id, (existingId, existingStudent) -> {
            updatedData.setId(existingId);
            return updatedData;
        });
        if (result == null) {
            throw new StudentNotFoundException(id);
        }
        return result;
    }

    /** Deletes a student or throws StudentNotFoundException (-> HTTP 404). */
    public void delete(Long id) {
        if (students.remove(id) == null) {
            throw new StudentNotFoundException(id);
        }
    }

    /** Removes every student. Not exposed through the API; used to reset state between tests. */
    public void deleteAll() {
        students.clear();
    }
}
