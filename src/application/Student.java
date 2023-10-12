package application;

import java.util.ArrayList;

public class Student {
	private int sn = -1;
	private String id;
	private String name;
	private String grade;

	private static ArrayList<String> assessmentNames = new ArrayList<>();
	private ArrayList<String> assessmentMarks = new ArrayList<>(); 
	
	public Student() {
		this.id = "";
		this.name = "";
	}
	public Student(String id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public int getSn() {
		return sn;
	}
	public void setSn(int sn) {
		this.sn = sn;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public static ArrayList<String> getAssessmentNames() {
		return Student.assessmentNames;
	}
	public static void setAssessmentNames(ArrayList<String> assessmentNames) {
		Student.assessmentNames = assessmentNames;
	}
	public ArrayList<String> getAssessmentMarks() {
		return this.assessmentMarks;
	}
	public void setAssessmentMarks(ArrayList<String> assessmentMarks) {
		this.assessmentMarks = assessmentMarks;
	}
	
	public String getMark(String assessmentName) {
		int i;
		for(i = 0; i < Student.assessmentNames.size(); i++) {
			if(Student.assessmentNames.get(i).equals(assessmentName)) {
				return assessmentMarks.get(i);
			}
		}
		return "0.0";
	}
	
	public void setMark(String assessmentName, String mark) {
		int i;
		for(i = 0; i < assessmentNames.size(); i++) {
			if(assessmentNames.get(i).equals(assessmentName)) {
				assessmentMarks.set(i, mark);
			}
		}
	}
	
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	
}
