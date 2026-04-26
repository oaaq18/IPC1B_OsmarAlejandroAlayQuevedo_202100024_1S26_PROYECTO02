# Manual Tecnico — Sancarlista Academy
**Curso:** Introduccion a la Programacion y Computacion 1 — USAC
**Lenguaje:** Java | **IDE:** NetBeans | **Patron:** MVC

---

**Estudiante:** Osmar Alejandro Alay Quevedo
**Carne:** 202100024
**Seccion:** B
**Fecha:** 25/04/2026

---

## 1. Descripcion General

Sancarlista Academy es una aplicacion de escritorio desarrollada en Java con interfaz grafica Swing. Permite gestionar una plataforma academica con tres roles: Administrador, Instructor y Estudiante. El sistema implementa el patron MVC (Modelo-Vista-Controlador), herencia, polimorfismo, hilos concurrentes y persistencia mediante serializacion de objetos.

---

## 2. Arquitectura del Sistema

El proyecto sigue el patron MVC dividido en tres paquetes principales:

```
usacacademy/
    USACAcademy.java          -- punto de entrada
    Modelo/                   -- datos y logica de negocio
        Usuario.java
        Curso.java
        Seccion.java
        Nota.java
        inscripcion.java
        listaSimple.java
        HiloSesiones.java
        HiloInscripcion.java
        HiloEstadisticas.java
    controlador/              -- logica de control
        AppControlador.java
        Reportes.java
        Bitacora.java
    vista/                    -- interfaz grafica
        VistaPrincipal.java
        PanelLogin.java
        PanelAdministrador.java
        PanelInstructor.java
        PanelEstudiante.java
```

---

## 3. Modelo

### 3.1 Clase Usuario (abstracta)

Clase base de la que heredan Administrador, Instructor y Estudiante. Implementa `Serializable` para persistencia.

```java
public abstract class Usuario implements Serializable {
    protected String codigo, nombre, password, fechanacimiento, genero, rol;

    public abstract String getTipoUsuario();

    public boolean autenticar(String password) {
        return this.password.equals(password);
    }
    // clases hijas estaticas anidadas:
    public static class Administrador extends Usuario { ... }
    public static class Instructor    extends Usuario { ... }
    public static class Estudiante    extends Usuario { ... }
}
```

La jerarquia de herencia es:
```
Usuario (abstracta)
    Administrador
    Instructor  -- agrega seccionesAsignadas
    Estudiante  -- agrega cursosInscritos
```

### 3.2 Clase Curso

Representa un curso del catalogo academico. Formato CSV de carga: `Codigo, NombreCurso, Descripcion, Creditos, Seccion`.

```java
public class Curso implements Serializable {
    private String codigo, nombre, descripcion, seccion;
    private int creditos;
    // getters y setters (codigo no tiene setter, es inmutable)
}
```

### 3.3 Clase Seccion

Contiene la lista de estudiantes inscritos usando `listaSimple<String>`. Maneja inscripciones y desasignaciones internamente.

```java
public class Seccion implements Serializable {
    private listaSimple<String> estudiantesInscritos;

    public boolean inscribirEstudiante(String codigoEstudiante) {
        if (estaInscrito(codigoEstudiante)) return false;
        if (cupos <= 0) return false;
        estudiantesInscritos.agregar(codigoEstudiante);
        cupos--;
        return true;
    }
}
```

### 3.4 Clase Nota

Almacena la calificacion de un estudiante en una seccion. El promedio se calcula en el controlador usando ponderacion.

```java
public class Nota implements Serializable {
    private String codigoCurso, codigoSeccion, codigoEstudiante, etiqueta, fecha;
    private double ponderacion, valor;

    // aporte ponderado de esta nota al promedio
    public double getNotaPonderacion() {
        return ponderacion * valor;
    }
}
```

### 3.5 listaSimple

Estructura de datos propia que reemplaza ArrayList (prohibido por el enunciado). Implementa un arreglo dinamico con expansion automatica.

```java
public class listaSimple<T> implements Serializable {
    private Object[] elementos;
    private int contador;

    public void agregar(T elemento) {
        if (contador == elementos.length) expandir(); // duplica capacidad
        elementos[contador++] = elemento;
    }

    private void expandir() {
        Object[] nuevo = new Object[elementos.length * 2];
        System.arraycopy(elementos, 0, nuevo, 0, elementos.length);
        elementos = nuevo;
    }
}
```

---

## 4. Controlador

### 4.1 AppControlador

Clase central que maneja todas las operaciones CRUD y la persistencia. Mantiene las listas principales en memoria y las sincroniza con archivos `.ser`.

