package com.siakad.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StudentTest {
    @Test
    void testStudentGetterSetter() {
        Student s = new Student("001", "Rengga", "rengga@mail.com", "IF", 5, 3.6, "ACTIVE");

        assertEquals("001", s.getStudentId());
        assertEquals("Rengga", s.getName());
        assertEquals("rengga@mail.com", s.getEmail());
        assertEquals("IF", s.getMajor());
        assertEquals(5, s.getSemester());
        assertEquals(3.6, s.getGpa());
        assertEquals("ACTIVE", s.getAcademicStatus());
    }
}
