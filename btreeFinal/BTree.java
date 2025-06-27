package btreeFinal;

import LinkedList.Cola;
import LinkedList.MensajeException;

public class BTree<E extends Comparable<E>> {
    private BNode<E> raiz;
    private int orden;
    private boolean dividido; //Controla si se realizó división durante inserción
    private BNode<E> derechoTemporal; //Almacena nodo derecho resultante de división
    private int minimoClaves; //Número mínimo de claves por nodo

    //Inicializa árbol B con orden especificado
    public BTree(int orden) {
        this.orden = orden;
        this.raiz = null;
        minimoClaves = (int) Math.ceil(orden / 2.0) - 1;
    }

    public int minimoClaves(int orden){
        if (orden%2 == 0) { //es par
            return (int) Math.ceil(orden / 2.0);
        }
        else{
            return (int) Math.ceil(orden / 2.0) - 1;
        }
    }

    public boolean isEmpty() {
        return this.raiz == null;
    }

    /************************************************************************************
     * MÉTODOS DE INSERCIÓN
     ************************************************************************************/

    //Inserta nueva clave manejando división de raíz si es necesario
    public void insert(E nuevaClave){
        dividido = false;
        E claveMedia;
        BNode<E> nuevaRaiz;
        claveMedia = push(this.raiz,nuevaClave);

        //Si hubo división, crear nueva raíz
        if(dividido){
            nuevaRaiz = new BNode<E>(this.orden);
            nuevaRaiz.contadorClaves = 1;
            nuevaRaiz.claves.set(0,claveMedia);
            nuevaRaiz.setChild(0, this.raiz);
            nuevaRaiz.setChild(1, derechoTemporal);
            this.raiz = nuevaRaiz;
        }
    }

