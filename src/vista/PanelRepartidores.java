package vista;

import dao.RepartidorDAO;
import modelo.Repartidor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class PanelRepartidores extends JPanel {

    private JTextField txtNombre;
    private JTable tabla;
    private DefaultTableModel modelo;
    private RepartidorDAO dao = new RepartidorDAO();
    private int idSeleccionado = -1;

    public PanelRepartidores() {
        setLayout(new BorderLayout(10, 10));

        // --- Formulario superior ---
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtNombre = new JTextField(20);
        form.add(txtNombre, gbc);

        JButton btnGuardar = new JButton("Registrar");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");

        JPanel botones = new JPanel(new FlowLayout());
        botones.add(btnGuardar);
        botones.add(btnEditar);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; gbc.weightx = 0;
        form.add(botones, gbc);

        add(form, BorderLayout.NORTH);

        // --- Tabla central ---
        modelo = new DefaultTableModel(new String[]{"ID", "Nombre"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabla = new JTable(modelo);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // --- Eventos ---
        btnGuardar.addActionListener(e -> registrar());
        btnEditar.addActionListener(e -> editar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() != -1) {
                idSeleccionado = (int) modelo.getValueAt(tabla.getSelectedRow(), 0);
                txtNombre.setText((String) modelo.getValueAt(tabla.getSelectedRow(), 1));
            }
        });

        cargarTabla();
    }

    private void registrar() {
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            dao.create(new Repartidor(0, txtNombre.getText().trim()));
            JOptionPane.showMessageDialog(this, "Repartidor registrado correctamente.");
            limpiar();
            cargarTabla();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al registrar: " + ex.getMessage(),
                    "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editar() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un repartidor de la tabla.");
            return;
        }
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio.");
            return;
        }
        try {
            dao.update(new Repartidor(idSeleccionado, txtNombre.getText().trim()));
            JOptionPane.showMessageDialog(this, "Repartidor actualizado.");
            limpiar();
            cargarTabla();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al actualizar: " + ex.getMessage());
        }
    }

    private void eliminar() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un repartidor.");
            return;
        }
        int op = JOptionPane.showConfirmDialog(this,
                "¿Eliminar repartidor seleccionado?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (op != JOptionPane.YES_OPTION) return;

        try {
            dao.delete(idSeleccionado);
            JOptionPane.showMessageDialog(this, "Repartidor eliminado.");
            limpiar();
            cargarTabla();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage());
        }
    }

    private void limpiar() {
        txtNombre.setText("");
        idSeleccionado = -1;
        tabla.clearSelection();
    }

    private void cargarTabla() {
        modelo.setRowCount(0);
        try {
            List<Repartidor> lista = dao.readAll();
            for (Repartidor r : lista) {
                modelo.addRow(new Object[]{r.getId(), r.getNombre()});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos: " + ex.getMessage());
        }
    }
}