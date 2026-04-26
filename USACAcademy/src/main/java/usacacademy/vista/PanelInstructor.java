package usacacademy.vista;

import usacacademy.Modelo.Nota;
import usacacademy.Modelo.Seccion;
import usacacademy.Modelo.Usuario;
import usacacademy.Modelo.listaSimple;
import usacacademy.controlador.AppControlador;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import usacacademy.controlador.Bitacora;
import usacacademy.controlador.Reportes;

public class PanelInstructor extends JPanel{
    private Reportes reportes;
    private VistaPrincipal ventanaPrincipal;
    private AppControlador controlador;
    private Usuario.Instructor instructorActual; // quien inicio sesion
    
    // tablas
    private JTable tablaSecciones, tablaNotas;
    private DefaultTableModel modeloSecciones, modeloNotas;
    //constructor
    public PanelInstructor(VistaPrincipal ventanaPrincipal) {
        this.ventanaPrincipal = ventanaPrincipal;
        this.controlador = ventanaPrincipal.getControlador();
        setLayout(new BorderLayout());
        initComponents();
    }
     // se llama desde VistaPrincipal al cambiar a esta vista
    // para saber que instructor inicio sesion
    public void setInstructor(Usuario.Instructor instructor) {
        this.instructorActual = instructor;
        this.reportes = new Reportes(controlador);
        cargarTablaSecciones();
        cargarTablaNotas();
    }
    private void initComponents() {
        JLabel lblTitulo = new JLabel("Panel Instructor", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 18));
        add(lblTitulo, BorderLayout.NORTH);
 
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Tahoma", Font.BOLD, 13));
 
        tabs.addTab("Mis Secciones", pestanaSecciones());
        tabs.addTab("Notas",         pestanaNotas());
 
        add(tabs, BorderLayout.CENTER);
 
