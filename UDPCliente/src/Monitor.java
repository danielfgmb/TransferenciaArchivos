import java.util.ArrayList;

public class Monitor extends Thread{

    public static boolean modoCompatibilidad = false; 

    public static boolean hayEnviosActivos = false;

    private static ArrayList<UDPCliente> clientes = new ArrayList<>();

    public static void asignarCliente(UDPCliente cliente){
        clientes.add(cliente);
    }

    @Override
    public void run(){
        System.out.print(formatDiv("a----------------------------------------------------------------c\n"));
        System.out.print(formatRow("|                  LISTA TRANSFERENCIA CLIENTES                  |\n"));
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
            System.out.print(formatRow("| ESTADO CLIENTES                                                |\n"));
            System.out.print(formatDiv("d----------------------------------------------------------------f\n"));
        }

        

        if(hayEnviosActivos)
            System.out.print(formatRow("| Transfiriendo archivos ...                                     |\n"));
        else
            System.out.print(formatRow("| Envíos completados o en espera de respuesta ...                |\n"));
        System.out.print(formatDiv("d----------------------------------------------------------------f\n"));
        
        
        
        hayEnviosActivos = false;

        for(int i=0; i<clientes.size(); i++){

            UDPCliente cliente = clientes.get(i);
            int porcentaje =  (int) ((double) cliente.progreso*100);
            int cantidadBarrasProgreso = (int) Math.ceil((double) cliente.progreso*62);
            System.out.print(formatRow(String.format("| Conexión %03d con %-20s          Progreso: %5s |\n",cliente.idCliente,UDPCliente.hostname+":"+cliente.puertoServidorConexion, porcentaje)));
            System.out.print(formatProgress(String.format("| %-62s |\n","-".repeat(cantidadBarrasProgreso))));
            System.out.print(formatRow(String.format("| Paquetes: %-20s                      %10s |\n",cliente.paqRecibidos+"/"+cliente.paquetesEsperados, cliente.tiempoTotal()+"ms")));

            double porcentajePerdida = Math.round(100 *((double) cliente.paqPerdidos)*100/cliente.paquetesEsperados)/100;

            if(cliente.estado.equals("Transferencia")){
                hayEnviosActivos = true;
                System.out.print(formatRow(String.format("| Paquetes Perdidos: %-20s                 Activa  |\n",cliente.paqPerdidos+" "+ porcentajePerdida)));
            }
            else if(cliente.estado.equals("Solicitud")){
                System.out.print(formatRow(String.format("| Solicitud de conexión enviada ...                              |\n")));
            }
            else if(cliente.estado.equals("Termino")){
                System.out.print(formatRow(String.format("| Paquetes Perdidos: %-20s             Finalizada |\n",cliente.paqPerdidos+" "+porcentajePerdida)));
            }
            else if(cliente.estado.equals("Timeout")){
                System.out.print(formatRow(String.format("| Paquetes Perdidos: %-20s          Error Timeout |\n",cliente.paqPerdidos+" "+porcentajePerdida)));
            }
            else{
                System.out.print(formatRow(String.format("| Paquetes Perdidos: %-20s      Error - Cancelado |\n",cliente.paqPerdidos+" "+porcentajePerdida)));
            }


            if(i==clientes.size()-1)
                System.out.print(formatDiv("g----------------------------------------------------------------i\n"));
            else{
                System.out.print(formatDiv("d----------------------------------------------------------------f\n"));
            }
        }
        if(!modoCompatibilidad){
            System.out.print("\033[1A\033[1A");
            for(int i=0; i<clientes.size(); i++){
                System.out.print("\033[1A\033[1A\033[1A\033[1A\033[1A");
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
