package domain.models;

public class AcademicYearPolicy {
    private final int academicYear;
    private boolean extraPointsEnabled; // allYearsTeachers true/false

    public AcademicYearPolicy(int academicYear, boolean extraPointsEnabled) {
        this.academicYear = academicYear;
        this.extraPointsEnabled = extraPointsEnabled;
    }

    public int getAcademicYear() {
        return academicYear;
    }

    public boolean isExtraPointsEnabled() {
        return extraPointsEnabled;
    }

    public void setExtraPointsEnabled(boolean extraPointsEnabled) {
        this.extraPointsEnabled = extraPointsEnabled;
    }
}
