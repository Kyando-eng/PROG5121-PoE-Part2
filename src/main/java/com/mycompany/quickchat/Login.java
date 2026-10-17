package com.mycompany.quickchat;
 
import java.util.regex.Pattern;
 
/**

* Handles registration and login (Part 1).

*/

public class Login {
 
    private String username;

    private String password;

    private String cellPhoneNumber;

    private String firstName;

    private String lastName;
 
    public Login() {

    }
 
    // Username must contain an underscore and be no more than five characters

    public boolean checkUserName(String username) {

        if (username == null) {

            return false;

        }

        return username.contains("_") && username.length() <= 5;

    }
 
    // Password: 8+ characters, a capital letter, a number and a special character

    public boolean checkPasswordComplexity(String password) {

        if (password == null || password.length() < 8) {

            return false;

        }

        boolean hasCapital = password.chars().anyMatch(Character::isUpperCase);

        boolean hasNumber = password.chars().anyMatch(Character::isDigit);

        boolean hasSpecial = password.chars().anyMatch(c -> !Character.isLetterOrDigit(c));
 
        return hasCapital && hasNumber && hasSpecial;

    }
 
    // Cell number: international code (+) followed by the number

    // TODO: add your reference for this regex here (source, year, URL, date accessed)

    public boolean checkCellPhoneNumber(String cellPhoneNumber) {

        if (cellPhoneNumber == null) {

            return false;

        }

        String regex = "^\\+\\d{11,12}$";

        return Pattern.matches(regex, cellPhoneNumber);

    }
 
    public String registerUser(String username, String password, String cellPhoneNumber,

                                String firstName, String lastName) {
 
        boolean usernameOk = checkUserName(username);

        boolean passwordOk = checkPasswordComplexity(password);

        boolean cellOk = checkCellPhoneNumber(cellPhoneNumber);
 
        StringBuilder message = new StringBuilder();
 
        if (!usernameOk) {

            message.append("Username is not correctly formatted; please ensure that your username "

                    + "contains an underscore and is no more than five characters in length.");

        }
 
        if (!passwordOk) {

            if (message.length() > 0) {

                message.append(" ");

            }

            message.append("Password is not correctly formatted; please ensure that the password "

                    + "contains at least eight characters, a capital letter, a number, and a special character.");

        }
 
        if (!cellOk) {

            if (message.length() > 0) {

                message.append(" ");

            }

            message.append("Cell phone number is incorrectly formatted or does not contain "

                    + "international code, please correct the number and try again.");

        }
 
        if (usernameOk && passwordOk && cellOk) {

            this.username = username;

            this.password = password;

            this.cellPhoneNumber = cellPhoneNumber;

            this.firstName = firstName;

            this.lastName = lastName;
 
            message.append("Username successfully captured. Password successfully captured. "

                    + "Cell phone number successfully captured. User registered successfully.");

        }
 
        return message.toString();

    }
 
    public boolean loginUser(String username, String password) {

        if (this.username == null || this.password == null) {

            return false;

        }

        return this.username.equals(username) && this.password.equals(password);

    }
 
    public String returnLoginStatus(boolean loginSuccessful) {

        if (loginSuccessful) {

            return "Welcome " + firstName + " " + lastName + ", it is great to see you again.";

        } else {

            return "Username or password incorrect, please try again.";

        }

    }
 
    public String getFirstName() {

        return firstName;

    }
 
    public String getLastName() {

        return lastName;

    }

}
 