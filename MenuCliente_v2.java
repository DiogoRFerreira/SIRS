import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.util.HashMap;

public class MenuCliente_v2 {
	
	//SOCKET
	
	public static void Publickeysexchange() throws Exception{
			
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
        String message="Try again";
        do{
			String Logindata = Menus.MenuPrincipal();
			
        	//Encrypt text                   
            BigInteger plaintextlogin = new BigInteger(Logindata.getBytes());
            BigInteger ciphertextlogin = rsa.encrypt(plaintextlogin, getValue(2),getValue(1));
        	System.out.println("Ciphertext(login): " + ciphertextlogin);
        	            
        	//Sends the text
        	oos.writeObject(ciphertextlogin);
        	oos.flush();
        	System.out.println("Message sent to the managementconsole : "+ Logindata);
        	 
        	//Get the return message from the server
        	//Decrypt with private from the client
        	BigInteger returnmessage = (BigInteger) iss.readObject();
        	System.out.println(returnmessage);
        	returnmessage = rsa.decrypt(returnmessage);
        	System.out.println(returnmessage);
        	
        	//Decrypt with the public from the management
        	returnmessage = rsa.decrypt2(returnmessage,getValue(2),getValue(1));
        	System.out.println(returnmessage);
        	
            message = new String(returnmessage.toByteArray());
            System.out.println("Message received from managementconsole "+ message);
        }while(message.trim().equals("Try again"));
        
            /******************************/
            
        int sequencenumber=Integer.parseInt(message);
            
        String commands="not end";
        do{
        	sequencenumber=sequencenumber+1;
        	String sequencenumberstring=Integer.toString(sequencenumber);
        	
        	String[] outornot;
        	outornot=new String[3];
        	outornot[1]="out";
        	while(outornot[1].equals("out")){
        		commands=Menus.MenuCasa();
       			outornot=commands.split(";");	
        	}
            String[] commandsforhome=commands.split(";");
            System.out.println(sequencenumberstring);
            System.out.println(commandsforhome[1]);
            String tosend=sequencenumberstring+";"+commandsforhome[1];
            
          	// Encrypt text
            BigInteger plaintextlogin = new BigInteger(tosend.getBytes());            
            BigInteger ciphertextlogin = rsa.encrypt(plaintextlogin, getValue(2), getValue(1));
        	System.out.println("Ciphertext(login): " + ciphertextlogin);
        	            
        	// Sends the text
        	oos.writeObject(ciphertextlogin);
        	oos.flush();
        	System.out.println("Message sent to the managementconsole : " + commands);
        	
        		
        	//Get the return message from the server	        
        	BigInteger returnmessage = (BigInteger) iss.readObject();
        	returnmessage = rsa.decrypt(returnmessage);
        	System.out.println(returnmessage);

            message = new String(returnmessage.toByteArray());
            System.out.println("Message received from managementconsole "+ message);
            
        }while(!commands.equals("zero;end"));
	}

	//RSA Algorithm
	private static RSA rsa;
		
	public static void InitializeRSA(){
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
	
	//EchoSocket	
	private static Socket echoSocket;
		
	//MAIN
	public static void main(String[] args) throws Exception{

		System.out.println("Bem-vindo!");
		InitializeRSA();
        echoSocket = new Socket("localhost",2222);
		Menus.CreateScanner();
		Publickeysexchange();
		Menus.DestroyScanner();
		System.out.println("Bye...");
	}
}