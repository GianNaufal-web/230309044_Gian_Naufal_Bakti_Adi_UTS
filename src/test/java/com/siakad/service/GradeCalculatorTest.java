package com.siakad.service;

import com.siakad.model.CourseGrade;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;


class GradeCalculatorTest {

    private final GradeCalculator calculator = new GradeCalculator();

    // ---------------------- calculateGPA ----------------------

    @Test
    void testCalculateGPA_NormalCase() {
        // Kasus normal: IPK dihitung dari 2 mata kuliah
        List<CourseGrade> grades = new ArrayList<>();
        grades.add(new CourseGrade("PPL101", 3, 4.0)); // A
        grades.add(new CourseGrade("RKS202", 2, 3.0)); // B

        double gpa = calculator.calculateGPA(grades);
        assertEquals(3.6, gpa);
    }

    @Test
    void testCalculateGPA_EmptyList() {
        // Kasus: daftar nilai kosong
        List<CourseGrade> grades = new ArrayList<>();
        double gpa = calculator.calculateGPA(grades);
        assertEquals(0.0, gpa);
    }

    @Test
    void testCalculateGPA_NullList() {
        // Kasus: daftar nilai null
        double gpa = calculator.calculateGPA(null);
        assertEquals(0.0, gpa);
    }

    @Test
    void testCalculateGPA_ZeroCredits() {
        // Kasus: total SKS nol
        List<CourseGrade> grades = new ArrayList<>();
        grades.add(new CourseGrade("PPL101", 0, 4.0));
        double gpa = calculator.calculateGPA(grades);
        assertEquals(0.0, gpa);
    }

    @Test
    void testCalculateGPA_InvalidGradePoint_TooHigh() {
        // Kasus: nilai grade point terlalu tinggi
        List<CourseGrade> grades = List.of(new CourseGrade("PPL101", 3, 5.0));
        Exception ex = assertThrows(IllegalArgumentException.class, () -> calculator.calculateGPA(grades));
        assertTrue(ex.getMessage().contains("Invalid grade point"));
    }

    @Test
    void testCalculateGPA_InvalidGradePoint_Negative() {
        // Kasus: nilai grade point negatif
        List<CourseGrade> grades = List.of(new CourseGrade("PPL101", 3, -1.0));
        Exception ex = assertThrows(IllegalArgumentException.class, () -> calculator.calculateGPA(grades));
        assertTrue(ex.getMessage().contains("Invalid grade point"));
    }

    // ---------------------- determineAcademicStatus ----------------------

    @Test
    void testDetermineAcademicStatus_Semester1_Active() {
        // Semester 1, IPK >= 2.0 -> ACTIVE
        assertEquals("ACTIVE", calculator.determineAcademicStatus(2.0, 1));
    }

    @Test
    void testDetermineAcademicStatus_Semester1_Probation() {
        // Semester 2, IPK < 2.0 -> PROBATION
        assertEquals("PROBATION", calculator.determineAcademicStatus(1.5, 2));
    }

    @Test
    void testDetermineAcademicStatus_Semester3_Active() {
        // Semester 3, IPK >= 2.25 -> ACTIVE
        assertEquals("ACTIVE", calculator.determineAcademicStatus(2.3, 3));
    }

    @Test
    void testDetermineAcademicStatus_Semester3_Probation() {
        // Semester 3, IPK 2.0-2.24 -> PROBATION
        assertEquals("PROBATION", calculator.determineAcademicStatus(2.1, 3));
    }

    @Test
    void testDetermineAcademicStatus_Semester3_Suspended() {
        // Semester 3, IPK < 2.0 -> SUSPENDED
        assertEquals("SUSPENDED", calculator.determineAcademicStatus(1.9, 3));
    }

    @Test
    void testDetermineAcademicStatus_Semester5_Active() {
        // Semester 5+, IPK >= 2.5 -> ACTIVE
        assertEquals("ACTIVE", calculator.determineAcademicStatus(3.0, 5));
    }

    @Test
    void testDetermineAcademicStatus_Semester5_Probation() {
        // Semester 5+, IPK 2.0-2.49 -> PROBATION
        assertEquals("PROBATION", calculator.determineAcademicStatus(2.1, 6));
    }

    @Test
    void testDetermineAcademicStatus_Semester5_Suspended() {
        // Semester 5+, IPK < 2.0 -> SUSPENDED
        assertEquals("SUSPENDED", calculator.determineAcademicStatus(1.5, 7));
    }

    @Test
    void testDetermineAcademicStatus_InvalidGPA() {
        // Kasus: IPK di luar range
        assertThrows(IllegalArgumentException.class, () -> calculator.determineAcademicStatus(5.0, 3));
    }

    @Test
    void testDetermineAcademicStatus_InvalidSemester() {
        // Kasus: semester < 1
        assertThrows(IllegalArgumentException.class, () -> calculator.determineAcademicStatus(3.0, 0));
    }

    // ---------------------- calculateMaxCredits ----------------------

    @Test
    void testCalculateMaxCredits_AllRanges() {
        // Menguji semua rentang IPK
        assertEquals(24, calculator.calculateMaxCredits(3.5)); // >= 3.0
        assertEquals(21, calculator.calculateMaxCredits(2.7)); // 2.5 - 2.99
        assertEquals(18, calculator.calculateMaxCredits(2.1)); // 2.0 - 2.49
        assertEquals(15, calculator.calculateMaxCredits(1.8)); // < 2.0
    }

    @Test
    void testCalculateMaxCredits_InvalidHigh() {
        // Kasus: IPK > 4.0
        assertThrows(IllegalArgumentException.class, () -> calculator.calculateMaxCredits(5.0));
    }

    @Test
    void testCalculateMaxCredits_InvalidNegative() {
        // Kasus: IPK < 0.0
        assertThrows(IllegalArgumentException.class, () -> calculator.calculateMaxCredits(-1.0));
    }

}
