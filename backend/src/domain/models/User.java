package domain.models;

public class User {
    private final String id;
    private final String username;
    private final String password; // plain for demo; hash in real app
    private final String role;

    public User(String id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
}
