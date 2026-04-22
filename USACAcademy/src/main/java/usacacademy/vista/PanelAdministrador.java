
package usacacademy.vista;
import usacacademy.controlador.AppControlador;
import usacacademy.Modelo.*;
import usacacademy.Modelo.HiloInscripcion;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.awt.*;
import java.io.File;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
/**
 * Panel principal del Administrador
 * Organizado con JTabbedPane
 *   1. Instructores (CRUD + CSV)
 *   2. Estudiantes (CRUD + CSV)
 *   3. Cursos       CRUD + CSV)
 *   4. Secciones     (CRUD)
 *   5. Monitor   (3 hilos en tiempo real)
 */

public class PanelAdministrador extends JPanel{
    
   private VistaPrincipal ventanaPrincipal;
   private AppControlador controlador;
   //HILOS
    private HiloSesiones      tareasSesiones;
    private HiloInscripcion   tareasInscripciones;
    private HiloEstadisticas  tareasEstadisticas;

    private Thread hiloSesiones, hiloInscripciones, hiloEstadisticas; 

    private JTextArea txtConsola; // consola donde se muestran las tareas
    private HiloInscripcion tareaInscripciones;
    //tablas:
    private JTable tablaInstructores, tablaEstudiantes, tablaCursos, tablaSecciones;
    private DefaultTableModel modeloInstructores, modeloEstudiantes, modeloCursos, modeloSecciones;
   
    public PanelAdministrador(VistaPrincipal ventanaPrincipal){
        this.ventanaPrincipal=ventanaPrincipal;
        this.controlador= ventanaPrincipal.getControlador();//por esto no es necesario llamarlo en el cotructor
        setLayout(new BorderLayout());
        initComponents();
    }
    private void initComponents() {
        //Titulo
        JLabel lblTitulo = new  JLabel("Panel Administrador", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 18));
        lblTitulo.setForeground(Color.GREEN);
        add(lblTitulo, BorderLayout.NORTH);
        //encabezado
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setBackground(new Color(0, 102, 153));
        encabezado.setBorder(new EmptyBorder(8, 12, 8, 12));
        //FORMATO DEL MONITOR 
        txtConsola = new JTextArea();  // primero se crea
        txtConsola.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtConsola.setEditable(false);
        txtConsola.setBackground(Color.BLACK);
        txtConsola.setForeground(Color.GREEN);
        txtConsola.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(txtConsola);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Consola de Inscripciones"));
        add(scrollPane, BorderLayout.CENTER);

        //Botones de lado de abajo
        
        JPanel panelBotones = new JPanel();
        JButton btnCargarCSV = new JButton("Cargar CSV");//boton
        JButton btnLogout = new JButton("Cerrar Sesion");//boton
        btnLogout.setBackground(new Color(200, 50, 50)); // fondo rojo
        btnLogout.setForeground(Color.WHITE);  //texto blanco
        btnLogout.setFocusPainted(false); //quitar borde punteado
        //lo que hace el boton salir:
        btnLogout.addActionListener(e -> {
            detenerHilos();
            ventanaPrincipal.cambiarVista("Login"); //regresa a login
        });

 
        //Acción de cargar CSV

