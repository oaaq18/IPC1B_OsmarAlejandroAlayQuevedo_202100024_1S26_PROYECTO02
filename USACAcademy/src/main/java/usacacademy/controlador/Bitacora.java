package usacacademy.controlador;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Bitacora {

    private static final String ARCHIVO = "bitacora.txt";

    // metodo estatico, se llama directo sin instanciar
    // Bitacora.registrar("ADMINISTRADOR", "admin", "LOGIN", "Inicio de sesion exitoso");
    public static void registrar(String tipoUsuario, String codigoUsuario, String operacion, String descripcion) {
        String fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
        String linea = "[" + fecha + "] | " + tipoUsuario + " | " + codigoUsuario + " | " + operacion + " | " + descripcion;
        
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO, true))) {
            // true = append, no sobreescribe
            pw.println(linea);
        } catch (Exception e) {
            System.out.println("Error bitacora: " + e.getMessage());
        }
    }
}
