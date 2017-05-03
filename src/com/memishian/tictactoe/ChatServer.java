package com.memishian.tictactoe;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;

public class ChatServer {
  public static void main(String args[]) {
	
	String st;
    ServerSocket echo = null;
    Socket csocket = null;
    BufferedReader is;
    PrintStream os;
    

    try {
      echo = new ServerSocket(8888); //port = 8888
    } catch (IOException e) {
      System.out.println(e);
    }

   
    //accept the socket connection 
    //initialize input streams and output streams
    System.out.println("Started the server");
    try {
      csocket = echo.accept();
      os = new PrintStream(csocket.getOutputStream());
      is = new BufferedReader(new InputStreamReader(csocket.getInputStream()));
      
      //echo back the received data to the client
      while (true) {
        st = is.readLine();
        os.println("Server says: " + st);
      }
    } catch (IOException e) {
      System.out.println(e);
    }
  }
}