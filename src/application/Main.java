package application;

	
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
	
	
	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = (BorderPane)FXMLLoader.load(getClass().getResource("HomeWindow.fxml"));
			Scene scene = new Scene(root,960,540);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public static Connection getConnection() throws Exception{
		Connection connection;
		String MYSQL_URI="jdbc:mysql://uk3z8eh3hn4bf7oq:ZzQdiYnqcyyqorjCLhgY@bgg6j6gd6h9py8pz6pqr-mysql.services.clever-cloud.com:3306/bgg6j6gd6h9py8pz6pqr";
		String MYSQL_USER="uk3z8eh3hn4bf7oq";
		String MYSQL_PASSWORD="ZzQdiYnqcyyqorjCLhgY";

		System.out.println("Connecting database...");
		try {
			connection = DriverManager.getConnection(MYSQL_URI, MYSQL_USER, MYSQL_PASSWORD);
			System.out.println("Database connected!");
			return connection;
		    
		} catch (SQLException e) {
			e.printStackTrace();
		    throw new IllegalStateException("Cannot connect the database!", e);
		}
	}
}
