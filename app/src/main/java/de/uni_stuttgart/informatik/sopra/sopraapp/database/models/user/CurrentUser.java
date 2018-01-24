package de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user;

public class CurrentUser {

    private static UserEntity currentUserEntity = null;

    static void set(UserEntity currentUserEntity) {
        CurrentUser.currentUserEntity = currentUserEntity;
    }

    public static UserEntity get() throws NoUserException{
        if(currentUserEntity == null){
            throw new NoUserException();
        }else return currentUserEntity;
    }
}
