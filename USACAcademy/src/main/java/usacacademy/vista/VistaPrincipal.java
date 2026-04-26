package usacacademy.vista;
import usacacademy.Modelo.Usuario;
import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import usacacademy.controlador.AppControlador;
import usacacademy.controlador.Bitacora;


public class VistaPrincipal extends JFrame{
    private AppControlador controlador;
    private CardLayout cardLayout;
    private JPanel panelPrincipal;
    private PanelAdministrador panelAdministrador;
    private PanelInstructor panelInstructor; // variable de clase
    private PanelEstudiante panelEstudiante;
    private Usuario usuarioActual; // guardar quien inicio sesion
  
    public VistaPrincipal() {
        this.controlador = new AppControlador();
        this.cardLayout = new CardLayout();
        this.panelPrincipal = new JPanel(cardLayout);

        panelAdministrador = new PanelAdministrador(this);
        panelInstructor    = new PanelInstructor(this); // asignar a la variable de clase
        panelEstudiante = new PanelEstudiante(this);
        PanelLogin panelLogin = new PanelLogin(this);

        panelPrincipal.add(panelLogin,  "Login");
        panelPrincipal.add(panelAdministrador, "Administrador");
        panelPrincipal.add(panelInstructor, "Instructor");
        panelPrincipal.add(panelEstudiante, "Estudiante");

        add(panelPrincipal);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
        public void cambiarVista(String nombreVista) {
        cardLayout.show(panelPrincipal, nombreVista);
        if ("Administrador".equals(nombreVista)) {
            Bitacora.registrar("ADMINISTRADOR", "admin", "LOGIN", "Inicio de sesion exitoso");
            panelAdministrador.iniciarHilos();
        }
        if ("Instructor".equals(nombreVista)) {
            // le pasa el instructor que inicio sesion
            panelInstructor.setInstructor((Usuario.Instructor) usuarioActual);
            Bitacora.registrar("INSTRUCTOR", usuarioActual.getCodigo(), "LOGIN", "Inicio de sesion exitoso");
        }
        if("Estudiante".equals(nombreVista)){
            Bitacora.registrar("ESTUDIANTE", usuarioActual.getCodigo(), "LOGIN", "Inicio de sesion exitoso");
            panelEstudiante.setEstudiante((Usuario.Estudiante)usuarioActual);
        }
    }
    public void setUsuarioActual(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    public AppControlador getControlador(){
        return controlador;
    }

    

}
