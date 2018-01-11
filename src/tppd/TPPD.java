
package tppd;


import java.net.*;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


   class ComunicaComJogo extends Thread{
    
         Socket s;
                
        ComunicaComJogo(){
             Socket s = null;
        }
        
        @Override
       public void run(){
            try{
           ServerSocket server = new ServerSocket(10001);
           while(true){
                this.s = server.accept();
               
               System.out.println("Aceitei cliente");
               BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
               System.out.println("MSG: " + reader.readLine());
               PrintWriter out = new PrintWriter(s.getOutputStream(), true);
               out.println("Eu sou um jogador e mandei uma mensagem");
               
           }
           
       }catch(Exception e){
                try {
                    System.out.println("Erro: " + e);
                    this.s.close();
                } catch (IOException ex) {
                    Logger.getLogger(ComunicaComJogo.class.getName()).log(Level.SEVERE, null, ex);
                }
       }
       
    }
 }

public class TPPD {
public static final int MAX_SIZE = 4000;
    public static final String TIME_REQUEST = "TIME";
    public static final int TIMEOUT = 10; //segundos

    public static void main(String[] args) throws IOException
    {
        
        InetAddress serverAddr = null;
        int serverPort = -1;
        Socket socket = null,socketToComunicaJogo = null ;
        BufferedReader in = null, inToComunicaJogo = null;
        PrintWriter out = null,outToComunicaJogo = null;
        String response;
        
        
        
       // ComunicaComJogo(Socket s){
        
        if(args.length != 2){
            System.out.println("Sintaxe: java TcpTimeClient serverAddress serverUdpPort");
            return;
        }

        try{

            serverAddr = InetAddress.getByName(args[0]);
            serverPort = Integer.parseInt(args[1]);   
            
            socket = new Socket(serverAddr, serverPort);
            socket.setSoTimeout(TIMEOUT*1000);
            
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            
            String mensagem = "";
            Scanner sc = new Scanner(System.in);
            
            
            ComunicaComJogo cj = new ComunicaComJogo();
             cj.start();
            
            while( !mensagem.equalsIgnoreCase("fim")){
            
            System.out.println("qual a sua mensagem? ");  
            mensagem = sc.next();  
            out.println(mensagem);
            out.flush();
            
            
            
            //A resposta deve terminar com uma mundanca de linha.
            //Os caracteres de mudanca de linha nao sao copiados para "response"
            response = in.readLine();
            
            if(response == null){
                System.out.println("O servidor nao enviou qualquer respota antes de"
                        + " fechar a ligacao TCP!");   
            }else{
                
                System.out.println("Hora indicada pelo servidor: " + response);

                //******************************************************************
                //Exemplo de como retirar os valores da mensagem
                try{
                    StringTokenizer tokens = new StringTokenizer(response," :");

                    int hour = Integer.parseInt(tokens.nextToken().trim());
                    int minute = Integer.parseInt(tokens.nextToken().trim());
                    int second = Integer.parseInt(tokens.nextToken().trim());

                    System.out.println("Horas: " + hour + " ; Minutos: " + minute + " ; Segundos: " + second);
                }catch(NumberFormatException e){}
                //******************************************************************
            }
           } // WHILE
        }catch(UnknownHostException e){
             System.out.println("Destino desconhecido:\n\t"+e);
        }catch(NumberFormatException e){
            System.out.println("O porto do servidor deve ser um inteiro positivo.");
        }catch(SocketTimeoutException e){
            System.out.println("Não foi recebida qualquer resposta:\n\t"+e);
        }catch(IOException e){
            System.out.println("Ocorreu um erro no acesso ao socket:\n\t"+e);
        }finally{
            if(socket != null){
                socket.close();
            }
        }
   }      
}

