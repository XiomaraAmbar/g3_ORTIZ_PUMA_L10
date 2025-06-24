package btreeArrayList;

public class BTree<E extends Comparable<E>> {
    private BNode<E> root;
    private int orden;
    private boolean up;
    private BNode<E> nDes;
    private int minKeys;

    public BTree(int orden){
        this.orden = orden;
        this.root = null;
        minKeys = (int)Math.ceil(orden / 2.0) - 1;
    }

    public boolean isEmpty(){
        return this.root == null;
    }

    public void insert(E cl){
        up = false;
        E mediana;
        BNode<E> pnew;
        mediana = push(this.root,cl);
        if(up){
            pnew = new BNode<E>(this.orden);
            pnew.count = 1;
            pnew.keys.set(0,mediana);
            pnew.childs.set(0,this.root);
            pnew.childs.set(1,nDes);
            this.root = pnew;
        }
    }
    private E push(BNode<E> current,E cl){
        int pos[] = new int[1];
        E mediana;
        if(current == null){
            up = true;
            nDes = null;
            return cl;
        }
        else{
            boolean fl;
            fl = current.searchNode(cl, pos);

            if(fl){
                System.out.println("Item duplicado\n");
                up = false;
                return null;
            }
            mediana = push(current.childs.get(pos[0]),cl);
            if(up){
                if(current.nodeFull(this.orden))
                    mediana = dividedNode(current,mediana,pos[0]);
                else{
                    up = false;
                    putNode(current,mediana,nDes,pos[0]);
                }
            }
            return mediana;
        }
    }
    private void putNode(BNode<E> current,E cl,BNode<E> rd,int k){
        int i;

        for(i = current.count-1; i >= k; i--) {
            current.keys.set(i+1,current.keys.get(i));
            current.childs.set(i+2,current.childs.get(i+1));
        }
        current.keys.set(k,cl);
        current.childs.set(k+1,rd);
        current.count++;
    }
    private E dividedNode(BNode<E> current,E cl,int k){
        BNode<E> rd = nDes;
        int i, posMdna;
        posMdna = (k <= this.orden/2) ? this.orden/2 : this.orden/2+1;
        nDes = new BNode<E>(this.orden);
        for(i = posMdna; i < this.orden-1; i++) {
            nDes.keys.set(i-posMdna,current.keys.get(i));
            nDes.childs.set(i-posMdna+1,current.childs.get(i+1));
        }
        nDes.count = (this.orden - 1) - posMdna;
        current.count = posMdna;
        if(k <= this.orden/2)
            putNode(current,cl,rd,k);
        else
            putNode(nDes,cl,rd,k-posMdna);
        E median = current.keys.get(current.count-1);
        nDes.childs.set(0,current.childs.get(current.count));
        current.count--;
        return median;
    }

    public boolean search(E cl) {
        if (isEmpty()) {
            return false;
        } else {
            SearchResult result = searchKey(this.root, cl);
            if (result.found) {
                System.out.println(cl + " se encuentra en una posición del nodo " + result.nodePosition);
            }
            return result.found;
        }
    }

    private SearchResult searchKey(BNode<E> current, E cl) {
        int pos = 0;
        while (pos < current.count && current.keys.get(pos).compareTo(cl) < 0) {
            pos++;
        }
        if (pos < current.count && current.keys.get(pos).equals(cl)) {
            return new SearchResult(true, pos);
        } else if (current.childs.get(pos) != null) {
            return searchKey(current.childs.get(pos), cl);
        } else {
            return new SearchResult(false, -1);
        }
    }

    private static class SearchResult {
        boolean found;
        int nodePosition;

        public SearchResult(boolean found, int nodePosition) {
            this.found = found;
            this.nodePosition = nodePosition;
        }
    }

