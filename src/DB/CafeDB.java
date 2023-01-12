package DB;

import java.sql.*;
public class CafeDB {
    private Connection conn = null;
    private PreparedStatement ps = null;

    //contructor que establece la conexion con la base de datos.
    public CafeDB() throws Exception {
        try{
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String url="jdbc:sqlserver://servidoriia.database.windows.net:1433;database=BDIIA;user=admin12@servidoriia;password=universidaddeHuelva12_;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";
            conn = DriverManager.getConnection(url);
            System.out.println("Conexion realizada");
        }
        catch(SQLException e){
            System.out.println("ERROR EN LA CONEXION "+e.getMessage());
            //JOptionPane.showMessageDialog(null,"No se ha podido establecer la conexión correctamene","ERROR",JOptionPane.ERROR, new ImageIcon("src/Aplicacion/img/desconexion.png"));
        }
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

    public boolean realizarConsulta(String sqlQuery) throws SQLException {
        ps=conn.prepareStatement(sqlQuery);
        ResultSet res=ps.executeQuery();
        String []consulta= sqlQuery.split(" ");
        String nombreTa=consulta[3];
        String nombreBebida=consulta[7];
        boolean existe=res.next();
        ps.close();

        if(existe){
            System.out.println("Se ha encontrado " + nombreBebida + " en el almacén.");
            ps=conn.prepareStatement("UPDATE " +nombreTa+ " SET stock = stock - 1" + " where Nombre="+nombreBebida);
            ps.executeUpdate();
            ps.close();
            return true;
        }
        else{
            System.out.println("No se ha encontrado " + nombreBebida + " en el almacén.");
        }


        return false;
    }

    /**
     Devuelve la propiedad Connection
     @return conn
     @throws SQLException si ocurre cualquier anormalidad
     **/

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