        // boton logout abajo
        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnLogout = new JButton("Cerrar Sesion");
        btnLogout.addActionListener(e -> {
            if (instructorActual != null) {
                Bitacora.registrar("INSTRUCTOR", instructorActual.getCodigo(), "CERRAR_SESION", "Sesion finalizada");
            }
            ventanaPrincipal.cambiarVista("Login");
        });
        panelSur.add(btnLogout);
        add(panelSur, BorderLayout.SOUTH);
    }
     // -------------------------------------------------
    //  PESTANA 1 — MIS SECCIONES
    // -----------------------------------------------
    private JPanel pestanaSecciones() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        // tabla de secciones
        String[] cols = {"Codigo Seccion", "Codigo Curso", "Horario", "Semestre", "Cupos", "Estado"};
        modeloSecciones = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaSecciones = new JTable(modeloSecciones);
        tablaSecciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(tablaSecciones), BorderLayout.CENTER);

        // tabla de estudiantes
        String[] colsEst = {"Codigo Estudiante", "Nombre", "Promedio", "Estado"};
        DefaultTableModel modeloEstudiantes = new DefaultTableModel(colsEst, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tablaEstudiantes = new JTable(modeloEstudiantes);
        JPanel panelEst = new JPanel(new BorderLayout());
        panelEst.setBorder(BorderFactory.createTitledBorder("Estudiantes de la seccion seleccionada"));
        panelEst.add(new JScrollPane(tablaEstudiantes), BorderLayout.CENTER);
        panelEst.setPreferredSize(new Dimension(0, 150));

        // boton reporte
        JPanel botonesReportes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnNotasSeccion = new JButton("Reporte Notas por Seccion");
        JButton btnReporteEstudiante = new JButton("Reporte por Estudiante");
        botonesReportes.add(btnNotasSeccion);
        botonesReportes.add(btnReporteEstudiante);
        btnNotasSeccion.addActionListener(e -> {
            String cod = JOptionPane.showInputDialog(this, "Ingrese el codigo de la seccion:");
            if (cod == null || cod.trim().isEmpty()) return;
            reportes.reporteCalificacionesPorSeccion(cod);
            Bitacora.registrar("INSTRUCTOR", instructorActual.getCodigo(), "REPORTE NOTAS SECCION", "Generado");
        });
        btnReporteEstudiante.addActionListener(e -> {
            String cod = JOptionPane.showInputDialog(this, "Ingrese el codigo del estudiante:");
            if (cod == null || cod.trim().isEmpty()) return;
            reportes.reporteIndividualEstudiante(cod);
            Bitacora.registrar("INSTRUCTOR", instructorActual.getCodigo(), "REPORTE ESTUDIANTE", "Generado");
            
        });

        // panel sur que contiene ambos: tabla estudiantes arriba, boton abajo
        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.add(panelEst,        BorderLayout.CENTER);
        panelSur.add(botonesReportes, BorderLayout.SOUTH);

        panel.add(panelSur, BorderLayout.SOUTH);
        // al seleccionar una seccion, mostrar sus estudiantes
        tablaSecciones.getSelectionModel().addListSelectionListener(e -> {
            int fila = tablaSecciones.getSelectedRow();
            if (fila < 0) return;
            String codSeccion = (String) modeloSecciones.getValueAt(fila, 0);
            modeloEstudiantes.setRowCount(0);
            Seccion sec = controlador.buscarSeccion(codSeccion);
            if (sec == null) return;
            listaSimple<String> inscritos = sec.getEstudiantesInscritos();
            for (int i = 0; i < inscritos.size(); i++) {
                String codEst = inscritos.obtener(i);
                Usuario u = controlador.buscarUsuario(codEst);
                String nombre = (u != null) ? u.getNombre() : "Desconocido";
                double promedio = controlador.calcularPromedio(codSeccion, codEst);
                String estado = promedio >= 61 ? "Aprobado" : "Reprobado";
                modeloEstudiantes.addRow(new Object[]{codEst, nombre, String.format("%.2f", promedio), estado});
            }
        });
 
        return panel;
    }
    private JPanel pestanaNotas() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));
 
        // tabla de notas
        String[] cols = {"Curso", "Seccion", "Estudiante", "Etiqueta", "Ponderacion", "Nota", "Fecha", "Promedio", "Estado"};
        modeloNotas = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaNotas = new JTable(modeloNotas);
        tablaNotas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(tablaNotas), BorderLayout.CENTER);
 
        // formulario
        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.setBorder(BorderFactory.createTitledBorder("Gestion de Notas"));
 
        JTextField txtCurso      = new JTextField();
        JTextField txtSeccion    = new JTextField();
        JTextField txtEstudiante = new JTextField();
        JTextField txtEtiqueta   = new JTextField(); // parcial, tarea, etc
        JTextField txtPonderacion = new JTextField();
        JTextField txtNota       = new JTextField();
        JTextField txtFecha      = new JTextField();
 
        form.add(new JLabel("Codigo Curso:"));     form.add(txtCurso);
        form.add(new JLabel("Codigo Seccion:"));   form.add(txtSeccion);
        form.add(new JLabel("Codigo Estudiante:")); form.add(txtEstudiante);
        form.add(new JLabel("Etiqueta:"));         form.add(txtEtiqueta);
        form.add(new JLabel("Ponderacion (%):"));  form.add(txtPonderacion);
        form.add(new JLabel("Nota (0-100):"));     form.add(txtNota);
        form.add(new JLabel("Fecha (YYYY-MM-DD):")); form.add(txtFecha);
 
        // botones
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        JButton btnAgregar    = new JButton("Agregar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar   = new JButton("Eliminar");
        JButton btnCSV        = new JButton("Cargar CSV");
        JButton btnLimpiar    = new JButton("Limpiar");
 
        botones.add(btnAgregar);
        botones.add(btnActualizar);
        botones.add(btnEliminar);
        botones.add(btnCSV);
        botones.add(btnLimpiar);
 
        JPanel sur = new JPanel(new BorderLayout());
        sur.add(form,    BorderLayout.CENTER);
        sur.add(botones, BorderLayout.SOUTH);
        panel.add(sur, BorderLayout.SOUTH);
 
        // llenar form al seleccionar fila
        tablaNotas.getSelectionModel().addListSelectionListener(e -> {
            int fila = tablaNotas.getSelectedRow();
            if (fila < 0) return;
            txtCurso.setText      ((String) modeloNotas.getValueAt(fila, 0));
            txtSeccion.setText    ((String) modeloNotas.getValueAt(fila, 1));
            txtEstudiante.setText ((String) modeloNotas.getValueAt(fila, 2));
            txtEtiqueta.setText   ((String) modeloNotas.getValueAt(fila, 3));
            txtPonderacion.setText(String.valueOf(modeloNotas.getValueAt(fila, 4)));
            txtNota.setText       (String.valueOf(modeloNotas.getValueAt(fila, 5)));
            txtFecha.setText      ((String) modeloNotas.getValueAt(fila, 6));
        });
 
        // AGREGAR
        btnAgregar.addActionListener(e -> {
            if (instructorActual == null) return;
            String cur  = txtCurso.getText().trim();
            String sec  = txtSeccion.getText().trim();
            String est  = txtEstudiante.getText().trim();
            String eti  = txtEtiqueta.getText().trim();
            String fecha = txtFecha.getText().trim();
 
            if (cur.isEmpty() || sec.isEmpty() || est.isEmpty() || eti.isEmpty()) {
                JOptionPane.showMessageDialog(this, "ERROR: Todos los campos son obligatorios.");
                Bitacora.registrar("INSTRUCTOR", instructorActual.getCodigo(), "AGREGAR NOTA", "Error datos incompletos");
                return;
            }
            double pond = 0, nota = 0;
            try {
                pond = Double.parseDouble(txtPonderacion.getText().trim());
                nota = Double.parseDouble(txtNota.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ERROR: Ponderacion y nota deben ser numeros.");
                Bitacora.registrar("INSTRUCTOR", instructorActual.getCodigo(), "AGREGAR NOTA", "Error formato numerico");
                return;
            }
                controlador.agregarNota(new Nota(cur, sec, est, eti, pond, nota, fecha),
                instructorActual.getCodigo()
            );
         
                JOptionPane.showMessageDialog(this, "Nota registrada correctamente.");
                Bitacora.registrar("INSTRUCTOR", instructorActual.getCodigo(), "AGREGAR NOTA", "Nota agregada correctamente");
                cargarTablaNotas();
                limpiarCampos(txtCurso, txtSeccion, txtEstudiante, txtEtiqueta, txtPonderacion, txtNota, txtFecha);
            
        });
 
        // ACTUALIZAR
        btnActualizar.addActionListener(e -> {
            String sec  = txtSeccion.getText().trim();
            String est  = txtEstudiante.getText().trim();
            String eti  = txtEtiqueta.getText().trim();
            if (sec.isEmpty() || est.isEmpty() || eti.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Seleccione una nota de la tabla.");
                return;
            }
            double pond = 0, nota = 0;
            try {
                pond = Double.parseDouble(txtPonderacion.getText().trim());
                nota = Double.parseDouble(txtNota.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ERROR: Ponderacion y nota deben ser numeros.");
                Bitacora.registrar("INSTRUCTOR", instructorActual.getCodigo(), "ACTUALIZAR NOTA", "Error datos incompletos");
                return;
            }
                controlador.actualizarNota(sec, est, eti, pond, nota);
                JOptionPane.showMessageDialog(this, "Nota actualizada.");
                Bitacora.registrar("INSTRUCTOR", instructorActual.getCodigo(), "ACTUALIZAR NOTA", "Nota actualizada");
                cargarTablaNotas();
            
        });
 
        // ELIMINAR
        btnEliminar.addActionListener(e -> {
            String sec = txtSeccion.getText().trim();
            String est = txtEstudiante.getText().trim();
            String eti = txtEtiqueta.getText().trim();
            if (sec.isEmpty() || est.isEmpty() || eti.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Seleccione una nota de la tabla.");
                Bitacora.registrar("INSTRUCTOR", instructorActual.getCodigo(), "ELIMINAR NOTA", "Error datos incompletos");
                return;
                
            }
            controlador.eliminarNota(sec, est, eti);
            JOptionPane.showMessageDialog(this, "Nota eliminada.");
            Bitacora.registrar("INSTRUCTOR", instructorActual.getCodigo(), "ELIMINAR NOTA", "Nota eliminada");
            cargarTablaNotas();
            limpiarCampos(txtCurso, txtSeccion, txtEstudiante, txtEtiqueta, txtPonderacion, txtNota, txtFecha);
            
        });
 
        // CARGAR CSV
        btnCSV.addActionListener(e -> {
            if (instructorActual == null) return;
            String path = elegirCSV();
            if (path != null) {
                controlador.cargarNotasCSV(path, instructorActual.getCodigo());
                Bitacora.registrar("INSTRUCTOR", instructorActual.getCodigo(), "CARGA CSV NOTAS", "Archivo cargado");
                cargarTablaNotas();
            }
        });
 
        // LIMPIAR
        btnLimpiar.addActionListener(e -> {
            limpiarCampos(txtCurso, txtSeccion, txtEstudiante, txtEtiqueta,
                          txtPonderacion, txtNota, txtFecha);
            tablaNotas.clearSelection();
        });
 
        return panel;
    }
    //------------------------PESTANA 2 NOTAS--------------------------------------
    
    //--------------------------UTILIDADES--------------------------------------------
 
    private void limpiarCampos(JComponent... campos) {
        for (JComponent c : campos) {
            if (c instanceof JTextField) ((JTextField) c).setText("");
        }
    }
    // carga solo las secciones asignadas al instructor actual
    private void cargarTablaSecciones() {
        if (modeloSecciones == null || instructorActual == null) return;
        modeloSecciones.setRowCount(0);
        listaSimple<Seccion> lista = controlador.getSEcciones();
        for (int i = 0; i < lista.size(); i++) {
            Seccion s = lista.obtener(i);
            if (!s.getCodigoInstructor().equals(instructorActual.getCodigo())) continue;
            modeloSecciones.addRow(new Object[]{
                s.getCodigo(), s.getCodigoCurso(),
                s.getHorario(), s.getSemestre(),
                s.getCupos(), s.getEstado()
            });
        }
    }
 
    
    private void cargarTablaNotas() {
        if (modeloNotas == null || instructorActual == null) return;
        modeloNotas.setRowCount(0);
        listaSimple<Nota> lista = controlador.getNotas();
        for (int i = 0; i < lista.size(); i++) {
            Nota n = lista.obtener(i);
            // verificar que la seccion pertenece al instructor
            Seccion sec = controlador.buscarSeccion(n.getCodigoSeccion());
            if (sec == null) continue;
            if (!sec.getCodigoInstructor().equals(instructorActual.getCodigo())) continue;
            double promedio = controlador.calcularPromedio(n.getCodigoSeccion(), n.getCodigoEstudiante());
            String estado = promedio >= 61 ? "Aprobado" : "Reprobado";
            modeloNotas.addRow(new Object[]{
                n.getCodigoCurso(), n.getCodigoSeccion(), n.getCodigoEstudiante(),
                n.getEtiqueta(), n.getPonderacion(), n.getValor(),
                n.getFecha(), String.format("%.2f", promedio), estado
            });
        }
        
    }
   
 
    private String elegirCSV() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Seleccionar archivo CSV");
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos CSV", "csv"));
        int res = fc.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFile().getAbsolutePath();
        }
        return null;
    }
}
