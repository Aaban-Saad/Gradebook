package application;

import java.io.*;
import java.util.ArrayList;

// The GradeReader class is responsible for reading data from a file related to grades, assessments, students, and grading details.
public class GradeReader {
    
    private File file;            // File object representing the input file.
    BufferedReader reader;       // BufferedReader to read the contents of the file.

    // Constructor that takes a File object and initializes the reader.
    public GradeReader(File file) {
        this.file = file;
        try {
            this.reader = new BufferedReader(new FileReader(file));
        } catch (IOException e) {
            // Handle IOException if there is an issue creating the BufferedReader.
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

    // Method to read the entire gradebook as a string from the file.
    public String readGradebook() {
        try {
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
        try {
            String line;
            boolean inAssessmentsSection = false;
            while ((line = reader.readLine()) != null) {
                // Check if the line marks the start of the Assessments section.
                if (line.trim().equalsIgnoreCase("<Assessments>")) {
                    inAssessmentsSection = true;
                    continue;
                }
                // Check if the line marks the end of the Assessments section.
                if (line.trim().equalsIgnoreCase("</Assessments>")) {
                    break;
                }
                // Process lines within the Assessments section.
                if (inAssessmentsSection) {
                    String[] parts = line.split(",");
                    if (parts.length >= 4) {
                        // Create an Assessment object and set its properties based on the file data.
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
            // Handle IOException if there is an issue reading assessments.
            System.err.println("Error reading Assessments: " + e.getMessage());
        }
        return assessments;
    }

    // Method to read student data from the file and return an ArrayList of Student objects.
    public ArrayList<Student> readStudent() {
        ArrayList<Student> students = new ArrayList<>();
        try {
            String line;
            boolean inStudentsSection = false;
            while ((line = reader.readLine()) != null) {
                // Check if the line marks the start of the Students section.
                if (line.trim().equalsIgnoreCase("<Students>")) {
                    inStudentsSection = true;
                    continue;
                }
                // Check if the line marks the end of the Students section.
                if (line.trim().equalsIgnoreCase("</Students>")) {
                    break;
                }
                // Process lines within the Students section.
                if (inStudentsSection) {
                    String[] parts = line.split(",");
                    if (parts.length >= 4) {
                        // Create a Student object and set its properties based on the file data.
                        Student student = new Student();
                        student.setSn(Integer.parseInt(parts[0]));
                        student.setId(parts[1]);
                        student.setName(parts[2]);
                        student.setGrade(parts[3]);

                        // Create an ArrayList to store assessment marks for the student.
                        ArrayList<String> marks = new ArrayList<>();
                        for (int i = 4; i < parts.length; i++) {
                            marks.add(parts[i]);
                        }
                        student.setAssessmentMarks(marks);
                        students.add(student);
                    }
                }
            }
        } catch (IOException e) {
            // Handle IOException if there is an issue reading student data.
            System.err.println("Error reading Students: " + e.getMessage());
        }
        return students;
    }

    // Method to read grading details from the file and return an ArrayList of Grade objects.
    public ArrayList<Grade> readGrade() {
        ArrayList<Grade> grades = new ArrayList<>();
        try {
            String line;
            boolean inGradingSection = false;
            while ((line = reader.readLine()) != null) {
                // Check if the line marks the start of the Grading section.
                if (line.trim().equals("<Grading>")) {
                    inGradingSection = true;
                    continue;
                }
                // Check if the line marks the end of the Grading section.
                if (line.trim().equals("</Grading>")) {
                    break;
                }
                // Process lines within the Grading section.
                if (inGradingSection) {
                    String[] parts = line.split(",");
                    if (parts.length >= 3) {
                        // Create a Grade object and set its properties based on the file data.
                        Grade grade = new Grade();
                        grade.setGradeName(parts[0]);
                        grade.setMinNumber(parts[1]);
                        grade.setMaxNumber(parts[2]);
                        grades.add(grade);
                    }
                }
            }
            // Close the reader after reading grading details.
            reader.close();
        } catch (IOException e) {
            // Handle IOException if there is an issue reading grading details.
            System.err.println("Error reading Grades: " + e.getMessage());
        }
        return grades;
    }

    // Method to close the BufferedReader.
    public void close() {
        try {
            reader.close();
        } catch (IOException e) {
            // Handle IOException if there is an issue closing the reader.
        }
    }
}
