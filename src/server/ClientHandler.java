package server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import client.User;

public class ClientHandler extends Thread{

	BufferedReader clientInput = null;
	PrintStream clientOutput = null;
	Socket communicationSocket = null;
	User user = new User();;
	
	public ClientHandler(Socket communicationSocket) {
		this.communicationSocket = communicationSocket;
	}
	
	@Override
	public void run() {
		
		try {
			clientInput = new BufferedReader(
					new InputStreamReader(communicationSocket.getInputStream()));
			clientOutput = new PrintStream(communicationSocket.getOutputStream());
			
			printFirstMenu();
			
			if(Server.activeUsers.contains(user)) {
				
				boolean signal = true;
				
				while(signal) {
					signal = printQuizMenu();
					
					if(signal == false) {
						exit();
						break;
					}
				}
				
			}else {
				clientOutput.println(">>>Error, user has not beed added to the appropriate list successfully!");
			}
			
			
		} catch (IOException e) {
			clientOutput.println(">>>Error in the run method of Client Handler!");
			Server.activeUsers.remove(user);
			Server.users.remove(this);
		}

	}
	
	
	public void printFirstMenu() {
		
		clientOutput.println(">>>Welcome :)");
		clientOutput.println(">>>Please enter the number: ");
		clientOutput.println("1 - Login");
		clientOutput.println("2 - Exit");
		
		try {
			String index = clientInput.readLine();
			
			if(index != null) {
				
				switch(index) {
					case "1": {
						login();
						break;
					}
					case "2": {
						exit();
						break;
					}
					default:  {
						clientOutput.println(">>>Error, you entered the inappropriate value!");
					}
				}
				
			}else {
				Server.users.remove(this);
				clientOutput.println(">>>Goodbye!");
				communicationSocket.close();
			}
			
		} catch (IOException e) {
			clientOutput.println(">>>Error while running the first menu!");
		}
	}
	
	public boolean printQuizMenu() {
		
		clientOutput.println(">>>Please enter the number: ");
		clientOutput.println("1 - Take the quiz");
		clientOutput.println("2 - Exit");
		
		boolean signal = false;
		
		try {
			String index = clientInput.readLine();
			
			if(index != null) {
				
				switch(index) {
					case "1": {
						signal = true;
						playQuiz();
						break;
					}
					case "2": {
						signal = false;
						exit();
						break;
					}
					default:  {
						clientOutput.println(">>>Error, you entered the inappropriate value!");
						signal = false;
					}
				}
				
			}else {
				Server.users.remove(this);
				clientOutput.println(">>>Goodbye!");
				communicationSocket.close();
			}
			
		} catch (SocketException e) {
			System.out.println(">>>Client got disconnected!");
		} catch (IOException e) {
			clientOutput.println(">>>Error in the quiz menu!");
		} 
		return signal;
	}
	
	public void exit() {
		
		try {
			clientOutput.println(">>>Please write 'exit': ");
			String message = clientInput.readLine();
			
			if(message != null && message.equals("exit")) {
				Server.activeUsers.remove(user);
				Server.users.remove(this);
				clientOutput.println(">>>Goodbye!");
				communicationSocket.close();
			}
			
		}catch (SocketException e) {
			clientOutput.println(">>>Error: Communication socket has not been closed properly!");
		} catch (IOException e) {
			clientOutput.println(">>>Error while reading the client input in exit function!");
		}
		
	}
	
	public void login() {
		
		try {
			String username;
			boolean valid = false;
			clientInput = new BufferedReader(
					new InputStreamReader(communicationSocket.getInputStream()));
			clientOutput = new PrintStream(communicationSocket.getOutputStream());
			
			do {
				clientOutput.println(">>>Please enter the username: ");
				username = clientInput.readLine();
				
				if(username != null && username != "" && !username.contains(" ")) {
					valid = true;
					user.setUsername(username);
					user.setScore(0);
					Server.activeUsers.add(user);
					clientOutput.println(">>>Welcome " + username + " :)");
				}else {
					clientOutput.println(">>>Username cannot contain blank spaces and cannot be an empty string!");
				}
				
			}while(valid == false);
			
		}catch(Exception e) {
			clientOutput.println(">>>Error happened during the login process!");
		}
	}
	
	
	public LinkedList<Question> readQuestionsFromFile(){
		
		LinkedList<Question> listOfQuestions = new LinkedList<Question>();
		String filePath = "/Users/jovana_segrt/eclipse-workspace-new/rmt_quiz/questions/quiz_questions.txt";
		
		try {
			BufferedReader in = new BufferedReader(new FileReader(filePath));
			boolean end = false;
			String textLine;
			
			while(!end) {
				textLine = in.readLine();
				
				if(textLine == null) {
					end = true;
				}else {
					String[] arrayQuestion = textLine.split("  ");
					listOfQuestions.add(new Question(arrayQuestion[0], arrayQuestion[1]));
				}
			}
			in.close();
		} catch (FileNotFoundException e) {
			clientOutput.println(">>>Error: File has not been found!");
		} catch (IOException e) {
			clientOutput.println(">>>Error while reading the file!");
		}
		
		return listOfQuestions;
	}
	
	
	public void printCurrentStatus() {
		
		Collections.sort(Server.activeUsers, new Comparator<User>() {
			@Override
			public int compare(User u1, User u2) {
				return u2.getScore() - u1.getScore();
			}
		});
		
		for(User u : Server.activeUsers) {
			clientOutput.println(u.getUsername() + " " + u.getScore());
		}
	}
	
	
	public void playQuiz() {
		
		LinkedList<Question> questions = readQuestionsFromFile();
		
		for (Question q : questions) {
			try {
				clientOutput.println();
				clientOutput.println(q.getQuestion());
				String answer = clientInput.readLine();
				
				if(answer.equals(q.getAnswer())) {
					clientOutput.println(">>>Your answer was correct!");
					user.setScore(user.getScore() + 1);
				}else {
					clientOutput.println(">>>Your answer was incorrect! Correct answer is " + q.getAnswer() + ".");
				}
				
				clientOutput.println();
				clientOutput.println(">>>Current status of players that are online: ");
				
				printCurrentStatus();
				
			} catch (IOException e) {
				clientOutput.println(">>>Error while reading the question!");
			}
		}
		clientOutput.println();
		clientOutput.println(">>>You have compleated the quiz successfully!");
		clientOutput.println(">>>Your final score is: " + user.getScore());
		clientOutput.println();	
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
