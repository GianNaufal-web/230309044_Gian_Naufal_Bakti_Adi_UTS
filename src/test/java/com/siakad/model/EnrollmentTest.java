package com.siakad.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class EnrollmentTest {

    @Test
    void testDefaultConstructorAndSetters() {
        Enrollment enrollment = new Enrollment();

        LocalDateTime now = LocalDateTime.now();
        enrollment.setEnrollmentId("ENR-001");
        enrollment.setStudentId("S123");
        enrollment.setCourseCode("CS101");
        enrollment.setEnrollmentDate(now);
        enrollment.setStatus("APPROVED");

        assertEquals("ENR-001", enrollment.getEnrollmentId());
        assertEquals("S123", enrollment.getStudentId());
        assertEquals("CS101", enrollment.getCourseCode());
        assertEquals(now, enrollment.getEnrollmentDate());
        assertEquals("APPROVED", enrollment.getStatus());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime date = LocalDateTime.of(2025, 10, 25, 10, 30);
        Enrollment enrollment = new Enrollment(
                "ENR-999", "S999", "CS999", date, "PENDING"
        );

        assertEquals("ENR-999", enrollment.getEnrollmentId());
        assertEquals("S999", enrollment.getStudentId());
        assertEquals("CS999", enrollment.getCourseCode());
        assertEquals(date, enrollment.getEnrollmentDate());
        assertEquals("PENDING", enrollment.getStatus());
    }
}