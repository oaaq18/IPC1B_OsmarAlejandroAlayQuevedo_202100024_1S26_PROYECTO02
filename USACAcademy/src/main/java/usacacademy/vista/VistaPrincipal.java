package usacacademy.vista;
import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import usacacademy.controlador.AppControlador;

public class VistaPrincipal extends JFrame{
    private AppControlador controlador;
    private CardLayout cardLayout; 
    private JPanel panelPrincipal;
    private PanelAdministrador panelAdministrador;
    
    public VistaPrincipal(){
        this.controlador = new AppControlador();

        //Configuración de la ventana
        this.cardLayout = new CardLayout();
        this.panelPrincipal = new JPanel(cardLayout);
        //creando paneles
        panelAdministrador = new PanelAdministrador(this);
        PanelLogin panelLogin = new PanelLogin(this);
        // Agregar al contenedor correcto (panelPrincipal)
        panelPrincipal.add(panelLogin, "Login");
        panelPrincipal.add(panelAdministrador, "Administrador");
        //acá les faltaría a ustedes agregar los demás paneles


        add(panelPrincipal);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

    }
    public void cambiarVista(String nombreVista)
    {
        cardLayout.show(panelPrincipal, nombreVista);
        if("Administrador".equals(nombreVista)) {
            panelAdministrador.iniciarHilos();
        }
    }

    public AppControlador getControlador()
    {
        return controlador;
    }

    

}
