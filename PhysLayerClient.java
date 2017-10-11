/* CS 380 - Computer Networks
 * Project 2 : Physical Layer of Communication in OSI Model
 * Ismail Abbas
 */
 
 import java.io.*;
 import java.net.*;
 import java.util*;
 import java.text.*;
 
 /*
  * This class will simulate the physical layer of communication
  */
 public class PhysLayerClient {
	 
	 /*
	  * This is the main method that will perform the essential
	  * tasks of the class
	  */
	 public static void main(String[]args) {
		 // Like everything else in life, you gotta try first before
		 // giving up
		 
	 }
	 
	/*
	 * This method will read the response from the server to test
	 * if the generated CRC code was correct
	 */
    public static boolean crcCheck(Socket s) {
        try {
            InputStream isStr = s.getInputStream();
            int successCode = is.read();
			// "If the boolean equals 1, return true for the method, else false"
            return (successCode == 1) ? true : false;
        } catch (Exception e) { }
		// This is just to satisfy Java coding conventions for methods
        return false;
    }
	 
	 
 }