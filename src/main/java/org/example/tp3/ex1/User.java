package org.example.tp3.ex1;
public class User {
    private long id;
    private String name;
    private String email;
    public User(long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
    public long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "', email='" + email + "'}";
    }
}
