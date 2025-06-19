package btree;

public class BTree2<E extends Comparable<E>> {
    private NodoB<E> root;     // Nodo raíz del árbol
    private int orden;         // Orden del árbol B (número máximo de claves + 1)
    private boolean up;        // Bandera que indica si hay división de nodo
    private NodoB<E> nDes;     // Nodo resultante de una división

    // Constructor: inicializa el árbol con el orden especificado
    public BTree2(int orden) {
        this.orden = orden;
        this.root = null;
        this.up = false;
        this.nDes = null;
    }

    // Verifica si el árbol está vacío
    public boolean isEmpty() {
        return this.root == null;
    }

    // Inserta una nueva clave en el árbol
    public void insert(E cl) {
        up = false;                    // Inicializar bandera de división
        E mediana;                     // Clave mediana en caso de división
        NodoB<E> pnew;                // Nuevo nodo raíz si es necesario

        // Intentar insertar la clave
        mediana = push(this.root, cl);

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

    // ==================== MÉTODOS DE ELIMINACIÓN ====================

    // Eliminar una clave del árbol
    public boolean delete(E key) {
        if (isEmpty()) {
            System.out.println("Árbol vacío, no se puede eliminar: " + key);
            return false;
        }

        boolean deleted = deleteRec(this.root, key);

        // Si la raíz queda vacía después de una fusión, actualizar raíz
        if (this.root != null && this.root.count == 0 && !this.root.isLeaf()) {
            this.root = this.root.childs.get(0);
        }

        return deleted;
    }

    // Eliminación recursiva
    private boolean deleteRec(NodoB<E> nodo, E key) {
        if (nodo == null) {
            return false;
        }

        int[] pos = new int[1];
        boolean found = nodo.searchNode(key, pos);

        if (found) {
            // Caso 1: Clave encontrada en nodo hoja
            if (nodo.isLeaf()) {
                removeFromLeaf(nodo, pos[0]);
                return true;
            }
            // Caso 2: Clave encontrada en nodo interno
            else {
                return removeFromInternal(nodo, pos[0], key);
            }
        } else {
            // Caso 3: Clave no encontrada, buscar en hijo correspondiente
            NodoB<E> child = nodo.childs.get(pos[0]);
            if (child == null) {
                return false;
            }

            boolean deleted = deleteRec(child, key);

            // Verificar underflow después de la eliminación
            if (deleted && hasUnderflow(child)) {
                fixUnderflow(nodo, pos[0]);
            }

            return deleted;
        }
    }

    // Remover clave de nodo hoja
    private void removeFromLeaf(NodoB<E> nodo, int pos) {
        // Desplazar claves hacia la izquierda
        for (int i = pos; i < nodo.count - 1; i++) {
            nodo.keys.set(i, nodo.keys.get(i + 1));
        }
        nodo.keys.set(nodo.count - 1, null);
        nodo.count--;
    }

    // Remover clave de nodo interno
    private boolean removeFromInternal(NodoB<E> nodo, int pos, E key) {
        E keyToDelete = nodo.keys.get(pos);
        NodoB<E> leftChild = nodo.childs.get(pos);
        NodoB<E> rightChild = nodo.childs.get(pos + 1);

        // Caso 2a: Hijo izquierdo tiene suficientes claves
        if (leftChild.count >= getMinKeys()) {
            E predecessor = getMaxKey(leftChild);
            nodo.keys.set(pos, predecessor);
            return deleteRec(leftChild, predecessor);
        }
        // Caso 2b: Hijo derecho tiene suficientes claves
        else if (rightChild.count >= getMinKeys()) {
            E successor = getMinKey(rightChild);
            nodo.keys.set(pos, successor);
            return deleteRec(rightChild, successor);
        }
        // Caso 2c: Ambos hijos tienen mínimas claves - fusionar
        else {
            mergeNodes(nodo, pos);
            return deleteRec(leftChild, key);
        }
    }

    // Verificar si un nodo tiene underflow
    private boolean hasUnderflow(NodoB<E> nodo) {
        return nodo != null && nodo.count < getMinKeys();
    }

    // Obtener número mínimo de claves para un nodo
    private int getMinKeys() {
        return (this.orden - 1) / 2;  // Mínimo de claves por nodo
    }

    // Corregir underflow mediante redistribución o fusión
    private void fixUnderflow(NodoB<E> padre, int childIndex) {
        NodoB<E> child = padre.childs.get(childIndex);

        // Intentar redistribuir desde hermano derecho
        if (childIndex < padre.count &&
                padre.childs.get(childIndex + 1).count > getMinKeys()) {
            redistributeFromRight(padre, childIndex);
        }
        // Intentar redistribuir desde hermano izquierdo
        else if (childIndex > 0 &&
                padre.childs.get(childIndex - 1).count > getMinKeys()) {
            redistributeFromLeft(padre, childIndex);
        }
        // Fusionar con hermano derecho
        else if (childIndex < padre.count) {
            mergeNodes(padre, childIndex);
        }
        // Fusionar con hermano izquierdo
        else if (childIndex > 0) {
            mergeNodes(padre, childIndex - 1);
        }
    }

    // Redistribución desde hermano derecho
    private void redistributeFromRight(NodoB<E> padre, int childIndex) {
        NodoB<E> child = padre.childs.get(childIndex);
        NodoB<E> rightSibling = padre.childs.get(childIndex + 1);

        // El separador del padre baja al hijo
        child.keys.set(child.count, padre.keys.get(childIndex));
        child.count++;

        // Si no es hoja, mover también el primer hijo del hermano
        if (!child.isLeaf()) {
            child.childs.set(child.count, rightSibling.childs.get(0));
            // Desplazar hijos del hermano derecho
            for (int i = 0; i < rightSibling.count; i++) {
                rightSibling.childs.set(i, rightSibling.childs.get(i + 1));
            }
            rightSibling.childs.set(rightSibling.count, null);
        }

        // La primera clave del hermano sube como nuevo separador
        padre.keys.set(childIndex, rightSibling.keys.get(0));

        // Desplazar claves del hermano derecho
        for (int i = 0; i < rightSibling.count - 1; i++) {
            rightSibling.keys.set(i, rightSibling.keys.get(i + 1));
        }
        rightSibling.keys.set(rightSibling.count - 1, null);
        rightSibling.count--;
    }

    // Redistribución desde hermano izquierdo
    private void redistributeFromLeft(NodoB<E> padre, int childIndex) {
        NodoB<E> child = padre.childs.get(childIndex);
        NodoB<E> leftSibling = padre.childs.get(childIndex - 1);

        // Desplazar claves del hijo hacia la derecha
        for (int i = child.count; i > 0; i--) {
            child.keys.set(i, child.keys.get(i - 1));
        }

        // Si no es hoja, desplazar también los hijos
        if (!child.isLeaf()) {
            for (int i = child.count + 1; i > 0; i--) {
                child.childs.set(i, child.childs.get(i - 1));
            }
            // Mover el último hijo del hermano izquierdo
            child.childs.set(0, leftSibling.childs.get(leftSibling.count));
            leftSibling.childs.set(leftSibling.count, null);
        }

        // El separador del padre baja al hijo
        child.keys.set(0, padre.keys.get(childIndex - 1));
        child.count++;

        // La última clave del hermano izquierdo sube como nuevo separador
        padre.keys.set(childIndex - 1, leftSibling.keys.get(leftSibling.count - 1));
        leftSibling.keys.set(leftSibling.count - 1, null);
        leftSibling.count--;
    }

    // Fusionar nodo con su hermano derecho
    private void mergeNodes(NodoB<E> padre, int childIndex) {
        NodoB<E> leftChild = padre.childs.get(childIndex);
        NodoB<E> rightChild = padre.childs.get(childIndex + 1);

        // El separador del padre baja al hijo izquierdo
        leftChild.keys.set(leftChild.count, padre.keys.get(childIndex));
        leftChild.count++;

        // Copiar todas las claves del hijo derecho al izquierdo
        for (int i = 0; i < rightChild.count; i++) {
            leftChild.keys.set(leftChild.count + i, rightChild.keys.get(i));
        }

        // Copiar todos los hijos del hijo derecho al izquierdo
        if (!leftChild.isLeaf()) {
            for (int i = 0; i <= rightChild.count; i++) {
                leftChild.childs.set(leftChild.count + i, rightChild.childs.get(i));
            }
        }

        leftChild.count += rightChild.count;

        // Remover el separador del padre y ajustar hijos
        for (int i = childIndex; i < padre.count - 1; i++) {
            padre.keys.set(i, padre.keys.get(i + 1));
            padre.childs.set(i + 1, padre.childs.get(i + 2));
        }
        padre.keys.set(padre.count - 1, null);
        padre.childs.set(padre.count, null);
        padre.count--;
    }

    // Obtener la clave máxima de un subárbol (predecesor)
    private E getMaxKey(NodoB<E> nodo) {
        while (!nodo.isLeaf()) {
            nodo = nodo.childs.get(nodo.count);
        }
        return nodo.keys.get(nodo.count - 1);
    }

    // Obtener la clave mínima de un subárbol (sucesor)
    private E getMinKey(NodoB<E> nodo) {
        while (!nodo.isLeaf()) {
            nodo = nodo.childs.get(0);
        }
        return nodo.keys.get(0);
    }
}