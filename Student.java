import java.util.*;

public class Student {
    private String studentId;
    private int tokenCount;
    private HashMap<Course, Integer> courseTokens;
    private double unhappyScore;
    private List<AbstractMap.SimpleEntry<Course, Boolean>> enrolledToAppliedCourses;

    private int appliedCourseCount;
    public Student(String studentId) {
        this.studentId = studentId;
        this.tokenCount = 100;
        this.courseTokens = new HashMap<Course, Integer>();
        enrolledToAppliedCourses = new ArrayList<>();
    }

    public String getStudentId() {
        return studentId;
    }

    public int getTokenCount() {
        return tokenCount;
    }

    public void AssignTokens(Course course, int tokenCount) {
        if (tokenCount <= this.tokenCount && tokenCount > 0) {
            this.tokenCount -= tokenCount;
            courseTokens.put(course, tokenCount);
            enrolledToAppliedCourses.add(new AbstractMap.SimpleEntry<>(course, false));
            course.AddBid(this, tokenCount);
            appliedCourseCount++;
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
        enrolledToAppliedCourses.stream().filter(entry -> entry.getKey().getCourseName().equals(course.getCourseName())).findFirst().get().setValue(true);
    }

    public void CalculateUnhappiness(double h)
    {
        if (tokenCount != 0)
        {
            throw new ArithmeticException("Student " + studentId + " has " + tokenCount + " tokens left");
        }
        final boolean[] assignedToClass = {false};

        enrolledToAppliedCourses.stream()
                .forEach(entry -> {
                    if (entry.getValue() == true) {
                        assignedToClass[0] = true;
                    }
                    if (!entry.getValue()) unhappyScore += (-100/h) * Math.log(1 - getAssignedToken(entry.getKey())/100.0);
                });

        if (!assignedToClass[0]) unhappyScore *= unhappyScore;
        if (unhappyScore > 100) unhappyScore = 100;
    }

    public double GetUnhappinessScore()
    {
        return unhappyScore;
    }
}
