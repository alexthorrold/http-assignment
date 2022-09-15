/**
 * Processes lines from a client request and stores necessary values
 */
public class HttpServerRequest {

    // File that the client is requesting
    private String file = null;
    // Host server that the client is requesting the file from
    private String host = null;
    // Whether the client request has finished
    private boolean done = false;
    // Number of lines that have been processed
    private int line = 0;

    public HttpServerRequest() {
    }

    public boolean isDone() { return done; }
    public String getFile() { return file; }
    public String getHost() { return host; }

    /**
     * Processes a line of an HTTP request
     * @param in Line to be processed
     */
    public void process(String in)
    {
        // Sets done to true and returns if end of header has been reached
        if (in == null || in.equals("")) {
            done = true;
            return;
        }

        line++;

        String[] parts = in.split(" ");

        if (parts[0].equals("GET")) {
            // Returns if GET does not have 3 parts
            if (parts.length < 3) {
                return;
            }

            // Returns if another line has been processed before GET
            if (line != 1) {
                return;
            }

            // Returns with file still null if GET does not specify a file
            if (parts[1].length() > 0) {
                file = parts[1].substring(1);

                // Defaults to get index.html if no file has been specified in a directory
                if (file.endsWith("/")) {
                    file = file.concat("index.html");
                }
            }
        }
        else {
            // Returns if well-formed get line has not yet been processed
            if (file == null) {
                return;
            }

            if (parts[0].equals("Host:")) {
                // Sets host to an empty string if well-formed but no host is given
                if (parts.length == 1) {
                    if (in.equals("Host: ")) {
                        host = "";
                    }
                }
                else {
                    host = parts[1];
                }
            }
        }
    }
}
