package atafies.degreehacks;

import java.util.ArrayList;

/**
 * Created by Adriel on 1/7/2016.
 */
public class Classroom {
    private String className = "";
    private long classroomID;
    private ArrayList<Assignment> assignments = new ArrayList<>();
    private double classGrade = 0;
    private double weightTotal = 0;

    public Classroom() {
    }

    public Classroom(String name, long classroomID) {
        className = name;
        this.classroomID = classroomID;
    }

    public long getClassroomID() {
        return classroomID;
    }

    public void setClassroomID(long classroomID) {
        this.classroomID = classroomID;
    }

    public void setWeight(Assignment as, double weight) {
        as.setWeight(weight);
    }

    public void addAssignment(String assignmentName, long assignmentID, double weight) {
        Assignment assignment = new Assignment(assignmentName, assignmentID, weight);
        assignments.add(assignment);

    }

    public double getWeightTotal() {
        return weightTotal;
    }

    public double findClassGrade() {
        double avg = 0;
        double w = 0;
        for (int i = 0; i < assignments.size(); i++) {
            avg += assignments.get(i).getAverage() * (assignments.get(i).getWeight() / 100);
            w += assignments.get(i).getWeight();
        }
        weightTotal = w;
        classGrade = avg;
        return avg;///todo: figure out how to get averages to show
    }

    public void deleteAssignment(long assignID) {
        for (Assignment as : assignments) {
            if (as.getAssignmentID() == assignID) {
                assignments.remove(as);
                break;
            }
        }
    }

    public ArrayList<Assignment> getAssignments() {
        return assignments;
    }

    public void addGrade(long id, double grade) {
        for (Assignment as : assignments) {
            if (as.getAssignmentID() == id) {
                as.addGrade(grade);
                break;
            }
        }
    }

    public void setGrades(long assignID, ArrayList<Double> list) {
        for (Assignment as : assignments) {
            if (as.getAssignmentID() == assignID) {
                as.setGrades(list);
                break;
            }
        }
    }

    public void removeGrades(long assignID) {
        for (Assignment as : assignments) {
            if (as.getAssignmentID() == assignID) {
                as.deleteGrades();
                break;
            }
        }
    }

    public double getClassGrade() {
        return classGrade;
    }

    public void setClassGrade(double grade) {
        classGrade = grade;
    }

    public Assignment getAssignment(long assignmentID) {
        for (Assignment as : assignments) {
            if (as.getAssignmentID() == assignmentID) {
                return as;
            }
        }
        return null;
    }


}

