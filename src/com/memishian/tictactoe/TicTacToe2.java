package com.memishian.tictactoe;

import java.awt.event.MouseEvent;
import java.io.BufferedReader; 
import java.io.IOException; 
import java.io.InputStreamReader;
import java.io.PrintWriter; 
import java.net.ServerSocket; 
import java.net.Socket;
import java.util.Scanner;

public class TicTacToe2 {

	//create a tic tac toe protocol instead of just 
	//passing data between client and server 
	//allows unlimited number of pairs of player to play
	
	public static void main(String[] args) throws Exception{
		ServerSocket listen = new ServerSocket(8888);
		System.out.println("The Tic Tac Toe Server is now running");
		try{
			while(true){
				TTTGame game = new TTTGame(); 
				TTTGame.Player playerX = game.new Player(listen.accept(), 'X');
				TTTGame.Player playerO = game.new Player(listen.accept(), 'O');
				playerX.setOpponent(playerO);
				playerO.setOpponent(playerX);
				//player X will be the first person to log on
				game.currentPlayer = playerX;
				playerX.start();
				playerO.start();
			}
		}finally{
			listen.close(); 
		}
	}
}
	//a board has 9 squares and each is either clear or 
		//has a marker in it put there by a player 
		//use an array of player references - if null the 
		//corresponding square is unowned, otherwise the array
		//cell stores a reference to the player that owns it
	//implements a 2 player game
class TTTGame{
		
	private Player[] TTTboard ={
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
				(TTTboard[0] != null && TTTboard[0]==TTTboard[1] && TTTboard[0] == TTTboard[2])
				|| (TTTboard[3] != null && TTTboard[3] == TTTboard[4] && TTTboard[3] == TTTboard[5])
				|| (TTTboard[6] != null && TTTboard[6] == TTTboard[7] && TTTboard[6] == TTTboard[8])
				||(TTTboard[0] != null && TTTboard[0] == TTTboard[3] && TTTboard[0] == TTTboard[6])
				||(TTTboard[1] != null && TTTboard[1] == TTTboard[4] && TTTboard[1] == TTTboard[7])
				||(TTTboard[2] != null && TTTboard[2] == TTTboard[5] && TTTboard[2] == TTTboard[8])
				||(TTTboard[0] != null && TTTboard[0] == TTTboard[4] && TTTboard[0] == TTTboard[8])
				||(TTTboard[2] != null && TTTboard[2] == TTTboard[4] && TTTboard[2] == TTTboard[6]);
	}
	
	//this will check to see if there are no more empty spaces in the board
	public boolean boardFilled(){
		for(int i=0; i<TTTboard.length; i++){
			if(TTTboard[i] == null){
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
	public synchronized boolean moveCheck(int location, Player player){
		if(player == currentPlayer && TTTboard[location] == null){
			TTTboard[location] = currentPlayer;
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
		Player opponent; 
		char character; 
		Socket TTTsocket; 
		BufferedReader input = null; 
		PrintWriter output = null; 
		
		//this makes a handler thread for a given socket and mark 
		//it initializes the stream field and dipslays 
		//2 welcoming messages
		public Player(Socket TTTsocket, char character){
			this.TTTsocket = TTTsocket; 
			this.character = character; 
			try{
				input = new BufferedReader(new InputStreamReader(TTTsocket.getInputStream())); 
				output = new PrintWriter(TTTsocket.getOutputStream(), true); 
				output.println("WELCOME Character "+ character);
				output.println("MESSAGE We are waiting for the opponent to connect"); 
			}catch(IOException e){
				System.out.println("The player died: "+e);
			}
		}
		
		//this accepts the opponent
		public void setOpponent(Player opponent){
			this.opponent=opponent; 
		}
		
		//handles otherPlayerMoved Message
		public void otherPlayerMoved(int location){
			output.println("OPPONENT_MOVED"+location);
			output.println(hasWinner()? "DEFEAT" : boardFilled()? "TIE": ""); 
			
		}
		
		//method that runs the thread
		public void run(){
			try{
				//only prints this if everyone connects
				output.println("MESSAGE All players are connected");
				
				if(character=='X'){
					output.println("MESSAGE It is now your move"); 
				}
				String command;
				while(true){
					command = input.readLine();
					
					if(command.startsWith("MOVE")){
						int location = Integer.parseInt(command.substring(5)); 
						if(moveCheck(location, this)){
							output.println("VALID MOVE");
							output.println(hasWinner()? "VICTORY": 
								boardFilled()? "TIE" : ""); 
						}
						else{
							output.println("MESSAGE ?"); 
						}	
					}
					else if(command.startsWith("QUIT")){
						//if(command.startsWith("QUIT")){
						return;
					}
				}
			
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally{
				try{
					TTTsocket.close();
				}catch(IOException e){}
			}	
			}
		
		
		}	
	}

