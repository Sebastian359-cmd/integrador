package Logica;

import Datos.vhabitacion;
import Datos.vproducto;
import Datos.vreserva;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.table.DefaultTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class freserva {
    
   private static final Logger logger = LoggerFactory.getLogger(freserva.class);
   
   private conexion mysql = new conexion();
   private Connection cn = mysql.conectar();
   private String sSQL = "";
   public Integer totalregistros;
   
   public DefaultTableModel mostrar(String buscar){
       DefaultTableModel modelo;
       String [] titulos = {"ID","Idhabitacion","Numero","idcliente","Cliente","idtrabajador","Trabajador","Tipo Reserva","Fecha Reserva","Fecha Ingreso","Fecha Salida","Costo","Estado"};
       String [] registro = new String [13];
       totalregistros = 0;
       modelo = new DefaultTableModel(null, titulos);
       
       sSQL = "select r.idreserva,r.idhabitacion,h.numero,r.idcliente,"+
               "(select nombre from persona where idpersona=r.idcliente) as clienten,"+
               "(select apaterno from persona where idpersona=r.idcliente) as clienteap,"+
               "r.idtrabajador,(select nombre from persona where idpersona=r.idtrabajador) as trabajadorn,"+
               "(select apaterno from persona where idpersona=r.idtrabajador) as trabajadorap,"+
               "r.tipo_reserva,r.fecha_reserva,r.fecha_ingresa,r.fecha_salida,"+
               "r.costo_alojamiento,r.estado from reserva r inner join habitacion h on r.idhabitacion=h.idhabitacion where r.fecha_reserva like '%"+ buscar + "%' order by idreserva desc";
       
       try {
           Statement st = cn.createStatement();
           ResultSet rs = st.executeQuery(sSQL);
           
           while(rs.next()){
               registro[0] = rs.getString("idreserva");
               registro[1] = rs.getString("idhabitacion");
               registro[2] = rs.getString("numero");
               registro[3] = rs.getString("idcliente");
               registro[4] = rs.getString("clienten") + " " + rs.getString("clienteap");
               registro[5] = rs.getString("idtrabajador");
               registro[6] = rs.getString("trabajadorn") + " " + rs.getString("trabajadorap");
               registro[7] = rs.getString("tipo_reserva");
               registro[8] = rs.getString("fecha_reserva");
               registro[9] = rs.getString("fecha_ingresa");
               registro[10] = rs.getString("fecha_salida");
               registro[11] = rs.getString("costo_alojamiento");
               registro[12] = rs.getString("estado");
               
               totalregistros++;
               modelo.addRow(registro);
           }
           logger.info("Se mostraron {} registros con fecha de reserva que contiene '{}'", totalregistros, buscar);
           return modelo;
       } catch (Exception e) {
           logger.error("Error al mostrar reservas", e);
           return null;
       }
   } 
   
       public boolean existeReservaSolapada(int idHabitacion, Date fechaIngresoNueva, Date fechaSalidaNueva) {
    boolean existe = false;
    String sql = "SELECT COUNT(*) FROM reserva "
               + "WHERE idhabitacion = ? AND estado <> 'Cancelado' "
               + "AND ( (fecha_ingresa <= ? AND fecha_salida >= ?) "
               + "OR (fecha_ingresa <= ? AND fecha_salida >= ?) "
               + "OR (fecha_ingresa >= ? AND fecha_salida <= ?) )";

    try {
        PreparedStatement pst = cn.prepareStatement(sql);
        pst.setInt(1, idHabitacion);
        pst.setDate(2, fechaSalidaNueva);
        pst.setDate(3, fechaSalidaNueva);
        pst.setDate(4, fechaIngresoNueva);
        pst.setDate(5, fechaIngresoNueva);
        pst.setDate(6, fechaIngresoNueva);
        pst.setDate(7, fechaSalidaNueva);

        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            int count = rs.getInt(1);
            existe = count > 0;
        }
        rs.close();
        pst.close();

    } catch (Exception e) {
        logger.error("Error al verificar reserva solapada", e);
    }

    return existe;
}
       
 public boolean insertar(vreserva dts){
    // Validar que no exista una reserva solapada en la misma habitación
    if (existeReservaSolapada(dts.getIdhabitacion(), dts.getFecha_ingresa(), dts.getFecha_salida())) {
        logger.warn("Intento de insertar reserva solapada: idhabitacion {}, fechas {} - {}", 
                    dts.getIdhabitacion(), dts.getFecha_ingresa(), dts.getFecha_salida());
        return false; // Aquí puedes manejarlo para mostrar un mensaje de error en la UI
    }

    sSQL = "insert into reserva (idhabitacion,idcliente,idtrabajador,tipo_reserva,fecha_reserva,fecha_ingresa,fecha_salida,costo_alojamiento,estado) values (?,?,?,?,?,?,?,?,?)";
    try {
        PreparedStatement pst = cn.prepareStatement(sSQL);
        pst.setInt(1, dts.getIdhabitacion());
        pst.setInt(2, dts.getIdcliente());
        pst.setInt(3, dts.getIdtrabajador());
        pst.setString(4, dts.getTipo_reserva());
        pst.setDate(5, dts.getFecha_reserva());
        pst.setDate(6, dts.getFecha_ingresa());
        pst.setDate(7, dts.getFecha_salida());
        pst.setDouble(8, dts.getCosto_alojamiento());
        pst.setString(9, dts.getEstado());

        int n = pst.executeUpdate();

        if (n != 0){
            logger.info("Reserva insertada correctamente: idhabitacion {}, idcliente {}, idtrabajador {}", dts.getIdhabitacion(), dts.getIdcliente(), dts.getIdtrabajador());
            return true;
        } else {
            logger.warn("No se pudo insertar la reserva");
            return false;
        }
    } catch (Exception e) {
        logger.error("Error al insertar reserva", e);
        return false;
    }
}
   
   public boolean editar(vreserva dts){
       sSQL = "update reserva set idhabitacion=?,idcliente=?,idtrabajador=?,tipo_reserva=?,fecha_reserva=?,fecha_ingresa=?,fecha_salida=?,costo_alojamiento=?,estado=? where idreserva=?";
       try {
           PreparedStatement pst = cn.prepareStatement(sSQL);
           pst.setInt(1, dts.getIdhabitacion());
           pst.setInt(2, dts.getIdcliente());
           pst.setInt(3, dts.getIdtrabajador());
           pst.setString(4, dts.getTipo_reserva());
           pst.setDate(5, dts.getFecha_reserva());
           pst.setDate(6, dts.getFecha_ingresa());
           pst.setDate(7, dts.getFecha_salida());
           pst.setDouble(8, dts.getCosto_alojamiento());
           pst.setString(9, dts.getEstado());
           pst.setInt(10, dts.getIdreserva());
           
           int n = pst.executeUpdate();
           
           if (n != 0){
               logger.info("Reserva editada correctamente: idreserva {}", dts.getIdreserva());
               return true;
           } else {
               logger.warn("No se pudo editar la reserva idreserva {}", dts.getIdreserva());
               return false;
           }
       } catch (Exception e) {
           logger.error("Error al editar reserva", e);
           return false;
       }
   }
   
   public boolean pagar(vreserva dts){
       sSQL = "update reserva set estado='Pagada' where idreserva=?";
       try {
           PreparedStatement pst = cn.prepareStatement(sSQL);
           pst.setInt(1, dts.getIdreserva());
           
           int n = pst.executeUpdate();
           
           if (n != 0){
               logger.info("Reserva pagada: idreserva {}", dts.getIdreserva());
               return true;
           } else {
               logger.warn("No se pudo pagar la reserva idreserva {}", dts.getIdreserva());
               return false;
           }
       } catch (Exception e) {
           logger.error("Error al pagar reserva", e);
           return false;
       }
   }
   
   public boolean eliminar(vreserva dts){
       sSQL = "delete from reserva where idreserva=?";
       try {
           PreparedStatement pst = cn.prepareStatement(sSQL);
           pst.setInt(1, dts.getIdreserva());
           
           int n = pst.executeUpdate();
           
           if (n != 0){
               logger.info("Reserva eliminada: idreserva {}", dts.getIdreserva());
               return true;
           } else {
               logger.warn("No se pudo eliminar la reserva idreserva {}", dts.getIdreserva());
               return false;
           }
       } catch (Exception e) {
           logger.error("Error al eliminar reserva", e);
           return false;
       }
   }
   


}
