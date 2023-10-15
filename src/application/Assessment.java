package application;

import javafx.scene.control.CheckBox;

// The Assessment class represents an assessment in a course, such as a test or assignment.
public class Assessment {
    
    // Fields to store information about the assessment.
    private String assessmentName;         // Name of the assessment.
    private double assessmentFullMark;     // Maximum possible marks for the assessment.
    private double assessmentWeight;       // Weight or importance of the assessment in the course.
    private boolean countableForGrade;     // Flag indicating if the assessment contributes to the final grade.
    
    // CheckBox for the countableForGrade property in the user interface.
    private CheckBox countableForGradeCheckBox = new CheckBox();
    
    // Getter method for assessmentName.
    public String getAssessmentName() {
        return assessmentName;
    }
    
    // Setter method for assessmentName.
    public void setAssessmentName(String assessmentName) {
        this.assessmentName = assessmentName;
    }
    
    // Getter method for assessmentFullMark.
    public double getAssessmentFullMark() {
        return assessmentFullMark;
    }
    
    // Setter method for assessmentFullMark.
    public void setAssessmentFullMark(double assessmentFullMark) {
        this.assessmentFullMark = assessmentFullMark;
    }
    
    // Getter method for assessmentWeight.
    public double getAssessmentWeight() {
        return assessmentWeight;
    }
    
    // Setter method for assessmentWeight.
    public void setAssessmentWeight(double assessmentWeight) {
        this.assessmentWeight = assessmentWeight;
    }
    
    // Getter method for countableForGrade.
    public boolean isCountableForGrade() {
        // Checks if the countableForGradeCheckBox is selected to determine if the assessment is countable for grade.
        if (countableForGradeCheckBox.isSelected()) 
            countableForGrade = true;
        else 
            countableForGrade = false;
        return countableForGrade;
    }
    
    // Setter method for countableForGrade.
    public void setCountForGrade(boolean countForGrade) {
        this.countableForGrade = countForGrade;
        // Sets the state of the countableForGradeCheckBox based on the input.
        this.countableForGradeCheckBox.setSelected(countForGrade);
    }
    
    // Getter method for countableForGradeCheckBox.
    public CheckBox getCountableForGradeCheckBox() {
        return countableForGradeCheckBox;
    }
    
    // Setter method for countableForGradeCheckBox.
    public void setCountableForGradeCheckBox(CheckBox countableForGradeCheckBox) {
        this.countableForGradeCheckBox = countableForGradeCheckBox;
        // Prints the current state of countableForGrade after setting the CheckBox.
        System.out.println(isCountableForGrade());
    }
}
