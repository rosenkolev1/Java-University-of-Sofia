import bg.sofia.uni.fmi.mjt.mail.MailClient;
import bg.sofia.uni.fmi.mjt.mail.Outlook;

public class Main {
    public static void main(String[] args) {

        final String newLine = System.getProperty("line.separator");

        MailClient client = new Outlook();

        client.addNewAccount("Stefan", "stefan@abv.bg");
        client.addNewAccount("Roskata123", "rosen123@abv.bg");

        client.createFolder("Roskata123", "/Important");
        client.createFolder("Roskata123", "/Spam");

        String rule1Definition = "subject-includes: mjt, izpit, 2022" + newLine +
            "subject-or-body-includes: izpit" + newLine +
            "from: stoyo@fmi.bg";
        int rule1Priority = 1;

        client.addRule("Roskata123", "/Important", rule1Definition, rule1Priority);
        //-> Works

        String rule2Definition = "recipients-includes: roskata123@abv.bg, stefan@abv.bg" + newLine +
            "subject-or-body-includes: izpit" + newLine +
            "subject-includes: StoyoIsMaddd, Something else";
        int rule2Priority = 3;

        client.addRule("Roskata123", "/Spam", rule2Definition, rule2Priority);
        //-> Works

//        String rule1Definition = "subject-includes: mjt, izpit, 2022" + newLine +
//            "subject-or-body-includes: izpit" + newLine +
//            "from: stoyo@fmi.bg";
//        int rule1Priority = 1;
//
//        client.addRule("Roskata123", "/ImportantSpam", rule1Definition, rule1Priority);
//        //-> Error because folder doesn't exist

//        String rule1Definition = "subject-includes: mjt, izpit, 2022" + newLine +
//            "subject-or-body-includes: izpit" + newLine +
//            "from: stoyo@fmi.bg" + newLine +
//            "subject-includes: Java Course, someOtherKeyword";
//        int rule1Priority = 1;
//
//        client.addRule("Roskata123", "/Important", rule1Definition, rule1Priority);
//        //-> Error because subject-includes is met more than once as a rule condition

//        client.createFolder("Roskata123", "Important"); //->Error because not in root
//        client.createFolder("Roskata123", "/Important/Documents");
        //->Error because subdir 'Important' doesn't exist
//        client.createFolder("Roskata123", "/Important"); //->works
//        client.createFolder("Roskata123", "/Important/Documents"); //->works
//        client.createFolder("Roskata123", "/Important/Documents/PDF/Notes");
        //->Error because subdir 'Important/Documents/PDF' doesn't exist
//        client.createFolder("Roskata123", "/Important/Documents");
        //->Error because this folder already exists
//        client.createFolder("Roskata123", "/Spam");
        //->Error because account doesn't exist
//        client.createFolder("Roskata123", "/Important//");
        //->Error because this folder already exists
//        client.createFolder("Roskata123", "/Important//");
        //->Error because this folder already exists
//        client.createFolder("Roskata123", "/");
        //->Error because this folder already exists
    }
}