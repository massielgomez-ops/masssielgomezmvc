package vallegrande.edu.pe.view;

import vallegrande.edu.pe.controller.ContactController;
import vallegrande.edu.pe.model.Contact;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Vista principal con Swing que muestra la lista de contactos y botones.
 * Ventana maximizada, botones con estilo moderno y colores.
 */
public class ContactView extends JFrame {
    private final ContactController controller;
    private DefaultTableModel tableModel;
    private JTable table;

    public ContactView(ContactController controller) {
        super("Agenda MVC Swing - Vallegrande");
        this.controller = controller;
        initUI();
        loadContacts();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Inicia maximizado a pantalla completa
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Fuente base para toda la ventana
        Font baseFont = new Font("Segoe UI", Font.PLAIN, 16);

        // Configuramos layout principal con espacio y márgenes
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(contentPanel);

        // Tabla con modelo y estilo
        tableModel = new DefaultTableModel(new String[]{"ID", "Nombre", "Email", "Teléfono"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(baseFont);
        table.setRowHeight(30);
        table.getTableHeader().setFont(baseFont.deriveFont(Font.BOLD, 18f));
        table.setForeground(new Color(33, 33, 33));
        table.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel para botones alineados a la derecha
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        buttonsPanel.setBackground(Color.WHITE); // fondo blanco limpio

        // Botón "Agregar" con estilo moderno
        JButton addBtn = new JButton("Agregar");
        styleButton(addBtn, new Color(0, 123, 255)); // azul vibrante

        // Botón "Eliminar" con estilo moderno
        JButton deleteBtn = new JButton("Eliminar");
        styleButton(deleteBtn, new Color(220, 53, 69)); // rojo vibrante

        buttonsPanel.add(addBtn);
        buttonsPanel.add(deleteBtn);

        contentPanel.add(buttonsPanel, BorderLayout.SOUTH);

        // Eventos botones
        addBtn.addActionListener(e -> showAddContactDialog());
        deleteBtn.addActionListener(e -> deleteSelectedContact());
    }

    /**
     * Aplica estilos modernos y hover a los botones.
     */
    private void styleButton(JButton button, Color baseColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(baseColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);

        // Cambio de color al pasar mouse (hover)
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(baseColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(baseColor);
            }
        });
    }

    private void loadContacts() {
        tableModel.setRowCount(0);
        List<Contact> contacts = controller.list();
        for (Contact c : contacts) {
            tableModel.addRow(new Object[]{c.id(), c.name(), c.email(), c.phone()});
        }
    }

    private void showAddContactDialog() {
        AddContactDialog dialog = new AddContactDialog(this, controller);
        dialog.setVisible(true);
        if (dialog.isSucceeded()) {
            loadContacts();
        }
    }

    private void deleteSelectedContact() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un contacto para eliminar.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = (String) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "¿Seguro que desea eliminar este contacto?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            controller.delete(id);
            loadContacts();
        }
    }
}