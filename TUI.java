import java.util.Scanner;
import java.util.*;
import java.io.*;
import java.time.*;

public final class TUI implements Serializable
{
   private static String cE; // código da Empresa atualmente em uso
   //private  Cliente actual_cliente;
   private static HashMap<String,Empresa> lE; //lista das Empresas registadas no sistema
   private static int nTUI=0;
   private static LocalDateTime data;
   private static String userName;
   private static String userEmail;
   
   private TUI(){
       this.cE = "";
       this.lE = new HashMap<String,Empresa>();
   }
   
   public TUI newTUI(){
       if(nTUI==0){
           this.nTUI++;
           return new TUI();
       }
       else{
           throw new AssertionError();
       }
   }
   
   private static void clearScreen(){System.out.print('\u000C');}
   
   /* Metodo que avança n minutos à nossa variavel de tempo
    * Para comparação entre as datas temos
    * isAfter(ChronoLocalDateTime<?> other)
    * Checks if this date-time is after the specified date-time.
    * isBefore(ChronoLocalDateTime<?> other)
    * Checks if this date-time is before the specified date-time.
    * until(Temporal endExclusive, TemporalUnit unit)
    * Calculates the amount of time until another date-time in terms of the specified unit.
    */
   private static void mais_minutos(long minutos){
       data.plusMinutes(minutos);
   }
   
   private static void sleep(){
       try {Thread.sleep(900);}
        catch (InterruptedException e) {
        e.printStackTrace();}
   }
   
   private static void sleep(int t){
       try {Thread.sleep(t);}
        catch (InterruptedException e) {
        e.printStackTrace();}
   }   
   
   /*
   
   private  int menuORIGINAL(String text, int max){
       String inp;
       int res;
       Scanner ler = new Scanner(System.in);
       clearScreen();
       System.out.print(text);
       inp=ler.next();
       try{
           res = Integer.parseInt(inp);
           if(res>max || res<0){
               invalido();
               ler.close();
               res = menuORIGINAL(text,max);
           }
       }
       catch(Throwable e){
           invalido();
           ler.close();
           res = menuORIGINAL(text,max);
       };
       ler.close();
       return res;
   }   
   
    
   */
   
   private static void invalido(){
       clearScreen();
       System.out.print("Inválido");
       sleep();
   }
   
   private static void invalido(String text){
       clearScreen();
       System.out.print(text);
       sleep();
   }
   
   private static void dots(){
       for(int i=0;i<4;i++){
           sleep(500);
           System.out.print(".");
       }
   }
   
   private static void fechar(){
       clearScreen();
       System.out.print("Saindo");
       dots();
       clearScreen();
   }
   
   private static void nope(){
       clearScreen();
       System.out.print("Opção ainda não implementada");
       dots();
   }
      
   private static int askOpcao(int max){
       int res;
       Scanner ler = new Scanner(System.in);
              
       try{
           res = ler.nextInt();
           if(res>max || res<0){
               invalido();
               ler.close();
               return -1;
           }
           return res;
       }
       catch(Throwable e){
           invalido();
           ler.close();
           return -1;
       }
   }
   
   private static void showMenu(String[] opcoes){
        int n = opcoes.length;
        for (int i=0; i<n; i++) {
            System.out.print("[");
            System.out.print(i+1);
            System.out.print("] - ");
            System.out.println(opcoes[i]);
        }        
   }
   
   private static int menu(String[] opcoes){
       clearScreen();
       showMenu(opcoes);
       System.out.print("\n[0] - Sair\n\n");
       int res = askOpcao(opcoes.length);
       if(res!=-1) return res;
       return menu(opcoes);
   }
   
   private static int menu(String title,String[] opcoes){
       clearScreen();
       System.out.print("\t["+title+"]\n\n");
       showMenu(opcoes);
       System.out.print("\n[0] - Sair\n\n");
       int res = askOpcao(opcoes.length);
       if(res!=-1) return res;
       return menu(title,opcoes);
   }
   
   private static int menu(String[] opcoes,String exitText){
       clearScreen();
       showMenu(opcoes);
       System.out.print("\n[0] - "+exitText+"\n\n");
       int res = askOpcao(opcoes.length);
       if(res!=-1) return res;
       return menu(opcoes,exitText);
   }
   
   private static int menu(String title,String[] opcoes,String exitText){
       clearScreen();
       System.out.print("\t["+title+"]\n\n");
       showMenu(opcoes);
       System.out.print("\n[0] - "+exitText+"\n\n");
       int res = askOpcao(opcoes.length);
       if(res!=-1) return res;
       return menu(title,opcoes,exitText);
   }
   
