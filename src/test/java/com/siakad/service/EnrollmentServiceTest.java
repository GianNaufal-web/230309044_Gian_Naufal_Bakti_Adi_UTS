package com.siakad.service;

import com.siakad.exception.*;
import com.siakad.model.Course;
import com.siakad.model.Student;
import com.siakad.repository.CourseRepository;
import com.siakad.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;


class EnrollmentServiceTest {

    private EnrollmentService service;
    private StudentRepository studentRepo;
    private CourseRepository courseRepo;
    private NotificationService notificationService;
    private GradeCalculator gradeCalculator;

    @BeforeEach
    void setUp() {
        studentRepo = Mockito.mock(StudentRepository.class);
        courseRepo = Mockito.mock(CourseRepository.class);
        notificationService = Mockito.mock(NotificationService.class);
        gradeCalculator = Mockito.mock(GradeCalculator.class);

        service = new EnrollmentService(studentRepo, courseRepo, notificationService, gradeCalculator);
    }

    @Test
    void testEnrollWhenCourseIsFull() {
        // setup course yang sudah penuh
        Course course = new Course("PPL101", "Pengujian Perangkat Lunak", 3, 30, 30, "Krisna Nuresa Qodri");
        Student student = new Student("230209059", "Rengga Lahani", "renggalahani@gmail.com",
                "Rekayasa Keamanan Siber", 5, 3.2, "ACTIVE");

        Mockito.when(courseRepo.findByCourseCode("PPL101")).thenReturn(course);
        Mockito.when(studentRepo.findById("230209059")).thenReturn(student);

        assertThrows(CourseFullException.class, () -> service.enrollCourse("230209059", "PPL101"));
    }

    @Test
    void testEnrollWhenStudentNotFound() {
        Mockito.when(studentRepo.findById("404")).thenReturn(null);
        assertThrows(StudentNotFoundException.class, () -> service.enrollCourse("404", "PPL101"));
    }

    @Test
    void testValidateCreditLimitWithinAllowedRange() {
        Student student = new Student("230209059", "Rengga Lahani", "renggalahani@gmail.com",
                "Rekayasa Keamanan Siber", 5, 3.5, "ACTIVE");

        Mockito.when(studentRepo.findById("230209059")).thenReturn(student);
        Mockito.when(gradeCalculator.calculateMaxCredits(3.5)).thenReturn(24);

        boolean valid = service.validateCreditLimit("230209059", 18);
        assertTrue(valid);
    }

    @Test
    void testValidateCreditLimitExceedsLimit() {
        Student student = new Student("002", "Budi", "budi@mail.com", "Rekayasa Keamanan Siber", 5, 2.0, "ACTIVE");

        Mockito.when(studentRepo.findById("002")).thenReturn(student);
        Mockito.when(gradeCalculator.calculateMaxCredits(2.0)).thenReturn(15);

        boolean valid = service.validateCreditLimit("002", 18);
        assertFalse(valid);
    }

    @Test
    void testEnrollWhenStudentSuspended() {
        Student student = new Student("002", "Budi", "budi@mail.com", "Rekayasa Keamanan Siber", 5, 3.2, "SUSPENDED");
        Mockito.when(studentRepo.findById("002")).thenReturn(student);

        assertThrows(EnrollmentException.class, () -> service.enrollCourse("002", "PPL101"));
    }

    @Test
    void testEnrollWhenCourseNotFound() {
        Student student = new Student("003", "Sari", "sari@mail.com", "Rekayasa Keamanan Siber", 5, 3.8, "ACTIVE");
        Mockito.when(studentRepo.findById("003")).thenReturn(student);
        Mockito.when(courseRepo.findByCourseCode("XYZ123")).thenReturn(null);

        assertThrows(CourseNotFoundException.class, () -> service.enrollCourse("003", "XYZ123"));
    }

