package application;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Iterator;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

public class Controller extends ControllerSkeleton implements Initializable{
    // ArrayList to store assessment names for the Student class
    private ArrayList<String> assessmentNames = new ArrayList<>();

    // ArrayList to store Assessment objects for the mini right table
    private ArrayList<Assessment> assessmentsArrayList = new ArrayList<>();

    // Version of the application
    final String version = "1.0";

    // The currently opened or saved file
    File savingFile;

    // Flag to indicate whether the table is locked
    private boolean tableIsLocked = false;

    // Flag to indicate whether a save file already exists
    private boolean saveFileExists = false;

    // Flag to track whether any changes have been made to the file
    private boolean fileIsSaved = false;
  
	
	/**
	 * Handles the action of adding student IDs and names to the table.
	 *
	 */
	@Override
	void addIdName() {
	    if (tableIsLocked) return;

	    int i, j;
	    String[] idStrings = enterId.getText().split("\n");
	    String[] nameStrings = enterName.getText().split("\n");
	    int length = idStrings.length <= nameStrings.length ? idStrings.length : nameStrings.length;

	    Student[] student = new Student[length];

	    for (i = 0; i < length; i++) {
	        student[i] = new Student();
	        student[i].setSn(tableView.getItems().size() + 1);
	        try {
	            student[i].setId(idStrings[i].trim());
	        } catch (Exception e) {
	            // Handle exceptions (e.g., invalid ID format)
	            break;
	        }
	        student[i].setName(nameStrings[i].trim());

	        if (student[i].getName().equals("")) {
	            // Handle empty names
	            break;
	        }

	        Student.setAssessmentNames(assessmentNames);

	        // Setting default marks ("") for each assessment in the student class
	        ArrayList<String> assessmentMarks = student[i].getAssessmentMarks();
	        for (j = 0; j < this.assessmentNames.size(); j++) {
	            assessmentMarks.add("");
	            student[i].setAssessmentMarks(assessmentMarks);
	        }

	        ObservableList<Student> students = tableView.getItems();
	        students.add(student[i]);
	        tableView.setItems(students);
	    }

	    // Clear input fields
	    enterId.clear();
	    enterName.clear();

	    // Indicate that the file is not saved
	    fileIsSaved = false;

	    // Set the footer status
	    setFooter(savingFile);
	}
	
	/**
	 * Handles the action of adding an assessment to the table and related components.
	 *
	 */
	@Override
	void addAssessment() {
	    if (tableIsLocked) return;

	    // Get the assessment name from the input field
	    String assessmentName = enterAssessment.getText().trim();

	    // Check if the assessment name is empty and return if it is
	    if (assessmentName.equals("")) return;

	    // Create a new TableColumn for the assessment
	    TableColumn<Student, String> assessmentCol = new TableColumn<>(assessmentName);
	    assessmentNames.add(assessmentName);

	    // Create a new Assessment object and add it to the list of assessments
	    Assessment assessment = new Assessment();
	    assessment.setAssessmentName(assessmentName);
	    assessment.setAssessmentFullMark(0.0);
	    assessment.setAssessmentWeight(0.0);
	    assessmentsArrayList.add(assessment);

	    // Get the list of students from the table
	    ObservableList<Student> students = tableView.getItems();

	    int i;

	    // Iterate through each student and add an empty mark for the new assessment
	    for (i = 0; i < students.size(); i++) {
	        Student student = students.get(i);
	        ArrayList<String> assessmentMarks = student.getAssessmentMarks();
	        assessmentMarks.add("");
	        student.setAssessmentMarks(assessmentMarks);
	    }

	    // Configure the assessment column to display the assessment marks
	    assessmentCol.setCellValueFactory(c -> {
	        Student student = c.getValue();
	        String mark = student.getMark(assessmentName);

	        return new ReadOnlyObjectWrapper<>(mark);
	    });

	    assessmentCol.setCellFactory(TextFieldTableCell.forTableColumn());

	    // Handle edits to the assessment marks
	    assessmentCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Student, String>>() {
	        @Override
	        public void handle(CellEditEvent<Student, String> arg0) {
	            if (!tableIsLocked) {
	                Student student = arg0.getRowValue();
	                String newMark = arg0.getNewValue();
	                student.setMark(assessmentName, newMark);
	                fileIsSaved = false;
	                setFooter(savingFile);
	            }
	            tableView.refresh();
	        }
	    });

	    // Add the assessment column to the table
	    tableView.getColumns().add(assessmentCol);
	    tableView.setItems(students);

	    // Update the rightmost mini assessment table
	    ObservableList<Assessment> assessmentsObservableList = assessmentMarksTable.getItems();
	    for (i = 0; i < assessmentsArrayList.size(); i++) {
	        try {
	            assessmentsObservableList.set(i, assessmentsArrayList.get(i));
	        } catch (Exception e) {
	            assessmentsObservableList.add(assessmentsArrayList.get(i));
	        }
	    }
	    assessmentMarksTable.setItems(assessmentsObservableList);

	    // Update the assessment choice box for removing assessments later
	    assessmentChoiceBox.getItems().removeAll(assessmentNames);
	    assessmentChoiceBox.getItems().addAll(assessmentNames);
	    Student.setAssessmentNames(assessmentNames);

	    // Update the choice box for mark calculation
	    choiceBoxForMarkCalculation.getItems().removeAll(assessmentNames);
	    choiceBoxForMarkCalculation.getItems().addAll(assessmentNames);

