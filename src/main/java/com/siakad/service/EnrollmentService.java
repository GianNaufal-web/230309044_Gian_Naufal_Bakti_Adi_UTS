package com.siakad.service;

import com.siakad.exception.*;
import com.siakad.model.Course;
import com.siakad.model.Enrollment;
import com.siakad.model.Student;
import com.siakad.repository.CourseRepository;
import com.siakad.repository.StudentRepository;

import java.time.LocalDateTime;

/**
 * Service untuk mengelola enrollment (pendaftaran mata kuliah)
 * Class ini akan diuji dengan STUB dan MOCK
 */
public class EnrollmentService {
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final NotificationService notificationService;
    private final GradeCalculator gradeCalculator;

    public EnrollmentService(StudentRepository studentRepository,
                             CourseRepository courseRepository,
                             NotificationService notificationService,
                             GradeCalculator gradeCalculator) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.notificationService = notificationService;
        this.gradeCalculator = gradeCalculator;
    }

    /**
     * Mendaftarkan mahasiswa ke mata kuliah
     * Method ini akan diuji dengan MOCK
     */
    public Enrollment enrollCourse(String studentId, String courseCode) {
        // Validasi mahasiswa
        Student student = studentRepository.findById(studentId);
        if (student == null) {
            throw new StudentNotFoundException("Student not found: " + studentId);
        }

        // Status akademik
        String status = student.getAcademicStatus();
        if ("SUSPENDED".equalsIgnoreCase(status)) {
            throw new EnrollmentException("Student is suspended");
        }

        // Validasi mata kuliah
        Course course = courseRepository.findByCourseCode(courseCode);
        if (course == null) {
            throw new CourseNotFoundException("Course not found: " + courseCode);
        }

        // Kapasitas
        int enrolled = course.getEnrolledCount();
        int capacity = course.getCapacity();
        if (enrolled >= capacity) {
            throw new CourseFullException("Course is full");
        }

        // Prasyarat
        boolean prereqMet = courseRepository.isPrerequisiteMet(studentId, courseCode);
        if (!prereqMet) {
            throw new PrerequisiteNotMetException("Prerequisites not met");
        }

        // Pembuatan enrollment
        Enrollment enrollment = new Enrollment();
        enrollment.setEnrollmentId(generateEnrollmentId());
        enrollment.setStudentId(studentId);
        enrollment.setCourseCode(courseCode);
        enrollment.setEnrollmentDate(LocalDateTime.now());
        enrollment.setStatus("APPROVED");

        // Update jumlah peserta
        course.setEnrolledCount(enrolled + 1);
        courseRepository.update(course);

        // Kirim notifikasi
        String email = student.getEmail();
        String subject = "Enrollment Confirmation";
        String message = "You have been enrolled in: " + course.getCourseName();
        notificationService.sendEmail(email, subject, message);

        return enrollment;
    }

    /**
     * Validasi batas SKS
     */
    public boolean validateCreditLimit(String studentId, int requestedCredits) {
        Student student = studentRepository.findById(studentId);
        if (student == null) {
            throw new StudentNotFoundException("Student not found");
        }

        int maxCredits = gradeCalculator.calculateMaxCredits(student.getGpa());
        return requestedCredits <= maxCredits;
    }

    /**
     * Drop mata kuliah
     */
    public void dropCourse(String studentId, String courseCode) {
        Student student = studentRepository.findById(studentId);
        if (student == null) {
            throw new StudentNotFoundException("Student not found");
        }

        Course course = courseRepository.findByCourseCode(courseCode);
        if (course == null) {
            throw new CourseNotFoundException("Course not found");
        }

        // Jika enrolledCount lebih besar dari nol baru dikurangi
        int enrolled = course.getEnrolledCount();
        if (enrolled > 0) {
            course.setEnrolledCount(enrolled - 1);
        } else {
            // Tambahan untuk menutup branch kondisi nol
            course.setEnrolledCount(0);
        }

        courseRepository.update(course);

        notificationService.sendEmail(
                student.getEmail(),
                "Course Drop Confirmation",
                "You have dropped: " + course.getCourseName()
        );
    }

    /**
     * Generate ID unik
     */
    private String generateEnrollmentId() {
        String prefix = "ENR-";
        String timestamp = String.valueOf(System.currentTimeMillis());
        return prefix + timestamp;
    }
}
