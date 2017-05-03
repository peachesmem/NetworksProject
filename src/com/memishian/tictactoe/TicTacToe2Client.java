package com.memishian.tictactoe;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


public class TicTacToe2Client {

    private JFrame frame = new JFrame("Tic Tac Toe");
    private JLabel msgLabel = new JLabel("");
    private ImageIcon currentIcon;
    private ImageIcon opponentIcon;

    private Square[] board = new Square[9];
    private Square currentSquare;
    private static int PORT = 8888;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    //this will connect to the server and do GUI and GUI listeners
    public TicTacToe2Client(String serverAddress) throws Exception {

        // Setup networking
        socket = new Socket(serverAddress, PORT);
        in = new BufferedReader(new InputStreamReader(
            socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // Layout GUI: graphical user interface
        msgLabel.setBackground(Color.lightGray);
        frame.getContentPane().add(msgLabel, "South");

        JPanel boardPanel = new JPanel();
        boardPanel.setBackground(Color.black);
        boardPanel.setLayout(new GridLayout(3, 3, 2, 2));
        for (int i = 0; i < board.length; i++) {
            final int j = i;
            board[i] = new Square();
            board[i].addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    currentSquare = board[j];
                    out.println("MOVE " + j);}});
            boardPanel.add(board[i]);
        }
        frame.getContentPane().add(boardPanel, "Center");
    }

    //this thread will listen to the messages from the server
    public void play() throws Exception {
        String response;
        try {
            response = in.readLine();
            if (response.startsWith("WELCOME")) {
            	
            	//NOTE*** I ran this line - charAt(8) and then it gave me 
            	//an index out of boudn error- so i changed it to 7 and it worked
            	
                char mark = response.charAt(7);
                currentIcon = new ImageIcon(mark == 'X' ? "x.gif" : "o.gif");
                opponentIcon  = new ImageIcon(mark == 'X' ? "o.gif" : "x.gif");
                frame.setTitle("Tic Tac Toe - Player " + mark);
            }
            while (true) {
                response = in.readLine();
                if (response.startsWith("VALID_MOVE")) {
                    msgLabel.setText("Valid move, please wait");
                    currentSquare.setIcon(currentIcon);
                    currentSquare.repaint();
                } else if (response.startsWith("OPPONENT_MOVED")) {
                    int loc = Integer.parseInt(response.substring(15));
                    board[loc].setIcon(opponentIcon);
                    board[loc].repaint();
                    msgLabel.setText("Opponent moved, your turn");
                } else if (response.startsWith("VICTORY")) {
                    msgLabel.setText("You win");
                    break;
                } else if (response.startsWith("DEFEAT")) {
                    msgLabel.setText("You lose");
                    break;
                } else if (response.startsWith("TIE")) {
                    msgLabel.setText("You tied");
                    break;
                } else if (response.startsWith("MESSAGE")) {
                    msgLabel.setText(response.substring(8));
                }
            }
            out.println("QUIT");
        }
        finally {
            socket.close();
        }
    }

    private boolean wantsToPlayAgain() {
        int response = JOptionPane.showConfirmDialog(frame,
            "Do you want to play again?",
            "Lets do it!!!",
            JOptionPane.YES_NO_OPTION);
        frame.dispose();
        return response == JOptionPane.YES_OPTION;
    }

    /**
     * Graphical square in the client window.  Each square is
     * a white panel containing.  A client calls setIcon() to fill
     * it with an Icon, presumably an X or O.
     */
    static class Square extends JPanel {
        JLabel label = new JLabel((Icon)null);

        public Square() {
            setBackground(Color.white);
            add(label);
        }

        public void setIcon(Icon icon) {
            label.setIcon(icon);
        }
    }

    /**
     * Runs the client as an application.
     */
    public static void main(String[] args) throws Exception {
        while (true) {
            String serverAddress = (args.length == 0) ? "localhost" : args[1];
            TicTacToe2Client client = new TicTacToe2Client(serverAddress);
            client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            client.frame.setSize(240, 160);
            client.frame.setVisible(true);
            client.frame.setResizable(false);
            client.play();
            if (!client.wantsToPlayAgain()) {
                break;
            }
        }
    }
}
