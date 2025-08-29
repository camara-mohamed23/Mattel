/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package menu;
import com.toedter.calendar.JDateChooser;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import login.LOGIN;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
/**
 *
 * @author User
 */
public class index extends javax.swing.JFrame {
 //private JTable table;
 private DefaultTableModel model;
private com.toedter.calendar.JDateChooser dateChooser;


    public index(){
        dateField = new JDateChooser();
         sommelabel = new JLabel();
      calculerSommeTotal();
        initComponents();
         
         recherchenom();
         rechercherParDate();
         calculerSommeTotal();
         calculerSoldeMax();
         createAndDisplayBarChart();
       calculerSoldeMIN();
       displayBarChart();
       calculerNombreTotalRevendeurs();
       sommelabel = new JLabel();
       
        JTableHeader header = table.getTableHeader();

    // Personnalisez l'apparence de l'en-tête
    header.setBackground(Color.lightGray);
    header.setForeground(Color.black);
    header.setFont(new Font("SansSerif", Font.BOLD, 15));

        //setUndecorated(true);
  Calendar calendar = Calendar.getInstance();     
calendar.add(Calendar.DAY_OF_MONTH, -1);

// Obtient la date d'hier
Date yesterday = calendar.getTime();

// Définir la date dans le composant JDateChooser
dategraphe.setDate(yesterday);
COPYRIGHT.setText("\u00A9 Mouhamed Camara - Tous droits réservés");
// Maximiser la JFrame pour occuper tout l'écran
setExtendedState(JFrame.MAXIMIZED_BOTH);
       
    }

       
  public Connection getConnection()
    {
        Connection con = null;
        
        try{
            con = DriverManager.getConnection("jdbc:MySQL://localhost:3306/matel","root","");
        }catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
        
        return con;
    }
   public ArrayList<Core> ListUsers(String ValToSearch)
    {
        ArrayList<Core> usersList = new ArrayList<>();
        
        
        try{
              Connection con = getConnection();
            Statement st = con.createStatement();
            String searchQuery = "SELECT * FROM donnees WHERE CONCAT(ID,DATE_HEURE, DATE_CREATION, NOM_V1, NOM_V2, NUMERO, GROUPE, TYPE, SOLDE) LIKE '%"+ValToSearch+"%'";
            ResultSet  rs = st.executeQuery(searchQuery);
            
            Core core;
            
            while(rs.next())
            {
                core = new Core(
                                 rs.getInt("ID"),
                                 rs.getString("DATE_HEURE"),
                                 rs.getDate("DATE_CREATION"),
                                 rs.getString("NOM_V1"),
                                rs.getString("NOM_V2"),
                                rs.getInt("NUMERO"),
                                rs.getString("GROUPE"),
                                rs.getString("TYPE"),
                                rs.getDouble("SOLDE") );
                usersList.add(core);
            }
            
        }catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
        
        return usersList;
    }
   
     public void recherchenom()
    {
       DefaultTableModel model = new DefaultTableModel();
    model.setColumnIdentifiers(new Object[]{"id","date et heure", "date", "nom1 revendeur", "nom2 revendeur", "numero", "groupe", "type", "solde"});
    
    try {
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/matel", "root", "");
        String name = nameField.getText();
        String query = "SELECT * FROM donnees WHERE NOM_V1 LIKE '%" + name + "%' OR NOM_V2 LIKE '%" + name + "%'";
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            Object[] row = new Object[9];
            row[0] = rs.getObject("id");
            row[1] = rs.getObject("date_heure");
            row[2] = rs.getObject("date_creation");
            row[3] = rs.getObject("nom_V1");
            row[4] = rs.getObject("nom_V2");
            row[5] = rs.getObject("numero");
            row[6] = rs.getObject("groupe");
            row[7] = rs.getObject("type");
            row[8] = rs.getObject("solde");
            model.addRow(row);
        }
        
        con.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
    
    table.setModel(model);
       
    }
     
