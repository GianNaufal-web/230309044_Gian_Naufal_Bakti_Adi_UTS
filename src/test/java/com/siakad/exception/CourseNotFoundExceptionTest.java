package com.siakad.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CourseNotFoundExceptionTest {

    @Test
    void testConstructorWithMessage() {
        CourseNotFoundException ex = new CourseNotFoundException("Course not found");
        assertEquals("Course not found", ex.getMessage());
    }

    @Test
    void testConstructorWithMessageAndCause() {
        Throwable cause = new RuntimeException("Underlying error");
        CourseNotFoundException ex = new CourseNotFoundException("Error loading course", cause);

        assertEquals("Error loading course", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }
}