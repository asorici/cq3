package org.aimas.craftingquest.core;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.aimas.craftingquest.user.MainAI;

public class Test {

	static long startTime;
	
	public static long getStartTime() {
		return startTime;
	}

	public static void setStartTime() {
		startTime = System.currentTimeMillis();
	}
	
	private static long[] getSecrets(){
		Random rnd = new Random();
		long[] secrets = new long[GamePolicy.noPlayers];
		
		for(int i=0; i < secrets.length; i++) {
		    secrets[i] = rnd.nextLong();
		}
		
		return secrets;
		
	}
	
	private static String[] getSecretsStrings(long[] secrets) {
		String[] r = new String[secrets.length];
		for (int i=0; i< secrets.length; i++) {
			  r[i] = secrets[i]+"";
		}
		
		return r;
	}
	
	public static void main(String[] args) {
		Test.setStartTime();
		
		final long[] secrets = getSecrets();
		final String[] servArgs = getSecretsStrings(secrets);
		
		// generate secrets and send them as cmd line arguments
		Thread server = new Thread(new Runnable() {

			public void run() {
				try {
					Server0.main(servArgs);
				} catch (Exception ex) {
					Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		});
		server.start();
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Thread[] clients = new Thread[GamePolicy.noPlayers];
		for (int i = 0; i < clients.length; i++) {
			final int holdi = i + 1;
			clients[i] = new Thread(new Runnable() {

				public void run() {
					try {
						MainAI.main(new String[]{"org.aimas.craftingquest.user.DummyAI", "localhost", "1198", "CraftingQuest", "" + holdi});
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
