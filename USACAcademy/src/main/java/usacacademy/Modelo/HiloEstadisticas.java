package usacacademy.Modelo;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import usacacademy.controlador.AppControlador;
//hilo que genera estadisticas academicas cada 15 s

public class HiloEstadisticas implements Runnable{
    private JTextArea areaConsola;
    private AppControlador controlador;
    private  boolean activo;

    public HiloEstadisticas(JTextArea areaConsola, AppControlador controlador) {
        this.areaConsola = areaConsola;
        this.controlador = controlador;
        this.activo = true;
    }
     public void detener() {
        this.activo = false;
    }
    @Override
    public void run() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
 
        while (activo) {
            try {
                int cantidad   = controlador.getUsuariosActivos();
                String ts      = sdf.format(new Date());
                String mensaje = "[Thread-Sesiones] Usuarios Activos: " + cantidad
                               + " - Última actividad: [" + ts + "]\n";
 
                SwingUtilities.invokeLater(() -> {
                    areaConsola.append(mensaje);
                    areaConsola.setCaretPosition(areaConsola.getDocument().getLength());
                });
 
                Thread.sleep(10_000); // 10 segundos
 
            } catch (InterruptedException e) {
                break; // salida limpia cuando detener() llama a interrupt()
            }
        }
    }
    
}
