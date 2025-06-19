package btree;

public class BTree <E extends Comparable<E>>{
    private NodoB<E> root; //Nodo raíz del árbol
    private int orden; //Orden del árbol B
    private boolean up; //Indicador si hay división de nodo
    private NodoB<E> nDes; //Nodo resultante de una división
    private E clavePromovida;  //Clave que se promueve en división

    //Constructor
    public BTree(int orden) {
        this.orden = orden;
        this.root = null;
        this.up = false;
        this.nDes = null;
        this.clavePromovida = null;
    }

    //Verifica si el árbol está vacío
    public boolean isEmpty() {
        return this.root == null;
    }

    /******************************************************
     BUSCAR NODOS DEL ÁRBOL B
     ******************************************************/

    public boolean search(E clave){
        return searchRec(root, clave);
    }

    private boolean searchRec(NodoB<E> nodo, E clave) {
        if (nodo == null) {
            return false;
        }

        //Buscar en el nodo actual
        if (nodo.searchNode(clave)) {
            return true; //Clave encontrada
        }

        //Si es hoja y no se encontró, no existe
        if (nodo.isLeaf()) {
            return false;
        }

        //Buscar en el hijo correspondiente
        int pos = nodo.getPos();
        return searchRec(nodo.childs.get(pos), clave);
    }


    /******************************************************
    INSERTAR NODOS AL ÁRBOL B
     ******************************************************/

    //Inserta una nueva clave en el árbol
    public void insert(E clave) {
        if (root == null) {
            root = new NodoB<>(orden - 1); //maxKeys = orden - 1
            root.insertKey(clave);
        } else {
            insertRec(root, clave);
            if (up) {
                //La raíz se dividió, se crea una nueva raíz
                NodoB<E> nuevaRaiz = new NodoB<>(orden - 1);
                nuevaRaiz.insertKey(clavePromovida);
                nuevaRaiz.insertChild(0, root);
                nuevaRaiz.insertChild(1, nDes);
                root = nuevaRaiz;
                up = false;
            }
        }
    }

    private void insertRec(NodoB<E> nodo, E clave) {
        // Buscar posición en el nodo
        if (nodo.searchNode(clave)) {
            return; // Clave ya existe
        }

        int pos = nodo.getPos();

        if (nodo.isLeaf()) {
            // Insertar en hoja
            nodo.insertKey(clave);
            if (nodo.nodeFull()) {
                overflow(nodo);
            }
        } else {
            // Insertar recursivamente en hijo
            insertRec(nodo.childs.get(pos), clave);

            if (up) {
                // Hijo se dividió, insertar clave promovida
                insertarClavePromovida(nodo, pos, clavePromovida, nDes);
                up = false;

                if (nodo.nodeFull()) {
                    overflow(nodo);
                }
            }
        }
    }

    private void insertarClavePromovida(NodoB<E> nodo, int pos, E clave, NodoB<E> nuevoHijo) {
        // Mover claves hacia la derecha
        for (int i = nodo.count - 1; i >= pos; i--) {
            nodo.keys.set(i + 1, nodo.keys.get(i));
        }

        // Mover hijos hacia la derecha
        for (int i = nodo.count; i > pos; i--) {
            nodo.childs.set(i + 1, nodo.childs.get(i));
        }

        // Insertar nueva clave e hijo
        nodo.keys.set(pos, clave);
        nodo.childs.set(pos + 1, nuevoHijo);
        nodo.count++;
    }

    //CASO OVERFLOW, EXCESO DEL MAXIMO
    public void overflow(NodoB<E> nodo) {
        split(nodo);
    }

    public boolean split(NodoB<E> nodo) {
        int medio = nodo.count / 2;
        if (nodo.count % 2 == 0) {
            medio = medio - 1;  // Ajustar para números pares
        }
        clavePromovida = nodo.keys.get(medio);

        // Crear nuevo nodo derecho
        nDes = new NodoB<>(orden - 1);

        // Mover claves a la derecha del medio al nuevo nodo
        for (int i = medio + 1; i < nodo.count; i++) {
            nDes.keys.set(nDes.count, nodo.keys.get(i));
            nodo.keys.set(i, null); // Limpiar clave movida
            nDes.count++;
        }

        // Mover hijos si no es hoja
        if (!nodo.isLeaf()) {
            for (int i = medio + 1; i <= nodo.count; i++) {
                nDes.childs.set(i - medio - 1, nodo.childs.get(i));
                nodo.childs.set(i, null); // Limpiar hijo movido
            }
        }

        // Limpiar clave promovida del nodo original
        nodo.keys.set(medio, null);

        // Actualizar conteo del nodo original
        nodo.count = medio;

        up = true;
        return true;
    }

