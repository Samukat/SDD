package application;

import java.util.Random;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.util.Duration;


public class Game {
	//https://www.colorhexa.com/b5e61d
	//elements
	public Canvas gameCanvas;
	public GraphicsContext gc;
	public Cat cat; 
	
	//game vars
	private int filledHexes = 0;
	private int placedHexes = 0;
	private int[] hexHover;
	public int gameState = 0; //0 is nutural, -1 is loss, 1 is sub-win, 2 is win
	
	
	
	//setup
	public int[] size = new int[] {11,11}; //is also set by controller 
	private boolean[][] gridCheck;
	
	
	private int spacing = 8;
	public int radius = 18;
	private double inradius = 0.75; //percent of radius size (visual)
	private double hexOverlap;
	private int[] zeroLocation;
	
	
	//constructor on init
	public Game(Canvas gameCanvas, Circle catVisual) {
		System.out.println("game starting");
		gc = gameCanvas.getGraphicsContext2D();
		gridCheck = new boolean[size[0]][size[1]];
		
		zeroLocation = new int[] {(int) gameCanvas.getWidth()/2,(int) gameCanvas.getHeight()/2};
		hexOverlap = 0.25*(radius-2*spacing);
		cat = new Cat(catVisual);
		cat.catVisual.setRadius(radius*0.333);
	}
	
	public void setInitialHex(int isFilled) {
		filledHexes = isFilled;
		System.out.println(filledHexes);
		Random rand = new Random();
		
		//setting random block spots
		gridCheck = new boolean[size[0]][size[1]];
		for (int rnd = 0; rnd < isFilled; rnd++) {
			
			 int x = rand.nextInt(size[0]);
			 int y = rand.nextInt(size[1]);
			 if (gridCheck[x][y]) {
				 rnd--; 
			 }
			 
			 //check if the center is selected
			 if (x == size[0]/2 && y == size[1]/2) {
				 rnd--;
			 } else {
				 gridCheck[x][y] = true; 
			 }			 
		}
		
		render();
		
		if (GameController.debug) {
			gc.setFill(Color.BLACK);
			gc.fillRect(zeroLocation[0]-1, zeroLocation[1]-1, 2, 2);
		}
	}
	
	
	public void render() {
		gc.clearRect(0, 0, zeroLocation[0]*2, zeroLocation[1]*2);
		
		
		for (int x = (int) (-size[0]*0.5); x <= (int) (size[0]*0.5); x++) {
			for (int y = (int) (-size[1]*0.5); y <= (int) (size[1]*0.5); y++) {
				
				
				int[] NewCoord = new int[] {y, (x-(y-(y&1))/2), -y-(x-(y-(y&1))/2)}; //y, (x-60deg), (x+60deg)
				
				gc.setFill(Color.rgb(181, 230, 29));
				makeHexagon(new int[] {NewCoord[1],NewCoord[0]}, this.radius);

				if (gridCheck[x+size[0]/2][y+size[1]/2]) {
					gc.setFill(Color.rgb(240, 240, 240));
					makeHexagon(new int[] {NewCoord[1],NewCoord[0]}, (int) (radius*inradius));
				} 
			}
		}
	}


	public void mouseClick(double[] MousePos) {
		if (gameState == -1 || gameState == 2) {
			return;
		}
		
		int[] position = mouseToPos(MousePos);
		//gc.setFill(Color.rgb(181, 230, 29));
		if (position != null && position[0] == cat.position[0] && position[1] == cat.position[1]) {  //cat uses cubic coordinate system, however, position variable is axial
			return;
		}
		
		gc.setFill(Color.rgb(240, 240, 240));

		if (position != null && gridCheck[Hex.cubeToFlat(position)[0] + size[0]/2][Hex.cubeToFlat(position)[1] + size[1]/2] == false) {
			
			gridCheck[Hex.cubeToFlat(position)[0] + size[0]/2][Hex.cubeToFlat(position)[1] + size[1]/2] = true;
			makeHexagon(position, (int) (radius*inradius));
			hexHover = null;
			filledHexes++;
			placedHexes++;
			int[] catMove = cat.move(position, size, gridCheck, this);
			if (catMove == null) {
				gameState = 2;	
				FadeTransition fade = new FadeTransition(Duration.millis(1000), cat.catVisual);
				fade.setFromValue(1);
				fade.setToValue(0);
				fade.play();
				
			}else{		
				
				int x = Hex.cubeToFlat(catMove)[0];
				int y = Hex.cubeToFlat(catMove)[1];
				int offsetx = (int) ((this.radius+spacing*0.5+1)*0.866*(y&1)) + spacing*x;   //+1 from rounding down correction, offset occurs on every even value
				if (x<0) {offsetx += 1;} //error correcting from rounded roots
				int offsety = (int) (0.5*this.radius+1-spacing)*y;
				
				int[] C = new int[] {(int) ((x*1.732*this.radius)+offsetx), y*2*this.radius - offsety};
				
				
				TranslateTransition translation = new TranslateTransition(Duration.millis(100),cat.catVisual);
				translation.setToX(C[0]);
				translation.setToY(C[1]);
				
				
				
				if (Math.abs(Hex.cubeToFlat(catMove)[0]) > size[0]/2 || Math.abs(Hex.cubeToFlat(catMove)[1]) > size[1]/2) {
					gameState = -1;
					FadeTransition fade = new FadeTransition(Duration.millis(500), cat.catVisual);
					fade.setFromValue(1);
					fade.setToValue(0);
					
					translation.setInterpolator(Interpolator.EASE_IN);
					translation.setDuration(Duration.millis(500));
					fade.play();
					
				}
				translation.play();
			}
		}	
	}