     private void createAndDisplayBarChart() {
    // Récupérer le groupe sélectionné dans la combobox
    String selectedGroup = (String) COMBO.getSelectedItem();
     Date selectedDate = dategraphe.getDate();
       if (selectedDate == null) {
         // JOptionPane.showMessageDialog(null, "Veuillez sélectionner une date et un nom", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

     if (dategraphe != null && dategraphe.getDate() != null) {
        // Récupère la date sélectionnée dans DATEGRAPHE
        selectedDate = dategraphe.getDate();
        
        // Votre code pour le reste de la méthode ici...
    } else {
        // Gérer le cas où DATEGRAPHE est null ou sa valeur est null
        //System.err.println("AUCUNE DATE EST SELECTIONNEE");
    }
    // Vérifier si un groupe est sélectionné
    if (selectedGroup != null) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        try {
            // Connexion à la base de données et exécution de la requête pour obtenir les données
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/matel", "root", "");
            String query = "SELECT NOM_V1, SUM(solde) AS total_solde FROM donnees WHERE groupe = ? AND DATE(DATE_HEURE) = ? GROUP BY NOM_V1";
                
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, selectedGroup);
            pstmt.setDate(2, new java.sql.Date(selectedDate.getTime()));
            ResultSet rs = pstmt.executeQuery();

            // Parcours des résultats de la requête et ajout des données au dataset
            while (rs.next()) {
                String revendeur = rs.getString("NOM_V1").trim();
                double solde = rs.getDouble("total_solde");
                //System.out.println("Revendeur: " + revendeur + ", Solde: " + solde);

                dataset.addValue(solde, "Solde", revendeur);
            }
               //System.out.println("Contenu du dataset :");
        for (int i = 0; i < dataset.getRowCount(); i++) {
            Comparable rowKey = dataset.getRowKey(i);
            for (int j = 0; j < dataset.getColumnCount(); j++) {
                Comparable columnKey = dataset.getColumnKey(j);
                Number value = dataset.getValue(i, j);
              //  System.out.println("Row: " + rowKey + ", Column: " + columnKey + ", Value: " + value);
            }}

            con.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Création du graphique à barres
        JFreeChart barChart = ChartFactory.createBarChart(
                "" + selectedGroup,
                "Revendeur",
                "Solde",
                dataset
        );
             ChartPanel chartPanel = new ChartPanel(barChart);

        // Ajout du ChartPanel au panneau
        panel.removeAll(); // Supprime tous les composants du panneau
        panel.setLayout(new BorderLayout()); // Définit le layout du panneau
        panel.add(chartPanel, BorderLayout.CENTER); // Ajoute le ChartPanel au centre du panneau
        panel.revalidate();
        CategoryPlot plot = barChart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();

        // Définition de la couleur des barres
        renderer.setSeriesPaint(0, Color.BLUE); 
        renderer.setMaximumBarWidth(0.03);

// Définition de la couleur des barres
       //
      
    }
     
}
     
     
    public void displayBarChart() {
    DefaultCategoryDataset dataset = createDataset();
    JFreeChart chart = ChartFactory.createBarChart(
            "", // Titre du graphique
            "les Groupes", // Titre de l'axe des abscisses
            "Total de somme", // Titre de l'axe des ordonnées
            dataset); // Données pour le graphique

    ChartPanel chartPanel = new ChartPanel(chart);

    // Ajoute le ChartPanel à votre interface graphique
     panel1.removeAll(); // Supprime tous les composants du panneau
        panel1.setLayout(new BorderLayout()); // Définit le layout du panneau
        panel1.add(chartPanel, BorderLayout.CENTER); // Ajoute le ChartPanel au centre du panneau
        panel1.revalidate();
        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();

        // Définition de la couleur des barres
        renderer.setSeriesPaint(0, Color.BLUE); 
        renderer.setMaximumBarWidth(0.03);

    pack(); // Redimensionne le JFrame pour s'adapter au contenu
}

private DefaultCategoryDataset createDataset() {
     DefaultCategoryDataset dataset = new DefaultCategoryDataset();

    // Récupérer la date sélectionnée
    Date selectedDate = dategraphe.getDate();
    
    // Vérifier si une date est sélectionnée
    if (selectedDate != null) {
        // Convertir la date en format SQL
        java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime());

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/matel", "root", "");
            PreparedStatement pstmt = conn.prepareStatement("SELECT GROUPE, SUM(SOLDE) AS Total FROM donnees WHERE DATE(DATE_HEURE)= ? GROUP BY GROUPE");
            pstmt.setDate(1, sqlDate); // Utiliser la date sélectionnée dans la requête SQL
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String groupe = rs.getString("GROUPE");
                double total = rs.getDouble("Total");
                dataset.addValue(total, "Total", groupe);
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    } 
    return dataset;

}

  


     
    
    

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        panelsomme = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        sommelabel = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        soldemax = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        nbrere = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        soldeminimal = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        panel = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        panel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        COMBO = new javax.swing.JComboBox<>();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel12 = new javax.swing.JLabel();
        dategraphe = new com.toedter.calendar.JDateChooser();
        jLabel5 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        comboBox = new javax.swing.JComboBox<>();
        dateField = new com.toedter.calendar.JDateChooser();
        nameField = new javax.swing.JTextField();
        supprimer = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        jPanel8 = new javax.swing.JPanel();
        COPYRIGHT = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Core Mattel");
        setAlwaysOnTop(true);

        jScrollPane1.setBackground(new java.awt.Color(153, 153, 255));
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setToolTipText("");
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setViewportBorder(javax.swing.BorderFactory.createEtchedBorder());
        jScrollPane1.setAutoscrolls(true);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(1682, 1775));

        jPanel1.setBackground(new java.awt.Color(226, 234, 244));
        jPanel1.setAutoscrolls(true);
        jPanel1.setPreferredSize(new java.awt.Dimension(1383, 1757));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(0, 153, 153));
        jPanel2.setPreferredSize(new java.awt.Dimension(1380, 97));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel1.setIcon(new javax.swing.ImageIcon("M:\\ICONE\\mattel.png")); // NOI18N
        jLabel1.setText("Gestion de mischilli");

        jButton2.setBackground(new java.awt.Color(0, 153, 153));
        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton2.setIcon(new javax.swing.ImageIcon("M:\\ICONE\\D.png")); // NOI18N
        jButton2.setBorder(null);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton5.setBackground(new java.awt.Color(0, 153, 153));
        jButton5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton5.setForeground(new java.awt.Color(255, 255, 255));
        jButton5.setText("Gestion transaction");
        jButton5.setBorder(null);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton1.setBackground(new java.awt.Color(0, 153, 153));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Transaction bancaire");
        jButton1.setBorder(null);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(0, 153, 153));
        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setText("Gestion rapport");
        jButton4.setBorder(null);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton6.setBackground(new java.awt.Color(0, 153, 153));
        jButton6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton6.setForeground(new java.awt.Color(255, 255, 255));
        jButton6.setText("Accueil");
        jButton6.setBorder(null);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setBackground(new java.awt.Color(0, 153, 153));
        jButton7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton7.setForeground(new java.awt.Color(255, 255, 255));
        jButton7.setText("à propos ");
        jButton7.setBorder(null);
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 266, Short.MAX_VALUE)
                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(98, 98, 98)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButton2)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(65, 65, 65))
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1370, 110));

        jPanel4.setBackground(new java.awt.Color(243, 243, 243));

        panelsomme.setBackground(new java.awt.Color(238, 237, 237));
        panelsomme.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setIcon(new javax.swing.ImageIcon("C:\\Users\\User\\OneDrive\\Documents\\NetBeansProjects\\matel\\src\\icone\\SOLDE.jpg")); // NOI18N
        jLabel3.setText("            TOTAL DE SOLDE");

        sommelabel.setBackground(new java.awt.Color(0, 153, 153));
        sommelabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        sommelabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jPanel12.setBackground(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 19, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout panelsommeLayout = new javax.swing.GroupLayout(panelsomme);
        panelsomme.setLayout(panelsommeLayout);
        panelsommeLayout.setHorizontalGroup(
            panelsommeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(panelsommeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelsommeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sommelabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelsommeLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(0, 72, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelsommeLayout.setVerticalGroup(
            panelsommeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelsommeLayout.createSequentialGroup()
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addGap(3, 3, 3)
                .addComponent(sommelabel, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));
        jPanel10.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setIcon(new javax.swing.ImageIcon("M:\\ICONE\\SALE.jpg")); // NOI18N
        jLabel6.setText("            SOLDE MAXIMUM");

        soldemax.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        soldemax.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jPanel13.setBackground(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 17, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(soldemax, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(0, 72, Short.MAX_VALUE))))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(soldemax, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel7.setBackground(new java.awt.Color(238, 237, 237));
        jPanel7.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jPanel9.setBackground(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 21, Short.MAX_VALUE)
        );

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel14.setIcon(new javax.swing.ImageIcon("C:\\Users\\User\\OneDrive\\Documents\\NetBeansProjects\\matel\\src\\icone\\USER.png")); // NOI18N
        jLabel14.setText("            NOMBRE REVENDEUR");

        nbrere.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        nbrere.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                        .addContainerGap())
                    .addComponent(nbrere, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(nbrere, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 48, Short.MAX_VALUE))
        );

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jPanel11.setBackground(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 19, Short.MAX_VALUE)
        );

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel15.setIcon(new javax.swing.ImageIcon("M:\\ICONE\\SOLDEMIN.jpg")); // NOI18N
        jLabel15.setText("              SOLDE MINIMAL");

        soldeminimal.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        soldeminimal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 39, Short.MAX_VALUE))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(soldeminimal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(soldeminimal, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jScrollPane3.setBackground(new java.awt.Color(51, 51, 255));
        jScrollPane3.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane3.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane3.setAutoscrolls(true);

        panel.setBackground(new java.awt.Color(204, 204, 204));
        panel.setAutoscrolls(true);

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 641, Short.MAX_VALUE)
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 580, Short.MAX_VALUE)
        );

        jScrollPane3.setViewportView(panel);

        jScrollPane4.setBackground(new java.awt.Color(0, 0, 0));
        jScrollPane4.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane4.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jScrollPane4.setAutoscrolls(true);

        panel1.setBackground(new java.awt.Color(153, 153, 153));
        panel1.setAutoscrolls(true);

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 674, Short.MAX_VALUE)
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 946, Short.MAX_VALUE)
        );

        jScrollPane4.setViewportView(panel1);

        jLabel4.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setIcon(new javax.swing.ImageIcon("M:\\ICONE\\graphique.png")); // NOI18N
        jLabel4.setText("Graphiques des données ");

        COMBO.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        COMBO.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Groupe1", "Groupe2", "Groupe3", " ", " " }));
        COMBO.setBorder(null);
        COMBO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                COMBOActionPerformed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel12.setIcon(new javax.swing.ImageIcon("M:\\ICONE\\graphe.jpg")); // NOI18N
        jLabel12.setText("                   Total de somme pour chaque groupe: ");

        dategraphe.setDateFormatString("yyyy-MM-dd");
        dategraphe.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                dategrapheAncestorAdded(evt);
            }
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });
        dategraphe.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                dategraphePropertyChange(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setIcon(new javax.swing.ImageIcon("M:\\ICONE\\graphe.jpg")); // NOI18N
        jLabel5.setText("                    Graphe solde par revendeur pour :");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(22, 22, 22)
                        .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(panelsomme, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(21, 21, 21))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 485, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(COMBO, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(dategraphe, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 462, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 643, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                        .addGap(8, 8, 8)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelsomme, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(COMBO, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel12)
                        .addComponent(dategraphe, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 451, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGap(9, 9, 9)))
                .addGap(17, 17, 17))
        );

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 178, -1, -1));

        jLabel2.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setIcon(new javax.swing.ImageIcon("C:\\Users\\User\\OneDrive\\Documents\\NetBeansProjects\\matel\\src\\icone\\dash.png")); // NOI18N
        jLabel2.setText("DASHBOARD");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 138, 210, 40));

        jPanel5.setBackground(new java.awt.Color(0, 153, 153));
        jPanel5.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel7.setIcon(new javax.swing.ImageIcon("C:\\Users\\User\\OneDrive\\Documents\\NetBeansProjects\\matel\\src\\icone\\icone-de-filtre-rouge.png")); // NOI18N
        jLabel7.setText("FILTRE");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setIcon(new javax.swing.ImageIcon("C:\\Users\\User\\OneDrive\\Documents\\NetBeansProjects\\matel\\src\\icone\\USEJ.png")); // NOI18N
        jLabel8.setText("NOM");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setIcon(new javax.swing.ImageIcon("C:\\Users\\User\\OneDrive\\Documents\\NetBeansProjects\\matel\\src\\icone\\date_heure.jpg")); // NOI18N
        jLabel9.setText("DATE");

        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton3.setIcon(new javax.swing.ImageIcon("M:\\ICONE\\ajoute.png")); // NOI18N
        jButton3.setText("Ajouter");
        jButton3.setBorder(null);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setIcon(new javax.swing.ImageIcon("M:\\ICONE\\groupe.png")); // NOI18N
        jLabel10.setText("GROUPE");

        comboBox.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        comboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Groupe1", "Groupe2", "Groupe3", "Groupe4" }));
        comboBox.setSelectedItem(table);
        comboBox.setAutoscrolls(true);
        comboBox.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 4, 0, new java.awt.Color(255, 255, 255)));
        comboBox.setDoubleBuffered(true);
        comboBox.setRequestFocusEnabled(false);
        comboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboBoxItemStateChanged(evt);
            }
        });
        comboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxActionPerformed(evt);
            }
        });

        dateField.setBackground(new java.awt.Color(255, 255, 255));
        dateField.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 4, 0, new java.awt.Color(255, 255, 255)));
        dateField.setAutoscrolls(true);
        dateField.setDateFormatString("yyyy-MM-dd");
        dateField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                dateFieldPropertyChange(evt);
            }
        });

        nameField.setBackground(new java.awt.Color(0, 153, 153));
        nameField.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        nameField.setForeground(new java.awt.Color(255, 255, 255));
        nameField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        nameField.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 4, 0, new java.awt.Color(255, 255, 255)));
        nameField.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                nameFieldAncestorAdded(evt);
            }
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
                nameFieldAncestorMoved(evt);
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
                nameFieldAncestorRemoved(evt);
            }
        });
        nameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nameFieldActionPerformed(evt);
            }
        });

        supprimer.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        supprimer.setIcon(new javax.swing.ImageIcon("M:\\ICONE\\supprimer.png")); // NOI18N
        supprimer.setText("Supprimer");
        supprimer.setBorder(null);
        supprimer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                supprimerActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(170, 170, 170)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dateField, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(158, 158, 158)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(comboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 136, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(supprimer, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE))
                .addGap(19, 19, 19))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel10)
                            .addComponent(jLabel9))
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(4, 4, 4)))
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(comboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(supprimer, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(dateField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel1.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 1000, -1, -1));

        jScrollPane2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane2.setViewportBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jScrollPane2.setAutoscrolls(true);
        jScrollPane2.setPreferredSize(new java.awt.Dimension(666, 416));

        table.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        table.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        table.setForeground(new java.awt.Color(0, 153, 153));
        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "id", "date_heure", "date", "nom_revendeur1", "nom_revendeur2", "numero", "groupe", "type", "solde"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        table.setDoubleBuffered(true);
        table.setGridColor(new java.awt.Color(227, 227, 227));
        table.setPreferredSize(null);
        table.setRowHeight(30);
        table.setSelectionBackground(new java.awt.Color(0, 153, 153));
        table.setShowGrid(true);
        jScrollPane2.setViewportView(table);

        jPanel1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 1166, 1340, 510));

        jPanel8.setBackground(new java.awt.Color(0, 153, 153));

        COPYRIGHT.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        COPYRIGHT.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(450, 450, 450)
                .addComponent(COPYRIGHT, javax.swing.GroupLayout.PREFERRED_SIZE, 427, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(COPYRIGHT, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 1682, 1374, 130));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel13.setIcon(new javax.swing.ImageIcon("M:\\ICONE\\tableau.png")); // NOI18N
        jLabel13.setText("Table des données");
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 944, 260, 50));

        jScrollPane1.setViewportView(jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1383, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1819, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
         Ajoute a = new Ajoute();
        a.setVisible(true);
       
        a.pack();
        a.setLocationRelativeTo(null); 
        this.dispose();       
    }//GEN-LAST:event_jButton3ActionPerformed

    private void comboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxActionPerformed
        String selectedGroup = (String) comboBox.getSelectedItem();
        
        // Exécuter une requête SQL pour récupérer les données correspondant à ce groupe
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/matel", "root", "");
            String query = "SELECT * FROM donnees WHERE groupe = ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, selectedGroup);
            ResultSet rs = pstmt.executeQuery();

            // Créer un nouveau modèle de tableau pour stocker les données récupérées
            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(new Object[]{"id","date et heure", "date", "nom1 revendeur", "nom2 revendeur", "numero", "groupe", "type", "solde"});
            
            // Parcourir les résultats de la requête et ajouter chaque ligne au modèle de tableau
            while (rs.next()) {
                Object[] row = new Object[9];
                row[0] = rs.getObject("id");
                row[1] = rs.getObject("date_heure");
                row[2] = rs.getObject("date_creation");
                row[3] = rs.getObject("nom_V1");
                row[4] = rs.getObject("nom_V2");
                row[5] = rs.getObject("numero");
                row[6] = rs.getObject("groupe");
                row[7] = rs.getObject("type");
                row[8] = rs.getObject("solde");
                model.addRow(row);
            }
            
            // Mettre à jour le modèle de tableau avec les données récupérées
            table.setModel(model);
            
            con.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }//GEN-LAST:event_comboBoxActionPerformed

    private void nameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nameFieldActionPerformed
        String name = nameField.getText();
    
    // Réalisez la requête SQL avec le texte du champ de saisie pour filtrer les résultats
    String query = "SELECT * FROM donnees WHERE NOM_V1 LIKE '%" + name + "%' OR NOM_V2 LIKE '%" + name + "%'";

    // Créez un modèle de table pour stocker les résultats de la requête
    DefaultTableModel model = new DefaultTableModel();
    model.setColumnIdentifiers(new Object[]{"id","date et heure", "date", "nom1 revendeur", "nom2 revendeur", "numero", "groupe", "type", "solde"});

    try {
        // Établissez la connexion à la base de données
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/matel", "root", "");
        Statement stmt = con.createStatement();

        // Exécutez la requête SQL et obtenez le résultat dans un ResultSet
        ResultSet rs = stmt.executeQuery(query);

        // Parcourez le résultat et ajoutez chaque ligne au modèle de table
        while (rs.next()) {
            Object[] row = new Object[9];
            row[0] = rs.getObject("id");
            row[1] = rs.getObject("date_heure");
            row[2] = rs.getObject("date_creation");
            row[3] = rs.getObject("nom_V1");
            row[4] = rs.getObject("nom_V2");
            row[5] = rs.getObject("numero");
            row[6] = rs.getObject("groupe");
            row[7] = rs.getObject("type");
            row[8] = rs.getObject("solde");
            model.addRow(row);
        }

        // Fermez la connexion à la base de données
        con.close();
    } catch (Exception ex) {
        ex.printStackTrace();
    }

    // Mettez à jour le modèle de table du tableau avec les nouvelles données
    table.setModel(model);

    }//GEN-LAST:event_nameFieldActionPerformed

    private void rechercherParDate() {
     DefaultTableModel model = new DefaultTableModel();
    model.setColumnIdentifiers(new Object[]{"id","date et heure", "date", "nom1 revendeur", "nom2 revendeur", "numero", "groupe", "type", "solde"});

    try {
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/matel", "root", "");
        String date = ((JTextField)dateField.getDateEditor().getUiComponent()).getText(); // Récupérer la date saisie
        String query = "SELECT * FROM donnees WHERE DATE(DATE_HEURE)=?";
        PreparedStatement pstmt = con.prepareStatement(query);
        pstmt.setString(1, date); // Remplacer le premier paramètre par la date saisie

        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            Object[] row = new Object[9];
            row[0] = rs.getObject("id");
            row[1] = rs.getObject("date_heure");
            row[2] = rs.getObject("date_creation");
            row[3] = rs.getObject("nom_V1");
            row[4] = rs.getObject("nom_V2");
            row[5] = rs.getObject("numero");
            row[6] = rs.getObject("groupe");
            row[7] = rs.getObject("type");
            row[8] = rs.getObject("solde");
            model.addRow(row);
        }
        
        con.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
    
    table.setModel(model);    

}
    
    
  

