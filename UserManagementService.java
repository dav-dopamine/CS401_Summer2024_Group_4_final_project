package biblioConnect_v3;

import java.util.List;

public interface UserManagementService {
    /**
     * Registers a new user in the system.
     * Pre-condition: All parameters are non-null, username is unique
     * Post-condition: A new user is created in the system
     */
    void registerUser(String name, String email, String username, String password, UserRole role);

    /**
     * Updates an existing user's information.
     * Pre-condition: username exists in the system, all parameters are non-null
     * Post-condition: User information is updated in the system
     */
    void updateUser(String username, String name, String email, String newUsername, String password);

    /**
     * Retrieves a user by their username.
     * Pre-condition: username is non-null
     * Post-condition: Returns the User object if found, null otherwise
     */
    User getUser(String username);

    /**
     * Authenticates a user.
     * Pre-condition: username and password are non-null
     * Post-condition: Returns true if authentication is successful, false otherwise
     */
    boolean authenticateUser(String username, String password);

    /**
     * Retrieves a list of all users in the system.
     * Pre-condition: None
     * Post-condition: Returns a list of all User objects in the system
     */
    List<User> listAllMembers();

    /**
     * Removes a user from the system.
     * Pre-condition: username exists in the system
     * Post-condition: User is removed from the system
     */
    void removeMember(String username);
}