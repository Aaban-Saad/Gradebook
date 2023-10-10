package application;

import javafx.scene.control.CheckBox;

public class Assessment {
	private String assessmentName;
	private double assessmentFullMark;
	private double assessmentWeight;
	private boolean countableForGrade;
	
	private CheckBox countableForGradeCheckBox = new CheckBox();
	
	public String getAssessmentName() {
		return assessmentName;
	}
	public void setAssessmentName(String assessmentName) {
		this.assessmentName = assessmentName;
	}
	public double getAssessmentFullMark() {
		return assessmentFullMark;
	}
	public void setAssessmentFullMark(double assessmentFullMark) {
		this.assessmentFullMark = assessmentFullMark;
	}
	public double getAssessmentWeight() {
		return assessmentWeight;
	}
	public void setAssessmentWeight(double assessmentWeight) {
		this.assessmentWeight = assessmentWeight;
	}
	public boolean isCountableForGrade() {
		if(countableForGradeCheckBox.isSelected()) countableForGrade = true;
		else countableForGrade = false;
		return countableForGrade;
	}
	public void setCountForGrade(boolean countForGrade) {
		this.countableForGrade = countForGrade;
		this.countableForGradeCheckBox.setSelected(countForGrade);
	}
	public CheckBox getCountableForGradeCheckBox() {
		return countableForGradeCheckBox;
	}
	public void setCountableForGradeCheckBox(CheckBox countableForGradeCheckBox) {
		this.countableForGradeCheckBox = countableForGradeCheckBox;
		System.out.println(isCountableForGrade());
	}
	
	
}
