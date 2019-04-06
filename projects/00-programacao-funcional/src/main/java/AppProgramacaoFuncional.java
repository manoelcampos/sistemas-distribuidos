import java.util.List;

/**
 * Aplicação de exemplo de princípios de programação funcional em Java,
 * Expressões Lambda e API de Streams do Java 8.
 *
 * Para aprofundar nestes assuntos, veja os links abaixo:
 *
 * <ul>
 * <li><a href=
 * "https://www.oracle.com/technetwork/pt/articles/java/streams-api-java-8-3410098-ptb.html">Curso
 * Streams e Expressões Lambda do Java 8</a></li>
 * <li><a href= "http://bit.ly/2I2U5bU">Curso JDK 8 MOOC: Lambdas and Streams
 * Introduction</a></li>
 * </ul>
 *
 * @author Manoel Campos da Silva Filho
 */
public class AppProgramacaoFuncional {
    private static final int TOTAL_STUDENTS = 1000;
    private final List<Student> students;

    public AppProgramacaoFuncional(){
        students = StudentGenerator.generate(TOTAL_STUDENTS);
    }

    public static void main(String[] args) {
        AppProgramacaoFuncional app = new AppProgramacaoFuncional();
        app.printStudents();
        System.out.println(app.maxScore());
    }

    private void printStudents(){
        for (Student student : students) {
            System.out.println(student);
        }
    }

    private double maxScore(){
        return students.stream()
                .filter(student -> filterStudents(student))
                .mapToDouble(student -> student.getScore())
                .max()
                .orElse(0.0);
    }

    private double averageScore(){
        return students.stream()
                .filter(student -> filterStudents(student))
                .mapToDouble(student -> student.getScore())
                .average()
                .orElse(0.0);
    }

    private boolean filterStudents(Student student) {
        return student.getGradYear() == 2011;
    }
}

