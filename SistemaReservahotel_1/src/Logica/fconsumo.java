package Logica;

import Datos.vconsumo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.table.DefaultTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class fconsumo {

    private conexion mysql = new conexion();
    private Connection cn = mysql.conectar();
    private String sSQL = "";
    public Integer totalregistros;
    public Double totalconsumo;

    private static final Logger logger = LoggerFactory.getLogger(fconsumo.class);

    public DefaultTableModel mostrar(String buscar) {
        DefaultTableModel modelo;

        String[] titulos = {"ID", "idreserva", "idproducto", "producto", "cantidad", "Precio Venta", "Estado"};
        String[] registro = new String[7];

        totalregistros = 0;
        totalconsumo = 0.0;
        modelo = new DefaultTableModel(null, titulos);

        sSQL = "select c.idconsumo,c.idreserva,c.idproducto,p.nombre,c.cantidad,c.precio_venta, c.estado "
                + "from consumo c inner join producto p on c.idproducto=p.idproducto "
                + "where c.idreserva =" + buscar + " order by c.idconsumo desc";

        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);

            while (rs.next()) {
                registro[0] = rs.getString("idconsumo");
                registro[1] = rs.getString("idreserva");
                registro[2] = rs.getString("idproducto");
                registro[3] = rs.getString("nombre");
                registro[4] = rs.getString("cantidad");
                registro[5] = rs.getString("precio_venta");
                registro[6] = rs.getString("estado");

                totalregistros++;
                totalconsumo += rs.getDouble("cantidad") * rs.getDouble("precio_venta");

                modelo.addRow(registro);
            }

            return modelo;

        } catch (Exception e) {
            logger.error("Error al mostrar consumos de la reserva {}: {}", buscar, e.getMessage(), e);
            return null;
        }
    }

    public boolean insertar(vconsumo dts) {
        sSQL = "insert into consumo (idreserva,idproducto,cantidad,precio_venta,estado) values (?,?,?,?,?)";
        try {
            PreparedStatement pst = cn.prepareStatement(sSQL);
            pst.setInt(1, dts.getIdreserva());
            pst.setInt(2, dts.getIdproducto());
            pst.setDouble(3, dts.getCantidad());
            pst.setDouble(4, dts.getPrecio_venta());
            pst.setString(5, dts.getEstado());

            int n = pst.executeUpdate();

            return n != 0;

        } catch (Exception e) {
            logger.error("Error al insertar consumo: {}", dts, e);
            return false;
        }
    }

    public boolean editar(vconsumo dts) {
        sSQL = "update consumo set idreserva=?,idproducto=?,cantidad=?,precio_venta=?,estado=? where idconsumo=?";
        try {
            PreparedStatement pst = cn.prepareStatement(sSQL);
            pst.setInt(1, dts.getIdreserva());
            pst.setInt(2, dts.getIdproducto());
            pst.setDouble(3, dts.getCantidad());
            pst.setDouble(4, dts.getPrecio_venta());
            pst.setString(5, dts.getEstado());
            pst.setInt(6, dts.getIdconsumo());

            int n = pst.executeUpdate();

            return n != 0;

        } catch (Exception e) {
            logger.error("Error al editar consumo con ID {}: {}", dts.getIdconsumo(), e.getMessage(), e);
            return false;
        }
    }

    public boolean eliminar(vconsumo dts) {
        sSQL = "delete from consumo where idconsumo=?";
        try {
            PreparedStatement pst = cn.prepareStatement(sSQL);
            pst.setInt(1, dts.getIdconsumo());

            int n = pst.executeUpdate();

            return n != 0;

        } catch (Exception e) {
            logger.error("Error al eliminar consumo con ID {}: {}", dts.getIdconsumo(), e.getMessage(), e);
            return false;
        }
    }
}
