package btreeFinal;

import btreeArrayList.BTree;

public class BTreeTest {

    public static void main(String[] args) {
        System.out.println("=== PRUEBAS DEL B-TREE ===\n");

        // Crear un B-Tree de orden 3
        btreeArrayList.BTree<Integer> btree = new btreeArrayList.BTree<>(3);

        // Prueba 1: Inserción básica
        System.out.println("1. PRUEBA DE INSERCIÓN BÁSICA");
        System.out.println("Insertando: 10, 20, 5, 6, 12, 30, 7, 17");
        btree.insert(10);
        btree.insert(20);
        btree.insert(5);
        btree.insert(6);
        btree.insert(12);
        btree.insert(30);
        btree.insert(7);
        btree.insert(17);

        System.out.println("\nEstructura del árbol después de inserción:");
        System.out.println(btree);

        // Prueba 2: Búsqueda
        System.out.println("\n2. PRUEBA DE BÚSQUEDA");
        testSearch(btree, 10);  // Debe encontrarse
        testSearch(btree, 25);  // No debe encontrarse
        testSearch(btree, 5);   // Debe encontrarse
        testSearch(btree, 100); // No debe encontrarse

        // Prueba 3: Inserción con duplicados
        System.out.println("\n3. PRUEBA DE DUPLICADOS");
        System.out.println("Intentando insertar 10 (duplicado):");
        btree.insert(10);

        // Prueba 4: Más inserciones para forzar divisiones
        System.out.println("\n4. PRUEBA DE DIVISIONES DE NODOS");
        System.out.println("Insertando más elementos: 1, 2, 3, 4, 8, 9, 11, 13, 14, 15");
        btree.insert(1);
        btree.insert(2);
        btree.insert(3);
        btree.insert(4);
        btree.insert(8);
        btree.insert(9);
        btree.insert(11);
        btree.insert(13);
        btree.insert(14);
        btree.insert(15);

        System.out.println("\nEstructura del árbol después de más inserciones:");
        System.out.println(btree);

        // Prueba 5: Eliminación de nodos hoja
        System.out.println("\n5. PRUEBA DE ELIMINACIÓN - NODOS HOJA");
        System.out.println("Eliminando 1 (nodo hoja):");
        btree.remove(1);
        System.out.println(btree);

        System.out.println("Eliminando 15 (nodo hoja):");
        btree.remove(15);
        System.out.println(btree);

        // Prueba 6: Eliminación de nodos internos
        System.out.println("\n6. PRUEBA DE ELIMINACIÓN - NODOS INTERNOS");
        System.out.println("Eliminando 10 (nodo interno - debe reemplazarse con sucesor):");
        btree.remove(10);
        System.out.println(btree);

        // Prueba 7: Eliminación que causa underflow y préstamo
        System.out.println("\n7. PRUEBA DE PRÉSTAMO DE HERMANOS");
        System.out.println("Eliminando elementos para provocar préstamos:");
        btree.remove(2);
        System.out.println("Después de eliminar 2:");
        System.out.println(btree);

        btree.remove(3);
        System.out.println("Después de eliminar 3:");
        System.out.println(btree);

        // Prueba 8: Eliminación que causa fusión
        System.out.println("\n8. PRUEBA DE FUSIÓN DE NODOS");
        System.out.println("Eliminando más elementos para provocar fusiones:");
        btree.remove(4);
        btree.remove(5);
        btree.remove(6);
        System.out.println("Después de eliminar 4, 5, 6:");
        System.out.println(btree);

        // Prueba 9: Eliminación de elementos inexistentes
        System.out.println("\n9. PRUEBA DE ELIMINACIÓN - ELEMENTOS INEXISTENTES");
        System.out.println("Intentando eliminar 100 (no existe):");
        btree.remove(100);

        // Prueba 10: Vaciado del árbol
        System.out.println("\n10. PRUEBA DE VACIADO DEL ÁRBOL");
        System.out.println("Eliminando todos los elementos restantes:");
        btree.remove(7);
        btree.remove(8);
        btree.remove(9);
        btree.remove(11);
        btree.remove(12);
        btree.remove(13);
        btree.remove(14);
        btree.remove(17);
        btree.remove(20);
        btree.remove(30);

        System.out.println("Árbol después de eliminar todos los elementos:");
        System.out.println(btree);

        // Prueba 11: Operaciones en árbol vacío
        System.out.println("\n11. PRUEBA DE OPERACIONES EN ÁRBOL VACÍO");
        testSearch(btree, 10);
        System.out.println("Intentando eliminar de árbol vacío:");
        btree.remove(10);

        // Prueba 12: Reconstrucción del árbol
        System.out.println("\n12. PRUEBA DE RECONSTRUCCIÓN");
        System.out.println("Reconstruyendo el árbol con nuevos elementos:");
        for (int i = 50; i <= 70; i += 2) {
            btree.insert(i);
        }
        System.out.println("\nÁrbol reconstruido:");
        System.out.println(btree);

        System.out.println("\n=== PRUEBAS COMPLETADAS ===");
    }

    private static void testSearch(BTree<Integer> btree, Integer key) {
        System.out.println("Buscando " + key + ": " +
                (btree.search(key) ? "ENCONTRADO" : "NO ENCONTRADO"));
    }
}