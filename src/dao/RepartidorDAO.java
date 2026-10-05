package dao;

import conexion.ConexionDB;
import modelo.Repartidor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para gestionar operaciones CRUD de Repartidor.
 */
public class RepartidorDAO {

    public void create(Repartidor r) throws SQLException {
        String sql = "INSERT INTO repartidores (nombre) VALUES (?)";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, r.getNombre());
            ps.executeUpdate();
        }
    }

    public List<Repartidor> readAll() throws SQLException {
        List<Repartidor> lista = new ArrayList<>();
        String sql = "SELECT * FROM repartidores ORDER BY id";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Repartidor(rs.getInt("id"), rs.getString("nombre")));
            }
        }
        return lista;
    }

    public void update(Repartidor r) throws SQLException {
        String sql = "UPDATE repartidores SET nombre=? WHERE id=?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, r.getNombre());
            ps.setInt(2, r.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM repartidores WHERE id=?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}