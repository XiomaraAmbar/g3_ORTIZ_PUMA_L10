package btreeArrayList;

import java.util.ArrayList;

public class BNode<E extends Comparable<E>> {
    protected ArrayList<E> keys;
    protected ArrayList<BNode<E>> childs;
    protected int count;
    protected int idNode;
    private static int nextId = 1;

    public BNode(int n) {
        this.keys = new ArrayList<>(n - 1);
        this.childs = new ArrayList<>(n);
        this.count = 0;
        this.idNode = nextId++;

        // Inicializar claves (n-1 máximo)
        for (int i = 0; i < n - 1; i++) {
            this.keys.add(null);
        }

        // Inicializar hijos (n máximo)
        for (int i = 0; i < n; i++) {
            this.childs.add(null);
        }
    }

    public boolean nodeFull(int n) {
        return this.count == n - 1;
    }

    public boolean nodeEmpty(int n) {
        return this.count < ((n - 1) / 2);
    }

    public boolean searchNode(E cl, int[] pos) {
        if (cl == null) {
            pos[0] = -1;
            return false;
        }

        pos[0] = 0;
        while (pos[0] < this.count && this.keys.get(pos[0]).compareTo(cl) < 0) {
            pos[0]++;
        }

        if (pos[0] == this.count) {
            return false;
        }

        return this.keys.get(pos[0]).equals(cl);
    }

    // Método de inserción que mantiene el orden
    public boolean insertKey(E key) {
        if (key == null || this.count >= this.keys.size()) {
            return false;
        }

        int[] pos = new int[1];
        if (searchNode(key, pos)) {
            return false; // Ya existe
        }

        // Desplazar elementos hacia la derecha
        for (int i = this.count; i > pos[0]; i--) {
            this.keys.set(i, this.keys.get(i - 1));
        }

        // Insertar nueva clave
        this.keys.set(pos[0], key);
        this.count++;
        return true;
    }

    public int removeKey(E key) {
        if (key == null) {
            return -1;
        }

        int index = -1;
        // Buscar el índice del elemento
        for (int i = 0; i < this.count; i++) {
            if (this.keys.get(i).equals(key)) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            // Desplazar elementos hacia la izquierda
            for (int i = index; i < this.count - 1; i++) {
                this.keys.set(i, this.keys.get(i + 1));
            }

            // Limpiar última posición y decrementar contador
            this.keys.set(this.count - 1, null);

            // Solo manejar hijos si es necesario
            if (index + 1 < this.childs.size() && this.childs.get(index + 1) != null) {
                for (int i = index + 1; i < this.count; i++) {
                    this.childs.set(i, this.childs.get(i + 1));
                }
                this.childs.set(this.count, null);
            }

            this.count--;
        }

        return index;
    }

    // Getters simples
    public E getKey(int index) {
        if (index >= 0 && index < this.count) {
            return this.keys.get(index);
        }
        return null;
    }

    public BNode<E> getChild(int index) {
        if (index >= 0 && index < this.childs.size()) {
            return this.childs.get(index);
        }
        return null;
    }

    public void setChild(int index, BNode<E> child) {
        if (index >= 0 && index < this.childs.size()) {
            this.childs.set(index, child);
        }
    }

    public int getCount() {
        return this.count;
    }

    public int getIdNode() {
        return this.idNode;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Nodo[").append(idNode).append("]: (");
        for (int i = 0; i < this.count; i++) {
            sb.append(this.keys.get(i));
            if (i < this.count - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }
}
