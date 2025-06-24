package btreeFinal;

import java.util.ArrayList;

//Clase que implementa un árbol B completo con operaciones de inserción, búsqueda y eliminación
public class BTree<E extends Comparable<E>> {
    private BNode<E> raiz; //Nodo raíz del árbol B
    private int orden; //Orden del árbol B
    private boolean dividido; //Booleano que indica si se realizó una división durante la inserción
    private BNode<E> derechoTemporal; //Nodo temporal usado durante las divisiones
    private int minimoClaves; //Número mínimo de claves que debe tener un nodo

    //Constructor que inicializa el árbol B con el orden del arbol
    public BTree(int orden){
        this.orden = orden;
        this.raiz = null;
        minimoClaves = (int)Math.ceil(orden / 2.0) - 1;
    }

    //Verifica si el árbol está vacío
    public boolean isEmpty(){
        return this.raiz == null;
    }

    //Método público para insertar una nueva clave en el árbol
    public void insert(E nuevaClave){
        dividido = false;
        E claveMedia;
        BNode<E> nuevaRaiz; //Nuevo nodo padre o nodo creado con la clave del medio
        claveMedia = push(this.raiz,nuevaClave);
        if(dividido){
            nuevaRaiz = new BNode<E>(this.orden);
            nuevaRaiz.contadorClaves = 1;
            nuevaRaiz.insertKey(claveMedia);
            nuevaRaiz.setChild(0, this.raiz);
            nuevaRaiz.setChild(1, derechoTemporal);
            this.raiz = nuevaRaiz;
        }
    }

    //Método recursivo que inserta una clave y maneja las divisiones de nodos
    private E push(BNode<E> nodoActual, E nuevaClave){
        int posicion[] = new int[1];
        E claveMedia;
        if(nodoActual == null){
            dividido = true;
            derechoTemporal = null;
            return nuevaClave;
        }
        else{
            boolean claveEncontrada;
            claveEncontrada = nodoActual.searchNode(nuevaClave, posicion);

            if(claveEncontrada){
                System.out.println("Item duplicado\n");
                dividido = false;
                return null;
            }
            claveMedia = push(nodoActual.getChild(posicion[0]),nuevaClave);
            if(dividido){
                if(nodoActual.nodeFull(this.orden))
                    claveMedia = dividedNode(nodoActual,claveMedia,posicion[0]);
                else{
                    dividido = false;
                    putNode(nodoActual,claveMedia,derechoTemporal,posicion[0]);
                }
            }
            return claveMedia;
        }
    }

    //Inserta una clave y su hijo derecho en un nodo que no está lleno
    private void putNode(BNode<E> nodoActual, E nuevaClave, BNode<E> nuevoDerecho, int posicionInsertar){
        try {
            //Crea un nodo temporal para reorganizar elementos
            BNode<E> nodoTemporal = new BNode<E>(this.orden);

            //Copia claves hasta la posición de inserción
            for (int i = 0; i < posicionInsertar; i++) {
                nodoTemporal.insertKey(nodoActual.getKey(i));
            }

            //Inserta la nueva clave
            nodoTemporal.insertKey(nuevaClave);

            //Copia el resto de claves
            for (int i = posicionInsertar; i < nodoActual.contadorClaves; i++) {
                nodoTemporal.insertKey(nodoActual.getKey(i));
            }

            //Reorganiza los hijos
            for (int i = 0; i <= posicionInsertar; i++) {
                nodoTemporal.setChild(i, nodoActual.getChild(i));
            }
            nodoTemporal.setChild(posicionInsertar + 1, nuevoDerecho);
            for (int i = posicionInsertar + 1; i <= nodoActual.contadorClaves; i++) {
                nodoTemporal.setChild(i + 1, nodoActual.getChild(i));
            }

            //Copia de vuelta al nodo original
            nodoActual.claves = nodoTemporal.claves;
            nodoActual.hijos = nodoTemporal.hijos;
            nodoActual.contadorClaves = nodoTemporal.contadorClaves;

        } catch (Exception e) {
            System.err.println("Error en putNode: " + e.getMessage());
        }
    }

