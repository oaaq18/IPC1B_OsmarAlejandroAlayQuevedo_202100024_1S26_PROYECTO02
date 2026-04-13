
package usacacademy.Modelo;
import java.io.Serializable;
/**
 *clase abstracta base para todos los usuarios del sistema
 * administrador, instructor y estuante heredan de esta clase
 */
public abstract  class Usuario implements Serializable{
    protected String codigo;
    protected String nombre;
    protected String password;
    protected String fechanacimiento;
    protected String genero;
    protected String rol;
    
    //constructor
    public Usuario(String codigo, String nombre, String password, String fechanacimiento, String genero, String rol) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.password = password;
        this.fechanacimiento = fechanacimiento;
        this.genero = genero;
        this.rol = rol;
    }
    public abstract String getTipoUsuario();
    
    //autenticacion
    public boolean autenticar(String password){
        return this.password.equals(password);
    }
    //GETTERS 

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getPassword() {
        return password;
    }

    public String getFechanacimiento() {
        return fechanacimiento;
    }

    public String getGenero() {
        return genero;
    }

    public String getRol() {
        return rol;
    }
    
    //SETTERS

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFechanacimiento(String fechanacimiento) {
        this.fechanacimiento = fechanacimiento;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
    
    
    @Override
    public String toString(){
        return "["+rol+"]"+codigo+" - "+nombre; 
    }
    
    //-------------CLASES HIJAS------------------
    //Admin  
    public static class Administrador extends Usuario{
         public Administrador(String codigo, String nombre, String password, String fechaNacimiento, String genero) {
            super(codigo, nombre, password, fechaNacimiento, genero, "ADMINISTRADOR");
        }
        @Override
        public String getTipoUsuario(){
        return "ADMINISTRADOR";
        }
    }
    
    //instructor
    public static class Instructor extends Usuario{
        private int seccionesAsignadas;
        
        public Instructor(String codigo, String nombre, String password, String fechaNacimiento, String genero){
            super(codigo, nombre, password, fechaNacimiento, genero, "INSTRUCTOR");
            this.seccionesAsignadas=0;
        }
        @Override
        public String getTipoUsuario(){
            return "INSTRUCTOR";
        }
        
        public int getSeccionesAsignadas(){
            return seccionesAsignadas;
        }
        public void setSeccionesAsignadas(int n){
            this.seccionesAsignadas=n;
        }
        public void incrementarSecciones(){
            seccionesAsignadas++;
        }
        public void decrementarSecciones(){
            if (true) {
                
            }
            seccionesAsignadas--;
        }
        
    }
    //ESTUDIANTE
      public static class Estudiante extends Usuario{
        public Estudiante(String codigo, String nombre, String password, String fechaNacimiento, String genero){
            super(codigo, nombre, password, fechaNacimiento, genero, "ESTUDIANTE");
        }
        @Override
        public String getTipoUsuario(){
            return "ESTUDIANTE";
        }
    }
    
  
}
