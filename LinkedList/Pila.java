package LinkedList;

public class Pila<E>{

    private ListaEnlazada<E> pila;

    public Pila(){
        pila = new ListaEnlazada<>();
    }

    //Verifica si la pila esta vacía
    public boolean isEmpty(){
        return pila.isEmpty();
    }

    //Agrega el elemento x al tope de la pila
    public void push(E elemento) throws MensajeException {
        if (isEmpty()){
            pila.insertFirst(elemento);
        }
        else{
            pila.insertLast(elemento);
        }
    }

    //Elimina el elemento del tope y lo retorna
    public E pop() throws MensajeException {
        if (isEmpty()){
            throw new MensajeException("Pila vacía, no hay tope.");
        }
        else{
            E antesTop = top();
            pila.removeNodeK(pila.length()-1);
            return antesTop;
        }
    }

    //Retorna el último elemento sin eliminarlo (tope) -> valor
    public E top() throws MensajeException {
        return pila.searchK(pila.length()-1);
        //return pila.search(pila.searchK(pila.length()-1));
    }

    //Muestra el elemento del tope sin eliminarlo -> muestra el valor del top
    public void peek() throws MensajeException {
        top();
        //return pila.searchK(pila.length()-1);
        //return pila.search(pila.searchK(pila.length()-1));
    }

    //Elimina todos los elementos, dejando la pila vacía.
    public void destroyStack(){
        pila.destroyList();
    }

    //verifica si la pila está llena. Se usa cuando la pila esta implementa sobre una estructura
    //    de datos estática.
    public void isFull(){

    }

    //Imprime la pila
    public void print() throws MensajeException {
        pila.print();
    }
}
