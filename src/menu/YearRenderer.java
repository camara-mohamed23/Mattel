/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package menu;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author User
 */
public class YearRenderer extends DefaultTableCellRenderer {
 public YearRenderer() {
        setOpaque(true); // Assure que le fond est opaque pour afficher la couleur de sélection
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        } else {
            setBackground(table.getBackground());
            setForeground(table.getForeground());
        }

        // Formater la valeur de la colonne Année
        if (value instanceof java.util.Date) {
            java.util.Date dateValue = (java.util.Date) value;
            setText(new java.text.SimpleDateFormat("yyyy-MM-dd").format(dateValue));
        } else {
            setText(value != null ? value.toString() : ""); // Afficher le texte d'origine
        }

        return this;
    } 
}
