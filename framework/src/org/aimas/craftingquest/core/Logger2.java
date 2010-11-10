package org.aimas.craftingquest.core;

public class Logger2 {

	public static long t0 = 0;

	public static void start() {
		if (t0 == 0) {
			t0 = System.currentTimeMillis();
			log("LOG", "start", "start");
		}
	}

	public static void log(String who, String where, String what) {
		System.out.println("[" + (System.currentTimeMillis() - t0) + "][" + who + "][" + where + "][" + what + "]");
	}
}
