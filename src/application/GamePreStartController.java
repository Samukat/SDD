package application;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GamePreStartController implements Initializable{
	public BorderPane preStartPane;
	
	public Text title1;
	public Text title2;
	public Text title3;
	
	public Button logoutButton;
	public Button startButton;
	public Button resetButton;
	
	public Slider difficultySlider;
	public Slider sizeSlider;
	
	public Label winsLbl;
	public Label totalLbl;
	public Label topLbl;
	
	
	public User user;
	Stage Loginstage; //for changing password
	
	
	
	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		title1.setFont(Font.loadFont(getClass().getResourceAsStream("resources/mysteron.ttf"), 40));
		title2.setFont(Font.loadFont(getClass().getResourceAsStream("resources/mysteron.ttf"), 35));
		title3.setFont(Font.loadFont(getClass().getResourceAsStream("resources/mysteron.ttf"), 35));		
	}
	
	public void update() {
		if (user != null) {
			winsLbl.setText(Integer.toString(user.gamesWon));
			totalLbl.setText(Integer.toString(user.gamesPlayed));
			topLbl.setText(Integer.toString(user.highScore));
			
			title2.setText(user.username);
		} else {
			title2.setText("guest");
			resetButton.setVisible(false);
		}
	}
	
	
	public void logoutButtonPressed(ActionEvent event) {
		try {
			Node node = (Node) event.getSource();
			Stage stage = (Stage) node.getScene().getWindow();
		    Scene scene = stage.getScene();

		    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("HomeWindow.fxml"));
			Parent root = (Parent) fxmlLoader.load();
			scene.setRoot(root);
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public void startButtonPressed(ActionEvent event) {
		try {
			Node node = (Node) event.getSource();
			Stage stage = (Stage) node.getScene().getWindow();
		    Scene scene = stage.getScene();

		    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GameWindow.fxml"));
			Parent root = (Parent) fxmlLoader.load();
			
			GameController gameController = fxmlLoader.getController();
			scene.setRoot(root);
			
			gameController.user = this.user;
			gameController.size = (int)sizeSlider.getValue();
			gameController.difficulty = difficultySlider.getValue();
			gameController.update();
			
			scene.setRoot(root);
		} catch (IOException e) {
			e.printStackTrace();
		}   
	}

	public void settingButtonPressed(ActionEvent event) throws IOException{
		FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginWindow.fxml"));
		Parent root = (Parent) loader.load();
		
		LoginController loginController = loader.getController();
		loginController.gamePreStartController = this;
		loginController.isChange = true;
		
		loginController.update();
		
		
		preStartPane.setDisable(true);
		
		Loginstage = new Stage();
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		Loginstage.setScene(scene);   
		
		//if window closes
		Loginstage.setOnCloseRequest(WindowEvent -> {
			this.open();
		});
		Loginstage.show();	
		
		
	}
	
	public void open() {
		preStartPane.setDisable(false);
		Loginstage.close(); 
	}
}
