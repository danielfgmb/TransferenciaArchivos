import java.net.*;
import java.io.*;

public class UDPServidor {

    public static int numeroPuerto = 6000;


    public static void main(String[] args) throws Exception {

        ConexionCliente.setNombreArchivo("C:\\Users\\dgomezba\\Videos\\views\\2022-08-23 19-43-40.mkv");
        //ConexionCliente.setNombreArchivo("C:\\Users\\dgomezba\\Videos\\2022-08-23 15-48-11.mkv");

        ConexionCliente.leerArchivo();

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

                // se obtienen los datos del cliente
                InetAddress direccionCliente = request.getAddress();
                int puertoCliente = request.getPort();

                // se inicia la conexion y se registra en el monitor para poder ver el progreso
                ConexionCliente conexion = new ConexionCliente(direccionCliente, puertoCliente);
                monitor.registrarConexion(conexion);
                conexion.start();
                

            }
        } catch(Exception e){
            socketLlegadaClientes.close();
        }

        socketLlegadaClientes.close();
    }
}


