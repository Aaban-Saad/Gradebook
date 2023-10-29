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
	//These 2 has to be initialized from file first;
	private ArrayList<String> assessmentNames = new ArrayList<>(); //For student class
	private ArrayList<Assessment> assessmentsArrayList = new ArrayList<>(); //this is for the mini right table (Assessment class)
	
	final String version = "1.0";
	File savingFile;
	
	private boolean tableIsLocked = false;
	
	private boolean saveFileExists = false;
	private boolean fileIsSaved = false;
  
	@Override
	void addIdName(ActionEvent event) {
		if(tableIsLocked) return;
		
		int i, j;
		String[] idStrings = enterId.getText().split("\n");
		String[] nameStrings = enterName.getText().split("\n");
		int length = idStrings.length <= nameStrings.length ? idStrings.length : nameStrings.length;

		Student[] student = new Student[length];
		
		for(i = 0; i < length; i++) {
			student[i] = new Student();
			student[i].setSn(tableView.getItems().size() + 1);
			try {					
				student[i].setId(idStrings[i].trim());
			} catch (Exception e) {
				break;
			}
			student[i].setName(nameStrings[i].trim());
			
			if(student[i].getName().equals("")) break;
			
			Student.setAssessmentNames(assessmentNames);
			
			//setting default marks("") for each assessment in student class
			ArrayList<String> assessmentMarks = student[i].getAssessmentMarks();
			for(j = 0; j < this.assessmentNames.size(); j++) {
				assessmentMarks.add("");
				student[i].setAssessmentMarks(assessmentMarks);
			}
			
			ObservableList<Student> students = tableView.getItems();
			students.add(student[i]);
			tableView.setItems(students);
		}
		
		enterId.clear();
		enterName.clear();
		fileIsSaved = false;
		
		setFooter(savingFile);
	}
	
	@Override
	void addAssessment(ActionEvent event) {
		if(tableIsLocked) return;
		
		String assessmentName = enterAssessment.getText().trim();
		
		if(assessmentName.equals("")) return;
		
		
		TableColumn<Student, String> assessmentCol = new TableColumn<>();
		assessmentNames.add(assessmentName);
		
		Assessment assessment = new Assessment();
		assessment.setAssessmentName(assessmentName);
		assessment.setAssessmentFullMark(0.0);
		assessment.setAssessmentWeight(0.0);
		assessmentsArrayList.add(assessment);
		
		ObservableList<Student> students = tableView.getItems();
		
		int i;
		for(i = 0; i < students.size(); i++) {
			Student student = students.get(i);
			ArrayList<String> assessmentMarks = student.getAssessmentMarks();
			assessmentMarks.add("");
			student.setAssessmentMarks(assessmentMarks);
			
		}
		
		assessmentCol.setCellValueFactory(c -> {
		    Student student = c.getValue();
		    String mark = student.getMark(assessmentName);

		    return new ReadOnlyObjectWrapper<>(mark);
		});
		
		assessmentCol.setCellFactory(TextFieldTableCell.forTableColumn());
		assessmentCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Student,String>>() {
			@Override
			public void handle(CellEditEvent<Student, String> arg0) {
				if(!tableIsLocked) {
					Student student = arg0.getRowValue();
					String newMark = arg0.getNewValue();
					student.setMark(assessmentName, newMark);
					fileIsSaved = false;
					setFooter(savingFile);
				}

				tableView.refresh();
			}
		});;
		
		tableView.getColumns().add(assessmentCol);
		tableView.setItems(students);
		
//			This is for the right most mini assessment table.
		ObservableList<Assessment> assessmentsObservableList = assessmentMarksTable.getItems();
		for(i = 0; i < assessmentsArrayList.size(); i++) {
			try {
//					set to index i
				assessmentsObservableList.set(i, assessmentsArrayList.get(i));
			} catch (Exception e) {
//					if index i doesn't exist then add it
				assessmentsObservableList.add(assessmentsArrayList.get(i));
			}
			
		}
		assessmentMarksTable.setItems(assessmentsObservableList);

		
