package Chat_Application;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;
    private List<ClientHandler> clientHandlers = new ArrayList<ClientHandler>();

    public  ClientHandler(Socket socket) {
        try{
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.username = bufferedReader.readLine();
            clientHandlers.add(this);
            broadCastMessage("Server : "+this.username+" has entered the chat room!");
        } catch (Exception e) {
            closeEverything(socket,bufferedWriter,bufferedReader);
        }
    }

    // This is for receiving the message from the client concurrently
    @Override
    public void run() {
        while (socket.isConnected()) {
            try {
                receiveMessage();
            } catch (IOException e) {
                closeEverything(socket, bufferedWriter, bufferedReader);
                break;
            }
        }
    }

    private void receiveMessage() throws IOException {
        String messageFromClient = bufferedReader.readLine();
        broadCastMessage(messageFromClient);
    }

    private void broadCastMessage(String message) {
        if(!clientHandlers.isEmpty()) {
            for (ClientHandler clientHandler : clientHandlers) {
                try{
                    if(!clientHandler.username.equals(username)) {
                        bufferedWriter.write(message);
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                        broadCastMessage(this.username + " :- " + message);
                    }
                } catch(IOException e){
                    closeEverything(socket, bufferedWriter, bufferedReader);
                }
            }
        }
    }

    public void removeClient() throws IOException {
        clientHandlers.remove(this);
        broadCastMessage("SERVER : "+this.username+" has left the chat room!");
    }

    private void closeEverything(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader) {
        try {
            removeClient();
            if (socket != null) {
                socket.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
        } catch(IOException e) {
           e.printStackTrace();
        }
    }
}
