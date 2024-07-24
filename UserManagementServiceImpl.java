package biblioConnect_v3;

import java.sql.SQLException;
import java.util.List;

public class UserManagementServiceImpl implements UserManagementService {

    @Override
    public void registerUser(String name, String email, String username, String password, UserRole role) {
        User newUser = new User(name, email, username, password, role);
        try {
            DatabaseConnection.create(newUser);
        } catch (SQLException e) {
            throw new RuntimeException("Error registering user: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateUser(String username, String name, String email, String newUsername, String password) {
        try {
            User user = getUser(username);
            if (user != null) {
                if (!username.equals(newUsername)) {
                    User existingUser = getUser(newUsername);
                    if (existingUser != null) {
                        throw new RuntimeException("Username already exists. Please choose a different username.");
                    }
                }
                
                user.setName(name);
                user.setEmail(email);
                user.setUsername(newUsername);
                user.setPassword(password);
                DatabaseConnection.update(user);
            } else {
                throw new RuntimeException("User not found: " + username);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating user: " + e.getMessage(), e);
        }
    }

    @Override
    public User getUser(String username) {
        try {
            // System.out.println("UserManagementServiceImpl: Attempting to retrieve user with username: " + username);
            User user = DatabaseConnection.readUserByUsername(username);
            if (user == null) {
                // System.out.println("UserManagementServiceImpl: No user found with username: " + username);
            } else {
                // System.out.println("UserManagementServiceImpl: User found - ID: " + user.getUserId() + ", Role: " + user.getRole());
            }
            return user;
        } catch (SQLException e) {
            // System.out.println("UserManagementServiceImpl: Error getting user: " + e.getMessage());
            throw new RuntimeException("Error getting user: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean authenticateUser(String username, String password) {
        try {
            User user = DatabaseConnection.readUserByUsername(username);
            return user != null && user.getPassword().equals(password);
        } catch (SQLException e) {
            throw new RuntimeException("Error authenticating user: " + e.getMessage(), e);
        }
    }

    @Override
    public List<User> listAllMembers() {
        try {
            return DatabaseConnection.readAll(User.class);
        } catch (SQLException e) {
            throw new RuntimeException("Error listing all members: " + e.getMessage(), e);
        }
    }

    @Override
    public void removeMember(String username) {
        try {
            User user = getUser(username);
            if (user != null) {
                DatabaseConnection.deleteUser(user.getUserId());
            } else {
                throw new RuntimeException("User not found: " + username);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error removing member: " + e.getMessage(), e);
        }
    }
}