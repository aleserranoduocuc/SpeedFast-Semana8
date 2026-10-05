package vista;

import javax.swing.*;

/**
 * Ventana principal que contiene las pestañas de gestión.
 */
public class VentanaPrincipal extends JFrame {

    public VentanaPrincipal() {
        setTitle("SpeedFast - Gestión de Pedidos");
        setSize(950, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Repartidores", new PanelRepartidores());
        tabs.addTab("Pedidos", new PanelPedidos());
        tabs.addTab("Entregas", new PanelEntregas());

        add(tabs);
    }
}