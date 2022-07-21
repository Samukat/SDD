package application;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GameController implements Initializable {
	//elements
	public Canvas GameCanvas;
	public BorderPane MainWindow;
	public GraphicsContext gc;
	public Circle catVisual;
	
	public Text title;
	public Button resetButton;
	public Text scoreText;
	public Text endText;
	public Text endScoreText;
	public VBox HTPBOX;
	public Text endHelpText;
	
	
	//game
	public Game game;
	public User user;
	private boolean endedFlag = false;
	
	
	public int size;
	public double difficulty;
	
	public static boolean debug = false;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		gc = GameCanvas.getGraphicsContext2D();   //Getting the Graphics for the canvas form the initialized canvas
		title.setFont(Font.loadFont(getClass().getResourceAsStream("resources/mysteron.ttf"), 40));
		endText.setFont(Font.loadFont(getClass().getResourceAsStream("resources/mysteron.ttf"), 80));
		endScoreText.setFont(Font.loadFont(getClass().getResourceAsStream("resources/mysteron.ttf"), 35));
		
		//Set Hex Grid and initialise game
		gc.setTextAlign(TextAlignment.CENTER);
		game = new Game(GameCanvas, catVisual);
		
		
		FadeTransition ft = new FadeTransition(Duration.millis(1000), MainWindow);
		ft.setFromValue(0.0);
		ft.setToValue(1.0);
		ft.play();
		
		endText.setOpacity(0);
		endText.setDisable(true);
		endScoreText.setOpacity(0);
		endScoreText.setDisable(true);

		//resizing
		MainWindow.widthProperty().addListener((obs, oldVal, newVal) -> {
			//GameCanvas.setWidth((double) newVal);
			//game.setInitialHex(4);
		});

		MainWindow.heightProperty().addListener((obs, oldVal, newVal) -> {
		     // Do whatever you want
		});	
	}
	
	public void update() {
		
		//slider to game size
		switch (size) {
			case 1:
				game.size = new int[] {9,9};
				break;
			case 2:
				game.size = new int[] {11,11};
				break;
			case 3:
				game.size = new int[] {13,13};
				break;
		}
		
		int toFill = (int) ((20-difficulty*0.2) * 0.01 * game.size[0]*game.size[1]); //diffiucty set a percentage infill of blockages between 0% and 20%
		game.setInitialHex(toFill);
		scoreText.setText("Blocks Placed: "+ game.getPlacedHexes() + "  Score: " + game.getScore());
		
		endHelpText.setVisible(false);
		if (user != null && user.gamesPlayed > 1) {
			HTPBOX.setVisible(false);
		}
	}
	
	
	public void ClickGrid(MouseEvent event) {
		//System.out.println(event.getX() + " " + event.getY());
		game.mouseClick(new double[] {event.getX(), event.getY()});
		scoreText.setText("Blocks Placed: "+ game.getPlacedHexes() + "  Score: " + game.getScore());
		
		//the game will end on a click so the end can be initatiated from here
		if ((game.getGameState() == -1 || game.getGameState() == 2) && endedFlag == false) {
			endGame();
		}
		
		
		//on partial end display futher help text
		if (game.getGameState() == 1) {
			endHelpText.setVisible(true);
		}
	}
	
	public void endGame() {
		resetButton.setText("New Game");
		if (game.getGameState() == -1) {
			endText.setText("Game Over");
		} else {
			endText.setText("You Win");
			endScoreText.setText("Score: " + game.getScore());
			FadeTransition ft1 = new FadeTransition(Duration.millis(2000), endScoreText);
			ft1.setFromValue(0.0);
			ft1.setToValue(1.0);
			ft1.play();
		}
		
		FadeTransition ft = new FadeTransition(Duration.millis(2000), endText);
		ft.setFromValue(0.0);
		ft.setToValue(1.0);
		ft.play();	
		
		endedFlag = true;
		if (user != null) {
			new Thread(() -> {
				try {
					user.endGame(game.getScore(), game.getGameState()==2);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}).start();
		}
		
		
		
		//if transitions dont finish before reset button pressed this is here to ensure they dont show up
		//its a bit odd though
		ft.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (game.getGameState() == 0) {
					endText.setOpacity(0);
					endScoreText.setOpacity(0);
				}
			}
		});
		
	}
	
	public void MouseMoved(MouseEvent event) {
		game.mouseMoved(new double[] {event.getX(), event.getY()});
	}	
	
	public void backButtonPressed(ActionEvent event) {
		
		try {
			Node node = (Node) event.getSource();
			Stage stage = (Stage) node.getScene().getWindow();
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
	
	public void resetButtonPressed() {
		FadeTransition ft = new FadeTransition(Duration.millis(200), GameCanvas);
		FadeTransition ft1 = new FadeTransition(Duration.millis(200), game.cat.catVisual);
		FadeTransition ft3 = new FadeTransition(Duration.millis(200), endText);
		FadeTransition ft4 = new FadeTransition(Duration.millis(200), endScoreText);
		
		
		
		//ft
		ft.setFromValue(1.0);
		ft.setToValue(0.0);
		ft.play();
		
		//ft1
		ft1.setFromValue(1.0);
		ft1.setToValue(0.0);
		if (game.cat.catVisual.getOpacity() != 0) {
			ft1.play();
		}
		
		//ft2
		ft3.setFromValue(1.0);
		ft3.setToValue(0.0);
		if (endText.getOpacity() != 0) {
			ft3.play();
		}
		
		//ft4
		ft4.setFromValue(1.0);
		ft4.setToValue(0.0);
		if (endScoreText.getOpacity() != 0) {
			ft4.play();
		}
		
		
		
		ft.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				reset();
			}
		});
	}
	
	private void reset() {
		game = new Game(GameCanvas, catVisual);
		FadeTransition ft = new FadeTransition(Duration.millis(200), GameCanvas);
		FadeTransition ft1= new FadeTransition(Duration.millis(200), game.cat.catVisual);
		
		ft.setFromValue(0.0);
		ft.setToValue(1.0);
		ft.play();
		
		
		ft1.setFromValue(0.0);
		ft1.setToValue(1.0);
		ft1.play();
		
		
		game.cat.catVisual.setTranslateX(0);
		game.cat.catVisual.setTranslateY(0);
		
		switch (size) {
			case 1:
				game.size = new int[] {9,9};
				break;
			case 2:
				game.size = new int[] {11,11};
				break;
			case 3:
				game.size = new int[] {13,13};
				break;
		}
		
		int toFill = (int) ((20-difficulty*0.2) * 0.01 * game.size[0]*game.size[1]); //diffiucty set a percentage infill of blockages between 0% and 20%
		game.setInitialHex(toFill);
		scoreText.setText("Blocks Placed: "+ game.getPlacedHexes() + "  Score: " + game.getScore());
		
		endedFlag = false;
		resetButton.setText("RESTART");
		
	}
}
