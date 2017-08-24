import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class SocketProcessor implements Runnable {

        private final Socket s;
        private final InputStream is;
        private final OutputStream os;

        SocketProcessor(Socket s) throws Throwable {
            this.s = s;
            this.is = s.getInputStream();
            this.os = s.getOutputStream();
        }

        @Override
        public void run() {
            try {
                String page = readInputHeaders();
                writeResponse("<html><head><title>Java doesnt sucks</title></head><body>Hello, java works</body></html>");
            } catch (Throwable t) {
                t.printStackTrace();
            } finally {
                try {//that feel when u use try in finally construction as u r so careful
                    s.close();
                } catch (Throwable t) {
                    /*do nothing*/
                }
            }
            System.out.println("Client processing finished");
        }
        
     /**
     *
     * @param s - html source
     * @throws Throwable
     */
        private void writeResponse(String s) throws Throwable {
            String response = "HTTP/1.1 200 OK\r\n" +
                    "Server: Local Java Server\r\n" +
                    "Content-Type: text/html\r\n" +
                    "Content-Length: " + s.length() + "\r\n" +
                    "Connection: close\r\n\r\n";
            String result = response + s;
            os.write(result.getBytes());
            os.flush();
            System.out.println("---SERVER_START_OUTPUT---");
            System.out.println(result);
            System.out.println("---SERVER_END_OUTPUT---");
        }

        private String readInputHeaders() throws Throwable {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            System.out.println("---CLIENT_START_INPUT---");
            String s = null;
            String GET = null;
            do{
                s = br.readLine();
                System.out.println(s);
                if(s.contains("GET"))
                    GET = s.split(" ")[1];
            }while(s != null && s.trim().length() != 0);
            System.out.println("---CLIENT_END_INPUT---");
            return GET;
        }
    }
