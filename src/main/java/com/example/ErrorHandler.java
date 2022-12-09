package com.example;

public class ErrorHandler {
	
	public static void printError(String subject, String message) {
		System.out.println(subject + ":" + message);
		if(subject.contains("(S")) return;
		//if(!Helper.isLocal)
		//	Mailer.sendMail(subject, message, "ma.manoj@gmail.com");
		//System.out.println("");
	}

}
