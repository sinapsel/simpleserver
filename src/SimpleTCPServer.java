import java.net.ServerSocket;
import java.net.Socket;

public class SimpleTCPServer {

    public static void main(String[] args) throws Throwable {
        ServerSocket servsock = new ServerSocket(5555);
        while (true) {
            Socket s = servsock.accept();
            System.out.println("Client accepted");
            new Thread(new SocketProcessor(s)).start();
        }
    }
}
