package application;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.paint.Color;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class LoginController implements Initializable{

	
	//to transfer information
	public HomeController homeController; //from home page
	public GamePreStartController gamePreStartController; //from other one 
	
	//for diffent functions of window
	public boolean isChange;
	public boolean isRegister;
	
	//elements
	public TextField usernameBox;
	public TextField passwordBox;
	public Button EnterPageBtn;
	public Text titleLoginPage;
	public Label returnLbl;
	public AnchorPane anchorPane;
	
	public Label Lbl1;
	public Label Lbl2;
	
	
	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		titleLoginPage.setFont(Font.loadFont(getClass().getResourceAsStream("resources/mysteron.ttf"), 70));
	}
	
	
	public void update() {
		//chaning UI depending on function
		if (isRegister){
			titleLoginPage.setText("Register");
			EnterPageBtn.setText("Register");
		} else if (isChange) {
			titleLoginPage.setText("Change Password");
			EnterPageBtn.setText("Change");
			titleLoginPage.setFont(Font.loadFont(getClass().getResourceAsStream("resources/mysteron.ttf"), 50));
			Lbl1.setText("Old_Password");
			Lbl2.setText("New_Password");
		}
	}
	
	//
	//Login Screen
	//
	public void backButtonPressed(ActionEvent event) {
		if (isChange) {
			gamePreStartController.open();
		} else {
			homeController.open(false);
		}		
	}
	
	public void EnterPageBtnPressed(ActionEvent event) {
		User user = null;
		returnLbl.setTextFill(Color.RED);
		
		if (isChange) {
			if (usernameBox.getText().length() > 20 || passwordBox.getText().length() > 20) {
				returnLbl.setText("Length cannot be greater then 20");
				return;
			}
			
			if (usernameBox.getText().length() == 0 || passwordBox.getText().length() == 0) {
				returnLbl.setText("Length cannot be zero");
				return;
			}
			
			if (usernameBox.getText().equals(gamePreStartController.user.password) == false) {
				returnLbl.setText("Old password is incorrect ");
				return;
			}
			
			if (passwordBox.getText().equals(gamePreStartController.user.password)) {
				returnLbl.setText("New password cannot be same as old password");
				return;
			}
			
			gamePreStartController.user.changePassword(passwordBox.getText());
			returnLbl.setTextFill(Color.GREEN);
			returnLbl.setText("Password Changed");
			
			
		}else {
			if (usernameBox.getText().length() > 20 || passwordBox.getText().length() > 20) {
				returnLbl.setTextFill(Color.RED);
				returnLbl.setText("Length cannot be greater then 20 for username or password");
				return;
			}
			
			if (usernameBox.getText().length() == 0 || passwordBox.getText().length() == 0) {
				returnLbl.setTextFill(Color.RED);
				returnLbl.setText("Password or username cannot be blank");
				return;
			}
			
	
			
			returnLbl.setText("");
			
			
			//anchorPane.setCursor(Cursor.WAIT); //if i want to do this threading is needed, i tried but it was hard as the FX contoller would be on a differnet thread to the user login and i would have to make a class for the handeler. effort

			Boolean loginAccepted = false;
			
			try {
				if (isRegister == false) {
					try {
						user = User.login(usernameBox.getText().toLowerCase(), passwordBox.getText());
					} catch (IllegalStateException e1) {
						//database failed connection
						returnLbl.setTextFill(Color.RED);
						returnLbl.setText("Could not connect to Database: Please Continue as Guest");
						
					} catch (SQLException e2) {
						returnLbl.setTextFill(Color.RED);
						returnLbl.setText("Database Error: Please Continue as Guest or try different username/password");
						e2.printStackTrace();
					} catch (Exception e3) {
						e3.printStackTrace();
					}
					
					if (user != null) {
						returnLbl.setTextFill(Color.GREEN);
						loginAccepted = true;
						returnLbl.setText("Success, welcome "+user.username);
						
					} else {
						returnLbl.setTextFill(Color.RED);
						returnLbl.setText("Invalid Username or Password");
					}
					
				} else {
					try {
						user = User.register(usernameBox.getText().toLowerCase(), passwordBox.getText());
					} catch (IllegalStateException e1) {
						returnLbl.setTextFill(Color.RED);
						returnLbl.setText("Could not connect to Database: Please Continue as Guest");
						
					} catch (java.sql.SQLIntegrityConstraintViolationException e2) {
						returnLbl.setTextFill(Color.RED);
						returnLbl.setText("User Already Exists");
						
					} catch (SQLException e3) {
						returnLbl.setTextFill(Color.RED);
						returnLbl.setText("Database Error: Please Continue as Guest");
					} catch (Exception e4) {
						e4.printStackTrace();
					}
					
					if (user != null) {
						returnLbl.setTextFill(Color.GREEN);
						loginAccepted = true;
						returnLbl.setText("Success, welcome "+user.username);
						
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			homeController.user = user;
			if (loginAccepted) {
				homeController.open(true);
			}
		}
	}
}
