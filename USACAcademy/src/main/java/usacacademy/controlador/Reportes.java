
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
     private void generarPDF(String titulo, String[] encabezados, listaSimple<String[]> filas) {
        String nombreArchivo = generarNombre("Reporte", "pdf");
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
    private void generarCSV(String[] encabezados, listaSimple<String[]> filas) {
      // genera el nombre con la fecha, se guarda donde corren los .ser
      String nombreArchivo = generarNombre("REPORTE", "csv");
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
    
     //EStudiente individual: 
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
       generarPDF(titulo, encabezados, filas);
       generarCSV(encabezados, filas);
    }
 
     
     
}
