package btreeFinal;

public class PruebaBTree {
    public static void main(String[] args) {
        // Crear árbol B de orden 3 (típico)
        BTree<Integer> arbol = new BTree<>(3);

        // Insertar elementos
        int[] elementos = {10, 20, 5, 6, 12, 30, 7, 17};
        for (int elem : elementos) {
            arbol.insert(elem);
        }

        // Mostrar el árbol
        System.out.println("Árbol B después de insertar:");
        System.out.println(arbol);

        // Buscar algunas claves
        System.out.println("\nBúsqueda de claves:");
        System.out.println("¿Existe 6? " + arbol.search(6));   // true
        System.out.println("¿Existe 15? " + arbol.search(15)); // false

        // Eliminar una clave
        System.out.println("\nEliminando clave 6:");
        arbol.remove(6);

        // Mostrar el árbol después de la eliminación
        System.out.println("Árbol B después de eliminar:");
        System.out.println(arbol);

        // Probar eliminación de una clave inexistente
        System.out.println("\nIntentando eliminar clave 100:");
        arbol.remove(100);
    }
}