// Déclarez un JLabel pour afficher la somme totale



// Méthode pour afficher la somme totale dans le panel
private void calculerSommeTotal() {
    try {
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/matel", "root", "");
        String query = "SELECT SUM(solde) AS total FROM donnees";
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        double sommeTotal = 0.0;
        if (rs.next()) {
            sommeTotal = rs.getDouble("total");
        }

        con.close();

        // Afficher la somme totale dans le label sommelabel
        sommelabel.setText( ""+sommeTotal);
    } catch (Exception e) {
        e.printStackTrace();
    }
}

private void calculerSoldeMax() {
    try {
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/matel", "root", "");
        String query = "SELECT MAX(solde) AS max_solde FROM donnees";
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        double soldeMax = 0.0;
        if (rs.next()) {
            soldeMax = rs.getDouble("max_solde");
        }

        con.close();

        // Afficher le solde maximum dans le label soldemax
        soldemax.setText(soldeMax+"");
    } catch (Exception e) {
        e.printStackTrace();
    }
}
private void calculerSoldeMIN() {
    try {
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/matel", "root", "");
        String query = "SELECT MIN(solde) AS min_solde FROM donnees";
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        double soldemin = 0.0;
        if (rs.next()) {
            soldemin = rs.getDouble("min_solde");
        }

        con.close();

        // Afficher le solde maximum dans le label soldemax
        soldeminimal.setText(soldemin+"");
    } catch (Exception e) {
        e.printStackTrace();
    }
}

