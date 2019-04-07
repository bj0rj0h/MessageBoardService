package se.bjorjoh.models;

import com.fasterxml.jackson.annotation.JsonProperty;


public class JwtContent {

    private String iss;
    private int iat;
    private int exp;
    private String aud;
    private String sub;
    @JsonProperty("GivenName")
    private String GivenName;
    @JsonProperty("Surname")
    private String Surname;
    private String Email;


    public JwtContent(@JsonProperty("iss") String iss,@JsonProperty("iat") int iat,@JsonProperty("exp") int exp,@JsonProperty("aud") String aud,@JsonProperty("sub") String sub, @JsonProperty("GivenName")String givenName, @JsonProperty("Surname")String surname,
                      @JsonProperty(required = true,value = "Email") String email) {
        this.iss = iss;
        this.iat = iat;
        this.exp = exp;
        this.aud = aud;
        this.sub = sub;
        GivenName = givenName;
        Surname = surname;
        Email = email;
    }

    public java.lang.String getIss() {
        return iss;
    }

    public void setIss(java.lang.String iss) {
        this.iss = iss;
    }

    public int getIat() {
        return iat;
    }

    public void setIat(int iat) {
        this.iat = iat;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public java.lang.String getAud() {
        return aud;
    }

    public void setAud(java.lang.String aud) {
        this.aud = aud;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getGivenName() {
        return GivenName;
    }

    public void setGivenName(String givenName) {
        GivenName = givenName;
    }

    public String getSurname() {
        return Surname;
    }

    public void setSurname(String surname) {
        Surname = surname;
    }

    public String getEmail() {
        return Email;
    }

    @JsonProperty(required = true)
    public void setEmail(String email) {
        Email = email;
    }
}
