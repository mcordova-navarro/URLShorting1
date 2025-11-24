package domain.models;

public class Evaluation {
    private final String name;
    private final double grade;     // nota obtenida
    private final double weight;    // porcentaje de peso sobre la nota final (0-100)

    public Evaluation(String name, double grade, double weight) {
        this.name = name;
        this.grade = grade;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public double getGrade() {
        return grade;
    }

    public double getWeight() {
        return weight;
    }
}
