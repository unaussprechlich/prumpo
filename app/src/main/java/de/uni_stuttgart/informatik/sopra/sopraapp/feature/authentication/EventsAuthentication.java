package de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication;

import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.UserEntity;


public class EventsAuthentication {

    private EventsAuthentication() {
        // no need to instantiate outer event
    }

    public static class Login {
        public final UserEntity userEntity;

        public Login(UserEntity userEntity) {
            this.userEntity = userEntity;
        }
    }

    public static class Logout {
        public final UserEntity userEntity;

        public Logout(UserEntity userEntity) {
            this.userEntity = userEntity;
        }
    }
}
