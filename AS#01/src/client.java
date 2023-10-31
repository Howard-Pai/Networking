import java.net.*;

import java.util.Scanner;

import java.io.*;
public class client {
	
	public static void main(String[] args) throws IOException{
		final String IP="127.0.0.1";
		final int PORT = 8888;
		Socket s = new Socket(IP,PORT);
		PrintWriter output = new PrintWriter(s.getOutputStream(),true);
		BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
		Scanner sc = new Scanner(System.in);
		String toServer= "";
		
		while(!"stop".equalsIgnoreCase(toServer)) {
			toServer= sc.nextLine();
			output.println(toServer);
			String responseFromServer= input.readLine();
			System.out.println("Response:"+responseFromServer);
			}
		input.close();
		output.close();
		sc.close();
		s.close();
		
		System.out.println("socket end");
	}
}
