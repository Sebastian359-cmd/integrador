package Logica;

import Datos.vcliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.table.DefaultTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class fcliente {

    private static final Logger logger = LoggerFactory.getLogger(fcliente.class);
    private conexion mysql = new conexion();
    private Connection cn = mysql.conectar();
    private String sSQL = "";
    private String sSQL2 = "";
    public Integer totalregistros;

    public DefaultTableModel mostrar(String buscar) {
        DefaultTableModel modelo;

        String[] titulos = {"ID", "Nombre", "Apaterno", "Amaterno", "Doc", "Númeo Documento", "Dirección", "Teléfono", "Email", "Código"};

        String[] registro = new String[10];
        totalregistros = 0;
        modelo = new DefaultTableModel(null, titulos);

        sSQL = "SELECT p.idpersona, p.nombre, p.apaterno, p.amaterno, p.tipo_documento, p.num_documento,"
             + " p.direccion, p.telefono, p.email, c.codigo_cliente"
             + " FROM persona p INNER JOIN cliente c ON p.idpersona = c.idpersona"
             + " WHERE num_documento LIKE '%" + buscar + "%'"
             + " ORDER BY idpersona DESC";

        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);

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
                registro[9] = rs.getString("codigo_cliente");

                totalregistros++;
                modelo.addRow(registro);
            }
            return modelo;

        } catch (Exception e) {
            logger.error("Error al mostrar clientes con filtro '{}'", buscar, e);
            return null;
        }
    }


  public boolean insertar(vcliente dts) {
    sSQL = "insert into persona (nombre,apaterno,amaterno,tipo_documento,num_documento,direccion,telefono,email)"
            + " values (?,?,?,?,?,?,?,?)";
    sSQL2 = "insert into cliente (idpersona,codigo_cliente)"
            + " values ((select idpersona from persona order by idpersona desc limit 1),?)";

    try {
        PreparedStatement pst = cn.prepareStatement(sSQL);
        PreparedStatement pst2 = cn.prepareStatement(sSQL2);

        pst.setString(1, dts.getNombre());
        pst.setString(2, dts.getApaterno());
        pst.setString(3, dts.getAmaterno());
        pst.setString(4, dts.getTipo_documento());
        pst.setString(5, dts.getNum_documento());
        pst.setString(6, dts.getDireccion());
        pst.setString(7, dts.getTelefono());
        pst.setString(8, dts.getEmail());

        pst2.setString(1, dts.getCodigo_cliente());

        int n = pst.executeUpdate();

        if (n > 0 && pst2.executeUpdate() > 0) {
            logger.info("Cliente insertado correctamente: {}", dts.getCodigo_cliente());
            return true;
        } else {
            logger.warn("No se pudo insertar el cliente: {}", dts.getCodigo_cliente());
            return false;
        }

    } catch (Exception e) {
        logger.error("Error al insertar cliente: {}", dts.getCodigo_cliente(), e);
        return false;
    }
}

    public boolean editar(vcliente dts) {
        sSQL = "update persona set nombre=?,apaterno=?,amaterno=?,tipo_documento=?,num_documento=?,"
                + " direccion=?,telefono=?,email=? where idpersona=?";
        
        sSQL2 = "update cliente set codigo_cliente=?"
                + " where idpersona=?";
        try {

            PreparedStatement pst = cn.prepareStatement(sSQL);
            PreparedStatement pst2 = cn.prepareStatement(sSQL2);

            pst.setString(1, dts.getNombre());
            pst.setString(2, dts.getApaterno());
            pst.setString(3, dts.getAmaterno());
            pst.setString(4, dts.getTipo_documento());
            pst.setString(5, dts.getNum_documento());
            pst.setString(6, dts.getDireccion());
            pst.setString(7, dts.getTelefono());
            pst.setString(8, dts.getEmail());
            pst.setInt(9, dts.getIdpersona());

            pst2.setString(1, dts.getCodigo_cliente());
            pst2.setInt(2, dts.getIdpersona());

            int n = pst.executeUpdate();

            if (n != 0) {
                int n2 = pst2.executeUpdate();

                if (n2 != 0) {
                    return true;

                } else {
                    return false;
                }

            } else {
                return false;
            }

        } catch (Exception e) {
        logger.error("Error al editar cliente", e);
            return false;
        }
    }

    public boolean eliminar(vcliente dts) {
        sSQL = "delete from cliente where idpersona=?";
        sSQL2 = "delete from persona where idpersona=?";

        try {

            PreparedStatement pst = cn.prepareStatement(sSQL);
            PreparedStatement pst2 = cn.prepareStatement(sSQL2);

            
            pst.setInt(1, dts.getIdpersona());

            
            pst2.setInt(1, dts.getIdpersona());

            int n = pst.executeUpdate();

            if (n != 0) {
                int n2 = pst2.executeUpdate();

                if (n2 != 0) {
                    return true;

                } else {
                    return false;
                }

            } else {
                return false;
            }

        } catch (Exception e) {
            logger.error("Error al eliminar cliente con ID {}", dts.getIdpersona(), e);
            return false;
        }
    }
}
