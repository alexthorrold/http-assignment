import java.net.*;
import java.io.*;
import java.util.*;

/**
 * Http server session to process a client connection
 */
class HttpServerSession extends Thread {

    // Client socket
    private Socket s;

    /**
     * Creates a new HttpServerSession
     * @param s Client socket
     */
    public HttpServerSession(Socket s) {
        this.s = s;
    }

    /**
     * Processes client request and returns file if request is well-formed
     */
    @Override
    public void run() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            BufferedOutputStream bos = new BufferedOutputStream(s.getOutputStream());
            HttpServerRequest hsr = new HttpServerRequest();

            // Processes request from client
            while (!hsr.isDone()) {
                hsr.process(br.readLine());
            }

            String filePath;

            // Gets correct directory for virtual hosting
            if (hsr.getHost() != null) {
                filePath = hsr.getHost() + "/" + hsr.getFile();
            }
            else {
                filePath = "localhost:60000/" + hsr.getFile();
            }

            File file = new File(filePath);

            // Returns file if exists
            if (file.exists()) {
                println(bos, "HTTP/1.1 200 OK");
                println(bos, "");

                FileInputStream fis = new FileInputStream(filePath);
                byte[] bytes = new byte[1024];

                int i = fis.read(bytes);

                // Writes the requested file to the BufferedOutputStream
                while (i != -1) {
                    // sleep(1000);
                    bos.write(bytes, 0, i);
                    i = fis.read(bytes);
                }

                fis.close();
            }
            // Sends 404 error if file requested is not found
            else {
                println(bos, "HTTP/1.1 404 Not Found");
                println(bos, "");
                System.out.println("File at " + filePath + " requested, 404 Not Found message sent.");
            }

            bos.flush();

            bos.close();
            br.close();
            s.close();
        }
        catch (Exception e) {
            System.err.println("Exception: " + e);
        }
    }

    private boolean println(BufferedOutputStream bos, String s)
    {
        String news = s + "\r\n";
        byte[] array = news.getBytes();
        try {
            bos.write(array, 0, array.length);
        } catch(IOException e) {
            return false;
        }
        return true;
    }
}

/**
 * HttpServer which processes connections from clients and creates a session for each connection
 */
class HttpServer {

    public static void main(String[] args)
    {
        System.out.println("web server starting");

        try {
            ServerSocket ss = new ServerSocket(60000);

            // Loops through checking for connections to accept,
            // starts an HttpServerSession thread when there is one
            while (true) {
                Socket s = ss.accept();
                System.out.println("Connection was received from " + s.getInetAddress().getHostAddress());

                HttpServerSession hss = new HttpServerSession(s);
                hss.start();
            }
        }
        catch (Exception e) {
            System.err.println(e);
        }
    }
}