//		This is for removing assessments later
		assessmentChoiceBox.getItems().removeAll(assessmentNames);
		assessmentChoiceBox.getItems().addAll(assessmentNames); 
		Student.setAssessmentNames(assessmentNames);
		System.out.println(assessmentNames + "= " + Student.getAssessmentNames());
		
		choiceBoxForMarkCalculation.getItems().removeAll(assessmentNames);
		choiceBoxForMarkCalculation.getItems().addAll(assessmentNames); 

		//Now adding choice boxes on the left side
		CheckBox assessmentCheckBox = new CheckBox();
		assessmentCheckBox.setText(assessmentName);
		listViewForMarkCalculation.getItems().add(assessmentCheckBox);
		
		enterAssessment.clear();
		
		fileIsSaved = false;
		
		setFooter(savingFile);
	}
	
	@Override
	void recalculateSerialNumber(ActionEvent event) {
		if(!tableIsLocked) {
			ObservableList<Student> students = tableView.getItems();
			int i;
			for(i = 0; i < students.size(); i++) {
				students.get(i).setSn(i + 1);
				tableView.setItems(students);
				tableView.refresh();
			}
		}
		
		fileIsSaved = false;
		
		setFooter(savingFile);
	}
	
	@Override
	void lockTable(ActionEvent event) {
		tableIsLocked = lockTable.isSelected() ? true : false;
	}
	
	@Override
	void removeStudent(ActionEvent event) {
		if(tableIsLocked) return;
		
		ObservableList<Student> students = tableView.getItems();
		int i;
		for(i = 0; i < students.size(); i++) {
			if(students.get(i).getId().equals(idToRemove.getText())) {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Gradebook");
				alert.setHeaderText("You are about remove ID-" + idToRemove.getText() + " from the table.");
				alert.setContentText("This cannot be undone. Press cancel if you are not sure.");
				if(alert.showAndWait().get() == ButtonType.OK) {						
					students.remove(i);
				}
				break;
			}
		}
		idToRemove.clear();
		
		fileIsSaved = false;
		
		setFooter(savingFile);
	}
	
	@Override
	void removeAssessment(ActionEvent event) {
		if(tableIsLocked) return;
		
		ObservableList<TableColumn<Student, ?>> tableColumns = tableView.getColumns();
		ObservableList<Assessment> assessmentsToRemove = assessmentMarksTable.getItems(); //some assessments will be removed from this list
		String columnToRemove = assessmentChoiceBox.getValue();
		int i;
		for(i = 0; i < tableColumns.size(); i++) {
			if(columnToRemove != null && columnToRemove.equals(tableColumns.get(i).getText())) {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Gradebook");
				alert.setHeaderText("You are about remove " + tableColumns.get(i).getText() + " from the table.");
				alert.setContentText("This cannot be undone. Press cancel if you are not sure.");
				if(alert.showAndWait().get() == ButtonType.OK) {						
					tableView.getColumns().remove(i);
					
//						removing from the mini right table
					for(i = 0; i < assessmentsToRemove.size(); i++) {
						if(assessmentsToRemove.get(i).getAssessmentName().equals(columnToRemove)) {
							assessmentsToRemove.remove(i);
							assessmentMarksTable.refresh();
							break;
						}
					}
					
					//removing from  student class
					ObservableList<Student> students = tableView.getItems();
					for (Student student : students) {
						ArrayList<String> markStrings = student.getAssessmentMarks();
						for (i = 0; i < markStrings.size(); i++) {
							System.out.println(Student.getAssessmentNames());
							System.out.println("rem = " +student.getMark(columnToRemove));
							if(markStrings.get(i).equals(student.getMark(columnToRemove))) {
								markStrings.remove(i);
								break;
							}
						}
						System.out.println(markStrings);
						student.setAssessmentMarks(markStrings);
					}
					
					
					for(i = 0; i < this.assessmentNames.size(); i++) {
						if(assessmentNames.get(i).equals(columnToRemove)) {
							assessmentNames.remove(i);
							assessmentsArrayList.remove(i);
							assessmentChoiceBox.getItems().remove(i);
							choiceBoxForMarkCalculation.getItems().remove(i);
							listViewForMarkCalculation.getItems().remove(i);
							break;
						}
					}
					
					Student.setAssessmentNames(assessmentNames);
					
				}
				break;
			}
		}
		
		fileIsSaved = false;
		
		setFooter(savingFile);
	}
	
	@Override
	void markCalculationProcess(ActionEvent event) {
		if(averageRadioButton.isSelected()) {
			best_n_textField.setDisable(true);
			bonusTextField.setDisable(true);
			
		} else if(bestRadioButton.isSelected()) {
			best_n_textField.setDisable(true);
			bonusTextField.setDisable(true);
			
		} else if(best_n_RadioButton.isSelected()) {
			best_n_textField.setDisable(false);
			bonusTextField.setDisable(true);
			
		} else if(bonusRadioButton.isSelected()) {
			best_n_textField.setDisable(true);
			bonusTextField.setDisable(false);
		} 
		fileIsSaved = false;
		
		setFooter(savingFile);
	}
	
	@Override
	void calculateMark(ActionEvent event) {
		if(tableIsLocked) return;
		
		if(averageRadioButton.isSelected()) {
			int i, j;
			ObservableList<CheckBox> checkBoxes = listViewForMarkCalculation.getItems();
			ArrayList<String> selectedAssessments = new ArrayList<>();
			ObservableList<Student> students = tableView.getItems();
			String colName = choiceBoxForMarkCalculation.getValue();
			
			//initializing selectedAssessments
			for(i = 0; i < checkBoxes.size(); i++) {
				if(checkBoxes.get(i).isSelected()) {
					selectedAssessments.add(checkBoxes.get(i).getText());
				}
			}
			
			//calculating average
			for(i = 0; i < students.size(); i++) {
				Float average = 0.0f;
				for(j = 0; j < selectedAssessments.size(); j++) {
					try {						
						average += Float.parseFloat(students.get(i).getMark(selectedAssessments.get(j)));
					} catch (Exception e) {
						average += 0.0f;
					}
				}
				average /= selectedAssessments.size();

				//adding to previous mark in column
				float previousMark;
				try {
					previousMark = Float.parseFloat(students.get(i).getMark(colName));
				} catch (Exception e) {
					previousMark = 0.0f;
				}
				
				if(addRadioButton.isSelected()) {
					students.get(i).setMark(colName, (previousMark + average)+"");					
				} else if(replaceRadioButton.isSelected()) {
					students.get(i).setMark(colName, average+"");
				}
			}
			
			tableView.setItems(students);
			tableView.refresh();
			averageRadioButton.setSelected(false);
			
			fileIsSaved = false;
			
			setFooter(savingFile);
			
		} 
		else if(bestRadioButton.isSelected()) {
			int i, j;
			ObservableList<CheckBox> checkBoxes = listViewForMarkCalculation.getItems();
			ArrayList<String> selectedAssessments = new ArrayList<>();
			float[] marksOfSelectedAssessments = new float[checkBoxes.size()]; //of 1 student
			ObservableList<Student> students = tableView.getItems();
			String colName = choiceBoxForMarkCalculation.getValue();
			
			//initializing selectedAssessments
			for(i = 0; i < checkBoxes.size(); i++) {
				if(checkBoxes.get(i).isSelected()) {
					selectedAssessments.add(checkBoxes.get(i).getText());
				}
			}
			
			//calculating best
			for(i = 0; i < students.size(); i++) {
				marksOfSelectedAssessments = new float[checkBoxes.size()]; //clearing array
				for(j = 0; j < selectedAssessments.size(); j++) {
					try {
						marksOfSelectedAssessments[j] = Float.parseFloat(students.get(i).getMark(selectedAssessments.get(j)));
					} catch (Exception e) {
						marksOfSelectedAssessments[j] = 0.0f;
					}
				}
				Arrays.sort(marksOfSelectedAssessments);
				float best = marksOfSelectedAssessments[marksOfSelectedAssessments.length - 1];
				
				//adding to previous mark in column
				float previousMark;
				try {
					previousMark = Float.parseFloat(students.get(i).getMark(colName));
				} catch (Exception e) {
					previousMark = 0.0f;
				}
				
				if(addRadioButton.isSelected()) {
					students.get(i).setMark(colName, (previousMark + best)+"");					
				} else if(replaceRadioButton.isSelected()) {
					students.get(i).setMark(colName, best+"");
				}
			}
			
			tableView.setItems(students);
			tableView.refresh();
			bestRadioButton.setSelected(false);
		}
		else if (best_n_RadioButton.isSelected()) {
			int i, j;
			ObservableList<CheckBox> checkBoxes = listViewForMarkCalculation.getItems();
			ArrayList<String> selectedAssessments = new ArrayList<>();
			float[] marksOfSelectedAssessments; //of 1 student
//			double[] calculatedBestMarks = new double[tableView.getItems().size()];//of all students
			ObservableList<Student> students = tableView.getItems();
			String colName = choiceBoxForMarkCalculation.getValue();
			
			//initializing selectedAssessments
			for(i = 0; i < checkBoxes.size(); i++) {
				if(checkBoxes.get(i).isSelected()) {
					selectedAssessments.add(checkBoxes.get(i).getText());
				}
			}
			
			//calculating best n average
			for(i = 0; i < students.size(); i++) {
				marksOfSelectedAssessments = new float[checkBoxes.size()];
				for(j = 0; j < selectedAssessments.size(); j++) {
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
				for(j = marksOfSelectedAssessments.length - 1; j > marksOfSelectedAssessments.length - bestN - 1; j--) {
					best_n_Average += marksOfSelectedAssessments[j];
				}
				best_n_Average /= bestN;
				
//				//adding or replacing to previous mark in column
				float previousMark = 0.0f;
				try {				
					previousMark = Float.parseFloat(students.get(i).getMark(colName));
				} catch (Exception e) {
					previousMark = 0.0f;
				} 
				
				if(addRadioButton.isSelected()) {
					students.get(i).setMark(colName, (previousMark + best_n_Average)+"");					
				} else if(replaceRadioButton.isSelected()) {
					students.get(i).setMark(colName, best_n_Average+"");
				}
			}
			
			tableView.setItems(students);
			tableView.refresh();
			best_n_RadioButton.setSelected(false);
		}
		else if(bonusRadioButton.isSelected() && !bonusTextField.getText().equals("")) {
			String colName = choiceBoxForMarkCalculation.getValue();
			float markToAdd = Float.parseFloat(bonusTextField.getText());
			ObservableList<Student> students = tableView.getItems();
			int i;
			for(i = 0; i < students.size(); i++) {
				float previousMark = 0.0f;
				try {				
					previousMark = Float.parseFloat(students.get(i).getMark(colName));
				} catch (Exception e) {
					previousMark = 0.0f;
				} finally {
					if(addRadioButton.isSelected()) {
						students.get(i).setMark(colName, (previousMark + markToAdd)+"");					
					} else if(replaceRadioButton.isSelected()) {
						students.get(i).setMark(colName, markToAdd+"");
					}
				}
			}
			tableView.setItems(students);
			tableView.refresh();
			bonusTextField.clear();
			bonusRadioButton.setSelected(false);
		}
		
		fileIsSaved = false;
		
		setFooter(savingFile);
	}
	
	@Override
	void calculateGrade(ActionEvent e) {
		if(tableIsLocked) return;
		
		gradeCol.setVisible(true);
		
		ObservableList<Assessment> assessmentsForGrade = assessmentMarksTable.getItems();
		int i, j;
		
		ObservableList<Student> students = tableView.getItems();

		double finalScore;
		for(i = 0; i < students.size(); i++) {
			finalScore = 0.0;
			for(j = 0; j < assessmentsForGrade.size(); j++) {
				if(!assessmentsForGrade.get(j).isCountableForGrade()) continue;
				String assessmentName = assessmentsForGrade.get(j).getAssessmentName();
				double mark;
				try {
					mark = Double.parseDouble(students.get(i).getMark(assessmentName));
				} catch (Exception ex) {
					mark = 0.0;
				}
				finalScore += mark * assessmentsForGrade.get(j).getAssessmentWeight() / assessmentsForGrade.get(j).getAssessmentFullMark();
			}
			
			//taking the grade ranges from grading table
			ObservableList<Grade> grades = gradingTable.getItems();
			for (Grade grade : grades) {
				double min, max;
				if(!grade.getMinNumber().trim().equals("*")) {					
					min = Double.parseDouble(grade.getMinNumber());
				} else {
					min = Double.MIN_VALUE;
				}
				
				if(!grade.getMaxNumber().trim().equals("*")) {					
					max = Double.parseDouble(grade.getMaxNumber());
				} else {
					max = Double.MAX_VALUE;
				}
				
				if(Math.ceil(finalScore) >= min && finalScore <= max) {
					students.get(i).setGrade(grade.getGradeName() + "  (" +finalScore + "%)");
					break;
				}
			}
		}

		tableView.setItems(students);
		tableView.refresh();
		
		fileIsSaved = false;
		
		setFooter(savingFile);
		
	}
	
	@Override
	void addGrade(ActionEvent e) {
		Grade grade = new Grade();
		ObservableList<Grade> grades= gradingTable.getItems();
		grades.add(grade);
		gradingTable.setItems(grades);
		gradingTable.refresh();
		
		fileIsSaved = false;
		
		setFooter(savingFile);
	}
	
	@Override
	void removeGrade(ActionEvent e) {
		int row = gradingTable.getSelectionModel().getSelectedIndex();
		try {
			gradingTable.getItems().remove(row);
		} catch (Exception ex) {
			// do nothing
		}
		
		fileIsSaved = false;
		
		setFooter(savingFile);
	}
	
	@Override
	void getDefaultGradingTable(ActionEvent e) {
		ObservableList<Grade> grades = gradingTable.getItems();
		grades.clear();
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
		gradingTable.setItems(grades);
		gradingTable.refresh();
		
		fileIsSaved = false;
		
		setFooter(savingFile);
	}
	
	@Override
	void saveFile() {
		if(!saveFileExists) {
			saveFileExists = true;
			saveAs();
			return;
		}
		
		ArrayList<Student> students = new ArrayList<>();
		ObservableList<Student> studentsoList = tableView.getItems();
		for (Student student : studentsoList) {	
			students.add(student);
		}
		
		ArrayList<Grade> grades = new ArrayList<>();
		ObservableList<Grade> gradesoList = gradingTable.getItems();
		for (Grade grade : gradesoList) {
			grades.add(grade);
		}
		
		GradeWriter gradeWriter = new GradeWriter(savingFile);
		gradeWriter.writeGradebook(version);
		gradeWriter.writeAssessment(assessmentsArrayList);
		gradeWriter.writeStudent(students);
		gradeWriter.writeGrade(grades);
		gradeWriter.close();
		
		fileIsSaved = true;
		
		setFooter(savingFile);
	}
	
	@Override
	void saveAs() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Gradebook File", "*.grade"));
		File file = fileChooser.showSaveDialog(null);
		if(file == null) return;
		savingFile = file;
		
		ArrayList<Student> students = new ArrayList<>();
		ObservableList<Student> studentsoList = tableView.getItems();
		for (Student student : studentsoList) {	
			students.add(student);
		}
		
		ArrayList<Grade> grades = new ArrayList<>();
		ObservableList<Grade> gradesoList = gradingTable.getItems();
		for (Grade grade : gradesoList) {
			grades.add(grade);
		}
		
		GradeWriter gradeWriter = new GradeWriter(file);
		gradeWriter.writeGradebook("alpha");
		gradeWriter.writeAssessment(assessmentsArrayList);
		gradeWriter.writeStudent(students);
		gradeWriter.writeGrade(grades);
		gradeWriter.close();
		
		fileIsSaved = true;
		
		setFooter(file);
	}
	
	@Override
	void saveCSV() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV File", "*.csv"));
		File file = fileChooser.showSaveDialog(null);
		if(file == null) return;
		
		ArrayList<Student> students = new ArrayList<>();
		ObservableList<Student> studentsoList = tableView.getItems();
		for (Student student : studentsoList) {	
			students.add(student);
		}
		
		GradeWriter gradeWriter = new GradeWriter(file);
		gradeWriter.ExportCSV(students);
		gradeWriter.close();
	}
	
	@Override
	void openFile() {
		if(!fileIsSaved) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Gradebook");
			alert.setHeaderText("Do you want to save changes to the current file?");
			if(alert.showAndWait().get() == ButtonType.OK) {
				saveFile();
			}			
		}
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Gradebook File", "*.grade"));
		File file = fileChooser.showOpenDialog(new Stage());
		
		if(file == null) return;
		
		//removing table columns
		for(int i = 0; i < tableView.getColumns().size(); i++) {

			if(tableView.getColumns().get(i).getText().equals("SN")  ||
			tableView.getColumns().get(i).getText().equals("ID") ||
			tableView.getColumns().get(i).getText().equals("Name") ||
			tableView.getColumns().get(i).getText().equals("Grade")) {
				
				continue;
			}
			tableView.getColumns().remove(i);
			i--;
			
		}
		
		GradeReader gradeReader = new GradeReader(file);
		ArrayList<Student> students = gradeReader.readStudent();
		gradeReader.close();
		
		gradeReader = new GradeReader(file);
		ArrayList<Assessment> assessments = gradeReader.readAssessment();
		gradeReader.close();
		
		gradeReader = new GradeReader(file);
		ArrayList<Grade> grades = gradeReader.readGrade();
		gradeReader.close();
		
		ObservableList<Student> studentoList = tableView.getItems();
		studentoList.clear();
		ObservableList<Assessment> assessmetoList = assessmentMarksTable.getItems();
		assessmetoList.clear();
		ObservableList<Grade> gradeoList = gradingTable.getItems();
		gradeoList.clear();
		listViewForMarkCalculation.getItems().clear();
		
		for (Student student : students) {
			studentoList.add(student);
		}
		
		for (Assessment assessment : assessments) {
			assessmetoList.add(assessment);
		}
		
		for (Grade grade : grades) {
			gradeoList.add(grade);
		}
		
		
		this.assessmentsArrayList = assessments;
		
		this.assessmentNames.clear();
		for (Assessment assessment : assessments) {
			this.assessmentNames.add(assessment.getAssessmentName());
		}
		Student.setAssessmentNames(assessmentNames); //this line is important.
		
		//for removing assessment later
		assessmentChoiceBox.getItems().clear();
		assessmentChoiceBox.getItems().addAll(assessmentNames);
		
		choiceBoxForMarkCalculation.getItems().clear();
		choiceBoxForMarkCalculation.getItems().addAll(assessmentNames);

		//Now adding choice boxes on the left side
		for (Assessment assessment : assessments) {			
			CheckBox assessmentCheckBox = new CheckBox();
			assessmentCheckBox.setText(assessment.getAssessmentName());
			listViewForMarkCalculation.getItems().add(assessmentCheckBox);
			
			TableColumn<Student, String> assessmentCol = new TableColumn<> (assessment.getAssessmentName());
			initializeColumn(assessmentCol, assessment.getAssessmentName());
			tableView.getColumns().add(assessmentCol);
		}
		
		tableView.setItems(studentoList);
		assessmentMarksTable.setItems(assessmetoList);
		gradingTable.setItems(gradeoList);
		
		savingFile = file;
		saveFileExists = true;
		fileIsSaved = true;
		
		setFooter(file);
	}
	
	private void initializeColumn(TableColumn<Student, String> tableColumn, String colName) {
		tableColumn.setCellValueFactory(c -> {
		    Student student = c.getValue();
		    String mark = student.getMark(colName);
		    return new ReadOnlyObjectWrapper<>(mark);
		});
		
		tableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		tableColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Student,String>>() {
			@Override
			public void handle(CellEditEvent<Student, String> arg0) {
				
				Student student = arg0.getRowValue();
				String newMark = arg0.getNewValue();
				student.setMark(colName, newMark);
				fileIsSaved = false;
				setFooter(savingFile);
				tableView.refresh();
			}
		});;
		
	}

	public void newFile() {
		if(!fileIsSaved) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Gradebook");
			alert.setHeaderText("Do you want to save changes to the current file?");
			if(alert.showAndWait().get() == ButtonType.OK) {
				saveFile();
			}			
		}
		

		for(int i = 0; i < tableView.getColumns().size(); i++) {
			if(tableView.getColumns().get(i).getText().equals("SN")  ||
			tableView.getColumns().get(i).getText().equals("ID") ||
			tableView.getColumns().get(i).getText().equals("Name") ||
			tableView.getColumns().get(i).getText().equals("Grade")) {
				
				continue;
			}
			tableView.getColumns().remove(i);
			i--;
			
		}
		
		this.assessmentNames.clear();
		this.assessmentsArrayList.clear();
		//for removing assessment later
		assessmentChoiceBox.getItems().clear();
		
		choiceBoxForMarkCalculation.getItems().clear();
		
		ObservableList<Student> studentoList = tableView.getItems();
		studentoList.clear();
		ObservableList<Assessment> assessmetoList = assessmentMarksTable.getItems();
		assessmetoList.clear();
		ObservableList<Grade> gradeoList = gradingTable.getItems();
		gradeoList.clear();
		listViewForMarkCalculation.getItems().clear();
		
		savingFile = null;
		saveFileExists = false;
		fileIsSaved = false;
		
		File file = null;
		setFooter(file);
	}
	
	private void setFooter(File file) {
		if(file != null && fileIsSaved) {			
			leftStatus.setText(file.getAbsolutePath());
			rightStatus.setText("Saved");
		} else if(file != null && !fileIsSaved){
			leftStatus.setText("*" + file.getAbsolutePath());
			rightStatus.setText("Unsaved");
		} else if(file == null) {
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
							choiceBoxForMarkCalculation.getItems().set(i, newValueString);
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
				System.out.println(Student.getAssessmentNames());
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
