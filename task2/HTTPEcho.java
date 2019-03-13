import java.net.*;
import java.io.*;

public class HTTPEcho {
    public static void main( String[] args) {
        ServerSocket listener = null;
        int port = Integer.parseInt(args[0]); 
        try {
            listener = new ServerSocket(port);//create socket listening to port given by parameter

            while(true) {
                String response = 
                "HTTP/1.1 200 OK\r\n\r\n";
                Socket socket = null;
                try {
                    socket = listener.accept(); //wait for incoming TCP connection

                    BufferedReader cr = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
 
                    String temp = "";
                    while((temp = cr.readLine()) != null && temp.length() > 0) { 
                        System.out.print("temp: " + temp + "\r\n");//debugging
                        response += temp + "\r\n"; 
                    }
                
                    System.out.print("response:\r\n" + response);//debugging
                    dos.writeBytes(response);
                } 
                catch (IOException e) { System.err.print(e); }
                catch (Exception e) { System.err.print(e); }
                finally {
                    if (socket != null) { socket.close(); }
                }
            }
        } 
        catch (IOException e) { System.err.print(e); } 
        finally {
            if (listener != null) {
                try {
                    listener.close();
                } catch (IOException e) { System.err.print(e); }
            }   
        }
    }
}

