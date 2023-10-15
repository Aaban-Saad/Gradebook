package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;

// The Main class serves as the entry point for the JavaFX application.
public class Main extends Application {
	
	// The start method is called when the JavaFX application is launched.
	@Override
	public void start(Stage primaryStage) {
		try {
			// Load the Gradebook.fxml file using FXMLLoader to create the UI.
			Parent root = FXMLLoader.load(getClass().getResource("Gradebook.fxml"));
			
			// Create a new Scene with the loaded UI and set its dimensions.
			Scene scene = new Scene(root, 1000, 600);
			
			// Add an external CSS file to style the UI.
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			// Set the created Scene to the primaryStage (main window).
			primaryStage.setScene(scene);
			
			// Set the title for the application window.
			primaryStage.setTitle("Gradebook - alpha");
			
			// Add an icon to the application window.
			Image iconImage = new Image("icon.png");
			primaryStage.getIcons().add(iconImage);
			
			// Display the primaryStage.
			primaryStage.show();
		} catch(Exception e) {
			// Print the stack trace in case of an exception.
			e.printStackTrace();
		}
	}
	
	// The main method is the entry point for the Java application.
	public static void main(String[] args) {
		// Launch the JavaFX application.
		launch(args);
	}
}
