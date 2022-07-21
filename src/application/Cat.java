/**
 * 
 */
package application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;


public class Cat {
	//Elements and setups
	Circle catVisual;
	
	
	//static variables
	int[][] vectorMoveset = new int[][] {
		{1,0,-1},{1,-1,0},{0,-1,1},
		{-1,0,+1},{-1,+1,0},{0,+1,-1}
	};
	
	int moves = 0;
	int[] position = new int[] {0,0,0};
	List<int[]> moveset = new ArrayList<>();
	
	
	
	public Cat(Circle InputVisual) {
		catVisual = InputVisual;
	}	
	
	public int[] move(int[] currentClick, int[] size, boolean[][] checkGrid, Game debug) {
		int[] nextMove;
		
		//check if a new path needs to be calculated if there is no moveset or user blocked current path 
		if (arrayInList(moveset, Hex.flatCudeToCube(currentClick)) || moveset.size() == 0) {
			nextMove = NextPath(size, checkGrid, debug);
			
			if (moveset.size() != 0) {
				moveset.remove(0);
			}
		} else {
			nextMove = moveset.get(0);
			moveset.remove(0);
		}
		
		
		
		if (nextMove != null) {
			position = nextMove;
		}
		
		if (GameController.debug) {
			debug.gc.setFill(Color.RED);
			debug.makeHexagon(nextMove, 10);
		}
		
		return nextMove;
		
		
	}
	
	
	public int[] NextPath(int[] size, boolean[][] checkGrid, Game debug) {
		if (GameController.debug) {debug.render();}
		
		
		moves += 1;
		if (moves == 0) {
			return vectorMoveset[new Random().nextInt(6)];
		}
		
		//setting up bfs arrays
		boolean pathFound = false;
		List<int[]> visited = new ArrayList<int[]>();
		List<ArrayList<int[]>> paths = new ArrayList<ArrayList<int[]>>();
		
		//starting point to the paths and visited
		visited.add(position);
		
		
		//priming BFS path lists ///tested, works
		for (int i = 0; i<6;i++) {
			ArrayList<int[]> path = new ArrayList<int[]>();
			
			int[] newPoint = new int[] { //adding the vector movements the the current position to prime the BFS paths list, i.e initial positions
					position[0]+vectorMoveset[i][0],
					position[1]+vectorMoveset[i][1],
					position[2]+vectorMoveset[i][2]
			};
			
			
			//check if new points are at bounds
			if (Math.abs(Hex.cubeToFlat(newPoint)[0]) > size[0]/2 || Math.abs(Hex.cubeToFlat(newPoint)[1]) > size[1]/2) {
				pathFound = true;
				return newPoint;
						
			} else if (checkGrid[Hex.cubeToFlat(newPoint)[0]+ size[0]/2][Hex.cubeToFlat(newPoint)[1]+ size[1]/2] == false) {
				//check that the point is not blocked
				//add to related BFS lists
				
				path.add(newPoint);
				visited.add(newPoint);
				paths.add(path);
				
				if (GameController.debug) {
					debug.gc.setFill(Color.ORANGE);
					debug.makeHexagon(newPoint, 10);
				}
			}
			
			
		} 
		
		while (paths.size() != 0 && pathFound == false) {
			
			for (int i = 0; i<6;i++) {
				ArrayList<int[]> path = new ArrayList<int[]>();
				path.addAll(paths.get(0));
				
				int[] newPoint = new int[] { //adding the vector movements the the current position to prime the BFS paths list, i.e initial positions
						path.get(path.size()-1)[0]+vectorMoveset[i][0],
						path.get(path.size()-1)[1]+vectorMoveset[i][1],
						path.get(path.size()-1)[2]+vectorMoveset[i][2]
				};

				
				if (arrayInList(visited, newPoint) == false) {
					
					//checking bounds of point
					if (Math.abs(Hex.cubeToFlat(newPoint)[0]) > size[0]/2 || Math.abs(Hex.cubeToFlat(newPoint)[1]) > size[1]/2) {
						pathFound = true;
						path.add(newPoint);
						moveset = path;
						
						if (GameController.debug) {
							debug.gc.setFill(Color.ORANGE);
							debug.makeHexagon(newPoint, 10);
							
							debug.gc.setFill(Color.RED);
							for (int[] P : path) {
								debug.makeHexagon(P, 10);
							} 
						}
						
						return path.get(0);
								
					//checked if blocked
					} else if (checkGrid[Hex.cubeToFlat(newPoint)[0]+ size[0]/2][Hex.cubeToFlat(newPoint)[1]+ size[1]/2] == false) {
						path.add(newPoint);
						visited.add(newPoint);
						paths.add(path);
						
						if (GameController.debug) {
							debug.gc.setFill(Color.ORANGE);
							debug.makeHexagon(newPoint, 10);
						}
					}
				}
			} 
			paths.remove(0);
		}
		
		//if there is not paths, return random move
		//use path found which will be false in this case
		
		//unique random array from 0-5 and use those to select vector then test vector\
		moveset.removeAll(moveset);
		int[] randomarr = new int[6];
		for (int i=0; i<=5; i++) {
			randomarr[i] = i;
		}
		
		int optionsLeft = 6;
		while (pathFound == false && optionsLeft >= 1) {
			int randomNum = new Random().nextInt(optionsLeft);
			int[] newPoint  = new int[] {
					position[0] + vectorMoveset[randomarr[randomNum]][0],
					position[1] + vectorMoveset[randomarr[randomNum]][1],
					position[2] + vectorMoveset[randomarr[randomNum]][2]		
			};
			
			if (checkGrid[Hex.cubeToFlat(newPoint)[0]+ size[0]/2][Hex.cubeToFlat(newPoint)[1]+ size[1]/2] == false) {
				pathFound = true;
				return newPoint;
			}
			
			randomarr = shiftArrayLeft(randomarr, randomNum);
			optionsLeft--;
			
		}
		
		//game over
		return null;
	}
	
	
	

	
	public void visMove(int[] position, int radius, int spacing) {
		
	}
	
	public void visExit() {
		
	}
	
	//helper function, linear search
	public static boolean arrayInList(List<int[]> listIn, int[] arrayIn) {
		int i = 0;
		boolean found = false;
		while (i<listIn.size() && found == false) {			
			if (Arrays.equals(arrayIn,listIn.get(i))) {
				found = true;
			}
			i++;
		}
		return found;
	}
	
	//helper shift function
	private int[] shiftArrayLeft(int[] array, int position) { //position is from index 0
		for (int i = position; i <= 4; i++) {
			array[i] = array[i + 1];
		}
		return array;
	}
}
