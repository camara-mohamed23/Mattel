/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package menu;

/**
 *
 * @author User
 */
public class classtrans {
    private int id;
   private double currentdate;
   private String tradate;	
   private String traid;	
   private double pinid;	
   private String groupname;
   private double accountname;
   private double username;	
   private double accountmobile;
   private double msisdn;
   private double access;	
   private String brandname;	
   private int amount;	
   private String status;	
   private double type;
   
 public classtrans(int id, double currentdate, String tradate, String traid, double pinid, String groupname,
                      double accountname, double username, double accountmobile, double msisdn, double access,
                      String brandname, int amount, String status, double type) {
        this.id = id;
        this.currentdate = currentdate;
        this.tradate = tradate;
        this.traid = traid;
        this.pinid = pinid;
        this.groupname = groupname;
        this.accountname = accountname;
        this.username = username;
        this.accountmobile = accountmobile;
        this.msisdn = msisdn;
        this.access = access;
        this.brandname = brandname;
        this.amount = amount;
        this.status = status;
        this.type = type;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getCurrentdate() {
        return currentdate;
    }

    public void setCurrentdate(double currentdate) {
        this.currentdate = currentdate;
    }

    public String getTradate() {
        return tradate;
    }

    public void setTradate(String tradate) {
        this.tradate = tradate;
    }

    public String getTraid() {
        return traid;
    }

    public void setTraid(String traid) {
        this.traid = traid;
    }

    public double getPinid() {
        return pinid;
    }

    public void setPinid(double pinid) {
        this.pinid = pinid;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public double getAccountname() {
        return accountname;
    }

    public void setAccountname(double accountname) {
        this.accountname = accountname;
    }

    public double getUsername() {
        return username;
    }

    public void setUsername(double username) {
        this.username = username;
    }

    public double getAccountmobile() {
        return accountmobile;
    }

    public void setAccountmobile(double accountmobile) {
        this.accountmobile = accountmobile;
    }

    public double getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(double msisdn) {
        this.msisdn = msisdn;
    }

    public double getAccess() {
        return access;
    }

    public void setAccess(double access) {
        this.access = access;
    }

    public String getBrandname() {
        return brandname;
    }

    public void setBrandname(String brandname) {
        this.brandname = brandname;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getType() {
        return type;
    }

    public void setType(double type) {
        this.type = type;
    }

    // toString method
    @Override
    public String toString() {
        return "classtrans{" +
                "id=" + id +
                ", currentdate=" + currentdate +
                ", tradate='" + tradate + '\'' +
                ", traid='" + traid + '\'' +
                ", pinid=" + pinid +
                ", groupname='" + groupname + '\'' +
                ", accountname=" + accountname +
                ", username=" + username +
                ", accountmobile=" + accountmobile +
                ", msisdn=" + msisdn +
                ", access=" + access +
                ", brandname='" + brandname + '\'' +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                ", type=" + type +
                '}';
    }
}


