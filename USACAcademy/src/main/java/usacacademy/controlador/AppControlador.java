
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
}
