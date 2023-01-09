package DB;

import java.sql.*;
public class CafeDB {
    private Connection conn = null;
    private PreparedStatement ps = null;

    //contructor que establece la conexion con la base de datos.
    public CafeDB() throws Exception {
        String bd = "sql7589280";
        String url = "jdbc:mysql://sql7.freesqldatabase.com:3306/";
        String user = "sql7589280";
        String password = "BK5YL3KlhN";
        String driver = "com.mysql.cj.jdbc.Driver";

        Class.forName(driver);
        conn = DriverManager.getConnection(url+bd,user,password);
        System.out.println("Conexion exitosa");
    }
    /**
     Implementa la desconexión con el servidor
     @throws SQLException si ocurre cualquier anormalidad
     */
    public void desconexion() throws SQLException {
        try{
            conn.close();
            System.out.println("Desconexion realizada con exito");
        }
        catch(SQLException e){
            System.out.println("ERROR EN LA DESCONEXION"+e.getMessage());
        }
    }

    public boolean existeBebidaFria(String nombre) throws SQLException {
        ps=conn.prepareStatement("select Nombre from dbo.BEBIDAS_FRIAS where Nombre=? and stock>0");
        ps.setString(1, nombre);
        ResultSet res=ps.executeQuery();
        boolean existe=res.next();
        ps.close();
        return existe;
    }

    public boolean existeBebidaCaliente(String nombre) throws SQLException {
        ps=conn.prepareStatement("select Nombre from dbo.BEBIDAS_CALIENTES where Nombre=? and stock>0");
        ps.setString(1, nombre);
        ResultSet res=ps.executeQuery();
        boolean existe=res.next();
        ps.close();
        return existe;
    }

    public boolean pedirBebidaFria(String nombre) throws SQLException {
        if (!existeBebidaFria(nombre)){
            System.out.println("No hay existencias");
            return false;
        }else{
            ps=conn.prepareStatement("UPDATE dbo.BEBIDAS_FRIAS SET stock = stock - 1 where Nombre = ?");
            ps.setString(1,nombre);
            ps.executeUpdate();
            ps.close();
        }
        return true;
    }

    public boolean pedirBebidaCalientes(String nombre) throws SQLException {
        if (!existeBebidaCaliente(nombre)){
            System.out.println("No hay existencias");
            return false;
        }else{
            ps=conn.prepareStatement("UPDATE dbo.BEBIDAS_CALIENTES SET stock = stock - 1 where Nombre = ?");
            ps.setString(1,nombre);
            ps.executeUpdate();
            ps.close();
        }
        return true;
    }

    public boolean realizarConsulta(String sqlQuery) throws SQLException {
        ps=conn.prepareStatement(sqlQuery);
        ResultSet res=ps.executeQuery();
        String []consulta= sqlQuery.split(" ");
        String nombreTa=consulta[3];
        String nombreBebida=consulta[7];
        boolean existe=res.next();
        ps.close();

        if(existe){
            ps=conn.prepareStatement("UPDATE" +nombreTa+ "SET stock = stock - 1" + "where Nombre="+nombreBebida);
            ps.executeUpdate();
            ps.close();
            return true;
        }


        return false;
    }
    /**
     Devuelve la propiedad Connection
     @return conn
     @throws SQLException si ocurre cualquier anormalidad
     */
    public Connection getConexionOracle() throws SQLException {
        return conn;
    }
    /**
     *  Inicia una transacción
     *@throws SQLException si ocurre cualquier anormalidad
     */
    public void inicioTransaccion() throws SQLException {
        conn.setAutoCommit(false);
    }
    /**
     *  Finaliza una transacción con commint
     *@throws SQLException si ocurre cualquier anormalidad
     */
    public void finTransaccionCommit() throws SQLException {
        conn.commit();
        conn.setAutoCommit(true);
    }
    /**
     *  Finaliza una transacción con rollback
     @throws SQLException si ocurre cualquier anormalidad
     */
    public void finTransaccionRollback() throws SQLException {
        conn.rollback();
        conn.setAutoCommit(true);
    }

    Statement createStatement() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
/*
    CallableStatement prepareCall(String sql) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
*/
}