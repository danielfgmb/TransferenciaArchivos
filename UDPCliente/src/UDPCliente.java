import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;

public class UDPCliente extends Thread {

    public static final int TAMANO_PAQUETE = 512;

    private byte[] contenido = new byte[TAMANO_PAQUETE+4];

    private int numeroByte;

    private int paquetesEsperados;

    private int paq;

    private int paqRecibidos;

    private int idCliente;
    public static void main(String[] args) throws Exception {        
        // se inicia la cantidad del clientes deseada
        for(int i=0; i<2; i++){
            new UDPCliente(i).start();
        }
    }

    public UDPCliente(int id){
        this.idCliente = id;
    }

    @Override
    public void run(){

        //inicio();
        // establece el host y el puerto
        String hostname = "localhost";
        int port = 6000;

        // contador de paquetes realmente recibidos
        int i = 0;

        // variable para indicar si termino
        boolean termino=false;

        try {
            // saca la dirección del host
            InetAddress address = InetAddress.getByName(hostname);

            // abre el datagram socket
            DatagramSocket socket = new DatagramSocket();

            // establece el timeout
            socket.setSoTimeout(1000);

            // se envía la solicitud de conexion
            DatagramPacket request = new DatagramPacket(new byte[1], 1, address, port);
            socket.send(request);
 
            // se reciben paquetes hasta que finalice el archivo de acuerdo con el numero de paquetes esperados
            while (!termino) {
                // tamaño de paquete que debe recibir + 4 bytes del número de paquete
                byte[] buffer = new byte[TAMANO_PAQUETE+4];
                // 4 bytes del numero de paquete
                byte[] numeroPaquete = new byte[4];

                // recibe el paquete desde el socket
                DatagramPacket response = new DatagramPacket(buffer, buffer.length);
                socket.receive(response);

                // se extraen los primero 4 bytes para identificar el número de paquete inverso
                numeroPaquete[0] = buffer[0];
                numeroPaquete[1] = buffer[1];
                numeroPaquete[2] = buffer[2];
                numeroPaquete[3] = buffer[3];

                paq = ByteBuffer.wrap(numeroPaquete).getInt();

                if(i==0){
                    // conociendo el tamaño del paquete, y como el primer paquete es el numero de paquetes total
                    contenido = new byte[TAMANO_PAQUETE*paq];
                    numeroByte = 0;
                    // mas 1 porque se entrega ya restándole el paquete que se envió
                    paquetesEsperados = paq+1;
                    
                }

                // si el numero de paquete es 0 quiere decir que es el ultimo
                if(paq==0){
                    termino=true;
                }

                // pasando cada paquete a un solo array de bytes con el contenido
                for(int nByte=4; nByte<buffer.length && numeroByte<contenido.length; nByte++){
                    contenido[numeroByte]=buffer[nByte];
                    numeroByte+=1;
                }

                // se sincrementa el numero de paquetes recibidos
                i+=1;
                paqRecibidos=i;
            }
 
        } catch (SocketTimeoutException ex) {
            System.out.println("Time out error");
        } catch (IOException ex) {
            System.out.println("Client error: " + ex.getMessage());
            ex.printStackTrace();
        }

        try {
            FileOutputStream fos = new FileOutputStream("Cliente"+idCliente+".txt");
            fos.write(contenido);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
