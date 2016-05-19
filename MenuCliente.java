import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

public class MenuCliente {
	
	//SOCKET INICIAL SABER KEYS
	
	public static void Publickeysexchange() throws IOException, ClassNotFoundException{
		String hostName="localhost";
		int PortNumber=2222;
		try (	
	            Socket echoSocket = new Socket(hostName/*Hostname*/,PortNumber);
				){ 
			
			ObjectInputStream iss = new ObjectInputStream(echoSocket.getInputStream());    	
			ObjectOutputStream oos = new ObjectOutputStream(echoSocket.getOutputStream());
			
			//Sends public key to the management console
            oos.writeObject(rsa.n);
     	    oos.flush();
   	        oos.writeObject(rsa.e);
   	        oos.flush();
   	        
   	        //Receives public key from the management console
   	        BigInteger n = (BigInteger) iss.readObject();       	        
            BigInteger e = (BigInteger) iss.readObject();
            
            InicializarHashMap(e,n);
		}
	
	}
	//SOCKET LOGIN USER
	
    public static String ConnectionAutentication(String login) throws IOException {
    	String hostName="localhost";
    	int PortNumber=4444;
        try (
            Socket echoSocket = new Socket(hostName/*Hostname*/,PortNumber);
        	ObjectOutputStream oos = new ObjectOutputStream(echoSocket.getOutputStream());
        	ObjectInputStream iss = new ObjectInputStream(echoSocket.getInputStream());
     	){
            String Logindata = login + "\n";
            System.out.println("Login data to send:"+Logindata);
            
            //RSA rsa = new RSA(1024);
            
            //Sends public key to the management console
            oos.writeObject(rsa.n);
     	    oos.flush();
   	        oos.writeObject(rsa.e);
   	        oos.flush();
   	        /*	           
   	        //Receives public key from the management console 
   	        ObjectInputStream iss = new ObjectInputStream(echoSocket.getInputStream());
        	BigInteger n = (BigInteger) iss.readObject();
        	        
            BigInteger e = (BigInteger) iss.readObject();
        	*/
        	//Encrypt text                   
            BigInteger plaintextlogin = new BigInteger(Logindata.getBytes());
            BigInteger ciphertextlogin = rsa.encrypt(plaintextlogin, getValue(2),getValue(1));
        	System.out.println("Ciphertext(login): " + ciphertextlogin);
        	            
        	//Sends the text
        	oos.writeObject(ciphertextlogin);
        	oos.flush();
        	System.out.println("Message sent to the managementconsole : "+ Logindata);
        	 
        	//Get the return message from the server
        	        
        	BigInteger returnmessage = (BigInteger) iss.readObject();
        	System.out.println(returnmessage);
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
        } catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return hostName;
    }
    
    
    //SOCKET COMMANDS (MANAGEMENT CONSOLE)
    
    public static String Commands(String commands,int PortNumber,String Freshness) throws IOException, ClassNotFoundException {
    	String hostName="localhost";
        try (
            Socket echoSocket = new Socket(hostName/*Hostname*/,PortNumber);
        	ObjectOutputStream oos = new ObjectOutputStream(echoSocket.getOutputStream());
        	ObjectInputStream iss = new ObjectInputStream(echoSocket.getInputStream());
            ){

        	 //RSA rsa_b = new RSA(1024);
        	 
             //Sends public key to the management console
             oos.writeObject(rsa.n);
             oos.flush();
    	     oos.writeObject(rsa.e);
    	     oos.flush();
         	   /*        
    	    //Receives public key from the management console
    	    ObjectInputStream iss = new ObjectInputStream(echoSocket.getInputStream());
         	BigInteger n_b = (BigInteger) iss.readObject();       
            BigInteger e_b = (BigInteger) iss.readObject();
        	*/
          	// Encrypt text
            BigInteger plaintextlogin = new BigInteger(commands.getBytes());
            
            BigInteger ciphertextlogin = rsa.encrypt(plaintextlogin, getValue(2), getValue(1));
        	System.out.println("Ciphertext(login): " + ciphertextlogin);
        	            
        	// Sends the text
        	oos.writeObject(ciphertextlogin);
        	oos.flush();
        	System.out.println("Message sent to the managementconsole : " + commands);
        	
        	//End of the commands to the house
        	if(commands.equals("end")){
        		String retmessage="fim";
        		return retmessage;
        	}
        		
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
    
	//2ºLOOP
	private static String [] commands;
	
	public static void InicializarLoop(){
		MenuCliente.commands = new String[2];
		commands[0]="um";
		commands[1]="";

	}
	//RSA Algorithm
		private static RSA rsa;
		
		public static void Inicializersa(){
			rsa = new RSA(1024);
		}
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
	//MAIN
	
	public static void main(String[] args) throws Exception{
		String done = null;		
		System.out.println("Bem-vindo!");
		int i=1;
		String um="um";
		String out="out";
		
		Inicializersa();
		//TROCAR KEYS
		Publickeysexchange();
		//Autenticação
		Menus.CreateScanner();
		while(i != 0){
			String Login = Menus.MenuPrincipal();
			done = ConnectionAutentication(Login);
			System.out.println(done+"\n");
			if(done.equals("Try again")){i=1;}else{i=0;};
		}
		//Ligação ao outro porto , comando de on e off
		String[] port_freshness = done.split(";");  //desconcatena
		int port = Integer.parseInt(port_freshness[0]);
		
		InicializarLoop();
		while(commands[0].equals(um)){
			String Comandoesc=Menus.MenuCasa();
			commands=Comandoesc.split(";");
			if(!commands[1].equals(out)){
				Commands(commands[1], port ,port_freshness[1]);
			}
		}
		Menus.DestroyScanner();
		System.out.println("Bye...");
	}
}
