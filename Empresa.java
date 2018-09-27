import java.util.*;
import java.util.stream.Collectors;
import java.io.*;
import java.lang.Math;
public class Empresa implements Serializable
{
    
    /**   TIRAR DE STATIC  **/
    // HOJE SAO 23 DE MAIO AS 14:00H
    // AVANÇARTEMPO();
    
    //CLASSIFICAR APOS AVANÇARTEMPO(); NO INICIO DO LOGIN!!!
    
        
    /** VARIÁVEIS DE INSTÂNCIA **/
    private String nome; // nome da Empresa
    private ArrayList<String> livres;
    private HashMap<String,String> pair; //email motorista -> viatura
    private HashMap<String,Viatura> viaturas; //matricula ->v viatura
    private HashMap<String,Motorista> motoristas; //email -> motorista
    private HashMap<String,Cliente> clientes;
    private ArrayList<Viagem> viagens_done;
    private HashMap<String,Queue<Viagem>> queue; // matricula -> queue
    private HashMap<String,Queue<Viagem>> userQueue; // email -> queue
      
    
    //CICLO PARA FAZER FILAS ESPERA
    /** CONSTRUTORES **/
    public Empresa(String nome){
        this.livres = new ArrayList<String>();
        this.pair = new HashMap<String,String>(); // <matricula,eMail motorista>
        this.viaturas = new HashMap<String,Viatura>();
        this.motoristas = new HashMap<String,Motorista>();
        this.clientes = new HashMap<String,Cliente>();
        this.viagens_done = new ArrayList<Viagem>();
        this.queue = new HashMap<String,Queue<Viagem>>();
        this.userQueue = new HashMap<String,Queue<Viagem>>();
        this.nome = nome;        
    }
    
    public boolean isCliente(String email){return (clientes.containsKey(email));}
    public boolean isMotorista(String email){return (motoristas.containsKey(email));}
    
    public void setNome(String nome){this.nome = nome;}
    
    public String getNome(){return this.nome;}
    public String getNomeM(String mat){return this.pair.get(mat);}
            
    public Map<String,Cliente> getClientes(){
        Map<String,Cliente> ret = new HashMap<String,Cliente> ();
        for (Cliente c : clientes.values()){
            ret.put(c.getEmail(), c.clone());
        }
        return ret;
    }
        
    public Map<String,Motorista> getMotoristas(){
        Map<String,Motorista> ret = new HashMap<String,Motorista> ();
        for (Motorista m : motoristas.values()){
            ret.put(m.getEmail(), m.clone());
        }
        return ret;
    }
    
    /** Outros Métodos **/
    public ArrayList<Viatura> deepCopy(ArrayList<Viatura> vs){
        ArrayList<Viatura> novo = new ArrayList<Viatura>();
        for(Viatura v : vs)
            novo.add(v.clone()); 
        return novo;
    }
    
    public int emailExists(String email){
        if(this.clientes.containsKey(email)) return 1;
        if(this.motoristas.containsKey(email)) return 2;
        return 0;
    }
    
    public int login(String email,String pass){
        switch(emailExists(email)){
            case 0: return 0;
            case 1: return (this.clientes.get(email).getPassword().equals(pass))?1:-1;
            case 2: return (this.motoristas.get(email).getPassword().equals(pass))?2:-2;
            default: return 0;
        }
    }
    
    public boolean associa(String matricula, String email_mot)
        throws MatriculaNaoExistenteException, MotoristaInexistenteException
    {
        Motorista m = motoristas.get(email_mot);
        if (m == null) throw new MotoristaInexistenteException(email_mot);
        
        Viatura v = viaturas.get(matricula);
        if (v == null) throw new MatriculaNaoExistenteException(matricula);
        
        return setPair(v, m);
    }
    
    public boolean setPair(Viatura v, Motorista m){
        if(!livres.contains(m.getEmail())) return false;
        String mm = pair.putIfAbsent(v.getMatricula(),m.getEmail());
        livres.remove(m.getEmail());
        
        if(mm!=null) livres.add(mm);
        else{
            viaturas.putIfAbsent(v.getMatricula(),v); // aqui talvez chegue fazer só put...
            livres.remove(mm);  // aqui é preciso clone????
        }
        return true;
    }
    
