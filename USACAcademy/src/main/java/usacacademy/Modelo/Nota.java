package usacacademy.Modelo;
import java.io.Serializable;
//clase para la nota de un estdiante
//formto csv: codigocurso, c.seccion, codigoEstudiante, podneracion, nota, fecha
public class Nota implements Serializable{
    private String codigoCurso;       // curso al que pertenece
    private String codigoSeccion;     // seccion a la que pertenece
    private String codigoEstudiante;  // estudiante al que pertenece la nota
    private String etiqueta;          // parcial, tarea, hoja 
    private double ponderacion;       // 
    private double valor;             // nota de 0 a 100
    private String fecha;   
    
    //constructor

    public Nota(String codigoCurso, String codigoSeccion, String codigoEstudiante, String etiqueta, double ponderacion, double valor, String fecha) {
        this.codigoCurso = codigoCurso;
        this.codigoSeccion = codigoSeccion;
        this.codigoEstudiante = codigoEstudiante;
        this.etiqueta = etiqueta;
        this.ponderacion = ponderacion;
        this.valor = valor;
        this.fecha = fecha;
    }
    /*calcular podneracion de la nota
        
        
    */
    public double getNotaPonderacion(){
        return ponderacion*valor;
    }
    //vericar si esta aprobado (promedio>=61)
    public boolean estaAprobado(){
        return valor >= 61;
    }
    
    //getters----------------

    public String getCodigoCurso() {
        return codigoCurso;
    }

    public String getCodigoSeccion() {
        return codigoSeccion;
    }

    public String getCodigoEstudiante() {
        return codigoEstudiante;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public double getPonderacion() {
        return ponderacion;
    }

    public double getValor() {
        return valor;
    }

    public String getFecha() {
        return fecha;
    }
    //setters---------

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public void setPonderacion(double ponderacion) {
        this.ponderacion = ponderacion;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
 
    @Override
    public String toString() {
        return etiqueta + " | " + valor + " | " + ponderacion + "% | " + fecha;
    }
}
