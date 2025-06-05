package Logica;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class conexion {
    
    private static final Logger logger = LoggerFactory.getLogger(conexion.class);

    public String db = "basereserva";
    public String url = "jdbc:mysql://192.168.100.48/" + db + "?useSSL=false&allowPublicKeyRetrieval=true";
    public String user = "usuario";
    public String pass = "usuario123";

    public conexion() {
    }
    
    public Connection conectar() {
        Connection link = null;
        
        try {
             Class.forName("org.gjt.mm.mysql.Driver");
            link = DriverManager.getConnection(this.url, this.user, this.pass);
            logger.info("Conexión establecida exitosamente con la base de datos '{}'.", db);
        } catch (ClassNotFoundException e) {
            logger.error("No se encontró el driver JDBC para MySQL.", e);
        } catch (SQLException e) {
            logger.error("Error al conectar con la base de datos '{}'.", db, e);
        }
        
        return link;
    }
}


