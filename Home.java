import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;


public class Home {
	
	public static void ReceiveCommands(int portNumber) throws IOException, ClassNotFoundException {

	    String returnmessage = null;
	    
	    try (
	    		ServerSocket serverSocket = new ServerSocket(portNumber);
	    		Socket clientSocket = serverSocket.accept();    
	        	ObjectInputStream iss = new ObjectInputStream(clientSocket.getInputStream());    
	    		ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
		         ) {
	        	 //Server is running always. This is done using this while(true) loop ---apaguei o loop ,realizado na managementconsole

	        	//RSA rsa = new RSA(1024);
	            
	        	/* Receives public key from the management user */
	           
	       		BigInteger n = (BigInteger) iss.readObject();
	       		BigInteger e = (BigInteger) iss.readObject();
	        		
	       		/* Envia public key da casa */
	            
	            oos.writeObject(rsa.n);
	            oos.flush();
	            oos.writeObject(rsa.e);
	            oos.flush();
	        		
	            //Receives comandos from management
	            //Decrypt with private key from Home
	            BigInteger commands = (BigInteger) iss.readObject();
	            commands = rsa.decrypt(commands);
	            System.out.println(commands);
	            //Decrypt with public key
	            commands = rsa.decrypt2(commands,e,n);
	            System.out.println(commands);
	            String text2 = new String(commands.toByteArray());
	            System.out.println("Message received from client is "+ text2);
	                	        		        	
	        		
	            //Comparações
	            //ESCREVER NOS FICHEIROS
	            /* Separar string */
	            int count=0;
	            String[] split = text2.split(",");
	          
	            File file = new File(split[0] + ".txt");
	          
	            /* Scanner para ler o ficheiro */
	            Scanner scanner = new Scanner(file);
	            
	            while(scanner.hasNextLine()){
	            	System.out.println("Feito");
	            	if(split[1].equals(scanner.nextLine().trim())){
	            	  /* A string split[2] encontra-se no ficheiro */
	            	  count = 1;
	            	  break;
	              }
	            }
	            scanner.close();
	            if(count == 0){
	          	  PrintWriter writer = new PrintWriter(file, "UTF-8");
	          	  writer.println(split[1]);
	          	  writer.close();
	            }
	            
	            //Mensagem de retorno
	            returnmessage="forno,foi ligado";

	            //Encrypt text 
	            BigInteger plaintextreturnmessage = new BigInteger(returnmessage.getBytes());
	            BigInteger ciphertextreturnmessage = rsa.encrypt(plaintextreturnmessage, e, n);
	            System.out.println("Ciphertext(Messagem de retorno): " + ciphertextreturnmessage);
	            	
	            //Sending the response back to the client.
	            oos.writeObject(ciphertextreturnmessage);
	            oos.flush();
	                
	                
	        } catch (IOException e) {
	            System.out.println("Exception caught when trying to listen on port "
	                + portNumber + " or listening for a connection");
	            System.out.println(e.getMessage());
	        }
	    }
	//RSA Algorithm
		private static RSA rsa;
		
		public static void Inicializersa(){
			rsa = new RSA(1024);
		}
		
	//MAIN
	public static void main(String [] args)throws IOException, ClassNotFoundException{
		
		
		Inicializersa();
		new Thread (manag).start();
		new Thread (inter).start();
	}
	
	private static Runnable manag = new Runnable(){
		public void run(){
			while(true){
				try {
					ReceiveCommands(6666);
				} catch (ClassNotFoundException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}	
		}
	};
	
	private static Runnable inter = new Runnable(){
		public void run(){
			while(true){
				try {
					ReceiveCommands(6667);
				} catch (ClassNotFoundException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}	
		}
	};
	
	
}
