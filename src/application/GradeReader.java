package application;


import java.io.*;
import java.util.ArrayList;

public class GradeReader {
    private File file;
    BufferedReader reader;

    public GradeReader(File file) {
        this.file = file;
        try {
			this.reader = new BufferedReader(new FileReader(file));
		} catch (IOException e) {
			
		}
    }

    public File getfile() {
        return file;
    }

    public void setfile(File file) {
        this.file = file;
    }

    public String readGradebook() {
        try {
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
        try {
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
                        assessment.setAssessmentName(parts[0].trim());
                        assessment.setAssessmentFullMark(Double.parseDouble(parts[1].trim()));
                        assessment.setAssessmentWeight(Double.parseDouble(parts[2].trim()));
                        assessment.setCountForGrade(Boolean.parseBoolean(parts[3].trim()));
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
        try {
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
                        student.setId(parts[1].trim());
                        student.setName(parts[2].trim());
                        student.setGrade(parts[3].trim());

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
        try {
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
                        grade.setGradeName(parts[0].trim());
                        grade.setMinNumber(parts[1].trim());
                        grade.setMaxNumber(parts[2].trim());
                        grades.add(grade);
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("Error reading Grades: " + e.getMessage());
        }
        return grades;
    }

    public void close() {
		try {
			reader.close();
		} catch (IOException e) {

		}
	}

}