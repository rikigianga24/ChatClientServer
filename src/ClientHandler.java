import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class ClientHandler extends Thread {

    private final Socket socket;
    private InputStreamReader received_msg;
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_WHITE = "\u001B[37m";

    static ArrayList<String> history = new ArrayList<String>();

    public ClientHandler(Socket socket, InputStreamReader received_msg) {
        this.socket = socket;
        this.received_msg = received_msg;
    }

    public void run() {
        while (!currentThread().isInterrupted()) {

            String msg = read(received_msg);
            history.add(msg);
            InetSocketAddress checkban = new InetSocketAddress(this.socket.getInetAddress(), this.socket.getPort());

            if (msg.equals("Close")) {
                System.out.println(ANSI_RED+"Client " + this.socket + " sends exit...");
                System.out.println(ANSI_RED+"Closing this connection.");
                try {
                    this.socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ServerChat.connectedClient.remove(checkban); //elimino il client che si disconnette
                currentThread().interrupt();

            }


            if (ServerChat.bannedClient.contains(checkban)) {
                if(!msg.contains("Close")) {
                    System.out.println(ANSI_YELLOW + "Un utente bannato ha provato ad inviare");
                }
            } else {

                System.out.println("[" + socket.getInetAddress() + ":" + socket.getPort() + "]==>" + msg);

                if (msg.contains("BAN_")) { //il messaggio che arriva per controllare il ban è: BAN_127.0.0.1:PORTA
                    String client[] = msg.split("_"); //BAN_ , 127.0.0.1:PORTA
                    String port[] = client[1].split(":"); //127.0.0.1, PORTA
                    checkban = new InetSocketAddress(port[0], Integer.parseInt(port[1]));// creo inetsocketaddress per controllare il ban
                    System.out.println(ANSI_YELLOW+"Banno "+checkban.getAddress()+":"+checkban.getPort());
                    ServerChat.bannedClient.add(checkban);
                }

                if (msg.contains("SHOW_BAN")) {
                    for (int i = 0; i < ServerChat.bannedClient.size(); i++) {
                        System.out.println(ANSI_YELLOW+"Banned client=> " + ServerChat.bannedClient.get(i));
                    }
                }

                if (msg.equals("WHO")) {
                    for (int i = 0; i < ServerChat.connectedClient.size(); i++) {
                        System.out.println(ANSI_WHITE+"[" + ServerChat.connectedClient.get(i) + "]");
                    }
                }

                if (msg.equals("HISTORY")) {
                    for (int i = 0; i < history.size(); i++) {
                        System.out.println(ANSI_GREEN + history.get(i));
                    }
                }

            }

        }

    }

    public String read(InputStreamReader received_msg) {
        char[] buffer = new char[1024];
        try {
            int lenght = received_msg.read(buffer, 0, buffer.length);
            String msg = new String(buffer, 0, lenght);
            return msg;
        } catch (SocketException exception) {
            exception.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Close";
    }


}
