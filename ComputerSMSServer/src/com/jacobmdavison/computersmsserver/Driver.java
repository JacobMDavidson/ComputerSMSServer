package com.jacobmdavison.computersmsserver;

public class Driver {
	public static void main(String[] args) {
		// opens the window where the messages will be received and sent
		ServerBoard frame = new ServerBoard();
		frame.pack();
		frame.setVisible(true);
	}
}
