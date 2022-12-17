package bg.sofia.uni.fmi.mjt.mail;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

public record Mail(Account sender, Set<String> recipients, String subject, String body, LocalDateTime received) {

    public static final String SENDER = "sender: ";
    public static final String SUBJECT = "subject: ";
    public static final String RECIPIENTS = "recipients: ";
    public static final String RECEIVED = "received: ";

    private static final String NEWLINE = System.lineSeparator();

    public static Mail createMail(Account sender, String metadata, String content) {

        List<String> metadataFields = List.of(metadata.split(NEWLINE));

        String subject = null;
        Set<String> recipients = null;
        LocalDateTime received = null;

        for (String field : metadataFields) {

            if (field.startsWith(Mail.SUBJECT)) {
                subject = field.replaceFirst(Mail.SUBJECT, "");
            } else if (field.startsWith(Mail.RECEIVED)) {
                String receivedString = field.replaceFirst(Mail.RECEIVED, "");
                //yyyy-MM-dd HH:mm
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                received = LocalDateTime.parse(receivedString, formatter);
            } else if (field.startsWith(Mail.RECIPIENTS)) {
                recipients = Set.of(field.replaceFirst(Mail.RECIPIENTS, "").split(", "));
            }

            if (subject != null && recipients != null && received != null) {
                break;
            }
        }

        return new Mail(sender, recipients, subject, content, received);
    }

    public static String getSender(String metadata) {
        List<String> metadataFields = List.of(metadata.split(NEWLINE));

        for (String field : metadataFields) {

            if (field.startsWith(Mail.SENDER)) {
                return field.replaceFirst(Mail.SENDER, "");
            }
        }

        return null;
    }
}