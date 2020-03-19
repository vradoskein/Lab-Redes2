
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.Socket;
import javax.json.Json;

/**
 *
 * @author vradoskein
 */
public class Peer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        //fazer um login aqui...
        //Pensei em fazer uma classe user q guarda os dados do usuario e 
        //a porta pra manter. hhh
        System.out.println("> Username and Port#");
        String[] setupValues = bufferedReader.readLine().split(" ");//Entrada de dados, mudar pra user and pass
        ServerThread serverThread = new ServerThread(setupValues[1]);// inicia o breguinithgs com a porta, trocar pelo user.port
        serverThread.start();
        new Peer().updateListenToPeers(bufferedReader, setupValues[0], serverThread);//username 
    }
    
    public void updateListenToPeers(BufferedReader bufferedReader, String username, ServerThread serverThread) throws Exception{
        //aqui ele pega a conexao com outra pessoa, antes disso pensei em ter uma lista das pessoas
        //que estao online, ai a parte de resolver a porta e o localhost a gente deixaria
        //para a classe do user, se cada usuario tiver a porta q ele vai acessar da pra manter facil
        System.out.println("> enter space separated hostname:port#"); 
        System.out.println("Peers to recieve messa from (s to skip)");
        String input = bufferedReader.readLine();
        String[] inputValues = input.split(" ");
        
        //Aqui eh a logica de comecar a conexao com o outro peer
        if(!input.equals("s")){
            for(int i = 0; i < inputValues.length; i++){
                String[] address = inputValues[i].split(":");
                Socket socket = null;
                
                try {
                    socket = new Socket(address[0], Integer.valueOf(address[1]));
                    new PeerThread(socket).start();
                } catch(Exception e){
                    if (socket!= null) socket.close();
                    else System.out.println("Invalid Input...");
                }
            }
        }
        communicate(bufferedReader, username, serverThread);
    }
    
     public void communicate(BufferedReader bufferedReader, String username, ServerThread serverThread){
         try{
             System.out.println("> write message, (e to exit, c to change)");
             boolean flag = true;
             while(flag){
                 String message = bufferedReader.readLine();
                 if (message.equals("e")){
                     flag = false;
                     break;
                 } else if (message.equals("c")){
                     updateListenToPeers(bufferedReader, username, serverThread);
                 } else{
                     StringWriter stringWriter = new StringWriter();
                     Json.createWriter(stringWriter).writeObject(Json.createObjectBuilder()
                             .add("username", username)
                             .add("message", message)
                             .build());
                     serverThread.sendMessage(stringWriter.toString());
                 }
             }
             System.exit(0);
         }catch (Exception e){}
     }
}
