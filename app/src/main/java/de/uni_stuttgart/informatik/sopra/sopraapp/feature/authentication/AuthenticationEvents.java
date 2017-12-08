package de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication;

import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.user.User;


public class AuthenticationEvents {

    public static class Login{
        public final User user;

        public Login(User user) {
            this.user = user;
        }
    }

    public static class Logout{
        public final User user;

        public Logout(User user) {
            this.user = user;
        }
    }
}
