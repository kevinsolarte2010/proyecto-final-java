package com.mycompany.mavenproject3.dao;

import com.mycompany.mavenproject3.db.DatabaseConfig;
import com.mycompany.mavenproject3.model.Producto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO = Data Access Object para la tabla de productos.
 * Único responsable de ejecutar el SQL contra la base de datos.
 */
public class ProductoDAO {

    // ---------------------------------------------------------------
    // SELECT: traer todos los productos
    // ---------------------------------------------------------------
    public List<Producto> listar() throws SQLException {
        List<Producto> lista = new ArrayList<>();

        String sql = "SELECT id, nombre, precio, cantidad FROM productos ORDER BY id";

        try (Connection con = DatabaseConfig.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // Usamos el constructor original y luego le asignamos el ID
                Producto p = new Producto(
                    rs.getString("nombre"),
                    rs.getDouble("precio"),
                    rs.getInt("cantidad")
                        
                        
                );
                p.setId(rs.getInt("id")); // Guardamos el ID de la BD en el objeto
                
                lista.add(p);
            }
        }

        return lista;
    }

    // ---------------------------------------------------------------
    // INSERT: guardar un nuevo producto
    // ---------------------------------------------------------------
    public int insertar(Producto producto) throws SQLException {
        String sql = "INSERT INTO productos (nombre, precio, cantidad) VALUES (?, ?, ?)";

        try (Connection con = DatabaseConfig.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, producto.getNombre());
            ps.setDouble(2, producto.getPrecio());
            ps.setInt(3, producto.getCantidad());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }

        return -1;
    }

    // ---------------------------------------------------------------
    // UPDATE: modificar un producto existente por su id
    // ---------------------------------------------------------------
    public boolean actualizar(int id, Producto producto) throws SQLException {
        String sql = "UPDATE productos SET nombre = ?, precio = ?, cantidad = ? WHERE id = ?";

        try (Connection con = DatabaseConfig.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, producto.getNombre());
            ps.setDouble(2, producto.getPrecio());
            ps.setInt(3, producto.getCantidad());
            ps.setInt(4, id);

            return ps.executeUpdate() > 0;
        }
    }

    // ---------------------------------------------------------------
    // DELETE: eliminar un producto por su id
    // ---------------------------------------------------------------
    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM productos WHERE id = ?";

        try (Connection con = DatabaseConfig.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}