    public boolean freePair(String v,String emailM){
        Motorista m = motoristas.get(emailM);
        if(livres.contains(m.getEmail())) return false;
        if(pair.get(v)!=m.getEmail()) return false;
        livres.add(pair.remove(v));
        return true;
    }
    
    public boolean addFreeM(Motorista m){
        if(livres.contains(m.getEmail()) || pair.containsValue(m)) return false;
        return livres.add(m.getEmail());
    }
    
    public ArrayList<Motorista> addFreeM(ArrayList<Motorista> list){
        ArrayList<Motorista> res = new ArrayList<>();
        list.stream().map((m->(!livres.contains(m.getEmail())
                            && !pair.containsValue(m.getEmail()))?
                               (livres.add(m.getEmail())) : (res.add(m.clone()))));
        return res;
    }
    
    public boolean addFreeV(Viatura v){
        if(pair.containsKey(v.getMatricula())) return false;
        viaturas.put(v.getMatricula(),v);
        return true;
    }
    
    public Map<String,String> getActivePair(){
        return pair.entrySet().stream()
                   .filter(map->map.getValue()!=null)
                   .collect(Collectors.toMap(p->p.getKey(),p->p.getValue()));
    }
    
    public String getTaxi(double x,double y){ // Taxi mais prox
        String res = null;
        double dist = Double.MAX_VALUE;
        Coordenadas c = new Coordenadas(x,y);
        
        for(String k : getActivePair().keySet()){
            if(viaturas.get(k).getCoord().distEuc(c)<dist &&
               motoristas.get(pair.get(k)).getDisponibilidade()){
                dist= viaturas.get(k).getCoord().distEuc(c);
                res = viaturas.get(k).getMatricula();
            }
        }
        
        return res;
    }
    
    public String getTaxi(String m){ // Taxi especifico
        if(viaturas.get(m)!=null 
        && (viaturas.get(m).hasQueue() || motoristas.get(pair.get(m)).getDisponibilidade()))
        return m;
        return null;
    }
    
    //faz tb a média logo c a anterior
    public void Classificar_mot(String id_mot, double classificacao){
        double med_atual = this.motoristas.get(id_mot).getClassificacao();
        double med_final = ( (med_atual + classificacao) / 2 );
        this.motoristas.get(id_mot).setClassificacao(med_final);
    }
    
    public boolean matriculaExiste(String mat){
        return viaturas.containsKey(mat);
    }
    
    public boolean emailExiste(String email){
        return (clientes.containsKey(email) || motoristas.containsKey(email));
    }
    
    public String addVeiculo(int tipoVeiculo,String mat,double velM,double precoKm
           ,double reliability,double x,double y,boolean hasQueue)
           throws MatriculaNaoExistenteException,OpcaoInexistenteException{
        Viatura v;
        if(matriculaExiste(mat)) throw new MatriculaNaoExistenteException(mat);
        switch(tipoVeiculo){
            case 1: v = new Carro    (mat,velM,precoKm,reliability,x,y,hasQueue);break;
            case 2: v = new Carrinha (mat,velM,precoKm,reliability,x,y,hasQueue);break;
            case 3: v = new Moto     (mat,velM,precoKm,reliability,x,y,hasQueue);break;
            case 4: v = new Bicicleta(mat,velM,precoKm,reliability,x,y,hasQueue);break;
            default: throw new OpcaoInexistenteException("Tipo de Viatura \'"+
                               tipoVeiculo+"\' não se encontra definido");
        }
        viaturas.put(mat,v);
        return v.toString();
    }
    
    public String addCliente(String nome,String email,String password
           ,String morada,String dataNascimento,double x,double y)
           throws EMailExistenteException{
        if(emailExiste(email))
            throw new EMailExistenteException(email);
        Cliente c = new Cliente(nome,email,password,morada,dataNascimento,x,y);
        clientes.put(email,c);
        return c.toString();
    }
    
