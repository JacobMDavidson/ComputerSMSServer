package com.jacobmdavison.computersmsserver;

import javax.swing.JFrame;

public class Driver {
	public static void main( String[] args ) {
		//opens the window where the messages will be received and sent
        ServerBoard frame = new ServerBoard();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
	}
}
