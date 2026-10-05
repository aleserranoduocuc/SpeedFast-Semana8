package vista;

import dao.PedidoDAO;
import modelo.Pedido;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class PanelPedidos extends JPanel {

    private JTextField txtDireccion;
    private JComboBox<String> cbTipo;
    private JComboBox<String> cbEstado;
    private JComboBox<String> cbFiltroEstado;
    private JComboBox<String> cbFiltroTipo;
    private JTable tabla;
    private DefaultTableModel modelo;
    private PedidoDAO dao = new PedidoDAO();
    private int idSeleccionado = -1;

    private final String[] TIPOS = {"COMIDA", "ENCOMIENDA", "EXPRESS"};
    private final String[] ESTADOS = {"PENDIENTE", "EN_REPARTO", "ENTREGADO"};

    public PanelPedidos() {
        setLayout(new BorderLayout(10, 10));

        // --- Formulario ---
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Dirección:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtDireccion = new JTextField(20);
        form.add(txtDireccion, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        form.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 1;
        cbTipo = new JComboBox<>(TIPOS);
        form.add(cbTipo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        form.add(new JLabel("Estado:"), gbc);
        gbc.gridx = 1;
        cbEstado = new JComboBox<>(ESTADOS);
        form.add(cbEstado, gbc);

        // --- Botones CRUD ---
        JButton btnGuardar = new JButton("Registrar");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");

        JPanel botones = new JPanel(new FlowLayout());
        botones.add(btnGuardar);
        botones.add(btnEditar);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        form.add(botones, gbc);

        // --- Filtros ---
        cbFiltroEstado = new JComboBox<>(new String[]{"TODOS", "PENDIENTE", "EN_REPARTO", "ENTREGADO"});
        cbFiltroTipo = new JComboBox<>(new String[]{"TODOS", "COMIDA", "ENCOMIENDA", "EXPRESS"});
        JButton btnFiltrar = new JButton("Filtrar");

        JPanel filtros = new JPanel(new FlowLayout());
        filtros.add(new JLabel("Filtro estado:"));
        filtros.add(cbFiltroEstado);
        filtros.add(new JLabel("Filtro tipo:"));
        filtros.add(cbFiltroTipo);
        filtros.add(btnFiltrar);

        gbc.gridy = 4;
        form.add(filtros, gbc);

        add(form, BorderLayout.NORTH);

        // --- Tabla ---
        modelo = new DefaultTableModel(new String[]{"ID", "Dirección", "Tipo", "Estado"}, 0) {
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
        btnFiltrar.addActionListener(e -> aplicarFiltros());

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() != -1) {
                int row = tabla.getSelectedRow();
                idSeleccionado = (int) modelo.getValueAt(row, 0);
                txtDireccion.setText((String) modelo.getValueAt(row, 1));
                cbTipo.setSelectedItem(modelo.getValueAt(row, 2));
                cbEstado.setSelectedItem(modelo.getValueAt(row, 3));
            }
        });

        cargarTabla();
    }

    private void registrar() {
        if (txtDireccion.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "La dirección es obligatoria.");
            return;
        }
        try {
            dao.create(new Pedido(0, txtDireccion.getText().trim(),
                    (String) cbTipo.getSelectedItem(),
                    (String) cbEstado.getSelectedItem()));
            JOptionPane.showMessageDialog(this, "Pedido registrado.");
            limpiar();
            cargarTabla();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void editar() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un pedido.");
            return;
        }
        try {
            dao.update(new Pedido(idSeleccionado, txtDireccion.getText().trim(),
                    (String) cbTipo.getSelectedItem(),
                    (String) cbEstado.getSelectedItem()));
            JOptionPane.showMessageDialog(this, "Pedido actualizado.");
            limpiar();
            cargarTabla();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void eliminar() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un pedido.");
            return;
        }
        int op = JOptionPane.showConfirmDialog(this, "¿Eliminar pedido?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (op != JOptionPane.YES_OPTION) return;

        try {
            dao.delete(idSeleccionado);
            JOptionPane.showMessageDialog(this, "Pedido eliminado.");
            limpiar();
            cargarTabla();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void aplicarFiltros() {
        String estado = (String) cbFiltroEstado.getSelectedItem();
        String tipo = (String) cbFiltroTipo.getSelectedItem();
        if ("TODOS".equals(estado)) estado = null;
        if ("TODOS".equals(tipo)) tipo = null;

        modelo.setRowCount(0);
        try {
            List<Pedido> lista = dao.readByFiltro(estado, tipo);
            for (Pedido p : lista) {
                modelo.addRow(new Object[]{p.getId(), p.getDireccion(), p.getTipo(), p.getEstado()});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al filtrar: " + ex.getMessage());
        }
    }

    private void limpiar() {
        txtDireccion.setText("");
        cbTipo.setSelectedIndex(0);
        cbEstado.setSelectedIndex(0);
        idSeleccionado = -1;
        tabla.clearSelection();
    }

    private void cargarTabla() {
        modelo.setRowCount(0);
        try {
            for (Pedido p : dao.readAll()) {
                modelo.addRow(new Object[]{p.getId(), p.getDireccion(), p.getTipo(), p.getEstado()});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar: " + ex.getMessage());
        }
    }
}