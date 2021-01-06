import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerChat extends Thread {

    private ServerSocket server;
    public static ArrayList<InetSocketAddress> bannedClient = new ArrayList<>();
    public static ArrayList<InetSocketAddress> connectedClient = new ArrayList<>();
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_WHITE = "\u001B[37m";


    public ServerChat() throws IOException {
        server = new ServerSocket(13);
    }

    public void run() {
        Socket connection = null;

        while (!currentThread().isInterrupted()) {
            try {
                connection = server.accept();
                InetSocketAddress client = new InetSocketAddress(connection.getInetAddress(), connection.getPort());
                System.out.println(ANSI_GREEN+"Client connected: " + connection.getInetAddress().toString() + ":" + connection.getPort());
                connectedClient.add(client);
                InputStreamReader received_msg = new InputStreamReader(connection.getInputStream());
                System.out.println(ANSI_WHITE+"Assign a new thread for this client...");

                ClientHandler t = new ClientHandler(connection, received_msg);
                t.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) {
        try {
            ServerChat server = new ServerChat();
            server.start();
            int c = System.in.read();
            server.interrupt();
            server.join();
        } catch (IOException exception) {
            exception.printStackTrace();
            System.err.println("Errore!");
        } catch (InterruptedException exception) {
            System.err.println("Errore!");
        }
    }
}
