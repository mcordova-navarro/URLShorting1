import domain.models.AcademicYearPolicy;
import domain.models.Evaluation;
import domain.models.GradeBreakdown;
import domain.models.StudentRecord;
import domain.services.FinalGradeCalculator;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Application {

    // almacenamiento en memoria (sin base de datos)
    private static final Map<String, StudentRecord> students = new HashMap<>();
    private static final Map<Integer, AcademicYearPolicy> yearPolicies = new HashMap<>();

    private static final FinalGradeCalculator calculator = new FinalGradeCalculator();

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            runMenu(scanner);
        }
    }

    private static void runMenu(Scanner scanner) {
        while (true) {
            System.out.println("=======================================");
            System.out.println(" Sistema de Notas UTEC (Consola)");
            System.out.println("=======================================");
            System.out.println("1. Registrar/actualizar estudiante");
            System.out.println("2. Registrar evaluaciones de un estudiante");
            System.out.println("3. Registrar asistencia mínima de un estudiante");
            System.out.println("4. Registrar política de puntos extra por año académico");
            System.out.println("5. Calcular nota final y ver detalle");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");

            String option = scanner.nextLine().trim();
            switch (option) {
                case "1":
                    registerOrUpdateStudent(scanner);
                    break;
                case "2":
                    registerEvaluations(scanner);
                    break;
                case "3":
                    registerAttendance(scanner);
                    break;
                case "4":
                    registerYearPolicy(scanner);
                    break;
                case "5":
                    calculateAndShowFinalGrade(scanner);
                    break;
                case "0":
                    System.out.println("Hasta luego.");
                    return;
                default:
                    System.out.println("Opción inválida. Intente nuevamente.");
            }
            System.out.println();
        }
    }

    // 1) Registrar estudiante
    private static void registerOrUpdateStudent(Scanner scanner) {
        System.out.print("Código del estudiante: ");
        String id = scanner.nextLine().trim();

        System.out.print("Nombre del estudiante: ");
        String name = scanner.nextLine().trim();

        System.out.print("Año académico (ej. 2025): ");
        int year = readInt(scanner);

        StudentRecord record = new StudentRecord(id, name, year);
        students.put(id, record);

        System.out.println("Estudiante registrado/actualizado correctamente.");
    }

    // 2) Registrar evaluaciones
    private static void registerEvaluations(Scanner scanner) {
        StudentRecord record = findStudent(scanner);
        if (record == null) {
            return;
        }

        record.clearEvaluations();

        System.out.print("Cantidad de evaluaciones (máximo " + StudentRecord.MAX_EVALUATIONS + "): ");
        int count = readInt(scanner);
        if (count < 0 || count > StudentRecord.MAX_EVALUATIONS) {
            System.out.println("Cantidad inválida.");
            return;
        }

        double totalWeight = 0.0;
        for (int i = 1; i <= count; i++) {
            System.out.println("--- Evaluación " + i + " ---");
            System.out.print("Nombre: ");
            String name = scanner.nextLine().trim();

            System.out.print("Nota obtenida (0-20): ");
            double grade = readDouble(scanner);

            System.out.print("Peso (%) de esta evaluación: ");
            double weight = readDouble(scanner);

            totalWeight += weight;
            record.addEvaluation(new Evaluation(name, grade, weight));
        }

        System.out.println("Evaluaciones registradas correctamente. Peso total = " + totalWeight + "%");
    }

    // 3) Registrar asistencia mínima
    private static void registerAttendance(Scanner scanner) {
        StudentRecord record = findStudent(scanner);
        if (record == null) {
            return;
        }

        System.out.print("¿Cumplió la asistencia mínima? (s/n): ");
        String ans = scanner.nextLine().trim().toLowerCase();
        boolean hasReached = ans.startsWith("s");
        record.setHasReachedMinimumClasses(hasReached);

        System.out.println("Asistencia mínima registrada: " + (hasReached ? "Sí" : "No"));
    }

    // 4) Registrar política de puntos extra por año
    private static void registerYearPolicy(Scanner scanner) {
        System.out.print("Año académico (ej. 2025): ");
        int year = readInt(scanner);

        System.out.print("¿Todos los docentes están de acuerdo en otorgar puntos extra este año? (s/n): ");
        String ans = scanner.nextLine().trim().toLowerCase();
        boolean enabled = ans.startsWith("s");

        AcademicYearPolicy policy = yearPolicies.get(year);
        if (policy == null) {
            policy = new AcademicYearPolicy(year, enabled);
        } else {
            policy.setExtraPointsEnabled(enabled);
        }
        yearPolicies.put(year, policy);

        System.out.println("Política de puntos extra para " + year + ": " + (enabled ? "ACTIVADA" : "DESACTIVADA"));
    }

    // 5) Calcular nota final y mostrar detalle
    private static void calculateAndShowFinalGrade(Scanner scanner) {
        StudentRecord record = findStudent(scanner);
        if (record == null) {
            return;
        }

        AcademicYearPolicy policy = yearPolicies.get(record.getAcademicYear());
        GradeBreakdown breakdown = calculator.calculate(record, policy);

        System.out.println("============================");
        System.out.println("Detalle del cálculo de nota");
        System.out.println("Estudiante: " + record.getStudentName() + " (" + record.getStudentId() + ")");
        System.out.println("Año académico: " + record.getAcademicYear());
        System.out.println("----------------------------");
        System.out.printf("Promedio ponderado: %.2f%n", breakdown.getWeightedAverage());
        System.out.printf("Penalización por inasistencia: %.2f%n", breakdown.getAttendancePenalty());
        System.out.printf("Puntos extra base: %.2f%n", breakdown.getBaseExtraPoints());
        System.out.printf("Puntos extra duplicados: %.2f%n", breakdown.getDuplicatedExtraPoints());
        System.out.println("----------------------------");
        System.out.printf("NOTA FINAL: %.2f%n", breakdown.getFinalGrade());
    }

    // --- Helpers ---

    private static StudentRecord findStudent(Scanner scanner) {
        System.out.print("Código del estudiante: ");
        String id = scanner.nextLine().trim();
        StudentRecord record = students.get(id);
        if (record == null) {
            System.out.println("No se encontró estudiante con código " + id + ". Regístrelo primero.");
        }
        return record;
    }

    private static int readInt(Scanner scanner) {
        while (true) {
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.print("Valor inválido, ingrese un entero: ");
            }
        }
    }

    private static double readDouble(Scanner scanner) {
        while (true) {
            String line = scanner.nextLine().trim();
            try {
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.print("Valor inválido, ingrese un número: ");
            }
        }
    }
}
