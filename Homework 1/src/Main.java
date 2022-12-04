import bg.sofia.uni.fmi.mjt.mail.MailClient;
import bg.sofia.uni.fmi.mjt.mail.Outlook;

public class Main {
    public static void main(String[] args) {

        MailClient client = new Outlook();

        client.addNewAccount("Stef4o", "stefan@abv.bg");
        client.addNewAccount("Roskata", "rosen123@abv.bg");
    }
}