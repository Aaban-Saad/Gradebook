package application;


import java.io.*;
import java.util.ArrayList;
import java.util.Date;

public class GradeWriter {
    private File file;
    BufferedWriter writer;

    public GradeWriter(File file) {
        this.file = file;
        try {
			this.writer = new BufferedWriter(new FileWriter(file));
		} catch (IOException e) {
			
		}
    }

    public File getfile() {
        return file;
    }

    public void setfile(File file) {
        this.file = file;
    }

    public void writeGradebook(String version) {

        String gradebookData = "<Gradebook>\nVersion = " + version + "\nDateCreated = " + new Date() +"\n</Gradebook>";
        writeToFile(file, gradebookData);
    }

    public void writeAssessment(ArrayList<Assessment> assessments) {

        StringBuilder assessmentData = new StringBuilder("<Assessments>\n");
        for (Assessment assessment : assessments) {

            assessmentData.append(assessment.getAssessmentName()).append(",")
                    .append(assessment.getAssessmentFullMark()).append(",")
                    .append(assessment.getAssessmentWeight()).append(",")
                    .append(assessment.isCountableForGrade()).append("\n");
        }
        assessmentData.append("</Assessments>");
        writeToFile(file, assessmentData.toString());
    }

    public void writeStudent(ArrayList<Student> students) {

        StringBuilder studentData = new StringBuilder("<Students>\n");
        ArrayList<String> marks = new ArrayList<>();
        
        for (Student student : students) {

            studentData.append(student.getSn()).append(",")
                    .append(student.getId()).append(",")
                    .append(student.getName()).append(",")
                    .append(student.getGrade());

            marks = student.getAssessmentMarks();
            if(marks.size() != 0) {
            	for (String mark : marks) {
            		studentData.append(",").append(mark);
            	}
            }
            studentData.append("\n");
        }
        studentData.append("</Students>");
        writeToFile(file, studentData.toString());
    }

    public void writeGrade(ArrayList<Grade> grades) {

        StringBuilder gradeData = new StringBuilder("<Grading>\n");
        for (Grade grade : grades) {

            gradeData.append(grade.getGradeName()).append(",")
                    .append(grade.getMinNumber()).append(",")
                    .append(grade.getMaxNumber()).append("\n");
        }
        gradeData.append("</Grading>");
        writeToFile(file, gradeData.toString());
    }

    private void writeToFile(File file, String data) {
        try  {
            writer.write(data + "\n\n");
            
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    public String readGradebook() {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder gradebookData = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                gradebookData.append(line).append("\n");
            }
            return gradebookData.toString();
        } catch (IOException e) {
            System.err.println("Error reading Gradebook: " + e.getMessage());
            return null;
        }
    }
    public ArrayList<Assessment> readAssessment() {
        ArrayList<Assessment> assessments = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean inAssessmentsSection = false;
            while ((line = reader.readLine()) != null) {
                if (line.trim().equalsIgnoreCase("<Assessments>")) {
                    inAssessmentsSection = true;
                    continue;
                }
                if (line.trim().equalsIgnoreCase("</Assessments>")) {
                    break;
                }
                if (inAssessmentsSection) {
                    String[] parts = line.split(",");
                    if (parts.length >= 4) {
                        Assessment assessment = new Assessment();
                        assessment.setAssessmentName(parts[0]);
                        assessment.setAssessmentFullMark(Double.parseDouble(parts[1]));
                        assessment.setAssessmentWeight(Double.parseDouble(parts[2]));
                        assessment.setCountForGrade(Boolean.parseBoolean(parts[3]));
                        assessments.add(assessment);
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("Error reading Assessments: " + e.getMessage());
        }
        return assessments;
    }

    public void ExportCSV(ArrayList<Student> students) {

        StringBuilder studentData = new StringBuilder("");
        ArrayList<String> marks = new ArrayList<>();
        
        studentData.append("SN").append(",")
                    .append("ID").append(",")
                    .append("Name").append(",")
                    .append("Grade");
        marks = Student.getAssessmentNames();
        if(marks.size() != 0){
            for (String mark : marks) {
                studentData.append(",").append(mark);
            }
        }
        studentData.append("\n");

        for (Student student : students) {
            studentData.append(student.getSn()).append(",")
                    .append(student.getId()).append(",")
                    .append(student.getName()).append(",")
                    .append(student.getGrade());

            marks = student.getAssessmentMarks();
            if(marks.size() != 0) {
            	for (String mark : marks) {
            		studentData.append(",").append(mark);
            	}
            }
            studentData.append("\n");
        }
        writeToCsvFile(file, studentData.toString());
    }

    public void writeToCsvFile(File file, String data) {
        try  {
            writer.write(data + "\n\n");
            
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
    
    public void close() {
		try {
			writer.close();
		} catch (IOException e) {

		}
	}

}