
package usacacademy.controlador;
import usacacademy.Modelo.Curso;
import usacacademy.Modelo.Nota;
import usacacademy.Modelo.Seccion;
import usacacademy.Modelo.Usuario;
import usacacademy.Modelo.listaSimple;

import com.itextpdf.text.Document;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfWriter;
 
import usacacademy.Modelo.Curso;
import usacacademy.Modelo.Nota;
import usacacademy.Modelo.Seccion;
import usacacademy.Modelo.Usuario;
import usacacademy.Modelo.listaSimple;
 
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Reportes {
    private AppControlador controlador;
       public Reportes(AppControlador controlador) {
        this.controlador = controlador;
    }
       //metodo para generar el nombre del archivo
    private String generarNombre(String tipoReporte, String extension) {
        String fecha = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss").format(new Date());
        return fecha + "_" + tipoReporte + "." + extension;
    }
    
    //------------------------------REPORTES PDF-------------------------------------------------
     private void generarPDF(String titulo, String[] encabezados, listaSimple<String[]> filas, String NombreArchivo) {
        String nombreArchivo = generarNombre(NombreArchivo+"PDF", "pdf");
        Document doc = new Document();
        
        try {
            PdfWriter.getInstance(doc, new FileOutputStream(nombreArchivo));
            doc.open();
 
            // titulo del reporte
            doc.add(new Paragraph(titulo));
            doc.add(new Paragraph(" ")); // espacio
 
            // tabla con el numero de columnas
            PdfPTable tabla = new PdfPTable(encabezados.length);
            tabla.setWidthPercentage(100); // ocupa todo el ancho
 
            // agregar encabezados
            for (String enc : encabezados) {
                tabla.addCell(new PdfPCell(new Paragraph(enc)));
            }
 
            // agregar filas de datos
            for (int i = 0; i < filas.size(); i++) {
                String[] fila = filas.obtener(i);
                for (String celda : fila) {
                    tabla.addCell(celda != null ? celda : "");
                }
            }
 
            doc.add(tabla);
            doc.close();
            JOptionPane.showMessageDialog(null, "PDF generado correctamente:");
 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al generar PDF: " + e.getMessage());
        }
    }
     //GENERAR CSV
      // metodo base para crear un CSV
    private void generarCSV(String[] encabezados, listaSimple<String[]> filas, String NombreArchivo) {
      // genera el nombre con la fecha, se guarda donde corren los .ser
      String nombreArchivo = generarNombre(NombreArchivo+"CSV", "csv");
      try (PrintWriter pw = new PrintWriter(new FileWriter(nombreArchivo))) {
          pw.println(String.join(",", encabezados));
          for (int i = 0; i < filas.size(); i++) {
              pw.println(String.join(",", filas.obtener(i)));
          }
          JOptionPane.showMessageDialog(null, "CSV generado:\n" + nombreArchivo);
      } catch (Exception e) {
          JOptionPane.showMessageDialog(null, "Error al generar CSV: " + e.getMessage());
      }
    }
    
     //-------------------------Reporte por estudiante-------------------------- 
       public void reporteIndividualEstudiante(String codEstudiante) {
        String tipo = "ReporteEstudiante_" + codEstudiante;
 
        Usuario u = controlador.buscarUsuario(codEstudiante);
        if (u == null) {
            JOptionPane.showMessageDialog(null, "Estudiante no encontrado.");
            return;
        }
 
        String[] encabezados = {"Seccion", "Curso", "Semestre", "Promedio", "Estado"};
        listaSimple<String[]> filas = new listaSimple<>();
 
        listaSimple<Seccion> secciones = controlador.getSEcciones();
        for (int i = 0; i < secciones.size(); i++) {
            Seccion sec = secciones.obtener(i);
            if (!sec.estaInscrito(codEstudiante)) continue;
            double prom  = controlador.calcularPromedio(sec.getCodigo(), codEstudiante);
            String estado = prom >= 61 ? "Aprobado" : "Reprobado";
            filas.agregar(new String[]{
                sec.getCodigo(), sec.getCodigoCurso(),
                sec.getSemestre(), String.format("%.2f", prom), estado
            });
        }
 
       String titulo = "Reporte Individual: " + u.getNombre() + " (" + codEstudiante + ")";
       generarPDF(titulo, encabezados, filas,"REPORTE_INDIVIDUAL");
       generarCSV(encabezados, filas,"REPORTE_INDIVIDUAL");
    }
    //------------------------INSCRIPCIONES POR CURSO---------------------
    public void reporteInscripcionesPorCurso() {
        String tipo = "InscripcionesPorCurso";

        String[] encabezados = {"Curso", "Nombre Curso", "Secciones Abiertas", "Total Inscritos"};
        listaSimple<String[]> filas = new listaSimple<>();
 
        listaSimple<Curso>    cursos    = controlador.getCursos();
        listaSimple<Seccion>  secciones = controlador.getSEcciones();
 
        for (int i = 0; i < cursos.size(); i++) {
            Curso cur = cursos.obtener(i);
            int seccionesAbiertas = 0;
            int totalInscritos    = 0;
 
            for (int j = 0; j < secciones.size(); j++) {
                Seccion sec = secciones.obtener(j);
                if (!sec.getCodigoCurso().equals(cur.getCodigo())) continue;
                if (sec.getEstado().equals("ABIERTA")) seccionesAbiertas++;
                totalInscritos += sec.getEstudiantesInscritos().size();
            }
 
            filas.agregar(new String[]{
                cur.getCodigo(), cur.getNombre(),
                String.valueOf(seccionesAbiertas),
                String.valueOf(totalInscritos)
            });
        }
 
        generarPDF("Reporte: Inscripciones por Curso", encabezados, filas,"REPORTEINSPORCURSO");
        generarCSV(encabezados, filas,"REPORTEINSPORCURSO");
    }  
    
    //--------------CALIFICACIONES POR SECCION
      public void reporteCalificacionesPorSeccion(String codSeccion) {

        Seccion sec = controlador.buscarSeccion(codSeccion);
        if (sec == null) {
            JOptionPane.showMessageDialog(null, "Seccion no encontrada.");
            return;
        }
 
        String[] encabezados = {"Estudiante", "Nombre", "Etiqueta", "Ponderacion", "Nota", "Fecha", "Promedio", "Estado"};
        listaSimple<String[]> filas = new listaSimple<>();
 
        listaSimple<Nota> notas = controlador.getNotas();
        for (int i = 0; i < notas.size(); i++) {
            Nota n = notas.obtener(i);
            if (!n.getCodigoSeccion().equals(codSeccion)) continue;
            Usuario u = controlador.buscarUsuario(n.getCodigoEstudiante());
            String nombre = (u != null) ? u.getNombre() : "Desconocido";
            double prom   = controlador.calcularPromedio(codSeccion, n.getCodigoEstudiante());
            String estado = prom >= 61 ? "Aprobado" : "Reprobado";
            filas.agregar(new String[]{
                n.getCodigoEstudiante(), nombre, n.getEtiqueta(),
                String.valueOf(n.getPonderacion()), String.valueOf(n.getValor()),
                n.getFecha(), String.format("%.2f", prom), estado
            });
        }
 
        String titulo = "Reporte Calificaciones Seccion: " + codSeccion;
        generarPDF(titulo, encabezados, filas, "CALIFICACIONESPORSECCION");
        generarCSV(encabezados, filas, "CALIFICACIONESPORSECCION");
    }
      
     public void reporteInscripcionesPorSemestre(String semestre) {
    String[] encabezados = {"Curso", "Nombre Curso", "Seccion", "Instructor", "Total Inscritos"};
    listaSimple<String[]> filas = new listaSimple<>();

    listaSimple<Seccion> secciones = controlador.getSEcciones();
    for (int i = 0; i < secciones.size(); i++) {
        Seccion sec = secciones.obtener(i);
        // filtrar solo las secciones del semestre indicado
        if (!sec.getSemestre().equals(semestre)) continue;

        Curso cur = controlador.buscarCurso(sec.getCodigoCurso());
        String nombreCurso = (cur != null) ? cur.getNombre() : "Desconocido";

        Usuario ins = controlador.buscarUsuario(sec.getCodigoInstructor());
        String nombreIns = (ins != null) ? ins.getNombre() : "Sin instructor";

        filas.agregar(new String[]{
            sec.getCodigoCurso(), nombreCurso, sec.getCodigo(),
            nombreIns, String.valueOf(sec.getEstudiantesInscritos().size())
        });
    }

    String titulo = "Reporte Inscripciones Semestre: " + semestre;
    generarPDF(titulo, encabezados, filas, "CALIFICACIONESPORSEMESTRE");
    generarCSV(encabezados, filas,"CALIFICACIONESPORSECCION");
}
     //--------------------------ESTUDIANTE POR SEMESTRE (PARA USO DE ESTUDIANTE)--------
    public void reporteEstudiantePorSemestre(String codEstudiante, String semestre) {
        Usuario u = controlador.buscarUsuario(codEstudiante);
        String nombre=u.getNombre();
        
        if (u == null) return;
        String[] encabezados = {"Seccion", "Curso", "Semestre", "Promedio", "Estado"};
        listaSimple<String[]> filas = new listaSimple<>();
        listaSimple<Seccion> secciones = controlador.getSEcciones();
        for (int i = 0; i < secciones.size(); i++) {
            Seccion sec = secciones.obtener(i);
            // filtrar por estudiante y semestre
            if (!sec.estaInscrito(codEstudiante)) continue;
            if (!sec.getSemestre().equals(semestre)) continue;
            double prom   = controlador.calcularPromedio(sec.getCodigo(), codEstudiante);
            String estado = prom >= 61 ? "Aprobado" : "Reprobado";
            filas.agregar(new String[]{
                sec.getCodigo(), sec.getCodigoCurso(),
                sec.getSemestre(), String.format("%.2f", prom), estado
            });
        }

        String titulo = "Historial Semestre " + semestre + ": " + u.getNombre();
        generarPDF(titulo, encabezados, filas, nombre+"REPORTE");
        generarCSV(encabezados, filas,nombre+"REPORTE");
    }
     
}
