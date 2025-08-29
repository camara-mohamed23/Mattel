/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package menu;

import java.awt.Component;
import java.net.URL;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author User
 */
public class PdfIconRenderer extends DefaultTableCellRenderer {
    private Icon pdfIcon;

    public PdfIconRenderer() {
        // Charger l'icône PDF en utilisant le chemin relatif
        URL imgURL = getClass().getResource("/RAPPORT.png");
        if (imgURL != null) {
            pdfIcon = new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: pdf_icon.png");
            pdfIcon = null; // Défaut à une valeur par défaut si l'icône n'est pas trouvée
        }
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (value instanceof byte[]) {
            label.setText("");
            label.setIcon(pdfIcon);
        } else {
            label.setIcon(null);
        }

        return label;
    }
    
}
