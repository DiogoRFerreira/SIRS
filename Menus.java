import java.util.Scanner;

public class Menus {
	
	private static Scanner scanner=null;
	
	public static void CreateScanner(){
		Menus.scanner=new Scanner(System.in);
	}
	
	public static void DestroyScanner(){
		Menus.scanner.close();
	}
	
	//LOGIN DE UTILIZADOR
	
	public static String MenuPrincipal() throws Exception{
		int escolha;
		Scanner sc=null;
		System.out.println("Menu\n1.Login");
		
		sc=Menus.scanner;
		escolha = sc.nextInt();
		//escolha=1;
		String username = null;
		String password = null;
			
		if(escolha == 1){
			System.out.println("\nUsername:");
			username = sc.next(); 
			//username="root";
			System.out.println(username);
			System.out.println("\nPassword:");
			password = sc.next(); 
			//password="password";
			System.out.println(password);
		}	
		 String Login = username + "," + password;
			 
	return Login;
	}	
	
	//MENU ACESSO À CASA
		
	public static String MenuCasa() throws Exception{
		int escolha; 
		String commando="out";
		Scanner sc2=null;
		System.out.println("Menu\n1.Entrada\n2.Sala\n3.Cozinha\n4.Sair\n\n");
		
		sc2=Menus.scanner;
		
		escolha = sc2.nextInt();
		
		do {
			if(escolha==1){
				System.out.println("Porta 1.Abrir\n2.Fechar\n3.Sair\n");
				int comandoss = sc2.nextInt();
				switch(comandoss){
					case 1:
						commando="um;Porta,on";
						break;
					case 2:
						commando="um;Porta,off";
						break;
					case 3:
						commando="um;out";
						break;	
				}
				return commando;
					
			}else if(escolha==2){
		       	System.out.println("Televisão 1.Ligar\n2.Desligar\n3.Sair\n");
		       	int comandoss = sc2.nextInt();
				switch(comandoss){
					case 1:
						commando="um;TV,on";
						break;
					case 2:
						commando="um;TV,off";
						break;
					case 3:
						commando="um;out";
						break;	
				}
				return commando;
			}else if(escolha==3){
		       	System.out.println("Forno 1.Ligar\n2.Desligar\n3.Sair\n");
		       	int comandoss = sc2.nextInt();
				switch(comandoss){
					case 1:
						commando="um;Forno,on";
						break;
					case 2:
						commando="um;Forno,off";
						break;
					case 3:
						commando="um;out";
						break;	
				}
				return commando;
			}else if(escolha==4){	
		       	commando="zero;end";
		       	return commando;
			}
		}while(escolha !=4);
		
		//commando="um;Porta,on";
    return commando; 
	}
}