    //Divide un nodo lleno en dos nodos y devuelve la clave mediana
    private E dividedNode(BNode<E> nodoActual, E nuevaClave, int posicionInsertar){
        BNode<E> nuevoDerecho = derechoTemporal;
        int posicionMediana;
        posicionMediana = (posicionInsertar <= this.orden/2) ? this.orden/2 : this.orden/2+1;
        derechoTemporal = new BNode<E>(this.orden);

        try {
            //Crea nodo temporal con todas las claves (incluyendo la nueva)
            BNode<E> nodoTemporal = new BNode<E>(this.orden);

            //Copia claves hasta la posición de inserción
            for (int i = 0; i < posicionInsertar; i++) {
                nodoTemporal.insertKey(nodoActual.getKey(i));
            }
            //Inserta la nueva clave
            nodoTemporal.insertKey(nuevaClave);
            //Copia el resto de claves
            for (int i = posicionInsertar; i < nodoActual.contadorClaves; i++) {
                nodoTemporal.insertKey(nodoActual.getKey(i));
            }

            //Copia hijos al nodo temporal
            for (int i = 0; i <= posicionInsertar; i++) {
                nodoTemporal.setChild(i, nodoActual.getChild(i));
            }
            nodoTemporal.setChild(posicionInsertar + 1, nuevoDerecho);
            for (int i = posicionInsertar + 1; i <= nodoActual.contadorClaves; i++) {
                nodoTemporal.setChild(i + 1, nodoActual.getChild(i));
            }
            //Obtiene la mediana
            E median = nodoTemporal.getKey(posicionMediana);

            //Limpia el nodo actual
            nodoActual.claves = new ArrayList<>();
            nodoActual.hijos = new ArrayList<>();
            nodoActual.contadorClaves = 0;

            //Llena la mitad izquierda (nodo actual)
            for (int i = 0; i < posicionMediana; i++) {
                nodoActual.insertKey(nodoTemporal.getKey(i));
                nodoActual.setChild(i, nodoTemporal.getChild(i));
            }
            nodoActual.setChild(posicionMediana, nodoTemporal.getChild(posicionMediana));

            //Llena la mitad derecha (nuevo nodo)
            for (int i = posicionMediana + 1; i < nodoTemporal.contadorClaves; i++) {
                derechoTemporal.insertKey(nodoTemporal.getKey(i));
                derechoTemporal.setChild(i - posicionMediana - 1, nodoTemporal.getChild(i));
            }
            derechoTemporal.setChild(nodoTemporal.contadorClaves - posicionMediana - 1, nodoTemporal.getChild(nodoTemporal.contadorClaves));

            return median;

        } catch (Exception e) {
            System.err.println("Error en dividedNode: " + e.getMessage());
            return null;
        }
    }

    //Método público para buscar una clave en el árbol
    public boolean search(E clave) {
        if (isEmpty()) {
            return false;
        } else {
            ResultadoBusqueda resultado = searchKey(this.raiz, clave);
            if (resultado.encontrado) {
                System.out.println(clave + " se encuentra en una posición del nodo " + resultado.posicionNodo);
            }
            return resultado.encontrado;
        }
    }

    //Método recursivo que busca una clave en el árbol
    private ResultadoBusqueda searchKey(BNode<E> nodoActual, E clave) {
        int pos = 0;
        while (pos < nodoActual.contadorClaves && nodoActual.getKey(pos).compareTo(clave) < 0) {
            pos++;
        }
        if (pos < nodoActual.contadorClaves && nodoActual.getKey(pos).equals(clave)) {
            return new ResultadoBusqueda(true, pos);
        } else if (nodoActual.getChild(pos) != null) {
            return searchKey(nodoActual.getChild(pos), clave);
        } else {
            return new ResultadoBusqueda(false, -1);
        }
    }

    //Clase interna para almacenar el resultado de una búsqueda
    private static class ResultadoBusqueda {
        boolean encontrado; //Indica si la clave fue encontrada
        int posicionNodo; //Posición de la clave en el nodo

        public ResultadoBusqueda(boolean encontrado, int posicionNodo) {
            this.encontrado = encontrado;
            this.posicionNodo = posicionNodo;
        }
    }

    //Método público para eliminar una clave del árbol
    public void remove(E clave) {
        if (isEmpty()) {
            System.out.println("El árbol está vacío. No se puede eliminar la clave.");
            return;
        }

        boolean eliminado = removeKey(this.raiz, clave);
        if (eliminado) {
            System.out.println("Se eliminó la clave " + clave + " del árbol.");

            //Si la raíz queda vacía después de la eliminación, ajustar la raíz
            if (this.raiz.contadorClaves == 0 && this.raiz.getChild(0) != null) {
                this.raiz = this.raiz.getChild(0);
            }
        } else {
            System.out.println("La clave " + clave + " no se encontró en el árbol.");
        }
    }

