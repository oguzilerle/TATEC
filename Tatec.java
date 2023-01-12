import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.*;

public class Tatec
{
    private static final int CORRECT_TOTAL_TOKEN_PER_STUDENT = 100;
    private static final String OUT_TATEC_UNHAPPY = "unhappyOutTATEC.txt";
    private static final String OUT_TATEC_ADMISSION = "admissionOutTATEC.txt";
    private static final String OUT_RAND_UNHAPPY = "unhappyOutRANDOM.txt";
    private static final String OUT_RAND_ADMISSION = "admissionOutRANDOM.txt";

    public static void main(String args[])
    {
        if(args.length < 4)
        {
            System.err.println("Not enough arguments!");
            return;
        }

        // File Paths
        String courseFilePath = args[0];
        String studentIdFilePath = args[1];
        String tokenFilePath = args[2];
        double h;

        try { h = Double.parseDouble(args[3]);}
        catch (NumberFormatException ex)
        {
            System.err.println("4th argument is not a double!");
            return;
        }

        List<Course> courses = ParseCourseFile(courseFilePath);
        List<Student> students = ParseStudentIds(studentIdFilePath);
        AddBids(students, courses, tokenFilePath);
        courses.forEach(Course::EnrollStudents);
        students.forEach(student -> student.CalculateUnhappiness(h));
        WriteTATECAdmission(courses);
        WriteTATECUnhappiness(students);
    }

    private static List<Course> ParseCourseFile(String courseFilePath)
    {
        Path path = Paths.get(courseFilePath);

        try
        {
            List<Course> courses = Files.lines(path)
                    .map(line -> line.split(","))
                    .map(line -> new Course(line[0], Integer.parseInt(line[1])))
                    .collect(Collectors.toList());

            return courses;
        }
        catch (IOException e)
        {
            System.err.println("Error reading file: " + e.getMessage());
        }

        return null;
    }

    private static List<Student> ParseStudentIds(String studentIdsPath)
    {
        Path path = Paths.get(studentIdsPath);

        try
        {
            List<Student> students = Files.lines(path)
                    .map(line -> new Student(line))
                    .collect(Collectors.toList());

            return students;
        }
        catch (IOException e)
        {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return null;
    }

    private static void AddBids(List<Student> students, List<Course> courses, String tokenFilePath)
    {
        Path path = Paths.get(tokenFilePath);

        try
        {
            AtomicInteger studentIndex = new AtomicInteger(0);
            Files.lines(path)
                    .map(line -> line.split(","))
                    .forEach(line -> {
                        Student student = students.get(studentIndex.get());

                        for (int i = 0; i < courses.size(); i++)
                        {
                            int bid = Integer.parseInt(line[i]);
                            student.AssignTokens(courses.get(i), bid);
                        }
                        studentIndex.incrementAndGet();
                    });
        }
        catch (IOException e)
        {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    private static void WriteTATECAdmission(List<Course> courses)
    {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(OUT_TATEC_ADMISSION))) {
            courses.stream().map(course -> course.getCourseName() + ", " + course.getEnrolledStudents().stream().map(Student::getStudentId).collect(Collectors.joining(", "))).forEach(line -> {
                try {
                    writer.write(line);
                    writer.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void WriteTATECUnhappiness(List<Student> students)
    {
        Double averageUnhappinessScore = students.stream()
                .mapToDouble(Student::GetUnhappinessScore)
                .average()
                .orElse(0);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(OUT_TATEC_UNHAPPY))) {
            writer.write(averageUnhappinessScore.toString());
            writer.newLine();
            students.stream().map(student -> student.GetUnhappinessScore()).forEach(line -> {
                try {
                    writer.write(line.toString());
                    writer.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
