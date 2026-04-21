package usacacademy.Modelo;
import java.io.Serializable;
public class inscripcion implements Serializable{
    private String nombreEstudiante; //nombre de quien se einscribio
    private String codigoEstudiante;  // quien se inscribio
    private String codigoSeccion;     // que seccion
    private String codigoCurso;       // que curso
    private String fecha;             // fecha de inscriocion
    private String semestre;          // semestre de inscripcion

    public inscripcion(String nombreEstudiante, String codigoEstudiante, String codigoSeccion, String codigoCurso, String fecha, String semestre) {
        this.nombreEstudiante= nombreEstudiante;
        this.codigoEstudiante = codigoEstudiante;
        this.codigoSeccion = codigoSeccion;
        this.codigoCurso = codigoCurso;
        this.fecha = fecha;
        this.semestre = semestre;
    }
    //----GETTERS

    public String getNombreEstudiante() {
        return nombreEstudiante;
    }
    

    public String getCodigoEstudiante() {
        return codigoEstudiante;
    }

    public String getCodigoSeccion() {
        return codigoSeccion;
    }

    public String getCodigoCurso() {
        return codigoCurso;
    }

    public String getFecha() {
        return fecha;
    }

    public String getSemestre() {
        return semestre;
    }


    @Override
    public String toString() {
        return "Inscripcion{"+
                "Nombre: "+ nombreEstudiante + "\n"+
                "Codigo"+ codigoCurso +"\n"+
                "Fecha"+ fecha+"\n"+
                "Codigo Curso"+ codigoCurso+"\n"+
                "Codigo Seccion: "+ codigoSeccion+"\n"+
                "Semestre"+ semestre+"\n"
                ;   
    }

   
    
    
}
