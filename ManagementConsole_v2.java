import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class ManagementConsole_v2 {
	private static int randomnumberfreshness;
	
	public static void Publickeysexchange() throws IOException, ClassNotFoundException{

        	ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());    
        	ObjectInputStream iss = new ObjectInputStream(clientSocket.getInputStream());
        	 
        	// Receives public key from the external user
        	BigInteger n = (BigInteger) iss.readObject();
            BigInteger e = (BigInteger) iss.readObject();
            System.out.println("Public keys recebidas:\nn:"+n+"\ne:"+e);
            //InicializarHashMap(e,n);
            
            // Sends public key from the management console 
            oos.writeObject(rsa.n);
            oos.flush();
            oos.writeObject(rsa.e);
            oos.flush();
            
            /*******************************************/
            File file = new File("Logins.txt");
	        int count = 1;
	        String returnmessage;
	    	
	        do{
	            //Receives login data
	            
	            BigInteger userpass = (BigInteger) iss.readObject();
	            userpass = rsa.decrypt(userpass);
	            System.out.println(userpass);

	            String text2 = new String(userpass.toByteArray());
	            System.out.println("Message received from client is "+ text2);
	            
	            //Aplicar salt ao userpass
	            String passwordToHash = text2.trim();
	            String securePassword = SHA.get_SHA_1_SecurePassword(passwordToHash, ManagementConsole_v2.salt);
	            System.out.println(securePassword);
	        
	            //Comparar
	            Scanner scanner = new Scanner(file);

	            while(scanner.hasNextLine()){
	            	String abc=scanner.nextLine().trim();
	            	System.out.println(abc);
	              if(securePassword.trim().equals(abc)){
	                System.out.println("Found String\n");
	                count = 0;
	                break;
	              }else{}
	            }
	            if(count == 1){
	                System.out.println("String is not in Logins.txt\n");
	                returnmessage="Try again";//Rejeitado
	            }else{
	            	
	            	randomnumberfreshness=generate_random();
	                returnmessage=Integer.toString(randomnumberfreshness);
	                //flag=true;//Aceite
	            }
	            scanner.close();
	            System.out.println("Mensagem não cifrada: "+ returnmessage);
	            
	            //Encrypt text with the private from the management
	            BigInteger plaintextreturnmessage = new BigInteger(returnmessage.getBytes());
	            BigInteger ciphertextreturnmessage = rsa.encrypt2(plaintextreturnmessage);
	            System.out.println("Ciphertext(Messagem de retorno): " + ciphertextreturnmessage);
	        	
	            //Encrypt text with the public from the external user
	            BigInteger ciphertextreturnmessage1 = rsa.encrypt(ciphertextreturnmessage, e, n);
	            System.out.println("Ciphertext(Messagem de retorno): " + ciphertextreturnmessage1);
	        	
	            //Sending the response back to the client.
	            oos.writeObject(ciphertextreturnmessage1);
	            oos.flush();
	
	        }while(returnmessage.equals("Try again"));
	            
	        //Receives comandos para casa
	   
	        String[] random_commands;
	        random_commands=new String[3];
	        random_commands[1]="not end";
	        do{
		        BigInteger commands = (BigInteger) iss.readObject();
		        commands = rsa.decrypt(commands);
		        System.out.println(commands);

		        String text = new String(commands.toByteArray());
		        System.out.println("Message received from client is "+ text);
	        	 //Desconcatena o que chega
	        	 random_commands =text.split(";");
		        //Desligar ligação
		         if(random_commands[1].trim().equals("end")){
		        	 returnmessage="OK";
			         //Encrypt text
			         BigInteger plaintextreturnmessage = new BigInteger(returnmessage.getBytes());
			         BigInteger ciphertextreturnmessage = rsa.encrypt(plaintextreturnmessage, e, n);
			         System.out.println("Ciphertext(Messagem de retorno): " + ciphertextreturnmessage);
			            	
			            //Sending the response back to the client.
			            oos.writeObject(ciphertextreturnmessage);
			            oos.flush();
		         }else{
		            //Comparar o randomnumberfrsshness
		        	 randomnumberfreshness=randomnumberfreshness+1;

		        	 count=Integer.parseInt(random_commands[0]);
		        	 if(count==randomnumberfreshness){
		        		 //Ligar aos Devices(Home)
		        		 returnmessage = DevicesConnection(random_commands[1]);
		        	 }else{
		        		 returnmessage ="bye bye...";
		        	 }
		            //Encrypt text
		            BigInteger plaintextreturnmessage = new BigInteger(returnmessage.getBytes());
		            BigInteger ciphertextreturnmessage = rsa.encrypt(plaintextreturnmessage, e, n);
		            System.out.println("Ciphertext(Messagem de retorno): " + ciphertextreturnmessage);
		            	
		            //Sending the response back to the client.
		            oos.writeObject(ciphertextreturnmessage);
		            oos.flush();

		         }
	        }while(!random_commands[1].trim().equals("end"));
}
	
	
	public static String DevicesConnection(String commands) throws IOException, ClassNotFoundException {
    	String hostName="localhost";
    	
        try (
            Socket echoSocket = new Socket(hostName/*Hostname*/,6666);
        	ObjectOutputStream oos = new ObjectOutputStream(echoSocket.getOutputStream());		             
        	ObjectInputStream iss = new ObjectInputStream(echoSocket.getInputStream());	          	
        ){
        	
             /*Sends public key to the home*/
             oos.writeObject(rsa.n);
             oos.flush();
    	     oos.writeObject(rsa.e);
    	     oos.flush();
    	     
    	     /*Receives public key from the home */
     	    BigInteger n_b = (BigInteger) iss.readObject();       
            BigInteger e_b = (BigInteger) iss.readObject();
            
            /* Encrypt text with mc private key*/
            BigInteger plaintextlogin = new BigInteger(commands.getBytes());	           
            BigInteger ciphertextlogin = rsa.encrypt2(plaintextlogin);
         	System.out.println("Ciphertext(login): " + ciphertextlogin);
            
         	/* Encrypt text with home public key*/
            BigInteger ciphertextlogin2 = rsa.encrypt(ciphertextlogin, e_b, n_b);
         	System.out.println("Ciphertext(login): " + ciphertextlogin2);
         	            
         	/* Sends the text */
         	oos.writeObject(ciphertextlogin2);
         	oos.flush();
         	System.out.println("Message sent to the managementconsole : " + commands);
         	
    	     
         	//Get the return message from the server
	        
        	BigInteger returnmessage = (BigInteger) iss.readObject();
        	returnmessage = rsa.decrypt(returnmessage);
        	System.out.println(returnmessage);

            String message = new String(returnmessage.toByteArray());
            System.out.println("Message received from managementconsole "+ message);
            
            return message;
        	
        }catch (UnknownHostException e) {
            System.err.println("Don't know about host " + "localhost"/*Hostname*/);
            System.exit(1);
        }catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
            		"localhost"/*Hostname*/);
            System.exit(1);
        }
		return hostName;
    }	
	/**
	 * @throws IOException 
	 * @throws ClassNotFoundException ************************************************/
	
	public static void Registeruser() throws IOException, ClassNotFoundException{
    	ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());    
    	ObjectInputStream iss = new ObjectInputStream(clientSocket.getInputStream());
    	 
    	// Receives public key from the internal user
    	BigInteger n = (BigInteger) iss.readObject();
        BigInteger e = (BigInteger) iss.readObject();
        System.out.println("Public keys recebidas:\nn:"+n+"\ne:"+e);
        
        // Sends public key from the management console 
        oos.writeObject(rsa.n);
        oos.flush();
        oos.writeObject(rsa.e);
        oos.flush();
		
        //Receives login data and new users
        BigInteger userpass = (BigInteger) iss.readObject();
        userpass = rsa.decrypt(userpass);
        System.out.println(userpass);
        String text2 = new String(userpass.toByteArray());
        System.out.println("Message received from client is "+ text2);
        
        String[]login_Newusers;
        login_Newusers=text2.split(";");
        //no 0 envia login no 1 envia novos users
        
        //Aplicar salt ao userpass
        String passwordToHash =  login_Newusers[0].trim();
        String securePassword = SHA.get_SHA_1_SecurePassword(passwordToHash, ManagementConsole_v2.salt);
        System.out.println(securePassword);
    
        //Comparar
        File file = new File("Logins.txt");
        int count = 1;
        
        Scanner scanner = new Scanner(file);
        while(scanner.hasNextLine()){
        	String abc=scanner.nextLine().trim();
        	System.out.println(abc);
          if(securePassword.trim().equals(abc)){
            System.out.println("Found String\n");
            count = 0;
            break;
          }
        }
          String returnmessage;
        //Count 0 existe 
        if(count == 1){
            System.out.println("String is not in Logins.txt\n");
            returnmessage="Try again";
            //enviar a dizer que não deu
            returnmessage="Wrong Login";
        }else{
            returnmessage="Accepted";
            
            //Insert new user in the text file
            passwordToHash =  login_Newusers[1].trim();
            securePassword = SHA.get_SHA_1_SecurePassword(passwordToHash, ManagementConsole_v2.salt);          
            Writer output;
            output = new BufferedWriter(new FileWriter("Logins.txt",true));
            output.append(securePassword);
            output.close();
            
            //enviar a dizer que deu
            returnmessage="Inserted new user!";
        }
        scanner.close();
        
        //Encrypt text
		BigInteger plaintextreturnmessage = new BigInteger(returnmessage.getBytes());
        BigInteger ciphertextreturnmessage = rsa.encrypt(plaintextreturnmessage, e, n);
        System.out.println("Ciphertext(Messagem de retorno): " + ciphertextreturnmessage);
        	
        //Sending the response back to the client.
        oos.writeObject(ciphertextreturnmessage);
        oos.flush();	
	}
	
	/**************************************************/		    	
	//HashMap
	private static HashMap<Integer, BigInteger> PublicManagment;
		
	public static void InicializarHashMap(BigInteger e,BigInteger n){
		PublicManagment = new HashMap<>();
		PublicManagment.put(1, n);
		PublicManagment.put(2, e);
	}
	public static BigInteger getValue(int key){
		BigInteger value = PublicManagment.get(key);
		return value;
	}
	//RandomNumber
	
	public static int generate_random(){
		int max=9999;
		int min=1000;
		Random rand = new Random();
		int randomnumberfreshness = rand.nextInt(max - min + 1) + min;
	return randomnumberfreshness;
	}
	    
	//SHA
	public static String salt;
	
	public static void Inicializar_salt() throws NoSuchAlgorithmException{
		salt = SHA.getSalt();
	}
	
	//RSA Algorithm
	private static RSA rsa;
	
	public static void Inicializersa(){
		rsa = new RSA(1024);
	}
	private static ServerSocket serverSocket;
	private static Socket clientSocket;
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
		//Login File Text
		try {
			Inicializar_salt();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String passwordToHash = "root,password";
		String securePassword = SHA.get_SHA_1_SecurePassword(passwordToHash, ManagementConsole_v2.salt);
		Writer writer = null;
		try {
		    writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("Logins.txt"), "utf-8"));
		    writer.write(securePassword.trim());
		} catch (IOException ex) {
		  // report
		} finally {
		   try {writer.close();} catch (Exception ex) {/*ignore*/}
		}
		
		//InicializarPublickey_privatekey
		Inicializersa();
		
		new Thread(MAin).start();
		new Thread(Int).start();
	}	
	
	/***************************************/
	//Receives from the client
	private static Runnable MAin = new Runnable(){
		public void run(){
			
			//ServerSocket Port 2222
			try {
				serverSocket = new ServerSocket(2222);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//Accept Clients Port 2222
			while(true){
				try {
					clientSocket = serverSocket.accept();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				new Thread(ek).start();
			}
		}
	};

	

	private static Runnable ek = new Runnable(){
		public void run(){
			//Escrever as publickeys num ficheiro
			try {
				Publickeysexchange();
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	/**********************************************/
	//Register new users from internal
	private static Runnable Int = new Runnable(){
		public void run(){
			//ServerSocket Port 2223
			try {
				serverSocket = new ServerSocket(2223);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//Accept Clients Port 2222
			while(true){
				try {
					clientSocket = serverSocket.accept();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				new Thread(registusers).start();
			}
		}
	};	
	
	private static Runnable registusers = new Runnable(){
		public void run(){
			//Escrever as publickeys num ficheiro
			try {
				Registeruser();
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	/**************************************************/
}


