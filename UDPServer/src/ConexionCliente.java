import java.net.*;
import java.io.*;
import java.io.File;
import java.nio.file.Files;

public class ConexionCliente extends Thread{

    public static final int TAMANO_PAQUETE = 512;

    private static int numeroClientes = 0;

    public static String nombreArchivo;

    public InetAddress direccionCliente;

    public int puertoCliente;

    public int idCliente;

    public int numeroByte;

    public int numeroPaquetesTotales;

    public int numeroPaquetesEnviados;

    public byte[] contenidoArchivo;


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

    public byte[] obtenerPaqueteArchivo(){

        byte[] paquete = new byte[TAMANO_PAQUETE + 1];

        byte numeroPaqueteInversoB = (byte) (numeroPaquetesTotales-numeroPaquetesEnviados-TAMANO_PAQUETE);

        paquete[0] = numeroPaqueteInversoB;

        int contador = 0;

        while(numeroByte < contenidoArchivo.length && contador < TAMANO_PAQUETE){
            paquete[contador+1] = contenidoArchivo[numeroByte];
            numeroByte+=1;
            contador+=1;
        }

        return paquete;
    }

    @Override
    public void run(){
        try {

            File archivo = new File(nombreArchivo);

            contenidoArchivo = Files.readAllBytes(archivo.toPath());

            numeroByte = 0;

            numeroPaquetesEnviados = 0;

            numeroPaquetesTotales = (int) (Math.ceil(contenidoArchivo.length/TAMANO_PAQUETE));

            // socket para conexion
            DatagramSocket socket = new DatagramSocket();

            // UDP, inicio ahora ya solo le envía no hay más confirmaciones ni nada
            while(numeroPaquetesEnviados < numeroPaquetesTotales){

                // donde se almacena el paquete de archivo
                byte[] paquete = obtenerPaqueteArchivo();

                // se envia el paquete
                DatagramPacket request = new DatagramPacket(paquete, paquete.length, direccionCliente, puertoCliente);
                socket.send(request);

                numeroPaquetesEnviados+=1;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}