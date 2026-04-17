
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
import usacacademy.Modelo.Curso;
import usacacademy.Modelo.Seccion;
import usacacademy.Modelo.Nota;


public class AppControlador {
    //-------------------------listas principales
    private listaSimple<Usuario> usuarios;
    private listaSimple<inscripcion> inscripcionesPendientes;
    private listaSimple<Curso>       cursos;
    private listaSimple<Seccion>     secciones;
    private listaSimple<Nota>        notas;
    //------------archivos.ser ej.  private static final String ARCHIVO_DATOS = "datos.ser";
    private static final String ARCHIVO_USUARIOS   = "usuarios.ser";
    private static final String ARCHIVO_CURSOS     = "cursos.ser";
    private static final String ARCHIVO_SECCIONES  = "secciones.ser";
    private static final String ARCHIVO_NOTAS      = "notas.ser";
   
    
    public  AppControlador() {
        this.usuarios                = new listaSimple<>();
        this.cursos                  = new listaSimple<>();
        this.secciones               = new listaSimple<>();
        this.notas                   = new listaSimple<>();
        this.inscripcionesPendientes = new listaSimple<>();
        
        //cargar los datos.ser
        cargarArchivos();
        
        //usuario admin
        if(usuarios.size() == 0)
        {
            usuarios.agregar(new Usuario.Administrador("202100024", "admin", "password","18/09/2000","H"));
        }
    }
    
