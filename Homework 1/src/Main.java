import bg.sofia.uni.fmi.mjt.mail.Mail;
import bg.sofia.uni.fmi.mjt.mail.MailClient;
import bg.sofia.uni.fmi.mjt.mail.Outlook;

import java.util.Collection;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        final String newLine = System.getProperty("line.separator");

        MailClient client = new Outlook();

        client.addNewAccount("Stefan", "stefan@abv.bg");
        client.addNewAccount("Roskata123", "rosen123@abv.bg");
        client.addNewAccount("vajenRecipient", "vr@abv.bg");
        client.addNewAccount("malovajenRecipient", "mvr@abv.bg");

        client.createFolder("Roskata123", "/inbox/Important");
        client.createFolder("Roskata123", "/inbox/Important/SUPER_IMPORTANT");
        client.createFolder("Roskata123", "/inbox/Spam");
        client.createFolder("vajenRecipient", "/inbox/Spam");
//        subject-includes: <list-of-keywords>
//     * subject-or-body-includes: <list-of-keywords>
//     * recipients-includes: <list-of-recipient-emails>
//     * from: <sender-email>
        String ruleDefinition1 = "subject-includes: voda" + newLine +
            "subject-or-body-includes: voda vajno" + newLine +
            "recipients-includes: vr@abv.bg" + newLine +
            "from: stefan@abv.bg";

        String ruleDefinition2 = "subject-includes: ne6to";

        String ruleDefinition3 = "subject-includes: voda, ne6to" + newLine +
            "subject-or-body-includes: voda vajno, Absolutno" + newLine +
            "recipients-includes: stefan@abv.bg, vr@abv.bg" + newLine +
            "from: stefan@abv.bg";

        String ruleDefinition4 = "subject-or-body-includes: Absolutno";

        client.addRule("Roskata123", "/inbox/Important", ruleDefinition1, 3);
        client.addRule("Roskata123", "/inbox/Spam", ruleDefinition2, 4);
        client.addRule("vajenRecipient", "/inbox/Spam", ruleDefinition2, 4);
        client.addRule("Roskata123", "/inbox/Important/SUPER_IMPORTANT", ruleDefinition3, 2);

//        sender: <sender-email>
//     * subject: <subject>
//     * recipients: <list-of-emails>
//     * received: <LocalDateTime> - in format yyyy-MM-dd HH:mm
        String emailMetadata1 = "sender: stefan@abv.bg" + newLine +
            "subject: ni6to zna4itelno" + newLine +
            "recipients: vr@abv.bg, rosen123@abv.bg" + newLine +
            "received: 2022-12-03 12:00";
        String emailContent1 = "Absolutno ni6to voda vajno nqma v tozi email";

        client.sendMail("Stefan", emailMetadata1, emailContent1);

        String emailMetadata2 = "sender: stefan@abv.bg" + newLine +
            "subject: ne6to voda" + newLine +
            "recipients: vr@abv.bg, rosen123@abv.bg" + newLine +
            "received: 2022-12-03 12:00";
        String emailContent2 = "Absolutno ni6to voda vajno nqma vutre v tozi email";

        client.sendMail("Stefan", emailMetadata2, emailContent2);

        Collection<Mail> importantEmails = client.getMailsFromFolder("Roskata123", "/inbox/Important");
        Collection<Mail> superImportantEmails = client.getMailsFromFolder("Roskata123",
            "/inbox/Important/SUPER_IMPORTANT");
        Collection<Mail> spamEmails = client.getMailsFromFolder("Roskata123", "/inbox/Spam");
        Collection<Mail> inboxEmails = client.getMailsFromFolder("Roskata123", "/inbox");
        Collection<Mail> inboxEmailsVajenRecipient = client.getMailsFromFolder("vajenRecipient", "/inbox");
        Collection<Mail> spamEmailsVajenRecipient = client.getMailsFromFolder("vajenRecipient", "/inbox/Spam");

        client.addRule("Roskata123", "/inbox/Important", ruleDefinition4, 1);

        importantEmails = client.getMailsFromFolder("Roskata123", "/inbox/Important");
        superImportantEmails = client.getMailsFromFolder("Roskata123",
            "/inbox/Important/SUPER_IMPORTANT");
        spamEmails = client.getMailsFromFolder("Roskata123", "/inbox/Spam");
        inboxEmails = client.getMailsFromFolder("Roskata123", "/inbox");

        String emailMetadata3 =
            "subject: ne6to voda" + newLine +
            "recipients: vr@abv.bg, rosen123@abv.bg" + newLine +
            "received: 2022-12-03 12:00";
        String emailContent3 = "Ni6to voda vjno nqma vutre v tozi email";

        client.sendMail("Stefan", emailMetadata3, emailContent3);

        String emailMetadata4 =
            "sender: KURRRRRR" + newLine +
            "subject: ne6to voda" + newLine +
                "recipients: vr@abv.bg, rosen123@abv.bg" + newLine +
                "received: 2022-12-03 12:00";
        String emailContent4 = "KURRR";

        client.sendMail("Stefan", emailMetadata4, emailContent4);

        var sentEmailsRoskata = client.getMailsFromFolder("Roskata123", "/sent");
        var sentEmailsVajenRecipient = client.getMailsFromFolder("vajenRecipient", "/sent");
        var sentEmailsStefan = client.getMailsFromFolder("Stefan", "/sent");

        return;
    }
}