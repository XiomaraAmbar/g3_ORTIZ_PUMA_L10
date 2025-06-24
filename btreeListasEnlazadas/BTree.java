package btreeListasEnlazadas;

import LinkedList.ListaEnlazada;

public class BTree<E extends Comparable<E>> {
    private BNode<E> raiz;
    private int orden;
    private boolean dividido;
    private BNode<E> derechoTemporal; //nodo que se crea cuando hay overflow
    private int minimoClaves;

    //Constructor que inicializa el arbol B con el orden del arbol
    public BTree(int orden){
        this.orden = orden;
        this.raiz = null;
        minimoClaves = (int)Math.ceil(orden / 2.0) - 1;
    }

    //Verifica si el arbol esta vacio
    public boolean isEmpty(){
        return this.raiz == null;
    }

    //Metodo publico para insertar una nueva clave en el arbol
    public void insert(E nuevaClave){
        dividido = false;
        E claveMedia;
        BNode<E> nuevaRaiz;
        claveMedia = push(this.raiz,nuevaClave);
        //Si hubo division, crear nueva raiz
        if(dividido){
            nuevaRaiz = new BNode<E>(this.orden);
            nuevaRaiz.contadorClaves = 1;
            nuevaRaiz.insertKey(claveMedia);
            nuevaRaiz.setChild(0, this.raiz);
            nuevaRaiz.setChild(1, derechoTemporal);
            this.raiz = nuevaRaiz;
        }
    }

    //Metodo recursivo para insertar una clave en el arbol
    private E push(BNode<E> nodoActual,E nuevaClave){
        int posicion[] = new int[1];
        E claveMedia;
        //Caso base: nodo es null, se debe crear
        if(nodoActual == null){
            dividido = true;
            derechoTemporal = null;
            return nuevaClave;
        }
        else{
            boolean claveEncontrada;
            claveEncontrada = nodoActual.searchNode(nuevaClave, posicion);

            //Si la clave ya existe, no se inserta
            if(claveEncontrada){
                System.out.println("Item duplicado\n");
                dividido = false;
                return null;
            }
            //Insertar recursivamente en el hijo correspondiente
            claveMedia = push(nodoActual.getChild(posicion[0]),nuevaClave);
            if(dividido){
                //Si el nodo esta lleno, dividirlo
                if(nodoActual.nodeFull(this.orden))
                    claveMedia = dividedNode(nodoActual,claveMedia,posicion[0]);
                else{
                    //Si hay espacio, insertar normalmente
                    dividido = false;
                    putNode(nodoActual,claveMedia,derechoTemporal,posicion[0]);
                }
            }
            return claveMedia;
        }
    }

    //Inserta una clave y su hijo derecho en un nodo que tiene espacio
    private void putNode(BNode<E> nodoActual,E nuevaClave, BNode<E> nuevoDerecho, int posicionInsertar){
        try {
            //Crear nodo temporal para reorganizar elementos
            BNode<E> nodoTemporal = new BNode<E>(this.orden);

            //Copiar claves hasta la posicion de insercion
            for (int i = 0; i < posicionInsertar; i++) {
                nodoTemporal.insertKey(nodoActual.getKey(i));
            }

            //Insertar la nueva clave
            nodoTemporal.insertKey(nuevaClave);

            //Copiar el resto de claves
            for (int i = posicionInsertar; i < nodoActual.contadorClaves; i++) {
                nodoTemporal.insertKey(nodoActual.getKey(i));
            }

            //Reorganizar los punteros a hijos
            for (int i = 0; i <= posicionInsertar; i++) {
                nodoTemporal.setChild(i, nodoActual.getChild(i));
            }
            nodoTemporal.setChild(posicionInsertar + 1, nuevoDerecho);
            for (int i = posicionInsertar + 1; i <= nodoActual.contadorClaves; i++) {
                nodoTemporal.setChild(i + 1, nodoActual.getChild(i));
            }

            //Copiar de vuelta al nodo original
            nodoActual.claves = nodoTemporal.claves;
            nodoActual.hijos = nodoTemporal.hijos;
            nodoActual.contadorClaves = nodoTemporal.contadorClaves;

        } catch (Exception e) {
            System.err.println("Error en putNode: " + e.getMessage());
        }
    }

