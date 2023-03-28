import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;

public class ConexionCliente extends Thread{

    public static final int TAMANO_PAQUETE = 512;

    private static int numeroClientes = 0;

    public static String nombreArchivo;

    public InetAddress direccionCliente;

    public int puertoCliente;

    public int idCliente;

    public int numeroByte;

    public static int numeroPaquetesTotales;

    public int numeroPaquetesEnviados;

    public static byte[] contenidoArchivo;


    public ConexionCliente(InetAddress direccionCliente, int puertoCliente){
        this.direccionCliente = direccionCliente;
        this.puertoCliente = puertoCliente;
    }

    public static synchronized void setNombreArchivo(String pNombreArchivo){
        nombreArchivo = pNombreArchivo;
    }

    public static synchronized int asignarIdCliente(){
        numeroClientes += 1;
        return numeroClientes;
    }

    public byte[] obtenerPaqueteArchivo(){

        byte[] paquete = new byte[TAMANO_PAQUETE + 4];

        int numeroPaqueteInverso = numeroPaquetesTotales-numeroPaquetesEnviados-1;

        byte[] inicio = ByteBuffer.allocate(4).putInt(numeroPaqueteInverso).array();

        paquete[0] = inicio[0];
        paquete[1] = inicio[1];
        paquete[2] = inicio[2];
        paquete[3] = inicio[3];

        int contador = 0;

        while(numeroByte < contenidoArchivo.length && contador < TAMANO_PAQUETE){
            paquete[contador+4] = contenidoArchivo[numeroByte];
            numeroByte+=1;
            contador+=1;
        }

        return paquete;
    }

    public static void leerArchivo(){

        File archivo = new File(nombreArchivo);

        try {
            contenidoArchivo = Files.readAllBytes(archivo.toPath());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if(contenidoArchivo.length==0){
            System.out.println("EL ARCHIVO NO SE ENCONTRO O ESTA VACÍO");
        }

        numeroPaquetesTotales = (int) (Math.ceil( (double) contenidoArchivo.length/TAMANO_PAQUETE));
    }

    @Override
    public void run(){
        try {

            

            numeroByte = 0;

            numeroPaquetesEnviados = 0;

            

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