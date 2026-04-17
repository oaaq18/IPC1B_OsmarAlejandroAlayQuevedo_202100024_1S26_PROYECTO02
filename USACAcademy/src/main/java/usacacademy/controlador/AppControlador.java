
package usacacademy.controlador;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.swing.JOptionPane;

import usacacademy.Modelo.inscripcion;
import usacacademy.Modelo.listaSimple;
import usacacademy.Modelo.Usuario;



public class AppControlador {
    private listaSimple<Usuario> usuarios;
    private listaSimple<inscripcion> inscripcionesPendientes;
    private static final String ARCHIVO_DATOS = "datos.ser";
    
    public  AppControlador() {
        this.usuarios = new listaSimple<>();
        this.inscripcionesPendientes = new listaSimple<>();
        cargarUsuarios();
        //usuario admin
        if(usuarios.size() == 0)
        {
            usuarios.agregar(new Usuario.Administrador("202100024", "admin", "password","18/09/2000","H"));
        }
    }
    //---
    public void guardarDatos(){
        try(ObjectOutputStream oos= new ObjectOutputStream(new FileOutputStream(ARCHIVO_DATOS))){
            oos.writeObject(usuarios);
        
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }
    //-----
    public void cargarUsuarios(){
        File file = new File(ARCHIVO_DATOS);
        if(file.exists()){
            try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                usuarios=(listaSimple<Usuario>) ois.readObject();
            }catch(Exception ex){
                usuarios = new listaSimple<>();
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
            
            
            
        }
    }
    //------------
     public Usuario autenticar(String usuario, String password){
        for (int i = 0; i < usuarios.size(); i++) {
            Usuario u = usuarios.obtener(i);
            //validación de código y contraseña
            if(u.getCodigo().equals(usuario) && u.getPassword().equals(password))
            {
                return u; //retorna el objeto usuario
            }
        }
        return null; //credenciales incorrectas
    }
     
     public synchronized int getInscripcionesPendientes(){
        return inscripcionesPendientes.size();
    }
     
     public synchronized inscripcion procesarInscripcion()  {
        if(inscripcionesPendientes.size() > 0) {
            inscripcion ins = inscripcionesPendientes.obtener(0);
            inscripcionesPendientes.eliminar(0);
            return ins;
        }
        return null;
    }
        public void cargarCSV(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) { // Saltar header si existe
                    firstLine = false;
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    //String nombreEstudiante, 
                    //String codigoEstudiante, 
                    //String codigoSeccion, /
                    //String codigoCurso, String fecha, String semestre
                    String nombreEstudiante = parts[0].trim();
                    String codigoEstudiante = parts[1].trim();
                    String codgigoSeccion = parts[2].trim();
                    String codigoCurso = parts[3].trim();
                    String fecha = parts[4].trim();
                    String Semestre = parts[5].trim();
                    synchronized (this) {
                        inscripcionesPendientes.agregar(new inscripcion(nombreEstudiante, 
                                                            codigoEstudiante, 
                                                            codgigoSeccion,
                                                            codigoCurso,
                                                            fecha,
                                                            Semestre   ));
                                                                
                    }
                }
            }
            JOptionPane.showMessageDialog(null, "CSV cargado exitosamente.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar CSV: " + e.getMessage());
        }
    }
      
}
