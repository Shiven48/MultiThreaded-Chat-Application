import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final int port = 8080;
    String username;
    BufferedWriter writer;
    BufferedReader reader;
    Socket socket;

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        }
        catch (IOException e) {
            closeEverything(socket,reader,writer);
        }
    }

    // This will share the message to the group chat
    private void shareMessage() {
        try{
            writer.write(username);
            writer.newLine();
            writer.flush();

            Scanner scanner = new Scanner(System.in);
            while(socket.isConnected()) {
                String messageToPass = scanner.nextLine();
                writer.write(messageToPass);
                writer.newLine();
                writer.flush();
            }
        } catch (IOException e){
            closeEverything(socket,reader,writer);
        }
    }

    // This will be used to receive message from the group chat
    private void receiveMessage() {
        new Thread(new Runnable() {
            public void run() {
                while (socket.isConnected()){
                    try {
                        String messageReceived = reader.readLine();
                        System.out.println(messageReceived);
                    } catch (IOException e) {
                        closeEverything(socket,reader,writer);
                    }
                }
            }
        }).start();
    }

    public void closeEverything(Socket socket,BufferedReader reader,BufferedWriter writer) {
        try {
            if (socket != null) {
                socket.close();
            }
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost",port);
        System.out.println("Enter your Username : ");
        Scanner scanner = new Scanner(System.in);
        String username = scanner.nextLine();
        Client client = new Client(socket,username);
        client.shareMessage();
        client.receiveMessage();
    }
}
