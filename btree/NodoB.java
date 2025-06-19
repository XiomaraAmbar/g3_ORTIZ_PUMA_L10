package btree;

import java.util.ArrayList;

public class NodoB<E extends Comparable<E>> {
    protected ArrayList<E> keys; //Lista de claves ordenadas
    protected ArrayList<NodoB<E>> childs; //Lista de hijos del nodo
    protected int count; //Número actual de claves en el nodo
    protected int maxKeys; //Máximo número de claves permitidas
    protected int pos; //Posición encontrada en búsquedas
    protected int idNode; //Identificador único del nodo
    private static int nextId = 1; //Contador estático para generar IDs únicos

    public NodoB(int n) {
        this.maxKeys = n;
        this.keys = new ArrayList<E>(n);
        this.childs = new ArrayList<NodoB<E>>(n + 1); //Un hijo más que claves
        this.count = 0;
        this.pos = -1;
        this.idNode = nextId++; //Asigna ID único y aumenta el contador

        //Inicializa arrays con nulls
        for(int i = 0; i < n; i++) {
            this.keys.add(null);
        }
        for(int i = 0; i < n + 1; i++) {
            this.childs.add(null);
        }
    }

    // Getters y setters
    public ArrayList<E> getClaves() {
        return keys;
    }

    public ArrayList<NodoB<E>> getHijos() {
        return childs;
    }

    // Verifica si el nodo está lleno sin parámetro
    public boolean nodeFull() {
        return this.count == this.maxKeys;
    }

    // Verifica si el nodo está lleno con parámetro
    public boolean nodeFull(int maxKeys) {
        return this.count == maxKeys;
    }

    //Verifica si el nodo está vacío
    public boolean nodeEmpty() {
        return this.count == 0;
    }

    //Verifica si es nodo hoja - sin hijos
    public boolean isLeaf() {
        return this.childs.get(0) == null;
    }

    //Verifica si es nodo hoja - sin hijos
    public boolean esHoja() {
        return isLeaf();
    }

    //Busca una clave en el nodo. Retorna true si la encuentra, false si no.
    //Guarda en "pos" la posición de la clave o del hijo donde buscar

    public boolean searchNode(E key, int[] pos) {
        if (key == null) {
            pos[0] = -1;
            return false;
        }

        int i = 0;
        //Recorre claves hasta encontrar posición correcta
        while (i < this.count && key.compareTo(this.keys.get(i)) > 0) {
            i++;
        }

        //Si se encuentra la clave exacta
        if (i < this.count && key.compareTo(this.keys.get(i)) == 0) {
            pos[0] = i;  // Posición de la clave encontrada
            return true;
        }

        //Clave no encontrada, pos indica el hijo donde buscar
        pos[0] = i;
        return false;
    }

    public boolean searchNode(E key) {
        if (key == null) {
            this.pos = -1;
            return false;
        }

        int i = 0;
        //Recorre claves hasta encontrar posición correcta
        while (i < this.count && key.compareTo(this.keys.get(i)) > 0) {
            i++;
        }

        //Si se encuentra la clave exacta
        if (i < this.count && key.compareTo(this.keys.get(i)) == 0) {
            this.pos = i;  //Posición de la clave encontrada
            return true;
        }

        //Clave no encontrada, pos indica el hijo donde buscar
        this.pos = i;
        return false;
    }

    //Obtiene el ID único del nodo
    public int getIdNode() {
        return this.idNode;
    }

    //Obtiene la posición de la última búsqueda
    public int getPos() {
        return this.pos;
    }

    //Inserta una clave manteniendo el orden ascendente
    public void insertKey(E key) {
        if (this.count < this.maxKeys) {
            int i = this.count - 1;

            //Mueve claves mayores hacia la derecha
            while (i >= 0 && this.keys.get(i).compareTo(key) > 0) {
                this.keys.set(i + 1, this.keys.get(i));
                i--;
            }

            //Inserta nueva clave en posición correcta
            this.keys.set(i + 1, key);
            this.count++;
        }
    }

    //Inserta un hijo en la posición especificada
    public void insertChild(int index, NodoB<E> child) {
        if (index <= this.count) {
            //Mueve hijos hacia la derecha
            for (int i = this.count; i > index; i--) {
                this.childs.set(i + 1, this.childs.get(i));
            }
            this.childs.set(index, child);
        }
    }

    // Convierte las claves del nodo a String
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Nodo[").append(idNode).append("]: [");
        for (int i = 0; i < this.count; i++) {
            if (i > 0) sb.append(", ");
            sb.append(this.keys.get(i));
        }
        sb.append("] (").append(this.count).append(" claves)");
        return sb.toString();
    }

    //Muestra información detallada del nodo
    public String detalleNodo() {
        StringBuilder sb = new StringBuilder();
        sb.append(toString());
        sb.append(" (Número de  claves: ").append(this.count);
        sb.append(", Es hoja?: ").append(isLeaf()).append(")");
        return sb.toString();
    }
}