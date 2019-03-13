package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient {
    
    public static String askServer(String hostname, int port, String optionalToServer) throws  IOException { 
        Socket cSocket = null; 
        String serverResponse = "";
        try {
            cSocket = new Socket(hostname, port);

            //only open output stream to server if optional string has been given
            if(optionalToServer != null) {
                DataOutputStream toServer = new DataOutputStream(cSocket.getOutputStream());
                toServer.writeBytes(optionalToServer + '\n');
            }

            BufferedReader fromServer = new BufferedReader(
                new InputStreamReader(cSocket.getInputStream())
            );
            
            cSocket.setSoTimeout(10000);
            String temp = "";
            int counter = 0;
            try {
                while((temp=fromServer.readLine())!= null && counter < 50000) { serverResponse += temp + '\n'; counter++; }
            } catch (SocketTimeoutException e) {/*System.out.println("Connection timed out.");*/}

        } 
        catch(IOException e) {throw e;}
        catch(Exception e) { System.err.println(e); }
        finally { 
            if (cSocket != null) { 
                cSocket.close(); 
            }
        }
        
        return serverResponse;
    }

    public static String askServer(String hostname, int port) throws  IOException {
        try { return askServer(hostname, port, null); } 
        catch (IOException e) { throw e; }
    }
}

