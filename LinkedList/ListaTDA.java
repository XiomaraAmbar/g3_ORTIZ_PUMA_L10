package LinkedList;

//Interface Lista TDA
public interface ListaTDA<E> {

    boolean isEmpty(); //Método que determina si la lista esta vacía
    int length() throws MensajeException; //Método que determina la longitud de elementos de la lista (el tamaño)
    void destroyList(); //Método que elimina los elementos de la lista dejandola vacía
    int search(E valor) throws MensajeException; //Método que verifica si el elemento x está en la lista y retorna su posición
    E searchK(int k) throws MensajeException; //Método que busca el k-ésimo elemento
    void insertFirst(E valor); //Método que inserta el nuevo nodo al inicio de la lista
    void insertLast(E valor) throws MensajeException; //Método que inserta el nuevo nodo al final de la lista
    void removeNode(E valor) throws MensajeException; //Método que elimina un nodo de la lista enlazada
}
