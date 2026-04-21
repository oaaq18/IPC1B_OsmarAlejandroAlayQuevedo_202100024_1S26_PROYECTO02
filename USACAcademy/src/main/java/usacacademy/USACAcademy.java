
package usacacademy;
import usacacademy.vista.VistaPrincipal;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class USACAcademy {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Opcional: Ajustar el aspecto visual al del sistema operativo
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                // Iniciamos la ventana principal
                // Esta a su vez crea el AppController y carga los datos serializados
                new VistaPrincipal();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
