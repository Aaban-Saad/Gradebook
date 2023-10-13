package application;


import java.io.*;
import java.util.ArrayList;
import java.util.Date;

public class GradeWriter {
    private String filePath;

    public GradeWriter(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void writeGradebook(String version) {

        String gradebookData = "<Gradebook>\nVersion = " + version + "\nDateCreated = " + new Date() +"\n</Gradebook>";
        writeToFile(filePath, gradebookData);
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
        writeToFile(filePath, assessmentData.toString());
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
            if(marks.size() == 0) continue;
            for (String mark : marks) {
                studentData.append(",").append(mark);
            }
            studentData.append("\n");
        }
        studentData.append("</Students>");
        writeToFile(filePath, studentData.toString());
    }

    public void writeGrade(ArrayList<Grade> grades) {

        StringBuilder gradeData = new StringBuilder("<Grading>\n");
        for (Grade grade : grades) {

            gradeData.append(grade.getGradeName()).append(",")
                    .append(grade.getMinNumber()).append(",")
                    .append(grade.getMaxNumber()).append("\n");
        }
        gradeData.append("</Grading>");
        writeToFile(filePath, gradeData.toString());
    }

    private void writeToFile(String filePath, String data) {
        try  {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true));
            writer.write(data + "\n\n");
            writer.close();
            
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    public String readGradebook() {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
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
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
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
        } catch (IOException e) {
            System.err.println("Error reading Assessments: " + e.getMessage());
        }
        return assessments;
    }

    public ArrayList<Student> readStudent() {
        ArrayList<Student> students = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean inStudentsSection = false;
            while ((line = reader.readLine()) != null) {
                if (line.trim().equalsIgnoreCase("<Students>")) {
                    inStudentsSection = true;
                    continue;
                }
                if (line.trim().equalsIgnoreCase("</Students>")) {
                    break;
                }
                if (inStudentsSection) {
                    String[] parts = line.split(",");
                    if (parts.length >= 4) {
                        Student student = new Student();
                        student.setSn(Integer.parseInt(parts[0]));
                        student.setId(parts[1]);
                        student.setName(parts[2]);
                        student.setGrade(parts[3]);

                        ArrayList<String> marks = new ArrayList<>();
                        for(int i = 4; i < parts.length; i++) {
                            marks.add(parts[i]);
                        }
                        student.setAssessmentMarks(marks);
                        students.add(student);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading Students: " + e.getMessage());
        }
        return students;
    }

    public ArrayList<Grade> readGrade() {
        ArrayList<Grade> grades = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean inGradingSection = false;
            while ((line = reader.readLine()) != null) {
                if (line.trim().equals("<Grading>")) {
                    inGradingSection = true;
                    continue;
                }
                if (line.trim().equals("</Grading>")) {
                    break;
                }
                if (inGradingSection) {
                    String[] parts = line.split(",");
                    if (parts.length >= 3) {
                        Grade grade = new Grade();
                        grade.setGradeName(parts[0]);
                        grade.setMinNumber(parts[1]);
                        grade.setMaxNumber(parts[2]);
                        grades.add(grade);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading Grades: " + e.getMessage());
        }
        return grades;
    }

    public void ExportCSV(ArrayList<Student> students, String file) {

        StringBuilder studentData = new StringBuilder("");
        ArrayList<String> marks = new ArrayList<>();
        
        studentData.append("SN").append(",")
                    .append("ID").append(",")
                    .append("Name").append(",")
                    .append("Grade");
        marks = Student.getAssessmentNames();
        if(marks.size() == 0){
            for (String mark : marks) {
                studentData.append(",").append(mark);
            }
            studentData.append("\n");
        }

        for (Student student : students) {
            studentData.append(student.getSn()).append(",")
                    .append(student.getId()).append(",")
                    .append(student.getName()).append(",")
                    .append(student.getGrade());

            marks = student.getAssessmentMarks();
            if(marks.size() == 0) continue;
            for (String mark : marks) {
                studentData.append(",").append(mark);
            }
            studentData.append("\n");
        }
        writeToCsvFile(file, studentData.toString());
    }

    private void writeToCsvFile(String filePath, String data) {
        try  {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            writer.write(data + "\n\n");
            writer.close();
            
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

}