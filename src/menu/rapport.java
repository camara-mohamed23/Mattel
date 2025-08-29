/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package menu;
import com.sun.jdi.connect.spi.Connection;
import com.toedter.calendar.JDateChooser;
import java.awt.Color;
import java.awt.Font;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableModel;
import org.apache.pdfbox.pdmodel.PDDocument;
import login.LOGIN; // Assurez-vous que cette importation est nécessaire
/**
 *
 * @author User
 */
public class rapport extends javax.swing.JFrame {
        
    /**
     * Creates new form rapport
     */
    public rapport() {
         
        initComponents();
        recherchenom();
       
      
         JTableHeader header = table.getTableHeader();
         // Personnalisez l'apparence de l'en-tête
    header.setBackground(Color.lightGray);
    header.setForeground(Color.black);
    header.setFont(new Font("SansSerif", Font.BOLD, 16));
        COPYRIGHT.setText("\u00A9 Mouhamed Camara - Tous droits réservés");
// Maximiser la JFrame pour occuper tout l'écran
setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
    private byte[] readPdfFromFile(String filePath) throws IOException {
    Path path = Paths.get(filePath);
    return Files.readAllBytes(path);
}
     public java.sql.Connection getConnection()
    {
        java.sql.Connection con = null;
        
        try{
            con = DriverManager.getConnection("jdbc:MySQL://localhost:3306/matel","root","");
        }catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
        
        return con;
    }
     
     public ArrayList<Rap> listRapports() {
        ArrayList<Rap> rapportsList = new ArrayList<>();
        
        try {
           java.sql.Connection con = getConnection();
            java.sql.Statement st = con.createStatement();
            String searchQuery = "SELECT * FROM rapport";
            ResultSet rs = st.executeQuery(searchQuery);
            
            while (rs.next()) {
                Rap rapport = new Rap(
                    rs.getInt("id"),
                    rs.getString("dateHeure"),
                    rs.getString("pdf")
                );
                rapportsList.add(rapport);
            }
            
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        
        return rapportsList;
    }
    public void recherchenom()
    {
       DefaultTableModel model = new DefaultTableModel();
    model.setColumnIdentifiers(new Object[]{"id","Année", "Rapport"});
    
    try {
        java.sql.Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/matel", "root", "");
       
        String query = "SELECT * FROM rapport";
        java.sql.Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            Object[] row = new Object[3];
            row[0] = rs.getObject("id");
            row[1] = rs.getObject("date");
            row[2] = rs.getObject("pdf");
            
            model.addRow(row);
        }
        
        con.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
    table.setDefaultRenderer(Object.class, new PdfIconRenderer());
    int yearColumnIndex = 1; // Indice de la colonne Année dans votre modèle de table
    table.getColumnModel().getColumn(yearColumnIndex).setCellRenderer(new YearRenderer());

    table.setModel(model);
   // table.getColumn("Rapport").setCellRenderer(new PdfIconRenderer());
       
    }
    
    
    
    
    
    
    
      private void downloadPdf(int id) throws FileNotFoundException, IOException {
     
          try {
        // Établir la connexion à la base de données
              java.sql.Connection con = getConnection();

        // Préparer la requête SQL pour récupérer le PDF en tant que BLOB
        String query = "SELECT pdf FROM rapport WHERE id = ?";
        PreparedStatement pst = con.prepareStatement(query);
        pst.setInt(1, id);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            // Récupérer les données du BLOB PDF
            byte[] pdfData = rs.getBytes("pdf");

            if (pdfData == null || pdfData.length == 0) {
                JOptionPane.showMessageDialog(this, "Le PDF est vide ou n'a pas été trouvé.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Sélectionner l'emplacement de téléchargement
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File("Rapport.pdf"));
            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();

                // Écrire les données du PDF dans le fichier local
                try (FileOutputStream fos = new FileOutputStream(fileToSave)) {
                    fos.write(pdfData);
                    fos.flush(); // Assurez-vous que toutes les données sont écrites
                    System.out.println("PDF a été écrit dans le fichier : " + fileToSave.getAbsolutePath());
                }

                // Afficher les premiers octets pour vérifier le contenu
                System.out.println("Premiers octets du fichier PDF : " + Arrays.toString(Arrays.copyOf(pdfData, 20)));

                // Lire le fichier pour vérifier le contenu
                try (FileInputStream fis = new FileInputStream(fileToSave);
                     BufferedInputStream bis = new BufferedInputStream(fis)) {

                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = bis.read(buffer)) != -1) {
                        // Afficher le contenu lu (pour le débogage, cela peut être commenté plus tard)
                        System.out.write(buffer, 0, bytesRead);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Vérification de la validité du fichier PDF (optionnel)
                if (fileToSave.exists() && fileToSave.length() > 0) {
                    System.out.println("Le fichier PDF a été vérifié et existe à l'emplacement : " + fileToSave.getAbsolutePath());
                } else {
                    System.out.println("Le fichier PDF semble vide ou n'a pas été correctement enregistré.");
                }

                // Informer l'utilisateur que le téléchargement est complet
                JOptionPane.showMessageDialog(this, "rapport téléchargé avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Aucun PDF trouvé pour l'ID spécifié.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }

    } catch (SQLException | IOException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Erreur lors du téléchargement du PDF : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
    }
                }
private void saveData() throws IOException {
        // Récupérer la date sélectionnée et le chemin du fichier PDF
          Date selectedDate = date.getDate();
    String pdfPath = pdf.getText();
    if (selectedDate == null || pdfPath.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Veuillez sélectionner une date et un fichier PDF.", "Erreur", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Convertir la date en chaîne de caractères
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String dateStr = sdf.format(selectedDate);

    // Lire le contenu binaire du fichier PDF
    byte[] pdfBytes = Files.readAllBytes(Path.of(pdfPath));

    // Connexion à la base de données
    String jdbcURL = "jdbc:mysql://localhost:3306/matel";
    String dbUser = "root";
    String dbPassword = "password"; // Remplacez par votre mot de passe de base de données

    String sql = "INSERT INTO rapport (date, pdf) VALUES (?, ?)";

    try (java.sql.Connection connection = getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {
        
        statement.setString(1, dateStr);
        statement.setBytes(2, pdfBytes); // Enregistrer le contenu binaire du PDF

        int rowsInserted = statement.executeUpdate();
        if (rowsInserted > 0) {
            JOptionPane.showMessageDialog(this, "Rapport est enregistré.", "Succès", JOptionPane.INFORMATION_MESSAGE);
        }
          rapport b = new rapport();
                
                b.setVisible(true);
                b.pack();
                b.setLocationRelativeTo(null); 
                this.dispose();

    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Erreur lors de l'enregistrement de rapport.", "Erreur", JOptionPane.ERROR_MESSAGE);
    }
}

 


//
 
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        date = new com.toedter.calendar.JDateChooser();
        jLabel2 = new javax.swing.JLabel();
        pdf = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        enregistrer = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        COPYRIGHT = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jScrollPane1.setPreferredSize(new java.awt.Dimension(1215, 1077));

        jPanel1.setPreferredSize(new java.awt.Dimension(1213, 1355));

        jPanel2.setBackground(new java.awt.Color(0, 153, 153));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setIcon(new javax.swing.ImageIcon("M:\\ICONE\\mattel.png")); // NOI18N
        jLabel7.setText("gestion rapport");

        jButton5.setBackground(new java.awt.Color(0, 153, 153));
        jButton5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton5.setForeground(new java.awt.Color(255, 255, 255));
        jButton5.setText("Accueil");
        jButton5.setBorder(null);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setBackground(new java.awt.Color(0, 153, 153));
        jButton6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton6.setForeground(new java.awt.Color(255, 255, 255));
        jButton6.setText("Gestion transaction");
        jButton6.setBorder(null);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setBackground(new java.awt.Color(0, 153, 153));
        jButton7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton7.setForeground(new java.awt.Color(255, 255, 255));
        jButton7.setText("Transaction bancaire");
        jButton7.setBorder(null);
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setBackground(new java.awt.Color(0, 153, 153));
        jButton8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton8.setForeground(new java.awt.Color(255, 255, 255));
        jButton8.setText("Gestion rapport");
        jButton8.setBorder(null);
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton9.setBackground(new java.awt.Color(0, 153, 153));
        jButton9.setIcon(new javax.swing.ImageIcon("M:\\ICONE\\D.png")); // NOI18N
        jButton9.setBorder(null);
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButton1.setBackground(new java.awt.Color(0, 153, 153));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("à propos");
        jButton1.setBorder(null);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(281, 281, 281)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton9)
                .addGap(49, 49, 49))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton9)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(48, Short.MAX_VALUE))
        );

        jLabel1.setIcon(new javax.swing.ImageIcon("M:\\ICONE\\rpp.jpg")); // NOI18N

        table.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        table.setForeground(new java.awt.Color(0, 153, 153));
        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Année ", "Rapport"
            }
        ));
        table.setGridColor(new java.awt.Color(204, 204, 204));
        table.setRowHeight(35);
        table.setSelectionBackground(new java.awt.Color(0, 153, 153));
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(true);
        jScrollPane2.setViewportView(table);

        jPanel3.setBackground(new java.awt.Color(0, 153, 153));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        date.setDateFormatString("yyyy-MM-dd");
        date.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel2.setText("Ajouter");

        pdf.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        pdf.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        pdf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pdfActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setIcon(new javax.swing.ImageIcon("M:\\ICONE\\date.png")); // NOI18N
        jLabel5.setText("L'année de rapport");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setIcon(new javax.swing.ImageIcon("M:\\ICONE\\RAPPORT.png")); // NOI18N
        jLabel6.setText("Selectionner le rapport");

        enregistrer.setBackground(new java.awt.Color(0, 153, 153));
        enregistrer.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        enregistrer.setForeground(new java.awt.Color(255, 255, 255));
        enregistrer.setIcon(new javax.swing.ImageIcon("M:\\ICONE\\enregistrer.png")); // NOI18N
        enregistrer.setText("Enregistrer");
        enregistrer.setBorder(null);
        enregistrer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enregistrerActionPerformed(evt);
            }
        });

        jButton4.setIcon(new javax.swing.ImageIcon("M:\\ICONE\\S.png")); // NOI18N
        jButton4.setBorder(null);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(date, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(70, 70, 70)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(pdf, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton4))
                            .addComponent(jLabel6))
                        .addContainerGap(73, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(187, 187, 187))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                .addComponent(enregistrer, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(149, 149, 149))))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(7, 7, 7)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(date, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pdf, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(enregistrer, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton2.setIcon(new javax.swing.ImageIcon("M:\\ICONE\\supprimer.png")); // NOI18N
        jButton2.setText("SUPPRIMER");
        jButton2.setBorder(null);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setIcon(new javax.swing.ImageIcon("M:\\ICONE\\filtre.png")); // NOI18N
        jLabel4.setText("Filtre");

        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton3.setIcon(new javax.swing.ImageIcon("M:\\ICONE\\TELE.png")); // NOI18N
        jButton3.setText("télécharger");
        jButton3.setBorder(null);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 334, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(152, 152, 152)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(17, 17, 17))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel4)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setIcon(new javax.swing.ImageIcon("M:\\ICONE\\tableau.png")); // NOI18N
        jLabel3.setText("Table de Données");

        jPanel5.setBackground(new java.awt.Color(0, 153, 153));

        COPYRIGHT.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(COPYRIGHT, javax.swing.GroupLayout.PREFERRED_SIZE, 481, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(415, 415, 415))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(25, Short.MAX_VALUE)
                .addComponent(COPYRIGHT, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 26, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 369, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 405, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jScrollPane1.setViewportView(jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1367, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1258, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
          int selectedRow = table.getSelectedRow();
    
    // Vérifiez si une ligne est sélectionnée
    if (selectedRow != -1) {
        // Obtenez l'identifiant de l'élément à supprimer à partir du modèle de tableau
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        int selectedId = (int) model.getValueAt(selectedRow, 0); // Supposons que la première colonne contient l'identifiant
        
        try {
            // Connexion à la base de données
            java.sql.Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/matel", "root", "");

            // Requête SQL DELETE pour supprimer l'élément de la base de données par ID
            String query = "DELETE FROM rapport WHERE id = ?";
            
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
            JOptionPane.showMessageDialog(this, "rapport est supprimé avec succès .");
             rapport b = new rapport();
                
                b.setVisible(true);
                b.pack();
                b.setLocationRelativeTo(null); 
                this.dispose();
        } catch (SQLException ex) {
            // Gérer les exceptions liées à la connexion ou à l'exécution de la requête
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Une erreur s'est produite lors de la suppression .", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    } else {
        // Affichez un message indiquant à l'utilisateur de sélectionner une ligne avant de supprimer
        JOptionPane.showMessageDialog(this, "Veuillez sélectionner un rapport à supprimer.", "Avertissement", JOptionPane.WARNING_MESSAGE);
    }

    }//GEN-LAST:event_jButton2ActionPerformed

    private void pdfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pdfActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_pdfActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
 int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) table.getValueAt(selectedRow, 0);
                try {
                    downloadPdf(id);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un rapport.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }        
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        index a = new index ();
        a.setVisible(true);
       
        a.pack();
        a.setLocationRelativeTo(null); 
        this.dispose();    
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        trans d;
     d = new trans();
        d.setVisible(true);
       
        d.pack();
        d.setLocationRelativeTo(null); 
        this.dispose();  
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        bank d;
     d = new bank();
        d.setVisible(true);
       
        d.pack();
        d.setLocationRelativeTo(null); 
        this.dispose();    
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
          rapport d;
     d = new  rapport();
        d.setVisible(true);
       
        d.pack();
        d.setLocationRelativeTo(null); 
        this.dispose();    
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
     
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
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
       JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Sélectionner un fichier PDF");
                fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF Files", "pdf"));

                // Afficher le sélecteur de fichiers
                int userSelection = fileChooser.showOpenDialog(null);

                // Si l'utilisateur sélectionne un fichier
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    // Afficher le chemin du fichier dans le champ de saisie
                    pdf.setText(selectedFile.getAbsolutePath());
                
                    
    }
            
    }//GEN-LAST:event_jButton4ActionPerformed

    private void enregistrerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enregistrerActionPerformed
        try {
            saveData();
        } catch (IOException ex) {
            Logger.getLogger(rapport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_enregistrerActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
         propos d;
     d = new  propos();
        d.setVisible(true);
       
        d.pack();
        d.setLocationRelativeTo(null); 
        this.dispose();    
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(rapport.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(rapport.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(rapport.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(rapport.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new rapport().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel COPYRIGHT;
    private com.toedter.calendar.JDateChooser date;
    private javax.swing.JButton enregistrer;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField pdf;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}
