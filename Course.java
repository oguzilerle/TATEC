import java.util.*;

public class Course {
    private String courseName;
    private int courseCapacity;
    private HashSet<Student> enrolledStudents;
    private List<AbstractMap.SimpleEntry<Student, Integer>> bids;

    public Course(String courseName, int courseCapacity) {
        this.courseName = courseName;
        this.courseCapacity = courseCapacity;
        this.enrolledStudents = new HashSet<Student>();
        this.bids = new ArrayList<>();
    }

    public String getCourseName() {
        return courseName;
    }

    public int getCourseCapacity() {
        return courseCapacity;
    }

    public List<Student> getEnrolledStudents() {
        return new ArrayList<Student>(enrolledStudents);
    }

    public void EnrollStudents() {
        int lastEnrolledBid = 100;
        while (enrolledStudents.size() < courseCapacity && bids.size() > 0) {
            enrolledStudents.add(bids.get(0).getKey());
            bids.get(0).getKey().EnrollToCourse(this);
            lastEnrolledBid = bids.get(0).getValue();
            bids.remove(0);
        }
        final int finalEnrolledBid = lastEnrolledBid;
        bids.stream()
                .filter(bid -> bid.getValue() == finalEnrolledBid)
                .forEach(bid -> {
                    enrolledStudents.add(bid.getKey());
                    bid.getKey().EnrollToCourse(this);
                });
    }

    public void EnrollStudentsRandomly()
    {
        Collections.shuffle(bids);
        bids.stream()
                .filter(bid -> enrolledStudents.size() < courseCapacity)
                .limit(courseCapacity - enrolledStudents.size())
                .forEach(bid -> {
                    enrolledStudents.add(bid.getKey());
                    bid.getKey().EnrollToCourse(this);
                });
    }

    public void AddBid(Student student, int bid)
    {
        bids.add(new AbstractMap.SimpleEntry<>(student,bid));
        for (int i = 1; i < bids.size(); i++) {
            AbstractMap.SimpleEntry current = bids.get(i);
            int j = i - 1;
            int currentValue = bids.get(i).getValue().intValue();
            while (j >= 0 && bids.get(j).getValue().intValue() < currentValue) {
                bids.set(j + 1, bids.get(j));
                j--;
            }
            bids.set(j + 1, current);
        }
    }


    public List<AbstractMap.SimpleEntry<Student, Integer>> getBidsList()
    {
        return bids;
    }
}
