package com.memishian.tictactoe;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JFrame;


public class TicTacToe implements Runnable{

	private String ip = "localhost";
	private int port = 88888;
	private Scanner scanner = new Scanner(System.in);
	private final int WIDTH = 0;
	private final int HEIGHT = 0;
	private JFrame frame;
	private Thread thread;
	
	private Painter painter; 
	
	//end point of connection between two machines
	//communicating between two machines over a network
	private Socket socket;
	
	//writing primitive data to output stream
	private DataOutputStream dos;
	
	//lets app write primitive data to stream
	private DataInputStream dis;
	
	
	
	public TicTacToe(){
		
	}
	
	public void run(){
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TicTacToe ticTacToe = new TicTacToe();
	}

	public class Painter {
		
	}
	
}
