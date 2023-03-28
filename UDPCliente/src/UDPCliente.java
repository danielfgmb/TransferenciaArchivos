import java.net.*;
import java.io.*;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.file.Files;

public class UDPCliente {

    public static final int TAMANO_PAQUETE = 512;
    public static void main(String[] args) throws Exception {
        
        String hostname = "localhost";
        int port = 6000;
        int i = 0;
 
        try {
            InetAddress address = InetAddress.getByName(hostname);
            DatagramSocket socket = new DatagramSocket();

            DatagramPacket request = new DatagramPacket(new byte[1], 1, address, port);
            socket.send(request);
 
            while (true) {
 
            
                byte[] buffer = new byte[TAMANO_PAQUETE+4];
                byte[] numeroPaquete = new byte[4];

                DatagramPacket response = new DatagramPacket(buffer, buffer.length);
                socket.receive(response);

                numeroPaquete[0] = buffer[0];
                numeroPaquete[1] = buffer[1];
                numeroPaquete[2] = buffer[2];
                numeroPaquete[3] = buffer[3];

                int paq = ByteBuffer.wrap(numeroPaquete).getInt();
                if(i<10 || paq<20){
                System.out.println("P"+String.valueOf(paq)+" I"+String.valueOf(i));
                }
                i+=1;
                
            }
 
        } catch (SocketTimeoutException ex) {
            System.out.println("Timeout error: " + ex.getMessage());
            ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println("Client error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