   private static String askString(String text){
       Scanner ler = new Scanner(System.in);
       String res = "n/a";
       clearScreen();
       System.out.print(text);
       res = ler.next();
       ler.close();
       return res;
   }
   
   private static double askDouble(String text){
       String inp = askString(text);
       double res;
       try{
           res = Double.parseDouble(inp);
       }
       catch(Throwable e){
           clearScreen();
           System.out.print("Erro ao converter para Double");
           sleep();
           res = askDouble(text);
       }
       return res;
   }
   
   private static int askInt(String text){
       String inp = askString(text);
       int res;
       try{
           res = Integer.parseInt(inp);
       }
       catch(Throwable e){
           clearScreen();
           System.out.print("Erro ao converter para Int");
           sleep();
           res = askInt(text);
       }
       return res;
   }
   
   private static boolean askBool(String text){
       Scanner ler = new Scanner(System.in);
       String inp;
       clearScreen();
       System.out.print(text);
       inp = ler.next();
       ler.close();
       switch(inp.toUpperCase()){
           case "Y": return true;
           case "N": return false;
           default : return askBool(text); 
       }
   }
   
   private static String askEmail(){
       String email = askString("Introduza o seu email:\n");
       if(!(email.contains("@") && email.contains("."))){
           invalido();
           email = askEmail();
       }
       return email;
   }
   
   private static String askNewEmail(){
       String email = askEmail();
       if(lE.get(cE).emailExiste(email)){
           clearScreen();
           System.out.print("Email já utilizado");
           sleep();
           email = askNewEmail();
       }
       return email;
   }
   
   private static String askData(){
       String data = askString("Data de Nascimento (dd/mm/aaaa):\n");
       String[] dma = data.split("/");
       if (dma.length!=3){
           clearScreen();
           System.out.println("Formato Inválido!");
           sleep();
           data = askData();
       }
       else{
           try{
               int d,m,a;
               d = Integer.parseInt(dma[0]);
               m = Integer.parseInt(dma[1]);
               a = Integer.parseInt(dma[2]);
           }
           catch(Throwable e){
               clearScreen();
               System.out.print("Erro ao converter para Inteiro");
               sleep();
               data = askData();
           }
       }
       return data;
   }
   
   private static final String[] textMenu2 =
    {"Táxi mais proximo",
     "Táxi específico"};
   
   private static void menuViagem(){
       double x = 0, y = 0;
       int inp = menu("Selecione uma das opções",textMenu2);
       switch(inp){
           case 0:  break;
           case 1: {
               x = askDouble("Introduza a coordenada X do destino pretendido:\n"); 
               y = askDouble("Introduza a coordenada Y do destino pretendido:\n");
               int classif = askInt("Classifique a sua viagem.\n");
               String mat_mais_prox = lE.get(cE).getTaxi(x,y);
               lE.get(cE).addViagem(userEmail, mat_mais_prox, x,y);
               lE.get(cE).concluirViagem(mat_mais_prox,lE.get(cE).getNomeM(mat_mais_prox),userEmail,classif);
               break;
           }
           case 2: {
               String email_mot = askString("Introduza a matricula do Veículo que pretende:\n");
               x = askDouble("Introduza a coordenada X do destino pretendido:\n"); 
               y = askDouble("Introduza a coordenada Y do destino pretendido:\n");
               if (lE.get(cE).podeFazer(email_mot)){
                   int classif = askInt("Classifique a sua viagem.\n");
                   while (classif < 0 || classif > 10){
                       System.out.println("Classificação inválida!");
                       classif = askInt("Classifique a sua viagem.\n");
                   }
                   String mat = askMatricula();
                   lE.get(cE).addViagem(userEmail,mat,x,y);
                   lE.get(cE).concluirViagem(mat,lE.get(cE).getNomeM(mat),userEmail,classif);
                   //lE.get(cE).fazerViagem(actual_cliente, lE.get(cE).getViatura_mot(email_mot), lE.get(cE).getMotorista_shallow(email_mot), new Coordenadas(x,y), classif);
               }
               break;
           }
           default: System.out.print("Erro1"); sleep(); break;
       }
   }
   
   private static String askNewMatricula(){
       String mat = askMatricula();
       
       if(lE.get(cE).matriculaExiste(mat)){
           clearScreen();
           System.out.print("A matrícula "+mat+"já se encontra em uso");
           sleep();
           mat = askMatricula();
       }
       
       return mat;
   }
   
