package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class User {
	String username;
	String password;
	int gamesPlayed = 0;
	int gamesWon = 0;
	int highScore = 0;
	
	List<Integer> sortedScores;  //note to remember for futher, int is a primitivae data type while Interger is wrapper class, and thefore, as java only accepts non-primitive lists Interget is used
	
	
	public User (String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	
	public boolean endGame(int score, boolean win) throws Exception {
		int scoreToSend = 0;
		boolean newHigh = false;
		
		this.gamesPlayed++;	
		if (win == false) {
			scoreToSend = 0;
		} else {
			scoreToSend = score;
			this.gamesWon++;
		}
		
		if (scoreToSend > this.highScore) {
			this.highScore = scoreToSend;
			newHigh = true;
			System.out.println("won game");
		}
		
		
		Connection connection = Main.getConnection();
		PreparedStatement pstmt1;
		PreparedStatement pstmt2;
		
		//trying to put all statements in one as to reduce things to send to database as there is a high latency
		String query1 = "INSERT into Scores(username, score) values (?, ?)";
		String query2 = "UPDATE Users SET highScore = ?, gamesPlayed = ?, gamesWon = ? WHERE username = ?";
		pstmt1 = connection.prepareStatement(query1);
		pstmt2 = connection.prepareStatement(query2);
		
		pstmt1.setString( 1, username);
		pstmt1.setInt( 2, scoreToSend);
		
		pstmt2.setInt( 1, this.highScore);
		pstmt2.setInt( 2, this.gamesPlayed);
		pstmt2.setInt( 3, this.gamesWon);
		pstmt2.setString( 4, username);
		
		if (win) {
			pstmt1.execute();
		}
		
		pstmt2.execute();
		
		pstmt1.close();
		pstmt2.close();
		
		connection.close();
		return newHigh;
	}
	
	
	
	public static User login (String username, String password) throws Exception {
		User user = null;
		
		// check username and passowrd is correct
		Connection connection = Main.getConnection();
			
		String query = "SELECT * FROM Users WHERE username = ? AND pass = ?;";
		PreparedStatement pstmt = connection.prepareStatement( query );
			
		pstmt.setString( 1, username); //lol no sql injection for you
		pstmt.setString( 2, password);
		
		ResultSet results = pstmt.executeQuery();
			
		if (results.next()) {
			user = new User(username, password); 
			
			user.highScore = results.getInt("highScore");
			user.gamesPlayed = results.getInt("gamesPlayed");
			user.gamesWon = results.getInt("gamesWon");
		}
			
		results.close();
		pstmt.close();
		connection.close();
		
		return user;
	}
	
	public static User register (String username, String password) throws Exception {
		User user = null;
		
		// check username and passowrd is correct
		Connection connection = Main.getConnection();
			
		String query = "INSERT into Users(username, pass) values (?, ?);";
		PreparedStatement pstmt = connection.prepareStatement( query );
			
		pstmt.setString( 1, username); //lol no sql injection for you
		pstmt.setString( 2, password);
		
		pstmt.execute();
		user = new User(username, password); 
			
		pstmt.close();
		connection.close();
		
		return user;
	}
	
	public static String[] getLeaderboard() {
		Connection connection;
		String[] leaderboardString = new String[7];
		try {
			connection = Main.getConnection();
			String query = "SELECT * from Scores ORDER BY score DESC LIMIT 7;";
			PreparedStatement pstmt = connection.prepareStatement(query);
			ResultSet results = pstmt.executeQuery();
			
			int i = 0;
			while (results.next()) {
				leaderboardString[i] = (i+1) + ".     " + results.getInt("score") + " by " + results.getString("username");
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return leaderboardString;
	}


	public void changePassword(String new_password) {
		Connection connection;
		try {
			connection = Main.getConnection();
			String query = "update Users SET pass = ? where username = ?;";
			PreparedStatement pstmt;
			pstmt = connection.prepareStatement(query);
		
			
			pstmt.setString( 1, new_password); //lol no sql injection for you
			pstmt.setString( 2, username);
			
			pstmt.execute();
			password = new_password;
	
			pstmt.close();
			connection.close();
			this.password = new_password;
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return;
	}
}
