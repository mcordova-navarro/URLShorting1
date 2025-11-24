package domain.services;

import domain.models.AcademicYearPolicy;
import domain.models.Evaluation;
import domain.models.GradeBreakdown;
import domain.models.StudentRecord;

public class FinalGradeCalculator {

    // Constantes de negocio (puedes ajustarlas)
    private static final double MAX_GRADE = 20.0;
    private static final double ATTENDANCE_PENALTY = 2.0;      // penalización si no cumple asistencia mínima
    private static final double EXTRA_POINTS_BASE = 0.5;       // puntos extra base
    private static final double EXTRA_POINTS_THRESHOLD = 15.0; // umbral de promedio para optar a extra

    /**
     * Cálculo determinista de la nota final de un estudiante.
     * - promedio ponderado
     * - penalización por inasistencia
     * - puntos extra (posible duplicación)
     */
    public GradeBreakdown calculate(StudentRecord record, AcademicYearPolicy policy) {
        double weightedAverage = computeWeightedAverage(record);

        // Penalización por inasistencia
        double attendancePenalty = record.hasReachedMinimumClasses() ? 0.0 : ATTENDANCE_PENALTY;
        double gradeAfterPenalty = Math.max(0.0, weightedAverage - attendancePenalty);

        // Puntos extra
        double baseExtra = 0.0;
        if (policy != null
                && policy.isExtraPointsEnabled()
                && record.hasReachedMinimumClasses()
                && weightedAverage >= EXTRA_POINTS_THRESHOLD) {
            baseExtra = EXTRA_POINTS_BASE;
        }

        // "Puntos extra duplicados": aplicamos el doble de los puntos extra base
        double duplicatedExtra = baseExtra * 2.0;

        double finalGrade = Math.min(MAX_GRADE, gradeAfterPenalty + duplicatedExtra);

        return new GradeBreakdown(weightedAverage, attendancePenalty, baseExtra, duplicatedExtra, finalGrade);
    }

    private double computeWeightedAverage(StudentRecord record) {
        double totalWeight = 0.0;
        double weightedSum = 0.0;

        for (Evaluation e : record.getEvaluations()) {
            totalWeight += e.getWeight();
            weightedSum += e.getGrade() * e.getWeight();
        }

        if (totalWeight == 0.0) {
            return 0.0;
        }

        // Normalizamos a escala 0-20 asumiendo que el peso total debe ser 100
        return weightedSum / totalWeight; // si totalWeight=100, es el promedio clásico
    }
}
