package domain.services;

import domain.models.AcademicYearPolicy;
import domain.models.Evaluation;
import domain.models.GradeBreakdown;
import domain.models.StudentRecord;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FinalGradeCalculatorTest {

    private final FinalGradeCalculator calculator = new FinalGradeCalculator();

    private StudentRecord createRecordWithEvaluations(boolean reachedMinimumClasses, Evaluation... evaluations) {
        StudentRecord record = new StudentRecord("student-1", "Alice", 2025);
        record.setHasReachedMinimumClasses(reachedMinimumClasses);
        for (Evaluation e : evaluations) {
            record.addEvaluation(e);
        }
        return record;
    }

    @Test
    void shouldCalculateFinalGradeNormallyWhenAttendanceOkAndNoExtraPoints() {
        // promedio ponderado: (10*50 + 20*50) / 100 = 15
        StudentRecord record = createRecordWithEvaluations(true,
                new Evaluation("Parcial", 10.0, 50.0),
                new Evaluation("Final", 20.0, 50.0)
        );
        AcademicYearPolicy policy = new AcademicYearPolicy(2025, false); // extra puntos desactivados

        GradeBreakdown breakdown = calculator.calculate(record, policy);

        assertEquals(15.0, breakdown.getWeightedAverage(), 0.0001);
        assertEquals(0.0, breakdown.getAttendancePenalty(), 0.0001);
        assertEquals(0.0, breakdown.getBaseExtraPoints(), 0.0001);
        assertEquals(0.0, breakdown.getDuplicatedExtraPoints(), 0.0001);
        assertEquals(15.0, breakdown.getFinalGrade(), 0.0001);
    }

    @Test
    void shouldApplyAttendancePenaltyWhenMinimumClassesNotReached() {
        // promedio ponderado: 16, pero sin asistencia mínima => penalización de 2 puntos
        StudentRecord record = createRecordWithEvaluations(false,
                new Evaluation("Parcial", 16.0, 100.0)
        );
        AcademicYearPolicy policy = new AcademicYearPolicy(2025, true); // no importa, no hay asistencia mínima

        GradeBreakdown breakdown = calculator.calculate(record, policy);

        assertEquals(16.0, breakdown.getWeightedAverage(), 0.0001);
        assertEquals(2.0, breakdown.getAttendancePenalty(), 0.0001);
        assertEquals(0.0, breakdown.getBaseExtraPoints(), 0.0001);
        assertEquals(14.0, breakdown.getFinalGrade(), 0.0001);
    }

    @Test
    void shouldApplyExtraPointsWhenPolicyEnabledAndThresholdReached() {
        // promedio ponderado >= 15, asistencia mínima cumplida y política activa => 0.5 puntos extra base, duplicados a 1.0
        StudentRecord record = createRecordWithEvaluations(true,
                new Evaluation("Parcial", 16.0, 50.0),
                new Evaluation("Final", 16.0, 50.0)
        );
        AcademicYearPolicy policy = new AcademicYearPolicy(2025, true);

        GradeBreakdown breakdown = calculator.calculate(record, policy);

        assertEquals(16.0, breakdown.getWeightedAverage(), 0.0001);
        assertEquals(0.0, breakdown.getAttendancePenalty(), 0.0001);
        assertEquals(0.5, breakdown.getBaseExtraPoints(), 0.0001);
        assertEquals(1.0, breakdown.getDuplicatedExtraPoints(), 0.0001);
        assertEquals(17.0, breakdown.getFinalGrade(), 0.0001);
    }

    @Test
    void shouldNotApplyExtraPointsWhenPolicyDisabledOrThresholdNotReached() {
        // promedio ponderado < 15 => sin puntos extra aunque la política esté habilitada
        StudentRecord recordBelowThreshold = createRecordWithEvaluations(true,
                new Evaluation("Parcial", 14.0, 100.0)
        );
        AcademicYearPolicy policyEnabled = new AcademicYearPolicy(2025, true);

        GradeBreakdown breakdownBelowThreshold = calculator.calculate(recordBelowThreshold, policyEnabled);
        assertEquals(14.0, breakdownBelowThreshold.getWeightedAverage(), 0.0001);
        assertEquals(0.0, breakdownBelowThreshold.getBaseExtraPoints(), 0.0001);
        assertEquals(0.0, breakdownBelowThreshold.getDuplicatedExtraPoints(), 0.0001);
        assertEquals(14.0, breakdownBelowThreshold.getFinalGrade(), 0.0001);

        // política deshabilitada => sin puntos extra aunque se cumpla el umbral
        StudentRecord recordAtThreshold = createRecordWithEvaluations(true,
                new Evaluation("Parcial", 15.0, 100.0)
        );
        AcademicYearPolicy policyDisabled = new AcademicYearPolicy(2025, false);

        GradeBreakdown breakdownPolicyDisabled = calculator.calculate(recordAtThreshold, policyDisabled);
        assertEquals(15.0, breakdownPolicyDisabled.getWeightedAverage(), 0.0001);
        assertEquals(0.0, breakdownPolicyDisabled.getBaseExtraPoints(), 0.0001);
        assertEquals(0.0, breakdownPolicyDisabled.getDuplicatedExtraPoints(), 0.0001);
        assertEquals(15.0, breakdownPolicyDisabled.getFinalGrade(), 0.0001);
    }

    @Test
    void shouldReturnZeroWhenThereAreNoEvaluations() {
        StudentRecord record = new StudentRecord("student-1", "Alice", 2025);
        record.setHasReachedMinimumClasses(true);
        AcademicYearPolicy policy = new AcademicYearPolicy(2025, true);

        GradeBreakdown breakdown = calculator.calculate(record, policy);

        assertEquals(0.0, breakdown.getWeightedAverage(), 0.0001);
        assertEquals(0.0, breakdown.getAttendancePenalty(), 0.0001);
        assertEquals(0.0, breakdown.getBaseExtraPoints(), 0.0001);
        assertEquals(0.0, breakdown.getDuplicatedExtraPoints(), 0.0001);
        assertEquals(0.0, breakdown.getFinalGrade(), 0.0001);
    }

    @Test
    void shouldHandleNonStandardWeightsWithoutThrowing() {
        // pesos que no suman 100: (18*30 + 12*30) / 60 = 15
        StudentRecord record = createRecordWithEvaluations(true,
                new Evaluation("Trabajo", 18.0, 30.0),
                new Evaluation("Examen", 12.0, 30.0)
        );
        AcademicYearPolicy policy = new AcademicYearPolicy(2025, false);

        GradeBreakdown breakdown = calculator.calculate(record, policy);

        assertEquals(15.0, breakdown.getWeightedAverage(), 0.0001);
        assertEquals(15.0, breakdown.getFinalGrade(), 0.0001);
    }
}
