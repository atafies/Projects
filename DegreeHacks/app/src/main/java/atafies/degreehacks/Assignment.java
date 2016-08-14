package atafies.degreehacks;

import java.util.ArrayList;

/**
 * Created by Adriel on 1/11/2016.
 */
public class Assignment{
    private long assignmentID;
    private String assignmentName;
    private double weight;
    private ArrayList<Double> grades = new ArrayList<Double>();
    private double average;

    public Assignment(String assignmentName,long assignmentID,double weight) {
        this.weight = weight;
        this.assignmentName = assignmentName;
        this.assignmentID = assignmentID;
    }
    public Assignment(String assignmentName,long assignmentID,double weight,double grade){
        this.weight = weight;
        this.assignmentName = assignmentName;
        grades.add(grade);
        average = grade;
        this.assignmentID = assignmentID;
    }

    public ArrayList<Double> getGrades() {
        return grades;
    }
    public double[] getGradesArray(){
        double[] array = new double[grades.size()];
        for (int i = 0;i < array.length; i++) {
            array[i] = grades.get(i);
        }

        return array;
    }
    public void deleteGrades(){
        grades = new ArrayList<>();
    }

    public long getAssignmentID() {
        return assignmentID;
    }

    public void setAssignmentID(long assignmentID) {
        this.assignmentID = assignmentID;
    }

    public double getAverage(){
        double sum = 0;
        for (Double grade:grades){
            sum += grade;
        }
        average = sum/grades.size();
        return average;
    }

    public void addGrade(double addgrade){
        grades.add(addgrade);
        getAverage();
    }
    public void remove(double g){
        grades.remove(g);
    }

    public String getAssignmentName() {
        return assignmentName;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
    public void setGrades(ArrayList<Double> grades) {
        this.grades = grades;
    }
}