```java
public class AppControlador {
    private listaSimple<Usuario>     usuarios;
    private listaSimple<Curso>       cursos;
    private listaSimple<Seccion>     secciones;
    private listaSimple<Nota>        notas;
    private listaSimple<inscripcion> inscripcionesPendientes;

    private static final String ARCHIVO_USUARIOS  = "usuarios.ser";
    private static final String ARCHIVO_CURSOS    = "cursos.ser";
    private static final String ARCHIVO_SECCIONES = "secciones.ser";
    private static final String ARCHIVO_NOTAS     = "notas.ser";
}
```

**Persistencia:** Al iniciar, carga los `.ser` si existen. Tras cada operacion CRUD, guarda el archivo correspondiente.

```java
private void cargarArchivos() {
    usuarios  = cargarArchivo(ARCHIVO_USUARIOS,  usuarios);
    cursos    = cargarArchivo(ARCHIVO_CURSOS,    cursos);
    secciones = cargarArchivo(ARCHIVO_SECCIONES, secciones);
    notas     = cargarArchivo(ARCHIVO_NOTAS,     notas);
}
```

**Autenticacion:** Recorre la lista de usuarios comparando codigo y contrasena.

```java
public Usuario autenticar(String usuario, String password) {
    for (int i = 0; i < usuarios.size(); i++) {
        Usuario u = usuarios.obtener(i);
        if (u.getCodigo().equals(usuario) && u.getPassword().equals(password))
            return u;
    }
    return null;
}
```

**Calculo de promedio:** Formula ponderada: Promedio = Suma(Nota x Ponderacion) / Suma(Ponderacion).

```java
public double calcularPromedio(String codigoSeccion, String codigoEstudiante) {
    double sumaAportes = 0, sumaPonderacion = 0;
    for (int i = 0; i < notas.size(); i++) {
        Nota n = notas.obtener(i);
        if (n.getCodigoSeccion().equals(codigoSeccion)
                && n.getCodigoEstudiante().equals(codigoEstudiante)) {
            sumaAportes     += n.getNotaPonderacion();
            sumaPonderacion += n.getPonderacion();
        }
    }
    return sumaPonderacion == 0 ? 0 : sumaAportes / sumaPonderacion;
}
```

**Validaciones en agregarNota:** Verifica que la seccion exista, que el instructor tenga asignada esa seccion, que el estudiante este inscrito, que la nota sea 0-100, que la ponderacion sea mayor a 0, y que no haya duplicado de etiqueta.

### 4.2 Bitacora

Clase estatica que escribe un registro de texto en `bitacora.txt` con append, sin sobreescribir.

```java
public class Bitacora {
    private static final String ARCHIVO = "bitacora.txt";

    public static void registrar(String tipoUsuario, String codigoUsuario,
                                  String operacion, String descripcion) {
        String fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
        String linea = "[" + fecha + "] | " + tipoUsuario + " | "
                     + codigoUsuario + " | " + operacion + " | " + descripcion;
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO, true))) {
            pw.println(linea); // true = append
        } catch (Exception e) { ... }
    }
}
```

Formato del registro:
```
[25/04/2026 14:30:00] | ADMINISTRADOR | admin | LOGIN | Inicio de sesion exitoso
```

### 4.3 Reportes

Genera archivos PDF (usando iText 5) y CSV en la carpeta raiz del proyecto. Cada metodo de reporte construye una `listaSimple<String[]>` con los datos y llama a `generarPDF` y `generarCSV`.

```java
// nombre con fecha: 25_04_2026_14_30_00_TipoReporte.pdf
private String generarNombre(String tipoReporte, String extension) {
    String fecha = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss").format(new Date());
    return fecha + "_" + tipoReporte + "." + extension;
}
```

Reportes disponibles: Inscripciones por Curso, Inscripciones por Semestre, Historial Individual de Estudiante, Calificaciones por Seccion, Historial por Semestre (estudiante), Bitacora del Sistema.

---

## 5. Hilos

El sistema implementa tres hilos concurrentes que se ejecutan en segundo plano durante la sesion activa del administrador.

| Hilo | Intervalo | Mensaje |
|------|-----------|---------|
| HiloSesiones | 10 seg | Usuarios Activos: N |
| HiloInscripcion | 8 seg | Inscripciones Pendientes: N |
| HiloEstadisticas | 15 seg | Cursos: N / Estudiantes: N / Notas: N |

Cada hilo implementa `Runnable` con un `while(activo)` controlado por bandera booleana. Se detienen al llamar `detener()` + `interrupt()`:

