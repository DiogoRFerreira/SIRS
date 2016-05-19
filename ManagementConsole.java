import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
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


public class ManagementConsole {
	
	
	//SOCKET TO GET THE PUBLIC KEY AND SEND FROM EXT OR INT
	
	public static void Publickeysexchange() throws IOException, ClassNotFoundException{
        try (
        	ServerSocket serverSocket = new ServerSocket(2222);
	        Socket clientSocket = serverSocket.accept();    
	      	 ) {
        	ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());    
             
        	// Receives public key from the external user
        	ObjectInputStream iss = new ObjectInputStream(clientSocket.getInputStream());
        	BigInteger n = (BigInteger) iss.readObject();
            BigInteger e = (BigInteger) iss.readObject();
            System.out.println("Public keys recebidas:\nn:"+n+"\ne:"+e);
            //InicializarHashMap(e,n);
            
            // Sends public key from the management console 
            //ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());    
            oos.writeObject(rsa.n);
            oos.flush();
            oos.writeObject(rsa.e);
            oos.flush();
        }
	}
	
	
	//LOGIN AUTENTICATION
	
	public static boolean ConnectionServer() throws IOException, ClassNotFoundException {
	    	int portNumber = 4444;
	        File file = new File("Logins.txt");
	        int count = 1;
	        boolean flag=false;
	        String returnmessage;
	    	
	        
	        try (
	            ServerSocket serverSocket = new ServerSocket(portNumber);
	            Socket clientSocket = serverSocket.accept();    
	        	ObjectInputStream iss = new ObjectInputStream(clientSocket.getInputStream());
	        	ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());  
	        	) {
	     
	            
	            // Receives public key from the external user 
	            
	            BigInteger n = (BigInteger) iss.readObject();
	            BigInteger e = (BigInteger) iss.readObject();
	            
	            // Envia public key do servidor 
	           /*ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
	     
	            oos.writeObject(rsa.n);
	            oos.flush();
	            oos.writeObject(rsa.e);
	            oos.flush();
	       */
	            //Receives login data
	            
	            BigInteger userpass = (BigInteger) iss.readObject();
	            userpass = rsa.decrypt(userpass);
	            System.out.println(userpass);

	            String text2 = new String(userpass.toByteArray());
	            System.out.println("Message received from client is "+ text2);
	            
	            //Aplicar salt ao userpass
	            String passwordToHash = text2;
	            String securePassword = SHA.get_SHA_1_SecurePassword(passwordToHash, ManagementConsole.salt);
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
	            	generate_random();
	                returnmessage="5555"+";"+ randomnumberfreshness;
	                flag=true;//Aceite
	            }
	            scanner.close();
	            System.out.println("Mensagem não cifrada: "+ returnmessage);
	            
	            //Encrypt text
	            BigInteger plaintextreturnmessage = new BigInteger(returnmessage.getBytes());
	            BigInteger ciphertextreturnmessage = rsa.encrypt(plaintextreturnmessage, e, n);
	            System.out.println("Ciphertext(Messagem de retorno): " + ciphertextreturnmessage);
	        	
	            //Sending the response back to the client.
	            oos.writeObject(ciphertextreturnmessage);
	            oos.flush();
	           
	            return flag;
	        	
	        } catch (IOException e) {
	            System.out.println("Exception caught when trying to listen on port "
	                + portNumber + " or listening for a connection");
	            System.out.println(e.getMessage());
	        }
			return flag;
	    }
	
	 //COMMANDS TO THE HOME
	 
	 public static boolean ConnectionServer2() throws IOException, ClassNotFoundException {
	    	int portNumber = 5555;
	        boolean flag=false;
	        String returnmessage;
	    	
	        try (
	            ServerSocket serverSocket = new ServerSocket(portNumber);
	            Socket clientSocket = serverSocket.accept();    
	        	ObjectInputStream iss = new ObjectInputStream(clientSocket.getInputStream());    
	        	ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());  
	        	) {
	        	 //Server is running always. This is done using this while(true) loop ---apaguei o loop ,realizado na managementconsole
	        	      	
	            
	            //Receives public key from the external user 
	            
	            BigInteger n = (BigInteger) iss.readObject();
	            BigInteger e = (BigInteger) iss.readObject();
	            
	            /*
	            //Envia public key do servidor 
	            ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
	     
	            oos.writeObject(rsa.n);
	            oos.flush();
	            oos.writeObject(rsa.e);
	            oos.flush();
	        	*/
	            //Receives comandos para casa
	            
	            BigInteger commands = (BigInteger) iss.readObject();
	            commands = rsa.decrypt(commands);
	            System.out.println(commands);

	            String text2 = new String(commands.toByteArray());
	            System.out.println("Message received from client is "+ text2);
	            
	            //Desligar ligação
	            if(text2.equals("end")){
	               	flag=true;
	               	return flag;
	            }
	                
	            //Ligar aos Devices
	            returnmessage = DevicesConnection(text2);
	                
	            //Encrypt text
	            BigInteger plaintextreturnmessage = new BigInteger(returnmessage.getBytes());
	            BigInteger ciphertextreturnmessage = rsa.encrypt(plaintextreturnmessage, e, n);
	            System.out.println("Ciphertext(Messagem de retorno): " + ciphertextreturnmessage);
	            	
	            //Sending the response back to the client.
	            oos.writeObject(ciphertextreturnmessage);
	            oos.flush();
	  
	            return flag;
	        	
	        } catch (IOException e) {
	            System.out.println("Exception caught when trying to listen on port "
	                + portNumber + " or listening for a connection");
	            System.out.println(e.getMessage());
	        }
			return flag;
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

	            /* Encrypt text */
	            BigInteger plaintextlogin = new BigInteger(commands.getBytes());
	            BigInteger ciphertextlogin = rsa.encrypt(plaintextlogin, e_b, n_b);
	         	System.out.println("Ciphertext(login): " + ciphertextlogin);
	         	            
	         	/* Sends the text */
	         	oos.writeObject(ciphertextlogin);
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
	    
	//Enter new Clients 
	    
	//Time Stamp
	//TimeStamp to String
	    /*
	public static void timstamp_to_string()   
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	String string  = dateFormat.format(new Date());
    System.out.println(string);
    */
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
	private static int randomnumberfreshness;
	
	public static void generate_random(){
		int max=9999;
		int min=1000;
		Random rand = new Random();
		randomnumberfreshness =rand.nextInt(max - min + 1) + min;
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
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {

		//Initialize logins file
		Inicializar_salt();
		String passwordToHash = "root,password";
		String securePassword = SHA.get_SHA_1_SecurePassword(passwordToHash, ManagementConsole.salt);
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
		//Publickeysexchange();
		new Thread(ek).start();
		new Thread(eu).start();
        new Thread(com).start();
		new Thread(ru).start();
	}
	
	//RECEIVE SOCKETS TO EXCHANGE KEYS
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
	
	//RECEIVE SOCKETS TO DO THE Login
	
	private static Runnable eu = new Runnable(){
		public void run(){
			boolean	logindone = false;

			
			while(true){
				//Login
				//Inicialize_publickeys();
				
				do{
					//Ler ficheiro!!
					try {
						logindone = ConnectionServer();
					} catch (ClassNotFoundException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//System.out.println(logindone);
				}while(logindone==false);
			}
		}
	};
				
	//RECEIVE SOCKETS FROM THE EXTERNAL USER or INTERNAL USER to execute commands to the home
				
	private static Runnable com = new Runnable(){
		public void run(){
			//Recebe Comandos
			boolean endofconnection = false;
			while(endofconnection==false){
					//endofconnection Recebe as opções e envia para os devices*/
					try {
						endofconnection = ConnectionServer2();
					} catch (ClassNotFoundException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		}
	};
		
	
	//RECEIVE SOCKETS FROM THE INTERNAL USER(Add new Users)
	
	private static Runnable ru = new Runnable(){
		public void run(){
			//
		}
	};
	
	
}
	