    @Test
    void testEnrollWhenPrerequisiteNotMet() {
        Student student = new Student("004", "Dewi", "dewi@mail.com", "Rekayasa Keamanan Siber", 5, 3.8, "ACTIVE");
        Course course = new Course("PPL101", "Pengujian Perangkat Lunak", 3, 30, 0, "Krisna Nuresa Qodri");

        Mockito.when(studentRepo.findById("004")).thenReturn(student);
        Mockito.when(courseRepo.findByCourseCode("PPL101")).thenReturn(course);
        Mockito.when(courseRepo.isPrerequisiteMet("004", "PPL101")).thenReturn(false);

        assertThrows(PrerequisiteNotMetException.class, () -> service.enrollCourse("004", "PPL101"));
    }

    @Test
    void testDropCourseSuccessfully() {
        Student student = new Student("230209059", "Rengga Lahani", "renggalahani@gmail.com",
                "Rekayasa Keamanan Siber", 5, 3.5, "ACTIVE");
        Course course = new Course("PPL101", "Pengujian Perangkat Lunak", 3, 30, 10, "Krisna Nuresa Qodri");

        Mockito.when(studentRepo.findById("230209059")).thenReturn(student);
        Mockito.when(courseRepo.findByCourseCode("PPL101")).thenReturn(course);

        service.dropCourse("230209059", "PPL101");

        Mockito.verify(courseRepo).update(course);
        Mockito.verify(notificationService).sendEmail(Mockito.eq("renggalahani@gmail.com"),
                Mockito.anyString(), Mockito.contains("Pengujian Perangkat Lunak"));
    }

    @Test
    void testValidateCreditLimitThrowsStudentNotFound() {
        Mockito.when(studentRepo.findById("999")).thenReturn(null);
        assertThrows(StudentNotFoundException.class, () -> service.validateCreditLimit("999", 20));
    }

    @Test
    void testEnrollCourseSuccessfully() {
        Student student = new Student("005", "Lina", "lina@mail.com", "Rekayasa Keamanan Siber", 5, 3.8, "ACTIVE");
        Course course = new Course("PPL101", "Pengujian Perangkat Lunak", 3, 30, 10, "Krisna Nuresa Qodri");

        Mockito.when(studentRepo.findById("005")).thenReturn(student);
        Mockito.when(courseRepo.findByCourseCode("PPL101")).thenReturn(course);
        Mockito.when(courseRepo.isPrerequisiteMet("005", "PPL101")).thenReturn(true);

        var enrollment = service.enrollCourse("005", "PPL101");

        assertNotNull(enrollment);
        assertEquals("005", enrollment.getStudentId());
        assertEquals("PPL101", enrollment.getCourseCode());
        assertEquals("APPROVED", enrollment.getStatus());

        Mockito.verify(courseRepo).update(course);
        Mockito.verify(notificationService).sendEmail(
                Mockito.eq("lina@mail.com"),
                Mockito.anyString(),
                Mockito.contains("Pengujian Perangkat Lunak")
        );
    }

    // ============================
    // TEST STUB
    // ============================

    // Stub class untuk GradeCalculator
    static class StubGradeCalculator extends GradeCalculator {
        @Override
        public int calculateMaxCredits(double gpa) {
            // Simulasi: selalu mengembalikan nilai tetap 24 SKS
            return 24;
        }
    }

    @Test
    void testValidateCreditLimit_UsingStub() {
        // Pakai mock untuk repository dan notification, tapi stub untuk GradeCalculator
        StudentRepository studentRepo = Mockito.mock(StudentRepository.class);
        CourseRepository courseRepo = Mockito.mock(CourseRepository.class);
        NotificationService notif = Mockito.mock(NotificationService.class);

        // Gunakan stub manual, bukan Mockito
        GradeCalculator stubCalc = new StubGradeCalculator();

        // Buat service dengan stub
        EnrollmentService service = new EnrollmentService(studentRepo, courseRepo, notif, stubCalc);

        // Setup data mahasiswa
        Student s = new Student("230209059", "Rengga Lahani", "renggalahani@gmail.com",
                "Rekayasa Keamanan Siber", 5, 3.8, "ACTIVE");
        Mockito.when(studentRepo.findById("230209059")).thenReturn(s);

        // Jalankan method yang diuji
        boolean valid = service.validateCreditLimit("230209059", 18);

        // Verifikasi hasil
        assertTrue(valid, "Seharusnya valid karena stub selalu mengembalikan 24 SKS");
    }
}