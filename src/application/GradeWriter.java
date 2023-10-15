package application;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;

// The GradeWriter class is responsible for writing data related to grades, assessments, students, and grading details to a file.
public class GradeWriter {
    private File file;            // File object representing the output file.
    BufferedWriter writer;       // BufferedWriter to write contents to the file.

    // Constructor that takes a File object and initializes the writer.
    public GradeWriter(File file) {
        this.file = file;
        try {
            this.writer = new BufferedWriter(new FileWriter(file));
        } catch (IOException e) {
            // Handle IOException if there is an issue creating the BufferedWriter.
            // (Note: The exception is caught, but no action is taken, which may not be ideal.)
        }
    }

    // Getter method for the file.
    public File getfile() {
        return file;
    }

    // Setter method for the file.
    public void setfile(File file) {
        this.file = file;
    }

    // Method to write gradebook data to the file.
    public void writeGradebook(String version) {
        // Construct a string containing gradebook data and write it to the file.
        String gradebookData = "<Gradebook>\nVersion = " + version + "\nDateCreated = " + new Date() + "\n</Gradebook>";
        writeToFile(file, gradebookData);
    }

    // Method to write assessment data to the file.
    public void writeAssessment(ArrayList<Assessment> assessments) {
        // Construct a string containing assessment data and write it to the file.
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

    // Method to write student data to the file.
    public void writeStudent(ArrayList<Student> students) {
        // Construct a string containing student data and write it to the file.
        StringBuilder studentData = new StringBuilder("<Students>\n");
        ArrayList<String> marks = new ArrayList<>();

        for (Student student : students) {
            studentData.append(student.getSn()).append(",")
                    .append(student.getId()).append(",")
                    .append(student.getName()).append(",")
                    .append(student.getGrade());

            marks = student.getAssessmentMarks();
            if (marks.size() != 0) {
                for (String mark : marks) {
                    studentData.append(",").append(mark);
                }
            }
            studentData.append("\n");
        }
        studentData.append("</Students>");
        writeToFile(file, studentData.toString());
    }

    // Method to write grading details to the file.
    public void writeGrade(ArrayList<Grade> grades) {
        // Construct a string containing grade data and write it to the file.
        StringBuilder gradeData = new StringBuilder("<Grading>\n");
        for (Grade grade : grades) {
            gradeData.append(grade.getGradeName()).append(",")
                    .append(grade.getMinNumber()).append(",")
                    .append(grade.getMaxNumber()).append("\n");
        }
        gradeData.append("</Grading>");
        writeToFile(file, gradeData.toString());
    }

    // Private method to write data to the file.
    private void writeToFile(File file, String data) {
        try {
            writer.write(data + "\n\n");
        } catch (IOException e) {
            // Handle IOException if there is an issue writing to the file.
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    // Method to read the entire gradebook as a string from the file.
    public String readGradebook() {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder gradebookData = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                gradebookData.append(line).append("\n");
            }
            return gradebookData.toString();
        } catch (IOException e) {
            // Handle IOException if there is an issue reading the gradebook.
            System.err.println("Error reading Gradebook: " + e.getMessage());
            return null;
        }
    }

    // Method to read assessment data from the file and return an ArrayList of Assessment objects.
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
            // Handle IOException if there is an issue reading assessments.
            System.err.println("Error reading Assessments: " + e.getMessage());
        }
        return assessments;
    }

    // Method to export student data to a CSV file.
    public void ExportCSV(ArrayList<Student> students) {
        StringBuilder studentData = new StringBuilder("");
        ArrayList<String> marks = new ArrayList<>();

        // Construct header for CSV file.
        studentData.append("SN").append(",")
                .append("ID").append(",")
                .append("Name").append(",")
                .append("Grade");
        marks = Student.getAssessmentNames();
        if (marks.size() != 0) {
            for (String mark : marks) {
                studentData.append(",").append(mark);
            }
        }
        studentData.append("\n");

        // Construct data for CSV file.
        for (Student student : students) {
            studentData.append(student.getSn()).append(",")
                    .append(student.getId()).append(",")
                    .append(student.getName()).append(",")
                    .append(student.getGrade());

            marks = student.getAssessmentMarks();
            if (marks.size() != 0) {
                for (String mark : marks) {
                    studentData.append(",").append(mark);
                }
            }
            studentData.append("\n");
        }
        // Write data to CSV file.
        writeToCsvFile(file, studentData.toString());
    }

    // Private method to write data to a CSV file.
    public void writeToCsvFile(File file, String data) {
        try {
            writer.write(data + "\n\n");
        } catch (IOException e) {
            // Handle IOException if there is an issue writing to the file.
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    // Method to close the BufferedWriter.
    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            // Handle IOException if there is an issue closing the writer.
        }
    }
}