    /******************************************************
     ELIMINAR NODOS DEL ÁRBOL B
     ******************************************************/

    public void remove(E clave) {
        if (root == null) return;

        removeRec(root, clave);

        // Si la raíz queda vacía después de la eliminación y no es hoja
        if (root.nodeEmpty() && !root.isLeaf()) {
            root = root.childs.get(0);
        }
        // Si la raíz queda completamente vacía
        else if (root.nodeEmpty() && root.isLeaf()) {
            root = null;
        }
    }

    private void removeRec(NodoB<E> nodo, E clave) {
        if (nodo.searchNode(clave)) {
            // Clave encontrada
            int pos = nodo.getPos();

            if (nodo.isLeaf()) {
                // Eliminar de hoja
                eliminarDeHoja(nodo, pos);
                // Solo verificar underflow si NO es la raíz
                if (!tieneMinimo(nodo, nodo == root) && nodo != root) {
                    underflow(nodo);
                }
            } else {
                // Eliminar de nodo interno
                eliminarDeNodoInterno(nodo, pos);
            }
        } else {
            // Clave no está en este nodo, buscar en hijo
            if (!nodo.isLeaf()) {
                int pos = nodo.getPos();
                NodoB<E> hijo = nodo.childs.get(pos);
                removeRec(hijo, clave);

                // Solo manejar underflow si el hijo NO es la raíz
                if (!tieneMinimo(hijo, hijo == root) && hijo != root) {
                    manejarUnderflow(nodo, pos);
                }
            }
        }
    }

    private void eliminarDeHoja(NodoB<E> nodo, int pos) {
        // Mover claves hacia la izquierda
        for (int i = pos; i < nodo.count - 1; i++) {
            nodo.keys.set(i, nodo.keys.get(i + 1));
        }
        nodo.keys.set(nodo.count - 1, null);
        nodo.count--;
    }

    private void eliminarDeNodoInterno(NodoB<E> nodo, int pos) {
        E clave = nodo.keys.get(pos);
        NodoB<E> hijoIzq = nodo.childs.get(pos);
        NodoB<E> hijoDer = nodo.childs.get(pos + 1);

        // Buscar sucesor en hijo derecho PRIMERO
        if (tieneMinimo(hijoDer, false) && hijoDer.count >= getMinimo()) {
            E sucesor = encontrarMinimo(hijoDer);
            nodo.keys.set(pos, sucesor);
            removeRec(hijoDer, sucesor);
        }
        // Buscar predecesor en hijo izquierdo DESPUÉS
        else if (tieneMinimo(hijoIzq, false) && hijoIzq.count >= getMinimo()) {
            E predecesor = encontrarMaximo(hijoIzq);
            nodo.keys.set(pos, predecesor);
            removeRec(hijoIzq, predecesor);
        }
        // Fusionar hijos
        else {
            fusion(nodo, pos);
            removeRec(hijoIzq, clave);
        }
    }

    private E encontrarMaximo(NodoB<E> nodo) {
        while (!nodo.isLeaf()) {
            // Encontrar el último hijo no nulo
            int ultimoHijo = 0;
            for (int i = 0; i <= nodo.count; i++) {
                if (nodo.childs.get(i) != null) {
                    ultimoHijo = i;
                }
            }
            nodo = nodo.childs.get(ultimoHijo);
        }
        return nodo.keys.get(nodo.count - 1);
    }

    private E encontrarMinimo(NodoB<E> nodo) {
        while (!nodo.isLeaf()) {
            nodo = nodo.childs.get(0);
        }
        return nodo.keys.get(0);
    }

    private boolean tieneMinimo(NodoB<E> nodo, boolean esRaiz) {
        if (esRaiz) {
            return nodo.count >= 1; // La raíz solo necesita al menos 1 clave
        }
        return nodo.count >= getMinimo();
    }

    private int getMinimo() {
        return (int) Math.ceil((double) orden / 2) - 1;
    }

    //CASO UNDERFLOW, BAJO DEL MINIMO
    public void underflow(NodoB<E> nodo) {
        // Este método se maneja a través de manejarUnderflow desde el padre
    }

