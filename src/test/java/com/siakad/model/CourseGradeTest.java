package com.siakad.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CourseGradeTest {

    @Test
    void testCourseGradeGetterSetter() {
        CourseGrade grade = new CourseGrade();
        grade.setCourseCode("PBO101");
        grade.setCredits(3);
        grade.setGradePoint(3.5);

        assertEquals("PBO101", grade.getCourseCode());
        assertEquals(3, grade.getCredits());
        assertEquals(3.5, grade.getGradePoint());
    }

    @Test
    void testCourseGradeConstructor() {
        CourseGrade grade = new CourseGrade("IF101", 2, 4.0);

        assertEquals("IF101", grade.getCourseCode());
        assertEquals(2, grade.getCredits());
        assertEquals(4.0, grade.getGradePoint());
    }
}