   private static String askMatricula(){
       String mat = askString("Introduza uma matricula(00-AA-00):\n");

       String[] m = mat.split("-");
       if (m.length!=3){
           clearScreen();
           System.out.println("Formato Inválido!");
           sleep();
           mat = askMatricula();
       }
       else{
           int isNumb = 0;
           boolean badChar = false;
           int i;
           try{
               i = Integer.parseInt(m[0]);
               if(i>99||i<0) isNumb=5;
               isNumb++;
           }
           catch(Throwable e){
               if(m[0].length()!=2) badChar=true;
           }
           try{
               i = Integer.parseInt(m[1]);
               if(i>99||i<0) isNumb=5;
               isNumb++;
           }
           catch(Throwable e){
               if(m[0].length()!=2) badChar=true;
           }
           try{
               i = Integer.parseInt(m[2]);
               if(i>99||i<0) isNumb=5;
               isNumb++;
           }
           catch(Throwable e){
               if(m[0].length()!=2) badChar=true;
           }
           
           if(isNumb!=2 || badChar){
               clearScreen();
               System.out.println("Formato Inválido!");
               sleep();
               mat = askMatricula();
           }
           
       }
       return mat;
   }
   
   private static final String[] textMenu1 =
    {"Iniciar Sessão"
    ,"Registar Nova Conta"
    ,"Adicionar Veículo"
    ,"Associar Motorista a Veículo"
    ,"Guardar"};
   
   private static void menu1(){
       System.out.println(cE);
       int inp = menu("Bem Vindo à "+lE.get(cE).getNome()+"!",textMenu1);
       //try{
           switch(inp){
               case 0:  break;
               case 1:{
                   if (login() <= 0)  break;
                   else menuViagem(); break;
               }
               case 2:  addNewUser(); break;
               case 3:  System.out.print("\u000C"+addNewVeiculo());sleep(6000); break;
               case 4:  associa(); sleep(6000); break;
               case 5:  break;//guardaEstado("savefile.obj"); break;
               default: System.out.print("Erro1"); sleep(); break;
           }
       //}
       //catch (FileNotFoundException e) {System.out.println("Ficheiro não encontrado!");}
        //catch (IOException e) {System.out.println("Erro a aceder a ficheiro!");}
       if(inp!=0) menu1();
   }
   private static void associa() {
       String email_mot = askString("Introduza o email do motorista que pretende associar:\n");
       String matricula = askString("introduza a matricula do veiculo que pretende associar:\n");
       
       try{
           lE.get(cE).associa(email_mot, matricula);
       }
       catch(MatriculaNaoExistenteException e){
           System.out.println("\nA matricula que inseriu não se encontra registado no sistema.");
       }
       catch(MotoristaInexistenteException e){
           System.out.println("\nO motorista que inseriu não se encontra registado no sistema.");
       }
   }
   private static int login(){
       String email = askEmail();
       String pass = askString("Introduza a password:\n");
       int log = lE.get(cE).login(email,pass);
       switch(log){
           case 0: invalido("Email Inválido"); break;
           case 1:
           case 2: invalido("Bem-Vindo"); break;
           case -1:
           case -2: invalido("Password Incorreta!"); break;
           default: invalido("Erro no Login..."); break;
       }
       if(log>0 && log<3){
           userEmail = email;
           
           //userName = null;
           //^se for para guardar o nome: fazer metodo em Empresa getNome(email)
       }
       return log;
   }
   
   private static final String[] textAddNewUser =
    {"Novo Cliente","Novo Motorista"};
   
   private static void addNewUser(){
       int inp = menu("Registar Nova Conta",textAddNewUser,"Voltar");
       switch(inp){
           case 0:  break;
           case 1:  System.out.print("\u000C"+addNewCliente());sleep(6000); break;
           case 2:  System.out.print("\u000C"+addNewMotorista());sleep(6000); break;
           default: System.out.print("Erro2"); sleep(); break;
       }
       if(inp!=0) addNewUser();
   }
   
   private static final String[] textMenuTipoVeiculo =
    {"Carro","Carrinha de 9 Lugares","Moto","Bicicleta"};
   
   private static String addNewVeiculo(){
       int inp = menu("Tipo de Veículo",textMenuTipoVeiculo);
       if(inp!=0)
       try{
           return "\t[Veiculo Adicionado]\n\n"+
           lE.get(cE).addVeiculo(inp,
                              askNewMatricula(),
                              askDouble("Introduza a velocidade média:\n"),
                              askDouble("Introduza o preço por Km:\n"),
                              askDouble("Introduza o fator de fiabilidade:\n"),
                              askDouble("Introduza a coordenada X:\n"),
                              askDouble("Introduza a coordenada Y:\n"),
                              askBool("É um Veiculo com fila? (y/n)")
                              );
       }
       catch(Throwable e){}
       return "Erro ao adicionar Veiculo";
   }
   
