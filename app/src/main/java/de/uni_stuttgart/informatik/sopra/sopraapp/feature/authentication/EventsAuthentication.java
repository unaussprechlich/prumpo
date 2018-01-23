package de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication;

import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.User;


public class EventsAuthentication {

    private EventsAuthentication() {
        // no need to instantiate outer event
    }

    public static class Login {

    }

    public static class Logout {
        public final User user;

        public Logout(User user) {
            this.user = user;
        }
    }
}