    public void remove(E cl) {
        if (isEmpty()) {
            System.out.println("El árbol está vacío. No se puede eliminar la clave.");
            return;
        }

        boolean success = removeKey(this.root, cl);
        if (success) {
            System.out.println("Se eliminó la clave " + cl + " del árbol.");

            //Si la raíz queda vacía después de la eliminación, ajustar la raíz
            if (this.root.count == 0 && this.root.childs.get(0) != null) {
                this.root = this.root.childs.get(0);
            }
        } else {
            System.out.println("La clave " + cl + " no se encontró en el árbol.");
        }
    }

    private boolean removeKey(BNode<E> current, E cl) {
        int pos[] = new int[1];
        boolean found = current.searchNode(cl, pos);

        // Si se encuentra la clave en el nodo actual
        if (found) {
            // Si es un nodo hoja
            if (current.childs.get(pos[0]) == null) {
                // Eliminar directamente
                for (int i = pos[0]; i < current.count - 1; i++) {
                    current.keys.set(i, current.keys.get(i + 1));
                }
                current.count--;
                return true;
            } else {
                // Si no es hoja, reemplazar con el sucesor
                BNode<E> succ = current.childs.get(pos[0] + 1);
                while (succ.childs.get(0) != null) {
                    succ = succ.childs.get(0);
                }
                E successorKey = succ.keys.get(0);
                current.keys.set(pos[0], successorKey);

                // Eliminar el sucesor del subárbol derecho
                boolean success = removeKey(current.childs.get(pos[0] + 1), successorKey);

                // Verificar underflow en el hijo derecho
                if (success && current.childs.get(pos[0] + 1).count <  minKeys) {
                    fixUnderflow(current, pos[0] + 1);
                }
                return success;
            }
        }
        // Si la clave no se encuentra en el nodo actual, buscar en el hijo correspondiente
        else if (current.childs.get(pos[0]) != null) {
            boolean success = removeKey(current.childs.get(pos[0]), cl);

            // Verificar underflow en el hijo
            if (success && current.childs.get(pos[0]).count < minKeys) {
                fixUnderflow(current, pos[0]);
            }
            return success;
        }
        // Si no se encuentra la clave
        else {
            return false;
        }
    }

