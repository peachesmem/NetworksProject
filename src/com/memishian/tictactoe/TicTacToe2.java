package com.memishian.tictactoe;

import java.io.BufferedReader; 
import java.io.IOException; 
import java.io.InputStreamReader;
import java.io.PrintWriter; 
import java.net.ServerSocket; 
import java.net.Socket;

public class TicTacToe2 {

	//create a tic tac toe protocol instead of just 
	//passing data between client and server 
	//can test game with telnet? 
	//allows unlimited number of pairs of player to play
	
	public static void main(String[] args) throws Exception{
		ServerSocket listen = new ServerSocket(8888);
		System.out.println("Tic Tac Toe Server is running");
		try{
			while(true){
				TTTGame game = new TTTGame(); 
				TTTGame.Player playerX = game.new Player(listen.accept(), 'X');
				TTTGame.Player playerO = game.new Player(listen.accept(), 'O');
				playerX.setOpponent(playerO);
				playerO.setOpponent(playerX);
				game.currentPlayer = playerX;
				playerX.start();
				playerO.start();
			}
		}finally{
			listen.close(); 
		}
	}
}
	//implements a 2 player game
class TTTGame{
		//a board has 9 squares and each is either clear or 
		//has a marker in it put there by a player 
		//use an array of player references - if null the 
		//corresponding square is unowned, otherwise the array
		//cell stores a reference to the player that owns it
	private Player[] board ={
			null, null, null, 
			null, null, null, 
			null, null, null}; 
		
	//who is currently playing
	Player currentPlayer; 
	
	//will return current state of board
	//if the current board has a winner
	//check all the options that could make a win 
	//check horizontal wins first and then vertical
	public boolean hasWinner(){
		return
				(board[0] != null && board[0]==board[1] && board[0] == board[2])
				|| (board[3] != null && board[3] == board[4] && board[3] == board[5])
				|| (board[6] != null && board[6] == board[7] && board[6] == board[8])
				||(board[0] != null && board[0] == board[3] && board[0] == board[6])
				||(board[1] != null && board[1] == board[4] && board[1] == board[7])
				||(board[2] != null && board[2] == board[5] && board[2] == board[8])
				||(board[0] != null && board[0] == board[4] && board[0] == board[8])
				||(board[2] != null && board[2] == board[4] && board[2] == board[6]);
	}
	
	//this will check to see if there are no more empty spaces in the board
	public boolean boardFilledUp(){
		for(int i=0; i<board.length; i++){
			if(board[i] == null){
				return false; 
			}
		}
		return true;
	}
	
	//this will check if the next move is legal
	//aka that the current player is the one requesting th emove
	//and the square is not already filled
	//if it is legal then the game will be updated
	//next player is notified 
	public synchronized boolean legalMove(int location, Player player){
		if(player == currentPlayer && board[location] == null){
			board[location] = currentPlayer;
			currentPlayer = currentPlayer.opponent;
			currentPlayer.otherPlayerMoved(location);
			return true;
		}
		return false;
	}
	
	//this is a class for the helper threads in the multi 
	//threaded server application. A player is identified by a 
	//a character mark (X or O)
	//to communicate with client, player has socket with input 
	//and output streams - use a reader and a writer
	class Player extends Thread{
		char mark; 
		Player opponent; 
		Socket socket; 
		BufferedReader input; 
		PrintWriter output; 
		
		//this makes a handler thread for a given socket and mark 
		//it initializes the stream field and dipslays 
		//2 welcoming messages
		public Player(Socket socket, char mark){
			this.socket = socket; 
			this.mark = mark; 
			try{
				input = new BufferedReader(new InputStreamReader(socket.getInputStream())); 
				output = new PrintWriter(socket.getOutputStream(), true); 
				output.println("WELCOME"+mark);
				output.println("MESSAGE Waiting for opponent to connect"); 
			}catch(IOException e){
				System.out.println("Player died: "+e);
			}
		}
		
		//this accepts the opponent
		public void setOpponent(Player opponent){
			this.opponent=opponent; 
		}
		
		//handles otherPlayerMoved Message
		public void otherPlayerMoved(int location){
			output.println("OPPONENT_MOVED"+location);
			output.println(hasWinner()? "DEFEAT" : boardFilledUp()? "TIE": ""); 
			
		}
		
		//method that runs the thread
		public void run(){
			try{
				//only prints this if everyone connects
				output.println("MESSAGE All players connected");
				
				if(mark=='X'){
					output.println("MESSAGE Your move"); 
				}
				
				while(true){
					String command = input.readLine();
					if(command.startsWith("MOVE")){
						int location = Integer.parseInt(command.substring(5)); 
						if(legalMove(location, this)){
							output.println("VALID MOVE");
							output.println(hasWinner()? "VICTORY": 
								boardFilledUp()? "TIE" : ""); 
						}
						else{
							output.println("MESSAGE ?"); 
						}	
					}
					else if(command.startsWith("QUIT")){
						return;
					}
				}
				
			}catch(IOException e){
				System.out.println("Player died: "+e);
			}finally{
				try{
					socket.close();
				}catch(IOException e){}
			}	
			}
		}	
	}
}
