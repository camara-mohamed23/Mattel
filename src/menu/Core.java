/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package menu;

import java.util.Date;


public class Core {
    private int id;
    private String dateHeure;
    private Date datec;
    private String nomV1;
    private String nomV2;
    private int numero;
    private String groupe;
    private String type;
    private double solde;

    // Constructeur

    /**
     *
     * @param ID
     * @param dateHeure
     * @param numero
     * @param nomV1
     * @param nomV2
     * @param datec
     * 
     * @param type
     * @param groupe
     * @param solde
     */
    public Core(int ID , String dateHeure, Date datec, String nomV1,String nomV2 , int numero,String groupe, String type, double solde) {
        this.id =ID;
        this.dateHeure = dateHeure;
        this.datec = datec;
        this.nomV1 = nomV1;
        this.nomV2 = nomV2;
        this.numero = numero;
        this.groupe = groupe;
        this.type = type;
        this.solde = solde;
    }

  

    // Getters et setters

    /**
     *
     */
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

    public Date getDatec() {
        return datec;
    }

    public void setDatec(Date datec) {
        this.datec = datec;
    }

    public String getNomV1() {
        return nomV1;
    }

    public void setNomV1(String nomV1) {
        this.nomV1 = nomV1;
    }

    public String getNomV2() {
        return nomV2;
    }

    public void setNomV2(String nomV2) {
        this.nomV2 = nomV2;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getGroupe() {
        return groupe;
    }

    public void setGroupe(String groupe) {
        this.groupe = groupe;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getSolde() {
        return solde;
    }

    public void setSolde(double solde) {
        this.solde = solde;
    }

    
}

  