    private void manejarUnderflow(NodoB<E> padre, int indiceHijo) {
        NodoB<E> hijo = padre.childs.get(indiceHijo);

        // Intentar redistribuir con hermano derecho PRIMERO
        if (indiceHijo < padre.count) {
            NodoB<E> hermanoDer = padre.childs.get(indiceHijo + 1);
            if (hermanoDer != null && hermanoDer.count > getMinimo()) {
                redistribucionDerecha(padre, indiceHijo, indiceHijo + 1);
                return;
            }
        }

        // Intentar redistribuir con hermano izquierdo DESPUÉS
        if (indiceHijo > 0) {
            NodoB<E> hermanoIzq = padre.childs.get(indiceHijo - 1);
            if (hermanoIzq.count > getMinimo()) {
                redistribucionIzquierda(padre, indiceHijo - 1, indiceHijo);
                return;
            }
        }

        // No se puede redistribuir, fusionar (también cambiar orden)
        // Fusionar con hermano derecho primero si existe
        if (indiceHijo < padre.count) {
            fusion(padre, indiceHijo);  // Fusiona actual con derecho
        } else {
            fusion(padre, indiceHijo - 1);  // Fusiona izquierdo con actual
        }
    }

    public void redistribucion(NodoB<E> padre, int indiceIzq, int indiceDer) {
        redistribucionIzquierda(padre, indiceIzq, indiceDer);
    }

    private void redistribucionIzquierda(NodoB<E> padre, int indiceIzq, int indiceDer) {
        NodoB<E> hermanoIzq = padre.childs.get(indiceIzq);
        NodoB<E> hijo = padre.childs.get(indiceDer);

        // Mover claves del hijo hacia la derecha
        for (int i = hijo.count; i > 0; i--) {
            hijo.keys.set(i, hijo.keys.get(i - 1));
        }

        // Mover hijos si no es hoja
        if (!hijo.isLeaf()) {
            for (int i = hijo.count + 1; i > 0; i--) {
                hijo.childs.set(i, hijo.childs.get(i - 1));
            }
        }

        // Mover clave del padre al hijo
        hijo.keys.set(0, padre.keys.get(indiceIzq));
        hijo.count++;

        // Mover clave del hermano al padre
        padre.keys.set(indiceIzq, hermanoIzq.keys.get(hermanoIzq.count - 1));
        hermanoIzq.keys.set(hermanoIzq.count - 1, null);
        hermanoIzq.count--;

        // Mover hijo si no es hoja
        if (!hijo.isLeaf()) {
            hijo.childs.set(0, hermanoIzq.childs.get(hermanoIzq.count + 1));
            hermanoIzq.childs.set(hermanoIzq.count + 1, null);
        }
    }

    private void redistribucionDerecha(NodoB<E> padre, int indiceIzq, int indiceDer) {
        NodoB<E> hijo = padre.childs.get(indiceIzq);
        NodoB<E> hermanoDer = padre.childs.get(indiceDer);

        // Mover clave del padre al hijo
        hijo.keys.set(hijo.count, padre.keys.get(indiceIzq));
        hijo.count++;

        // Mover clave del hermano al padre
        padre.keys.set(indiceIzq, hermanoDer.keys.get(0));

        // Mover claves del hermano hacia la izquierda
        for (int i = 0; i < hermanoDer.count - 1; i++) {
            hermanoDer.keys.set(i, hermanoDer.keys.get(i + 1));
        }
        hermanoDer.keys.set(hermanoDer.count - 1, null);
        hermanoDer.count--;

        // Mover hijo si no es hoja
        if (!hijo.isLeaf()) {
            hijo.childs.set(hijo.count, hermanoDer.childs.get(0));

            // Mover hijos del hermano hacia la izquierda
            for (int i = 0; i < hermanoDer.count + 1; i++) {
                hermanoDer.childs.set(i, hermanoDer.childs.get(i + 1));
            }
            hermanoDer.childs.set(hermanoDer.count + 1, null);
        }
    }