   private static String addNewCliente(){
       try{
           return "\t[Cliente Adicionado]\n\n"+
           lE.get(cE).addCliente(askString("Introduza o nome:\n"),
                              askNewEmail(),
                              askString("Introduza uma password:\n"),
                              askString("Introduza a sua morada:\n"),
                              askData(),
                              askDouble("Coordenada X: "),
                              askDouble("Coordenada Y: ")
                              );
       }
       catch(Throwable e){}
       return "Erro ao adicionar Cliente";
   }
   
   private static String addNewMotorista(){
       try{
           return "\t[Motorista Adicionado]\n\n"+
           lE.get(cE).addMotorista(askString("Introduza o nome:\n"),
                                askNewEmail(),
                                askString("Introduza uma password:\n"),
                                askString("Introduza a sua morada:\n"),
                                askData(),
                                0,
                                0,
                                0,
                                true
                                );
       }
       catch(Throwable e){}
       return "Erro ao adicionar Motorista";
   }
   
   private static String askEmpresa(){
       String res = askString("Introduza o nome da Empresa:\n");
       if(!lE.containsKey(res.toUpperCase())) return res;
       invalido("\""+res+"\" já existe!");
       return askEmpresa();
   }
   
   private static void addEmpresa(){
       String nome = askEmpresa();
       lE.put(nome.toUpperCase(),new Empresa(nome));
   }
   
   private static String[] getNomesEmpresas(){
       return lE.entrySet().stream()
                .map(m->m.getValue().getNome())
                .toArray(n -> new String[n]); // .toArray(String[]::new); -> parse error
   }
   
   private static boolean loadEmpresa(){
       if(lE.keySet().isEmpty()){
           //aqui vamos ter de utilizar a função carregaestado que foi vista nas práticas e procurar por um ficheiro objeto
           invalido("Não existem empresas no sistema!");
           return false;
       }
       String[] lNomes = getNomesEmpresas();
       int inp = menu("Carregar Empresa",lNomes,"Voltar");
       if(inp==0) return false;
       cE = lNomes[inp-1].toUpperCase();
       return true;
   }
   
   private static final String[] menuEmpText =
    {"Carregar Empresa","Criar Nova Empresa"};
    
   private static void menuEmpresa(){
       int inp = menu("Bem Vindo à UMeR",menuEmpText);
       boolean b = false;
       switch(inp){
           case 0:  break;
           case 1:  b = loadEmpresa(); break;
           case 2:  addEmpresa(); break;
           default: System.out.print("Erro3"); sleep(); break;
       }
       if(inp==0) return;
       if(!b) menuEmpresa();
   }
   
   //aqui não é void, a função que carrega os dados vai devolver um objeto empresa
   private static boolean load(String s){
       Empresa new_Empresa;
       String file;
       if(s!=null) file = s;
       else file = askString("Introduza o nome do ficheiro que pretende carregar.\nSe não pretender carregar qualquer Empresa digite o número 0.\n");
       
       String cheat = "0";
       
       if (file.equals(cheat))
            return true;
            
       try{
           new_Empresa = Empresa.carregaEstado(file);
       }
       catch(FileNotFoundException e){
           clearScreen();
           invalido("O ficheiro que tentou carregar não existe.");
           //sleep(3000);
           return false;
       }
       catch(IOException e){
           e.printStackTrace();
           sleep(3000);
           return false;
       }
       catch(ClassNotFoundException e){
           e.printStackTrace();
           sleep(3000);
           return false;
       }
              
       lE.put(new_Empresa.getNome(), new_Empresa);
       //cE = new_Empresa.getNome();
       
       return true;
   }
   
   /*public void guardaEstado(String nomeFicheiro)
        throws FileNotFoundException,IOException {
        FileOutputStream fos = new FileOutputStream(nomeFicheiro);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(this); 
        oos.flush();
        oos.close();
    }
   public TUI carregaEstado(String nomeFicheiro)
        throws FileNotFoundException,IOException,ClassNotFoundException {
        FileInputStream fis = new FileInputStream(nomeFicheiro);
        ObjectInputStream ois = new ObjectInputStream(fis);
        TUI e = (TUI) ois.readObject();
        ois.close();
        return e;
   }*/
   
   public static void main(){
       //try{this.carregaEstado("savefile.obj");}
       //catch(Throwable e){invalido("Erro a ler Ficheiro");}
       lE = new HashMap<String,Empresa>();
       cE=null;
       userName=null;
       userEmail=null;
       while(!load(null));
       menuEmpresa();
       if(cE!=null) menu1();
       if(cE!=null)
       try{lE.get(cE).guardaEstado(lE.get(cE).getNome().toUpperCase()+".obj");}
       catch(Throwable e){invalido("Erro a guardar Ficheiro");}
       fechar();
   }
}
