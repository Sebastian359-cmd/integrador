package Logica;

import Datos.vtrabajador;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.table.DefaultTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ftrabajador {

    private static final Logger logger = LoggerFactory.getLogger(ftrabajador.class);

    private conexion mysql = new conexion();
    private Connection cn = mysql.conectar();
    private String sSQL = "";
    private String sSQL2 = "";
    public Integer totalregistros;

    public DefaultTableModel mostrar(String buscar) {
        DefaultTableModel modelo;

        String[] titulos = {"ID", "Nombre", "Apaterno", "Amaterno", "Doc", "Número Documento",
            "Dirección", "Teléfono", "Email", "Sueldo", "Acceso", "Login", "Clave", "Estado"};

        String[] registro = new String[14];

        totalregistros = 0;
        modelo = new DefaultTableModel(null, titulos);

        sSQL = "select p.idpersona,p.nombre,p.apaterno,p.amaterno,p.tipo_documento,p.num_documento,"
                + "p.direccion,p.telefono,p.email,t.sueldo,t.acceso,t.login,t.password,t.estado "
                + "from persona p inner join trabajador t on p.idpersona=t.idpersona "
                + "where p.num_documento like ? order by p.idpersona desc";

        try (PreparedStatement pst = cn.prepareStatement(sSQL)) {
            pst.setString(1, "%" + buscar + "%");
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                registro[0] = rs.getString("idpersona");
                registro[1] = rs.getString("nombre");
                registro[2] = rs.getString("apaterno");
                registro[3] = rs.getString("amaterno");
                registro[4] = rs.getString("tipo_documento");
                registro[5] = rs.getString("num_documento");
                registro[6] = rs.getString("direccion");
                registro[7] = rs.getString("telefono");
                registro[8] = rs.getString("email");
                registro[9] = rs.getString("sueldo");
                registro[10] = rs.getString("acceso");
                registro[11] = rs.getString("login");
                registro[12] = rs.getString("password");
                registro[13] = rs.getString("estado");

                totalregistros++;
                modelo.addRow(registro);
            }
            return modelo;

        } catch (Exception e) {
            logger.error("Error mostrando trabajadores con filtro '{}': ", buscar, e);
            return null;
        }
    }

    public boolean insertar(vtrabajador dts) {
        sSQL = "insert into persona (nombre,apaterno,amaterno,tipo_documento,num_documento,direccion,telefono,email)"
                + " values (?,?,?,?,?,?,?,?)";
        sSQL2 = "insert into trabajador (idpersona,sueldo,acceso,login,password,estado) values (?,?,?,?,?,?)";

        try (
            PreparedStatement pst = cn.prepareStatement(sSQL, Statement.RETURN_GENERATED_KEYS);
            PreparedStatement pst2 = cn.prepareStatement(sSQL2);
        ) {
            // Insert persona
            pst.setString(1, dts.getNombre());
            pst.setString(2, dts.getApaterno());
            pst.setString(3, dts.getAmaterno());
            pst.setString(4, dts.getTipo_documento());
            pst.setString(5, dts.getNum_documento());
            pst.setString(6, dts.getDireccion());
            pst.setString(7, dts.getTelefono());
            pst.setString(8, dts.getEmail());

            int filasAfectadas = pst.executeUpdate();

            if (filasAfectadas == 0) {
                logger.warn("No se pudo insertar la persona.");
                return false;
            }

            // Obtener el idpersona generado
            ResultSet generatedKeys = pst.getGeneratedKeys();
            int idpersona = -1;
            if (generatedKeys.next()) {
                idpersona = generatedKeys.getInt(1);
            } else {
                logger.error("No se pudo obtener el ID generado para persona.");
                return false;
            }

            // Insert trabajador usando idpersona obtenido
            pst2.setInt(1, idpersona);
            pst2.setDouble(2, dts.getSueldo());
            pst2.setString(3, dts.getAcceso());
            pst2.setString(4, dts.getLogin());
            pst2.setString(5, dts.getPassword());
            pst2.setString(6, dts.getEstado());

            int filasAfectadas2 = pst2.executeUpdate();

            if (filasAfectadas2 == 0) {
                logger.warn("No se pudo insertar el trabajador.");
                return false;
            }

            return true;

        } catch (Exception e) {
            logger.error("Error al insertar trabajador: ", e);
            return false;
        }
    }

    public boolean editar(vtrabajador dts) {
        sSQL = "update persona set nombre=?,apaterno=?,amaterno=?,tipo_documento=?,num_documento=?,"
                + "direccion=?,telefono=?,email=? where idpersona=?";
        sSQL2 = "update trabajador set sueldo=?,acceso=?,login=?,password=?,estado=? where idpersona=?";

        try (
            PreparedStatement pst = cn.prepareStatement(sSQL);
            PreparedStatement pst2 = cn.prepareStatement(sSQL2);
        ) {
            pst.setString(1, dts.getNombre());
            pst.setString(2, dts.getApaterno());
            pst.setString(3, dts.getAmaterno());
            pst.setString(4, dts.getTipo_documento());
            pst.setString(5, dts.getNum_documento());
            pst.setString(6, dts.getDireccion());
            pst.setString(7, dts.getTelefono());
            pst.setString(8, dts.getEmail());
            pst.setInt(9, dts.getIdpersona());

            pst2.setDouble(1, dts.getSueldo());
            pst2.setString(2, dts.getAcceso());
            pst2.setString(3, dts.getLogin());
            pst2.setString(4, dts.getPassword());
            pst2.setString(5, dts.getEstado());
            pst2.setInt(6, dts.getIdpersona());

            int filas1 = pst.executeUpdate();
            int filas2 = pst2.executeUpdate();

            if (filas1 != 0 && filas2 != 0) {
                return true;
            } else {
                logger.warn("No se pudo actualizar trabajador o persona.");
                return false;
            }

        } catch (Exception e) {
            logger.error("Error al editar trabajador: ", e);
            return false;
        }
    }

    public boolean eliminar(vtrabajador dts) {
        sSQL = "delete from trabajador where idpersona=?";
        sSQL2 = "delete from persona where idpersona=?";

        try (
            PreparedStatement pst = cn.prepareStatement(sSQL);
            PreparedStatement pst2 = cn.prepareStatement(sSQL2);
        ) {
            pst.setInt(1, dts.getIdpersona());
            int filas1 = pst.executeUpdate();

            if (filas1 == 0) {
                logger.warn("No se pudo eliminar trabajador con idpersona: {}", dts.getIdpersona());
                return false;
            }

            pst2.setInt(1, dts.getIdpersona());
            int filas2 = pst2.executeUpdate();

            if (filas2 == 0) {
                logger.warn("No se pudo eliminar persona con idpersona: {}", dts.getIdpersona());
                return false;
            }

            return true;

        } catch (Exception e) {
            logger.error("Error al eliminar trabajador: ", e);
            return false;
        }
    }

    public DefaultTableModel login(String login, String password) {
        DefaultTableModel modelo;

        String[] titulos = {"ID", "Nombre", "Apaterno", "Amaterno", "Acceso", "Login", "Clave", "Estado"};

        String[] registro = new String[8];

        totalregistros = 0;
        modelo = new DefaultTableModel(null, titulos);

        sSQL = "select p.idpersona,p.nombre,p.apaterno,p.amaterno,"
                + "t.acceso,t.login,t.password,t.estado from persona p inner join trabajador t "
                + "on p.idpersona=t.idpersona where t.login=? and t.password=? and t.estado='A'";

        try (PreparedStatement pst = cn.prepareStatement(sSQL)) {
            pst.setString(1, login);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                registro[0] = rs.getString("idpersona");
                registro[1] = rs.getString("nombre");
                registro[2] = rs.getString("apaterno");
                registro[3] = rs.getString("amaterno");

                registro[4] = rs.getString("acceso");
                registro[5] = rs.getString("login");
                registro[6] = rs.getString("password");
                registro[7] = rs.getString("estado");

                totalregistros++;
                modelo.addRow(registro);
            }
            return modelo;

        } catch (Exception e) {
            logger.error("Error en login con usuario '{}': ", login, e);
            return null;
        }
    }
}