    public void fusion(NodoB<E> padre, int indice) {
        NodoB<E> hijoIzq = padre.childs.get(indice);
        NodoB<E> hijoDer = padre.childs.get(indice + 1);

        // Mover clave del padre al hijo izquierdo
        hijoIzq.keys.set(hijoIzq.count, padre.keys.get(indice));
        hijoIzq.count++;

        // Mover todas las claves del hijo derecho al izquierdo
        for (int i = 0; i < hijoDer.count; i++) {
            hijoIzq.keys.set(hijoIzq.count, hijoDer.keys.get(i));
            hijoIzq.count++;
        }

        // Mover todos los hijos del hijo derecho al izquierdo
        if (!hijoIzq.isLeaf()) {
            for (int i = 0; i <= hijoDer.count; i++) {
                if (hijoDer.childs.get(i) != null) {
                    hijoIzq.childs.set(hijoIzq.count - hijoDer.count + i, hijoDer.childs.get(i));
                }
            }
        }

        // Eliminar clave del padre
        for (int i = indice; i < padre.count - 1; i++) {
            padre.keys.set(i, padre.keys.get(i + 1));
        }
        padre.keys.set(padre.count - 1, null);
        padre.count--;

        // Eliminar referencia al hijo derecho
        for (int i = indice + 1; i <= padre.count; i++) {
            padre.childs.set(i, padre.childs.get(i + 1));
        }
        padre.childs.set(padre.count + 1, null);

        // Verificar underflow en padre SOLO si no es la raíz
        if (!tieneMinimo(padre, padre == root) && padre != root) {
            underflow(padre);
        }
    }

    public String toString() {
        if (root == null) {
            return "Árbol B vacío";
        }
        StringBuilder sb = new StringBuilder();
        toStringRec(root, sb, 0);
        return sb.toString();
    }

    private void toStringRec(NodoB<E> nodo, StringBuilder sb, int nivel) {
        if (nodo != null) {
            sb.append("  ".repeat(nivel)).append("Nivel ").append(nivel).append(": ");
            sb.append(nodo.toString()).append("\n");

            if (!nodo.isLeaf()) {
                for (int i = 0; i <= nodo.count; i++) {
                    if (nodo.childs.get(i) != null) {
                        toStringRec(nodo.childs.get(i), sb, nivel + 1);
                    }
                }
            }
        }
    }

    //Método auxiliar para obtener información del árbol
    public void printTreeInfo() {
        System.out.println("Orden del árbol: " + orden);
        System.out.println("Máximo de claves por nodo: " + (orden - 1));
        System.out.println("Mínimo de claves por nodo (excepto raíz): " + getMinimo());
        System.out.println("Mínimo de claves para la raíz: 1");
        System.out.println("Máximo de hijos por nodo: " + orden);
        System.out.println("Mínimo de hijos por nodo (excepto raíz): " + Math.ceil((double) orden / 2));
        System.out.println("Mínimo de hijos para la raíz: 2 (si no es hoja)");
    }

    public void printArbolFormatoTabla() {
        System.out.println("Id.Nodo\tClaves Nodo\tId.Padre\tId.Hijos");

        Map<NodoB<E>, Integer> ids = new HashMap<>();
        int[] id = {1}; // Contador de ID nodo, empieza en 1

        asignarIds(root, ids, id); // Asignar IDs a cada nodo

        recorrerYMostrar(root, null, ids); // Mostrar los datos
    }

    private void asignarIds(NodoB<E> nodo, Map<NodoB<E>, Integer> ids, int[] id) {
        if (nodo == null || ids.containsKey(nodo)) return;

        ids.put(nodo, id[0]++);
        if (!nodo.isLeaf()) {
            for (int i = 0; i <= nodo.count; i++) {
                NodoB<E> hijo = nodo.childs.get(i);
                if (hijo != null) asignarIds(hijo, ids, id);
            }
        }
    }

    private void recorrerYMostrar(NodoB<E> nodo, NodoB<E> padre, Map<NodoB<E>, Integer> ids) {
        if (nodo == null) return;

        int idNodo = ids.get(nodo);
        String claves = nodo.toString().replace("[", "").replace("]", "");
        String idPadre = (padre == null) ? "--" : String.valueOf(ids.get(padre));

        StringBuilder hijos = new StringBuilder();
        if (!nodo.isLeaf()) {
            for (int i = 0; i <= nodo.count; i++) {
                NodoB<E> hijo = nodo.childs.get(i);
                if (hijo != null) {
                    hijos.append(ids.get(hijo)).append(", ");
                }
            }
            if (hijos.length() > 0) {
                hijos.setLength(hijos.length() - 2); // Quitar última coma
            }
        } else {
            hijos.append("--");
        }

        System.out.printf("%d\t%s\t\t%s\t\t%s\n", idNodo, claves, idPadre, hijos);

        if (!nodo.isLeaf()) {
            for (int i = 0; i <= nodo.count; i++) {
                NodoB<E> hijo = nodo.childs.get(i);
                recorrerYMostrar(hijo, nodo, ids);
            }
        }
    }

}