	    // Add a checkbox in the left side list for mark calculation
	    CheckBox assessmentCheckBox = new CheckBox();
	    assessmentCheckBox.setText(assessmentName);
	    listViewForMarkCalculation.getItems().add(assessmentCheckBox);

	    // Clear the input field
	    enterAssessment.clear();

	    // Indicate that the file is not saved
	    fileIsSaved = false;

	    // Update the footer status
	    setFooter(savingFile);	
	}
	
	/**
	 * Recalculates and updates the serial numbers for students in the table.
	 */
	@Override
	void recalculateSerialNumber() {
	    // Check if the table is not locked
	    if (!tableIsLocked) {
	        ObservableList<Student> students = tableView.getItems();
	        int i;

	        // Recalculate and update serial numbers for students
	        for (i = 0; i < students.size(); i++) {
	            students.get(i).setSn(i + 1);
	        }

	        // Update the table and refresh its view
	        tableView.setItems(students);
	        tableView.refresh();
	    }

	    // Indicate that the file is not saved
	    fileIsSaved = false;

	    // Update the footer status
	    setFooter(savingFile);
	}
	
	/**
	 * Toggles the locking or unlocking of the table based on the user's selection.
	 *
	 */
	@Override
	void lockTable() {
	    // Set the table lock status based on the user's selection
	    tableIsLocked = lockTable.isSelected();
	}
	
	/**
	 * Removes a student from the table based on the provided student ID.
	 *
	 */
	@Override
	void removeStudent() {
	    // Check if the table is locked
	    if (tableIsLocked) return;

	    ObservableList<Student> students = tableView.getItems();
	    int i;

	    // Iterate through the list of students
	    for (i = 0; i < students.size(); i++) {
	        if (students.get(i).getId().equals(idToRemove.getText())) {
	            // Display a confirmation dialog for removing the student
	            Alert alert = new Alert(AlertType.CONFIRMATION);
	            alert.setTitle("Gradebook");
	            alert.setHeaderText("You are about to remove ID-" + idToRemove.getText() + " from the table.");
	            alert.setContentText("This action cannot be undone. Press 'Cancel' if you are not sure.");
	            
	            if (alert.showAndWait().get() == ButtonType.OK) {
	                // Remove the student if the user confirms
	                students.remove(i);
	            }
	            break;
	        }
	    }

	    // Clear the input field
	    idToRemove.clear();

	    // Indicate that the file is not saved
	    fileIsSaved = false;

	    // Update the footer status
	    setFooter(savingFile);
	}
	
	/**
	 * Removes an assessment column from the table and related components based on the selected assessment name.
	 *
	 */
	@Override
	void removeAssessment() {
	    // Check if the table is locked
	    if (tableIsLocked) return;

	    ObservableList<TableColumn<Student, ?>> tableColumns = tableView.getColumns();
	    ObservableList<Assessment> assessmentsToRemove = assessmentMarksTable.getItems(); // Some assessments will be removed from this list
	    String columnToRemove = assessmentChoiceBox.getValue();
	    int i;

	    // Iterate through the table columns to find the matching assessment column
	    for (i = 0; i < tableColumns.size(); i++) {
	        if (columnToRemove != null && columnToRemove.equals(tableColumns.get(i).getText())) {
	            // Display a confirmation dialog for removing the assessment
	            Alert alert = new Alert(AlertType.CONFIRMATION);
	            alert.setTitle("Gradebook");
	            alert.setHeaderText("You are about to remove " + tableColumns.get(i).getText() + " from the table.");
	            alert.setContentText("This action cannot be undone. Press 'Cancel' if you are not sure.");

	            if (alert.showAndWait().get() == ButtonType.OK) {
	                // Remove the assessment column from the table
	                tableView.getColumns().remove(i);

	                // Remove the assessment from the mini right table
	                for (i = 0; i < assessmentsToRemove.size(); i++) {
	                    if (assessmentsToRemove.get(i).getAssessmentName().equals(columnToRemove)) {
	                        assessmentsToRemove.remove(i);
	                        assessmentMarksTable.refresh();
	                        break;
	                    }
	                }

	                // Remove the assessment mark from the student class
	                ObservableList<Student> students = tableView.getItems();
	                for (Student student : students) {
	                    ArrayList<String> markStrings = student.getAssessmentMarks();
	                    for (i = 0; i < markStrings.size(); i++) {
	                        if (markStrings.get(i).equals(student.getMark(columnToRemove))) {
	                            markStrings.remove(i);
	                            break;
	                        }
	                    }
	                    student.setAssessmentMarks(markStrings);
	                }

	                // Remove the assessment from assessmentNames and related global components
	                for (i = 0; i < this.assessmentNames.size(); i++) {
	                    if (assessmentNames.get(i).equals(columnToRemove)) {
	                        assessmentNames.remove(i);
	                        assessmentsArrayList.remove(i);
	                        assessmentChoiceBox.getItems().remove(i);
	                        choiceBoxForMarkCalculation.getItems().remove(i);
	                        listViewForMarkCalculation.getItems().remove(i);
	                        break;
	                    }
	                }

	                // Update the assessment names in the Student class
	                Student.setAssessmentNames(assessmentNames);
	            }
	            break;
	        }
	    }

	    // Indicate that the file is not saved
	    fileIsSaved = false;

	    // Update the footer status
	    setFooter(savingFile);
	}

	
	/**
	 * Handles the selection of mark calculation methods and adjusts the input fields accordingly.
	 *
	 */
	@Override
	void markCalculationProcess() {
	    // Check the selected mark calculation method and enable/disable input fields accordingly
	    if (averageRadioButton.isSelected()) {
	        best_n_textField.setDisable(true);
	        bonusTextField.setDisable(true);
	    } else if (bestRadioButton.isSelected()) {
	        best_n_textField.setDisable(true);
	        bonusTextField.setDisable(true);
	    } else if (best_n_RadioButton.isSelected()) {
	        best_n_textField.setDisable(false);
	        bonusTextField.setDisable(true);
	    } else if (bonusRadioButton.isSelected()) {
	        best_n_textField.setDisable(true);
	        bonusTextField.setDisable(false);
	    }

	    // Indicate that the file is not saved
	    fileIsSaved = false;

	    // Update the footer status
	    setFooter(savingFile);
	}
	
	/**
	 * Calculates and updates the marks for students based on the selected mark calculation method and input values.
	 * 
	 */
	@Override
	void calculateMark() {
	    // Check if the table is locked
	    if (tableIsLocked) return;

	    if (averageRadioButton.isSelected()) {
	        int i, j;
	        ObservableList<CheckBox> checkBoxes = listViewForMarkCalculation.getItems();
	        ArrayList<String> selectedAssessments = new ArrayList<>();
	        ObservableList<Student> students = tableView.getItems();
	        String colName = choiceBoxForMarkCalculation.getValue();

	        // Initialize selectedAssessments
	        for (i = 0; i < checkBoxes.size(); i++) {
	            if (checkBoxes.get(i).isSelected()) {
	                selectedAssessments.add(checkBoxes.get(i).getText());
	            }
	        }

	        // Calculate the average
	        for (i = 0; i < students.size(); i++) {
	            float average = 0.0f;
	            for (j = 0; j < selectedAssessments.size(); j++) {
	                try {
	                    average += Float.parseFloat(students.get(i).getMark(selectedAssessments.get(j)));
	                } catch (Exception e) {
	                    average += 0.0f;
	                }
	            }
	            average /= selectedAssessments.size();

	            // Add or replace the calculated average to the specified mark column
	            float previousMark;
	            try {
	                previousMark = Float.parseFloat(students.get(i).getMark(colName));
	            } catch (Exception e) {
	                previousMark = 0.0f;
	            }

	            if (addRadioButton.isSelected()) {
	                students.get(i).setMark(colName, (previousMark + average) + "");
	            } else if (replaceRadioButton.isSelected()) {
	                students.get(i).setMark(colName, average + "");
	            }
	        }

	        tableView.setItems(students);
	        tableView.refresh();
	        averageRadioButton.setSelected(false);
	    } else if (bestRadioButton.isSelected()) {
	        int i, j;
	        ObservableList<CheckBox> checkBoxes = listViewForMarkCalculation.getItems();
	        ArrayList<String> selectedAssessments = new ArrayList<>();
	        float[] marksOfSelectedAssessments = new float[checkBoxes.size()]; // Marks of one student
	        ObservableList<Student> students = tableView.getItems();
	        String colName = choiceBoxForMarkCalculation.getValue();

	        // Initialize selectedAssessments
	        for (i = 0; i < checkBoxes.size(); i++) {
	            if (checkBoxes.get(i).isSelected()) {
	                selectedAssessments.add(checkBoxes.get(i).getText());
	            }
	        }

	        // Calculate the best
	        for (i = 0; i < students.size(); i++) {
	            marksOfSelectedAssessments = new float[checkBoxes.size()]; // Clear the array
	            for (j = 0; j < selectedAssessments.size(); j++) {
	                try {
	                    marksOfSelectedAssessments[j] = Float.parseFloat(students.get(i).getMark(selectedAssessments.get(j)));
	                } catch (Exception e) {
	                    marksOfSelectedAssessments[j] = 0.0f;
	                }
	            }
	            Arrays.sort(marksOfSelectedAssessments);
	            float best = marksOfSelectedAssessments[marksOfSelectedAssessments.length - 1];

	            // Add or replace the calculated best mark to the specified mark column
	            float previousMark;
	            try {
	                previousMark = Float.parseFloat(students.get(i).getMark(colName));
	            } catch (Exception e) {
	                previousMark = 0.0f;
	            }

	            if (addRadioButton.isSelected()) {
	                students.get(i).setMark(colName, (previousMark + best) + "");
	            } else if (replaceRadioButton.isSelected()) {
	                students.get(i).setMark(colName, best + "");
	            }
	        }

	        tableView.setItems(students);
	        tableView.refresh();
	        bestRadioButton.setSelected(false);
	    } else if (best_n_RadioButton.isSelected()) {
	        int i, j;
	        ObservableList<CheckBox> checkBoxes = listViewForMarkCalculation.getItems();
	        ArrayList<String> selectedAssessments = new ArrayList<>();
	        float[] marksOfSelectedAssessments; // Marks of one student
	        ObservableList<Student> students = tableView.getItems();
	        String colName = choiceBoxForMarkCalculation.getValue();

	        // Initialize selectedAssessments
	        for (i = 0; i < checkBoxes.size(); i++) {
	            if (checkBoxes.get(i).isSelected()) {
	                selectedAssessments.add(checkBoxes.get(i).getText());
	            }
	        }

	        // Calculate best_n average
	        for (i = 0; i < students.size(); i++) {
	            marksOfSelectedAssessments = new float[checkBoxes.size()];
	            for (j = 0; j < selectedAssessments.size(); j++) {
	                try {
	                    marksOfSelectedAssessments[j] = Float.parseFloat(students.get(i).getMark(selectedAssessments.get(j)));
	                } catch (Exception e) {
	                    marksOfSelectedAssessments[j] = 0.0f;
	                }
	            }
	            Arrays.sort(marksOfSelectedAssessments);

	            int bestN;
	            try {
	                bestN = Integer.parseInt(best_n_textField.getText());
	            } catch (Exception e) {
	                return;
	            }

	            float best_n_Average = 0.0f;
	            for (j = marksOfSelectedAssessments.length - 1; j > marksOfSelectedAssessments.length - bestN - 1; j--) {
	                best_n_Average += marksOfSelectedAssessments[j];
	            }
	            best_n_Average /= bestN;

	            // Add or replace the calculated best_n average to the specified mark column
	            float previousMark = 0.0f;
	            try {
	                previousMark = Float.parseFloat(students.get(i).getMark(colName));
	            } catch (Exception e) {
	                previousMark = 0.0f;
	            }

	            if (addRadioButton.isSelected()) {
	                students.get(i).setMark(colName, (previousMark + best_n_Average) + "");
	            } else if (replaceRadioButton.isSelected()) {
	                students.get(i).setMark(colName, best_n_Average + "");
	            }
	        }

	        tableView.setItems(students);
	        tableView.refresh();
	        best_n_RadioButton.setSelected(false);
	    } else if (bonusRadioButton.isSelected() && !bonusTextField.getText().equals("")) {
	        String colName = choiceBoxForMarkCalculation.getValue();
	        float markToAdd = Float.parseFloat(bonusTextField.getText());
	        ObservableList<Student> students = tableView.getItems();
	        int i;
	        for (i = 0; i < students.size(); i++) {
	            float previousMark = 0.0f;
	            try {
	                previousMark = Float.parseFloat(students.get(i).getMark(colName));
	            } catch (Exception e) {
	                previousMark = 0.0f;
	            } finally {
	                if (addRadioButton.isSelected()) {
	                    students.get(i).setMark(colName, (previousMark + markToAdd) + "");
	                } else if (replaceRadioButton.isSelected()) {
	                    students.get(i).setMark(colName, markToAdd + "");
	                }
	            }
	        }

	        tableView.setItems(students);
	        tableView.refresh();
	        bonusTextField.clear();
	        bonusRadioButton.setSelected(false);
	    }

	    // Indicate that the file is not saved
	    fileIsSaved = false;

	    // Update the footer status
	    setFooter(savingFile);
	}

	
	/**
	 * Calculates and updates the grade for each student based on the assessments and grading table.
	 *
	 */
	@Override
	void calculateGrade() {
	    // Check if the table is locked
	    if (tableIsLocked) return;

	    // Make the grade column visible
	    gradeCol.setVisible(true);

	    // Get the list of assessments used for grading
	    ObservableList<Assessment> assessmentsForGrade = assessmentMarksTable.getItems();
	    int i, j;

	    // Get the list of students
	    ObservableList<Student> students = tableView.getItems();

	    double finalScore;

	    // Calculate the grade for each student
	    for (i = 0; i < students.size(); i++) {
	        finalScore = 0.0;

	        for (j = 0; j < assessmentsForGrade.size(); j++) {
	            if (!assessmentsForGrade.get(j).isCountableForGrade()) continue;

	            String assessmentName = assessmentsForGrade.get(j).getAssessmentName();
	            double mark;

	            try {
	                mark = Double.parseDouble(students.get(i).getMark(assessmentName));
	            } catch (Exception ex) {
	                mark = 0.0;
	            }

	            // Calculate the final score based on weighted marks
	            finalScore += mark * assessmentsForGrade.get(j).getAssessmentWeight() / assessmentsForGrade.get(j).getAssessmentFullMark();
	        }

	        // Retrieve grade ranges from the grading table
	        ObservableList<Grade> grades = gradingTable.getItems();

	        for (Grade grade : grades) {
	            double min, max;

	            if (!grade.getMinNumber().trim().equals("*")) {
	                min = Double.parseDouble(grade.getMinNumber());
	            } else {
	                min = Double.MIN_VALUE;
	            }

	            if (!grade.getMaxNumber().trim().equals("*")) {
	                max = Double.parseDouble(grade.getMaxNumber());
	            } else {
	                max = Double.MAX_VALUE;
	            }

	            // Check if the final score falls within the grade range
	            if (Math.ceil(finalScore) >= min && finalScore <= max) {
	                // Set the grade for the student
	                students.get(i).setGrade(grade.getGradeName() + "  (" + finalScore + "%)");
	                break;
	            }
	        }
	    }

	    // Update the student table with the calculated grades
	    tableView.setItems(students);
	    tableView.refresh();

	    // Indicate that the file is not saved
	    fileIsSaved = false;

	    // Update the footer status
	    setFooter(savingFile);
	}

	
	/**
	 * Adds a new grade entry to the grading table.
	 *
	 */
	@Override
	void addGrade() {
	    // Create a new grade entry
	    Grade grade = new Grade();

	    // Get the list of grades from the grading table
	    ObservableList<Grade> grades = gradingTable.getItems();

	    // Add the new grade entry to the list
	    grades.add(grade);

	    // Set the updated list of grades in the grading table
	    gradingTable.setItems(grades);
	    gradingTable.refresh();

	    // Indicate that the file is not saved
	    fileIsSaved = false;

	    // Update the footer status
	    setFooter(savingFile);
	}

	
	/**
	 * Removes the selected grade entry from the grading table.
	 *
	 */
	@Override
	void removeGrade() {
	    // Get the index of the selected row in the grading table
	    int row = gradingTable.getSelectionModel().getSelectedIndex();

	    try {
	        // Remove the selected grade entry from the grading table
	        gradingTable.getItems().remove(row);
	    } catch (Exception ex) {
	        // Handle exceptions or errors (do nothing in this case)
	    }

	    // Indicate that the file is not saved
	    fileIsSaved = false;

	    // Update the footer status
	    setFooter(savingFile);
	}

	/**
	 * Sets the default grading table with common grade ranges and labels.
	 *
	 */
	@Override
	void getDefaultGradingTable() {
		// Get the list of grades from the grading table
	    ObservableList<Grade> grades = gradingTable.getItems();

	    // Clear the existing list
	    grades.clear();
	    
	    // Add default grade entries with common grade ranges and labels
		gradingTable.setItems(grades);
		grades.add(new Grade("93", "*", "A"));
		grades.add(new Grade("90", "92", "A-"));
		grades.add(new Grade("87", "89", "B+"));
		grades.add(new Grade("83", "86", "B"));
		grades.add(new Grade("80", "82", "B-"));
		grades.add(new Grade("77", "79", "C+"));
		grades.add(new Grade("73", "76", "C"));
		grades.add(new Grade("70", "72", "C-"));
		grades.add(new Grade("97", "69", "D+"));
		grades.add(new Grade("60", "66", "D"));
		grades.add(new Grade("*", "59", "F"));
		
		// Set the updated list of grades in the grading table
	    gradingTable.setItems(grades);
	    gradingTable.refresh();

	    // Indicate that the file is not saved
	    fileIsSaved = false;

	    // Update the footer status
	    setFooter(savingFile);
	}
	
	/**
	 * Saves the current data to the existing file, if it exists. If the file does not exist,
	 * it performs a "Save As" operation to choose a file location.
	 */
	@Override
	void saveFile() {
	    if (!saveFileExists) {
	        // If the save file does not exist, perform a "Save As" operation
	        saveFileExists = true;
	        saveAs();
	        return;
	    }

	    // Create lists to store student and grade data
	    ArrayList<Student> students = new ArrayList<>();
	    ArrayList<Grade> grades = new ArrayList();

	    // Retrieve data from the TableView and grading table
	    ObservableList<Student> studentList = tableView.getItems();
	    ObservableList<Grade> gradeList = gradingTable.getItems();

	    // Populate the student and grade lists
	    for (Student student : studentList) {
	        students.add(student);
	    }
	    for (Grade grade : gradeList) {
	        grades.add(grade);
	    }

	    // Create a GradeWriter instance to write data to the savingFile
	    GradeWriter gradeWriter = new GradeWriter(savingFile);

	    // Write the gradebook version, assessment data, student data, and grade data to the file
	    gradeWriter.writeGradebook(version);
	    gradeWriter.writeAssessment(assessmentsArrayList);
	    gradeWriter.writeStudent(students);
	    gradeWriter.writeGrade(grades);
	    gradeWriter.close();

	    // Indicate that the file is saved
	    fileIsSaved = true;

	    // Update the footer status to display the savingFile path
	    setFooter(savingFile);
	}

	
	/**
	 * Opens a file dialog for the user to specify the file location for saving the data.
	 * The chosen file location will be used for saving the data, and the data will be written to that file.
	 */
	@Override
	void saveAs() {
	    // Create a FileChooser dialog for selecting the file location
	    FileChooser fileChooser = new FileChooser();
	    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Gradebook File", "*.grade"));

	    // Show the save dialog and get the user-selected file
	    File file = fileChooser.showSaveDialog(null);

	    // If the user cancels the dialog or doesn't select a file, return without saving
	    if (file == null) {
	        return;
	    }

	    // Set the savingFile to the user-selected file
	    savingFile = file;

	    // Create lists to store student and grade data
	    ArrayList<Student> students = new ArrayList<>();
	    ArrayList<Grade> grades = new ArrayList();

	    // Retrieve data from the TableView and grading table
	    ObservableList<Student> studentList = tableView.getItems();
	    ObservableList<Grade> gradeList = gradingTable.getItems();

	    // Populate the student and grade lists
	    for (Student student : studentList) {
	        students.add(student);
	    }
	    for (Grade grade : gradeList) {
	        grades.add(grade);
	    }

	    // Create a GradeWriter instance to write data to the selected file
	    GradeWriter gradeWriter = new GradeWriter(file);

	    // Write the gradebook version, assessment data, student data, and grade data to the file
	    gradeWriter.writeGradebook("alpha");
	    gradeWriter.writeAssessment(assessmentsArrayList);
	    gradeWriter.writeStudent(students);
	    gradeWriter.writeGrade(grades);
	    gradeWriter.close();

	    // Indicate that the file is saved
	    fileIsSaved = true;

	    // Update the footer status to display the selected file path
	    setFooter(file);
	}

	
	/**
	 * Opens a file dialog for the user to specify the file location for saving data in CSV format.
	 * The chosen file location will be used for saving the data in CSV format.
	 */
	@Override
	void saveCSV() {
	    // Create a FileChooser dialog for selecting the file location
	    FileChooser fileChooser = new FileChooser();
	    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV File", "*.csv"));

	    // Show the save dialog and get the user-selected file
	    File file = fileChooser.showSaveDialog(null);

	    // If the user cancels the dialog or doesn't select a file, return without saving
	    if (file == null) {
	        return;
	    }

	    // Create a list to store student data
	    ArrayList<Student> students = new ArrayList<>();

	    // Retrieve data from the TableView
	    ObservableList<Student> studentList = tableView.getItems();

	    // Populate the student list
	    for (Student student : studentList) {
	        students.add(student);
	    }

	    // Create a GradeWriter instance to write data to the selected file in CSV format
	    GradeWriter gradeWriter = new GradeWriter(file);

	    // Export the student data to the selected file in CSV format
	    gradeWriter.ExportCSV(students);
	    gradeWriter.close();
	}

	
	/**
	 * Opens a file dialog for the user to select and open a Gradebook file. If there are unsaved changes in the
	 * current data, the method prompts the user to save those changes. After selecting a Gradebook file, the method
	 * reads the data from the file and populates the application with the saved data, including students, assessments,
	 * and grades. It also updates the UI to reflect the loaded data.
	 */
	@Override
	void openFile() {
	    // Check if there are unsaved changes in the current data
	    if (!fileIsSaved) {
	        Alert alert = new Alert(AlertType.CONFIRMATION);
	        alert.setTitle("Gradebook");
	        alert.setHeaderText("Do you want to save changes to the current file?");
	        
	        // Display a confirmation dialog to save changes
	        if (alert.showAndWait().get() == ButtonType.OK) {
	            saveFile();
	        }
	    }

	    // Create a FileChooser dialog for selecting a Gradebook file
	    FileChooser fileChooser = new FileChooser();
	    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Gradebook File", "*.grade"));

	    // Show the open dialog and get the user-selected file
	    File file = fileChooser.showOpenDialog(new Stage());

	    // If the user cancels the dialog or doesn't select a file, return without opening a file
	    if (file == null) {
	        return;
	    }

	    // Remove unwanted table columns
	    for (int i = 0; i < tableView.getColumns().size(); i++) {
	        if (tableView.getColumns().get(i).getText().equals("SN") ||
	            tableView.getColumns().get(i).getText().equals("ID") ||
	            tableView.getColumns().get(i).getText().equals("Name") ||
	            tableView.getColumns().get(i).getText().equals("Grade")) {
	            continue;
	        }
	        tableView.getColumns().remove(i);
	        i--;
	    }

	    // Create a GradeReader instance to read data from the selected file
	    GradeReader gradeReader = new GradeReader(file);

	    // Read student data from the file
	    ArrayList<Student> students = gradeReader.readStudent();
	    gradeReader.close();

	    // Read assessment data from the file
	    gradeReader = new GradeReader(file);
	    ArrayList<Assessment> assessments = gradeReader.readAssessment();
	    gradeReader.close();

	    // Read grade data from the file
	    gradeReader = new GradeReader(file);
	    ArrayList<Grade> grades = gradeReader.readGrade();
	    gradeReader.close();

	    // Clear existing data in the UI
	    ObservableList<Student> studentList = tableView.getItems();
	    studentList.clear();

	    ObservableList<Assessment> assessmentList = assessmentMarksTable.getItems();
	    assessmentList.clear();

	    ObservableList<Grade> gradeList = gradingTable.getItems();
	    gradeList.clear();

	    // Clear the list view for mark calculation
	    listViewForMarkCalculation.getItems().clear();

	    // Populate the UI with the data from the file
	    for (Student student : students) {
	        studentList.add(student);
	    }

	    for (Assessment assessment : assessments) {
	        assessmentList.add(assessment);
	    }

	    for (Grade grade : grades) {
	        gradeList.add(grade);
	    }

	    // Update class variables to reflect the loaded data
	    this.assessmentsArrayList = assessments;
	    this.assessmentNames.clear();

	    // Populate the assessment names
	    for (Assessment assessment : assessments) {
	        this.assessmentNames.add(assessment.getAssessmentName());
	    }
	    
	    // Set the assessment names in the Student class
	    Student.setAssessmentNames(assessmentNames);

	    // Update the UI components related to assessments
	    assessmentChoiceBox.getItems().clear();
	    assessmentChoiceBox.getItems().addAll(assessmentNames);

	    choiceBoxForMarkCalculation.getItems().clear();
	    choiceBoxForMarkCalculation.getItems().addAll(assessmentNames);

	    // Add choice boxes for mark calculation
	    for (Assessment assessment : assessments) {
	        CheckBox assessmentCheckBox = new CheckBox();
	        assessmentCheckBox.setText(assessment.getAssessmentName());
	        listViewForMarkCalculation.getItems().add(assessmentCheckBox);

	        // Add columns to the table view for assessments
	        TableColumn<Student, String> assessmentCol = new TableColumn<>(assessment.getAssessmentName());
	        initializeColumn(assessmentCol, assessment.getAssessmentName());
	        tableView.getColumns().add(assessmentCol);
	    }

	    // Update the UI with the loaded data
	    tableView.setItems(studentList);
	    assessmentMarksTable.setItems(assessmentList);
	    gradingTable.setItems(gradeList);

	    // Set the current saving file to the loaded file
	    savingFile = file;
	    saveFileExists = true;
	    fileIsSaved = true;

	    // Update the footer to show the loaded file path
	    setFooter(file);
	}

	
	/**
	 * Initializes a TableColumn for a specific assessment in the TableView.
	 *
	 * @param tableColumn The TableColumn to be initialized.
	 * @param colName The name of the assessment corresponding to the column.
	 */
	private void initializeColumn(TableColumn<Student, String> tableColumn, String colName) {
	    // Set the cell value factory for the TableColumn to display student marks for the given assessment
	    tableColumn.setCellValueFactory(c -> {
	        Student student = c.getValue();
	        String mark = student.getMark(colName);
	        return new ReadOnlyObjectWrapper<>(mark);
	    });

	    // Set a cell factory to allow editing marks using a TextField
	    tableColumn.setCellFactory(TextFieldTableCell.forTableColumn());

	    // Handle changes made to the marks in the TableColumn
	    tableColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Student, String>>() {
	        @Override
	        public void handle(CellEditEvent<Student, String> arg0) {
	            // Get the edited student and the new mark value
	            Student student = arg0.getRowValue();
	            String newMark = arg0.getNewValue();

	            // Update the student's mark for the assessment
	            student.setMark(colName, newMark);

	            // Mark the file as unsaved
	            fileIsSaved = false;

	            // Update the footer to reflect unsaved changes
	            setFooter(savingFile);

	            // Refresh the TableView to reflect the changes
	            tableView.refresh();
	        }
	    });
	}


	/**
	 * Creates a new gradebook file, prompting the user to save changes to the current file if it's unsaved.
	 * Clears the current data including students, assessments, and grades, and resets the UI.
	 */
	void newFile() {
	    // Prompt the user to save changes to the current file if it's unsaved
	    if (!fileIsSaved) {
	        Alert alert = new Alert(AlertType.CONFIRMATION);
	        alert.setTitle("Gradebook");
	        alert.setHeaderText("Do you want to save changes to the current file?");
	        if (alert.showAndWait().get() == ButtonType.OK) {
	            saveFile();
	        }
	    }

	    // Remove all columns from the TableView except for "SN", "ID", "Name", and "Grade"
	    for (int i = 0; i < tableView.getColumns().size(); i++) {
	        if (tableView.getColumns().get(i).getText().equals("SN") ||
	            tableView.getColumns().get(i).getText().equals("ID") ||
	            tableView.getColumns().get(i).getText().equals("Name") ||
	            tableView.getColumns().get(i).getText().equals("Grade")) {
	            continue;
	        }
	        tableView.getColumns().remove(i);
	        i--;
	    }

	    // Clear the lists of assessment names and assessment data
	    this.assessmentNames.clear();
	    this.assessmentsArrayList.clear();

	    // Clear the assessment choice box and the choice box for mark calculation
	    assessmentChoiceBox.getItems().clear();
	    choiceBoxForMarkCalculation.getItems().clear();

	    // Clear the current data in the UI
	    ObservableList<Student> studentoList = tableView.getItems();
	    studentoList.clear();
	    ObservableList<Assessment> assessmetoList = assessmentMarksTable.getItems();
	    assessmetoList.clear();
	    ObservableList<Grade> gradeoList = gradingTable.getItems();
	    gradeoList.clear();
	    listViewForMarkCalculation.getItems().clear();

	    // Reset file-related variables
	    savingFile = null;
	    saveFileExists = false;
	    fileIsSaved = false;

	    // Clear the footer
	    File file = null;
	    setFooter(file);
	}

	
	/**
	 * Update the status bar in the UI with the current file path and save status.
	 *
	 * @param file The current file being edited, or null if there is no file.
	 */
	private void setFooter(File file) {
	    if (file != null && fileIsSaved) {
	        // The file is not null, and it has been saved
	        leftStatus.setText(file.getAbsolutePath());
	        rightStatus.setText("Saved");
	    } else if (file != null && !fileIsSaved) {
	        // The file is not null, but it has unsaved changes
	        leftStatus.setText("*" + file.getAbsolutePath());
	        rightStatus.setText("Unsaved");
	    } else if (file == null) {
	        // No file is open or saved
	        leftStatus.setText("*Untitled.grade");
	        rightStatus.setText("Unsaved");
	    }
	}

	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		snCol.setCellValueFactory(new PropertyValueFactory<Student, Integer>("sn"));
		snCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		snCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Student,Integer>>() {
			
			@Override
			public void handle(CellEditEvent<Student, Integer> arg0) {
				if(!tableIsLocked) {
					Student student = arg0.getRowValue();
					student.setSn(arg0.getNewValue());
				}
				tableView.refresh();
			}
		});;
		
		idCol.setCellValueFactory(new PropertyValueFactory<Student, String>("id"));
		idCol.setCellFactory(TextFieldTableCell.forTableColumn());
		idCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Student,String>>() {
			@Override
			public void handle(CellEditEvent<Student, String> arg0) {
				if(!tableIsLocked) {
					Student student = arg0.getRowValue();
					student.setId(arg0.getNewValue());
					fileIsSaved = false;
					setFooter(savingFile);
				}
				tableView.refresh();
			}
		});;
		
		nameCol.setCellValueFactory(new PropertyValueFactory<Student, String>("name"));
		nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
		nameCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Student,String>>() {
			
			@Override
			public void handle(CellEditEvent<Student, String> arg0) {
				if(!tableIsLocked) {
					Student student = arg0.getRowValue();
					student.setName(arg0.getNewValue());
					fileIsSaved = false;
					setFooter(savingFile);
				}
				tableView.refresh();
			}
		});;
		
		gradeCol.setCellValueFactory(new PropertyValueFactory<Student, String>("grade"));
		gradeCol.setCellFactory(TextFieldTableCell.forTableColumn());
		gradeCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Student,String>>() {
			
			@Override
			public void handle(CellEditEvent<Student, String> arg0) {
				if(!tableIsLocked) {
					Student student = arg0.getRowValue();
					student.setGrade(arg0.getNewValue());
					fileIsSaved = false;
					setFooter(savingFile);
				}
				tableView.refresh();
			}
		});;
		
		
		assessmentNameCol.setCellValueFactory(new PropertyValueFactory<Assessment, String>("assessmentName"));
		assessmentNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
		assessmentNameCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Assessment,String>>() {
			
			@Override
			public void handle(CellEditEvent<Assessment, String> arg0) {
				if(!tableIsLocked) {
					int i;
					Assessment a = arg0.getRowValue();
					String oldValueString = a.getAssessmentName();
					String newValueString = arg0.getNewValue();
					
					a.setAssessmentName(newValueString);
					
					//now editing other related stuff
					 
					for(i = 0; i < assessmentNames.size(); i++) {
						if(assessmentNames.get(i).equals(oldValueString)) {
							assessmentNames.set(i, newValueString);
							assessmentsArrayList.set(i, a);
							assessmentChoiceBox.getItems().set(i, newValueString);
							choiceBoxForMarkCalculation.getItems().set(i, newValueString);;
						    listViewForMarkCalculation.getItems().get(i).setText(newValueString);
							break;
						}
					}
					
//					changing name in main table
					for(i = 0; i < tableView.getColumns().size(); i++) {
						if(tableView.getColumns().get(i).getText().equals(oldValueString)) {
							tableView.getColumns().get(i).setText(newValueString);
							initializeColumn((TableColumn<Student, String>) tableView.getColumns().get(i), newValueString);
							tableView.refresh();
							break;
						}
					}
					fileIsSaved = false;
					setFooter(savingFile);
				}
				
				assessmentMarksTable.refresh();
			}
		});;
		
		
		assessmentFullMarkCol.setCellValueFactory(new PropertyValueFactory<Assessment, Double>("assessmentFullMark"));
		assessmentFullMarkCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
		assessmentFullMarkCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Assessment, Double>>() {
			
			@Override
			public void handle(CellEditEvent<Assessment, Double> arg0) {
				if(!tableIsLocked) {
					Assessment a = arg0.getRowValue();
					a.setAssessmentFullMark(arg0.getNewValue());
					fileIsSaved = false;
					setFooter(savingFile);
				}
				assessmentMarksTable.refresh();
			}
		});;
		
		assessmentWeightCol.setCellValueFactory(new PropertyValueFactory<Assessment, Double>("assessmentWeight"));
		assessmentWeightCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
		assessmentWeightCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Assessment, Double>>() {
			
			@Override
			public void handle(CellEditEvent<Assessment, Double> arg0) {
				if(!tableIsLocked) {
					Assessment a = arg0.getRowValue();
					a.setAssessmentWeight(arg0.getNewValue());
					fileIsSaved = false;
					setFooter(savingFile);
				}
				assessmentMarksTable.refresh();
			}
		});;
		
		assessmentCountColumn.setCellValueFactory(new PropertyValueFactory<Assessment, CheckBox>("countableForGradeCheckBox"));
		
		
		gradeFromCol.setCellValueFactory(new PropertyValueFactory<Grade, String>("minNumber"));
		gradeFromCol.setCellFactory(TextFieldTableCell.forTableColumn());
		gradeFromCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Grade, String>>() {
			
			@Override
			public void handle(CellEditEvent<Grade, String> arg0) {
				if(!tableIsLocked) {
					Grade grade = arg0.getRowValue();
					grade.setMinNumber(arg0.getNewValue());
					fileIsSaved = false;
					setFooter(savingFile);
				}
				tableView.refresh();
			}
		});;
		
		gradeToCol.setCellValueFactory(new PropertyValueFactory<Grade, String>("maxNumber"));
		gradeToCol.setCellFactory(TextFieldTableCell.forTableColumn());
		gradeToCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Grade, String>>() {
			
			@Override
			public void handle(CellEditEvent<Grade, String> arg0) {
				if(!tableIsLocked) {
					Grade grade = arg0.getRowValue();
					grade.setMaxNumber(arg0.getNewValue());
					fileIsSaved = false;
					setFooter(savingFile);
				}
				tableView.refresh();
			}
		});;
		
		gradeNameCol.setCellValueFactory(new PropertyValueFactory<Grade, String>("gradeName"));
		gradeNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
	    gradeNameCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Grade, String>>() {
			
			@Override
			public void handle(CellEditEvent<Grade, String> arg0) {
				if(!tableIsLocked) {
					Grade grade = arg0.getRowValue();
					grade.setGradeName(arg0.getNewValue());
					fileIsSaved = false;
					setFooter(savingFile);
				}
				tableView.refresh();
			}
		});;
	}
}
