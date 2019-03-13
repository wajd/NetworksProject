import java.net.*;
import java.util.Arrays;

import tcpclient.TCPClient;
import java.io.*;

public class ConcHTTPAsk {
    public static void main( String[] args) {
        ServerSocket listener = null;
        try {
            listener = new ServerSocket(Integer.parseInt(args[0]));//create socket listening to port given by parameter
            
            while(true) {
                Socket socket =listener.accept();
                Thread thread = new Thread(new Connection(socket));
                thread.start();
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

class Connection implements Runnable {
    private Socket socket = null;

    Connection(Socket socket) {
        this.socket = socket;
    }

    public  void run() {
        String request = "";
        String client = "";
        String response = "";
        try {

            BufferedReader cr = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

            DataOutputStream dos = new DataOutputStream(this.socket.getOutputStream());

            String temp = "";
            while((temp = cr.readLine()) != null && temp.length() > 0) { 
                request += temp + "\r\n"; 
            }
            //System.out.print("\nrequest:\n" + request);//debugging

            String[] parsedRequest = RequestParser.parse(request);
            //System.out.print("\nparsed:\n" + Arrays.toString(parsedRequest));//debugging

            response += parsedRequest[0];

            if (parsedRequest[0]=="HTTP/1.1 200 OK\r\n\r\n"){
                try {
                    client = TCPClient.askServer(parsedRequest[1], Integer.parseInt(parsedRequest[2]), parsedRequest[3]);
                } catch(IOException e) {
                    response = "HTTP/1.1 404 Not Found\r\n\r\n";
                }
            }

            //System.out.print("\nclient:\n" + client);//debugging

            response += client;

            //System.out.print("\nresponse:\n" + response);//debugging
            dos.writeBytes(response);
        } 
        catch (IOException e) { System.err.print(e); }
        catch (Exception e) { System.err.print(e); }
        finally {
            if (this.socket != null) {
                try {
                    this.socket.close();
                } catch (IOException e) { System.err.print(e); } 
            }
        }
    } 
}

class RequestParser {
    public static String[] parse(String request) {
        String[] parsed = new String[4];
        //System.out.print("\nRparsed:\n" + Arrays.toString(parsed));//debugging

        //get the GET line part of the request
        String[] get = request.split("\r\n")[0].split(" ");
        //System.out.print("\nRget:\n" + Arrays.toString(get));//debugging

        //if not a valid GET request, abort and return 400
        if(get[0].equals("GET") && get[1].startsWith("/ask?")) {
            String[] params = get[1].split("&");
            params[0] = params[0].replaceFirst("/ask\\?", "");
            //System.out.print("Rparams:\r\n" + Arrays.toString(params));//debugging

            for (int i = 0; i < params.length; i++) {
                String[] param = params[i].split("=");
                //System.out.print("Rparam:\r\n" + Arrays.toString(param));//debugging
                switch (param[0]) {
                    case "hostname": parsed[1] = param[1]; break;
                    case "port": parsed[2] = param[1]; break;
                    case "string": parsed[3] = param[1]; break;
                    default: break;//ignore any other parameters
                }
            }
            
            parsed[0] = "HTTP/1.1 200 OK\r\n\r\n";
        } else {
            parsed[0] = "HTTP/1.1 400 Bad Request\r\n\r\n";
        }
        //System.out.print("\nRparsed2:\n" + Arrays.toString(parsed));//debugging

        return parsed;
    }
}

