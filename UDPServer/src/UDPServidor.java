import java.net.*;
import java.util.Scanner;

public class UDPServidor {

    public static int numeroPuerto;


    public static void main(String[] args) throws Exception {

        //inicio();
        inicio_rapido();

        ConexionCliente.leerArchivo();

        DatagramSocket socketLlegadaClientes = new DatagramSocket(numeroPuerto);

        new Monitor().start();
        
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

                conexion.log("Request de "+direccionCliente+":"+puertoCliente+" recibida, iniciando conexion.");
                conexion.log("Asignando ID Cliente "+conexion.idCliente);
                Monitor.registrarConexion(conexion);
                conexion.start();
                

            }
        } catch(Exception e){
            e.printStackTrace();
            socketLlegadaClientes.close();
        }

        socketLlegadaClientes.close();
    }

    public static void inicio_rapido(){
        ConexionCliente.setNombreArchivo("C:\\Users\\dgomezba\\Videos\\views\\2022-08-23 19-43-40.mkv");
        Monitor.modoCompatibilidad=true;
        numeroPuerto = 6000;
        //ConexionCliente.setNombreArchivo("C:\\Users\\dgomezba\\Videos\\2022-08-23 15-48-11.mkv");
    }

    public static void inicio(){
        String header = "\n";
        header += formatDiv("a----------------------------------------------------------------c\n");
        header += formatRow("|                        LABORATORIO 3                           |\n");
        header += formatRow("|              Implementación de Servidores TCP y UDP            |\n");
        header += formatRow("|                     Profesor: Yezid Donoso                     |\n");
        header += formatRow("|                 Asistente Graduado: Nicolás Segura             |\n");
        header += formatDiv("d----------------------------------------------------------------f\n");
        header += formatRow("|                          SERVIDOR UDP                          |\n");
        header += formatDiv("d--------------------------------b-------------------------------f\n");
        header += formatRow("|           GRUPO 1              |           SECCION 5           |\n");
        header += formatDiv("g--------------------------------h-------------------------------i\n");     
        System.out.println(header);

        String datos = "";
        datos += formatDiv("a----------------------------------------------------------------c\n");
        datos += formatRow("| INGRESE LOS PARÁMETROS DE CONFIGURACION                        |\n");
        datos += formatDiv("d----------------------------------------------------------------f\n");
        System.out.print(datos);

        
        System.out.print(formatRow("| Puerto Conexiones Entrantes Servidor:  "));
        Scanner sc = new Scanner(System.in);
        numeroPuerto = Integer.parseInt(sc.nextLine());
        System.out.print(formatDiv("d----------------------------------------------------------------f\n"));

        System.out.print(formatRow("|                   Modo compatibilidad mensajes                 |\n"));
        System.out.print(formatRow("|        (activar cuando los progresos no se ven correctamente)  |\n"));
        System.out.print(formatDiv("d----------------------------------------------------------------f\n"));
        System.out.print(formatRow("| (S/N): "));
        String compat = sc.nextLine();
        if(compat.equals("S")){
            Monitor.modoCompatibilidad=true;
        }
        System.out.print(formatDiv("d----------------------------------------------------------------f\n"));

        System.out.print(formatRow("| Dirección absoluta archivo transmisión:                        |\n"));
        System.out.print(formatRow("| "));
        ConexionCliente.setNombreArchivo(sc.nextLine());
        System.out.print(formatDiv("g----------------------------------------------------------------i\n\n"));    
        sc.close();
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

    public static String formatRow(String str)
    {
        return str.replace('|', '\u2502');
    }
}


