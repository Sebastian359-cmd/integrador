package Logica;

import Datos.vhabitacion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.table.DefaultTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class fhabitacion {

    private final conexion mysql = new conexion();
    private final Connection cn = mysql.conectar();
    private String sSQL = "";
    public Integer totalregistros;

    private static final Logger logger = LoggerFactory.getLogger(fhabitacion.class);

    public DefaultTableModel mostrar(String buscar) {
        DefaultTableModel modelo;

        String[] titulos = {"ID", "Número", "Piso", "Descripción", "Caracteristicas", "Precio", "Estado", "Tipo habitación"};
        String[] registro = new String[8];

        totalregistros = 0;
        modelo = new DefaultTableModel(null, titulos);

        sSQL = "select * from habitacion where piso like '%" + buscar + "%' order by idhabitacion";

        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);

            while (rs.next()) {
                registro[0] = rs.getString("idhabitacion");
                registro[1] = rs.getString("numero");
                registro[2] = rs.getString("piso");
                registro[3] = rs.getString("descripcion");
                registro[4] = rs.getString("caracteristicas");
                registro[5] = rs.getString("precio_diario");
                registro[6] = rs.getString("estado");
                registro[7] = rs.getString("tipo_habitacion");

                totalregistros++;
                modelo.addRow(registro);
            }

            logger.info("Habitaciones encontradas: {}", totalregistros);
            return modelo;

        } catch (Exception e) {
            logger.error("Error al mostrar habitaciones: {}", e.getMessage(), e);
            return null;
        }
    }

    public DefaultTableModel mostrarvista(String buscar) {
        DefaultTableModel modelo;

        String[] titulos = {"ID", "Número", "Piso", "Descripción", "Caracteristicas", "Precio", "Estado", "Tipo habitación"};
        String[] registro = new String[8];

        totalregistros = 0;
        modelo = new DefaultTableModel(null, titulos);

        sSQL = "select * from habitacion where piso like '%" + buscar + "%' and estado='Disponible' order by idhabitacion";

        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);

            while (rs.next()) {
                registro[0] = rs.getString("idhabitacion");
                registro[1] = rs.getString("numero");
                registro[2] = rs.getString("piso");
                registro[3] = rs.getString("descripcion");
                registro[4] = rs.getString("caracteristicas");
                registro[5] = rs.getString("precio_diario");
                registro[6] = rs.getString("estado");
                registro[7] = rs.getString("tipo_habitacion");

                totalregistros++;
                modelo.addRow(registro);
            }

            logger.info("Habitaciones disponibles encontradas: {}", totalregistros);
            return modelo;

        } catch (Exception e) {
            logger.error("Error al mostrar vista de habitaciones: {}", e.getMessage(), e);
            return null;
        }
    }

    public boolean insertar(vhabitacion dts) {
        sSQL = "insert into habitacion (numero,piso,descripcion,caracteristicas,precio_diario,estado,tipo_habitacion)" +
                " values (?,?,?,?,?,?,?)";
        try {
            PreparedStatement pst = cn.prepareStatement(sSQL);
            pst.setString(1, dts.getNumero());
            pst.setString(2, dts.getPiso());
            pst.setString(3, dts.getDescripcion());
            pst.setString(4, dts.getCaracteristicas());
            pst.setDouble(5, dts.getPrecio_diario());
            pst.setString(6, dts.getEstado());
            pst.setString(7, dts.getTipo_habitacion());

            int n = pst.executeUpdate();

            logger.info("Habitación insertada: {}", dts);
            return n != 0;

        } catch (Exception e) {
            logger.error("Error al insertar habitación: {}", e.getMessage(), e);
            return false;
        }
    }

    public boolean editar(vhabitacion dts) {
        sSQL = "update habitacion set numero=?,piso=?,descripcion=?,caracteristicas=?,precio_diario=?,estado=?,tipo_habitacion=?" +
                " where idhabitacion=?";

        try {
            PreparedStatement pst = cn.prepareStatement(sSQL);
            pst.setString(1, dts.getNumero());
            pst.setString(2, dts.getPiso());
            pst.setString(3, dts.getDescripcion());
            pst.setString(4, dts.getCaracteristicas());
            pst.setDouble(5, dts.getPrecio_diario());
            pst.setString(6, dts.getEstado());
            pst.setString(7, dts.getTipo_habitacion());
            pst.setInt(8, dts.getIdhabitacion());

            int n = pst.executeUpdate();

            logger.info("Habitación editada: {}", dts);
            return n != 0;

        } catch (Exception e) {
            logger.error("Error al editar habitación: {}", e.getMessage(), e);
            return false;
        }
    }

    public boolean desocupar(vhabitacion dts) {
        sSQL = "update habitacion set estado='Disponible' where idhabitacion=?";

        try {
            PreparedStatement pst = cn.prepareStatement(sSQL);
            pst.setInt(1, dts.getIdhabitacion());

            int n = pst.executeUpdate();

            logger.info("Habitación desocupada: id={}", dts.getIdhabitacion());
            return n != 0;

        } catch (Exception e) {
            logger.error("Error al desocupar habitación: {}", e.getMessage(), e);
            return false;
        }
    }

    public boolean ocupar(vhabitacion dts) {
        sSQL = "update habitacion set estado='Ocupado' where idhabitacion=?";

        try {
            PreparedStatement pst = cn.prepareStatement(sSQL);
            pst.setInt(1, dts.getIdhabitacion());

            int n = pst.executeUpdate();

            logger.info("Habitación ocupada: id={}", dts.getIdhabitacion());
            return n != 0;

        } catch (Exception e) {
            logger.error("Error al ocupar habitación: {}", e.getMessage(), e);
            return false;
        }
    }

    public boolean eliminar(vhabitacion dts) {
        sSQL = "delete from habitacion where idhabitacion=?";

        try {
            PreparedStatement pst = cn.prepareStatement(sSQL);
            pst.setInt(1, dts.getIdhabitacion());

            int n = pst.executeUpdate();

            logger.info("Habitación eliminada: id={}", dts.getIdhabitacion());
            return n != 0;

        } catch (Exception e) {
            logger.error("Error al eliminar habitación: {}", e.getMessage(), e);
            return false;
        }
    }
}
