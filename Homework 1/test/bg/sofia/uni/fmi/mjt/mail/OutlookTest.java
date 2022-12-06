package bg.sofia.uni.fmi.mjt.mail;

import bg.sofia.uni.fmi.mjt.mail.exceptions.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

public class OutlookTest {
    private Outlook client = new Outlook();

    private final String newLine = System.lineSeparator();

    private void testCollectionIsUnmodifiable(Collection<?> collection){
        Assertions.assertThrows(UnsupportedOperationException.class,
            () -> collection.remove(0),
            "The collection should be unmodifiable");
    }

    private String getInboxPath(String path) {
        return Outlook.INBOX_FOLDER_PATH + path;
    }

    @Test
    void testAddNewAccountWorks(){

        String newAccName = "Roskata123";
        String newAccEmail = "roskata123@abv.bg";
        Account newAccount = client.addNewAccount(newAccName, newAccEmail);

        Account expectedNewAccount = new Account(newAccEmail, newAccName);

        List<AccountFolder> expectedInboxFolders = List.of(
            new AccountFolder(newAccName, Outlook.INBOX_FOLDER_PATH)
        );

        AccountFolder expectedSentFolder = new AccountFolder(newAccName, Outlook.SENT_FOLDER_PATH);

        Assertions.assertEquals(expectedNewAccount, newAccount,
            "The returned account differs from the expected!");
        Assertions.assertIterableEquals(expectedInboxFolders, client.getInboxFoldersForAcc(newAccName),
            "The inbox folder for the new account doesn't exist!");
        Assertions.assertEquals(expectedSentFolder, client.getSentFolderForAcc(newAccName),
            "The sent folder for the new account doesn't exist!");

        newAccName = "Stefan";
        newAccEmail = "stefan123@abv.bg";
        newAccount = client.addNewAccount(newAccName, newAccEmail);

        expectedNewAccount = new Account(newAccEmail, newAccName);

        expectedInboxFolders = List.of(
            new AccountFolder(newAccName, Outlook.INBOX_FOLDER_PATH)
        );

        expectedSentFolder = new AccountFolder(newAccName, Outlook.SENT_FOLDER_PATH);

        Assertions.assertEquals(expectedNewAccount, newAccount,
            "The returned account differs from the expected!");
        Assertions.assertIterableEquals(expectedInboxFolders, client.getInboxFoldersForAcc(newAccName),
            "The inbox folder for the new account doesn't exist!");
        Assertions.assertEquals(expectedSentFolder, client.getSentFolderForAcc(newAccName),
            "The sent folder for the new account doesn't exist!");

    }

    @Test
    void testAddNewAccountAlreadyExists(){
        client.addNewAccount("Roskata123", "roskata123@abv.bg");

        String newAccName = "Roskata123";
        String newAccEmail = "stefan@abv.bg";

        Assertions.assertThrows(AccountAlreadyExistsException.class,
            () -> client.addNewAccount(newAccName, newAccEmail),
            "Expected AccountAlreadyExistsException but nothing was thrown!");

        Assertions.assertThrows(AccountAlreadyExistsException.class,
            () -> client.addNewAccount("Stefan", "roskata123@abv.bg"),
            "Expected AccountAlreadyExistsException but nothing was thrown!");

        Assertions.assertThrows(AccountAlreadyExistsException.class,
            () -> client.addNewAccount("Roskata123", "roskata123@abv.bg"),
        "Expected AccountAlreadyExistsException but nothing was thrown!");
    }

