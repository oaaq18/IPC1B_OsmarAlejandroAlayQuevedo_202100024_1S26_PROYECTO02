package usacacademy.Modelo;
import usacacademy.Modelo.listaSimple;
import java.io.Serializable;
/*
    clase de seccion, contiene instructor, horario y lista de estudiantes inscritos

*/
public class Seccion implements Serializable{
    private String codigo;           // único de seccion
    private String codigoCurso;      // curso al que pertenece
    private String codigoInstructor; // instructor asignado
    private String horario;          
    private String semestre;        
    private int    cupos;            // cupos disponibles
    private String estado;           // ABIERTA / CERRADAc  
    
     // Lista de codigos de estudiantes inscritos
    private listaSimple<String> estudiantesInscritos;
    //constructor

    public Seccion(String codigo, String codigoCurso, String codigoInstructor, String horario, String semestre, int cupos, listaSimple estudiantesInscrito) {
        this.codigo = codigo;
        this.codigoCurso = codigoCurso;
        this.codigoInstructor = codigoInstructor;
        this.horario = horario;
        this.semestre = semestre;
        this.cupos = cupos;
        this.estado = "ABIERTA";
        this.estudiantesInscritos = new listaSimple<>();
    }
    //inscribir/desasignar estuantess
    //retorna true si se instricio o false si ya estabba inscrito o el cupo esta lleno
    public boolean inscribirEstudiante(String codigoEstudiante) {
        if (estaInscrito(codigoEstudiante)) return false;
            if (cupos <= 0) return false;
             estudiantesInscritos.agregar(codigoEstudiante);
            cupos--;
        return true;
    }
    //desasignar estudiante, true si se pudo desasignar y false si no estaba isncrito
       public boolean desasignarEstudiante(String codigoEstudiante) {
        for (int i = 0; i < estudiantesInscritos.size(); i++) {
            if (estudiantesInscritos.obtener(i).equals(codigoEstudiante)) {
                estudiantesInscritos.eliminar(i);
                cupos++;
                return true;
            }
        }
        return false;
    }

    
    //validacion para verificar rsi esta isntrito 
    public boolean estaInscrito(String codigoEstudiante) {
        for (int i = 0; i < estudiantesInscritos.size(); i++) {//recorrer la lista
            if (estudiantesInscritos.obtener(i).equals(codigoEstudiante))
                return true;
        }
        return false;
    }
    //getters-------------

    public String getCodigo() {
        return codigo;
    }

    public String getCodigoCurso() {
        return codigoCurso;
    }

    public String getCodigoInstructor() {
        return codigoInstructor;
    }

    public String getHorario() {
        return horario;
    }

    public String getSemestre() {
        return semestre;
    }

    public int getCupos() {
        return cupos;
    }

    public String getEstado() {
        return estado;
    }

    public listaSimple<String> getEstudiantesInscritos() {
        return estudiantesInscritos;
    }
    
    //setters
    public void setCodigoInstructor(String codigoInstructor) {
        this.codigoInstructor = codigoInstructor;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public void setSemestre(String semestre) {
        this.semestre = semestre;
    }

    public void setCupos(int cupos) {
        this.cupos = cupos;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    
            
}
   
    
