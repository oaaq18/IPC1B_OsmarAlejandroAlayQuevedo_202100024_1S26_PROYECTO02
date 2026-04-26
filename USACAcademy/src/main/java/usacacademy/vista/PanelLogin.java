package usacacademy.vista;
import usacacademy.Modelo.Usuario;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import usacacademy.controlador.Bitacora;

public class PanelLogin extends JPanel{
    private JTextField usuario;
    private JPasswordField password;
    private JButton boton;
    private VistaPrincipal ventanaPrincipal;
    public PanelLogin(VistaPrincipal ventanaPrincipal) {
        this.ventanaPrincipal = ventanaPrincipal;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        initComponents();
    }
     private void initComponents() {
        //Título
        JLabel lblTitulo = new JLabel("Sancarlista Academy - Login:", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 18));
        lblTitulo.setAlignmentX(CENTER_ALIGNMENT);
        add(lblTitulo);
        add(Box.createVerticalStrut(20)); // Espacio

        //Panel para código
        JPanel panelCodigo = new JPanel(new GridLayout(1, 2, 5, 5));
        panelCodigo.add(new JLabel("Código:"));
        usuario = new JTextField(10);
        panelCodigo.add(usuario);
        add(panelCodigo);
        add(Box.createVerticalStrut(10));

        //Panel para contraseña
        JPanel panelPassword = new JPanel(new GridLayout(1, 2, 5, 5));
        panelPassword.add(new JLabel("Contraseña:"));
        password = new JPasswordField(10);
        panelPassword.add(password);
        add(panelPassword);
        add(Box.createVerticalStrut(20));

        //Botón de ingreso
        boton = new JButton("Ingresar");
        boton.setAlignmentX(CENTER_ALIGNMENT);
        add(boton);

        //Evento click
        boton.addActionListener(e -> ejecutarLogin());
    }
      private void ejecutarLogin() {
        String user = usuario.getText();
        String pass = String.valueOf(password.getPassword());

        //Aquí se llama al controlador para validar las credenciales

        Usuario usuario = ventanaPrincipal.getControlador().autenticar(user, pass);

        if(usuario != null) {
            //redireccion según el rol
        if(usuario instanceof Usuario.Administrador) {
            JOptionPane.showMessageDialog(this, "Ingresando como administrador");
            ventanaPrincipal.cambiarVista("Administrador");
            
        }else if (usuario instanceof Usuario.Instructor){
            JOptionPane.showMessageDialog(this, "Ingresando como instructor");
            ventanaPrincipal.setUsuarioActual(usuario);
            ventanaPrincipal.cambiarVista("Instructor");
        }else if(usuario instanceof Usuario.Estudiante){
            JOptionPane.showMessageDialog(this, "Ingresando como estudiante");
            ventanaPrincipal.setUsuarioActual(usuario);
            ventanaPrincipal.cambiarVista("Estudiante");
        }
            
        }else {
            //manejo de errores visuales
            JOptionPane.showMessageDialog(this, "Codigo o contraseña incorrectos");
            Bitacora.registrar("DESCONOCIDO", "null", "LOGIN", "Credenciales incorrectas");
        }
    }
    
}