    //Método recursivo que elimina una clave del árbol
    private boolean removeKey(BNode<E> nodoActual, E clave) {
        int posicion[] = new int[1];
        boolean encontrado = nodoActual.searchNode(clave, posicion);

        //Si se encuentra la clave en el nodo actual
        if (encontrado) {
            //Si es un nodo hoja
            if (nodoActual.getChild(posicion[0]) == null) {
                //Eliminar directamente usando el método de BNode
                nodoActual.removeKey(clave);
                return true;
            } else {
                //Si no es hoja, reemplazar con el sucesor
                BNode<E> nodoSucesor = nodoActual.getChild(posicion[0] + 1);
                while (nodoSucesor.getChild(0) != null) {
                    nodoSucesor = nodoSucesor.getChild(0);
                }
                E claveSucesor = nodoSucesor.getKey(0);

                //Reemplazar la clave en el nodo actual
                nodoActual.removeKey(clave);
                nodoActual.insertKey(claveSucesor);

                //Eliminar el sucesor del subárbol derecho
                boolean success = removeKey(nodoActual.getChild(posicion[0] + 1), claveSucesor);

                //Verificar underflow en el hijo derecho
                if (success && nodoActual.getChild(posicion[0] + 1).contadorClaves < minimoClaves) {
                    fixUnderflow(nodoActual, posicion[0] + 1);
                }
                return encontrado;
            }
        }
        //Si la clave no se encuentra en el nodo actual, buscar en el hijo correspondiente
        else if (nodoActual.getChild(posicion[0]) != null) {
            encontrado = removeKey(nodoActual.getChild(posicion[0]), clave);

            //Verificar underflow en el hijo
            if (encontrado && nodoActual.getChild(posicion[0]).contadorClaves < minimoClaves) {
                fixUnderflow(nodoActual, posicion[0]);
            }
            return encontrado;
        }
        //Si no se encuentra la clave
        else {
            return false;
        }
    }

    //Corrige el underflow (déficit de claves) en un nodo
    private void fixUnderflow(BNode<E> nodoPadre, int indiceHijo) {
        BNode<E> hijoDesbalanceado = nodoPadre.getChild(indiceHijo);
        BNode<E> hermanoIzquierdo = (indiceHijo > 0) ? nodoPadre.getChild(indiceHijo - 1) : null;
        BNode<E> hermanoDerecho = (indiceHijo < nodoPadre.contadorClaves) ? nodoPadre.getChild(indiceHijo + 1) : null;

        //1. Intentar obtener una clave prestada del hermano DERECHO primero
        if (hermanoDerecho != null && hermanoDerecho.contadorClaves > minimoClaves) {
            //Bajar clave del padre al hijo con underflow
            E clavePadre = nodoPadre.getKey(indiceHijo);
            hijoDesbalanceado.insertKey(clavePadre);
            hijoDesbalanceado.setChild(hijoDesbalanceado.contadorClaves, hermanoDerecho.getChild(0));

            //Subir la primera clave del hermano derecho al padre
            E primeraClave = hermanoDerecho.getKey(0);
            nodoPadre.removeKey(clavePadre);
            nodoPadre.insertKey(primeraClave);

            //Eliminar la primera clave del hermano derecho
            hermanoDerecho.removeKey(primeraClave);

            //Reorganizar hijos del hermano derecho
            for (int i = 0; i < hermanoDerecho.contadorClaves; i++) {
                hermanoDerecho.setChild(i, hermanoDerecho.getChild(i + 1));
            }
        }
        //2. Intentar obtener una clave prestada del hermano IZQUIERDO
        else if (hermanoIzquierdo != null && hermanoIzquierdo.contadorClaves > minimoClaves) {
            //Bajar clave del padre al hijo con underflow
            E clavePadre = nodoPadre.getKey(indiceHijo - 1);

            //Crear nodo temporal para reorganizar
            BNode<E> nodoTemporal = new BNode<E>(this.orden);
            nodoTemporal.insertKey(clavePadre);
            for (int i = 0; i < hijoDesbalanceado.contadorClaves; i++) {
                nodoTemporal.insertKey(hijoDesbalanceado.getKey(i));
            }

            //Reorganizar hijos
            nodoTemporal.setChild(0, hermanoIzquierdo.getChild(hermanoIzquierdo.contadorClaves));
            for (int i = 0; i <= hijoDesbalanceado.contadorClaves; i++) {
                nodoTemporal.setChild(i + 1, hijoDesbalanceado.getChild(i));
            }

            //Subir la última clave del hermano izquierdo al padre
            E ultimaClave = hermanoIzquierdo.getKey(hermanoIzquierdo.contadorClaves - 1);
            nodoPadre.removeKey(clavePadre);
            nodoPadre.insertKey(ultimaClave);

            //Actualizar el hijo
            hijoDesbalanceado.claves = nodoTemporal.claves;
            hijoDesbalanceado.hijos = nodoTemporal.hijos;
            hijoDesbalanceado.contadorClaves = nodoTemporal.contadorClaves;

            //Eliminar la última clave del hermano izquierdo
            hermanoIzquierdo.removeKey(ultimaClave);
        }
        //3. Si no es posible préstamo, realizar fusión
        else {
            //Fusión con hermano DERECHO (preferencia)
            if (hermanoDerecho != null) {
                mergeNodes(hijoDesbalanceado, nodoPadre.getKey(indiceHijo), hermanoDerecho);
                nodoPadre.removeKey(nodoPadre.getKey(indiceHijo));

                //Reorganizar hijos del padre
                for (int i = indiceHijo + 1; i < nodoPadre.contadorClaves; i++) {
                    nodoPadre.setChild(i, nodoPadre.getChild(i + 1));
                }
            }
            //Fusión con hermano IZQUIERDO
            else if (hermanoIzquierdo != null) {
                mergeNodes(hermanoIzquierdo, nodoPadre.getKey(indiceHijo - 1), hijoDesbalanceado);
                nodoPadre.removeKey(nodoPadre.getKey(indiceHijo - 1));

                //Reorganizar hijos del padre
                for (int i = indiceHijo; i < nodoPadre.contadorClaves; i++) {
                    nodoPadre.setChild(i, nodoPadre.getChild(i + 1));
                }
            }
        }
    }

