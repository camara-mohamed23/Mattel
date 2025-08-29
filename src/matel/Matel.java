 package matel;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import login.LOGIN;


public class Matel {

    public Matel (){
       
        
         
        
    }

   
    public static void main(String[] args) {
        
        LOGIN L= new LOGIN();
        L.setVisible(true);
        L.pack();
         L.setLocationRelativeTo(null); 
       
         JPanel panel = new JPanel(new BorderLayout());
         L.setExtendedState(JFrame.MAXIMIZED_BOTH);
        

                // Ajoutez vos composants au panel ici

               // L.getContentPane().add(panel);
    }    
}
