package usacacademy.vista;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import usacacademy.Modelo.Curso;
import usacacademy.Modelo.Nota;
import usacacademy.Modelo.Seccion;
import usacacademy.Modelo.Usuario;
import usacacademy.Modelo.listaSimple;
import usacacademy.controlador.AppControlador;

public class PanelEstudiante extends JPanel{
    private VistaPrincipal ventanaPrincipal; 
    private AppControlador controlador; 
    private Usuario.Estudiante estudianteActual; // estudiante que inicio sesion
    
    //tablas
    private JTable tablaDisponibles, tablaMisInscripciones, tablaNotas;
    private DefaultTableModel modeloDisponibles, modeloMisInscripciones, modeloNotas;
    
    public PanelEstudiante(VistaPrincipal ventanaPrincipal) {
        this.ventanaPrincipal = ventanaPrincipal;
        this.controlador = ventanaPrincipal.getControlador();
        setLayout(new BorderLayout());
        initComponents();
    }
    // se llama desde VistaPrincipal al cambiar a esta vista
    // para saber que estudiante inicio sesion
    
    public void setEstudiante(Usuario.Estudiante estudiante) {
        this.estudianteActual = estudiante;
        // recargar todas las tablas con los datos del estudiante actual
        cargarTablaDisponibles();
        cargarTablaMisInscripciones();
        cargarTablaNotas();
    }
    private void initComponents() {
        // titulo
        JLabel lblTitulo = new JLabel("Panel Estudiante", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 18));
        add(lblTitulo, BorderLayout.NORTH);
 
        // pestanas
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Tahoma", Font.BOLD, 13));
 
        tabs.addTab("Secciones Disponibles", pestanaCursosDisponibles());
        tabs.addTab("Mis Inscripciones", pestanaMisInscripciones());
        tabs.addTab("Mis Notas", pestanaNotas());
 
        add(tabs, BorderLayout.CENTER);
 