public int calculerNombreTotalRevendeurs() {
    int nombreTotalRevendeurs = 0;
    try {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/matel", "root", "");
        Statement stmt = conn.createStatement();
        String query = "SELECT COUNT(DISTINCT NOM_V1) AS TotalRevendeurs FROM donnees";
        ResultSet rs = stmt.executeQuery(query);

        if (rs.next()) {
            nombreTotalRevendeurs = rs.getInt("TotalRevendeurs");
        }

        conn.close();
         nbrere.setText(nombreTotalRevendeurs+"");
    } catch (Exception e) {
        e.printStackTrace();
    }
    return nombreTotalRevendeurs;
}
// Méthode pour supprimer l'élément de la base de données par ID




    private void comboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboBoxItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_comboBoxItemStateChanged

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
      // Afficher un message de confirmation
    int confirmation = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment vous déconnecter ?", "Confirmation", JOptionPane.YES_NO_OPTION);
    
    // Vérifier la réponse de l'utilisateur
    if (confirmation == JOptionPane.YES_OPTION) {
        // Afficher un message de "au revoir"
        JOptionPane.showMessageDialog(this, "Au revoir !");
        
        LOGIN LoginFrame = new LOGIN();
        LoginFrame.setVisible(true);
        LoginFrame.pack();
        LoginFrame.setLocationRelativeTo(null); 
        this.dispose();
    }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void nameFieldAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_nameFieldAncestorAdded
        recherchenom();
    }//GEN-LAST:event_nameFieldAncestorAdded

    private void nameFieldAncestorMoved(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_nameFieldAncestorMoved
       recherchenom();
    }//GEN-LAST:event_nameFieldAncestorMoved

    private void nameFieldAncestorRemoved(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_nameFieldAncestorRemoved
        recherchenom();
    }//GEN-LAST:event_nameFieldAncestorRemoved

    private void dateFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_dateFieldPropertyChange
         if ("date".equals(evt.getPropertyName())) {
        rechercherParDate();
    }
    }//GEN-LAST:event_dateFieldPropertyChange

    private void COMBOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_COMBOActionPerformed
        
        createAndDisplayBarChart();
    }//GEN-LAST:event_COMBOActionPerformed

    private void supprimerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_supprimerActionPerformed
        int selectedRow = table.getSelectedRow();
    
    // Vérifiez si une ligne est sélectionnée
    if (selectedRow != -1) {
        // Obtenez l'identifiant de l'élément à supprimer à partir du modèle de tableau
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        int selectedId = (int) model.getValueAt(selectedRow, 0); // Supposons que la première colonne contient l'identifiant
        
        try {
            // Connexion à la base de données
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/matel", "root", "");

            // Requête SQL DELETE pour supprimer l'élément de la base de données par ID
            String query = "DELETE FROM donnees WHERE id = ?";
            
            // Préparer la requête SQL
            PreparedStatement pstmt = conn.prepareStatement(query);
            
            // Remplacer le paramètre "?" par l'ID de l'élément à supprimer
            pstmt.setInt(1, selectedId);
            
            // Exécuter la requête SQL DELETE
            pstmt.executeUpdate();
            
            // Fermer la connexion à la base de données
            conn.close();
            
            // Supprimez les données de la ligne sélectionnée dans le modèle de tableau
            model.removeRow(selectedRow);
            
            // Affichez un message de succès ou effectuez d'autres actions nécessaires
            JOptionPane.showMessageDialog(this, "L'élément a été supprimé avec succès de la base de données.");
        } catch (SQLException ex) {
            // Gérer les exceptions liées à la connexion ou à l'exécution de la requête
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Une erreur s'est produite lors de la suppression de l'élément de la base de données.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    } else {
        // Affichez un message indiquant à l'utilisateur de sélectionner une ligne avant de supprimer
        JOptionPane.showMessageDialog(this, "Veuillez sélectionner une ligne à supprimer.", "Avertissement", JOptionPane.WARNING_MESSAGE);
    }
    }//GEN-LAST:event_supprimerActionPerformed

    private void dategrapheAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_dategrapheAncestorAdded
      
    }//GEN-LAST:event_dategrapheAncestorAdded

    private void dategraphePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_dategraphePropertyChange
      if ("date".equals(evt.getPropertyName())) {
            // Recharge les données du graphique en fonction de la nouvelle date sélectionnée
            createAndDisplayBarChart();
        }
      
       if ("date".equals(evt.getPropertyName())) {
            // La propriété "date" a changé, mettre à jour le graphique
            displayBarChart(); // Mettre à jour le graphique
        }
    }//GEN-LAST:event_dategraphePropertyChange

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        rapport a = new rapport();
        a.setVisible(true);
       
        a.pack();
        a.setLocationRelativeTo(null); 
        this.dispose();    
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        bank d;
     d = new bank();
        d.setVisible(true);
       
        d.pack();
        d.setLocationRelativeTo(null); 
        this.dispose();      
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
       trans d;
     d = new trans();
        d.setVisible(true);
       
        d.pack();
        d.setLocationRelativeTo(null); 
        this.dispose();    
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
         index d;
     d = new index();
        d.setVisible(true);
       
        d.pack();
        d.setLocationRelativeTo(null); 
        this.dispose();    
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
      propos d;
     d = new  propos();
        d.setVisible(true);
       
        d.pack();
        d.setLocationRelativeTo(null); 
        this.dispose();   
    }//GEN-LAST:event_jButton7ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
       
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(index.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new index().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> COMBO;
    private javax.swing.JLabel COPYRIGHT;
    private javax.swing.JComboBox<String> comboBox;
    private com.toedter.calendar.JDateChooser dateField;
    private com.toedter.calendar.JDateChooser dategraphe;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField nameField;
    private javax.swing.JLabel nbrere;
    private javax.swing.JPanel panel;
    private javax.swing.JPanel panel1;
    private javax.swing.JPanel panelsomme;
    private javax.swing.JLabel soldemax;
    private javax.swing.JLabel soldeminimal;
    private javax.swing.JLabel sommelabel;
    private javax.swing.JButton supprimer;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}
