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
public class bankclass {
     private int id;
    private String nom;
    private double numero;
   

public bankclass(int id , String nom , double numero){
this.id = id;
this.nom = nom;
this.numero=numero;

}

 public int getid(){
        return id;
    }

    public void setid(int id) {
        this.id= id;
    }
    
 public String getnom(){
        return nom;
    }

    public void setnom(String nom) {
        this.nom= nom;
    }
    

public double getnumero(){
        return numero;
    }

    public void setnumero(double numero) {
        this.numero= numero;
    }
}