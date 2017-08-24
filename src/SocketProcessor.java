package simpletcpserver;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
            String page = (new StringBuilder("www/").append(readInputHeaders().substring(1))).toString();
            if(page.equals("www/"))
                page = "www/index.html";
            System.out.println(page);
            String ext = page.split("\\.")[page.split("\\.").length - 1];
            System.out.println(ext);
            StringBuilder html = new StringBuilder("");
            try{
                BufferedReader br = null;
                if(ext.equals("html") || ext.equals("htm")){
                    br = new BufferedReader(new InputStreamReader(new FileInputStream(page)));
                }
                else if(ext.equals("php")){
                    Process php = (Runtime.getRuntime().exec("php ".concat(page)));
                    br = new BufferedReader(new InputStreamReader(php.getInputStream(), "UTF-8"));
                }
                String line;
                while((line = br.readLine())!= null){
                    if(line.contains("Could not open"))
                        throw new FileNotFoundException();
                    html.append(line);
                }
            }catch(FileNotFoundException | NullPointerException e){
                System.out.print("Err:\t");
                System.out.println(e.getMessage());
                writeResponse("",1);
            }
              
            if(!html.toString().equals("")) 
            writeResponse(html.toString(),0);
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
     * @param i - index of output code:
     *              0: 200 OK
     *              1: 404 Not Found
     * @throws Throwable
     */
    private void writeResponse(String s, int i) throws Throwable {
            String[] response = {"HTTP/1.1 200 OK\r\n" +
                    "Server: Local Java Server\r\n" +
                    "Content-Type: text/html\r\n" +
                    "Content-Length: " + s.length() + "\r\n" +
                    "Connection: close\r\n\r\n",
                    "HTTP/1.0 404 Not Found\r\n" +
                    "Server: Local Java Server\r\n" +
                    "Content-Type: text/html\r\n" +
                    "Content-Length: " + s.length() + "\r\n" +
                    "Connection: close\r\n\r\n"
            };
            String result = response[i] + s;
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
        }while(!(s.equals(null) || s.trim().length() == 0));
        System.out.println("---CLIENT_END_INPUT---");
        return GET;
        }
}
