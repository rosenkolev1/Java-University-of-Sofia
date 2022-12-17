package bg.sofia.uni.fmi.mjt.mail;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

public class MailTest {

    private final String newLine = System.lineSeparator();

    @Test
    void testCreateMail() {
        Account sender = new Account("stefan@abv.bg", "Stefan");

        String mailMetadata =
            "sender: " + sender.emailAddress() + newLine +
                "subject: ne6to voda" + newLine +
                "recipients: vr@abv.bg, rosen123@abv.bg" + newLine +
                "received: 2022-12-03 12:00";
        String mailContent = "Ni6to voda vjno nqma vutre v tozi email";

        Mail expectedMail = new Mail(
            sender,
            Set.of(
                "vr@abv.bg",
                "rosen123@abv.bg"
            ),
            "ne6to voda",
            "Ni6to voda vjno nqma vutre v tozi email",
            LocalDateTime.of(2022, 12, 3, 12, 0)
        );

        Assertions.assertEquals(expectedMail, Mail.createMail(sender, mailMetadata, mailContent),
            "The actual mail differs from the expected!");

        mailMetadata =
            "subject: ne6to voda" + newLine +
                "recipients: vr@abv.bg, rosen123@abv.bg" + newLine +
                "received: 2022-12-03 12:00";
        mailContent = "Ni6to voda vjno nqma vutre v tozi email";

        expectedMail = new Mail(
            sender,
            Set.of(
                "vr@abv.bg",
                "rosen123@abv.bg"
            ),
            "ne6to voda",
            "Ni6to voda vjno nqma vutre v tozi email",
            LocalDateTime.of(2022, 12, 3, 12, 0)
        );

        Assertions.assertEquals(expectedMail, Mail.createMail(sender, mailMetadata, mailContent),
            "The actual mail differs from the expected! The passed mail metadata has missing sender. " +
                "Make sure that the sender is set for the email automatically anyway");

        mailMetadata =
            "sender: KUUR" + newLine +
                "subject: ne6to voda" + newLine +
                "recipients: vr@abv.bg, rosen123@abv.bg" + newLine +
                "received: 2022-12-03 12:00";
        mailContent = "Ni6to voda vjno nqma vutre v tozi email";

        expectedMail = new Mail(
            sender,
            Set.of(
                "vr@abv.bg",
                "rosen123@abv.bg"
            ),
            "ne6to voda",
            "Ni6to voda vjno nqma vutre v tozi email",
            LocalDateTime.of(2022, 12, 3, 12, 0)
        );

        Assertions.assertEquals(expectedMail, Mail.createMail(sender, mailMetadata, mailContent),
            "The actual mail differs from the expected! The passed mail metadata has incorrect sender. " +
                "Make sure that the sender is set for the email correctly anyway");
    }

    @Test
    void testGetSender() {
        String mailMetadata =
            "sender: someEmail@email.com" + newLine +
                "subject: ne6to voda" + newLine +
                "recipients: vr@abv.bg, rosen123@abv.bg" + newLine +
                "received: 2022-12-03 12:00";

        String expectedEmail = "someEmail@email.com";

        Assertions.assertEquals(expectedEmail, Mail.getSender(mailMetadata),
            "The expected email doesn't match the actual one");

        mailMetadata =
            "subject: ne6to voda" + newLine +
                "recipients: vr@abv.bg, rosen123@abv.bg" + newLine +
                "received: 2022-12-03 12:00" + newLine + "sender: someEmail@email.com";

        expectedEmail = "someEmail@email.com";

        Assertions.assertEquals(expectedEmail, Mail.getSender(mailMetadata),
            "The expected email doesn't match the actual one");

        mailMetadata =
            "subject: ne6to voda" + newLine +
                "recipients: vr@abv.bg, rosen123@abv.bg" + newLine +
                "received: 2022-12-03 12:00";

        Assertions.assertNull(Mail.getSender(mailMetadata),
            "The sender email should be null in this case!");
    }
}
