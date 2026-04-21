
package usacacademy.Modelo;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import usacacademy.controlador.AppControlador;


public class HiloSesiones implements  Runnable{
    private  boolean activo; 
    private JTextArea areaConsola;
    private AppControlador controlador;

    public HiloSesiones(JTextArea areaConsola, AppControlador controlador) {
        this.activo = true;
        this.areaConsola = areaConsola;
        this.controlador = controlador;
    }
    //Detiene el hilo 
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