    //Divide un nodo que esta lleno al insertar una nueva clave
    private E dividedNode(BNode<E> nodoActual, E nuevaClave, int posicionInsertar){
        BNode<E> nuevoDerecho = derechoTemporal;
        int posicionMediana;
        //Calcular la posicion de la mediana segun donde se inserte
        posicionMediana = (posicionInsertar <= this.orden/2) ? this.orden/2 : this.orden/2+1;
        derechoTemporal = new BNode<E>(this.orden);

        try {
            //Crear nodo temporal con todas las claves incluyendo la nueva
            BNode<E> nodoTemporal = new BNode<E>(this.orden);

            //Copiar claves hasta la posicion k
            for (int i = 0; i < posicionInsertar; i++) {
                nodoTemporal.insertKey(nodoActual.getKey(i));
            }

            //Insertar la nueva clave
            nodoTemporal.insertKey(nuevaClave);

            //Copiar el resto de claves
            for (int i = posicionInsertar; i < nodoActual.contadorClaves; i++) {
                nodoTemporal.insertKey(nodoActual.getKey(i));
            }

            //Copiar hijos al nodo temporal
            for (int i = 0; i <= posicionInsertar; i++) {
                nodoTemporal.setChild(i, nodoActual.getChild(i));
            }
            nodoTemporal.setChild(posicionInsertar + 1, nuevoDerecho);
            for (int i = posicionInsertar + 1; i <= nodoActual.contadorClaves; i++) {
                nodoTemporal.setChild(i + 1, nodoActual.getChild(i));
            }

            //Obtener la clave mediana que subira al padre
            E median = nodoTemporal.getKey(posicionMediana);

            //Limpiar el nodo actual para reconstruirlo
            nodoActual.claves = new ListaEnlazada<>();
            nodoActual.hijos = new ListaEnlazada<>();
            nodoActual.contadorClaves = 0;

            //Llenar la mitad izquierda en el nodo actual
            for (int i = 0; i < posicionMediana; i++) {
                nodoActual.insertKey(nodoTemporal.getKey(i));
                nodoActual.setChild(i, nodoTemporal.getChild(i));
            }
            nodoActual.setChild(posicionMediana, nodoTemporal.getChild(posicionMediana));

            //Llenar la mitad derecha en el nuevo nodo
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

    //Metodo publico para buscar una clave en el arbol
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

    //Metodo recursivo para buscar una clave
    private ResultadoBusqueda searchKey(BNode<E> nodoActual, E clave) {
        int pos = 0;
        //Buscar la posicion correcta en el nodo actual
        while (pos < nodoActual.contadorClaves && nodoActual.getKey(pos).compareTo(clave) < 0) {
            pos++;
        }
        //Verificar si se encontro la clave
        if (pos < nodoActual.contadorClaves && nodoActual.getKey(pos).equals(clave)) {
            return new ResultadoBusqueda(true, pos);
        } else if (nodoActual.getChild(pos) != null) {
            //Buscar recursivamente en el hijo correspondiente
            return searchKey(nodoActual.getChild(pos), clave);
        } else {
            //No se encontro la clave
            return new ResultadoBusqueda(false, -1);
        }
    }

    //Clase interna para almacenar resultado de busqueda
    private static class ResultadoBusqueda {
        boolean encontrado;
        int posicionNodo;

        public ResultadoBusqueda(boolean encontrado, int posicionNodo) {
            this.encontrado = encontrado;
            this.posicionNodo = posicionNodo;
        }
    }

    //Metodo publico para eliminar una clave del arbol
    public void remove(E clave) {
        if (isEmpty()) {
            System.out.println("El árbol está vacío. No se puede eliminar la clave.");
            return;
        }

        boolean eliminado = removeKey(this.raiz, clave);
        if (eliminado) {
            System.out.println("Se eliminó la clave " + clave + " del árbol.");

            //Si la raiz queda vacia despues de eliminar, ajustar la raiz
            if (this.raiz.contadorClaves == 0 && this.raiz.getChild(0) != null) {
                this.raiz = this.raiz.getChild(0);
            }
        } else {
            System.out.println("La clave " + clave + " no se encontró en el árbol.");
        }
    }

    //Metodo recursivo para eliminar una clave
    private boolean removeKey(BNode<E> nodoActual, E clave) {
        int posicion[] = new int[1];
        boolean encontrado = nodoActual.searchNode(clave, posicion);

        //Si se encuentra la clave en el nodo actual
        if (encontrado) {
            //Si es un nodo hoja, eliminar directamente
            if (nodoActual.getChild(posicion[0]) == null) {
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

                //Eliminar el sucesor del subarbol derecho
                boolean success = removeKey(nodoActual.getChild(posicion[0] + 1), claveSucesor);

                //Verificar underflow en el hijo derecho
                if (success && nodoActual.getChild(posicion[0] + 1).contadorClaves < minimoClaves) {
                    fixUnderflow(nodoActual, posicion[0] + 1);
                }
                return encontrado;
            }
        }
        //Si la clave no se encuentra, buscar en el hijo correspondiente
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

    //Corrige el underflow cuando un nodo tiene menos claves del minimo
    private void fixUnderflow(BNode<E> nodoPadre, int indiceHijo) {
        BNode<E> hijoDesbalanceado = nodoPadre.getChild(indiceHijo);
        BNode<E> hermanoIzquierdo = (indiceHijo > 0) ? nodoPadre.getChild(indiceHijo - 1) : null;
        BNode<E> hermanoDerecho = (indiceHijo < nodoPadre.contadorClaves) ? nodoPadre.getChild(indiceHijo + 1) : null;

        //Intentar obtener una clave prestada del hermano derecho primero
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
        //Intentar obtener una clave prestada del hermano izquierdo
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

            //Subir la ultima clave del hermano izquierdo al padre
            E ultimaClave = hermanoIzquierdo.getKey(hermanoIzquierdo.contadorClaves - 1);
            nodoPadre.removeKey(clavePadre);
            nodoPadre.insertKey(ultimaClave);

            //Actualizar el hijo
            hijoDesbalanceado.claves = nodoTemporal.claves;
            hijoDesbalanceado.hijos = nodoTemporal.hijos;
            hijoDesbalanceado.contadorClaves = nodoTemporal.contadorClaves;

            //Eliminar la ultima clave del hermano izquierdo
            hermanoIzquierdo.removeKey(ultimaClave);
        }
        //Si no es posible prestamo, realizar fusion
        else {
            //Fusion con hermano derecho como preferencia
            if (hermanoDerecho != null) {
                mergeNodes(hijoDesbalanceado, nodoPadre.getKey(indiceHijo), hermanoDerecho);
                nodoPadre.removeKey(nodoPadre.getKey(indiceHijo));

                //Reorganizar hijos del padre
                for (int i = indiceHijo + 1; i < nodoPadre.contadorClaves; i++) {
                    nodoPadre.setChild(i, nodoPadre.getChild(i + 1));
                }
            }
            //Fusion con hermano izquierdo
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

    //Convierte el arbol a string para visualizacion
    public String toString() {
        StringBuilder s = new StringBuilder();
        if (isEmpty()) {
            s.append("BTree esta vacío...");
        } else {
            writeTree(this.raiz, null, s);
        }
        return s.toString();
    }

    //Metodo recursivo para escribir el arbol en formato tabla
    private StringBuilder writeTree(BNode<E> nodoActual, BNode<E> nodoPadre, StringBuilder resultado) {
        if (nodoActual != null) {
            //Si es el primer nodo raiz, agregar encabezados
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