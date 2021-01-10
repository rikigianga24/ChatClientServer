import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

public class ClientReader extends Thread {


    private final Socket socket;
    private InputStreamReader received_msg;

    public ClientReader(Socket socket, InputStreamReader received_msg) {
        this.socket = socket;
        this.received_msg = received_msg;
    }

    public void run() {
        String msg = read(received_msg);
        System.out.println(msg+"\n");
        if (msg.equals("Close")) {
            System.out.println("Client " + this.socket + " sends exit...");
            System.out.println("Closing this connection.");
            try {
                this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            currentThread().interrupt();
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
