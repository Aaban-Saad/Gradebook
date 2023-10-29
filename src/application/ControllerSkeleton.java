package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

public abstract class ControllerSkeleton { 

    @FXML
    protected Button addAssessmentButton;

    @FXML
    protected Button addGradeButton;

    @FXML
    protected Button addIdButton;

    @FXML
    protected ToggleGroup addOrReplace;

    @FXML
    protected RadioButton addRadioButton;

    @FXML
	protected TableView<Student> tableView;
	
    @FXML
    protected TableView<Assessment> assessmentMarksTable;
    
    @FXML
    protected TableColumn<Assessment, Double> assessmentFullMarkCol;

    @FXML
    protected TableColumn<Assessment, String> assessmentNameCol;

    @FXML
    protected TableColumn<Assessment, Double> assessmentWeightCol;
    
    @FXML
    protected TableColumn<Assessment, CheckBox> assessmentCountColumn;
	
	@FXML
    protected TableColumn<Student, Integer> snCol;
	
	@FXML
    protected TableColumn<Student, String> idCol;
	
	@FXML
    protected TableColumn<Student, String> nameCol;
	
	@FXML
    protected TableColumn<Student, String> gradeCol;

    @FXML
    protected RadioButton averageRadioButton;

    @FXML
    protected RadioButton bestRadioButton;

    @FXML
    protected RadioButton best_n_RadioButton;

    @FXML
    protected TextField best_n_textField;

    @FXML
    protected RadioButton bonusRadioButton;

    @FXML
    protected TextField bonusTextField;

    @FXML
    protected Button calculateGradeButton;

    @FXML
    protected Button calculateMark;

    @FXML
    protected TextField enterAssessment;

    @FXML
    protected TextArea enterId;

    @FXML
    protected TextArea enterName;

    @FXML
    protected TextField idToRemove;

    @FXML
    protected Label leftStatus;

    @FXML
    protected CheckBox lockTable;

    @FXML
    protected ToggleGroup radioButtonsForMarkCalculation;

    @FXML
    protected Button removeAssessmentButton;

    @FXML
    protected Button removeGradeButton;

    @FXML
    protected Button removeStudentButton;

    @FXML
    protected RadioButton replaceRadioButton;

    @FXML
    protected Label rightStatus;

    @FXML
    protected Button snRecalculate;
    
    @FXML
    protected ChoiceBox<String> assessmentChoiceBox;

    @FXML
	protected TableView<Grade> gradingTable;
	  
	@FXML
	protected TableColumn<Grade, String> gradeFromCol;
	  
	@FXML
	protected TableColumn<Grade, String> gradeToCol;
	
	@FXML
	protected TableColumn<Grade, String> gradeNameCol;
	
	@FXML
	protected ChoiceBox<String> choiceBoxForMarkCalculation;
	  
	@FXML
	protected ListView<CheckBox> listViewForMarkCalculation;
    
    @FXML
    abstract void addAssessment();;

    @FXML
    abstract void addGrade();  

    @FXML
    abstract void addIdName();  

    @FXML
    abstract void calculateGrade();  

    @FXML
    abstract void calculateMark();      

    @FXML
    abstract void getDefaultGradingTable();     

    @FXML
    abstract void lockTable();     

    @FXML
    abstract void markCalculationProcess();  
     
    @FXML
    abstract void newFile();  

    @FXML
    abstract void openFile();  

    @FXML
    abstract void recalculateSerialNumber();  

    @FXML
    abstract void removeAssessment();  

    @FXML
    abstract void removeGrade();  

    @FXML
    abstract void removeStudent();  

    @FXML
    abstract void saveAs();       

    @FXML
    abstract void saveCSV();  

    @FXML
    abstract void saveFile();  
} 

 
