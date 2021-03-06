package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;

import client.User;

public class Server {

	static LinkedList<ClientHandler> users = new LinkedList<ClientHandler>();
	static LinkedList<User> activeUsers = new LinkedList<User>();
	
	public static void main(String[] args) {
		
		int port = 9000;
		ServerSocket serverSocket = null;
		Socket communicationSocket = null;
		
		try {
			serverSocket = new ServerSocket(port);
			
			while(true) {
				System.out.println("Waiting for connection...");
				communicationSocket = serverSocket.accept();
				System.out.println("Connection has been made...");
				ClientHandler client = new ClientHandler(communicationSocket);
				users.add(client);
				client.start();
			}
			
		} catch(SocketException e) {
			System.out.println("Client got disconnected!");
		}catch (IOException e) {
			System.out.println("Error with running the server!");
		}
		
		
		
		
		
		
		
		
	}
	
	
	
	
	
	
	
	
}
