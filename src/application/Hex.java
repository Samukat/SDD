package application;

public class Hex {
	//Function to return converted grid cords
	public static int[] cubeToFlat(int[] position){
		int y = position[1];
		int x = (position[0]+(y-(y&1))/2);
		return new int[] {x,y};
	}
	
	public static int[] flatToCube(int[] position){
		int y = position[1];
		int x = (position[0]-(y-(y&1))/2);
		return new int[] {x,y};
	}

	public static int[] flatCudeToCube(int[] currentClick) {
		return (new int[] {currentClick[0], currentClick[1], -currentClick[0]-currentClick[1]});
	}
}
