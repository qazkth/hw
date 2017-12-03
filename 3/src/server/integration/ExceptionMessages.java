/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.integration;

/**
 *
 * @author Oscar
 */
class ExceptionMessages {
    static final String NAME_OCCUPIED = "The username is already taken, try againg with a new one. Entered username: ";
    static final String INVALID_CREDS = "Invalid login credentials";
    static final String FAILED_USER_CREATE = "Failed to create a new user";
    static final String DB_COM_ERROR = "Something went wrong communicating with the DB, try again or contanct server support.";
    static final String NO_SUCH_FILE = "The file with the sepcified name doesn't exist on the server... Entered filename: ";
    static final String UNKOWN_ERROR = "We don't know what went wrong. Try again...";
    static final String FAILED_LIST_FETCH = "Something went wrong trying to fetch the lists";
    static final String YOU_NOT_BE_HERE = "You're not supposed to be here..... GET OUT!";
    static final String FILE_EXISTS = "There is already a file in the catalog with that name. Enterd filename: ";
}
