package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
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
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.shape.TriangleMesh;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.FloatStringConverter;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.LongStringConverter;

public class Controller implements Initializable{
	//These 2 has to be initialized from file first;
	private ArrayList<String> assessmentNames = new ArrayList<>(); //For student class
	private ArrayList<Assessment> assessmentsArrayList = new ArrayList<>(); //this is for the mini right table (Assessment class)
	
	//private ArrayList<CheckBox> checkBoxes = new ArrayList<>(); //this is for mark calculation on the left side
	
	private boolean tableIsLocked = false;
	
	
	@FXML
    private CheckBox lockTable;
	
	@FXML
	private TableView<Student> tableView;
	
    @FXML
    private TableView<Assessment> assessmentMarksTable;
    
    @FXML
    private TableColumn<Assessment, Double> assessmentFullMarkCol;

    @FXML
    private TableColumn<Assessment, String> assessmentNameCol;

    @FXML
    private TableColumn<Assessment, Double> assessmentWeightCol;
    
    @FXML
    private TableColumn<Assessment, CheckBox> assessmentCountColumn;
	
	@FXML
    private TableColumn<Student, Integer> snCol;
	
	@FXML
    private TableColumn<Student, String> idCol;
	
	@FXML
    private TableColumn<Student, String> nameCol;
	
	@FXML
    private TableColumn<Student, String> gradeCol;

	@FXML
    private TextArea enterId;

    @FXML
    private TextArea enterName;
    
    @FXML
    private TextField enterAssessment;

    @FXML
    private TextField idToRemove;
    
    @FXML
    private ChoiceBox<String> assessmentChoiceBox;
    
    @FXML
    private RadioButton averageRadioButton;

    @FXML
    private RadioButton bestRadioButton;

    @FXML
    private RadioButton best_n_RadioButton;

    @FXML
    private RadioButton bonusRadioButton;
    
    @FXML
    private TextField bonusTextField;

    @FXML
    private TextField best_n_textField;
    
    @FXML
    private ChoiceBox<String> choiceBoxForMarkCalculation;
    
    @FXML
    private ListView<CheckBox> listViewForMarkCalculation;
    
    @FXML
    private RadioButton replaceRadioButton;
    
    @FXML
    private RadioButton addRadioButton;
    
    @FXML
    private TableView<Grade> gradingTable;
    
    @FXML
    private TableColumn<Grade, String> gradeFromCol;
    
    @FXML
    private TableColumn<Grade, String> gradeToCol;

    @FXML
    private TableColumn<Grade, String> gradeNameCol;


    
	public void addIdName(ActionEvent event) {
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
			
			student[i].setAssessmentNames(assessmentNames);
			
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
	}
	
	
	public void addAssessment(ActionEvent event) {
		if(tableIsLocked) return;
		
		String assessmentName = enterAssessment.getText().trim();
		
		if(assessmentName.equals("")) return;
		
		
		TableColumn<Student, String> assessmentCol = new TableColumn<> (assessmentName);
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
			student.setAssessmentNames(assessmentNames);
			
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
		
		choiceBoxForMarkCalculation.getItems().removeAll(assessmentNames);
		choiceBoxForMarkCalculation.getItems().addAll(assessmentNames); 

		//Now adding choice boxes on the left side
		CheckBox assessmentCheckBox = new CheckBox();
		assessmentCheckBox.setText(assessmentName);
		listViewForMarkCalculation.getItems().add(assessmentCheckBox);
		
		enterAssessment.clear();
		
		
		
	}
	
	
	public void recalculateSerialNumber(ActionEvent event) {
		if(!tableIsLocked) {
			ObservableList<Student> students = tableView.getItems();
			int i;
			for(i = 0; i < students.size(); i++) {
				students.get(i).setSn(i + 1);
				tableView.setItems(students);
				tableView.refresh();
			}
		}
		
	}
	
	public void lockTable(ActionEvent event) {
		tableIsLocked = lockTable.isSelected() ? true : false;
	}
	
	public void removeStudent(ActionEvent event) {
		if(tableIsLocked) return;
		
		ObservableList<Student> students = tableView.getItems();
		int i;
		for(i = 0; i < students.size(); i++) {
			if(students.get(i).getId().equals(idToRemove.getText())) {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Remove Student");
				alert.setHeaderText("You are about remove ID-" + idToRemove.getText() + " from the table.");
				alert.setContentText("This cannot be undone. Press cancel if you are not sure.");
				if(alert.showAndWait().get() == ButtonType.OK) {						
					students.remove(i);
				}
				break;
			}
		}
		idToRemove.clear();
		
	}
	