	public void mouseMoved(double[] MousePos) {
		if (gameState == -1 || gameState == 2) {
			return;
		}
		
		
		int[] position = mouseToPos(MousePos);
		if (position != null && position[0] == cat.position[0] && position[1] == cat.position[1]) {  //cat uses cubic coordinate system, however, position variable is axial
			return;
		}
		
		if (position != null && gridCheck[Hex.cubeToFlat(position)[0] + size[0]/2][Hex.cubeToFlat(position)[1] + size[1]/2]) {
			return;
		}
		
		if (hexHover != position && hexHover != null) {
			gc.setFill(Color.rgb(181, 230, 29));
			makeHexagon(hexHover, (int) (radius));
			hexHover = null;
		}
				
		if (position != null) {
			gc.setFill(Color.rgb(216, 242, 135));
			makeHexagon(position, (int) (radius*inradius));
			hexHover = position;
		} else if (hexHover != null) {;
			gc.setFill(Color.rgb(181, 230, 29));
			makeHexagon(hexHover, (int) (radius));
			hexHover = null;
		}
		
	}
	
	
	public void makeHexagon(int[] position, int radius) {
		if (position == null) {
			return;
		}
		
		int y = position[1];
		int x = (position[0]+(y-(y&1))/2);
		
		//Hexagonal offset and pixel center of each hexagon
		int offsetx = (int) ((this.radius+spacing*0.5+1)*0.866*(y&1)) + spacing*x;   //+1 from rounding down correction, offset occurs on every even value
		if (x<0) {offsetx += 1;} //error correcting from rounded roots
		int offsety = (int) (0.5*this.radius+1-spacing)*y;
		
		int[] C = new int[] {(int) ((zeroLocation[0]+x*1.732*this.radius)+offsetx), 
				zeroLocation[1]+y*2*this.radius - offsety};
		
		gc.fillPolygon(new double[]{C[0],C[0]+radius*(0.866),C[0]+radius*(0.866),C[0],C[0]-radius*(0.866),C[0]-radius*(0.866)},
				new double[]{C[1]+radius,C[1]+0.5*radius,C[1]-0.5*radius,C[1]-radius,C[1]-0.5*radius,C[1]+0.5*radius},
				6);
		
		//debug
		if (GameController.debug) {
			Color color = (Color) gc.getFill();
			gc.setFill(Color.BLACK);
			gc.fillRect(C[0]-1, C[1]-1, 2, 2);
			
			gc.setFont(new Font(radius/3.5));
			gc.fillText("y:" + y + ", q:" + position[0] + ", r:" + (-y-position[0]), C[0], C[1]);
			gc.setFill(color);
		}
	}
	
	
	
	public int[] mouseToPos (double[] MousePos) {
		double xSpacingAndRad = spacing*0.5+0.866*radius;
		
		//function to return a int array in the form (r, y) this works as the third value can be calculated as all values add to 0, note the "/" represents DIV
		//this took along time
		
		//Calculate Height value
		int yOrd = (int) (
				(MousePos[1]-zeroLocation[1])/(spacing*0.5+radius*0.75)
				);
		
		if (yOrd > 0) {
			yOrd += 1;
		} else {
			yOrd -= 1;
		}
		yOrd /= 2;
		
		
		
		int xOrd = (int) (
				(MousePos[0]-zeroLocation[0]-yOrd*xSpacingAndRad)/xSpacingAndRad
				);
		
		if (xOrd > 0) {
			xOrd += 1;
		} else {
			xOrd -= 1;
		}
		
		xOrd /= 2;
		
		boolean inBorder = false;
		int xBound =(int) (Math.abs((MousePos[0]-zeroLocation[0]-yOrd*xSpacingAndRad-xSpacingAndRad)%(2*xSpacingAndRad))-xSpacingAndRad);
		if (xOrd <= 0) {
			xBound = -xBound;
		}
		
		
		int yBound = (int) (Math.abs((MousePos[1]-zeroLocation[1]-(spacing*0.5+radius*0.75))%(spacing+radius*1.5))-(spacing*0.5+radius*0.75));
		if (yOrd > 0) {
			yBound = -yBound;
		}
		
		if (Math.abs(xBound) <= 0.866*radius && Math.abs(yBound) <= 0.5*radius) {
			inBorder = true;
		}
		
		if ((Math.abs(yBound) <= radius-0.5*Math.abs(xBound)) && Math.abs(yBound) > 0.5*radius) {
			inBorder = true;
		}
		
		//testing the tips of the hexagon as the bounding boxes are averaged
		if (hexOverlap > 0) { 
			if ((Math.abs(yBound) > (radius*0.5+spacing)+((xSpacingAndRad-Math.abs(xBound))*0.5))) {
				inBorder = true;
				
				//fixing ordinal values
				if (xBound < 0 && yBound > 0) {
					yOrd -= 1;
				} else if (xBound < 0 && yBound > 0) {
					xOrd +=1;
				} else if (xBound < 0 && yBound < 0) {
					yOrd += 1;
					xOrd -= 1;
				} else {
					yOrd += 1;
				}
			}
		}
		
		//if is hexagon and in grid boundaries
		if (inBorder && Math.abs(yOrd) <= size[1]/2 && Math.abs(xOrd+(yOrd-(yOrd&1))/2) <= size[0]/2) {
			return new int[] {xOrd,yOrd};
		} else {
			return null;
		}
	}
	
	//getters and setters
	public int getFilledHexes() {
		return filledHexes;
	}
	
	public int getGameState() {
		return gameState;
	}
	
	public int getScore() {
		int score = (int) (100000/(double)(size[0]*size[1]));
		double percentFilled = 1-(double)filledHexes/(size[0]*size[1]);		
		score = (int)(score * percentFilled);
		return score;
		
	}
	
	public int getPlacedHexes() {
		return placedHexes;
	}
	
}