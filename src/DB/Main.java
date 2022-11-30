package DB;

public class Main {
    public static void main(String[] args) throws Exception {
        Conexion con=new Conexion();

        if(con.existeBebidaFria("Fanta")){
            System.out.println("sii");
        }
        //hola
        con.pedirBebidaCalientes("Cafe");
        con.desconexion();
    }
}