    //Fusiona dos nodos hermanos con una clave del padre
    private void mergeNodes(BNode<E> hermanoIzquierdo, E clavePadre, BNode<E> hermanoDerecho) {
        //Agregar la clave del padre al nodo izquierdo
        hermanoIzquierdo.insertKey(clavePadre);

        //Agregar todas las claves del nodo derecho al izquierdo
        for (int i = 0; i < hermanoDerecho.contadorClaves; i++) {
            hermanoIzquierdo.insertKey(hermanoDerecho.getKey(i));
        }

        //Agregar todos los hijos del nodo derecho al izquierdo
        for (int i = 0; i <= hermanoDerecho.contadorClaves; i++) {
            if (hermanoDerecho.getChild(i) != null) {
                hermanoIzquierdo.setChild(hermanoIzquierdo.getChildCount(), hermanoDerecho.getChild(i));
            }
        }
    }

    //Representación en cadena del árbol completo
    public String toString() {
        StringBuilder s = new StringBuilder();
        if (isEmpty()) {
            s.append("BTree esta vacío...");
        } else {
            writeTree(this.raiz, null, s);
        }
        return s.toString();
    }

    //Método recursivo que construye la representación tabular del árbol
    private StringBuilder writeTree(BNode<E> nodoActual, BNode<E> nodoPadre, StringBuilder resultado) {
        if (nodoActual != null) {
            //Si es el primer nodo (raíz), agregar encabezados
            if (nodoPadre == null && resultado.length() == 0) {
                resultado.append(String.format("%-10s %-15s %-10s %-15s%n",
                        "Id.Nodo", "Claves Nodo", "Id.Padre", "Id.Hijos"));
            }

            //Construir string de claves
            StringBuilder claves = new StringBuilder("(");
            for (int i = 0; i < nodoActual.contadorClaves; i++) {
                claves.append(nodoActual.getKey(i));
                if (i < nodoActual.contadorClaves - 1) {
                    claves.append(", ");
                }
            }
            claves.append(")");

            //Construir string del padre
            String padreStr = (nodoPadre != null) ? "[" + nodoPadre.getIdNode() + "]" : "--";

            //Construir string de hijos
            StringBuilder hijos = new StringBuilder("[");
            boolean hasChildren = false;
            for (int i = 0; i <= nodoActual.contadorClaves; i++) {
                if (nodoActual.getChild(i) != null) {
                    if (hasChildren) {
                        hijos.append(", ");
                    }
                    hijos.append(nodoActual.getChild(i).getIdNode());
                    hasChildren = true;
                }
            }
            if (!hasChildren) {
                hijos.append("--");
            }
            hijos.append("]");

            //Agregar fila a la tabla
            resultado.append(String.format("%-10s %-15s %-10s %-15s%n",
                    nodoActual.getIdNode(),
                    claves.toString(),
                    padreStr,
                    hijos.toString()));

            //Recursivamente procesar los hijos
            for (int i = 0; i <= nodoActual.contadorClaves; i++) {
                if (nodoActual.getChild(i) != null) {
                    writeTree(nodoActual.getChild(i), nodoActual, resultado);
                }
            }
        }

        return resultado;
    }
}