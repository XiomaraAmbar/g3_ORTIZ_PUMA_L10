package LinkedList;

public class Cola<E> {
    private ListaEnlazada<E> cola;

    public Cola(){
        cola = new ListaEnlazada<>();
    }

    //Verifica si la cola esta vacía
    public boolean isEmpty(){
        return cola.isEmpty();
    }

    //Agrega el elemento x al final de la cola
    public void enqueue(E elemento) throws MensajeException {
        if (isEmpty()){
            cola.insertFirst(elemento);
        }
        else{
            cola.insertLast(elemento);
        }
    }

    //Retorna el elemento que se ubica al inicio de la cola(desencolar)
    public E dequeue() throws MensajeException {
        E primero = front();
        cola.removeNodeK(0);
        return primero;
    }

    //Elimina los elementos de la cola dejándola vacía.
    public void destroyQueue(){
        cola.destroyList();
    }

    //Retorna el elemento inicial de la cola
    public E front() throws MensajeException {
        return cola.searchK(0);
    }

    //Retorna el elemento final de la cola
    public E back() throws MensajeException {
        return cola.searchK(cola.length()-1);
    }

    //Verifica si la cola está llena o no. Se usa cuando la cola está implementada sobre una
    //    estructura estática.
    public void isFull(){

    }

    //Imprime la pila
    public void print() throws MensajeException {
        cola.print();
    }
}