    public String addMotorista(String nome,String email,String password
           ,String morada,String dataN,double tKm,double cf,double cm,boolean available)
           throws EMailExistenteException{
        if(emailExiste(email))
            throw new EMailExistenteException(email);
        Motorista m = new Motorista(nome,email,password,morada,dataN,tKm,cf,cm,available);
        motoristas.put(email,m);
        return m.toString();
    }
    
    
    //verifica se pode fazer uma viagem aka motorista disponivel
    public boolean podeFazer(String mat){
        if(!this.pair.containsKey(mat)) return false;
        if(this.motoristas.get(pair.get(mat)).getDisponibilidade()) return true;
        return this.viaturas.get(mat).hasQueue();        
    }
    
    //no fim, adicionar ao registo de viagens - noutro sitio
    //adicionar metodo para calcular preço ou dentre deste metodo
    //preço = if tempoestimado * 0.25 > real ->preço = ...
    public Viagem fazerViagem(String mailCliente, String matricula,double x,double y){    
        Cliente c = clientes.get(mailCliente);
        Viatura v = viaturas.get(matricula);
        Coordenadas dest = new Coordenadas(x,y);
        double distV = dest.distEuc(c.getLocalizacao());
        double distC = c.getLocalizacao().distEuc(v.getCoord());
        double duracao_teorica = v.getVelMedia() * distV; 
        double espera_teorica = v.getVelMedia() * distC;
        double duracao_real = duracao_teorica; // + random do fator fiabilidade 
        double espera_real = espera_teorica; // + random do fator fiabilidade 
        double preco = v.getPrecoKm() *( distV + distC );
        
        double rand1 = Math.random();
        double rand2 = Math.random();        
        double fiab = v.getFiabilidade();

        rand1 = (rand1<=fiab)? 0 : rand1-fiab; // se a "gravidade" do imprevisto<=fiabilidade da viatura
        rand2 = (rand2<=fiab)? 0 : rand2-fiab;//a diferença vem da probabilidade de imprevistos
                                              // menos a fiabilidade q o veiculo já tem(se um veiculo tem menos fiabilidade q outro
                                              //está menos preparado logo tb se vai penalizar mais a nivel de tempo)
        duracao_real*=1+rand1;
        espera_real*=1+rand2;
                
        return new Viagem(duracao_teorica, duracao_real, espera_teorica, espera_real
                         ,c.getLocalizacao(), dest, preco);
             
    }
    
    public boolean addViagem(String mailCliente,String matricula,double x,double y){
        if(!podeFazer(matricula)) return false;
        Viagem v = fazerViagem(mailCliente,matricula,x,y);
        this.queue.get(matricula).add(v);
        return true;
    }
    
    public Viagem removeQueue(String matricula){
        return this.queue.get(matricula).remove();
    }
    
    public boolean concluirViagem(String mat,String emailM,String emailC,int classif){
        Viagem v = removeQueue(mat);
        v.setClassificacao(classif);
        this.motoristas.get(emailM).addViagem(v);
        this.clientes.get(emailC).addViagem(v);
        return true;
    }
    
    public ArrayList<Viagem> getHistoricoViagens(String email){
        if(motoristas.containsKey(email)) return motoristas.get(email).getViagensInfo();
        if(clientes.containsKey(email)) return clientes.get(email).getViagensInfo();
        return null;
    }
    
    /**
     * Método que guarda em ficheiro de objectos o objecto que recebe a mensagem.
     */
    
    public void guardaEstado(String nomeFicheiro) throws FileNotFoundException,IOException {
        FileOutputStream fos = new FileOutputStream(nomeFicheiro);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(this); //guarda-se todo o objecto de uma só vez
        oos.flush();
        oos.close();
    }
    
    /**
     * Método que recupera uma instância de HoteisInc de um ficheiro de objectos.
     * Este método tem de ser um método de classe que devolva uma instância já construída de
     * HoteisInc.
     * 
     * @param nome do ficheiro onde está guardado um objecto do tipo HoteisInc
     * @return objecto HoteisInc inicializado
     */
    
    public static Empresa carregaEstado(String nomeFicheiro) throws FileNotFoundException,
                                            IOException,
                                            ClassNotFoundException {
        FileInputStream fis = new FileInputStream(nomeFicheiro);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Empresa e = (Empresa) ois.readObject();
        ois.close();
        return e;
    }
}
