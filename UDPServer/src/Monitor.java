import java.util.ArrayList;

public class Monitor extends Thread{

    public static ArrayList<ConexionCliente> conexiones = new ArrayList<>();

    public static boolean hayEnviosActivos = false;

    public static boolean modoCompatibilidad = false;

    public static synchronized void registrarConexion(ConexionCliente conexion){
        conexiones.add(conexion);
    }

    @Override
    public void run(){
        System.out.print(formatDiv("a----------------------------------------------------------------c\n"));
        System.out.print(formatRow("|                    DESCRIPCION ARCHIVO                         |\n"));
        System.out.print(formatDiv("d----------------------------------------------------------------f\n"));
        System.out.print(formatRow("| Número total de paquetes:  "+ConexionCliente.numeroPaquetesTotales)+"\n");
        System.out.print(formatDiv("d----------------------------------------------------------------f\n"));
        System.out.print(formatRow("| Bytes:  "+ConexionCliente.contenidoArchivo.length)+"\n");
        System.out.print(formatDiv("g----------------------------------------------------------------i\n"));
        System.out.print(formatDiv("a----------------------------------------------------------------c\n"));
        System.out.print(formatRow("|                  LISTA TRANSFERENCIA SERVIDOR                  |\n"));
        System.out.print(formatDiv("d----------------------------------------------------------------f\n"));
        
        while(true){
            // se llama a un método que imprime el estado de los envíos
            darEstadoEnvios();

            // se da una espera de 200ms para no actualizar siempre
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void darEstadoEnvios(){

        if(modoCompatibilidad){

            System.out.print("\n".repeat(50));
            System.out.print(formatDiv("a----------------------------------------------------------------c\n"));
            System.out.print(formatRow("| ESTADO SERVIDOR                                                |\n"));
            System.out.print(formatDiv("d----------------------------------------------------------------f\n"));
        }

        
        if(conexiones.size()==0){
            System.out.print(formatRow("| En espera de solicitudes ...                                   |\n"));
            System.out.print(formatDiv("g----------------------------------------------------------------i\n"));
        } else{
            if(hayEnviosActivos)
                System.out.print(formatRow("| Transfiriendo archivos ...                                     |\n"));
            else
                System.out.print(formatRow("| Envíos completados, en espera de solicitudes ...               |\n"));
            System.out.print(formatDiv("d----------------------------------------------------------------f\n"));
        }
        
        
        hayEnviosActivos = false;

        for(int i=0; i<conexiones.size(); i++){
            ConexionCliente conexion = conexiones.get(i);
            String porcentaje = String.valueOf( Math.ceil(conexion.numeroPaquetesEnviados*100.0/ConexionCliente.numeroPaquetesTotales));
            int cantidadBarrasProgreso = (int) ((double)conexion.numeroPaquetesEnviados*62.0/ConexionCliente.numeroPaquetesTotales);
            System.out.print(formatRow(String.format("| Conexión %03d con %-20s          Progreso: %5s |\n",conexion.idCliente,conexion.direccionCliente.getHostAddress()+":"+conexion.puertoCliente, porcentaje)));
            System.out.print(formatProgress(String.format("| %-62s |\n","-".repeat(cantidadBarrasProgreso))));
            if(!conexion.transferenciaCompleta){
                hayEnviosActivos = true;
                System.out.print(formatRow(String.format("| Paquetes: %-20s                      %10s |\n",conexion.numeroPaquetesEnviados+"/"+ConexionCliente.numeroPaquetesTotales, conexion.totalTime+"ms")));
            }
            else{
                System.out.print(formatRow(String.format("| Paquetes: %-20s                      %10s |\n","Envio completo", conexion.totalTime+"ms")));       
            }
            if(i==conexiones.size()-1)
                System.out.print(formatDiv("g----------------------------------------------------------------i\n"));
            else{
                System.out.print(formatDiv("d----------------------------------------------------------------f\n"));
            }
        }
        if(!modoCompatibilidad){
            System.out.print("\033[1A\033[1A");
            for(int i=0; i<conexiones.size(); i++){
                System.out.print("\033[1A\033[1A\033[1A\033[1A");
            }
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
