package btree;

public class BTree0<E extends Comparable<E>> {
    private NodoB<E> root; //Nodo raíz del árbol
    private int orden; //Orden del árbol B -> número máximo de claves + 1
    private boolean up; //Indicador si hay división de nodo
    private NodoB<E> nDes; //Nodo resultante de una división

    //Constructor
    public BTree0(int orden) {
        this.orden = orden;
        this.root = null;
        this.up = false;
        this.nDes = null;
    }

    //Verifica si el árbol está vacío
    public boolean isEmpty() {
        return this.root == null;
    }

    //Inserta una nueva clave en el árbol
    public void insert(E clave) {
        up = false; //Inicializa el indicador de división
        E mediana; //Clave mediana en caso de división
        NodoB<E> pnew; //Nuevo nodo raíz si es necesario

        // Intentar insertar la clave
        mediana = push(this.root, clave);

        // Si hubo división en la raíz, crear nueva raíz
        if (up) {
            pnew = new NodoB<E>(this.orden);
            pnew.count = 1;
            pnew.keys.set(0, mediana);      // Mediana como única clave
            pnew.childs.set(0, this.root);  // Raíz anterior como hijo izquierdo
            pnew.childs.set(1, nDes);       // Nuevo nodo como hijo derecho
            this.root = pnew;               // Actualizar raíz
        }
    }

    // Método recursivo para insertar una clave
    private E push(NodoB<E> current, E cl) {
        int[] pos = new int[1];        // Array para obtener posición por referencia
        E mediana;

        // Caso base: nodo nulo (crear nuevo nodo hoja)
        if (current == null) {
            up = true;                 // Indicar que hay que insertar hacia arriba
            nDes = null;              // No hay nodo derecho
            return cl;                // Retornar la clave a insertar
        } else {
            boolean found;
            // Buscar la clave en el nodo actual
            found = current.searchNode(cl, pos);

            // Si la clave ya existe, no insertar
            if (found) {
                System.out.println("Item duplicado: " + cl);
                up = false;
                return null;
            }

            // Insertar recursivamente en el hijo correspondiente
            mediana = push(current.childs.get(pos[0]), cl);

            // Si hay división en el nivel inferior
            if (up) {
                // Verificar si el nodo actual está lleno
                if (current.nodeFull(this.orden - 1)) {
                    // Dividir el nodo actual
                    mediana = dividedNode(current, mediana, pos[0]);
                } else {
                    // Insertar la mediana en el nodo actual
                    up = false;
                    putNode(current, mediana, nDes, pos[0]);
                }
            }
            return mediana;
        }
    }

    // Inserta una clave y su hijo derecho en un nodo en la posición k
    private void putNode(NodoB<E> current, E cl, NodoB<E> rd, int k) {
        int i;

        // Desplazar claves y hijos hacia la derecha
        for (i = current.count - 1; i >= k; i--) {
            current.keys.set(i + 1, current.keys.get(i));        // Mover claves
            current.childs.set(i + 2, current.childs.get(i + 1)); // Mover hijos
        }

        // Insertar nueva clave y hijo
        current.keys.set(k, cl);        // Insertar clave en posición k
        current.childs.set(k + 1, rd);  // Insertar hijo derecho en k+1
        current.count++;                // Incrementar contador de claves
    }

    // Divide un nodo lleno e inserta una nueva clave
    private E dividedNode(NodoB<E> current, E cl, int k) {
        NodoB<E> rd = nDes;            // Guardar nodo derecho temporal
        int i, posMdna;

        // Calcular posición de la mediana
        posMdna = (k <= this.orden / 2) ? this.orden / 2 : this.orden / 2 + 1;

        // Crear nuevo nodo para la mitad derecha
        nDes = new NodoB<E>(this.orden);

        // Mover claves de la mitad derecha al nuevo nodo
        for (i = posMdna; i < this.orden - 1; i++) {
            nDes.keys.set(i - posMdna, current.keys.get(i));
            nDes.childs.set(i - posMdna + 1, current.childs.get(i + 1));
        }

        // Establecer contadores
        nDes.count = (this.orden - 1) - posMdna;    // Claves en nodo derecho
        current.count = posMdna;                     // Claves en nodo izquierdo

        // Insertar la nueva clave en el nodo correspondiente
        if (k <= this.orden / 2) {
            putNode(current, cl, rd, k);             // Insertar en nodo izquierdo
        } else {
            putNode(nDes, cl, rd, k - posMdna);      // Insertar en nodo derecho
        }

        // Obtener la mediana (última clave del nodo izquierdo)
        E median = current.keys.get(current.count - 1);

        // Ajustar punteros: el primer hijo del nodo derecho es el último hijo del izquierdo
        nDes.childs.set(0, current.childs.get(current.count));
        current.count--;                             // Remover mediana del nodo izquierdo

        return median;                              // Retornar mediana para subir
    }

    // Método para mostrar el árbol (recorrido en orden)
    public void inOrder() {
        if (isEmpty()) {
            System.out.println("Árbol vacío");
        } else {
            System.out.println("Recorrido en orden:");
            inOrderRec(this.root);
            System.out.println();
        }
    }

    // Recorrido recursivo en orden
    private void inOrderRec(NodoB<E> nodo) {
        if (nodo != null) {
            int i;
            // Recorrer hijos y claves intercaladamente
            for (i = 0; i < nodo.count; i++) {
                inOrderRec(nodo.childs.get(i));      // Visitar hijo izquierdo
                System.out.print(nodo.keys.get(i) + " "); // Imprimir clave
            }
            inOrderRec(nodo.childs.get(i));          // Visitar último hijo
        }
    }

    // Método para mostrar la estructura del árbol
    public void showTree() {
        if (isEmpty()) {
            System.out.println("Árbol vacío");
        } else {
            System.out.println("Estructura del árbol:");
            showTreeRec(this.root, 0);
        }
    }

    // Mostrar árbol recursivamente con indentación
    private void showTreeRec(NodoB<E> nodo, int nivel) {
        if (nodo != null) {
            // Imprimir indentación según el nivel
            for (int i = 0; i < nivel; i++) {
                System.out.print("  ");
            }
            // Mostrar el nodo con su ID
            System.out.println(nodo.toString());

            // Mostrar hijos recursivamente
            for (int i = 0; i <= nodo.count; i++) {
                if (nodo.childs.get(i) != null) {
                    showTreeRec(nodo.childs.get(i), nivel + 1);
                }
            }
        }
    }

    // Buscar una clave en el árbol
    public boolean search(E key) {
        return searchRec(this.root, key);
    }

    // Búsqueda recursiva
    private boolean searchRec(NodoB<E> nodo, E key) {
        if (nodo == null) {
            return false;
        }

        int[] pos = new int[1];
        boolean found = nodo.searchNode(key, pos);

        if (found) {
            return true;    // Clave encontrada
        } else {
            // Buscar en el hijo correspondiente
            return searchRec(nodo.childs.get(pos[0]), key);
        }
    }
}