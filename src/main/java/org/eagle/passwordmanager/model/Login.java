package org.eagle.passwordmanager.model;

import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Callback;

import java.util.Objects;

public final class Login {
    private int loginId;
    private final StringProperty name = new SimpleStringProperty();
    private String userName = "";
    private String password = "";
    private String URL = "";
    private String notes = "";

    // constructor that takes one parameter
    public Login(String name) {
        this.name.set(name);
    }

    public Login(int loginId,String name, String userName, String password, String URL, String notes) {
        this.loginId = loginId;
        this.name.set(name);
        this.userName = userName;
        this.password = password;
        this.URL = URL;
        this.notes = notes;
    }
    // Getters and Setters


    public int getLoginId() {
        return loginId;
    }

    public void setLoginId(int loginId) {
        this.loginId = loginId;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getUserName() {
        return userName;
    }


    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getURL() {
        return URL;
    }


    public void setURL(String URL) {
        if (URL.isEmpty() || URL.isBlank()) {
            return;
        }

        if (URL.startsWith("http://") || URL.startsWith("https://")) {

            this.URL = URL;
        } else {
            this.URL = "https://" + URL;
        }


    }

    public String getNotes() {
        return notes;
    }


    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Login login = (Login) o;

        if (!name.equals(login.name)) return false;
        if (!Objects.equals(userName, login.userName)) return false;
        if (!Objects.equals(password, login.password)) return false;
        if (!Objects.equals(URL, login.URL)) return false;
        return Objects.equals(notes, login.notes);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (URL != null ? URL.hashCode() : 0);
        result = 31 * result + (notes != null ? notes.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return name.get();
    }

    public static Callback<Login, Observable[]> extractor = param -> new Observable[]{
            param.nameProperty()
    };
}
