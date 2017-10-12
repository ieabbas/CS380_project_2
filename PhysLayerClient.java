/* CS 380 - Computer Networks
 * Project 2 : Physical Layer of Communication in OSI Model
 * Ismail Abbas
 */
 
 import java.io.*;
 import java.net.*;
 import java.util.*;
 import java.text.*;
 
 /*
  * This class will simulate the physical layer of communication
  * by 
  */
 public class PhysLayerClient {
	 
	 private static double BASE = 0;
	 
	 /*
	  * This is the main method that will perform the essential
	  * tasks of the class
	  */
	 public static void main(String[]args) {
		 // Like everything else in life, you gotta try first before
		 // giving up
		 try {
            Socket s = new Socket("18.221.102.182",38002);
            if(s.isConnected()) {
                System.out.println("\nConnected to server.");
                // Operations performed in order to decode message that is sent
                System.out.println(isHighOrLow(s));
                ArrayList<Integer> sig = getBytes(s);
                ArrayList<Integer> fiveB = nrziEncoding(sig);
                ArrayList<Integer> decoded = referenceTable(fiveB);
                ArrayList<Integer> mergeBits = merge(decoded);
                sendMessageBack(s, mergeBits);
            } else {
				System.out.println("Something goofed, exiting program");
				System.exit(0);
			}
            String message = (serverCheck(s)) ? "Response good." : "Bad response.";
            System.out.println(message);
            s.close(); // NEVER FORGET
            System.out.println("Disconnected from server"); // ...and call it a day

        } catch (Exception e) { e.printStackTrace(); }
	 }
	 
	/*
	 * This method will read the response from the server to test
	 * if the response was correct or not
	 */
    public static boolean serverCheck(Socket s) {
        try {
            InputStream isStr = s.getInputStream();
            int successCode = isStr.read();
			// "If the boolean equals 1, return true for the method, else false"
            return (successCode == 1) ? true : false;
        } catch (Exception e) { }
		// This is just to satisfy Java coding conventions for methods
        return false;
    }
	
	/*
	 * This method utilizes the preamble to find the check if the signal is
	 * high or low
	 */
    public static String isHighOrLow(Socket s) {
        try {
            InputStream isStr = s.getInputStream();
            double preamble = 0.0;
            for(int i = 0; i < 64; ++i) {
                preamble += isStr.read();
            }
			// 64 bits for preamble
            BASE = preamble / 64;
            DecimalFormat dec = new DecimalFormat("#.##");
           return "Baseline established from preamble: " + dec.format(BASE);
        } catch (Exception e) { e.printStackTrace(); }
		return "Could not create base line";
	}
	
	/*
	 * This method will reference every 5 bits f the decoded message to determine 
	 * real values
	 */
    public static ArrayList<Integer> referenceTable(ArrayList<Integer> sig) {

        refTable table = new refTable();
        ArrayList<Integer> decodedMessage = new ArrayList<>();

        for(int i = 0; i < sig.size() - 4; i += 5) {
            ArrayList<Integer> sub = new ArrayList<>();
            sub.add(sig.get(i));     
			sub.add(sig.get(i + 1));
            sub.add(sig.get(i + 2)); 
			sub.add(sig.get(i + 3));
            sub.add(sig.get(i + 4));
            decodedMessage.add(table.get_5_bit_code(sub));
        }
        return decodedMessage;
    }
	
	/*
	 * This method will take the bytes from the server and convert them
	 * so they can be used for NRZI
	 */
    public static ArrayList<Integer> getBytes(Socket s) {
        try {
            InputStream isStr = s.getInputStream();

            ArrayList<Integer> sig = new ArrayList<>();

            for(int i = 0; i < 320; ++i) {
                int input = isStr.read();
                sig.add(checkSignal(input));
            }
            return sig;
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }
	
	/*
	 * This method will combine the lower and upper bits so that the
	 * right binary value is returned into the ArrayList
	 */
    public static ArrayList<Integer> merge(ArrayList<Integer> bits) {
        ArrayList<Integer> merged = new ArrayList<>();
        for(int i = 0; i < bits.size(); i += 2) {
            int upper = bits.get(i);
            int lower = bits.get(i + 1);
            upper = (16 * upper) + lower;
            merged.add(upper);
        }
        return merged;
    }

	/*
	 * This method will let the user see what they're sending, then 
	 * give the server the message as an array of bytes
	 */
    public static void sendMessageBack(Socket s, ArrayList<Integer> message) {
        try {
            int size = message.size();
            OutputStream outStr = s.getOutputStream();
            byte[] send = new byte[message.size()];

            System.out.print("Received bytes: ");
            for(int i = 0; i < size; ++i) {
                int current = message.get(i);
                System.out.print(Integer.toHexString(current).toUpperCase());
                send[i] = (byte) current;
            }
            System.out.println();
            outStr.write(send);
        } catch (Exception e) { e.printStackTrace(); }
    }
	
	/*
	 * This method will decode the NRZI signal
	 */
    public static ArrayList<Integer> nrziEncoding(ArrayList<Integer> ogSignal) {
        ArrayList<Integer> decodedSignal = new ArrayList<>();
        int lastBit = 0;
        for(int i = 0; i < ogSignal.size(); ++i) {
            if(ogSignal.get(i) == lastBit){
                decodedSignal.add(0);
            } else{
                decodedSignal.add(1);
            }
            lastBit = ogSignal.get(i);
        }
        return decodedSignal;
    }

	/*
	 * This method will determine if a signal is 1 or 0
	 */
    public static int checkSignal(int check) {
        return  (check > BASE) ? 1 : 0;
    }
	
	/*
	 * This helper class will essentially be the reference table 
	 * so the bits can be converted into a string and become a key
	 * that basically hashes to the right value
	 */
    static final class refTable {
		// This variable will eventually contain the table for reference
		// and conversion
        HashMap<String, String> table;

		/*
		 * This default constructor instantiates a new HashMap so values
		 * from 4B can be assigned to the correct 5B entry
		 */
        public refTable() {
            table = new HashMap<String, String>();
            fillMap();
        }

		// Jesus this took forever: the reference table from the textbook hardcoded
		// 4B on the right, and 5B on the left
        private void fillMap() {
            table.put("11110", "0000"); table.put("10010", "1000");
            table.put("01001", "0001"); table.put("10011", "1001");
            table.put("10100", "0010"); table.put("10110", "1010");
            table.put("10101", "0011"); table.put("10111", "1011");
            table.put("01010", "0100"); table.put("11010", "1100");
            table.put("01110", "0110"); table.put("11100", "1110");
            table.put("01011", "0101"); table.put("11011", "1101");
            table.put("01111", "0111"); table.put("11101", "1111");
        }
		
		// This simply converts the 5B 
        public int get_5_bit_code(ArrayList<Integer> signal) {
            String key = convertToString(signal);
            return Integer.parseInt(table.get(key), 2);
        }

        // Converts the signal to a String to be used as the Key
        private String convertToString(ArrayList<Integer> sig) {
            String res = "";
            for(Integer i : sig) {
                res += i + "";
            }
            return res;
        }
    }
	 
 }