
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Scanner;

public class Client extends Thread {

    private String server_name;
    private int server_port;
    private Socket client_socket = new Socket();
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public Client(String server, int port) {
        server_name = server;
        server_port = port;
    }

    public void run() {

        InetSocketAddress server_address = new InetSocketAddress(server_name, server_port);

        try {
            client_socket.connect(server_address);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int s = 0;
        Scanner scanner = new Scanner(System.in);

        try {

            while (s != 5) {

                InputStreamReader received_msg = new InputStreamReader(client_socket.getInputStream());
                ClientReader t = new ClientReader(client_socket, received_msg);
                t.start();

                System.out.println(ANSI_GREEN + "1)Invia msg \n2)Banna\n3)Client connessi\n4)History\n5)Disconnect");
                System.out.println(ANSI_YELLOW + "Scegli un numero per continuare:");
                s = scanner.nextInt();

                switch (s) {
                    case 1:
                        System.out.println(ANSI_WHITE + "Scrivi qui=> ");
                        String msg = scanner.next();
                        this.sendMessage(msg);
                        break;
                    case 2:
                        System.out.println(ANSI_YELLOW + "Inserire il client da bannare[127.0.0.1:PORTA]==>");
                        this.sendMessage("BAN_" + scanner.next());
                        break;
                    case 3:
                        this.sendMessage("WHO");
                        break;
                    case 4:
                        this.sendMessage("HISTORY");
                        break;
                }

            }
            System.out.println(ANSI_RED + "Client disconnected");
            sendMessage("Close");


        } catch (SocketTimeoutException exception) {
            System.err.println("Nessuna risposta dal server!");
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }

    public void sendMessage(String message) throws IOException {
        OutputStreamWriter msg_out = new OutputStreamWriter(this.client_socket.getOutputStream());
        msg_out.write(message);
        msg_out.flush();

    }



    public static void main(String args[]) throws IOException {
        String server;
        int port;

        if (args.length != 3) {
            server = "127.0.0.1"; // localhost
            port = 13; // porta TCP standard servizio daytime
        } else {
            server = args[0];
            port = Integer.parseInt(args[1]);
        }

        Client client = new Client(server, port);
        client.start();
    }

}
