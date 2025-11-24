package domain.models;

public class GradeBreakdown {
    private final double weightedAverage;
    private final double attendancePenalty;
    private final double baseExtraPoints;
    private final double duplicatedExtraPoints;
    private final double finalGrade;

    public GradeBreakdown(double weightedAverage,
                          double attendancePenalty,
                          double baseExtraPoints,
                          double duplicatedExtraPoints,
                          double finalGrade) {
        this.weightedAverage = weightedAverage;
        this.attendancePenalty = attendancePenalty;
        this.baseExtraPoints = baseExtraPoints;
        this.duplicatedExtraPoints = duplicatedExtraPoints;
        this.finalGrade = finalGrade;
    }

    public double getWeightedAverage() {
        return weightedAverage;
    }

    public double getAttendancePenalty() {
        return attendancePenalty;
    }

    public double getBaseExtraPoints() {
        return baseExtraPoints;
    }

    public double getDuplicatedExtraPoints() {
        return duplicatedExtraPoints;
    }

    public double getFinalGrade() {
        return finalGrade;
    }
}
