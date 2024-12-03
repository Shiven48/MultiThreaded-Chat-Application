package Chat_Application;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static final int port = 8080;
    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    // This will start the server and send clients to clientHandler
    public void Start() throws IOException {
       try{
           while(!serverSocket.isClosed()){
               Socket socket = serverSocket.accept();
               ClientHandler clientHandler= new ClientHandler(socket);
               Thread thread = new Thread(clientHandler);
               thread.start();
           }
       } catch(IOException e){
           closeServerSocket();
       }
    }

    private void closeServerSocket() throws IOException {
        if(serverSocket != null){
            serverSocket.close();
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        Server server = new Server(serverSocket);
        server.Start();
    }

}
