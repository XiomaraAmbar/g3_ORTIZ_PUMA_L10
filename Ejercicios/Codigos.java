package Ejercicios;

public class Codigos {
    private BTree<RegistroEstudiante> arbol;

    public Codigos() {
        arbol = new BTree<>(4); // Árbol de orden 4
    }

    public void insertarEstudiante(int codigo, String nombre) {
        arbol.insert(new RegistroEstudiante(codigo, nombre));
    }

    public String buscarNombre(int codigo) {
        return buscarNombreRecursivo(arbol.getRaiz(), codigo);
    }

    private String buscarNombreRecursivo(BNode<RegistroEstudiante> nodo, int codigo) {
        if (nodo == null) return "No encontrado";

        for (int i = 0; i < nodo.getContadorClaves(); i++) {
            RegistroEstudiante est = nodo.getKey(i);
            if (est.getCodigo() == codigo) {
                return est.getNombre();
            } else if (codigo < est.getCodigo()) {
                return buscarNombreRecursivo(nodo.getChild(i), codigo);
            }
        }

        return buscarNombreRecursivo(nodo.getChild(nodo.getContadorClaves()), codigo);
    }

    public BTree<RegistroEstudiante> getArbol() {
        return arbol;
    }
}