    private void fixUnderflow(BNode<E> parent, int childIndex) {
        BNode<E> child = parent.childs.get(childIndex);
        BNode<E> leftSibling = (childIndex > 0) ? parent.childs.get(childIndex - 1) : null;
        BNode<E> rightSibling = (childIndex < parent.count) ? parent.childs.get(childIndex + 1) : null;

        // 1. Intentar obtener una clave prestada del hermano DERECHO primero
        if (rightSibling != null && rightSibling.count > minKeys) {
            // Bajar clave del padre al hijo con underflow
            child.keys.set(child.count, parent.keys.get(childIndex));
            child.childs.set(child.count + 1, rightSibling.childs.get(0));
            child.count++;

            // Subir la primera clave del hermano derecho al padre
            parent.keys.set(childIndex, rightSibling.keys.get(0));

            // Eliminar la primera clave y hijo del hermano derecho
            for (int i = 0; i < rightSibling.count - 1; i++) {
                rightSibling.keys.set(i, rightSibling.keys.get(i + 1));
            }
            for (int i = 0; i < rightSibling.count; i++) {
                rightSibling.childs.set(i, rightSibling.childs.get(i + 1));
            }
            rightSibling.count--;
        }
        // 2. Intentar obtener una clave prestada del hermano IZQUIERDO
        else if (leftSibling != null && leftSibling.count > minKeys) {
            // Hacer espacio en el hijo con underflow
            for (int i = child.count; i > 0; i--) {
                child.keys.set(i, child.keys.get(i - 1));
            }
            for (int i = child.count + 1; i > 0; i--) {
                child.childs.set(i, child.childs.get(i - 1));
            }

            // Bajar clave del padre al hijo con underflow
            child.keys.set(0, parent.keys.get(childIndex - 1));
            child.childs.set(0, leftSibling.childs.get(leftSibling.count));
            child.count++;

            // Subir la última clave del hermano izquierdo al padre
            parent.keys.set(childIndex - 1, leftSibling.keys.get(leftSibling.count - 1));
            leftSibling.count--;
        }
        // 3. Si no es posible préstamo, realizar fusión
        else {
            // Fusión con hermano DERECHO (preferencia)
            if (rightSibling != null) {
                // Bajar la clave del padre
                child.keys.set(child.count, parent.keys.get(childIndex));
                child.count++;

                // Agregar todas las claves del hermano derecho
                for (int i = 0; i < rightSibling.count; i++) {
                    child.keys.set(child.count, rightSibling.keys.get(i));
                    child.childs.set(child.count, rightSibling.childs.get(i));
                    child.count++;
                }
                child.childs.set(child.count, rightSibling.childs.get(rightSibling.count));

                // Eliminar la clave del padre y el puntero al hermano derecho
                for (int i = childIndex; i < parent.count - 1; i++) {
                    parent.keys.set(i, parent.keys.get(i + 1));
                }
                for (int i = childIndex + 1; i < parent.count; i++) {
                    parent.childs.set(i, parent.childs.get(i + 1));
                }
                parent.count--;
            }
            // Fusión con hermano IZQUIERDO
            else if (leftSibling != null) {
                // Bajar la clave del padre al hermano izquierdo
                leftSibling.keys.set(leftSibling.count, parent.keys.get(childIndex - 1));
                leftSibling.count++;

                // Agregar todas las claves del hijo con underflow al hermano izquierdo
                for (int i = 0; i < child.count; i++) {
                    leftSibling.keys.set(leftSibling.count, child.keys.get(i));
                    leftSibling.childs.set(leftSibling.count, child.childs.get(i));
                    leftSibling.count++;
                }
                leftSibling.childs.set(leftSibling.count, child.childs.get(child.count));

                // Eliminar la clave del padre y el puntero al hijo con underflow
                for (int i = childIndex - 1; i < parent.count - 1; i++) {
                    parent.keys.set(i, parent.keys.get(i + 1));
                }
                for (int i = childIndex; i < parent.count; i++) {
                    parent.childs.set(i, parent.childs.get(i + 1));
                }
                parent.count--;
            }
        }
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        if (isEmpty()) {
            s.append("BTree is empty...");
        } else {
            writeTree(this.root, null, s);
        }
        return s.toString();
    }

    private StringBuilder writeTree(BNode<E> current, BNode<E> parent, StringBuilder sb) {
        if (current != null) {
            // Si es el primer nodo (raíz), agregar encabezados
            if (parent == null && sb.length() == 0) {
                sb.append(String.format("%-10s %-15s %-10s %-15s%n",
                        "Id.Nodo", "Claves Nodo", "Id.Padre", "Id.Hijos"));
            }

            // Construir string de claves
            StringBuilder claves = new StringBuilder("(");
            for (int i = 0; i < current.count; i++) {
                claves.append(current.getKey(i));
                if (i < current.count - 1) {
                    claves.append(", ");
                }
            }
            claves.append(")");

            // Construir string del padre
            String padreStr = (parent != null) ? "[" + parent.getIdNode() + "]" : "--";

            // Construir string de hijos
            StringBuilder hijos = new StringBuilder("[");
            boolean hasChildren = false;
            for (int i = 0; i <= current.count; i++) {
                if (current.getChild(i) != null) {
                    if (hasChildren) {
                        hijos.append(", ");
                    }
                    hijos.append(current.getChild(i).getIdNode());
                    hasChildren = true;
                }
            }
            if (!hasChildren) {
                hijos.append("--");
            }
            hijos.append("]");

            // Agregar fila a la tabla
            sb.append(String.format("%-10s %-15s %-10s %-15s%n",
                    current.getIdNode(),
                    claves.toString(),
                    padreStr,
                    hijos.toString()));

            // Recursivamente procesar los hijos
            for (int i = 0; i <= current.count; i++) {
                if (current.getChild(i) != null) {
                    writeTree(current.getChild(i), current, sb);
                }
            }
        }

        return sb;
    }
}