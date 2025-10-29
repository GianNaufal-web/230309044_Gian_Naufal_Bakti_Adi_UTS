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
        Course course = new Course("PPL101", "Pengujian Perangkat Lunak", 3, 30, 30, "Krisna Nuresa Qodri");
        Student student = new Student("230309044", "Gian Naufal Bakti Adi", "giannaufal@gmail.com",
                "Rekayasa Keamanan Siber", 5, 3.2, "ACTIVE");

        Mockito.when(courseRepo.findByCourseCode("PPL101")).thenReturn(course);
        Mockito.when(studentRepo.findById("230309044")).thenReturn(student);

        assertThrows(CourseFullException.class, () -> service.enrollCourse("230309044", "PPL101"));
    }

    @Test
    void testEnrollWhenStudentNotFound() {
        Mockito.when(studentRepo.findById("404")).thenReturn(null);
        assertThrows(StudentNotFoundException.class, () -> service.enrollCourse("404", "PPL101"));
    }

    @Test
    void testValidateCreditLimitWithinAllowedRange() {
        Student student = new Student("230309044", "Gian Naufal Bakti Adi", "giannaufal@gmail.com",
                "Rekayasa Keamanan Siber", 5, 3.5, "ACTIVE");

        Mockito.when(studentRepo.findById("230309044")).thenReturn(student);
        Mockito.when(gradeCalculator.calculateMaxCredits(3.5)).thenReturn(24);

        boolean valid = service.validateCreditLimit("230309044", 18);
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
        Student student = new Student("230309044", "Gian Naufal Bakti Adi", "giannaufal@gmail.com",
                "Rekayasa Keamanan Siber", 5, 3.5, "ACTIVE");
        Course course = new Course("PPL101", "Pengujian Perangkat Lunak", 3, 30, 10, "Krisna Nuresa Qodri");

        Mockito.when(studentRepo.findById("230309044")).thenReturn(student);
        Mockito.when(courseRepo.findByCourseCode("PPL101")).thenReturn(course);

        service.dropCourse("230309044", "PPL101");

        Mockito.verify(courseRepo).update(course);
        Mockito.verify(notificationService).sendEmail(Mockito.eq("giannaufal@gmail.com"),
                Mockito.anyString(), Mockito.contains("Pengujian Perangkat Lunak"));
    }

    // ✅ Tambahan untuk menutup branch else (enrolled == 0)
    @Test
    void testDropCourse_WhenEnrolledCountIsZero() {
        Student student = new Student("230309044", "Gian Naufal Bakti Adi", "giannaufal@gmail.com",
                "Rekayasa Keamanan Siber", 5, 3.0, "ACTIVE");
        Course course = new Course("PPL000", "Software Testing", 3, 30, 0, "Krisna");

        Mockito.when(studentRepo.findById("230309044")).thenReturn(student);
        Mockito.when(courseRepo.findByCourseCode("PPL000")).thenReturn(course);

        service.dropCourse("230309044", "PPL000");

        assertEquals(0, course.getEnrolledCount());
        Mockito.verify(courseRepo).update(course);
        Mockito.verify(notificationService).sendEmail(
                Mockito.eq("giannaufal@gmail.com"),
                Mockito.anyString(),
                Mockito.contains("Software Testing")
        );
    }

    // ✅ Tambahan: test dropCourse dengan student tidak ditemukan
    @Test
    void testDropCourse_ThrowsStudentNotFoundException() {
        Mockito.when(studentRepo.findById("404")).thenReturn(null);
        assertThrows(StudentNotFoundException.class, () -> service.dropCourse("404", "PPL101"));
    }

    // ✅ Tambahan: test dropCourse dengan course tidak ditemukan
    @Test
    void testDropCourse_ThrowsCourseNotFoundException() {
        Student student = new Student("230309044", "Gian Naufal Bakti Adi", "giannaufal@gmail.com",
                "Rekayasa Keamanan Siber", 5, 3.5, "ACTIVE");

        Mockito.when(studentRepo.findById("230309044")).thenReturn(student);
        Mockito.when(courseRepo.findByCourseCode("XYZ404")).thenReturn(null);

        assertThrows(CourseNotFoundException.class, () -> service.dropCourse("230309044", "XYZ404"));
    }

    @Test
    void testValidateCreditLimitThrowsStudentNotFound() {
        Mockito.when(studentRepo.findById("999")).thenReturn(null);
        assertThrows(StudentNotFoundException.class, () -> service.validateCreditLimit("999", 20));
    }

    @Test
    void testEnrollCourseSuccessfully() {
        Student student = new Student("230309044", "Gian Naufal Bakti Adi", "giannaufal@gmail.com",
                "Rekayasa Keamanan Siber", 5, 3.8, "ACTIVE");
        Course course = new Course("PPL101", "Pengujian Perangkat Lunak", 3, 30, 10, "Krisna Nuresa Qodri");

        Mockito.when(studentRepo.findById("230309044")).thenReturn(student);
        Mockito.when(courseRepo.findByCourseCode("PPL101")).thenReturn(course);
        Mockito.when(courseRepo.isPrerequisiteMet("230309044", "PPL101")).thenReturn(true);

        var enrollment = service.enrollCourse("230309044", "PPL101");

        assertNotNull(enrollment);
        assertEquals("230309044", enrollment.getStudentId());
        assertEquals("PPL101", enrollment.getCourseCode());
        assertEquals("APPROVED", enrollment.getStatus());

        Mockito.verify(courseRepo).update(course);
        Mockito.verify(notificationService).sendEmail(
                Mockito.eq("giannaufal@gmail.com"),
                Mockito.anyString(),
                Mockito.contains("Pengujian Perangkat Lunak")
        );
    }

    // ============================
    // TEST STUB
    // ============================

    static class StubGradeCalculator extends GradeCalculator {
        @Override
        public int calculateMaxCredits(double gpa) {
            return 24;
        }
    }

    @Test
    void testValidateCreditLimit_UsingStub() {
        StudentRepository studentRepo = Mockito.mock(StudentRepository.class);
        CourseRepository courseRepo = Mockito.mock(CourseRepository.class);
        NotificationService notif = Mockito.mock(NotificationService.class);
        GradeCalculator stubCalc = new StubGradeCalculator();

        EnrollmentService service = new EnrollmentService(studentRepo, courseRepo, notif, stubCalc);

        Student s = new Student("230309044", "Gian Naufal Bakti Adi", "giannaufal@gmail.com",
                "Rekayasa Keamanan Siber", 5, 3.8, "ACTIVE");
        Mockito.when(studentRepo.findById("230309044")).thenReturn(s);

        boolean valid = service.validateCreditLimit("230309044", 18);
        assertTrue(valid, "Seharusnya valid karena stub selalu mengembalikan 24 SKS");
    }
}
