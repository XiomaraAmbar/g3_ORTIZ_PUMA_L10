package Ejercicios;

class TestEjercicio04 {
    public static void main(String[] args) {
        Codigos sistema = new Codigos();

        sistema.insertarEstudiante(103, "Ana");
        sistema.insertarEstudiante(110, "Luis");
        sistema.insertarEstudiante(101, "Carlos");
        sistema.insertarEstudiante(120, "Lucía");
        sistema.insertarEstudiante(115, "David");
        sistema.insertarEstudiante(125, "Jorge");
        sistema.insertarEstudiante(140, "Camila");
        sistema.insertarEstudiante(108, "Rosa");
        sistema.insertarEstudiante(132, "Ernesto");
        sistema.insertarEstudiante(128, "Denis");
        sistema.insertarEstudiante(145, "Enrique");
        sistema.insertarEstudiante(122, "Karina");
        sistema.insertarEstudiante(108, "Juan"); // Código repetido

        System.out.println("Buscar 115: " + sistema.buscarNombre(115)); // David
        System.out.println("Buscar 132: " + sistema.buscarNombre(132)); // Ernesto
        System.out.println("Buscar 999: " + sistema.buscarNombre(999)); // No encontrado

        // Insertar nuevo estudiante
        sistema.insertarEstudiante(106, "Sara");
        System.out.println("Buscar 106: " + sistema.buscarNombre(106)); // Sara
    }
}

