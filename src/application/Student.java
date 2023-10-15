package application;

import java.util.ArrayList;

// The Student class represents a student in a course, with information such as ID, name, assessments, and grade.
public class Student {
	
	// Fields to store information about the student.
	private int sn = -1;                   // Serial number or unique identifier for the student.
	private String id;                     // ID of the student.
	private String name;                   // Name of the student.
	private String grade;                  // Grade achieved by the student.
	
	// Static field to store assessment names shared across all instances of the class.
	private static ArrayList<String> assessmentNames = new ArrayList<>();
	
	// Dynamic field to store assessment marks for the specific student instance.
	private ArrayList<String> assessmentMarks = new ArrayList<>(); 
	
	// Default constructor initializes the student with empty ID and name.
	public Student() {
		this.id = "";
		this.name = "";
	}
	
	// Parameterized constructor allows setting the ID and name of the student.
	public Student(String id, String name) {
		this.id = id;
		this.name = name;
	}
	
	// Getter method for sn.
	public int getSn() {
		return sn;
	}
	
	// Setter method for sn.
	public void setSn(int sn) {
		this.sn = sn;
	}
	
	// Getter method for id.
	public String getId() {
		return id;
	}
	
	// Setter method for id.
	public void setId(String id) {
		this.id = id;
	}
	
	// Getter method for name.
	public String getName() {
		return name;
	}
	
	// Setter method for name.
	public void setName(String name) {
		this.name = name;
	}
	
	// Static getter method for assessmentNames.
	public static ArrayList<String> getAssessmentNames() {
		return Student.assessmentNames;
	}
	
	// Static setter method for assessmentNames.
	public static void setAssessmentNames(ArrayList<String> assessmentNames) {
		Student.assessmentNames = assessmentNames;
	}
	
	// Getter method for assessmentMarks.
	public ArrayList<String> getAssessmentMarks() {
		return this.assessmentMarks;
	}
	
	// Setter method for assessmentMarks.
	public void setAssessmentMarks(ArrayList<String> assessmentMarks) {
		this.assessmentMarks = assessmentMarks;
	}
	
	// Method to get the mark for a specific assessment.
	public String getMark(String assessmentName) {
		int i;
		for(i = 0; i < Student.assessmentNames.size(); i++) {
			if(Student.assessmentNames.get(i).equals(assessmentName)) {
				return assessmentMarks.get(i);
			}
		}
		return "0.0";
	}
	
	// Method to set the mark for a specific assessment.
	public void setMark(String assessmentName, String mark) {
		int i;
		for(i = 0; i < assessmentNames.size(); i++) {
			if(assessmentNames.get(i).equals(assessmentName)) {
				assessmentMarks.set(i, mark);
			}
		}
	}
	
	// Getter method for grade.
	public String getGrade() {
		return grade;
	}
	
	// Setter method for grade.
	public void setGrade(String grade) {
		this.grade = grade;
	}
}
