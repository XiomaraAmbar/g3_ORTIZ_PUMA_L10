package Ejercicios;

import java.util.ArrayList;

public class BNode<E extends Comparable<E>> {
    protected ArrayList<E> claves;
    protected ArrayList<BNode<E>> hijos;
    protected int contadorClaves;
    protected int maximoClaves;
    protected int maximoHijos;
    protected int idNodo;
    private static int idSiguiente = 1;

    //Constructor que inicializa un nodo con el orden del arbol
    public BNode(int orden) {
        this.claves = new ArrayList<>();
        this.hijos = new ArrayList<>();
        //Inicializa las listas con valores null según el orden
        for (int i = 0; i < orden; i++) this.claves.add(null);
        for (int i = 0; i <= orden; i++) this.hijos.add(null);
        this.contadorClaves = 0;
        this.maximoClaves = orden - 1;
        this.maximoHijos = orden;
        this.idNodo = idSiguiente++;
    }

    public int getContadorClaves() {
        return contadorClaves;
    }

    //Calcula el número mínimo de claves requeridas según el orden
    private int getMinimoClaves(int orden) {
        return (orden % 2 == 0) ? (int)Math.ceil(orden / 2.0) - 1 : orden / 2;
    }

    //Verifica si el nodo está lleno
    public boolean nodeFull(int orden) {
        return this.contadorClaves == orden - 1;
    }

    //Verifica si el nodo tiene menos claves del mínimo requerido
    public boolean nodeEmpty(int orden) {
        return this.contadorClaves < getMinimoClaves(orden);
    }

    //Busca una clave en el nodo y retorna su posición
    public boolean searchNode(E clave, int[] pos) {
        pos[0] = 0;

        while (pos[0] < contadorClaves) {
            E actual = claves.get(pos[0]);
            if (actual == null) break; // 👉 Evita null

            int cmp = clave.compareTo(actual);
            if (cmp == 0) {
                return true; // Clave encontrada
            } else if (cmp < 0) {
                break; // Parar si clave es menor
            }
            pos[0]++;
        }
        return false; // No encontrada
    }

    //Inserta una clave en el nodo manteniendo el orden
    public boolean insertKey(E clave) {
        if (clave == null || this.contadorClaves >= this.maximoClaves) {
            return false;
        }

        int[] posicion = new int[1];
        //No permite claves duplicadas
        if (searchNode(clave, posicion)) {
            return false;
        }

        //Agrega espacio para la nueva clave
        this.claves.add(null);
        //Desplaza las claves mayores hacia la derecha
        for (int i = this.contadorClaves; i > posicion[0]; i--) {
            claves.set(i, claves.get(i - 1));
        }
        //Inserta la nueva clave en su posición correcta
        claves.set(posicion[0], clave);
        this.contadorClaves++;
        return true;
    }

    //Elimina una clave del nodo y retorna su posición anterior
    public int removeKey(E clave) {
        if (clave == null) {
            return -1;
        }

        int indice = claves.indexOf(clave);
        if (indice == -1) {
            return -1;
        }

        //Desplaza las claves hacia la izquierda para llenar el hueco
        for (int i = indice; i < contadorClaves - 1; i++) {
            claves.set(i, claves.get(i + 1));
        }
        //Limpia la última posición
        claves.set(contadorClaves - 1, null);
        contadorClaves--;
        return indice;
    }

    //Obtiene la clave en la posición especificada
    public E getKey(int indice) {
        if (indice >= 0 && indice < this.contadorClaves) {
            return this.claves.get(indice);
        }
        return null;
    }

    //Obtiene el nodo hijo en la posición especificada
    public BNode<E> getChild(int indice) {
        if (indice >= 0 && indice < this.hijos.size()) {
            return this.hijos.get(indice);
        }
        return null;
    }

    //Establece un nodo hijo en la posición especificada
    public void setChild(int indice, BNode<E> nodoHijo) {
        if (indice >= 0 && indice < this.hijos.size()) {
            this.hijos.set(indice, nodoHijo);
        } else if (indice == this.hijos.size() && indice < this.maximoHijos) {
            this.hijos.add(nodoHijo);
        }
    }

    //Retorna el número actual de claves en el nodo
    public int getCount() {
        return this.contadorClaves;
    }

    //Retorna el identificador único del nodo
    public int getIdNode() {
        return this.idNodo;
    }

    //Genera una cadena con todas las claves del nodo separadas por comas
    private String getClavesCadena() {
        StringBuilder resultado = new StringBuilder();
        for (int i = 0; i < this.contadorClaves; i++) {
            resultado.append(this.claves.get(i));
            if (i < this.contadorClaves - 1) {
                resultado.append(", ");
            }
        }
        return resultado.toString();
    }

    //Representación en cadena del nodo mostrando ID y claves
    @Override
    public String toString() {
        StringBuilder resultado = new StringBuilder();
        resultado.append("Nodo[").append(idNodo).append("]: (");
        resultado.append(getClavesCadena());
        resultado.append(")");
        return resultado.toString();
    }

    //Verifica si el nodo es una hoja revisando si todos los hijos son null
    public boolean isLeaf() {
        for (int i = 0; i < hijos.size(); i++) {
            if (hijos.get(i) != null) return false;
        }
        return true;
    }

    //Retorna el número total de posiciones para hijos
    public int getChildCount() {
        return this.hijos.size();
    }
}
