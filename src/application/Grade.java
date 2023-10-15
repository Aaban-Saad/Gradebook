package application;

// The Grade class represents a grade in a course, with a name and corresponding numerical range.
public class Grade {
	
	// Fields to store information about the grade.
	private String gradeName;    // Name of the grade (e.g., A++, A, B).
	private String minNumber;    // Minimum numerical value for the grade.
	private String maxNumber;    // Maximum numerical value for the grade.
	
	// Default constructor initializes the grade to "A++" with a numerical range from 0 to 100.
	public Grade() {
		this.gradeName = "A++";
		this.minNumber = "0";
		this.maxNumber = "100";
	}
	
	// Parameterized constructor allows setting the grade name and numerical range.
	public Grade(String minNumber, String maxNumber, String gradeName) {
		this.gradeName = gradeName;
		this.minNumber = minNumber;
		this.maxNumber = maxNumber;
	}

	// Getter method for gradeName.
	public String getGradeName() {
		return gradeName;
	}

	// Setter method for gradeName.
	public void setGradeName(String gradeName) {
		this.gradeName = gradeName;
	}

	// Getter method for minNumber.
	public String getMinNumber() {
		return minNumber;
	}

	// Setter method for minNumber.
	public void setMinNumber(String minNumber) {
		this.minNumber = minNumber;
	}

	// Getter method for maxNumber.
	public String getMaxNumber() {
		return maxNumber;
	}

	// Setter method for maxNumber.
	public void setMaxNumber(String maxNumber) {
		this.maxNumber = maxNumber;
	}
}
