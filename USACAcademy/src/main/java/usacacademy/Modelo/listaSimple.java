package usacacademy.Modelo;
import java.io.Serializable;

public class listaSimple<T> implements Serializable {
    private Object[] elementos;
    private int contador;

    public listaSimple() {
        this.elementos = new Object[10]; //tamaño inicial
        this.contador = 0;
    }

    public void agregar(T elemento) {
        if(contador == this.elementos.length) {
            expandir();
        }
        elementos[contador++] = elemento;
    }

    private void expandir() {
        Object[] nuevo = new Object[this.elementos.length * 2];
        System.arraycopy(this.elementos, 0, nuevo, 0, this.elementos.length);
        elementos = nuevo;
    }

    public T obtener(int indice){
        return (T) elementos[indice];
    }

    public void eliminar(int indice) {
        if (indice < 0 || indice >= contador) {
            throw new IndexOutOfBoundsException("indice fuera de rango");
        }
        for (int i = indice; i < contador - 1; i++) {
            elementos[i] = elementos[i + 1];
        }
        elementos[--contador] = null; // Limpiar referencia
    }

    public int size(){
        return contador;
    }
}
