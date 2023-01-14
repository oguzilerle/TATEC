import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
        SortBids();
        AtomicInteger lastEnrolledBid = new AtomicInteger(100);
        List<AbstractMap.SimpleEntry<Student, Integer>> remainingBids = bids.stream()
                .filter(bid -> enrolledStudents.size() < courseCapacity)
                .peek(bid -> {
                    enrolledStudents.add(bid.getKey());
                    bid.getKey().EnrollToCourse(this);
                    lastEnrolledBid.set(bid.getValue());;
                })
                .collect(Collectors.toList());
        bids.removeAll(remainingBids);
        final int finalEnrolledBid = lastEnrolledBid.get();
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
    }

    public void SortBids()
    {
        bids = bids.stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                .collect(Collectors.toList());
    }


    public List<AbstractMap.SimpleEntry<Student, Integer>> getBidsList()
    {
        return bids;
    }
}
