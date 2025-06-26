package Ejercicios;

public class TestEjercicio3 {

    public static void main(String[] args) {
        try {
            BTree<Integer> arbol = BTree.building_Btree("arbolB.txt");
            System.out.println("Árbol construido correctamente:\n");
            System.out.println(arbol);
        } catch (Exception e) {
            System.err.println("Error al construir el árbol: " + e.getMessage());
        }
    }
}

