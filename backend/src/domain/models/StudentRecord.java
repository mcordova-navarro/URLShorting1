package domain.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StudentRecord {
    private final String studentId;
    private final String studentName;
    private final int academicYear;
    private boolean hasReachedMinimumClasses;
    private final List<Evaluation> evaluations = new ArrayList<>();

    public static final int MAX_EVALUATIONS = 10;

    public StudentRecord(String studentId, String studentName, int academicYear) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.academicYear = academicYear;
        this.hasReachedMinimumClasses = false;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public int getAcademicYear() {
        return academicYear;
    }

    public boolean hasReachedMinimumClasses() {
        return hasReachedMinimumClasses;
    }

    public void setHasReachedMinimumClasses(boolean hasReachedMinimumClasses) {
        this.hasReachedMinimumClasses = hasReachedMinimumClasses;
    }

    public List<Evaluation> getEvaluations() {
        return Collections.unmodifiableList(evaluations);
    }

    public void clearEvaluations() {
        evaluations.clear();
    }

    public void addEvaluation(Evaluation evaluation) {
        if (evaluations.size() >= MAX_EVALUATIONS) {
            throw new IllegalStateException("El máximo de evaluaciones por estudiante es " + MAX_EVALUATIONS);
        }
        evaluations.add(evaluation);
    }
}
