/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package menu;

import java.util.Date;

/**
 *
 * @author User
 */
public class Rap {
    private int id;
    private String dateHeure;
    private String pdf;

public Rap(int id , String dateHeure, String pdf){
    this.id = id;   
    this.dateHeure = dateHeure;
    this.pdf = pdf ;

}

 public int getid(){
        return id;
    }

    public void setid(int id) {
        this.id= id;
    }
    
    
    public String getDateHeure() {
        return dateHeure;
    }

    public void setDateHeure(String dateHeure) {
        this.dateHeure = dateHeure;
    }

    public String getpdf() {
        return pdf;
    }

    public void setpdf(String pdf) {
        this.pdf = pdf;
    }
}