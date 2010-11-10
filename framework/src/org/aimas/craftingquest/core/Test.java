package org.aimas.craftingquest.core;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.aimas.craftingquest.user.MonkeyAI;

public class Test {

	static long startTime;
	
	public static long getStartTime() {
		return startTime;
	}

	public static void setStartTime() {
		startTime = System.currentTimeMillis();
	}
	
	public static void main(String[] args) {
		Test.setStartTime();
			
		// generate secrets and send them as cmd line arguments
		Thread server = new Thread(new Runnable() {

			public void run() {
				try {
					Server0.main(null);
				} catch (Exception ex) {
					Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		});
		server.start();

		Thread[] clients = new Thread[GamePolicy.noPlayers];
		for (int i = 0; i < clients.length; i++) {
			clients[i] = new Thread(new Runnable() {

				public void run() {
					try {
						MonkeyAI.main(null);
					} catch (Exception ex) {
						Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			});
		}

		for (int i = 0; i < clients.length; i++) {
			clients[i].start();
		}

	}

}
