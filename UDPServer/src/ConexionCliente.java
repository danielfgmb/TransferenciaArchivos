import java.net.*;
import java.io.*;

public class ConexionCliente extends Thread{

    private static int numeroClientes = 0;

    public static String nombreArchivo;

    public InetAddress direccionCliente;

    public int puertoCliente;

    public int idCliente;

    public int progreso;

    public ConexionCliente(InetAddress direccionCliente, int puertoCliente){
        idCliente = ConexionCliente.asignarIdCliente();
    }

    public static synchronized void setNombreArchivo(String pNombreArchivo){
        nombreArchivo = pNombreArchivo;
    }

    public static synchronized int asignarIdCliente(){
        numeroClientes += 1;
        return numeroClientes;
    }

    @Override
    public void run(){
        try {
            // socket para conexion
            DatagramSocket socket = new DatagramSocket();

            // enviar confirmacion al cliente
            DatagramPacket confirmacionServidor = new DatagramPacket(new byte[1], 1, direccionCliente, puertoCliente);
            socket.send(confirmacionServidor);
            try {
                sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            while(true){

                // donde se almacena la respuesta
                byte[] buffer = new byte[512];
                
                // se recibe el request
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);

                String peticion = new String(buffer, 0, request.getLength());

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}