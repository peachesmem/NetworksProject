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
				Game game = new Game(); 
				
			}
		}finally{
			listen.close(); 
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