        // boton logout abajo
        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnLogout = new JButton("Cerrar Sesion");
        btnLogout.addActionListener(e -> ventanaPrincipal.cambiarVista("Login"));
        panelSur.add(btnLogout);
        add(panelSur, BorderLayout.SOUTH);
    }
    
    //PESTANA 1: MUESTRA LAS SECCIONES DISPONIBLES PARA INSCRIBIRSE----------------------------------
    private JPanel pestanaCursosDisponibles() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));
 
        // tabla con secciones abiertas
        String[] cols = {"Codigo Seccion", "Codigo Curso", "Nombre Curso", "Instructor", "Horario", "Semestre", "Cupos"};
        modeloDisponibles = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaDisponibles = new JTable(modeloDisponibles);
        tablaDisponibles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(tablaDisponibles), BorderLayout.CENTER);
 
        // botones
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        JButton btnInscribirse = new JButton("Inscribirse");
        
 
        botones.add(btnInscribirse);
      
        panel.add(botones, BorderLayout.SOUTH);
 
        // INSCRIBIRSE en la seccion seleccionada
        btnInscribirse.addActionListener(e -> {
            if (estudianteActual == null) return;
            int fila = tablaDisponibles.getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(this, "Seleccione una seccion.");
                return;
            }
            String codSeccion = (String) modeloDisponibles.getValueAt(fila, 0);
            Seccion sec = controlador.buscarSeccion(codSeccion);
            if (sec == null) return;
 
            // validar que no este ya inscrito en esa seccion
            if (sec.estaInscrito(estudianteActual.getCodigo())) {
                JOptionPane.showMessageDialog(this, "ERROR: ya esta incrito en esta seccion.");
                return;
            }
            // validar que haya cupos
            if (sec.getCupos() <= 0) {
                JOptionPane.showMessageDialog(this, "ERROR: no hay cupos disponibles.");
                return;
            }
            // inscribir al estudiante en la seccion
            boolean ok = sec.inscribirEstudiante(estudianteActual.getCodigo());
            if (ok) {
                estudianteActual.incrementarCursos(); // actualizar contador del estudiante
                controlador.guardarSecciones();
                controlador.guardarUsuarios();
                JOptionPane.showMessageDialog(this, "inscripcion exitosa en seccion " + codSeccion);
                // refrescar tablas
                cargarTablaDisponibles();
                cargarTablaMisInscripciones();
            } else {
                JOptionPane.showMessageDialog(this, "ERROR: No se pudo inscribir.");
            }
        });
 

 
        return panel;
    }
    //PESTANA 2: MIS inscricpiones, muestra las secciones y permite desasignares
    private JPanel pestanaMisInscripciones() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));
 
        // tabla con secciones inscritas
        String[] cols = {"Codigo Seccion", "Codigo Curso", "Nombre Curso", "Instructor", "Horario", "Semestre", "Promedio", "Estado"};
        modeloMisInscripciones = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaMisInscripciones = new JTable(modeloMisInscripciones);
        tablaMisInscripciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(tablaMisInscripciones), BorderLayout.CENTER);
 
        // botones
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        JButton btnDesasignar = new JButton("Desasignarse");
        JButton btnRefrescar  = new JButton("Actualizar");
        //acciones de botones
        botones.add(btnDesasignar);
        botones.add(btnRefrescar);
        
        panel.add(botones, BorderLayout.SOUTH);
 
        // DESASIGNARSE de la seccion seleccionada
        btnDesasignar.addActionListener(e -> {
            if (estudianteActual == null) return;
            int fila = tablaMisInscripciones.getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(this, "Seleccione una seccion.");
                return;
            }
            String codSeccion = (String) modeloMisInscripciones.getValueAt(fila, 0);
            Seccion sec = controlador.buscarSeccion(codSeccion);
            if (sec == null) return;
 
            // validar que no tenga notas registradas en esa seccion
            // si tiene notas no se puede desasignar segun el enunciado
            if (tieneNotas(codSeccion, estudianteActual.getCodigo())) {
                JOptionPane.showMessageDialog(this, "ERROR: No se puede desasignar, tiene notas registradas en esta seccion.");
                return;
            }
            // desasignar al estudiante
            boolean ok = sec.desasignarEstudiante(estudianteActual.getCodigo());
            if (ok) {
                estudianteActual.decrementarCursos(); // actualizar contador
                controlador.guardarSecciones();
                controlador.guardarUsuarios();
                JOptionPane.showMessageDialog(this, "Desasignado correctamente de seccion " + codSeccion);
                // refrescar tablas
                cargarTablaDisponibles();
                cargarTablaMisInscripciones();
                cargarTablaNotas();
            } else {
                JOptionPane.showMessageDialog(this, "ERROR: No se pudo desasignar.");
            }
        });
 
        
 
        return panel;
    }
    //PESTANA 3: Mis Notas: muestra las notas del estduiante con promedio: 
    private JPanel pestanaNotas() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));
 
        // tabla con notas del estudiante
        String[] cols = {"Seccion", "Curso", "Etiqueta", "Ponderacion", "Nota", "Fecha", "Promedio Seccion", "Estado"};
        modeloNotas = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaNotas = new JTable(modeloNotas);
        tablaNotas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(tablaNotas), BorderLayout.CENTER);
 
        //panel.add(botones, BorderLayout.SOUTH);
 
        return panel;
    }
     // carga todas las secciones abiertas disponibles para inscribirse
    private void cargarTablaDisponibles() {
        if (modeloDisponibles == null) return;
        modeloDisponibles.setRowCount(0);
        listaSimple<Seccion> lista = controlador.getSEcciones();
        for (int i = 0; i < lista.size(); i++) {
            Seccion s = lista.obtener(i);
            // solo mostrar secciones abiertas
            if (!s.getEstado().equals("ABIERTA")) continue;
            // buscar nombre del curso
            Curso cur = controlador.buscarCurso(s.getCodigoCurso());
            String nombreCurso = (cur != null) ? cur.getNombre() : "Desconocido";
            // buscar nombre del instructor
            Usuario ins = controlador.buscarUsuario(s.getCodigoInstructor());
            String nombreIns = (ins != null) ? ins.getNombre() : "Sin instructor";
            modeloDisponibles.addRow(new Object[]{
                s.getCodigo(), s.getCodigoCurso(), nombreCurso,
                nombreIns, s.getHorario(), s.getSemestre(), s.getCupos()
            });
        }
    }
 
    // carga las secciones en las que el estudiante ya esta inscrito
    private void cargarTablaMisInscripciones() {
        if (modeloMisInscripciones == null || estudianteActual == null) return;
        modeloMisInscripciones.setRowCount(0);
        listaSimple<Seccion> lista = controlador.getSEcciones();
        for (int i = 0; i < lista.size(); i++) {
            Seccion s = lista.obtener(i);
            // solo las secciones donde este inscrito el estudiante
            if (!s.estaInscrito(estudianteActual.getCodigo())) continue;
            // buscar nombre del curso
            Curso cur = controlador.buscarCurso(s.getCodigoCurso());
            String nombreCurso = (cur != null) ? cur.getNombre() : "Desconocido";
            // buscar nombre del instructor
            Usuario ins = controlador.buscarUsuario(s.getCodigoInstructor());
            String nombreIns = (ins != null) ? ins.getNombre() : "Sin instructor";
            // calcular promedio del estudiante en esa seccion
            double promedio = controlador.calcularPromedio(s.getCodigo(), estudianteActual.getCodigo());
            String estado = promedio >= 61 ? "Aprobado" : "Reprobado";
            modeloMisInscripciones.addRow(new Object[]{
                s.getCodigo(), s.getCodigoCurso(), nombreCurso,
                nombreIns, s.getHorario(), s.getSemestre(),
                String.format("%.2f", promedio), estado
            });
        }
    }
 
    // carga todas las notas individuales del estudiante
    private void cargarTablaNotas() {
        if (modeloNotas == null || estudianteActual == null) return;
        modeloNotas.setRowCount(0);
        listaSimple<Nota> lista = controlador.getNotas();
        for (int i = 0; i < lista.size(); i++) {
            Nota n = lista.obtener(i);
            // solo las notas de este estudiante
            if (!n.getCodigoEstudiante().equals(estudianteActual.getCodigo())) continue;
            // calcular promedio de esa seccion para el estudiante
            double promedio = controlador.calcularPromedio(n.getCodigoSeccion(), estudianteActual.getCodigo());
            String estado = promedio >= 61 ? "Aprobado" : "Reprobado";
            modeloNotas.addRow(new Object[]{
                n.getCodigoSeccion(), n.getCodigoCurso(),
                n.getEtiqueta(), n.getPonderacion(), n.getValor(),
                n.getFecha(), String.format("%.2f", promedio), estado
            });
        }
    }
 

    // verifica si el estudiante tiene notas en una seccion, usado para validar si puede desasignarse
    private boolean tieneNotas(String codSeccion, String codEstudiante) {
        listaSimple<Nota> lista = controlador.getNotas();
        for (int i = 0; i < lista.size(); i++) {
            Nota n = lista.obtener(i);
            if (n.getCodigoSeccion().equals(codSeccion) &&
                n.getCodigoEstudiante().equals(codEstudiante)) {
                return true;
            }
        }
        return false;
    }
    
}
