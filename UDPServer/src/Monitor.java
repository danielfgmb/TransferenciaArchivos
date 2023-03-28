import java.util.ArrayList;

public class Monitor extends Thread{

    public ArrayList<ConexionCliente> conexiones = new ArrayList<>();

    public void registrarConexion(ConexionCliente conexion){
        conexiones.add(conexion);
    }

    @Override
    public void run(){
        while(true){
            System.out.println(new String(new char[50]).replace("\0", "\n"));
            System.out.println("SERVIDOR ACTIVO");
            System.out.println("PAQUETES TOTALES: "+ConexionCliente.numeroPaquetesTotales+"| BYTES: "+ConexionCliente.contenidoArchivo.length);
            for(int i=0; i<conexiones.size(); i++){
                ConexionCliente conexion = conexiones.get(i);
                String porcentaje = String.valueOf( Math.ceil(conexion.numeroPaquetesEnviados*100.0/ConexionCliente.numeroPaquetesTotales));
                String imprimir = "ID: "+String.valueOf(conexion.idCliente)+"| IP: "+conexion.direccionCliente.getHostAddress()+"| PUERTO: "+String.valueOf(conexion.puertoCliente)+"| PROGRESO: "+porcentaje+"% | B: "+String.valueOf(conexion.numeroByte)+"| PAQ: "+String.valueOf(conexion.numeroPaquetesEnviados);
                System.out.println(imprimir);
                
            }
            try {
                sleep(200);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
