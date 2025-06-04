package Logica;

import Datos.vproducto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.table.DefaultTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class fproducto {
   
   private static final Logger logger = LoggerFactory.getLogger(fproducto.class);
   
   private conexion mysql = new conexion();
   private Connection cn = mysql.conectar();
   private String sSQL = "";
   public Integer totalregistros;

   public DefaultTableModel mostrar(String buscar){
       DefaultTableModel modelo;
       String[] titulos = {"ID", "Producto", "Descripción", "Unidad Medida", "Precio Venta"};
       String[] registro = new String[5];
       totalregistros = 0;
       modelo = new DefaultTableModel(null, titulos);
       
       sSQL = "select * from producto where nombre like ? order by idproducto desc";
       
       try {
           PreparedStatement pst = cn.prepareStatement(sSQL);
           pst.setString(1, "%" + buscar + "%");
           ResultSet rs = pst.executeQuery();
           
           while(rs.next()){
               registro[0] = rs.getString("idproducto");
               registro[1] = rs.getString("nombre");
               registro[2] = rs.getString("descripcion");
               registro[3] = rs.getString("unidad_medida");
               registro[4] = rs.getString("precio_venta");
               totalregistros++;
               modelo.addRow(registro);
           }
           logger.info("Mostrados {} registros que contienen '{}'", totalregistros, buscar);
           return modelo;
           
       } catch (Exception e) {
           logger.error("Error en mostrar productos", e);
           return null;
       }
   }

   public boolean insertar(vproducto dts){
       sSQL = "insert into producto (nombre, descripcion, unidad_medida, precio_venta) values (?, ?, ?, ?)";
       try {
           PreparedStatement pst = cn.prepareStatement(sSQL);
           pst.setString(1, dts.getNombre());
           pst.setString(2, dts.getDescripcion());
           pst.setString(3, dts.getUnidad_medida());
           pst.setDouble(4, dts.getPrecio_venta());
           int n = pst.executeUpdate();
           if(n != 0) {
               logger.info("Producto insertado: {}", dts.getNombre());
               return true;
           } else {
               logger.warn("No se pudo insertar producto: {}", dts.getNombre());
               return false;
           }
       } catch (Exception e) {
           logger.error("Error al insertar producto", e);
           return false;
       }
   }

   public boolean editar(vproducto dts){
       sSQL = "update producto set nombre=?, descripcion=?, unidad_medida=?, precio_venta=? where idproducto=?";
       try {
           PreparedStatement pst = cn.prepareStatement(sSQL);
           pst.setString(1, dts.getNombre());
           pst.setString(2, dts.getDescripcion());
           pst.setString(3, dts.getUnidad_medida());
           pst.setDouble(4, dts.getPrecio_venta());
           pst.setInt(5, dts.getIdproducto());
           int n = pst.executeUpdate();
           if(n != 0) {
               logger.info("Producto editado: ID {}", dts.getIdproducto());
               return true;
           } else {
               logger.warn("No se pudo editar producto: ID {}", dts.getIdproducto());
               return false;
           }
       } catch (Exception e) {
           logger.error("Error al editar producto", e);
           return false;
       }
   }

   public boolean eliminar(vproducto dts){
       sSQL = "delete from producto where idproducto=?";
       try {
           PreparedStatement pst = cn.prepareStatement(sSQL);
           pst.setInt(1, dts.getIdproducto());
           int n = pst.executeUpdate();
           if(n != 0) {
               logger.info("Producto eliminado: ID {}", dts.getIdproducto());
               return true;
           } else {
               logger.warn("No se pudo eliminar producto: ID {}", dts.getIdproducto());
               return false;
           }
       } catch (Exception e) {
           logger.error("Error al eliminar producto", e);
           return false;
       }
   }
}
