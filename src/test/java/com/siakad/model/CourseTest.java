package com.siakad.model;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class CourseTest {
    @Test
    void testCourseGetterSetter() {
        Course c = new Course("IF101", "Algoritma", 3, 40, 10, "Pak Dosen");
        c.addPrerequisite("MATH001");

        assertEquals("IF101", c.getCourseCode());
        assertEquals("Algoritma", c.getCourseName());
        assertEquals(3, c.getCredits());
        assertEquals(40, c.getCapacity());
        assertEquals(10, c.getEnrolledCount());
        assertEquals("Pak Dosen", c.getLecturer());
        assertTrue(c.getPrerequisites().contains("MATH001"));
    }

    @Test
    void testAddPrerequisiteWhenNullList() {
        Course c = new Course("IF101", "Algoritma", 3, 40, 10, "Pak Dosen");

        // Paksa prerequisites jadi null (simulasi kondisi yang belum diinisialisasi)
        c.setPrerequisites(null);

        c.addPrerequisite("MATH001");

        assertNotNull(c.getPrerequisites());
        assertTrue(c.getPrerequisites().contains("MATH001"));
    }

}
