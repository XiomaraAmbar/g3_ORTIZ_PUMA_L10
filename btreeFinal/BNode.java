package btreeFinal;

import java.util.ArrayList;

public class BNode<E extends Comparable<E>> {
    protected ArrayList<E> claves;
    protected ArrayList<BNode<E>> hijos;
    protected int contadorClaves;

    //Inicializa nodo con capacidad según orden del árbol
    public BNode(int orden) {
        this.claves = new ArrayList<E>(orden);
        this.hijos = new ArrayList<BNode<E>>(orden + 1); //Los hijos son orden + 1
        this.contadorClaves = 0;
        this.idNodo = 0;

        //Inicializar listas con valores null para reservar espacio
        for (int i = 0; i < orden - 1; i++) { //orden - 1 claves máximo
            this.claves.add(null);
        }
        for (int i = 0; i < orden; i++) { //orden hijos máximo
            this.hijos.add(null);
        }

        asignarId();
    }

    public boolean nodeFull(int orden) {
        return this.contadorClaves == orden - 1;
    }

    public boolean nodeEmpty() {
        return this.contadorClaves == 0;
    }

    //Busca clave en nodo y retorna posición donde está o debería estar
    public boolean searchNode(E clave, int[] posicion) {
        int i = 0;

        //Buscar posición correcta comparando con claves existentes
        while (i < this.contadorClaves && clave.compareTo(this.claves.get(i)) > 0) {
            i++;
        }

        posicion[0] = i;

        //Verificar si clave fue encontrada exactamente
        if (i < this.contadorClaves && clave.compareTo(this.claves.get(i)) == 0) {
            return true; //Clave encontrada
        } else {
            return false; //Clave no encontrada, posicion indica donde buscar en hijos
        }
    }

    public E getKey(int index) {
        if (index >= 0 && index < this.contadorClaves) {
            return this.claves.get(index);
        }
        return null;
    }

    public BNode<E> getChild(int index) {
        if (index >= 0 && index < this.hijos.size()) {
            return this.hijos.get(index);
        }
        return null;
    }

    //Asigna hijo en posición específica expandiendo lista si es necesario
    public void setChild(int index, BNode<E> child) {
        //Expandir ArrayList si no tiene suficiente capacidad
        while (this.hijos.size() <= index) {
            this.hijos.add(null);
        }
        this.hijos.set(index, child);
    }

    //Elimina clave específica del nodo desplazando elementos restantes
    public void removeKey(E clave) {
        int posicion = -1;

        //Localizar posición de la clave a eliminar
        for (int i = 0; i < this.contadorClaves; i++) {
            if (this.claves.get(i).compareTo(clave) == 0) {
                posicion = i;
                break;
            }
        }

        if (posicion != -1) {
            //Desplazar claves hacia la izquierda para llenar hueco
            for (int i = posicion; i < this.contadorClaves - 1; i++) {
                this.claves.set(i, this.claves.get(i + 1));
            }
            this.contadorClaves--;
        }
    }

    //Inserta clave manteniendo orden ascendente
    public void insertKey(E clave) {
        int posicion = 0;

        //Encontrar posición de inserción manteniendo orden
        while (posicion < this.contadorClaves && clave.compareTo(this.claves.get(posicion)) > 0) {
            posicion++;
        }

        //Desplazar claves hacia la derecha para hacer espacio
        for (int i = this.contadorClaves; i > posicion; i--) {
            if (this.claves.size() <= i) {
                this.claves.add(null);
            }
            this.claves.set(i, this.claves.get(i - 1));
        }

        //Insertar nueva clave en posición correcta
        if (this.claves.size() <= posicion) {
            this.claves.add(null);
        }
        this.claves.set(posicion, clave);
        this.contadorClaves++;
    }

    //Cuenta hijos no nulos del nodo
    public int getChildCount() {
        int count = 0;
        for (int i = 0; i <= this.contadorClaves; i++) {
            if (this.getChild(i) != null) {
                count = i + 1;
            }
        }
        return count;
    }

    //Sistema de identificación única para nodos
    private static int contadorId = 0;
    private int idNodo;

    public static void reiniciarContadorId() {
        contadorId = 0;
    }

    //Asigna ID único incremental al crear nodo
    private void asignarId() {
        this.idNodo = ++contadorId;
    }

    //Retorna ID formateado para visualización del árbol
    public String getIdNode() {
        if (this.idNodo == 0) {
            asignarId();
        }
        return String.format("%02d", this.idNodo);
    }

    //Representación en cadena de las claves del nodo
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (int i = 0; i < this.contadorClaves; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(this.claves.get(i));
        }

        sb.append("]");
        return sb.toString();
    }
}