    @Test
    void testAddNewAccountNullOrEmptyOrBlankName(){
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> client.addNewAccount(null, "roskata123@abv.bg"),
            "Expected IllegalArgumentException because of null name but nothing was thrown!");

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> client.addNewAccount("", "roskata123@abv.bg"),
            "Expected IllegalArgumentException because of empty name but nothing was thrown!");

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> client.addNewAccount("    ", "roskata123@abv.bg"),
            "Expected IllegalArgumentException because of blank name but nothing was thrown!");
    }

    @Test
    void testAddNewAccountNullOrEmptyOrBlankEmail(){
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> client.addNewAccount("Roskata123", null),
            "Expected IllegalArgumentException because of null email but nothing was thrown!");

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> client.addNewAccount("Roskata123", ""),
            "Expected IllegalArgumentException because of empty email but nothing was thrown!");

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> client.addNewAccount("Roskata123", "    "),
            "Expected IllegalArgumentException because of blank email but nothing was thrown!");
    }

    @Test
    void testCreateFolderThrowsIllegalArgumentExceptionBecauseAccountNameIsNullOrBlank(){

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> this.client.createFolder(null, getInboxPath("/Important")),
            "Expected IllegalArgumentException because of null name, but nothing was thrown!");

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> this.client.createFolder("", getInboxPath("/Important")),
            "Expected IllegalArgumentException because of empty name, but nothing was thrown!");

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> this.client.createFolder("   ", getInboxPath("/Important")),
            "Expected IllegalArgumentException because of blank name, but nothing was thrown!");
    }

    @Test
    void testCreateFolderThrowsIllegalArgumentExceptionBecausePathIsNullOrBlank(){

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> this.client.createFolder("Roskata123", null),
            "Expected IllegalArgumentException because of null path, but nothing was thrown!");

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> this.client.createFolder("Roskata123", ""),
            "Expected IllegalArgumentException because of empty path, but nothing was thrown!");

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> this.client.createFolder("Roskata123", "   "),
            "Expected IllegalArgumentException because of blank path, but nothing was thrown!");
    }

    @Test
    void testCreateFolderThrowsAccountNotFoundException() {
        client.addNewAccount("Roskata123", "roskata@abv.bg");
        client.addNewAccount("Stefan", "stefan@abv.bg");

        Assertions.assertThrows(AccountNotFoundException.class,
            () -> this.client.createFolder("Roskata", getInboxPath("/Important")),
            "Expected AccountNotFoundException, but nothing was thrown!");
    }

    @Test
    void testCreateFolderThrowsInvalidPathException() {

        client.addNewAccount("Roskata123", "roskata@abv.bg");

        Assertions.assertThrows(InvalidPathException.class,
            () -> client.createFolder("Roskata123", "Important"),
            "Expected InvalidPathException because folder was not in root(inbox), but nothing was thrown!");

        client.createFolder("Roskata123", getInboxPath("/Important"));

        Assertions.assertThrows(InvalidPathException.class,
            () -> client.createFolder("Roskata123", getInboxPath("/Important/Documents/PDF")),
            "Expected InvalidPathException because subfolder was not found, but nothing was thrown!");

        Assertions.assertThrows(InvalidPathException.class,
            () -> client.createFolder("Roskata123", "/"),
            "Expected InvalidPathException because path is invalid, but nothing was thrown!");

        Assertions.assertThrows(InvalidPathException.class,
            () -> client.createFolder("Roskata123", "/NotInbox"),
            "Expected InvalidPathException because path is invalid, but nothing was thrown!");

        Assertions.assertThrows(InvalidPathException.class,
            () -> client.createFolder("Roskata123", Outlook.INBOX_FOLDER_PATH + "/"),
            "Expected InvalidPathException because path is invalid, but nothing was thrown!");

        Assertions.assertThrows(InvalidPathException.class,
            () -> client.createFolder("Roskata123", Outlook.INBOX_FOLDER_PATH + "//"),
            "Expected InvalidPathException because path is invalid, but nothing was thrown!");

        Assertions.assertThrows(InvalidPathException.class,
            () -> client.createFolder("Roskata123", getInboxPath("/Important//PDF")),
            "Expected InvalidPathException because path is invalid, but nothing was thrown!");

        Assertions.assertThrows(InvalidPathException.class,
            () -> client.createFolder("Roskata123", getInboxPath("Important")),
            "Expected InvalidPathException because path is invalid, but nothing was thrown!");
    }

    @Test
    void testCreateFolderThrowsFolderAlreadyExistsException() {

        client.addNewAccount("Roskata123", "roskata@abv.bg");

        client.createFolder("Roskata123", getInboxPath("/Important"));

        Assertions.assertThrows(FolderAlreadyExistsException.class,
            () -> client.createFolder("Roskata123", getInboxPath("/Important")),
            "Expected FolderAlreadyExistsException, but nothing was thrown!");

        client.createFolder("Roskata123", getInboxPath("/Important/PDF"));

        Assertions.assertThrows(FolderAlreadyExistsException.class,
            () -> client.createFolder("Roskata123", getInboxPath("/Important/PDF")),
            "Expected FolderAlreadyExistsException, but nothing was thrown!");

        Assertions.assertThrows(FolderAlreadyExistsException.class,
            () -> client.createFolder("Roskata123", Outlook.INBOX_FOLDER_PATH),
            "Expected FolderAlreadyExistsException, but nothing was thrown!");
    }

    @Test
    void testCreateFolderWorksProperly() {

        client.addNewAccount("Roskata123", "roskata@abv.bg");

        client.createFolder("Roskata123", getInboxPath("/Important"));
        client.createFolder("Roskata123", getInboxPath("/Spam"));
        client.createFolder("Roskata123", getInboxPath("/Important/PDF"));
        client.createFolder("Roskata123", getInboxPath("/Important/PDF/Diploma"));

        List<AccountFolder> expectedAccFolders = List.of(
            new AccountFolder("Roskata123", Outlook.INBOX_FOLDER_PATH),
            new AccountFolder("Roskata123", getInboxPath("/Important")),
            new AccountFolder("Roskata123", getInboxPath("/Spam")),
            new AccountFolder("Roskata123", getInboxPath("/Important/PDF")),
            new AccountFolder("Roskata123", getInboxPath("/Important/PDF/Diploma"))
        );

        var inboxFolders =  client.getInboxFoldersForAcc("Roskata123");
        Assertions.assertIterableEquals(expectedAccFolders, inboxFolders);

        Assertions.assertThrows(UnsupportedOperationException.class,
            () -> inboxFolders.remove(0),
            "The collection should be unmodifiable");
    }

    @Test
    void testAddRuleThrowsIllegalArgumentExceptionBecauseAccountNameIsNullOrBlank() {

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> client.addRule(null, "/Important",
                "subject-includes: mjt, izpit, 2022", 1),
            "Expected IllegalArgumentException because of null name, but nothing was thrown");

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> client.addRule("", "/Important",
                "subject-includes: mjt, izpit, 2022", 1),
            "Expected IllegalArgumentException because of empty name, but nothing was thrown");

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> client.addRule("   ", "/Important",
                "subject-includes: mjt, izpit, 2022", 1),
            "Expected IllegalArgumentException because of blank name, but nothing was thrown");

    }

    @Test
    void testAddRuleThrowsIllegalArgumentExceptionBecauseFolderPathIsNullOrBlank() {

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> client.addRule("Roskata123", null,
                "subject-includes: mjt, izpit, 2022", 1),
            "Expected IllegalArgumentException because of null folder path, but nothing was thrown");

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> client.addRule("Roskata123", "",
                "subject-includes: mjt, izpit, 2022", 1),
            "Expected IllegalArgumentException because of empty folder path, but nothing was thrown");

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> client.addRule("Roskata123", "  ",
                "subject-includes: mjt, izpit, 2022", 1),
            "Expected IllegalArgumentException because of blank folder path, but nothing was thrown");

    }

    @Test
    void testAddRuleThrowsIllegalArgumentExceptionBecauseRuleDefinitionIsNullOrBlank() {

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> client.addRule("Roskata123", "/Important",
                null, 1),
            "Expected IllegalArgumentException because of null rule definition, but nothing was thrown");

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> client.addRule("Roskata123", "/Important",
                "", 1),
            "Expected IllegalArgumentException because of empty rule definition, but nothing was thrown");

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> client.addRule("Roskata123", "/Important",
                "   ", 1),
            "Expected IllegalArgumentException because of blank rule definition, but nothing was thrown");

    }

    @Test
    void testAddRuleThrowsIllegalArgumentExceptionBecausePriorityIsNotCorrect() {

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> client.addRule("Roskata123", "/Important",
                "subject-includes: mjt, izpit, 2022", 0),
            "Expected IllegalArgumentException because of incorrect priority, but nothing was thrown");

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> client.addRule("Roskata123", "/Important",
                "subject-includes: mjt, izpit, 2022", 11),
            "Expected IllegalArgumentException because of incorrect priority, but nothing was thrown");

    }

    @Test
    void testAddRuleThrowsAccountNotFoundException() {

        Assertions.assertThrows(AccountNotFoundException.class,
            () -> client.addRule("Roskata123", "/Important",
                "subject-includes: mjt, izpit, 2022", 2),
            "Expected AccountNotFoundException, but nothing was thrown");

        client.addNewAccount("stefan", "stefan@abv.bg");

        Assertions.assertThrows(AccountNotFoundException.class,
            () -> client.addRule("Stefan", "/Important",
                "subject-includes: mjt, izpit, 2022", 1),
            "Expected AccountNotFoundException, but nothing was thrown");

    }

    @Test
    void testAddRuleThrowsFolderNotFoundException() {

        client.addNewAccount("Roskata123", "roskata@abv.bg");

        Assertions.assertThrows(FolderNotFoundException.class,
            () -> client.addRule("Roskata123", getInboxPath("/Important"),
                "subject-includes: mjt, izpit, 2022", 2),
            "Expected FolderNotFoundException, but nothing was thrown");

        client.createFolder("Roskata123", getInboxPath("/Important"));

        Assertions.assertThrows(FolderNotFoundException.class,
            () -> client.addRule("Roskata123", getInboxPath("/Important/PDF"),
                "subject-includes: mjt, izpit, 2022", 2),
            "Expected FolderNotFoundException, but nothing was thrown");

    }

    @Test
    void testAddRuleThrowsRuleAlreadyDefinedException() {

        client.addNewAccount("Roskata123", "roskata@abv.bg");

        client.createFolder("Roskata123", getInboxPath("/Important"));

        String rule1Definition = "subject-includes: mjt, izpit, 2022" + newLine +
            "subject-or-body-includes: izpit" + newLine +
            "from: stoyo@fmi.bg" + newLine +
            "subject-includes: Java Course, someOtherKeyword";

        Assertions.assertThrows(RuleAlreadyDefinedException.class,
            () -> client.addRule("Roskata123", getInboxPath("/Important"),
                rule1Definition, 2),
            "Expected RuleAlreadyDefinedException because of repeating rule condition," +
                " but nothing was thrown");

        String rule2Definition = "subject-includes: mjt, izpit, 2022" + newLine +
            "subject-or-body-includes: izpit" + newLine +
            "from: stoyo@fmi.bg";

        client.addRule("Roskata123", getInboxPath("/Important"), rule2Definition, 2);

        Assertions.assertThrows(RuleAlreadyDefinedException.class,
            () -> client.addRule("Roskata123", getInboxPath("/Important"),
                rule2Definition, 2),
            "Expected RuleAlreadyDefinedException due to trying to add the same rule twice, " +
                "but nothing was thrown");

    }

    @Test
    void testAddRuleWorksProperly() {

        client.addNewAccount("Roskata123", "roskata@abv.bg");

        client.createFolder("Roskata123", getInboxPath("/Important"));
        client.createFolder("Roskata123", getInboxPath("/Spam"));

        String rule1Definition = "subject-includes: mjt, izpit, 2022" + newLine +
            "subject-or-body-includes: izpit" + newLine +
            "from: stoyo@fmi.bg";

        client.addRule("Roskata123", getInboxPath("/Important"), rule1Definition, 2);

        String rule2Definition = "subject-includes: mjt, lab" + newLine +
            "subject-or-body-includes: lab" + newLine +
            "from: stoyo@fmi.bg";

        client.addRule("Roskata123", getInboxPath("/Spam"), rule2Definition, 4);

        var expectedRules = List.of(
            new AccountRule("Roskata123", getInboxPath("/Important"), rule1Definition, 2),
            new AccountRule("Roskata123", getInboxPath("/Spam"), rule2Definition, 4)
        );

        var actualRules = client.getAccountRulesForAcc("Roskata123");
        Assertions.assertIterableEquals(expectedRules, actualRules);

        testCollectionIsUnmodifiable(actualRules);
    }

    @Test
    void testSendMailThrowsIllegalArgumentExceptionBecauseAccountNameIsNullOrBlank(){
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> client.sendMail(null, "ss", "ss"),
            "Expected IllegalArgumentException because of null account name, but nothing was thrown");

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> client.sendMail("", "/Important", ""),
            "Expected IllegalArgumentException because of empty account name, but nothing was thrown");

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> client.sendMail("    ", "ss", "ss"),
            "Expected IllegalArgumentException because of blank null account name, but nothing was thrown");
    }

    @Test
    void testSendMailThrowsIllegalArgumentExceptionBecauseMetadataIsNullOrBlank(){
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> client.sendMail("ss", null, "ss"),
            "Expected IllegalArgumentException because of null metadata, but nothing was thrown");

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> client.sendMail("ss", "", "ss"),
            "Expected IllegalArgumentException because of empty metadata, but nothing was thrown");

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> client.sendMail("ss", "   ", "ss"),
            "Expected IllegalArgumentException because of blank metadata, but nothing was thrown");
    }

    @Test
    void testSendMailThrowsIllegalArgumentExceptionBecauseMailContentIsNullOrBlank(){
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> client.sendMail("ss", "ss", null),
            "Expected IllegalArgumentException because of null mail content, but nothing was thrown");

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> client.sendMail("ss", "ss", ""),
            "Expected IllegalArgumentException because of empty mail content, but nothing was thrown");

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> client.sendMail("ss", "ss", "   "),
            "Expected IllegalArgumentException because of blank mail content, but nothing was thrown");
    }

    @Test
    void testSendMailNoRules_SingleMailWorks(){
        client.addNewAccount("Stefan", "stefan@abv.bg");
        client.addNewAccount("Roskata123", "rosen123@abv.bg");
        client.addNewAccount("vajenRecipient", "vr@abv.bg");
        client.addNewAccount("malovajenRecipient", "mvr@abv.bg");

        client.createFolder("Roskata123", getInboxPath("/Important"));
        client.createFolder("Roskata123", getInboxPath("/Important/SUPER_IMPORTANT"));
        client.createFolder("Roskata123", getInboxPath("/Spam"));
        client.createFolder("vajenRecipient", getInboxPath("/Spam"));

        String emailMetadata1 = "sender: stefan@abv.bg" + newLine +
            "subject: ni6to zna4itelno" + newLine +
            "recipients: vr@abv.bg, rosen123@abv.bg" + newLine +
            "received: 2022-12-03 12:00";
        String emailContent1 = "Absolutno ni6to voda vajno nqma v tozi email";

        Account sender = new Account("stefan@abv.bg", "Stefan");

        client.sendMail("Stefan", emailMetadata1, emailContent1);

        LocalDateTime expectedReceivedTime = LocalDateTime.of(2022, 12, 3, 12, 0);
        Mail expectedMail = new Mail(
            sender,
            Set.of(
                "vr@abv.bg",
                "rosen123@abv.bg"
            ),
            "ni6to zna4itelno",
            emailContent1,
            expectedReceivedTime
        );

        List<StoredMail> expectedInboxEmailsRoskata = List.of(
            new StoredMail(
                expectedMail,
                new AccountFolder("Roskata123", Outlook.INBOX_FOLDER_PATH)
            )
        );

        List<StoredMail> expectedInboxEmailsVJ = List.of(
            new StoredMail(
                expectedMail,
                new AccountFolder("vajenRecipient", Outlook.INBOX_FOLDER_PATH)
            )
        );

        List<StoredMail> expectedSentEmails = List.of(
            new StoredMail(
                expectedMail,
                new AccountFolder("Stefan", Outlook.SENT_FOLDER_PATH)
            )
        );

        Assertions.assertIterableEquals(
            expectedInboxEmailsRoskata,
            client.getStoredInboxMailsForAccountAsUnmodifiable("Roskata123")
        );
        Assertions.assertIterableEquals(
            expectedInboxEmailsVJ,
            client.getStoredInboxMailsForAccountAsUnmodifiable("vajenRecipient")
        );

        Assertions.assertIterableEquals(
            expectedSentEmails,
            client.getStoredSentMailsForAccountAsUnmodifiable("Stefan")
        );
    }

    @Test
    void testSendMailSingleRule_SingleMailWorks() {
        client.addNewAccount("Stefan", "stefan@abv.bg");
        client.addNewAccount("Roskata123", "rosen123@abv.bg");
        client.addNewAccount("vajenRecipient", "vr@abv.bg");
        client.addNewAccount("malovajenRecipient", "mvr@abv.bg");

        client.createFolder("Roskata123", getInboxPath("/Important"));
        client.createFolder("Roskata123", getInboxPath("/Important/SUPER_IMPORTANT"));
        client.createFolder("Roskata123", getInboxPath("/Spam"));
        client.createFolder("vajenRecipient", getInboxPath("/Spam"));

        String ruleDefinition1 = "subject-includes: voda" + newLine +
            "subject-or-body-includes: voda vajno" + newLine +
            "recipients-includes: vr@abv.bg" + newLine +
            "from: stefan@abv.bg";

        client.addRule("Roskata123", getInboxPath("/Important"), ruleDefinition1, 3);

        String emailMetadata1 = "sender: stefan@abv.bg" + newLine +
            "subject: ne6to voda" + newLine +
            "recipients: vr@abv.bg, rosen123@abv.bg" + newLine +
            "received: 2022-12-03 12:00";
        String emailContent1 = "Absolutno ni6to voda vajno nqma v tozi email";

        Account sender = new Account("stefan@abv.bg", "Stefan");

        client.sendMail("Stefan", emailMetadata1, emailContent1);

        LocalDateTime expectedReceivedTime = LocalDateTime.of(2022, 12, 3, 12, 0);
        Mail expectedMail = new Mail(
            sender,
            Set.of(
                "vr@abv.bg",
                "rosen123@abv.bg"
            ),
            "ne6to voda",
            emailContent1,
            expectedReceivedTime
        );

        List<StoredMail> expectedInboxEmailsRoskata = List.of(
            new StoredMail(
                expectedMail,
                new AccountFolder("Roskata123", getInboxPath("/Important"))
            )
        );

        List<StoredMail> expectedInboxEmailsVJ = List.of(
            new StoredMail(
                expectedMail,
                new AccountFolder("vajenRecipient", Outlook.INBOX_FOLDER_PATH)
            )
        );

        List<StoredMail> expectedSentEmails = List.of(
            new StoredMail(
                expectedMail,
                new AccountFolder("Stefan", Outlook.SENT_FOLDER_PATH)
            )
        );

        Assertions.assertIterableEquals(
            expectedInboxEmailsRoskata,
            client.getStoredInboxMailsForAccountAsUnmodifiable("Roskata123")
        );
        Assertions.assertIterableEquals(
            expectedInboxEmailsVJ,
            client.getStoredInboxMailsForAccountAsUnmodifiable("vajenRecipient")
        );

        Assertions.assertIterableEquals(
            expectedSentEmails,
            client.getStoredSentMailsForAccountAsUnmodifiable("Stefan")
        );
    }

    @Test
    void testSendMailSingleRule_TwoMailsWorks() {
        client.addNewAccount("Stefan", "stefan@abv.bg");
        client.addNewAccount("Roskata123", "rosen123@abv.bg");
        client.addNewAccount("vajenRecipient", "vr@abv.bg");
        client.addNewAccount("malovajenRecipient", "mvr@abv.bg");

        client.createFolder("Roskata123", getInboxPath("/Important"));
        client.createFolder("Roskata123", getInboxPath("/Important/SUPER_IMPORTANT"));
        client.createFolder("Roskata123", getInboxPath("/Spam"));
        client.createFolder("vajenRecipient", getInboxPath("/Spam"));

        String ruleDefinition1 = "subject-includes: voda" + newLine +
            "subject-or-body-includes: voda vajno" + newLine +
            "recipients-includes: vr@abv.bg" + newLine +
            "from: stefan@abv.bg";

        client.addRule("Roskata123", getInboxPath("/Important"), ruleDefinition1, 3);

        String emailMetadata1 = "sender: stefan@abv.bg" + newLine +
            "subject: ni6to zna4itelno" + newLine +
            "recipients: vr@abv.bg, rosen123@abv.bg" + newLine +
            "received: 2022-12-03 12:00";
        String emailContent1 = "Absolutno ni6to voda vajno nqma v tozi email";

        Account sender = new Account("stefan@abv.bg", "Stefan");

        client.sendMail("Stefan", emailMetadata1, emailContent1);

        String emailMetadata2 = "sender: stefan@abv.bg" + newLine +
            "subject: ne6to voda" + newLine +
            "recipients: vr@abv.bg, rosen123@abv.bg" + newLine +
            "received: 2022-12-03 12:00";
        String emailContent2 = "Absolutno ni6to voda vajno nqma vutre v tozi email";

        client.sendMail("Stefan", emailMetadata2, emailContent2);

        LocalDateTime expectedReceivedTime = LocalDateTime.of(2022, 12, 3, 12, 0);

        Mail expectedSentMail1 = new Mail(
            sender,
            Set.of(
                "vr@abv.bg",
                "rosen123@abv.bg"
            ),
            "ni6to zna4itelno",
            emailContent1,
            expectedReceivedTime
        );
        Mail expectedSentMail2 = new Mail(
            sender,
            Set.of(
                "vr@abv.bg",
                "rosen123@abv.bg"
            ),
            "ne6to voda",
            emailContent2,
            expectedReceivedTime
        );

        List<StoredMail> expectedInboxEmailsRoskata = List.of(
            new StoredMail(
                expectedSentMail1,
                new AccountFolder("Roskata123", Outlook.INBOX_FOLDER_PATH)
            ),
            new StoredMail(
                expectedSentMail2,
                new AccountFolder("Roskata123", getInboxPath("/Important"))
            )
        );

        List<StoredMail> expectedInboxEmailsVJ = List.of(
            new StoredMail(
                expectedSentMail1,
                new AccountFolder("vajenRecipient", Outlook.INBOX_FOLDER_PATH)
            ),
            new StoredMail(
                expectedSentMail2,
                new AccountFolder("vajenRecipient", Outlook.INBOX_FOLDER_PATH)
            )
        );

        List<StoredMail> expectedSentEmails = List.of(
            new StoredMail(
                expectedSentMail1,
                new AccountFolder("Stefan", Outlook.SENT_FOLDER_PATH)
            ),
            new StoredMail(
                expectedSentMail2,
                new AccountFolder("Stefan", Outlook.SENT_FOLDER_PATH)
            )
        );

        Assertions.assertIterableEquals(
            expectedInboxEmailsRoskata,
            client.getStoredInboxMailsForAccountAsUnmodifiable("Roskata123")
        );
        Assertions.assertIterableEquals(
            expectedInboxEmailsVJ,
            client.getStoredInboxMailsForAccountAsUnmodifiable("vajenRecipient")
        );

        Assertions.assertIterableEquals(
            expectedSentEmails,
            client.getStoredSentMailsForAccountAsUnmodifiable("Stefan")
        );
    }

    @Test
    void testSendMailManyRulesWithMultipleMatchingSameMail_EmailsWorks() {
        client.addNewAccount("Stefan", "stefan@abv.bg");
        client.addNewAccount("Roskata123", "rosen123@abv.bg");
        client.addNewAccount("vajenRecipient", "vr@abv.bg");
        client.addNewAccount("malovajenRecipient", "mvr@abv.bg");

        client.createFolder("Roskata123", getInboxPath("/Important"));
        client.createFolder("Roskata123", getInboxPath("/Important/SUPER_IMPORTANT"));
        client.createFolder("Roskata123", getInboxPath("/Spam"));
        client.createFolder("vajenRecipient", getInboxPath("/Spam"));

        String ruleDefinition1 = "subject-includes: voda" + newLine +
            "subject-or-body-includes: voda vajno" + newLine +
            "recipients-includes: vr@abv.bg" + newLine +
            "from: stefan@abv.bg";

        String ruleDefinition2 = "subject-includes: ni6to";

        String ruleDefinition3 = "subject-includes: voda, ne6to" + newLine +
            "subject-or-body-includes: voda vajno, Absolutno" + newLine +
            "recipients-includes: stefan@abv.bg, vr@abv.bg" + newLine +
            "from: stefan@abv.bg";

        client.addRule("Roskata123", getInboxPath("/Important"), ruleDefinition1, 3);
        client.addRule("Roskata123", getInboxPath("/Spam"), ruleDefinition2, 4);
        client.addRule("vajenRecipient", getInboxPath("/Spam"), ruleDefinition2, 4);
        client.addRule("Roskata123", getInboxPath("/Important/SUPER_IMPORTANT"), ruleDefinition3, 2);

        String emailMetadata1 = "sender: stefan@abv.bg" + newLine +
            "subject: ni6to zna4itelno" + newLine +
            "recipients: vr@abv.bg, rosen123@abv.bg" + newLine +
            "received: 2022-12-03 12:00";
        String emailContent1 = "Absolutno ni6to voda vajno nqma v tozi email";

        Account sender = new Account("stefan@abv.bg", "Stefan");

        client.sendMail("Stefan", emailMetadata1, emailContent1);

        String emailMetadata2 = "sender: stefan@abv.bg" + newLine +
            "subject: ne6to voda ni6to" + newLine +
            "recipients: vr@abv.bg, rosen123@abv.bg" + newLine +
            "received: 2022-12-03 12:00";
        String emailContent2 = "Absolutno ni6to voda vajno nqma vutre v tozi email";

        client.sendMail("Stefan", emailMetadata2, emailContent2);

        LocalDateTime expectedReceivedTime = LocalDateTime.of(2022, 12, 3, 12, 0);

        Mail expectedSentMail1 = new Mail(
            sender,
            Set.of(
                "vr@abv.bg",
                "rosen123@abv.bg"
            ),
            "ni6to zna4itelno",
            emailContent1,
            expectedReceivedTime
        );
        Mail expectedSentMail2 = new Mail(
            sender,
            Set.of(
                "vr@abv.bg",
                "rosen123@abv.bg"
            ),
            "ne6to voda ni6to",
            emailContent2,
            expectedReceivedTime
        );

        List<StoredMail> expectedInboxEmailsRoskata = List.of(
            new StoredMail(
                expectedSentMail1,
                new AccountFolder("Roskata123", getInboxPath("/Spam"))
            ),
            new StoredMail(
                expectedSentMail2,
                new AccountFolder("Roskata123", getInboxPath("/Important/SUPER_IMPORTANT"))
            )
        );

        List<StoredMail> expectedInboxEmailsVJ = List.of(
            new StoredMail(
                expectedSentMail1,
                new AccountFolder("vajenRecipient", getInboxPath("/Spam"))
            ),
            new StoredMail(
                expectedSentMail2,
                new AccountFolder("vajenRecipient", getInboxPath("/Spam"))
            )
        );

        List<StoredMail> expectedSentEmails = List.of(
            new StoredMail(
                expectedSentMail1,
                new AccountFolder("Stefan", Outlook.SENT_FOLDER_PATH)
            ),
            new StoredMail(
                expectedSentMail2,
                new AccountFolder("Stefan", Outlook.SENT_FOLDER_PATH)
            )
        );

        Assertions.assertIterableEquals(
            expectedInboxEmailsRoskata,
            client.getStoredInboxMailsForAccountAsUnmodifiable("Roskata123")
        );
        Assertions.assertIterableEquals(
            expectedInboxEmailsVJ,
            client.getStoredInboxMailsForAccountAsUnmodifiable("vajenRecipient")
        );

        Assertions.assertIterableEquals(
            expectedSentEmails,
            client.getStoredSentMailsForAccountAsUnmodifiable("Stefan")
        );
    }

    @Test
    void testAddingRuleThatMatchesExistingEmailChangesEmailPosition_EmailsWorks() {
        client.addNewAccount("Stefan", "stefan@abv.bg");
        client.addNewAccount("Roskata123", "rosen123@abv.bg");
        client.addNewAccount("vajenRecipient", "vr@abv.bg");
        client.addNewAccount("malovajenRecipient", "mvr@abv.bg");

        client.createFolder("Roskata123", getInboxPath("/Important"));
        client.createFolder("Roskata123", getInboxPath("/Important/SUPER_IMPORTANT"));
        client.createFolder("Roskata123", getInboxPath("/Spam"));
        client.createFolder("vajenRecipient", getInboxPath("/Spam"));

        String ruleDefinition1 = "subject-includes: voda" + newLine +
            "subject-or-body-includes: voda vajno" + newLine +
            "recipients-includes: vr@abv.bg" + newLine +
            "from: stefan@abv.bg";

        String ruleDefinition2 = "subject-includes: ni6to";

        String ruleDefinition3 = "subject-includes: voda, ne6to" + newLine +
            "subject-or-body-includes: voda vajno, Absolutno" + newLine +
            "recipients-includes: stefan@abv.bg, vr@abv.bg" + newLine +
            "from: stefan@abv.bg";

        String ruleDefinition4 = "subject-or-body-includes: Absolutno";

        String ruleDefinition5 = "subject-includes: voda";

        client.addRule("Roskata123", getInboxPath("/Important"), ruleDefinition1, 3);
        client.addRule("Roskata123", getInboxPath("/Spam"), ruleDefinition2, 4);
//        client.addRule("vajenRecipient", getInboxPath("/Spam"), ruleDefinition2, 4);
        client.addRule("Roskata123", getInboxPath("/Important/SUPER_IMPORTANT"), ruleDefinition3, 2);
        client.addRule("vajenRecipient", getInboxPath("/Spam"), ruleDefinition5, 4);

        String emailMetadata1 = "sender: stefan@abv.bg" + newLine +
            "subject: ni6to zna4itelno" + newLine +
            "recipients: vr@abv.bg, rosen123@abv.bg" + newLine +
            "received: 2022-12-03 12:00";
        String emailContent1 = "Absolutno ni6to voda vajno nqma v tozi email";

        Account sender = new Account("stefan@abv.bg", "Stefan");

        client.sendMail("Stefan", emailMetadata1, emailContent1);

        String emailMetadata2 = "sender: stefan@abv.bg" + newLine +
            "subject: ne6to voda ni6to" + newLine +
            "recipients: vr@abv.bg, rosen123@abv.bg" + newLine +
            "received: 2022-12-03 12:00";
        String emailContent2 = "Absolutno ni6to voda vajno nqma vutre v tozi email";

        client.sendMail("Stefan", emailMetadata2, emailContent2);

        //Add other rules
        client.addRule("vajenRecipient", getInboxPath("/Spam"), ruleDefinition4, 1);
        client.addRule("Roskata123", getInboxPath("/Important"), ruleDefinition4, 1);

        String emailMetadata3 =
            "subject: ne6to voda" + newLine +
                "recipients: vr@abv.bg, rosen123@abv.bg" + newLine +
                "received: 2022-12-03 12:00";
        String emailContent3 = "Ni6to voda vjno nqma vutre v tozi email";

        client.sendMail("Stefan", emailMetadata3, emailContent3);

        Account email4Sender = new Account("mvr@abv.bg", "malovajenRecipient");
        String emailMetadata4 =
            "sender: mvr@abv.bg" + newLine +
                "subject: ni6to voda" + newLine +
                "recipients: vr@abv.bg, stefan@abv.bg, rosen123@abv.bg" + newLine +
                "received: 2022-12-03 12:00";
        String emailContent4 = "KURRR";

        client.sendMail("malovajenRecipient", emailMetadata4, emailContent4);

        LocalDateTime expectedReceivedTime = LocalDateTime.of(2022, 12, 3, 12, 0);

        Mail expectedSentMail1 = new Mail(
            sender,
            Set.of(
                "vr@abv.bg",
                "rosen123@abv.bg"
            ),
            "ni6to zna4itelno",
            emailContent1,
            expectedReceivedTime
        );
        Mail expectedSentMail2 = new Mail(
            sender,
            Set.of(
                "vr@abv.bg",
                "rosen123@abv.bg"
            ),
            "ne6to voda ni6to",
            emailContent2,
            expectedReceivedTime
        );
        Mail expectedSentMail3 = new Mail(
            sender,
            Set.of(
                "vr@abv.bg",
                "rosen123@abv.bg"
            ),
            "ne6to voda",
            emailContent3,
            expectedReceivedTime
        );
        Mail expectedSentMail4 = new Mail(
            email4Sender,
            Set.of(
                "vr@abv.bg",
                "rosen123@abv.bg",
                "stefan@abv.bg"
            ),
            "ni6to voda",
            emailContent4,
            expectedReceivedTime
        );

        List<StoredMail> expectedInboxMailsRoskata = List.of(
            new StoredMail(
                expectedSentMail1,
                new AccountFolder("Roskata123", getInboxPath("/Spam"))
            ),
            new StoredMail(
                expectedSentMail2,
                new AccountFolder("Roskata123", getInboxPath("/Important/SUPER_IMPORTANT"))
            ),
            new StoredMail(
                expectedSentMail3,
                new AccountFolder("Roskata123", Outlook.INBOX_FOLDER_PATH)
            ),
            new StoredMail(
                expectedSentMail4,
                new AccountFolder("Roskata123", getInboxPath("/Spam"))
            )
        );

        List<StoredMail> expectedInboxMailsVJ = List.of(
            new StoredMail(
                expectedSentMail2,
                new AccountFolder("vajenRecipient", getInboxPath("/Spam"))
            ),
            new StoredMail(
                expectedSentMail1,
                new AccountFolder("vajenRecipient", getInboxPath("/Spam"))
            ),
            new StoredMail(
                expectedSentMail3,
                new AccountFolder("vajenRecipient", getInboxPath("/Spam"))
            ),
            new StoredMail(
                expectedSentMail4,
                new AccountFolder("vajenRecipient", getInboxPath("/Spam"))
            )
        );

        List<StoredMail> expectedInboxMailsStefan = List.of(
            new StoredMail(
                expectedSentMail4,
                new AccountFolder("Stefan", Outlook.INBOX_FOLDER_PATH)
            )
        );

        List<StoredMail> expectedSentMailsStefan = List.of(
            new StoredMail(
                expectedSentMail1,
                new AccountFolder("Stefan", Outlook.SENT_FOLDER_PATH)
            ),
            new StoredMail(
                expectedSentMail2,
                new AccountFolder("Stefan", Outlook.SENT_FOLDER_PATH)
            ),

            new StoredMail(
                expectedSentMail3,
                new AccountFolder("Stefan", Outlook.SENT_FOLDER_PATH)
            )
        );
        List<StoredMail> expectedSentMailsMVR = List.of(
            new StoredMail(
                expectedSentMail4,
                new AccountFolder("malovajenRecipient", Outlook.SENT_FOLDER_PATH)
            )
        );

        Assertions.assertIterableEquals(
            expectedInboxMailsRoskata,
            client.getStoredInboxMailsForAccountAsUnmodifiable("Roskata123")
        );
        Assertions.assertIterableEquals(
            expectedInboxMailsVJ,
            client.getStoredInboxMailsForAccountAsUnmodifiable("vajenRecipient")
        );
        Assertions.assertIterableEquals(
            expectedInboxMailsStefan,
            client.getStoredInboxMailsForAccountAsUnmodifiable("Stefan")
        );

        Assertions.assertIterableEquals(
            expectedSentMailsStefan,
            client.getStoredSentMailsForAccountAsUnmodifiable("Stefan")
        );
        Assertions.assertIterableEquals(
            expectedSentMailsMVR,
            client.getStoredSentMailsForAccountAsUnmodifiable("malovajenRecipient")
        );
    }

    @Test
    void testGetMailsFromFolderWorks() {
        client.addNewAccount("Stefan", "stefan@abv.bg");
        client.addNewAccount("Roskata123", "rosen123@abv.bg");
        client.addNewAccount("vajenRecipient", "vr@abv.bg");
        client.addNewAccount("malovajenRecipient", "mvr@abv.bg");

        client.createFolder("Roskata123", getInboxPath("/Important"));
        client.createFolder("Roskata123", getInboxPath("/Important/SUPER_IMPORTANT"));
        client.createFolder("Roskata123", getInboxPath("/Spam"));
        client.createFolder("vajenRecipient", getInboxPath("/Spam"));

        String ruleDefinition1 = "subject-includes: voda" + newLine +
            "subject-or-body-includes: voda vajno" + newLine +
            "recipients-includes: vr@abv.bg" + newLine +
            "from: stefan@abv.bg";

        String ruleDefinition2 = "subject-includes: ni6to";

        String ruleDefinition3 = "subject-includes: voda, ne6to" + newLine +
            "subject-or-body-includes: voda vajno, Absolutno" + newLine +
            "recipients-includes: stefan@abv.bg, vr@abv.bg" + newLine +
            "from: stefan@abv.bg";

        String ruleDefinition4 = "subject-or-body-includes: AbsolutnoF";

        String ruleDefinition5 = "subject-includes: voda";

        client.addRule("Roskata123", getInboxPath("/Important"), ruleDefinition1, 3);
        client.addRule("Roskata123", getInboxPath("/Spam"), ruleDefinition2, 4);
        client.addRule("Roskata123", getInboxPath("/Important/SUPER_IMPORTANT"), ruleDefinition3, 2);
        client.addRule("vajenRecipient", getInboxPath("/Spam"), ruleDefinition5, 4);

        String emailMetadata1 = "sender: stefan@abv.bg" + newLine +
            "subject: ni6to zna4itelno" + newLine +
            "recipients: vr@abv.bg, rosen123@abv.bg" + newLine +
            "received: 2022-12-03 12:00";
        String emailContent1 = "AbsolutnoF ni6to voda vajno nqma v tozi email";

        Account sender = new Account("stefan@abv.bg", "Stefan");

        client.sendMail("Stefan", emailMetadata1, emailContent1);

        String emailMetadata2 = "sender: stefan@abv.bg" + newLine +
            "subject: ne6to voda ni6to" + newLine +
            "recipients: vr@abv.bg, rosen123@abv.bg" + newLine +
            "received: 2022-12-03 12:00";
        String emailContent2 = "Absolutno ni6to voda vajno nqma vutre v tozi email";

        client.sendMail("Stefan", emailMetadata2, emailContent2);

        //Add other rules
        client.addRule("vajenRecipient", getInboxPath("/Spam"), ruleDefinition4, 1);
        client.addRule("Roskata123", getInboxPath("/Important"), ruleDefinition4, 1);

        String emailMetadata3 =
            "subject: ne6to voda" + newLine +
                "recipients: vr@abv.bg, rosen123@abv.bg" + newLine +
                "received: 2022-12-03 12:00";
        String emailContent3 = "Ni6to voda vjno nqma vutre v tozi email";

        client.sendMail("Stefan", emailMetadata3, emailContent3);

        Account email4Sender = new Account("mvr@abv.bg", "malovajenRecipient");
        String emailMetadata4 =
            "sender: mvr@abv.bg" + newLine +
                "subject: ni6to voda" + newLine +
                "recipients: vr@abv.bg, stefan@abv.bg, rosen123@abv.bg" + newLine +
                "received: 2022-12-03 12:00";
        String emailContent4 = "KURRR";

        client.sendMail("malovajenRecipient", emailMetadata4, emailContent4);

        LocalDateTime expectedReceivedTime = LocalDateTime.of(2022, 12, 3, 12, 0);

        Mail expectedSentMail1 = new Mail(
            sender,
            Set.of(
                "vr@abv.bg",
                "rosen123@abv.bg"
            ),
            "ni6to zna4itelno",
            emailContent1,
            expectedReceivedTime
        );
        Mail expectedSentMail2 = new Mail(
            sender,
            Set.of(
                "vr@abv.bg",
                "rosen123@abv.bg"
            ),
            "ne6to voda ni6to",
            emailContent2,
            expectedReceivedTime
        );
        Mail expectedSentMail3 = new Mail(
            sender,
            Set.of(
                "vr@abv.bg",
                "rosen123@abv.bg"
            ),
            "ne6to voda",
            emailContent3,
            expectedReceivedTime
        );
        Mail expectedSentMail4 = new Mail(
            email4Sender,
            Set.of(
                "vr@abv.bg",
                "rosen123@abv.bg",
                "stefan@abv.bg"
            ),
            "ni6to voda",
            emailContent4,
            expectedReceivedTime
        );

        List<Mail> expectedRosenInbox = List.of(
            expectedSentMail3
        );
        List<Mail> expectedRosenInboxImportant = new ArrayList<>();
        List<Mail> expectedRosenInboxSuperImportant = List.of(
            expectedSentMail2
        );
        List<Mail> expectedRosenInboxSpam = List.of(
            expectedSentMail1,
            expectedSentMail4
        );

        List<Mail> expectedVRInbox = new ArrayList<>();
        List<Mail> expectedVRInboxSpam = List.of(
            expectedSentMail2,
            expectedSentMail1,
            expectedSentMail3,
            expectedSentMail4
        );

        List<Mail> expectedStefanInbox = List.of(
            expectedSentMail4
        );
        List<Mail> expectedMVRInbox = new ArrayList<>();

        List<Mail> expectedRosenSent = new ArrayList<>();
        List<Mail> expectedVRSent = new ArrayList<>();
        List<Mail> expectedMVRSent = List.of(
            expectedSentMail4
        );
        List<Mail> expectedStefanSent = List.of(
            expectedSentMail1,
            expectedSentMail2,
            expectedSentMail3
        );

        var rosenInbox =
            this.client.getMailsFromFolder("Roskata123", Outlook.INBOX_FOLDER_PATH);
        var rosenInboxImportant =
            this.client.getMailsFromFolder("Roskata123", getInboxPath("/Important"));
        var rosenInboxSuperImportant =
            this.client.getMailsFromFolder("Roskata123", getInboxPath("/Important/SUPER_IMPORTANT"));
        var rosenInboxSpam =
            this.client.getMailsFromFolder("Roskata123", getInboxPath("/Spam"));

        var vrInbox =
            this.client.getMailsFromFolder("vajenRecipient", Outlook.INBOX_FOLDER_PATH);
        var vrInboxSpam =
            this.client.getMailsFromFolder("vajenRecipient", getInboxPath("/Spam"));

        var stefanInbox =
            this.client.getMailsFromFolder("Stefan", Outlook.INBOX_FOLDER_PATH);
        var mvrInbox =
            this.client.getMailsFromFolder("malovajenRecipient", Outlook.INBOX_FOLDER_PATH);

        var stefanSent =
            this.client.getMailsFromFolder("Stefan", Outlook.SENT_FOLDER_PATH);
        var mvrSent =
            this.client.getMailsFromFolder("malovajenRecipient", Outlook.SENT_FOLDER_PATH);
        var rosenSent =
            this.client.getMailsFromFolder("Roskata123", Outlook.SENT_FOLDER_PATH);
        var vrSent =
            this.client.getMailsFromFolder("vajenRecipient", Outlook.SENT_FOLDER_PATH);

        testCollectionIsUnmodifiable(rosenInbox);
        testCollectionIsUnmodifiable(rosenInboxImportant);
        testCollectionIsUnmodifiable(rosenInboxSuperImportant);
        testCollectionIsUnmodifiable(rosenInboxSpam);

        testCollectionIsUnmodifiable(vrInbox);
        testCollectionIsUnmodifiable(vrInboxSpam);

        testCollectionIsUnmodifiable(stefanInbox);
        testCollectionIsUnmodifiable(mvrInbox);

        testCollectionIsUnmodifiable(rosenSent);
        testCollectionIsUnmodifiable(vrSent);
        testCollectionIsUnmodifiable(stefanSent);
        testCollectionIsUnmodifiable(mvrSent);

        Assertions.assertIterableEquals(
            expectedRosenInbox,
            rosenInbox,
            "The 'inbox' of Roskata123 is incorrect!"
        );
        Assertions.assertIterableEquals(
            expectedRosenInboxImportant,
            rosenInboxImportant,
            "The 'inbox/Important' of Roskata123 is incorrect!"
        );
        Assertions.assertIterableEquals(
            expectedRosenInboxSuperImportant,
            rosenInboxSuperImportant,
            "The 'inbox/Important/SUPER_IMPORTANT' of Roskata123 is incorrect!"
        );
        Assertions.assertIterableEquals(
            expectedRosenInboxSpam,
            rosenInboxSpam,
            "The 'inbox/Spam' of Roskata123 is incorrect!"
        );

        Assertions.assertIterableEquals(
            expectedVRInbox,
            vrInbox,
            "The 'inbox' of vajenRecipient is incorrect!"
        );
        Assertions.assertIterableEquals(
            expectedVRInboxSpam,
            vrInboxSpam,
            "The 'inbox/Spam' of vajenRecipient is incorrect!"
        );

        Assertions.assertIterableEquals(
            expectedStefanInbox,
            stefanInbox,
            "The 'inbox' of Stefan is incorrect!"
        );
        Assertions.assertIterableEquals(
            expectedMVRInbox,
            mvrInbox,
            "The 'inbox' of malovajenRecipient is incorrect!"
        );

        Assertions.assertIterableEquals(
            expectedStefanSent,
            stefanSent,
            "The 'sent' of Stefan is incorrect!"
        );
        Assertions.assertIterableEquals(
            expectedMVRSent,
            mvrSent,
            "The 'sent' of malovajenRecipient is incorrect!"
        );
        Assertions.assertIterableEquals(
            expectedRosenSent,
            rosenSent,
            "The 'sent' of Roskata123 is incorrect!"
        );
        Assertions.assertIterableEquals(
            expectedVRSent,
            vrSent,
            "The 'sent' of vajenRecipient is incorrect!"
        );
    }

    @Test
    void testGetMailsFromFolderThrowsIllegalArgumentExceptionBecauseNullOrBlankAccountName() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> client.getMailsFromFolder(null, "someFolder"),
            "Expected IllegalArgumentException because of null name, but nothing was thrown!"
        );

        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> client.getMailsFromFolder("", "someFolder"),
            "Expected IllegalArgumentException because of empty name, but nothing was thrown!"
        );

        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> client.getMailsFromFolder("   ", "someFolder"),
            "Expected IllegalArgumentException because of blank name, but nothing was thrown!"
        );
    }

    @Test
    void testGetMailsFromFolderThrowsIllegalArgumentExceptionBecauseNullOrBlankFolderPath() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> client.getMailsFromFolder("someName", null),
            "Expected IllegalArgumentException because of null folder path, but nothing was thrown!"
        );

        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> client.getMailsFromFolder("someName", ""),
            "Expected IllegalArgumentException because of empty folder path, but nothing was thrown!"
        );

        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> client.getMailsFromFolder("someName", "  "),
            "Expected IllegalArgumentException because of blank folder path, but nothing was thrown!"
        );
    }

    @Test
    void testGetMailsFromFolderThrowsAccountNotFoundException() {
        client.addNewAccount("Stefan", "stefan@abv.bg");
        client.addNewAccount("Roskata123", "rosen123@abv.bg");

        client.createFolder("Roskata123", getInboxPath("/Important"));

        Assertions.assertThrows(
            AccountNotFoundException.class,
            () -> client.getMailsFromFolder("nqma me", getInboxPath("/Important")),
            "Expected AccountNotFoundException, but nothing was thrown!"
        );
    }

    @Test
    void testGetMailsFromFolderThrowsFolderNotFoundException() {
        client.addNewAccount("Stefan", "stefan@abv.bg");
        client.addNewAccount("Roskata123", "rosen123@abv.bg");

        client.createFolder("Roskata123", getInboxPath("/Important"));

        Assertions.assertThrows(
            FolderNotFoundException.class,
            () -> client.getMailsFromFolder("Roskata123", getInboxPath("/Important/SUPER_IMPORTANT")),
            "Expected FolderNotFoundException, but nothing was thrown!"
        );
    }

    @Test
    void testReceiveEmailThrowsIllegalArgumentExceptionBecauseNullOrBlankAccountName() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> client.receiveMail(null, "metadata", "content"),
            "Expected IllegalArgumentException because of null name, but nothing was thrown!"
        );

        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> client.receiveMail("", "metadata", "content"),
            "Expected IllegalArgumentException because of empty name, but nothing was thrown!"
        );

        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> client.receiveMail("   ", "metadata", "content"),
            "Expected IllegalArgumentException because of blank name, but nothing was thrown!"
        );
    }

    @Test
    void testReceiveEmailThrowsIllegalArgumentExceptionBecauseNullOrBlankMetadata() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> client.receiveMail("name", null, "content"),
            "Expected IllegalArgumentException because of null metadata, but nothing was thrown!"
        );

        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> client.receiveMail("name", "", "content"),
            "Expected IllegalArgumentException because of empty metadata, but nothing was thrown!"
        );

        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> client.receiveMail("name", "   ", "content"),
            "Expected IllegalArgumentException because of blank metadata, but nothing was thrown!"
        );
    }

    @Test
    void testReceiveEmailThrowsIllegalArgumentExceptionBecauseNullOrBlankContent() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> client.receiveMail("name", "metadata", null),
            "Expected IllegalArgumentException because of null content, but nothing was thrown!"
        );

        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> client.receiveMail("name", "metadata", ""),
            "Expected IllegalArgumentException because of empty content, but nothing was thrown!"
        );

        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> client.receiveMail("name", "metadata", "   "),
            "Expected IllegalArgumentException because of blank content, but nothing was thrown!"
        );
    }

    @Test
    void testReceiveEmailThrowsAccountNotFoundException() {

        client.addNewAccount("Roskata123", "rosen@abv.bg");

        Assertions.assertThrows(
            AccountNotFoundException.class,
            () -> client.receiveMail("Stefan", "metadata", "content"),
            "Expected AccountNotFoundException, but nothing was thrown!"
        );
    }
}
