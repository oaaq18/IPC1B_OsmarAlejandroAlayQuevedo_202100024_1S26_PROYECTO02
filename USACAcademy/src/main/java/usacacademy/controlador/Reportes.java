
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
     private void generarPDF(String titulo, String[] encabezados, listaSimple<String[]> filas, String ruta) {
        Document doc = new Document();
        try {
            PdfWriter.getInstance(doc, new FileOutputStream(ruta));
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
            JOptionPane.showMessageDialog(null, "PDF generado correctamente:\n" + ruta);
 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al generar PDF: " + e.getMessage());
        }
    }
}
