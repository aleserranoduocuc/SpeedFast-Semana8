package dao;

import conexion.ConexionDB;
import modelo.Entrega;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para gestionar operaciones CRUD de Entrega.
 */
public class EntregaDAO {

    public void create(Entrega e) throws SQLException {
        String sql = "INSERT INTO entregas (id_pedido, id_repartidor, fecha, hora) VALUES (?,?,?,?)";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, e.getIdPedido());
            ps.setInt(2, e.getIdRepartidor());
            ps.setDate(3, e.getFecha());
            ps.setTime(4, e.getHora());
            ps.executeUpdate();
        }
    }

    public List<Entrega> readAll() throws SQLException {
        List<Entrega> lista = new ArrayList<>();
        String sql = "SELECT * FROM entregas ORDER BY id";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Entrega(
                        rs.getInt("id"),
                        rs.getInt("id_pedido"),
                        rs.getInt("id_repartidor"),
                        rs.getDate("fecha"),
                        rs.getTime("hora")));
            }
        }
        return lista;
    }

    public List<Entrega> readByPedido(int idPedido) throws SQLException {
        return readByFiltro("id_pedido", idPedido);
    }

    public List<Entrega> readByRepartidor(int idRepartidor) throws SQLException {
        return readByFiltro("id_repartidor", idRepartidor);
    }

    private List<Entrega> readByFiltro(String columna, int valor) throws SQLException {
        List<Entrega> lista = new ArrayList<>();
        String sql = "SELECT * FROM entregas WHERE " + columna + "=?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, valor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Entrega(
                            rs.getInt("id"),
                            rs.getInt("id_pedido"),
                            rs.getInt("id_repartidor"),
                            rs.getDate("fecha"),
                            rs.getTime("hora")));
                }
            }
        }
        return lista;
    }

    public void update(Entrega e) throws SQLException {
        String sql = "UPDATE entregas SET id_pedido=?, id_repartidor=?, fecha=?, hora=? WHERE id=?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, e.getIdPedido());
            ps.setInt(2, e.getIdRepartidor());
            ps.setDate(3, e.getFecha());
            ps.setTime(4, e.getHora());
            ps.setInt(5, e.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM entregas WHERE id=?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}