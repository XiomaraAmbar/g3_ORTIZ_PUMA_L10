package btreeListasEnlazadas;

public class TestBTree {

    public static void main(String[] args) {
        System.out.println("=== PRUEBAS DEL B-TREE CON LISTAS ENLAZADAS ===\n");

        // Crear B-Tree de orden 3
        BTree<Integer> btree = new BTree<>(3);

        // Prueba 1: Verificar que el árbol está vacío
        System.out.println("1. PRUEBA DE ÁRBOL VACÍO:");
        System.out.println("¿Está vacío? " + btree.isEmpty());
        System.out.println("Contenido del árbol:");
        System.out.println(btree.toString());
        System.out.println();

        // Prueba 2: Insertar elementos
        System.out.println("2. PRUEBA DE INSERCIÓN:");
        int[] valores = {10, 20, 5, 6, 12, 30, 7, 17, 26, 34, 3, 8, 19, 22, 40};

        System.out.println("Insertando valores: ");
        for (int valor : valores) {
            System.out.print(valor + " ");
            btree.insert(valor);
        }
        System.out.println("\n");

        System.out.println("Estado del árbol después de las inserciones:");
        System.out.println(btree.toString());
        System.out.println();

        // Prueba 3: Búsqueda de elementos
        System.out.println("3. PRUEBA DE BÚSQUEDA:");
        int[] buscar = {10, 15, 20, 35, 3, 100};

        for (int valor : buscar) {
            boolean encontrado = btree.search(valor);
            System.out.println("Buscar " + valor + ": " + (encontrado ? "ENCONTRADO" : "NO ENCONTRADO"));
        }
        System.out.println();

        // Prueba 4: Insertar elementos duplicados
        System.out.println("4. PRUEBA DE ELEMENTOS DUPLICADOS:");
        System.out.println("Intentando insertar 10 (duplicado):");
        btree.insert(10);
        System.out.println("Intentando insertar 20 (duplicado):");
        btree.insert(20);
        System.out.println();

        // Prueba 5: Eliminación de elementos
        System.out.println("5. PRUEBA DE ELIMINACIÓN:");
        int[] eliminar = {3, 20, 10, 100, 30};

        for (int valor : eliminar) {
            System.out.println("Eliminando " + valor + ":");
            btree.remove(valor);
            System.out.println("Estado del árbol:");
            System.out.println(btree.toString());
            System.out.println();
        }

        // Prueba 6: Operaciones sobre árbol con pocos elementos
        System.out.println("6. PRUEBA CON ÁRBOL REDUCIDO:");
        System.out.println("Estado actual del árbol:");
        System.out.println(btree.toString());

        System.out.println("Búsquedas en árbol reducido:");
        int[] buscarReducido = {5, 6, 7, 8, 12, 17};
        for (int valor : buscarReducido) {
            boolean encontrado = btree.search(valor);
            System.out.println("Buscar " + valor + ": " + (encontrado ? "ENCONTRADO" : "NO ENCONTRADO"));
        }
        System.out.println();

        // Prueba 7: Crear un nuevo B-Tree de orden diferente
        System.out.println("7. PRUEBA CON B-TREE DE ORDEN 5:");
        BTree<Integer> btree5 = new BTree<>(5);

        int[] valores5 = {50, 25, 75, 10, 30, 60, 80, 5, 15, 35, 65, 85, 1, 20, 40, 70, 90};
        System.out.println("Insertando en B-Tree de orden 5:");
        for (int valor : valores5) {
            System.out.print(valor + " ");
            btree5.insert(valor);
        }
        System.out.println("\n");

        System.out.println("B-Tree de orden 5:");
        System.out.println(btree5.toString());
        System.out.println();

        // Prueba 8: Prueba con Strings
        System.out.println("8. PRUEBA CON STRINGS:");
        BTree<String> btreeStr = new BTree<>(3);

        String[] palabras = {"manzana", "banana", "cereza", "durazno", "fresa", "uva", "kiwi", "limón"};
        System.out.println("Insertando palabras:");
        for (String palabra : palabras) {
            System.out.print(palabra + " ");
            btreeStr.insert(palabra);
        }
        System.out.println("\n");

        System.out.println("B-Tree de strings:");
        System.out.println(btreeStr.toString());

        System.out.println("Búsquedas en B-Tree de strings:");
        String[] buscarStr = {"banana", "naranja", "fresa", "pera"};
        for (String palabra : buscarStr) {
            boolean encontrado = btreeStr.search(palabra);
            System.out.println("Buscar '" + palabra + "': " + (encontrado ? "ENCONTRADO" : "NO ENCONTRADO"));
        }
        System.out.println();

        // Prueba 9: Eliminar hasta vaciar el árbol
        System.out.println("9. PRUEBA DE VACIADO COMPLETO:");
        BTree<Integer> btreeVacio = new BTree<>(3);
        int[] valoresVacio = {10, 20, 30};

        System.out.println("Insertando: 10, 20, 30");
        for (int valor : valoresVacio) {
            btreeVacio.insert(valor);
        }
        System.out.println("Árbol inicial:");
        System.out.println(btreeVacio.toString());

        System.out.println("Eliminando todos los elementos:");
        for (int valor : valoresVacio) {
            System.out.println("Eliminando " + valor);
            btreeVacio.remove(valor);
            System.out.println(btreeVacio.toString());
        }

        System.out.println("¿Está vacío después de eliminar todo? " + btreeVacio.isEmpty());
        System.out.println();

        System.out.println("=== FIN DE LAS PRUEBAS ===");
    }

    // Método auxiliar para crear separadores visuales
    private static void imprimirSeparador(String titulo) {
        System.out.println("=" + "=".repeat(titulo.length() + 2) + "=");
        System.out.println("| " + titulo + " |");
        System.out.println("=" + "=".repeat(titulo.length() + 2) + "=");
    }

    // Método para hacer pruebas específicas de rendimiento
    public static void pruebaRendimiento() {
        System.out.println("\n=== PRUEBA DE RENDIMIENTO ===");

        BTree<Integer> btree = new BTree<>(5);

        // Insertar muchos elementos
        long startTime = System.currentTimeMillis();
        for (int i = 1; i <= 1000; i++) {
            btree.insert(i);
        }
        long endTime = System.currentTimeMillis();

        System.out.println("Tiempo de inserción de 1000 elementos: " + (endTime - startTime) + " ms");

        // Buscar elementos
        startTime = System.currentTimeMillis();
        for (int i = 1; i <= 1000; i += 10) {
            btree.search(i);
        }
        endTime = System.currentTimeMillis();

        System.out.println("Tiempo de búsqueda de 100 elementos: " + (endTime - startTime) + " ms");

        // Eliminar elementos
        startTime = System.currentTimeMillis();
        for (int i = 1; i <= 500; i += 2) {
            btree.remove(i);
        }
        endTime = System.currentTimeMillis();

        System.out.println("Tiempo de eliminación de 250 elementos: " + (endTime - startTime) + " ms");
    }
}
