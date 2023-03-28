import java.util.ArrayList;

public class Monitor extends Thread{

    public ArrayList<ConexionCliente> conexiones;

    public void registrarConexion(ConexionCliente conexion){
        conexiones.add(conexion);
    }

    @Override
    public void run(){
        while(true){
            System.out.println(new String(new char[50]).replace("\0", "\n"));
            for(int i=0; i<conexiones.size(); i++){
                ConexionCliente conexion = conexiones.get(i);
                String porcentaje = String.valueOf(Math.round((conexion.numeroPaquetesEnviados/conexion.numeroPaquetesTotales)*100));
                String imprimir = "ID_CONEXION: "+String.valueOf(conexion.idCliente)+" IP: "+conexion.direccionCliente.getHostAddress()+" PROGRESO ENVIO: "+porcentaje+" MB ENVIADOS: "+String.valueOf(conexion.numeroByte/1024/1024);
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
