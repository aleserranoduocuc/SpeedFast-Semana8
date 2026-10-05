package vista;

import dao.EntregaDAO;
import dao.PedidoDAO;
import dao.RepartidorDAO;
import modelo.Entrega;
import modelo.Pedido;
import modelo.Repartidor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class PanelEntregas extends JPanel {

    private JComboBox<Pedido> cbPedido;
    private JComboBox<Repartidor> cbRepartidor;
    private JTextField txtFecha;
    private JTextField txtHora;
    private JTable tabla;
    private DefaultTableModel modelo;
    private EntregaDAO dao = new EntregaDAO();
    private PedidoDAO pedidoDAO = new PedidoDAO();
    private RepartidorDAO repartidorDAO = new RepartidorDAO();
    private int idSeleccionado = -1;

    public PanelEntregas() {
        setLayout(new BorderLayout(10, 10));

        // --- Formulario ---
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Pedido:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        cbPedido = new JComboBox<>();
        form.add(cbPedido, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        form.add(new JLabel("Repartidor:"), gbc);
        gbc.gridx = 1;
        cbRepartidor = new JComboBox<>();
        form.add(cbRepartidor, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        form.add(new JLabel("Fecha (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        txtFecha = new JTextField(LocalDate.now().toString());
        form.add(txtFecha, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        form.add(new JLabel("Hora (HH:MM:SS):"), gbc);
        gbc.gridx = 1;
        txtHora = new JTextField(LocalTime.now().withNano(0).toString());
        form.add(txtHora, gbc);

        // --- Botones ---
        JButton btnGuardar = new JButton("Registrar");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");
        JButton btnRefrescar = new JButton("Refrescar combos");

        JPanel botones = new JPanel(new FlowLayout());
        botones.add(btnGuardar);
        botones.add(btnEditar);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);
        botones.add(btnRefrescar);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        form.add(botones, gbc);

        add(form, BorderLayout.NORTH);

        // --- Tabla ---
        modelo = new DefaultTableModel(
                new String[]{"ID", "ID Pedido", "ID Repartidor", "Fecha", "Hora"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modelo);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // --- Eventos ---
        btnGuardar.addActionListener(e -> registrar());
        btnEditar.addActionListener(e -> editar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());
        btnRefrescar.addActionListener(e -> cargarCombos());

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() != -1) {
                int row = tabla.getSelectedRow();
                idSeleccionado = (int) modelo.getValueAt(row, 0);
                txtFecha.setText(modelo.getValueAt(row, 3).toString());
                txtHora.setText(modelo.getValueAt(row, 4).toString());

                int idPedido = (int) modelo.getValueAt(row, 1);
                int idRepartidor = (int) modelo.getValueAt(row, 2);

                seleccionarComboPorId(cbPedido, idPedido);
                seleccionarComboPorId(cbRepartidor, idRepartidor);
            }
        });

        cargarCombos();
        cargarTabla();
    }

    /**
     * Carga los combos de Pedido y Repartidor desde la base de datos.
     */
    private void cargarCombos() {
        try {
            cbPedido.removeAllItems();
            for (Pedido p : pedidoDAO.readAll()) {
                cbPedido.addItem(p);
            }
            cbRepartidor.removeAllItems();
            for (Repartidor r : repartidorDAO.readAll()) {
                cbRepartidor.addItem(r);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar combos: " + ex.getMessage());
        }
    }

    private void seleccionarComboPorId(JComboBox<?> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            Object item = combo.getItemAt(i);
            if (item instanceof Pedido && ((Pedido) item).getId() == id) {
                combo.setSelectedIndex(i); return;
            }
            if (item instanceof Repartidor && ((Repartidor) item).getId() == id) {
                combo.setSelectedIndex(i); return;
            }
        }
    }

    private void registrar() {
        if (cbPedido.getSelectedItem() == null || cbRepartidor.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Debes seleccionar pedido y repartidor.");
            return;
        }
        try {
            Date fecha = Date.valueOf(txtFecha.getText().trim());
            Time hora = Time.valueOf(txtHora.getText().trim());

            Pedido p = (Pedido) cbPedido.getSelectedItem();
            Repartidor r = (Repartidor) cbRepartidor.getSelectedItem();

            dao.create(new Entrega(0, p.getId(), r.getId(), fecha, hora));
            JOptionPane.showMessageDialog(this, "Entrega registrada.");
            limpiar();
            cargarTabla();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Formato de fecha u hora inválido.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void editar() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una entrega.");
            return;
        }
        try {
            Date fecha = Date.valueOf(txtFecha.getText().trim());
            Time hora = Time.valueOf(txtHora.getText().trim());
            Pedido p = (Pedido) cbPedido.getSelectedItem();
            Repartidor r = (Repartidor) cbRepartidor.getSelectedItem();

            dao.update(new Entrega(idSeleccionado, p.getId(), r.getId(), fecha, hora));
            JOptionPane.showMessageDialog(this, "Entrega actualizada.");
            limpiar();
            cargarTabla();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Formato de fecha u hora inválido.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void eliminar() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una entrega.");
            return;
        }
        int op = JOptionPane.showConfirmDialog(this, "¿Eliminar entrega?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (op != JOptionPane.YES_OPTION) return;

        try {
            dao.delete(idSeleccionado);
            JOptionPane.showMessageDialog(this, "Entrega eliminada.");
            limpiar();
            cargarTabla();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void limpiar() {
        if (cbPedido.getItemCount() > 0) cbPedido.setSelectedIndex(0);
        if (cbRepartidor.getItemCount() > 0) cbRepartidor.setSelectedIndex(0);
        txtFecha.setText(LocalDate.now().toString());
        txtHora.setText(LocalTime.now().withNano(0).toString());
        idSeleccionado = -1;
        tabla.clearSelection();
    }

    private void cargarTabla() {
        modelo.setRowCount(0);
        try {
            for (Entrega e : dao.readAll()) {
                modelo.addRow(new Object[]{
                        e.getId(), e.getIdPedido(), e.getIdRepartidor(),
                        e.getFecha(), e.getHora()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar: " + ex.getMessage());
        }
    }
}