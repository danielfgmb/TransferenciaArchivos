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

    public long startTime;

    public long currentTime;

    public long totalTime;

    public boolean transferenciaCompleta;


    public ConexionCliente(InetAddress direccionCliente, int puertoCliente){
        this.direccionCliente = direccionCliente;
        this.puertoCliente = puertoCliente;
        this.idCliente = asignarIdCliente();
        transferenciaCompleta = false;
    }

    public static synchronized void setNombreArchivo(String pNombreArchivo){
        nombreArchivo = pNombreArchivo;
    }

    public static synchronized int asignarIdCliente(){
        numeroClientes += 1;
        return numeroClientes;
    }

    public byte[] obtenerPaqueteArchivo(){

        // el tamaño del paquete útil + 4 bytes del entero que contiene el número paquete inverso
        byte[] paquete = new byte[TAMANO_PAQUETE + 4];

        // el número de paquete inverso, el primer número que tiene es numeroPaquetesTotales ya que se cuenta a si mismo
        // de manera que el último sea 0
        int numeroPaqueteInverso = numeroPaquetesTotales-numeroPaquetesEnviados-1;

        // convierte el entero a 4 bytes
        byte[] inicio = ByteBuffer.allocate(4).putInt(numeroPaqueteInverso).array();
        
        // se cambian los 4 primeros bytes del paquete con el número de paquete
        paquete[0] = inicio[0];
        paquete[1] = inicio[1];
        paquete[2] = inicio[2];
        paquete[3] = inicio[3];

        // se tiene un contador que intenta meter cuantos bytes le caben al paquete
        int contador = 0;

        // llenando el paquete
        while(numeroByte < contenidoArchivo.length && contador < TAMANO_PAQUETE){
            paquete[contador+4] = contenidoArchivo[numeroByte];
            // el número Byte es el general, así se logra que nunca sobrepase
            numeroByte+=1;
            contador+=1;
        }

        return paquete;
    }

    public static void leerArchivo(){

        File archivo = new File(nombreArchivo);

        try {
            // se convierte todo el contenido a bytes
            contenidoArchivo = Files.readAllBytes(archivo.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(contenidoArchivo.length==0){
            System.out.println("EL ARCHIVO NO SE ENCONTRO O ESTA VACÍO");
        }

        // el número de paquetes es el total de bytes sobre los bytes por paquete + 1, para tener en cuenta el residuo
        numeroPaquetesTotales = (int) (Math.ceil( (double) contenidoArchivo.length/TAMANO_PAQUETE));
    }

    @Override
    public void run(){
        try {
            // se toma el tiempo de inicio
            startTime = System.currentTimeMillis();
            // al comienzo no hay ningun byte transferido
            numeroByte = 0;
            // al comienzo no hay ningun paquete transferido
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

                // tiempo transcurrido en la transferencia
                currentTime = System.currentTimeMillis();
                totalTime = currentTime - startTime;
            }

            transferenciaCompleta = true;

           

            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}