	public void removeAssessment(ActionEvent event) {
		if(tableIsLocked) return;
		
		ObservableList<TableColumn<Student, ?>> tableColumns = tableView.getColumns();
		ObservableList<Assessment> assessmentsToRemove = assessmentMarksTable.getItems(); //some assessments will be removed from this list
		String columnToRemove = assessmentChoiceBox.getValue();
		int i;
		for(i = 0; i < tableColumns.size(); i++) {
			if(columnToRemove != null && columnToRemove.equals(tableColumns.get(i).getText())) {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Remove Assessment");
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
					
				}
				break;
			}
		}
		
	}
	
	
	public void markCalculationProcess(ActionEvent event) {
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
	}
	
	public void calculateMark(ActionEvent event) {
		if(tableIsLocked) return;
		
		if(averageRadioButton.isSelected()) {
			int i, j;
			ObservableList<CheckBox> checkBoxes = listViewForMarkCalculation.getItems();
			ArrayList<String> selectedAssessments = new ArrayList<>();
//			double[] calculatedAvgMarks = new double[tableView.getItems().size()];
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
					average += Float.parseFloat(students.get(i).getMark(selectedAssessments.get(j)));
				}
				average /= selectedAssessments.size();
//				calculatedAvgMarks[i] = average;
				
				//adding to previous mark in column
				float previousMark;
				if(students.get(i).getMark(colName).equals("")) {
					previousMark = 0.0f;
				} else {						
					previousMark = Float.parseFloat(students.get(i).getMark(colName));
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
			
		} 
		else if(bestRadioButton.isSelected()) {
			int i, j;
			ObservableList<CheckBox> checkBoxes = listViewForMarkCalculation.getItems();
			ArrayList<String> selectedAssessments = new ArrayList<>();
			float[] marksOfSelectedAssessments = new float[checkBoxes.size()]; //of 1 student
//			double[] calculatedBestMarks = new double[tableView.getItems().size()];//of all students
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
				for(j = 0; j < selectedAssessments.size(); j++) {
					marksOfSelectedAssessments[j] = Float.parseFloat(students.get(i).getMark(selectedAssessments.get(j)));
				}
				Arrays.sort(marksOfSelectedAssessments);
				float best = marksOfSelectedAssessments[j];
//				calculatedBestMarks[i] = best;
				
				//adding to previous mark in column
				float previousMark;
				if(students.get(i).getMark(colName).equals("")) {
					previousMark = 0.0f;
				} else {						
					previousMark = Float.parseFloat(students.get(i).getMark(colName));
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
					marksOfSelectedAssessments[j] = Float.parseFloat(students.get(i).getMark(selectedAssessments.get(j)));
				}
				Arrays.sort(marksOfSelectedAssessments);
				
				int bestN = Integer.parseInt(best_n_textField.getText());
				
				float best_n_Average = 0.0f;
				for(j = marksOfSelectedAssessments.length - 1; j > marksOfSelectedAssessments.length - bestN - 1; j--) {
					best_n_Average += marksOfSelectedAssessments[j];
				}
				best_n_Average /= bestN;
				
//				//adding or replacing to previous mark in column
				float previousMark;
				if(students.get(i).getMark(colName).equals("")) {
					previousMark = 0.0f;
				} else {						
					previousMark = Float.parseFloat(students.get(i).getMark(colName));
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
				try {
					float previousMark;
					if(students.get(i).getMark(colName).equals("")) {
						previousMark = 0.0f;
					} else {						
						previousMark = Float.parseFloat(students.get(i).getMark(colName));
					}
					
					if(addRadioButton.isSelected()) {
						students.get(i).setMark(colName, (previousMark + markToAdd)+"");					
					} else if(replaceRadioButton.isSelected()) {
						students.get(i).setMark(colName, markToAdd+"");
					}
				} catch (Exception e) {
					// do nothing
				}
			}
			tableView.setItems(students);
			tableView.refresh();
			bonusTextField.clear();
			bonusRadioButton.setSelected(false);
		}
	}
	
	public void calculateGrade() {
		if(tableIsLocked) return;
		
		gradeCol.setVisible(true);
		
		ObservableList<Assessment> assessmentsForGrade = assessmentMarksTable.getItems();
		int i, j;
//		for(i = 0; i < assessmentsForGrade.size(); i++) {
//			if(!assessmentsForGrade.get(i).isCountableForGrade()) {
//				assessmentsForGrade.remove(i);
//			}
//		}
		
		ObservableList<Student> students = tableView.getItems();

		double finalScore;
		for(i = 0; i < students.size(); i++) {
			finalScore = 0.0;
			for(j = 0; j < assessmentsForGrade.size(); j++) {
				if(!assessmentsForGrade.get(j).isCountableForGrade()) continue;
				String assessmentName = assessmentsForGrade.get(j).getAssessmentName();
				double mark = Double.parseDouble(students.get(i).getMark(assessmentName));
				finalScore += mark * assessmentsForGrade.get(j).getAssessmentWeight() / assessmentsForGrade.get(j).getAssessmentFullMark();
			}
			
			//taking the grade ranges from grading table
			ObservableList<Grade> grades = gradingTable.getItems();
			for (Grade grade : grades) {
				double min, max;
				if(!grade.getMinNumber().equals("*")) {					
					min = Double.parseDouble(grade.getMinNumber());
				} else {
					min = Double.MIN_VALUE;
				}
				
				if(!grade.getMaxNumber().equals("*")) {					
					max = Double.parseDouble(grade.getMaxNumber());
				} else {
					max = Double.MAX_VALUE;
				}
				
				if(Math.ceil(finalScore) >= min && finalScore <= max) {
//					students.get(i).setGrade(finalScore+"");
					students.get(i).setGrade(grade.getGradeName() + "  (" +finalScore + "%)");
					break;
				}
			}
//			if(finalScore >= 93) {
//				students.get(i).setGrade("A");
//			} else if (finalScore >= 90 && finalScore <= 92){
//				students.get(i).setGrade("A-");
//			} else if (finalScore >= 87 && finalScore <= 89){
//				students.get(i).setGrade("B+");
//			} else if (finalScore >= 83 && finalScore <= 86){
//				students.get(i).setGrade("B");
//			} else if (finalScore >= 80 && finalScore <= 82){
//				students.get(i).setGrade("B-");
//			} else if (finalScore >= 77 && finalScore <= 79){
//				students.get(i).setGrade("C+");
//			} else if (finalScore >= 73 && finalScore <= 76){
//				students.get(i).setGrade("C");
//			} else if (finalScore >= 70 && finalScore <= 72){
//				students.get(i).setGrade("C-");
//			} else if (finalScore >= 67 && finalScore <= 79){
//				students.get(i).setGrade("D+");
//			} else if (finalScore >= 60 && finalScore <= 66){
//				students.get(i).setGrade("D");
//			} else if (finalScore < 60){
//				students.get(i).setGrade("F");
//			}
		}
		
//		for(i = 0; i < students.size(); i++) {
//			students.get(i).setGrade("A");
//		}
		tableView.setItems(students);
		tableView.refresh();
		
	}
	
	public void addGrade() {
		Grade grade = new Grade();
		ObservableList<Grade> grades= gradingTable.getItems();
		grades.add(grade);
		gradingTable.setItems(grades);
		gradingTable.refresh();
	}
	
	public void removeGrade() {
		int row = gradingTable.getSelectionModel().getSelectedIndex();
		try {
			gradingTable.getItems().remove(row);
		} catch (Exception e) {
			// do nothing
		}
	}
	
	public void getDefaultGradingTable() {
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
					Assessment a = arg0.getRowValue();
					String newValueString = arg0.getNewValue();
					
					//now editing other related stuff
					 int i;
					 for(i = 0; i < assessmentNames.size(); i++) {
						 if(assessmentNames.get(i).equals(a.getAssessmentName())) {
							 assessmentNames.set(i, newValueString);
							 assessmentsArrayList.set(i, a);
							 assessmentChoiceBox.getItems().set(i, newValueString);
							 choiceBoxForMarkCalculation.getItems().set(i, newValueString);
							 break;
						 }
					 }
					 
					 
//					 changing name in main table
					 for(i = 0; i < tableView.getColumns().size(); i++) {
						 if(tableView.getColumns().get(i).getText().equals(a.getAssessmentName())) {
							 tableView.getColumns().get(i).setText(newValueString);
							 tableView.refresh();
							 break;
						 }
					 }
					 
//					 changing name in student class
					 try {
						 tableView.getItems().get(0).setAssessmentNames(assessmentNames); //used get(0) because assessmentNames is static						
					 } catch (Exception e) {
						//If table is empty do nothing
					 }

					
					a.setAssessmentName(newValueString);
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
				}
				tableView.refresh();
			}
		});;
	}
}