    //--------------------METODOS DE GUARDAR------------
    ///guardar lista de los usuarios
    public void guardarUsuarios() {
        guardarArchivo(ARCHIVO_USUARIOS, usuarios);
    }
    // guardar cursos.ser
       public void guardarCursos() {
        guardarArchivo(ARCHIVO_CURSOS, cursos);
    }
    //guaredarsecciones.ser
    public void guardarSecciones() {
        guardarArchivo(ARCHIVO_SECCIONES, secciones);
    }
    //guardar notas.ser
     public void guardarNotas() {
        guardarArchivo(ARCHIVO_NOTAS, notas);
    }
    //metodo para guardar los archivos
    private void guardarArchivo(String archivo, Object objeto) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(archivo))) {
            oos.writeObject(objeto);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                "Error al guardar " + archivo + ": " + ex.getMessage());
        }
    }
    // metodo para cargar los datos de los archivos.ser
    
     private void cargarArchivos() {
        usuarios   = cargarArchivo(ARCHIVO_USUARIOS,  usuarios);
        cursos     = cargarArchivo(ARCHIVO_CURSOS,    cursos);
        secciones  = cargarArchivo(ARCHIVO_SECCIONES, secciones);
        notas      = cargarArchivo(ARCHIVO_NOTAS,     notas);
    }
     
    //------------METODO PARA CARGAR .SER
     
    private <T> listaSimple<T> cargarArchivo(String archivo, listaSimple<T> listaDefault) {
        File file = new File(archivo);
        if (!file.exists()) return listaDefault;
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(file))) {
            return (listaSimple<T>) ois.readObject();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                "Error al cargar " + archivo + ": " + ex.getMessage());
            return listaDefault;
        }
    }
     /*------------AUTENTICACION ------------------------------------
     buscar el usuario por codigo y validar contrase;a, retorna el objeto usuario si coincide y nill si no   
     */
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
     
     //---------------CRUD DE USUARIOS----------------------------------
    //------------------------------------------------------------------
    //metodo que devuelve la lsita de los usuarios
     public listaSimple<Usuario> getUsuarios() { return usuarios; }
     //metodo para agregar usuario
    public boolean agregarUsuario(Usuario usuario) {
        // Validar que el cOdigo sea Unico
        if (buscarUsuario(usuario.getCodigo()) != null) { // caso falso
            JOptionPane.showMessageDialog(null,
                "ERROR: el codigo " + usuario.getCodigo() + " ya existe.");
            return false;
        }
        //caso verdadero(codigo disponible)
        usuarios.agregar(usuario);
        guardarUsuarios();
        return true;
    }
    //buscar usuario mediante si codigo y retornar el usuario si es econtrado o null en caso contrario
    public Usuario buscarUsuario(String codigo) {
        for (int i = 0; i < usuarios.size(); i++) {
            Usuario u = usuarios.obtener(i);
            if (u.getCodigo().equals(codigo)) return u;
        }
        return null;
    }
    //metodo para aclizar nombre o contrase;a de un usuario, retorna true si se actualizo o false si no se econtro
    public boolean actualizarUsuario(String codigo, String nuevoNombre, String nuevaPassword) {
        Usuario u = buscarUsuario(codigo);
        if (u == null) {
            JOptionPane.showMessageDialog(null, "Usuario no encontrado");
            return false;
        }
        //realizando las modificaciones en caso de ser econtradop
        if (nuevoNombre  != null && !nuevoNombre.isEmpty())  u.setNombre(nuevoNombre);
        if (nuevaPassword != null && !nuevaPassword.isEmpty()) u.setPassword(nuevaPassword);
        guardarUsuarios();
        return true;
    }
    //metodo para eliminar usuario por codigo
    public boolean eliminarUsuario(String codigo) {
        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.obtener(i).getCodigo().equals(codigo)) {
                usuarios.eliminar(i);// se llam al metodo alojado en lista simple
                guardarUsuarios();
                return true;
            }
        }
        JOptionPane.showMessageDialog(null, "Usuario no encontrado.");
        return false;
    }
    //---------------FIN CRUD DE USUARIOS----------------------------------
    //------------------------------------------------------------------
    
    //---------------CRUD DE CURSOS----------------------------------
    //------------------------------------------------------------------
    //metodo para retornar lista de los codigos
     public listaSimple<Curso> getCursos() { return cursos; }
     
    // AGREGAR CUARSO CALIDANDO QUE SU CODIGO SEA UNICO
        public boolean agregarCurso(Curso curso) {
        if (buscarCurso(curso.getCodigo()) != null) {
            JOptionPane.showMessageDialog(null,
                "El código de curso " + curso.getCodigo() + " ya existe.");
            return false;
        }
        cursos.agregar(curso);
        guardarCursos();
        return true;
    }
    //MOTODO PARA BUSACAR UN CURSO MEDIANTE SU CODIGO, RETORNA EL OBJ CURSO SI ES ECONTRADO O NULL SINO
    public Curso buscarCurso(String codigo) {
        for (int i = 0; i < cursos.size(); i++) {
            Curso c = cursos.obtener(i);
            if (c.getCodigo().equals(codigo)) return c;
        }
        return null;
    }
    //METODO PARA ACTUALIZAR CURSO(nombre descripcion, creditos  seccion)
    //retorna true si fue posible o false si no 
    public boolean actualizarCurso(String codigo, String nuevoNombre,
        String nuevaDesc, int nuevosCreditos, String nuevaSeccion) {
        Curso curso = buscarCurso(codigo);
        if (curso == null) {
            JOptionPane.showMessageDialog(null, "Curso no encontrado.");
            return false;
        }
        if (nuevoNombre   != null && !nuevoNombre.isEmpty())   curso.setNombre(nuevoNombre);
        if (nuevaDesc     != null && !nuevaDesc.isEmpty())     curso.setDescripcion(nuevaDesc);
        if (nuevosCreditos > 0)                                  curso.setCreditos(nuevosCreditos);
        if (nuevaSeccion  != null && !nuevaSeccion.isEmpty())  curso.setSeccion(nuevaSeccion);
        guardarCursos();
        return true;
    }
    //metodo para eliminar curso mediante su codigo
    public boolean eliminarCurso(String codigo) {
        for (int i = 0; i < cursos.size(); i++) {
            if (cursos.obtener(i).getCodigo().equals(codigo)) {
                cursos.eliminar(i);
                guardarCursos();
                return true;
            }
        }
        JOptionPane.showMessageDialog(null, "Curso no encontrado.");
        return false;
    }
    
    //---------------FIN CRUD DE USUARIOS----------------------------------
    //------------------------------------------------------------------
    
    
     //------------------------------------------------------------------
    //-----------------CARGAR ARCHIVOS-----------------------------------
    //-------------------------------------------------------------------
      public void cargarUsuariosCSV(String path, String rol) {
        int cargados = 0, errores = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String linea;
            boolean primera = true;
            while ((linea = br.readLine()) != null) {
                if (primera) { primera = false; continue; } // saltar header
                String[] p = linea.split(",");
                if (p.length < 5) { errores++; continue; }
                String codigo   = p[0].trim();
                String nombre   = p[1].trim();
                String fecha    = p[2].trim();
                String genero   = p[3].trim();
                String password = p[4].trim();
                // Validar que el código no esté duplicado
                if (buscarUsuario(codigo) != null) { errores++; continue; }
                // Crear el usuario según el rol
                if (rol.equals("INSTRUCTOR")) {
                    usuarios.agregar(new Usuario.Instructor(codigo, nombre, password, fecha, genero));
                } else {
                    usuarios.agregar(new Usuario.Estudiante(codigo, nombre, password, fecha, genero));
                }
                cargados++;
            }
            guardarUsuarios();
            JOptionPane.showMessageDialog(null,
                "CSV cargado: " + cargados + " registros exitosos, " + errores + " errores.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al leer CSV: " + e.getMessage());
        }
    }
      
    public void cargarCursosCSV(String path) {
        int cargados = 0, errores = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String linea;
            boolean primera = true;
            while ((linea = br.readLine()) != null) {
                if (primera) { primera = false; continue; }
                String[] p = linea.split(",");
                if (p.length < 5) { errores++; continue; }
                String codigo      = p[0].trim();
                String nombre      = p[1].trim();
                String descripcion = p[2].trim();
                int    creditos   = Integer.parseInt(p[3].trim());
                String seccion     = p[4].trim();
                if (buscarCurso(codigo) != null) { errores++; continue; }
                cursos.agregar(new Curso(codigo, nombre, descripcion, creditos, seccion));
                cargados++;
            }
            guardarCursos();
            JOptionPane.showMessageDialog(null,
                "CSV cargado: " + cargados + " registros exitosos, " + errores + " errores.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al leer CSV: " + e.getMessage());
        }
    }
    public void cargarInscripcionesCSV(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String linea;
            boolean primera = true;
            while ((linea = br.readLine()) != null) {
                if (primera) { primera = false; continue; }
                String[] p = linea.split(",");
                if (p.length < 6) continue;
                synchronized (this) {
                    inscripcionesPendientes.agregar(new inscripcion(
                        p[0].trim(), p[1].trim(), p[2].trim(),
                        p[3].trim(), p[4].trim(), p[5].trim()
                    ));
                }
            }
            JOptionPane.showMessageDialog(null, "Inscripciones CSV cargadas.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al leer CSV: " + e.getMessage());
        }
    }
    
    //--------------------------------------------------------------
    //FIN SECCION DE CARGA DE ARCHIVOS
      

 
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
