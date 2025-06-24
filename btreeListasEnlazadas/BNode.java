package btreeListasEnlazadas;

import LinkedList.ListaEnlazada;
import LinkedList.MensajeException;

//Clase que representa un nodo de un árbol B usando listas enlazadas
public class BNode<E extends Comparable<E>> {
    protected ListaEnlazada<E> claves;
    protected ListaEnlazada<BNode<E>> hijos;
    protected int contadorClaves;
    protected int maximoClaves;
    protected int maximoHijos;
    protected int idNodo;
    private static int idSiguiente = 1;

    //Constructor que inicializa un nodo con el orden del arbol
    public BNode(int orden) {
        this.claves = new ListaEnlazada<>();
        this.hijos = new ListaEnlazada<>();
        this.contadorClaves = 0;
        this.maximoClaves = orden - 1;
        this.maximoHijos = orden;
        this.idNodo = idSiguiente++;
    }

    //Calcula el número mínimo de claves que debe tener un nodo según el orden
    private int getMinimoClaves(int orden) {
        return (orden % 2 == 0) ? (int)Math.ceil(orden / 2.0) - 1 : orden / 2;
    }

    //Verifica si el nodo está lleno
    public boolean nodeFull(int orden) {
        return this.contadorClaves == orden - 1;
    }

    //Verifica si el nodo tiene menos claves del mínimo permitido
    public boolean nodeEmpty(int orden) {
        return this.contadorClaves < getMinimoClaves(orden);
    }

    //Busca una clave específica en el nodo y devuelve su posición
    public boolean searchNode(E clave, int[] posicion) {
        if (clave == null) {
            posicion[0] = -1;
            return false;
        }

        posicion[0] = 0;
        try {
            //Busca la posición donde debería estar la clave
            while (posicion[0] < this.contadorClaves && claves.searchK(posicion[0]).compareTo(clave) < 0) {
                posicion[0]++;
            }

            if (posicion[0] == this.contadorClaves) {
                return false;
            }

            return claves.searchK(posicion[0]).equals(clave);
        } catch (MensajeException e) {
            posicion[0] = -1;
            return false;
        }
    }

    //Inserta una nueva clave en el nodo manteniendo el orden
    public boolean insertKey(E clave) {
        if (clave == null || this.contadorClaves >= this.maximoClaves) {
            return false;
        }

        int[] posicion = new int[1];
        if (searchNode(clave, posicion)) {
            return false; //La clave ya existe
        }

        try {
            //Inserta la clave en la posición correcta para mantener el orden
            this.claves.insertPosicionK(clave, posicion[0]);
            this.contadorClaves++;
            return true;
        } catch (MensajeException e) {
            return false;
        }
    }

    //Elimina una clave del nodo y devuelve su índice
    public int removeKey(E clave) {
        if (clave == null) {
            return -1;
        }

        try {
            int indice = this.claves.search(clave);
            if (indice == -1) {
                return -1; //Clave no encontrada
            }

            //Elimina la clave de la lista
            this.claves.removeNodeK(indice);

            this.contadorClaves--;
            return indice;
        } catch (MensajeException e) {
            return -1;
        }
    }

    //Obtiene la clave en el índice especificado
    public E getKey(int indice) {
        if (indice >= 0 && indice < this.contadorClaves) {
            try {
                return this.claves.searchK(indice);
            } catch (MensajeException e) {
                return null;
            }
        }
        return null;
    }

    //Obtiene el nodo hijo en el índice especificado
    public BNode<E> getChild(int indice) {
        if (indice >= 0 && indice < this.hijos.length()) {
            try {
                return this.hijos.searchK(indice);
            } catch (MensajeException e) {
                return null;
            }
        }
        return null;
    }

    //Establece un nodo hijo en el índice especificado
    public void setChild(int indice, BNode<E> nodoHijo) {
        try {
            //Si el índice está dentro del rango actual, reemplaza el hijo existente
            if (indice >= 0 && indice < this.hijos.length()) {
                this.hijos.removeNodeK(indice);
                this.hijos.insertPosicionK(nodoHijo, indice);
            }
            //Si el índice es igual a la longitud actual, inserta al final
            else if (indice == this.hijos.length() && indice < this.maximoHijos) {
                this.hijos.insertLast(nodoHijo);
            }
        } catch (MensajeException e) {
            //Manejo de error al establecer el hijo
            System.err.println("Error al establecer hijo en posición " + indice);
        }
    }

    //Devuelve el número actual de claves en el nodo
    public int getCount() {
        return this.contadorClaves;
    }

    //Devuelve el ID único del nodo
    public int getIdNode() {
        return this.idNodo;
    }

    //Método auxiliar que convierte todas las claves del nodo en una cadena
    private String getClavesCadena() {
        StringBuilder resultado = new StringBuilder();
        try {
            for (int i = 0; i < this.contadorClaves; i++) {
                resultado.append(this.claves.searchK(i));
                if (i < this.contadorClaves - 1) {
                    resultado.append(", ");
                }
            }
        } catch (MensajeException e) {
            resultado.append("Error al obtener claves");
        }
        return resultado.toString();
    }

    //Representación en cadena del nodo mostrando su ID y claves
    @Override
    public String toString() {
        StringBuilder resultado = new StringBuilder();
        resultado.append("Nodo[").append(idNodo).append("]: (");
        resultado.append(getClavesCadena());
        resultado.append(")");
        return resultado.toString();
    }

    //Verifica si el nodo es una hoja (no tiene hijos)
    public boolean isLeaf() {
        return this.hijos.isEmpty();
    }

    //Devuelve el número actual de hijos del nodo
    public int getChildCount() {
        return this.hijos.length();
    }
}
