import java.util.*;

public class Student {
    private String studentId;
    private int tokenCount;
    private HashMap<Course, Integer> courseTokens;
    private double unhappyScore;
    private List<AbstractMap.SimpleEntry<Course, Boolean>> enrolledToAppliedCourses;

    private boolean enrolledToAtLeastOneCourse = false;
    private int enrolledCount;

    private int appliedCount;

    public Student(String studentId, int tokenCount) {
        this.studentId = studentId;
        this.tokenCount = tokenCount;
        this.courseTokens = new HashMap<Course, Integer>();
        enrolledToAppliedCourses = new ArrayList<>();
    }

    public String getStudentId() {
        return studentId;
    }

    public void AssignTokens(Course course, int tokenCount) {
        if (tokenCount <= this.tokenCount && tokenCount > 0) {
            this.tokenCount -= tokenCount;
            courseTokens.put(course, tokenCount);
            enrolledToAppliedCourses.add(new AbstractMap.SimpleEntry<>(course, false));
            course.AddBid(this, tokenCount);
            appliedCount++;
        }
        else if (tokenCount > this.tokenCount)
        {
            System.out.println("Student " + studentId + " does not have enough tokens to bid on " + course.getCourseName());
            throw new IllegalArgumentException("Student " + studentId + " does not have enough tokens to bid on " + course.getCourseName());
        }
    }

    public int getAssignedToken(Course course) {
        if (courseTokens.containsKey(course)) {
            return courseTokens.get(course);
        }
        else throw new IllegalArgumentException("Student " + studentId + " is not enrolled to " + course.getCourseName());
    }

    public void EnrollToCourse(Course course)
    {
        if (enrolledToAppliedCourses.stream().anyMatch(c -> c.getKey().getCourseName().equals(course.getCourseName())))
        {
            enrolledToAppliedCourses.stream().filter(entry -> entry.getKey().getCourseName().equals(course.getCourseName())).findFirst().get().setValue(true);
        }
        enrolledCount++;
        enrolledToAtLeastOneCourse = true;
    }

    public void CalculateUnhappiness(double h)
    {
        if (tokenCount != 0)
        {
            throw new ArithmeticException("Student " + studentId + " has " + tokenCount + " tokens left");
        }

        enrolledToAppliedCourses.stream()
                .forEach(entry -> {
                    if (!entry.getValue()) unhappyScore += (-100/h) * Math.log(1 - getAssignedToken(entry.getKey())/100.0);
                });

        if (!enrolledToAtLeastOneCourse) unhappyScore *= unhappyScore;
        if (unhappyScore > 100) unhappyScore = 100;
    }

    public double GetUnhappinessScore()
    {
        return unhappyScore;
    }

    public boolean canEnroll()
    {
        return enrolledCount < appliedCount;
    }
}
