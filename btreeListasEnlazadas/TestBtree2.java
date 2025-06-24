package btreeListasEnlazadas;

public class TestBtree2 {

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

        // Prueba 2: Insertar elementos UNO POR UNO para identificar el problema
        System.out.println("2. PRUEBA DE INSERCIÓN PASO A PASO:");
        int[] valores = {10, 20, 5, 6, 12, 30, 7, 17, 26, 34, 3, 8, 19, 22, 40};

        for (int i = 0; i < valores.length; i++) {
            int valor = valores[i];
            System.out.println("--- Insertando valor: " + valor + " ---");

            try {
                btree.insert(valor);
                System.out.println("✓ Inserción exitosa de " + valor);

                // Mostrar estado del árbol después de cada inserción
                System.out.println("Estado del árbol:");
                System.out.println(btree.toString());

                // Verificar si podemos buscar el elemento recién insertado
                System.out.println("Verificando búsqueda del elemento recién insertado:");
                boolean encontrado = buscarSeguro(btree, valor);
                System.out.println("Buscar " + valor + ": " + (encontrado ? "ENCONTRADO" : "NO ENCONTRADO"));

            } catch (Exception e) {
                System.err.println("✗ ERROR al insertar " + valor + ": " + e.getMessage());
                e.printStackTrace();
                System.out.println("Deteniendo pruebas debido al error.");
                return; // Salir si hay error
            }

            System.out.println(); // Línea en blanco entre inserciones

            // Pausa para poder ver el progreso
            if (i < 5) { // Solo para las primeras 5 inserciones
                System.out.println("Presiona Enter para continuar...");
                try {
                    System.in.read();
                } catch (Exception e) {
                    // Ignorar
                }
            }
        }

        // Solo continuar si llegamos hasta aquí sin errores
        System.out.println("3. PRUEBA DE BÚSQUEDA SEGURA:");
        int[] buscar = {10, 15, 20, 35, 3};

        for (int valor : buscar) {
            boolean encontrado = buscarSeguro(btree, valor);
            System.out.println("Buscar " + valor + ": " + (encontrado ? "ENCONTRADO" : "NO ENCONTRADO"));
        }
        System.out.println();

        System.out.println("=== PRUEBAS COMPLETADAS ===");
    }

    // Método de búsqueda segura que maneja excepciones
    private static boolean buscarSeguro(BTree<Integer> btree, Integer valor) {
        try {
            return btree.search(valor);
        } catch (Exception e) {
            System.err.println("Error en búsqueda de " + valor + ": " + e.getMessage());
            return false;
        }
    }

    // Método para hacer pruebas muy básicas con pocos elementos
    public static void pruebaMinima() {
        System.out.println("\n=== PRUEBA MÍNIMA ===");

        BTree<Integer> btree = new BTree<>(3);

        System.out.println("Insertando solo 3 elementos para probar división de nodo:");

        try {
            System.out.println("Insertando 10...");
            btree.insert(10);
            System.out.println("Estado: " + btree.toString());

            System.out.println("Insertando 20...");
            btree.insert(20);
            System.out.println("Estado: " + btree.toString());

            System.out.println("Insertando 30...");
            btree.insert(30);
            System.out.println("Estado: " + btree.toString());

            System.out.println("Insertando 5...");
            btree.insert(5);
            System.out.println("Estado: " + btree.toString());

        } catch (Exception e) {
            System.err.println("Error en prueba mínima: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método para probar solo con orden más alto
    public static void pruebaOrdenAlto() {
        System.out.println("\n=== PRUEBA CON ORDEN 5 ===");

        BTree<Integer> btree = new BTree<>(5);

        try {
            int[] valores = {10, 20, 30, 40, 50, 60, 70};

            for (int valor : valores) {
                System.out.println("Insertando " + valor + "...");
                btree.insert(valor);
                System.out.println("Estado: " + btree.toString());
            }

        } catch (Exception e) {
            System.err.println("Error en prueba de orden alto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método para debuggear el problema específico
    public static void debugInsercion() {
        System.out.println("\n=== DEBUG DE INSERCIÓN ===");

        BTree<Integer> btree = new BTree<>(3);

        System.out.println("Probando inserción muy simple...");

        try {
            // Solo insertar dos elementos
            System.out.println("1. Insertando 10...");
            btree.insert(10);
            System.out.println("Árbol después de insertar 10:");
            System.out.println(btree.toString());

            System.out.println("2. Intentando buscar 10...");
            boolean found = buscarSeguro(btree, 10);
            System.out.println("¿Se encontró 10? " + found);

            System.out.println("3. Insertando 5...");
            btree.insert(5);
            System.out.println("Árbol después de insertar 5:");
            System.out.println(btree.toString());

        } catch (Exception e) {
            System.err.println("Error en debug: " + e.getMessage());
            e.printStackTrace();
        }
    }
}