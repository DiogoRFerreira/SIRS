import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

public class Menuinternal {
	
	//SOCKET INICIAL SABER KEYS
	
	public static void Registuser(String Login_Newuser) throws IOException, ClassNotFoundException{

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
        
        //Encrypt text
        BigInteger plaintextlogin = new BigInteger(Login_Newuser.getBytes());
        BigInteger ciphertextlogin = rsa.encrypt(plaintextlogin,getValue(2),getValue(1));
    	System.out.println("Ciphertext(login): " + ciphertextlogin);
    	            
    	// Sends the text
    	oos.writeObject(ciphertextlogin);
    	oos.flush();
    	System.out.println("Message sent to the managementconsole : "+ Login_Newuser);
    	 
    	//Get the return message from the server
    	        
    	BigInteger returnmessage = (BigInteger) iss.readObject();
    	System.out.println(returnmessage);
    	returnmessage = rsa.decrypt(returnmessage);
    	System.out.println(returnmessage);

        String message = new String(returnmessage.toByteArray());
        System.out.println("Message received from managementconsole "+ message);
    	        
		}
	/*
	//LOGIN
	
	public static String ConnectionAutentication(String login) throws IOException {
    	String hostName="localhost";
    	
        try (
            Socket echoSocket = new Socket(hostName,4444);
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
        	     
   	        //Receives public key from the management console 
        	BigInteger n = (BigInteger) iss.readObject();        	        
            BigInteger e = (BigInteger) iss.readObject();
        	
        	//Encrypt text
            BigInteger plaintextlogin = new BigInteger(Logindata.getBytes());
            BigInteger ciphertextlogin = rsa.encrypt(plaintextlogin,getValue(2),getValue(1));
        	System.out.println("Ciphertext(login): " + ciphertextlogin);
        	            
        	// Sends the text
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
            System.err.println("Don't know about host " + "localhost");
            System.exit(1);
        }catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
            		"localhost");
            System.exit(1);
        } catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return hostName;
    }
	*/
	//DEVICES
	public static String DevicesConnection(String commands) throws IOException, ClassNotFoundException {
    	String hostName="localhost";
    	
        try (
            Socket echoSocket = new Socket(hostName/*Hostname*/,6667);
        ){
        	
        	 RSA rsa_b = new RSA(1024);
             /*Sends public key to the home*/
             ObjectOutputStream oos = new ObjectOutputStream(echoSocket.getOutputStream());
             oos.writeObject(rsa_b.n);
             oos.flush();
    	     oos.writeObject(rsa_b.e);
    	     oos.flush();
    	     
    	     /*Receives public key from the home */
     	    ObjectInputStream iss = new ObjectInputStream(echoSocket.getInputStream());
          	BigInteger n_b = (BigInteger) iss.readObject();       
            BigInteger e_b = (BigInteger) iss.readObject();

            /* Encrypt text */
            BigInteger plaintextlogin = new BigInteger(commands.getBytes());
            BigInteger ciphertextlogin = rsa_b.encrypt(plaintextlogin, e_b, n_b);
         	System.out.println("Ciphertext(login): " + ciphertextlogin);
         	            
         	/* Sends the text */
         	oos.writeObject(ciphertextlogin);
         	oos.flush();
         	System.out.println("Message sent to the managementconsole : " + commands);
         	
    	     
         	//Get the return message from the server
	        
        	BigInteger returnmessage = (BigInteger) iss.readObject();
        	returnmessage = rsa_b.decrypt(returnmessage);
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
			
	//RSA Algorithm
	private static RSA rsa;
			
	public static void Inicializersa(){
		rsa = new RSA(1024);
	}
	
	//EchoSocket	
	private static Socket echoSocket;
	
	//MAIN
	public static void main(String[] args) throws Exception{
		
		//String done = null;		
		System.out.println("Bem-vindo!");
		//int i=1;
		//String [] privileges=null;
		Inicializersa();
		Menus_internal.CreateScanner();
		String RegisterCommands = Menus_internal.MenuPrincipal2();
		String Login = Menus_internal.MenuPrincipal();
		
		String[]opcao=RegisterCommands.split(";");	
		//Commands to the home
		if(opcao[0].equals("0")){
			echoSocket = new Socket("localhost",6667);
			
		//Register users
		}else if(opcao[0].equals("1")){
			echoSocket = new Socket("localhost",2223);
			Registuser(Login);
			echoSocket = new Socket("localhost",6667);
			Registuser(Login);
		}

		
		//Authentication Internal User
		/*
		while(i != 0){
			String Login = Menus_internal.MenuPrincipal();
			done = ConnectionAutentication(Login);
			System.out.println(done+"\n");
			if(done.equals("Try again")){i=1;
			}else{i=0;
			privileges = done.split(";");
			}
		}*/
		/*
		if(privileges[0].equals("1")){//O que é que ele pode aceder
			String opcao=Menus_internal.MenuPrincipal2();
			String[] escolha=opcao.split(";");
			if(escolha[0].equals("0")){
					//Menus_internal.MenuCasa();
			}else if(escolha[0].equals("1")){
					//
			}
		}else if(privileges[0].equals("2")){
			String opcao=Menus_internal.MenuPrincipal3();
			String [] escolha =opcao.split(";");
		}	*/
	
		Menus_internal.DestroyScanner();
	}
		
}
