import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Month;
import java.time.Year;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;

public class UDPCliente extends Thread {

    public static final int TAMANO_PAQUETE = 512;

    private byte[] contenido = new byte[TAMANO_PAQUETE+4];

    private int numeroByte;

    public int paquetesEsperados;

    public int paq;

    public int paqRecibidos;

    public int paqPerdidos;

    public double progreso;

    public int idCliente;

    public static String hostname;

    public int puertoServidorConexion = 0;

    public static int puertoServidor;

    public static int cantidadClientes;

    public static int timeout = 5000;

    public String estado = "Solicitud";

    public long tiempoFinal=0;

    public long startTime=0;

    public String logFile="";

    public static void main(String[] args) throws Exception {    
        // inicio
        inicio_rapido();
        new Monitor().start();
        // se inicia la cantidad del clientes deseada
        for(int i=0; i<cantidadClientes; i++){
            UDPCliente cliente = new UDPCliente(i);
            Monitor.asignarCliente(cliente);
            cliente.start();
        }
    }

    public UDPCliente(int id){
        this.idCliente = id;
    }

    @Override
    public void run(){

        //inicio();
        // establece el host y el puerto
        

        // contador de paquetes realmente recibidos
        int i = 0;

        // variable para indicar si termino
        boolean termino=false;

        startTime = System.currentTimeMillis();

        try {
            // saca la dirección del host
            InetAddress direccion = InetAddress.getByName(hostname);

            // abre el datagram socket
            DatagramSocket socket = new DatagramSocket();

            // establece el timeout
            socket.setSoTimeout(timeout);

            // se envía la solicitud de conexion
            DatagramPacket request = new DatagramPacket(new byte[1], 1, direccion, puertoServidor);
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
                    puertoServidorConexion = response.getPort();
                    // conociendo el tamaño del paquete, y como el primer paquete es el numero de paquetes total
                    contenido = new byte[TAMANO_PAQUETE*paq];
                    numeroByte = 0;
                    // mas 1 porque se entrega ya restándole el paquete que se envió
                    paquetesEsperados = paq+1;

                    estado="Transferencia";
                    
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
                // como paq es el inverso
                paqPerdidos = (paquetesEsperados - paq ) - (paqRecibidos);
                progreso = ((double) paquetesEsperados - paq ) / paquetesEsperados;
            }

            estado="Termino";


            socket.close();
 
        } catch (SocketTimeoutException ex) {
            
            estado="Timeout";
            
        } catch (IOException ex) {
            estado="Error";
        }

        tiempoFinal = System.currentTimeMillis() - startTime;


        try {
            try{
                Files.createDirectories(Paths.get("/ArchivosRecibidos/"));
            }
            catch(Exception e){
                // directorio ya existia
            }

            FileOutputStream fos = new FileOutputStream("/ArchivosRecibidos/Cliente"+idCliente+"-Prueba"+cantidadClientes+".txt");
            fos.write(contenido);
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void log(String log){
        int year = Year.now().getValue();
        Date date = new Date();   
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date); 
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int second = calendar.get(Calendar.SECOND);
        int minute = calendar.get(Calendar.MINUTE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);       
        int month =calendar.get(Calendar.MONTH)+1;  
        String prefix = year+"-"+month+"-"+day+"-"+hour+"-"+minute+"-"+second;

        if(logFile.equals("")){
            try {
                try{
                    Files.createDirectories(Paths.get("/Logs/"));
                }
                catch(Exception e){
                    // si el directorio ya esta creado
                }
                
                logFile = "/Logs/"+prefix+"-log.txt"; 
                File file = new File(logFile);
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            BufferedWriter output = new BufferedWriter(new FileWriter(logFile, true));
            output.newLine();
            output.write("["+prefix+"] "+log);
            output.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /* METODOS PARA IMPRESION */

    public static String formatDiv(String str)
    {
        return str.replace('a', '\u250c')
                .replace('b', '\u252c')
                .replace('c', '\u2510')
                .replace('d', '\u251c')
                .replace('e', '\u253c')
                .replace('f', '\u2524')
                .replace('g', '\u2514')
                .replace('h', '\u2534')
                .replace('i', '\u2518')
                .replace('-', '\u2500');
    }

    public static void inicio_rapido(){
        hostname = "localhost";
        puertoServidor = 6000;
        cantidadClientes = 10;
        timeout = 6000;
        Monitor.modoCompatibilidad=true;
    }

    public long tiempoTotal(){
        if(estado.equals("Transferencia")||estado.equals("Solicitud")){
            return System.currentTimeMillis() - startTime;
        }
        return tiempoFinal;
    }

    public static void inicio(){
        String header = "\n";
        header += formatDiv("a----------------------------------------------------------------c\n");
        header += formatRow("|                        LABORATORIO 3                           |\n");
        header += formatRow("|              Implementación de Servidores TCP y UDP            |\n");
        header += formatRow("|                     Profesor: Yezid Donoso                     |\n");
        header += formatRow("|                 Asistente Graduado: Nicolás Segura             |\n");
        header += formatDiv("d----------------------------------------------------------------f\n");
        header += formatRow("|                          CLIENTE UDP                           |\n");
        header += formatDiv("d--------------------------------b-------------------------------f\n");
        header += formatRow("|           GRUPO 1              |           SECCION 5           |\n");
        header += formatDiv("g--------------------------------h-------------------------------i\n");     
        System.out.println(header);

        String datos = "";
        datos += formatDiv("a----------------------------------------------------------------c\n");
        datos += formatRow("| INGRESE LOS PARÁMETROS DE CONFIGURACION                        |\n");
        datos += formatDiv("d----------------------------------------------------------------f\n");
        System.out.print(datos);

        
        System.out.print(formatRow("| Puerto Servidor:  "));
        Scanner sc = new Scanner(System.in);
        puertoServidor = Integer.parseInt(sc.nextLine());
        
        System.out.print(formatDiv("d----------------------------------------------------------------f\n"));

        System.out.print(formatRow("| Hostname (IP): "));
        hostname=sc.nextLine();

        System.out.print(formatDiv("d----------------------------------------------------------------f\n"));

        System.out.print(formatRow("| Numero clientes: "));
        cantidadClientes=Integer.parseInt(sc.nextLine());

        System.out.print(formatDiv("d----------------------------------------------------------------f\n"));

        System.out.print(formatRow("| Timeout: "));
        timeout=Integer.parseInt(sc.nextLine());

        System.out.print(formatDiv("d----------------------------------------------------------------f\n"));

        System.out.print(formatRow("|                   Modo compatibilidad mensajes                 |\n"));
        System.out.print(formatRow("|        (activar cuando los progresos no se ven correctamente)  |\n"));
        System.out.print(formatDiv("d----------------------------------------------------------------f\n"));
        System.out.print(formatRow("| (S/N): "));
        String compat = sc.nextLine();
        if(compat.equals("S")){
            Monitor.modoCompatibilidad=true;
        }
        System.out.print(formatDiv("g----------------------------------------------------------------i\n\n")); 
        
        sc.close();

    }

    public static String formatRow(String str)
    {
        return str.replace('|', '\u2502');
    }

    public static String formatProgress(String str)
    {
        return str.replace('|', '\u2502')
               .replace('-', '\u2588');
    }

}
