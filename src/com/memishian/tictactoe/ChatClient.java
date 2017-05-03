package com.memishian.tictactoe;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatClient {
  public static void main(String[] args) {
	
	//initialization 
	BufferedReader input = null;
    Socket cSocket = null;
    PrintStream os = null;
    BufferedReader is = null;

    //use 8888 as a port for the socket, 
    //initialize input out output streams 
    try {
    	
      os = new PrintStream(cSocket.getOutputStream());
      is = new BufferedReader(new InputStreamReader(cSocket.getInputStream()));
      cSocket = new Socket("localhost", 8888);
      input = new BufferedReader(new InputStreamReader(System.in));
      
    } catch (UnknownHostException e) {
      System.err.println("error: Host is unknown");
    } catch (IOException e) {
      System.err.println("error: IO connection");
    }

   //if initialization without errors then write data to socket
    if (cSocket != null && os != null && is != null) {
      try {

       //once you see "ok" break, if not then keep reading/writing
        System.out.println("Client has started"
        		+ ". Please type some text. If you want to quit, type 'Ok'.");
        String response;
        os.println(input.readLine());
        while ((response = is.readLine()) != null) {
          System.out.println(response);
          if (response.indexOf("Ok") != -1) {
            break;
          }
          os.println(input.readLine());
        }

        //close both output stream, input stream, and socket
        os.close();
        is.close();
        cSocket.close();
      } catch (UnknownHostException e) {
        System.err.println("error: Host is unknown" + e);
      } catch (IOException e) {
        System.err.println("error: IOException:  " + e);
      }
    }
  }
}