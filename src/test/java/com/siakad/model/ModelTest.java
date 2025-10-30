package com.siakad.model;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ModelTest {

    @Test
    void testStudentGetterSetter() {
        Student s = new Student();
        s.setStudentId("001");
        s.setName("Gian");
        s.setEmail("giannaufal@mail.com");
        s.setMajor("IF");
        s.setSemester(3);
        s.setGpa(3.5);
        s.setAcademicStatus("ACTIVE");

        assertEquals("001", s.getStudentId());
        assertEquals("gian", s.getName());
        assertEquals("IF", s.getMajor());
    }

    @Test
    void testCourseAddPrerequisite() {
        Course c = new Course("PBO101", "PBO", 3, 30, 10, "Pak Budi");
        c.addPrerequisite("BASDAT");
        assertTrue(c.getPrerequisites().contains("BASDAT"));
    }

    @Test
    void testCourseGradeGetterSetter() {
        CourseGrade g = new CourseGrade("PBO101", 3, 4.0);
        assertEquals(4.0, g.getGradePoint());
    }

    @Test
    void testEnrollmentSetterGetter() {
        Enrollment e = new Enrollment();
        e.setEnrollmentId("E001");
        e.setStudentId("001");
        e.setCourseCode("PBO101");
        e.setStatus("APPROVED");

        assertEquals("E001", e.getEnrollmentId());
        assertEquals("APPROVED", e.getStatus());
    }

    @Test
    void testCourseSetterGetter() {
        Course c = new Course();
        c.setCourseCode("IF101");
        c.setCourseName("Dasar Pemrograman");
        c.setCredits(3);
        c.setCapacity(40);
        c.setEnrolledCount(10);
        c.setLecturer("Pak Andi");
        c.addPrerequisite("MTK001");

        assertEquals("IF101", c.getCourseCode());
        assertEquals("Pak Andi", c.getLecturer());
        assertTrue(c.getPrerequisites().contains("MTK001"));
    }

}