```java
public class HiloSesiones implements Runnable {
    private volatile boolean activo;

    public void detener() { this.activo = false; }

    @Override
    public void run() {
        while (activo) {
            try {
                // actualizar consola con SwingUtilities.invokeLater()
                Thread.sleep(10_000);
            } catch (InterruptedException e) {
                break; // salida limpia
            }
        }
    }
}
```

Los hilos se marcan como `daemon` para que no bloqueen el cierre de la JVM:
```java
hiloSesiones.setDaemon(true);
hiloSesiones.start();
```

Los metodos del controlador usados por los hilos son `synchronized` para evitar condiciones de carrera:
```java
public synchronized int getInscripcionesPendientes() {
    return inscripcionesPendientes.size();
}
```

---

## 6. Vista

### 6.1 VistaPrincipal

Ventana principal (`JFrame`) con `CardLayout` que permite navegar entre paneles sin abrir nuevas ventanas. Guarda el `usuarioActual` para pasarlo al panel correspondiente.

```java
public void cambiarVista(String nombreVista) {
    cardLayout.show(panelPrincipal, nombreVista);
    if ("Instructor".equals(nombreVista)) {
        panelInstructor.setInstructor((Usuario.Instructor) usuarioActual);
    }
    if ("Estudiante".equals(nombreVista)) {
        panelEstudiante.setEstudiante((Usuario.Estudiante) usuarioActual);
    }
    if ("Administrador".equals(nombreVista)) {
        panelAdministrador.iniciarHilos();
    }
}
```

### 6.2 Patron comun de los paneles

Todos los paneles siguen la misma estructura:

```
JPanel (BorderLayout)
    NORTH  -- titulo
    CENTER -- JTabbedPane con pestanas
    SOUTH  -- boton logout
```

Cada pestana sigue este patron:
```
JPanel (BorderLayout)
    CENTER -- JScrollPane > JTable
    SOUTH  -- JPanel sur (BorderLayout)
                  CENTER -- formulario (GridLayout 2 columnas)
                  SOUTH  -- botones (FlowLayout izquierda)
```

El llenado automatico del formulario al seleccionar fila usa `ListSelectionListener`:
```java
tabla.getSelectionModel().addListSelectionListener(e -> {
    int fila = tabla.getSelectedRow();
    if (fila < 0) return;
    txtCodigo.setText((String) modelo.getValueAt(fila, 0));
    // ... llenar los demas campos
});
```

### 6.3 Recarga de tablas

Despues de cada operacion CRUD se llama al metodo de carga correspondiente que limpia y rellena el modelo:

```java
private void cargarTablaUsuarios(DefaultTableModel modelo, String rol) {
    modelo.setRowCount(0); // limpiar todas las filas
    listaSimple<Usuario> lista = controlador.getUsuarios();
    for (int i = 0; i < lista.size(); i++) {
        Usuario u = lista.obtener(i);
        if (!u.getRol().equals(rol)) continue; // filtrar por rol
        modelo.addRow(new Object[]{ u.getCodigo(), u.getNombre(), ... });
    }
}
```

---

## 7. Carga de archivos CSV

Cada entidad tiene su propio metodo de carga en `AppControlador`. El proceso es: leer linea por linea, saltar el encabezado, validar formato y duplicados, agregar a la lista, guardar en `.ser`.

Formatos esperados:

| Entidad | Formato CSV |
|---------|-------------|
| Instructores / Estudiantes | Codigo, Nombre, FechaNacimiento, Genero, Contrasena |
| Cursos | Codigo, NombreCurso, Descripcion, Creditos, Seccion |
| Notas | CodigoCurso, CodigoSeccion, CodigoEstudiante, Etiqueta, Ponderacion, Nota, Fecha |

---

## 8. Persistencia

Los datos se guardan usando `ObjectOutputStream` y se cargan con `ObjectInputStream`. Todas las clases del modelo implementan `Serializable`.

```java
private void guardarArchivo(String archivo, Object objeto) {
    try (ObjectOutputStream oos = new ObjectOutputStream(
            new FileOutputStream(archivo))) {
        oos.writeObject(objeto);
    } catch (Exception ex) { ... }
}
```

Archivos generados en la raiz del proyecto:
- `usuarios.ser`
- `cursos.ser`
- `secciones.ser`
- `notas.ser`
- `bitacora.txt`

---

## 9. Usuario administrador por defecto

Al iniciar por primera vez (sin `.ser`), el sistema crea automaticamente el usuario administrador:

```java
if (usuarios.size() == 0) {
    usuarios.agregar(new Usuario.Administrador(
        "admin", "NombreAdmin", "IPC1B", "18/09/2000", "H"));
}
```

Credenciales: Codigo `admin` / Contrasena `IPC1B`
