import java.net.*;
import java.io.*;

public class UDPServidor {

    public static int numeroPuerto = 6000;
    public static void main(String[] args) throws Exception {

        DatagramSocket socketLlegadaClientes = new DatagramSocket(numeroPuerto);

        Monitor monitor = new Monitor();
        monitor.start();
        
        try{
            while(true){
                // donde se almacena la respuesta
                byte[] buffer = new byte[512];
                
                // se recibe el request
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                socketLlegadaClientes.receive(request);

                InetAddress direccionCliente = request.getAddress();
                int puertoCliente = request.getPort();

                ConexionCliente conexion = new ConexionCliente(direccionCliente, puertoCliente);
                

            }
        } catch(Exception e){

        }
    }
}


