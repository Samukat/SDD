package application;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class HomeController implements Initializable{
	//home window
	public BorderPane HomeWindow;
	public ListView<String> leaderboardUI;
	public Text Title;
	public String[] leaderboardList;
	
	
	User user = null;
	Node node;
	Stage Loginstage;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		Title.setFont(Font.loadFont(getClass().getResourceAsStream("resources/mysteron.ttf"), 70));	
		
		//worker thread for doings
		Thread worker = new Thread(new Runnable(){ 
		    public void run(){
		    	leaderboardList = User.getLeaderboard();
		    }
		});
		worker.start();
		
		//observer thread for notifications
		new Thread(new Runnable(){
		    public void run(){
		    	try{
		    		worker.join();
		    	} catch(Exception e){
		    		System.out.println(e);
		    	}finally{
		    		ObservableList<String> list = FXCollections.observableArrayList();
		    		for (int i = 0; i < 6; i++) {
		    			list.add(leaderboardList[i]);
		    		}
		    		leaderboardUI.setItems(list);
			    }
		    }
		}).start();
	}
	
	public void playClicked(ActionEvent event) throws IOException{
//		FadeTransition ft = new FadeTransition(Duration.millis(10), HomeWindow);
//		ft.setFromValue(1.0);
//		ft.setToValue(0.0);
//		ft.play();
		
	}
	
	public void LoginBtnPressed(ActionEvent event) throws IOException {
		this.node = (Node) event.getSource();

		FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginWindow.fxml"));
		Parent root = (Parent) loader.load();
		
		LoginController loginController = loader.getController();
		loginController.homeController = this;
		loginController.isRegister = false;
		loginController.update();
		HomeWindow.setDisable(true);
		
		Loginstage = new Stage();
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		Loginstage.setScene(scene); 
		
		//if window closes
		Loginstage.setOnCloseRequest(WindowEvent -> {
			this.open(false);
		});
		Loginstage.show();	
	}
	
	public void RegisterBtnPressed(ActionEvent event) throws IOException {
		this.node = (Node) event.getSource();
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginWindow.fxml"));
		Parent root = (Parent) loader.load();
		
		LoginController loginController = loader.getController();
		loginController.homeController = this;
		loginController.isRegister = true;
		loginController.update();
		HomeWindow.setDisable(true);
		
		Loginstage = new Stage();
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		Loginstage.setScene(scene); 
		
		//if window closes
		Loginstage.setOnCloseRequest(WindowEvent -> {
			this.open(false);
		});
		Loginstage.show();	
	}
	
	public void GuestBtnPressed(ActionEvent event) {
		this.node = (Node) event.getSource();
		System.out.println("guest");
		
		try {
			Stage stage = (Stage) this.node.getScene().getWindow();
		    Scene scene = stage.getScene();

		    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GamePreStartWindow.fxml"));
			Parent root = (Parent) fxmlLoader.load();
			
			GamePreStartController gamePreStartController = fxmlLoader.getController();
			scene.setRoot(root);
			
			gamePreStartController.update();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}         
	    
	}

	public void open(boolean loginAccepted) {
		HomeWindow.setDisable(false);
		Loginstage.close(); 
		
		if (loginAccepted) {
			try {
				Stage stage = (Stage) this.node.getScene().getWindow();
			    Scene scene = stage.getScene();

			    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GamePreStartWindow.fxml"));
				Parent root = (Parent) fxmlLoader.load();
				
				GamePreStartController gamePreStartController = fxmlLoader.getController();
				scene.setRoot(root);
				
				gamePreStartController.user = this.user;
				gamePreStartController.update();
				
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
	}
}