    //Inserta recursivamente y propaga divisiones hacia arriba
    private E push(BNode<E> nodoActual, E nuevaClave){
        int posicion[] = new int[1];
        E claveMedia;

        //Caso base: nodo nulo, insertar aquí
        if(nodoActual == null){
            dividido = true;
            derechoTemporal = null;
            return nuevaClave;
        }
        else{
            boolean claveEncontrada;
            claveEncontrada = nodoActual.searchNode(nuevaClave, posicion);

            //No permitir duplicados
            if(claveEncontrada){
                System.out.println("Item duplicado\n");
                dividido = false;
                return null;
            }

            //Insertar recursivamente en hijo apropiado
            claveMedia = push(nodoActual.getChild(posicion[0]),nuevaClave);

            //Manejar división propagada desde hijo
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

    //Inserta clave e hijo en nodo con espacio disponible
    private void putNode(BNode<E> nodoActual, E nuevaClave, BNode<E> nuevoDerecho, int posicionInsertar){
        int i;
        //Desplazar claves hacia la derecha
        for (i = nodoActual.contadorClaves - 1; i >= posicionInsertar; i--) {
            if (nodoActual.claves.size() <= i + 1) {
                nodoActual.claves.add(null);
            }
            nodoActual.claves.set(i + 1, nodoActual.getKey(i));
        }

        //Desplazar hijos hacia la derecha
        for (i = nodoActual.contadorClaves; i >= posicionInsertar + 1; i--) {
            nodoActual.setChild(i + 1, nodoActual.getChild(i));
        }

        //Insertar nueva clave e hijo en posición correcta
        if (nodoActual.claves.size() <= posicionInsertar) {
            nodoActual.claves.add(null);
        }
        nodoActual.claves.set(posicionInsertar, nuevaClave);
        nodoActual.setChild(posicionInsertar + 1, nuevoDerecho);
        nodoActual.contadorClaves++;
    }

    //Divide nodo lleno y retorna clave mediana
    private E dividedNode(BNode<E> nodoActual, E nuevaClave, int posicionInsertar){
        BNode<E> nuevoDerecho = derechoTemporal;
        int i, posicionMediana;

        //Calcular posición de la mediana
        posicionMediana = (posicionInsertar <= this.orden/2) ? this.orden/2 : this.orden/2+1;
        derechoTemporal = new BNode<E>(this.orden);

        //Mover mitad superior de claves e hijos al nuevo nodo
        for (i = posicionMediana; i < this.orden - 1; i++) {
            derechoTemporal.claves.set(i - posicionMediana, nodoActual.claves.get(i));
            derechoTemporal.hijos.set(i - posicionMediana + 1, nodoActual.hijos.get(i+1));
        }

        derechoTemporal.contadorClaves = (this.orden - 1) - posicionMediana;
        nodoActual.contadorClaves = posicionMediana;

        //Insertar nueva clave en el nodo apropiado
        if (posicionInsertar <= this.orden / 2) {
            putNode(nodoActual, nuevaClave, nuevoDerecho, posicionInsertar);
        } else {
            putNode(derechoTemporal, nuevaClave, nuevoDerecho, posicionInsertar - posicionMediana);
        }

        //Extraer y retornar clave mediana
        E claveMediana = nodoActual.claves.get(nodoActual.contadorClaves - 1);
        derechoTemporal.hijos.set(0, nodoActual.hijos.get(nodoActual.contadorClaves));
        nodoActual.contadorClaves--;

        return claveMediana;
    }

    /************************************************************************************
     * MÉTODOS DE BÚSQUEDA
     ************************************************************************************/

    public boolean search(E clave) {
        if (isEmpty() || clave == null) {
            return false;
        }
        return searchKey(this.raiz, clave);
    }

    //Busca clave recursivamente en el árbol
    private boolean searchKey(BNode<E> nodoActual, E clave) {
        if (nodoActual == null) {
            return false;
        }

        int[] posicion = new int[1];
        boolean encontrado = nodoActual.searchNode(clave, posicion);

        if (encontrado) {
            return true;
        } else {
            return searchKey(nodoActual.getChild(posicion[0]), clave);
        }
    }

    /************************************************************************************
     * MÉTODOS DE ELIMINACIÓN
     ************************************************************************************/

    public void remove(E clave) {
        if (isEmpty()) {
            System.out.println("El árbol está vacío. No se puede eliminar la clave.");
            return;
        }

        boolean eliminado = removeKey(this.raiz, clave);

        if (eliminado) {
            System.out.println("Se eliminó la clave " + clave + " del árbol.");
            //Ajustar raíz si quedó vacía
            if (this.raiz.contadorClaves == 0 && this.raiz.getChild(0) != null) {
                this.raiz = this.raiz.getChild(0);
            }
        } else {
            System.out.println("La clave " + clave + " no se encontró en el árbol.");
        }
    }

    //Elimina clave recursivamente manejando underflow
    private boolean removeKey(BNode<E> nodoActual, E clave) {
        if (nodoActual == null) {
            return false;
        }

        int[] posicion = new int[1];
        boolean encontrado = nodoActual.searchNode(clave, posicion);

        if (encontrado) {
            //Clave encontrada en nodo actual
            if (nodoActual.getChild(0) == null) {
                //Nodo hoja: eliminar directamente
                nodoActual.removeKey(clave);
                return true;
            } else {
                //Nodo interno: reemplazar con sucesor/predecesor
                return removeFromInternalNode(nodoActual, clave, posicion[0]);
            }
        } else {
            //Buscar en hijo correspondiente
            boolean eliminado = removeKey(nodoActual.getChild(posicion[0]), clave);

            //Verificar y corregir underflow en hijo
            if (eliminado && nodoActual.getChild(posicion[0]) != null &&
                    nodoActual.getChild(posicion[0]).contadorClaves < minimoClaves) {
                fixUnderflow(nodoActual, posicion[0]);
            }

            return eliminado;
        }
    }

    //Elimina clave de nodo interno usando sucesor o predecesor
    private boolean removeFromInternalNode(BNode<E> nodoActual, E clave, int posicionClave) {
        BNode<E> hijoIzquierdo = nodoActual.getChild(posicionClave);
        BNode<E> hijoDerecho = nodoActual.getChild(posicionClave + 1);

        //Intentar usar sucesor del subárbol derecho
        if (hijoDerecho != null && hijoDerecho.contadorClaves >= minimoClaves) {
            E sucesor = getMinKey(hijoDerecho);
            nodoActual.removeKey(clave);
            nodoActual.insertKey(sucesor);

            boolean eliminado = removeKey(hijoDerecho, sucesor);
            if (eliminado && hijoDerecho.contadorClaves < minimoClaves) {
                fixUnderflow(nodoActual, posicionClave + 1);
            }
            return true;
        }
        //Intentar usar predecesor del subárbol izquierdo
        else if (hijoIzquierdo != null && hijoIzquierdo.contadorClaves >= minimoClaves) {
            E predecesor = getMaxKey(hijoIzquierdo);
            nodoActual.removeKey(clave);
            nodoActual.insertKey(predecesor);

            boolean eliminado = removeKey(hijoIzquierdo, predecesor);
            if (eliminado && hijoIzquierdo.contadorClaves < minimoClaves) {
                fixUnderflow(nodoActual, posicionClave);
            }
            return true;
        }
        //Fusionar hijos si ambos tienen mínimo de claves
        else {
            if (hijoIzquierdo != null && hijoDerecho != null) {
                mergeNodes(hijoIzquierdo, clave, hijoDerecho);
                nodoActual.removeKey(clave);

                //Reorganizar hijos del padre
                for (int i = posicionClave + 1; i <= nodoActual.contadorClaves; i++) {
                    nodoActual.setChild(i, nodoActual.getChild(i + 1));
                }

                return removeKey(hijoIzquierdo, clave);
            }
        }

        return false;
    }

    //Encuentra clave mínima navegando al hijo más izquierdo
    private E getMinKey(BNode<E> nodo) {
        while (nodo.getChild(0) != null) {
            nodo = nodo.getChild(0);
        }
        return nodo.getKey(0);
    }

    //Encuentra clave máxima navegando al hijo más derecho
    private E getMaxKey(BNode<E> nodo) {
        while (nodo.getChild(nodo.contadorClaves) != null) {
            nodo = nodo.getChild(nodo.contadorClaves);
        }
        return nodo.getKey(nodo.contadorClaves - 1);
    }

    //Corrige underflow mediante préstamo o fusión
    private void fixUnderflow(BNode<E> nodoPadre, int indiceHijo) {
        BNode<E> hijoDesbalanceado = nodoPadre.getChild(indiceHijo);

        if (hijoDesbalanceado == null) {
            return;
        }

        BNode<E> hermanoIzquierdo = (indiceHijo > 0) ? nodoPadre.getChild(indiceHijo - 1) : null;
        BNode<E> hermanoDerecho = (indiceHijo < nodoPadre.contadorClaves) ? nodoPadre.getChild(indiceHijo + 1) : null;

        //Intentar préstamo de hermano derecho
        if (hermanoDerecho != null && hermanoDerecho.contadorClaves > minimoClaves) {
            borrowRight(nodoPadre, indiceHijo);
        }
        //Intentar préstamo de hermano izquierdo
        else if (hermanoIzquierdo != null && hermanoIzquierdo.contadorClaves > minimoClaves) {
            borrowLeft(nodoPadre, indiceHijo);
        }
        //Fusionar con hermano disponible
        else {
            if (hermanoDerecho != null) {
                mergeRight(nodoPadre, indiceHijo);
            } else if (hermanoIzquierdo != null) {
                mergeLeft(nodoPadre, indiceHijo);
            }
        }
    }

    //Toma clave prestada del hermano derecho
    private void borrowRight(BNode<E> nodoPadre, int indiceHijo) {
        BNode<E> hijoDesbalanceado = nodoPadre.getChild(indiceHijo);
        BNode<E> hermanoDerecho = nodoPadre.getChild(indiceHijo + 1);

        //Bajar clave del padre e insertar primer hijo del hermano
        E clavePadre = nodoPadre.getKey(indiceHijo);
        hijoDesbalanceado.insertKey(clavePadre);

        if (hermanoDerecho.getChild(0) != null) {
            hijoDesbalanceado.setChild(hijoDesbalanceado.contadorClaves, hermanoDerecho.getChild(0));
        }

        //Subir primera clave del hermano al padre
        E primeraClave = hermanoDerecho.getKey(0);
        nodoPadre.removeKey(clavePadre);
        nodoPadre.insertKey(primeraClave);

        hermanoDerecho.removeKey(primeraClave);

        //Reorganizar hijos del hermano derecho
        for (int i = 0; i <= hermanoDerecho.contadorClaves; i++) {
            hermanoDerecho.setChild(i, hermanoDerecho.getChild(i + 1));
        }
    }

    //Toma clave prestada del hermano izquierdo
    private void borrowLeft(BNode<E> nodoPadre, int indiceHijo) {
        BNode<E> hijoDesbalanceado = nodoPadre.getChild(indiceHijo);
        BNode<E> hermanoIzquierdo = nodoPadre.getChild(indiceHijo - 1);

        E clavePadre = nodoPadre.getKey(indiceHijo - 1);

        //Desplazar claves del hijo desbalanceado hacia la derecha
        for (int i = hijoDesbalanceado.contadorClaves; i > 0; i--) {
            if (hijoDesbalanceado.claves.size() <= i) {
                hijoDesbalanceado.claves.add(null);
            }
            hijoDesbalanceado.claves.set(i, hijoDesbalanceado.getKey(i - 1));
        }

        //Insertar clave del padre en primera posición
        hijoDesbalanceado.claves.set(0, clavePadre);
        hijoDesbalanceado.contadorClaves++;

        //Desplazar hijos hacia la derecha
        for (int i = hijoDesbalanceado.contadorClaves; i > 0; i--) {
            hijoDesbalanceado.setChild(i, hijoDesbalanceado.getChild(i - 1));
        }

        //Mover último hijo del hermano izquierdo
        if (hermanoIzquierdo.getChild(hermanoIzquierdo.contadorClaves) != null) {
            hijoDesbalanceado.setChild(0, hermanoIzquierdo.getChild(hermanoIzquierdo.contadorClaves));
        }

        //Subir última clave del hermano izquierdo al padre
        E ultimaClave = hermanoIzquierdo.getKey(hermanoIzquierdo.contadorClaves - 1);
        nodoPadre.removeKey(clavePadre);
        nodoPadre.insertKey(ultimaClave);

        hermanoIzquierdo.removeKey(ultimaClave);
        hermanoIzquierdo.setChild(hermanoIzquierdo.contadorClaves + 1, null);
    }

    private void mergeRight(BNode<E> nodoPadre, int indiceHijo) {
        BNode<E> hijoIzquierdo = nodoPadre.getChild(indiceHijo);
        BNode<E> hijoDerecho = nodoPadre.getChild(indiceHijo + 1);
        E clavePadre = nodoPadre.getKey(indiceHijo);

        mergeNodes(hijoIzquierdo, clavePadre, hijoDerecho);
        nodoPadre.removeKey(clavePadre);

        //Reorganizar referencias de hijos
        for (int i = indiceHijo + 1; i <= nodoPadre.contadorClaves; i++) {
            nodoPadre.setChild(i, nodoPadre.getChild(i + 1));
        }
    }

    private void mergeLeft(BNode<E> nodoPadre, int indiceHijo) {
        BNode<E> hermanoIzquierdo = nodoPadre.getChild(indiceHijo - 1);
        BNode<E> hijoDesbalanceado = nodoPadre.getChild(indiceHijo);
        E clavePadre = nodoPadre.getKey(indiceHijo - 1);

        mergeNodes(hermanoIzquierdo, clavePadre, hijoDesbalanceado);
        nodoPadre.removeKey(clavePadre);

        //Reorganizar referencias de hijos
        for (int i = indiceHijo; i <= nodoPadre.contadorClaves; i++) {
            nodoPadre.setChild(i, nodoPadre.getChild(i + 1));
        }
    }

    //Combina dos nodos hermanos con clave del padre
    private void mergeNodes(BNode<E> hermanoIzquierdo, E clavePadre, BNode<E> hermanoDerecho) {
        //Agregar clave del padre al nodo izquierdo
        hermanoIzquierdo.insertKey(clavePadre);

        //Transferir todas las claves del nodo derecho
        for (int i = 0; i < hermanoDerecho.contadorClaves; i++) {
            hermanoIzquierdo.insertKey(hermanoDerecho.getKey(i));
        }

        //Transferir todos los hijos del nodo derecho
        for (int i = 0; i <= hermanoDerecho.contadorClaves; i++) {
            if (hermanoDerecho.getChild(i) != null) {
                hermanoIzquierdo.setChild(hermanoIzquierdo.getChildCount(), hermanoDerecho.getChild(i));
            }
        }
    }

    /************************************************************************************
     * REPRESENTACIÓN ARBOL EN CADENA
     ************************************************************************************/

    public String toString() {
        StringBuilder s = new StringBuilder();
        if (isEmpty()) {
            s.append("BTree esta vacío...");
        } else {
            writeTree(this.raiz, s);
        }
        return s.toString();
    }

    //Genera representación tabular usando recorrido por niveles
    private StringBuilder writeTree(BNode<E> nodoRaiz, StringBuilder resultado) {
        if (nodoRaiz == null) {
            return resultado;
        }

        try {
            //Colas para procesamiento por niveles
            Cola<BNode<E>> colaNodos = new Cola<>();
            Cola<BNode<E>> colaPadres = new Cola<>();

            colaNodos.enqueue(nodoRaiz);
            colaPadres.enqueue(null);

            //Encabezados de tabla
            resultado.append(String.format("%-10s %-15s %-10s %-15s%n",
                    "Id.Nodo", "Claves Nodo", "Id.Padre", "Id.Hijos"));

            //Procesar todos los nodos nivel por nivel
            while (!colaNodos.isEmpty()) {
                BNode<E> nodoActual = colaNodos.dequeue();
                BNode<E> nodoPadre = colaPadres.dequeue();

                //Formatear claves del nodo
                StringBuilder claves = new StringBuilder("(");
                for (int i = 0; i < nodoActual.contadorClaves; i++) {
                    claves.append(nodoActual.getKey(i));
                    if (i < nodoActual.contadorClaves - 1) {
                        claves.append(", ");
                    }
                }
                claves.append(")");

                //Formatear información del padre
                String padreStr = (nodoPadre != null) ? "[" + nodoPadre.getIdNode() + "]" : "--";

                //Formatear información de hijos y agregar a cola
                StringBuilder hijos = new StringBuilder("[");
                boolean conHijos = false;
                for (int i = 0; i <= nodoActual.contadorClaves; i++) {
                    if (nodoActual.getChild(i) != null) {
                        if (conHijos) {
                            hijos.append(", ");
                        }
                        hijos.append(nodoActual.getChild(i).getIdNode());
                        conHijos = true;

                        //Agregar hijo para procesamiento posterior
                        colaNodos.enqueue(nodoActual.getChild(i));
                        colaPadres.enqueue(nodoActual);
                    }
                }
                if (!conHijos) {
                    hijos.append("--");
                }
                hijos.append("]");

                //Agregar fila formateada a la tabla
                resultado.append(String.format("%-10s %-15s %-10s %-15s%n",
                        nodoActual.getIdNode(),
                        claves.toString(),
                        padreStr,
                        hijos.toString()));
            }
        } catch (MensajeException e) {
            resultado.append("Error al procesar el árbol: ").append(e.getMessage());
        }

        return resultado;
    }
}
