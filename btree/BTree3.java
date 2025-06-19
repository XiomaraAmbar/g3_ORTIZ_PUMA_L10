package btree;

public class BTree3 {
    private NodoB root;        // Nodo raíz del árbol
    private int orden;         // Orden del árbol B (número máximo de claves + 1)
    private boolean up;        // Bandera que indica si hay división de nodo
    private NodoB nDes;        // Nodo resultante de una división

    // Constructor: inicializa el árbol con el orden especificado
    public BTree3(int orden) {
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
    public void insert(String cl) {
        up = false;                    // Inicializar bandera de división
        String mediana;                // Clave mediana en caso de división
        NodoB pnew;                    // Nuevo nodo raíz si es necesario

        // Intentar insertar la clave
        mediana = push(this.root, cl);

        // Si hubo división en la raíz, crear nueva raíz
        if (up) {
            pnew = new NodoB(this.orden);
            pnew.count = 1;
            pnew.keys.set(0, mediana);      // Mediana como única clave
            pnew.childs.set(0, this.root);  // Raíz anterior como hijo izquierdo
            pnew.childs.set(1, nDes);       // Nuevo nodo como hijo derecho
            this.root = pnew;               // Actualizar raíz
        }
    }

    // Método recursivo para insertar una clave
    private String push(NodoB current, String cl) {
        int[] pos = new int[1];        // Array para obtener posición por referencia
        String mediana;

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
                // Verificar overflow y manejar división
                if (hasOverflow(current)) {
                    mediana = handleOverflow(current, mediana, pos[0]);
                } else {
                    // Insertar la mediana en el nodo actual
                    up = false;
                    putNode(current, mediana, nDes, pos[0]);
                }
            }
            return mediana;
        }
    }

    // ==================== MÉTODOS DE OVERFLOW ====================

    // Verificar si un nodo tiene overflow
    private boolean hasOverflow(NodoB nodo) {
        return nodo.nodeFull(this.orden - 1);
    }

    // Manejar overflow mediante split
    private String handleOverflow(NodoB nodo, String mediana, int pos) {
        return split(nodo, mediana, pos);
    }

    // Divide un nodo lleno e inserta una nueva clave (split)
    private String split(NodoB current, String cl, int k) {
        NodoB rd = nDes;            // Guardar nodo derecho temporal
        int i;
        int totalKeys = this.orden - 1; // Máximo de claves que puede tener un nodo

        // Crear un array temporal para todas las claves (incluyendo la nueva)
        String[] tempKeys = new String[totalKeys + 1];
        NodoB[] tempChilds = new NodoB[totalKeys + 2];

        // Insertar todas las claves existentes y la nueva en el array temporal
        int j = 0;
        for (i = 0; i < current.count; i++) {
            if (j == k) {
                tempKeys[j] = cl;
                tempChilds[j + 1] = rd;
                j++;
            }
            tempKeys[j] = current.keys.get(i);
            tempChilds[j] = current.childs.get(i);
            j++;
        }
        if (j == k) { // Si la nueva clave va al final
            tempKeys[j] = cl;
            tempChilds[j + 1] = rd;
        }
        tempChilds[totalKeys + 1] = current.childs.get(current.count);

        // Calcular posición de la mediana
        int medianPos;
        if ((totalKeys + 1) % 2 == 0) {
            // Número par de claves totales: mediana = total/2 - 1 (índice)
            medianPos = (totalKeys + 1) / 2 - 1;
        } else {
            // Número impar de claves totales: mediana = total/2 (índice)
            medianPos = (totalKeys + 1) / 2;
        }

        // La clave mediana que subirá
        String median = tempKeys[medianPos];

        // Crear nuevo nodo para la mitad derecha
        nDes = new NodoB(this.orden);

        // Llenar nodo izquierdo (current) con claves antes de la mediana
        current.count = medianPos;
        for (i = 0; i < medianPos; i++) {
            current.keys.set(i, tempKeys[i]);
            current.childs.set(i, tempChilds[i]);
        }
        current.childs.set(medianPos, tempChilds[medianPos]);
        // Limpiar claves restantes
        for (i = medianPos; i < totalKeys; i++) {
            current.keys.set(i, null);
            if (i + 1 < this.orden) {
                current.childs.set(i + 1, null);
            }
        }

        // Llenar nodo derecho con claves después de la mediana
        nDes.count = totalKeys - medianPos;
        for (i = medianPos + 1; i <= totalKeys; i++) {
            nDes.keys.set(i - medianPos - 1, tempKeys[i]);
            nDes.childs.set(i - medianPos - 1, tempChilds[i]);
        }
        nDes.childs.set(nDes.count, tempChilds[totalKeys + 1]);

        return median;                              // Retornar mediana para subir
    }

    // Inserta una clave y su hijo derecho en un nodo en la posición k
    private void putNode(NodoB current, String cl, NodoB rd, int k) {
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

    // ==================== MÉTODOS DE ELIMINACIÓN ====================

    // Eliminar una clave del árbol
    public boolean delete(String key) {
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
    private boolean deleteRec(NodoB nodo, String key) {
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
            NodoB child = nodo.childs.get(pos[0]);
            if (child == null) {
                return false;
            }

            boolean deleted = deleteRec(child, key);

            // Verificar underflow después de la eliminación
            if (deleted && hasUnderflow(child)) {
                handleUnderflow(nodo, pos[0]);
            }

            return deleted;
        }
    }

    // ==================== MÉTODOS DE UNDERFLOW ====================

    // Verificar si un nodo tiene underflow
    private boolean hasUnderflow(NodoB nodo) {
        return nodo != null && nodo.count < getMinKeys();
    }

    // Manejar underflow mediante redistribution o fusion
    private void handleUnderflow(NodoB padre, int childIndex) {
        NodoB child = padre.childs.get(childIndex);

        // PRIMERO: Intentar redistribution desde hermano DERECHO (mayor)
        if (childIndex < padre.count &&
                padre.childs.get(childIndex + 1).count > getMinKeys()) {
            System.out.println("Redistribuyendo desde hermano derecho");
            redistributeFromRight(padre, childIndex);
        }
        // SEGUNDO: Intentar redistribution desde hermano IZQUIERDO (menor)
        else if (childIndex > 0 &&
                padre.childs.get(childIndex - 1).count > getMinKeys()) {
            System.out.println("Redistribuyendo desde hermano izquierdo");
            redistributeFromLeft(padre, childIndex);
        }
        // TERCERO: Fusion con hermano derecho (si existe)
        else if (childIndex < padre.count) {
            System.out.println("Fusionando con hermano derecho");
            fusion(padre, childIndex);
        }
        // CUARTO: Fusion con hermano izquierdo (si no hay derecho)
        else if (childIndex > 0) {
            System.out.println("Fusionando con hermano izquierdo");
            fusion(padre, childIndex - 1);
        }
    }

    // ==================== MÉTODOS DE REDISTRIBUTION ====================

    // Redistribution desde hermano derecho
    private void redistributeFromRight(NodoB padre, int childIndex) {
        NodoB child = padre.childs.get(childIndex);           // Nodo que necesita clave
        NodoB rightSibling = padre.childs.get(childIndex + 1); // Hermano derecho que presta

        // PASO 1: El separador del padre BAJA al hijo que necesita
        child.keys.set(child.count, padre.keys.get(childIndex));
        child.count++;

        // PASO 2: Si no es hoja, mover también el primer hijo del hermano derecho
        if (!child.isLeaf()) {
            child.childs.set(child.count, rightSibling.childs.get(0));
            // Desplazar hijos del hermano derecho hacia la izquierda
            for (int i = 0; i < rightSibling.count; i++) {
                rightSibling.childs.set(i, rightSibling.childs.get(i + 1));
            }
            rightSibling.childs.set(rightSibling.count, null);
        }

        // PASO 3: La primera clave del hermano derecho SUBE como nuevo separador
        padre.keys.set(childIndex, rightSibling.keys.get(0));

        // PASO 4: Desplazar claves del hermano derecho hacia la izquierda
        for (int i = 0; i < rightSibling.count - 1; i++) {
            rightSibling.keys.set(i, rightSibling.keys.get(i + 1));
        }
        rightSibling.keys.set(rightSibling.count - 1, null);
        rightSibling.count--;
    }

    // Redistribution desde hermano izquierdo
    private void redistributeFromLeft(NodoB padre, int childIndex) {
        NodoB child = padre.childs.get(childIndex);          // Nodo que necesita clave
        NodoB leftSibling = padre.childs.get(childIndex - 1); // Hermano izquierdo que presta

        // PASO 1: Desplazar claves del hijo hacia la derecha para hacer espacio
        for (int i = child.count; i > 0; i--) {
            child.keys.set(i, child.keys.get(i - 1));
        }

        // PASO 2: Si no es hoja, desplazar también los hijos hacia la derecha
        if (!child.isLeaf()) {
            for (int i = child.count + 1; i > 0; i--) {
                child.childs.set(i, child.childs.get(i - 1));
            }
            // Mover el último hijo del hermano izquierdo
            child.childs.set(0, leftSibling.childs.get(leftSibling.count));
            leftSibling.childs.set(leftSibling.count, null);
        }

        // PASO 3: El separador del padre BAJA al hijo que necesita
        child.keys.set(0, padre.keys.get(childIndex - 1));
        child.count++;

        // PASO 4: La última clave del hermano izquierdo SUBE como nuevo separador
        padre.keys.set(childIndex - 1, leftSibling.keys.get(leftSibling.count - 1));
        leftSibling.keys.set(leftSibling.count - 1, null);
        leftSibling.count--;
    }

    // ==================== MÉTODOS DE FUSION ====================

    // Fusion de nodo con su hermano derecho
    private void fusion(NodoB padre, int childIndex) {
        NodoB leftChild = padre.childs.get(childIndex);      // Nodo que necesita (izquierdo)
        NodoB rightChild = padre.childs.get(childIndex + 1); // Hermano derecho

        System.out.println("Fusionando nodos: " + leftChild.getId() + " + separador + " + rightChild.getId());

        // PASO 1: El separador del padre BAJA al nodo izquierdo
        leftChild.keys.set(leftChild.count, padre.keys.get(childIndex));
        leftChild.count++;

        // PASO 2: Copiar todas las claves del nodo derecho al izquierdo
        for (int i = 0; i < rightChild.count; i++) {
            leftChild.keys.set(leftChild.count + i, rightChild.keys.get(i));
        }

        // PASO 3: Copiar todos los hijos del nodo derecho al izquierdo (si no es hoja)
        if (!leftChild.isLeaf()) {
            for (int i = 0; i <= rightChild.count; i++) {
                leftChild.childs.set(leftChild.count + i, rightChild.childs.get(i));
            }
        }

        // PASO 4: Actualizar contador del nodo izquierdo
        leftChild.count += rightChild.count;

        // PASO 5: Remover el separador del padre y ajustar hijos
        for (int i = childIndex; i < padre.count - 1; i++) {
            padre.keys.set(i, padre.keys.get(i + 1));
            padre.childs.set(i + 1, padre.childs.get(i + 2));
        }
        padre.keys.set(padre.count - 1, null);
        padre.childs.set(padre.count, null);
        padre.count--;

        System.out.println("Fusión completada. Nodo resultante: " + leftChild.getId() + " con " + leftChild.count + " claves");
    }

    // ==================== MÉTODOS AUXILIARES ====================

    // Remover clave de nodo hoja
    private void removeFromLeaf(NodoB nodo, int pos) {
        // Desplazar claves hacia la izquierda
        for (int i = pos; i < nodo.count - 1; i++) {
            nodo.keys.set(i, nodo.keys.get(i + 1));
        }
        nodo.keys.set(nodo.count - 1, null);
        nodo.count--;
    }

    // Remover clave de nodo interno
    private boolean removeFromInternal(NodoB nodo, int pos, String key) {
        String keyToDelete = nodo.keys.get(pos);
        NodoB leftChild = nodo.childs.get(pos);
        NodoB rightChild = nodo.childs.get(pos + 1);

        // Caso 2a: Hijo izquierdo tiene suficientes claves
        if (leftChild.count >= getMinKeys()) {
            String predecessor = getMaxKey(leftChild);
            nodo.keys.set(pos, predecessor);
            return deleteRec(leftChild, predecessor);
        }
        // Caso 2b: Hijo derecho tiene suficientes claves
        else if (rightChild.count >= getMinKeys()) {
            String successor = getMinKey(rightChild);
            nodo.keys.set(pos, successor);
            return deleteRec(rightChild, successor);
        }
        // Caso 2c: Ambos hijos tienen mínimas claves - fusionar
        else {
            fusion(nodo, pos);
            return deleteRec(leftChild, key);
        }
    }

    // Obtener número mínimo de claves para un nodo
    private int getMinKeys() {
        return (this.orden - 1) / 2;  // Mínimo de claves por nodo
    }

    // Obtener la clave máxima de un subárbol (predecesor)
    private String getMaxKey(NodoB nodo) {
        while (!nodo.isLeaf()) {
            nodo = nodo.childs.get(nodo.count);
        }
        return nodo.keys.get(nodo.count - 1);
    }

    // Obtener la clave mínima de un subárbol (sucesor)
    private String getMinKey(NodoB nodo) {
        while (!nodo.isLeaf()) {
            nodo = nodo.childs.get(0);
        }
        return nodo.keys.get(0);
    }

    // ==================== MÉTODOS DE VISUALIZACIÓN ====================

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
    private void inOrderRec(NodoB nodo) {
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
    private void showTreeRec(NodoB nodo, int nivel) {
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
    public boolean search(String key) {
        return searchRec(this.root, key);
    }

    // Búsqueda recursiva
    private boolean searchRec(NodoB nodo, String key) {
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