            btnCargarCSV.addActionListener(e-> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Seleccionar archivo CSV");
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                String path = fileChooser.getSelectedFile().getAbsolutePath();
                ventanaPrincipal.getControlador().cargarCSV(path);
                // Actualizar contador
                String timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
                int actual = ventanaPrincipal.getControlador().getInscripcionesPendientes();
                txtConsola.append("[Carga CSV] Inscripciones Pendientes: " + actual + " [" + timeStamp + "]\n");
                txtConsola.setCaretPosition(txtConsola.getDocument().getLength());
            }
        });

        //Cerrar sesion y detener hilos

        btnLogout.addActionListener(e-> {
            detenerHilos();
            ventanaPrincipal.cambiarVista("Login");
        });

        panelBotones.add(btnCargarCSV);
        panelBotones.add(btnLogout);
        add(panelBotones, BorderLayout.SOUTH);
        //pestanas ----------------------
        // ── Primero el UIManager ──────────────────────
        UIManager.put("TabbedPane.foreground",         Color.GREEN);
        UIManager.put("TabbedPane.selected",           new Color(240, 247, 240));
        UIManager.put("TabbedPane.selectedForeground", Color.GREEN);

        // ── Luego crear el JTabbedPane ────────────────
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Tahoma", Font.BOLD, 13));
        tabs.setForeground(Color.GREEN);
        tabs.setOpaque(false);

        tabs.addTab("‍🏫Instructores", tablaInstructores());
        tabs.addTab("Estudiantes",     tablaEstudiantes());
        tabs.addTab("Cursos",          tablaCursos());
        tabs.addTab("Secciones",       tablaSecciones());
        tabs.addTab("Monitor",       crearTabMonitor());
 
        add(tabs, BorderLayout.CENTER);

        }
     //Metodo que debemos llamar justo cuando se muestea este panel
    //AREA DE CADA PESTNA
    //----------------------------------------PESTANA ISNTRUCTORES------------------------
    private JPanel tablaInstructores() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));
        // tabla
        
        String[] cols = {"Codigo", "Nombre", "Fecha Nacimiento", "Genero", "Secciones"}; //columnas
        //modelo de la tabla
        modeloInstructores = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }//evita la edicion de celdas
        };
        tablaInstructores = new JTable(modeloInstructores);
        tablaInstructores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(tablaInstructores), BorderLayout.CENTER);
        //form
        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.setBorder(BorderFactory.createTitledBorder("Gestion de Instructores"));
 
        JTextField txtCodigo  = new JTextField();
        JTextField txtNombre  = new JTextField();
        JTextField txtFecha   = new JTextField();
        JComboBox<String> cbGenero = new JComboBox<>(new String[]{"M", "F"});
        JPasswordField txtPass = new JPasswordField();
        
        form.add(new JLabel("Codigo:"));      form.add(txtCodigo);
        form.add(new JLabel("Nombre:"));      form.add(txtNombre);
        form.add(new JLabel("Fecha Nac.:"));  form.add(txtFecha);
        form.add(new JLabel("Genero:"));      form.add(cbGenero);
        form.add(new JLabel("Contrasena:"));  form.add(txtPass);
        
        //botones
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        JButton btnAgregar   = new JButton("Agregar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar  = new JButton("Eliminar");
        JButton btnCSV       = new JButton("Cargar CSV");
        JButton btnLimpiar   = new JButton("Limpiar espacios");
 

        //agregar botones al panel de botones
        botones.add(btnAgregar);
        botones.add(btnActualizar);
        botones.add(btnEliminar);
        botones.add(btnCSV);
        botones.add(btnLimpiar);
        //construye el panel sur y lo agrega al principal
        JPanel sur = new JPanel(new BorderLayout()); //form del espacio central
        sur.add(form,    BorderLayout.CENTER);      // bonotes bajo el formularoio
        sur.add(botones, BorderLayout.SOUTH);       //todo el bloque vde ababjo del panel
        panel.add(sur, BorderLayout.SOUTH);
        // cargar tabla al inicio
        cargarTablaUsuarios(modeloInstructores, "INSTRUCTOR");
        
        // ── llenar form al seleccionar fila ─────────────────────────
        tablaInstructores.getSelectionModel().addListSelectionListener(e -> {
            int fila = tablaInstructores.getSelectedRow();
            if (fila < 0) return;
            txtCodigo.setText((String) modeloInstructores.getValueAt(fila, 0));
            txtNombre.setText((String) modeloInstructores.getValueAt(fila, 1));
            txtFecha.setText ((String) modeloInstructores.getValueAt(fila, 2));
            cbGenero.setSelectedItem(modeloInstructores.getValueAt(fila, 3));
            txtPass.setText("");
        });
        
         // ── AGREGAR ──────────────────────────────────────────────────
        btnAgregar.addActionListener(e -> {
            String cod  = txtCodigo.getText().trim();
            String nom  = txtNombre.getText().trim();
            String fec  = txtFecha.getText().trim();
            String gen  = (String) cbGenero.getSelectedItem();
            String pass = new String(txtPass.getPassword()).trim();
 
            if (cod.isEmpty() || nom.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "EEROR: Codigo, nombre y contraseña son obligatorios.");
                return;
            }
            boolean ok = controlador.agregarUsuario(
                new Usuario.Instructor(cod, nom, pass, fec, gen));
            if (ok) {
                JOptionPane.showMessageDialog(this, "Instructor registrado correctamente.");
                cargarTablaUsuarios(modeloInstructores, "INSTRUCTOR");
                limpiarCampos(txtCodigo, txtNombre, txtFecha, txtPass);
            }
        });
 
        // ── ACTUALIZAR ───────────────────────────────────────────────
        btnActualizar.addActionListener(e -> {
            String cod  = txtCodigo.getText().trim();
            String nom  = txtNombre.getText().trim();
            String pass = new String(txtPass.getPassword()).trim();
            if (cod.isEmpty()) { JOptionPane.showMessageDialog(this, "Seleccione o ingrese un codigo."); return; }
            controlador.actualizarUsuario(cod, nom.isEmpty() ? null : nom, pass.isEmpty() ? null : pass);
            JOptionPane.showMessageDialog(this, "Instructor actualizado.");
            cargarTablaUsuarios(modeloInstructores, "INSTRUCTOR");
            
        });
 
        // ── ELIMINAR ─────────────────────────────────────────────────
        btnEliminar.addActionListener(e -> {
            String cod = txtCodigo.getText().trim();
            if (cod.isEmpty()) { JOptionPane.showMessageDialog(this, "Seleccione un instructor."); return; }
                controlador.eliminarUsuario(cod);
                JOptionPane.showMessageDialog(this, "Instructor eliminado.");
                cargarTablaUsuarios(modeloInstructores, "INSTRUCTOR");
                limpiarCampos(txtCodigo, txtNombre, txtFecha, txtPass);
            
        });
 
        // ── CARGAR CSV ───────────────────────────────────────────────
        btnCSV.addActionListener(e -> {
            String path = elegirCSV();
            if (path != null) {
                controlador.cargarUsuariosCSV(path, "INSTRUCTOR");
                cargarTablaUsuarios(modeloInstructores, "INSTRUCTOR");
            }
        });
 
        // ── LIMPIAR ──────────────────────────────────────────────────
        btnLimpiar.addActionListener(e -> {
            limpiarCampos(txtCodigo, txtNombre, txtFecha, txtPass);
            tablaInstructores.clearSelection();
        });
 
        return panel;
 
    }
    //------------------PESTANA ESTUDIANTE-----------------------------------------------------------------------
    private JPanel tablaEstudiantes() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));
 
        String[] cols = {"codigo", "Nombre", "Fecha Nacimiento", "Genero", "Cursos Inscritos"};
        modeloEstudiantes = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaEstudiantes = new JTable(modeloEstudiantes);
        tablaEstudiantes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(tablaEstudiantes), BorderLayout.CENTER);
 
        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.setBorder(BorderFactory.createTitledBorder("Gestion de Estudiantes"));
 
        JTextField txtCodigo  = new JTextField();
        JTextField txtNombre  = new JTextField();
        JTextField txtFecha   = new JTextField();
        JComboBox<String> cbGenero = new JComboBox<>(new String[]{"M", "F", "Otro"});
        JPasswordField txtPass = new JPasswordField();
 
        form.add(new JLabel("Codigo:"));      form.add(txtCodigo);
        form.add(new JLabel("Nombre:"));      form.add(txtNombre);
        form.add(new JLabel("Fecha Nac.:"));  form.add(txtFecha);
        form.add(new JLabel("Genero:"));      form.add(cbGenero);
        form.add(new JLabel("Contraseña:"));  form.add(txtPass);
 
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        JButton btnAgregar    = new JButton("Agregar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar   = new JButton("Eliminar");
        JButton btnCSV        = new JButton("Cargar CSV");
        JButton btnLimpiar    = new JButton("Limpiar");
 
        botones.add(btnAgregar); botones.add(btnActualizar);
        botones.add(btnEliminar); botones.add(btnCSV); botones.add(btnLimpiar);
 
        JPanel sur = new JPanel(new BorderLayout());
        sur.add(form, BorderLayout.CENTER);
        sur.add(botones, BorderLayout.SOUTH);
        panel.add(sur, BorderLayout.SOUTH);
 
        cargarTablaUsuarios(modeloEstudiantes, "ESTUDIANTE");
 
        tablaEstudiantes.getSelectionModel().addListSelectionListener(e -> {
            int fila = tablaEstudiantes.getSelectedRow();
            if (fila < 0) return;
            txtCodigo.setText((String) modeloEstudiantes.getValueAt(fila, 0));
            txtNombre.setText((String) modeloEstudiantes.getValueAt(fila, 1));
            txtFecha.setText ((String) modeloEstudiantes.getValueAt(fila, 2));
            cbGenero.setSelectedItem(modeloEstudiantes.getValueAt(fila, 3));
            txtPass.setText("");
        });
 
        btnAgregar.addActionListener(e -> {
            String cod  = txtCodigo.getText().trim();
            String nom  = txtNombre.getText().trim();
            String fec  = txtFecha.getText().trim();
            String gen  = (String) cbGenero.getSelectedItem();
            String pass = new String(txtPass.getPassword()).trim();
            if (cod.isEmpty() || nom.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Codigo, nombre y contraseña son obligatorios.");
                return;
            }
            boolean ok = controlador.agregarUsuario(
                new Usuario.Estudiante(cod, nom, pass, fec, gen));
            if (ok) {
                JOptionPane.showMessageDialog(this, "Estudiante registrado correctamente.");
                cargarTablaUsuarios(modeloEstudiantes, "ESTUDIANTE");
                limpiarCampos(txtCodigo, txtNombre, txtFecha, txtPass);
            }
        });
 
        btnActualizar.addActionListener(e -> {
            String cod  = txtCodigo.getText().trim();
            String nom  = txtNombre.getText().trim();
            String pass = new String(txtPass.getPassword()).trim();
            if (cod.isEmpty()) { JOptionPane.showMessageDialog(this, "Seleccione o ingrese un codigo."); return; }
            controlador.actualizarUsuario(cod, nom.isEmpty() ? null : nom, pass.isEmpty() ? null : pass);
            JOptionPane.showMessageDialog(this, "EStudiante actualizado.");
            cargarTablaUsuarios(modeloEstudiantes, "ESTUDIANTE");
            
        });
 
        btnEliminar.addActionListener(e -> {
            String cod = txtCodigo.getText().trim();
            if (cod.isEmpty()) { JOptionPane.showMessageDialog(this, "Seleccione un estudiante."); return; }
                    controlador.eliminarUsuario(cod);
                    JOptionPane.showMessageDialog(this, "Estudiante eliminado.");
                    cargarTablaUsuarios(modeloEstudiantes, "ESTUDIANTE");
                    limpiarCampos(txtCodigo, txtNombre, txtFecha, txtPass);
        });
 
        btnCSV.addActionListener(e -> {
            String path = elegirCSV();
            if (path != null) {
                controlador.cargarUsuariosCSV(path, "ESTUDIANTE");
                cargarTablaUsuarios(modeloEstudiantes, "ESTUDIANTE");
            }
        });
 
        btnLimpiar.addActionListener(e -> {
            limpiarCampos(txtCodigo, txtNombre, txtFecha, txtPass);
            tablaEstudiantes.clearSelection();
        });
 
        return panel;
    }
    //----------------------------------------PESTANA CURSOS---------------------------------
    private JPanel tablaCursos() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));
 
        String[] cols = {"Codigo", "Nombre", "Descripcion", "Creditos", "Sección"};
        modeloCursos = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaCursos = new JTable(modeloCursos);
        tablaCursos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(tablaCursos), BorderLayout.CENTER);
 
        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.setBorder(BorderFactory.createTitledBorder("Gestión de Cursos"));
 
        JTextField txtCodigo  = new JTextField();
        JTextField txtNombre  = new JTextField();
        JTextField txtDesc    = new JTextField();
        JTextField txtCreditos = new JTextField();
        JTextField txtSeccion = new JTextField();
 
        form.add(new JLabel("Codigo:"));      form.add(txtCodigo);
        form.add(new JLabel("Nombre:"));      form.add(txtNombre);
        form.add(new JLabel("Descripción:")); form.add(txtDesc);
        form.add(new JLabel("Creditos:"));    form.add(txtCreditos);
        form.add(new JLabel("Sección:"));     form.add(txtSeccion);
 
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        JButton btnAgregar    = new JButton("Agregar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar   = new JButton("Eliminar");
        JButton btnCSV        = new JButton("Cargar CSV");
        JButton btnLimpiar    = new JButton("Limpiar");
 

 
        botones.add(btnAgregar); botones.add(btnActualizar);
        botones.add(btnEliminar); botones.add(btnCSV); botones.add(btnLimpiar);
 
        JPanel sur = new JPanel(new BorderLayout());
        sur.add(form, BorderLayout.CENTER);
        sur.add(botones, BorderLayout.SOUTH);
        panel.add(sur, BorderLayout.SOUTH);
 
        cargarTablaCursos();
 
        tablaCursos.getSelectionModel().addListSelectionListener(e -> {
            int fila = tablaCursos.getSelectedRow();
            if (fila < 0) return;
            txtCodigo.setText ((String) modeloCursos.getValueAt(fila, 0));
            txtNombre.setText ((String) modeloCursos.getValueAt(fila, 1));
            txtDesc.setText   ((String) modeloCursos.getValueAt(fila, 2));
            txtCreditos.setText(String.valueOf(modeloCursos.getValueAt(fila, 3)));
            txtSeccion.setText((String) modeloCursos.getValueAt(fila, 4));
        });
 
        btnAgregar.addActionListener(e -> {
            String cod  = txtCodigo.getText().trim();
            String nom  = txtNombre.getText().trim();
            String desc = txtDesc.getText().trim();
            String sec  = txtSeccion.getText().trim();
            if (cod.isEmpty() || nom.isEmpty()) {
                JOptionPane.showMessageDialog(this, "codigo y nombre son obligatorios.");
                return;
            }
            int cred = 0;
            try { cred = Integer.parseInt(txtCreditos.getText().trim()); }
            catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Los créditos deben ser un número entero.");
                return;
            }
            boolean ok = controlador.agregarCurso(new Curso(cod, nom, desc, cred, sec));
            if (ok) {
                JOptionPane.showMessageDialog(this, "Curso registrado correctamente.");
                cargarTablaCursos();
                limpiarCampos(txtCodigo, txtNombre, txtDesc, txtCreditos, txtSeccion);
            }
        });
 
        btnActualizar.addActionListener(e -> {
            String cod  = txtCodigo.getText().trim();
            String nom  = txtNombre.getText().trim();
            String desc = txtDesc.getText().trim();
            String sec  = txtSeccion.getText().trim();
            if (cod.isEmpty()) { JOptionPane.showMessageDialog(this, "Seleccione o ingrese un codigo."); return; }
            int cred = 0;
            try { if (!txtCreditos.getText().trim().isEmpty()) cred = Integer.parseInt(txtCreditos.getText().trim()); }
            catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Los creitos deben ser un numero entero.");
                return;
            }
            controlador.actualizarCurso(cod,
            nom.isEmpty()  ? null : nom,
            desc.isEmpty() ? null : desc,
            cred, sec.isEmpty() ? null : sec);
            JOptionPane.showMessageDialog(this, "Curso actualizado.");
            cargarTablaCursos();
        });
 
        btnEliminar.addActionListener(e -> {
            String cod = txtCodigo.getText().trim();
            if (cod.isEmpty()) { JOptionPane.showMessageDialog(this, "Seleccione un curso."); return; }
            int c = JOptionPane.showConfirmDialog(this, "¿Eliminar curso " + cod + "?",
                    "confirmar eliminacion", JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) {
                boolean ok = controlador.eliminarCurso(cod);
                if (ok) {
                    JOptionPane.showMessageDialog(this, "curso eliminado.");
                    cargarTablaCursos();
                    limpiarCampos(txtCodigo, txtNombre, txtDesc, txtCreditos, txtSeccion);
                }
            }
        });
 
        btnCSV.addActionListener(e -> {
            String path = elegirCSV();
            if (path != null) {
                controlador.cargarCursosCSV(path);
                cargarTablaCursos();
            }
        });
 
        btnLimpiar.addActionListener(e -> {
            limpiarCampos(txtCodigo, txtNombre, txtDesc, txtCreditos, txtSeccion);
            tablaCursos.clearSelection();
        });
 
        return panel;
    }
    //--------------------------------PESTANA SECCIONES----------------------------
    private JPanel tablaSecciones() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));
 
        String[] cols = {"Código", "Curso", "Instructor", "Horario", "Semestre", "Cupos", "Estado"};
        modeloSecciones = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaSecciones = new JTable(modeloSecciones);
        tablaSecciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(tablaSecciones), BorderLayout.CENTER);
 
        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.setBorder(BorderFactory.createTitledBorder("Gestion de Secciones"));
 
        JTextField txtCodigo      = new JTextField();
        JTextField txtCurso       = new JTextField();
        JTextField txtInstructor  = new JTextField();
        JTextField txtHorario     = new JTextField();
        JTextField txtSemestre    = new JTextField();
        JTextField txtCupos       = new JTextField();
 
        form.add(new JLabel("Codigo Seccion:")); form.add(txtCodigo);
        form.add(new JLabel("Codigo Curso:")); form.add(txtCurso);
        form.add(new JLabel("Cod. Instructor:")); form.add(txtInstructor);
        form.add(new JLabel("Horario:"));      form.add(txtHorario);
        form.add(new JLabel("Semestre:"));   form.add(txtSemestre);
        form.add(new JLabel("Cupos:"));  form.add(txtCupos);
 
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        JButton btnAgregar    = new JButton("Agregar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar   = new JButton("Eliminar");
        JButton btnLimpiar    = new JButton("Limpiar");
 
        botones.add(btnAgregar); botones.add(btnActualizar);
        botones.add(btnEliminar); botones.add(btnLimpiar);
 
        JPanel sur = new JPanel(new BorderLayout());
        sur.add(form, BorderLayout.CENTER);
        sur.add(botones, BorderLayout.SOUTH);
        panel.add(sur, BorderLayout.SOUTH);
 
        cargarTablaSecciones();
 
        tablaSecciones.getSelectionModel().addListSelectionListener(e -> {
            int fila = tablaSecciones.getSelectedRow();
            if (fila < 0) return;
            txtCodigo.setText    ((String) modeloSecciones.getValueAt(fila, 0));
            txtCurso.setText     ((String) modeloSecciones.getValueAt(fila, 1));
            txtInstructor.setText((String) modeloSecciones.getValueAt(fila, 2));
            txtHorario.setText   ((String) modeloSecciones.getValueAt(fila, 3));
            txtSemestre.setText  ((String) modeloSecciones.getValueAt(fila, 4));
            txtCupos.setText     (String.valueOf(modeloSecciones.getValueAt(fila, 5)));
        });
 
        btnAgregar.addActionListener(e -> {
            String cod  = txtCodigo.getText().trim();
            String cur  = txtCurso.getText().trim();
            String ins  = txtInstructor.getText().trim();
            String hor  = txtHorario.getText().trim();
            String sem  = txtSemestre.getText().trim();
            
            if (cod.isEmpty() || cur.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Codigo y curso son obligatorios.");
                return;
            }
            //verificar que el curso exista
            if (controlador.buscarCurso(cur) == null) {
                JOptionPane.showMessageDialog(this, "El curso " + cur + " no existe.");
                return;
            }
            // verificar que el instructor exista 
            if (!ins.isEmpty() && controlador.buscarUsuario(ins) == null) {
                JOptionPane.showMessageDialog(this, "ERROR: El instructor " + ins + " no existe.");
                return;
            }
            int cupos = 30; // valor por defecto
            try { if (!txtCupos.getText().trim().isEmpty()) cupos = Integer.parseInt(txtCupos.getText().trim()); }
            catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "EEROR: los cupos deben ser un numero entero");
                return;
            }
            boolean ok = controlador.agregarSeccion(new Seccion(cod, cur, ins, hor, sem, cupos, null));
            if (ok) {
                // Incrementar secciones del instructor si fue asignado
                if (!ins.isEmpty()) {
                    Usuario u = controlador.buscarUsuario(ins);
                    if (u instanceof Usuario.Instructor) {
                        ((Usuario.Instructor) u).incrementarSecciones();
                        controlador.guardarUsuarios();
                    }
                }
                JOptionPane.showMessageDialog(this, "Seccion registrada correctamente.");
                cargarTablaSecciones();
                limpiarCampos(txtCodigo, txtCurso, txtInstructor, txtHorario, txtSemestre, txtCupos);
            }
        });
 
        btnActualizar.addActionListener(e -> {
            String cod = txtCodigo.getText().trim();
            String ins = txtInstructor.getText().trim();
            String hor = txtHorario.getText().trim();
            String sem = txtSemestre.getText().trim();
            if (cod.isEmpty()) { JOptionPane.showMessageDialog(this, "Seleccione o ingrese un codigo."); return; }
            int cupos = 0;
            try { if (!txtCupos.getText().trim().isEmpty()) cupos = Integer.parseInt(txtCupos.getText().trim()); }
            catch (NumberFormatException ex) { }
                    controlador.actualizarSeccion(cod,
                        ins.isEmpty() ? null : ins,
                        sem.isEmpty() ? null : sem,
                        hor.isEmpty() ? null : hor,
                        cupos);
                    JOptionPane.showMessageDialog(this, "Seccion actualizada.");
                    cargarTablaSecciones();
                
        });
 
        btnEliminar.addActionListener(e -> {
            String cod = txtCodigo.getText().trim();
            if (cod.isEmpty()) { JOptionPane.showMessageDialog(this, "Seleccione una seccion."); return; }
            int c = JOptionPane.showConfirmDialog(this, "¿Eliminar sección " + cod + "?",
                    "Confirmar eliminacion", JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) {
                boolean ok = controlador.eliminarSeccion(cod);
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Seccion eliminada.");
                    cargarTablaSecciones();
                    limpiarCampos(txtCodigo, txtCurso, txtInstructor, txtHorario, txtSemestre, txtCupos);
                }
            }
        });
 
        btnLimpiar.addActionListener(e -> {
            limpiarCampos(txtCodigo, txtCurso, txtInstructor, txtHorario, txtSemestre, txtCupos);
            tablaSecciones.clearSelection();
        });
 
        return panel;
    }
    //----------------------------------------------HILOS-----------------------------------
    
   private JPanel crearTabMonitor() {
        JPanel panel = new JPanel(new BorderLayout());

        txtConsola = new JTextArea();
        txtConsola.setEditable(false);
        txtConsola.setBackground(Color.BLACK);
        txtConsola.setForeground(Color.GREEN);
        txtConsola.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scroll = new JScrollPane(txtConsola);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }
        
    public void iniciarHilos() {
        if (txtConsola == null) return;

        txtConsola.setText("-----Iniciando monitor de hilos -----\n");

        tareasSesiones      = new HiloSesiones(txtConsola, controlador);
        tareasInscripciones = new HiloInscripcion(txtConsola, controlador);
        tareasEstadisticas  = new HiloEstadisticas(txtConsola, controlador);

        hiloSesiones      = new Thread(tareasSesiones,      "HILO - Sesiones");
        hiloInscripciones = new Thread(tareasInscripciones, "HILO -Inscripciones");
        hiloEstadisticas  = new Thread(tareasEstadisticas,  "HILO -Estadísticas");

        hiloSesiones.setDaemon(true);
        hiloInscripciones.setDaemon(true);
        hiloEstadisticas.setDaemon(true);

        hiloSesiones.start();
        hiloInscripciones.start();
        hiloEstadisticas.start();
    }

    public void detenerHilos() {
        if(tareaInscripciones != null)
        {
            tareaInscripciones.detener(); // cambia de activo a false
            hiloInscripciones.interrupt(); //interrumpe el sleep
        }
    }
    
    
    //metodos CARGA DE TABLAS
    
    private void cargarTablaUsuarios(DefaultTableModel modelo, String rol) {
        modelo.setRowCount(0);
        listaSimple<Usuario> lista = controlador.getUsuarios();
        for (int i = 0; i < lista.size(); i++) {
            Usuario u = lista.obtener(i);
            if (!u.getRol().equals(rol)) continue;
            if (u instanceof Usuario.Instructor) {
                modelo.addRow(new Object[]{
                    u.getCodigo(), u.getNombre(), u.getFechanacimiento(),
                    u.getGenero(), ((Usuario.Instructor) u).getSeccionesAsignadas()
                });
            } else if (u instanceof Usuario.Estudiante) {
                modelo.addRow(new Object[]{
                    u.getCodigo(), u.getNombre(), u.getFechanacimiento(),
                    u.getGenero(), ((Usuario.Estudiante) u).getCursosInscritos()
                });
            }
        }
    }
 
    private void cargarTablaCursos() {
        modeloCursos.setRowCount(0);
        listaSimple<Curso> lista = controlador.getCursos();
        for (int i = 0; i < lista.size(); i++) {
            Curso c = lista.obtener(i);
            modeloCursos.addRow(new Object[]{
                c.getCodigo(), c.getNombre(), c.getDescripcion(),
                c.getCreditos(), c.getSeccion()
            });
        }
    }
 
    private void cargarTablaSecciones() {
        modeloSecciones.setRowCount(0);
        listaSimple<Seccion> lista = controlador.getSEcciones();
        for (int i = 0; i < lista.size(); i++) {
            Seccion s = lista.obtener(i);
            modeloSecciones.addRow(new Object[]{
                s.getCodigo(), s.getCodigoCurso(), s.getCodigoInstructor(),
                s.getHorario(), s.getSemestre(), s.getCupos(), s.getEstado()
            });
        }
    }
 
    
    //UTILIDADES
 
    //Limpia uno o mas JTextComponents
    private void limpiarCampos(JComponent... campos) {
        for (JComponent c : campos) {
            if (c instanceof JTextField)     ((JTextField) c).setText("");
            else if (c instanceof JPasswordField) ((JPasswordField) c).setText("");
        }
    }
 
    //aplica color a un boto
    private void colorearBoton(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
    }
 
    //Abre un JFileChooser y retorna la ruta del CSV seleccionado 
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
