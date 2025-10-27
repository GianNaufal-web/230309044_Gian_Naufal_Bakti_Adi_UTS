package com.siakad.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExceptionTest {

    @Test
    void testCourseFullException() {
        CourseFullException ex1 = new CourseFullException("Course full");
        assertEquals("Course full", ex1.getMessage());

        Throwable cause = new RuntimeException("Root cause");
        CourseFullException ex2 = new CourseFullException("Course full", cause);
        assertEquals("Course full", ex2.getMessage());
        assertEquals(cause, ex2.getCause());
    }

    @Test
    void testCourseNotFoundException() {
        CourseFullException ex1 = new CourseFullException("Course not found");
        assertEquals("Course not found", ex1.getMessage());

        Throwable cause = new RuntimeException("Not found cause");
        CourseFullException ex2 = new CourseFullException("Course not found", cause);
        assertEquals("Course not found", ex2.getMessage());
        assertEquals(cause, ex2.getCause());
    }

    @Test
    void testEnrollmentException() {
        EnrollmentException ex1 = new EnrollmentException("Enrollment failed");
        assertEquals("Enrollment failed", ex1.getMessage());

        Throwable cause = new RuntimeException("Database error");
        EnrollmentException ex2 = new EnrollmentException("Enrollment failed", cause);
        assertEquals("Enrollment failed", ex2.getMessage());
        assertEquals(cause, ex2.getCause());
    }

    @Test
    void testPrerequisiteNotMetException() {
        PrerequisiteNotMetException ex1 = new PrerequisiteNotMetException("Prerequisite not met");
        assertEquals("Prerequisite not met", ex1.getMessage());

        Throwable cause = new RuntimeException("Missing prerequisite");
        PrerequisiteNotMetException ex2 = new PrerequisiteNotMetException("Prerequisite not met", cause);
        assertEquals("Prerequisite not met", ex2.getMessage());
        assertEquals(cause, ex2.getCause());
    }

    @Test
    void testStudentNotFoundException() {
        StudentNotFoundException ex1 = new StudentNotFoundException("Student not found");
        assertEquals("Student not found", ex1.getMessage());

        Throwable cause = new RuntimeException("ID not found");
        StudentNotFoundException ex2 = new StudentNotFoundException("Student not found", cause);
        assertEquals("Student not found", ex2.getMessage());
        assertEquals(cause, ex2.getCause());
    }
}