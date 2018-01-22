package de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user;

public class CurrentUser {

    private static User currentUser = null;

    static void set(User currentUser) {
        CurrentUser.currentUser = currentUser;
    }

    public static User get() throws NoUserException{
        if(currentUser == null){
            throw new NoUserException();
        }else return currentUser;
    }
}
