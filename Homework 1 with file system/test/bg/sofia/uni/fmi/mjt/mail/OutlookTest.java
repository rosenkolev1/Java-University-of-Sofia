package bg.sofia.uni.fmi.mjt.mail;

import bg.sofia.uni.fmi.mjt.mail.exceptions.AccountAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.AccountNotFoundException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.FolderAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.InvalidPathException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class OutlookTest {

    private final Path testDatabaseRoot = Path.of("test", "test_files");
    private final Path testAccountsTable = Path.of(testDatabaseRoot.toString(), "accounts.txt");
    private final String newLine = System.getProperty("line.separator");
    private final String entrySeparator = "/*****************************************/"
        + newLine;
    private final String colSeparator = System.getProperty("line.separator");

    private Outlook client;

    String getDataFromFile(Path filePath) throws IOException {
        String output = "";

        try (var reader = Files.newBufferedReader(filePath)){
            int readerCharInt = reader.read();

            while(readerCharInt != -1) {
                output += (char) readerCharInt;
                readerCharInt = reader.read();
            }
        }

        return output;
    }

    void writeDataIntoFile(Path filePath, String data) throws IOException {
        try (var writer = Files.newBufferedWriter(filePath)){
            writer.write(data);
        }
    }
    void appendDataIntoFile(Path filePath, String data) throws IOException {
        try (var writer = Files.newBufferedWriter(filePath, StandardOpenOption.APPEND)){
            writer.write(data);
        }
    }

    void removeWholeDirectory(Path dir) throws IOException {
        //Recursively delete everything inside the test root database folder
        try (var paths = Files.newDirectoryStream(dir)){

            for(Path curPath : paths) {

                if(Files.isRegularFile(curPath)) {
                    Files.delete(curPath);
                }
                else if(Files.isDirectory(curPath)) {
                    removeWholeDirectory(curPath);
                }
            }

        }

        //Remove the test root
        Files.delete(dir);
    }

    void createFileWithDirectories(Path dir) throws IOException {
        Files.createDirectories(dir.getParent());
        Files.createFile(dir);
    }

    void seedDatabase(List<Account> accounts, List<Mail> emails) throws IOException {

        if(accounts == null || accounts.isEmpty()) {
            return;
        }

        createFileWithDirectories(testAccountsTable);

        String oldTestAccountTableData = "";

        for(Account acc : accounts) {
            oldTestAccountTableData += acc.name() +
                colSeparator + acc.emailAddress() + colSeparator +
                entrySeparator;
        }
        writeDataIntoFile(testAccountsTable, oldTestAccountTableData);

        client = new Outlook(testDatabaseRoot);

        //Create emails and inbox and sent directories
        for(Account acc : accounts) {
            Path oldInboxPath = Outlook.getAccountInboxFolderPath(client, acc.name());
            Path oldSentPath = Outlook.getAccountSentFolderPath(client, acc.name());
            Files.createDirectories(oldInboxPath);
            Files.createDirectories(oldSentPath);
        }

        if(emails == null || emails.isEmpty()) {
            return;
        }
    }

    void seedDatabase(List<Account> accounts) throws IOException {
        seedDatabase(accounts, null);
    }

    void seedDatabase() throws IOException {
        seedDatabase(null, null);
    }

    @AfterEach
    void removeTestFilesAndDirectories() throws IOException {
        removeWholeDirectory(testDatabaseRoot);
    }

    @Test
    void testAddNewAccountInEmptyTable() throws IOException{
        client = new Outlook(testDatabaseRoot);

        String newAccName = "Roskata123";
        String newAccEmail = "roskata123@abv.bg";
        Account newAccount = client.addNewAccount(newAccName, newAccEmail);

        Account expectedNewAccount = new Account(newAccEmail, newAccName);

        String expectedAccountsTableData = "Roskata123" +
            colSeparator + "roskata123@abv.bg" + colSeparator +
            entrySeparator;

        Assertions.assertEquals(expectedNewAccount, newAccount,
            "The returned account differs from the expected!");

        String accountsTableData = getDataFromFile(testAccountsTable);

        Assertions.assertEquals(expectedAccountsTableData, accountsTableData,
            "The accounts table after adding an account differs from the expected!");

        Path inboxPath = Outlook.getAccountInboxFolderPath(client, newAccName);
        Path sentPath = Outlook.getAccountSentFolderPath(client, newAccName);

        Assertions.assertTrue(Files.exists(inboxPath),
            "The inbox folder for the account doesn't exist in the email!");

        Assertions.assertTrue(Files.exists(sentPath),
            "The inbox folder for the account doesn't exist in the email!");
    }

    @Test
    void testAddNewAccountInEmptyTableWithAlreadyExistingFileWithLeadingNewline() throws IOException{
        createFileWithDirectories(testAccountsTable);
        client = new Outlook(testDatabaseRoot);

        writeDataIntoFile(testAccountsTable, newLine);

        String newAccName = "Roskata123";
        String newAccEmail = "roskata123@abv.bg";
        Account newAccount = client.addNewAccount(newAccName, newAccEmail);

        Account expectedNewAccount = new Account(newAccEmail, newAccName);

        String expectedAccountsTableData = newLine + newAccName +
            colSeparator + newAccEmail + colSeparator +
            entrySeparator;

        Assertions.assertEquals(expectedNewAccount, newAccount,
            "The returned account differs from the expected!");

        String accountsTableData = getDataFromFile(testAccountsTable);

        Assertions.assertEquals(expectedAccountsTableData, accountsTableData,
            "The accounts table after adding an account differs from the expected!");

        Path inboxPath = Outlook.getAccountInboxFolderPath(client, newAccName);
        Path sentPath = Outlook.getAccountSentFolderPath(client, newAccName);

        Assertions.assertTrue(Files.exists(inboxPath),
            "The inbox folder for the account doesn't exist in the email!");

        Assertions.assertTrue(Files.exists(sentPath),
            "The inbox folder for the account doesn't exist in the email!");
    }

    @Test
    void testAddNewAccountInEmptyTableWithAlreadyExistingFile() throws IOException{
        createFileWithDirectories(testAccountsTable);
        client = new Outlook(testDatabaseRoot);

        String newAccName = "Roskata123";
        String newAccEmail = "roskata123@abv.bg";
        Account newAccount = client.addNewAccount(newAccName, newAccEmail);

        Account expectedNewAccount = new Account(newAccEmail, newAccName);

        String expectedAccountsTableData = newAccName +
            colSeparator + newAccEmail + colSeparator +
            entrySeparator;

        Assertions.assertEquals(expectedNewAccount, newAccount,
            "The returned account differs from the expected!");

        String accountsTableData = getDataFromFile(testAccountsTable);

        Assertions.assertEquals(expectedAccountsTableData, accountsTableData,
            "The accounts table after adding an account differs from the expected!");

        Path inboxPath = Outlook.getAccountInboxFolderPath(client, newAccName);
        Path sentPath = Outlook.getAccountSentFolderPath(client, newAccName);

        Assertions.assertTrue(Files.exists(inboxPath),
            "The inbox folder for the account doesn't exist in the email!");

        Assertions.assertTrue(Files.exists(sentPath),
            "The inbox folder for the account doesn't exist in the email!");
    }

    @Test
    void testAddNewAccountInExistingTableWithLeadingNewline() throws IOException{
        List<Account> existingAccounts = List.of(new Account("roskata123@abv.bg", "Roskata123"));
        seedDatabase(existingAccounts);
        String oldTestAccountTableData = getDataFromFile(this.testAccountsTable);

        String newAccName = "Stefan";
        String newAccEmail = "stefan@abv.bg";
        Account newAccount = client.addNewAccount(newAccName, newAccEmail);

        Account expectedNewAccount = new Account(newAccEmail, newAccName);

        String expectedAccountsTableData = oldTestAccountTableData +
            newAccName + colSeparator +
            newAccEmail + colSeparator +
            entrySeparator;

        Assertions.assertEquals(expectedNewAccount, newAccount,
            "The returned account differs from the expected!");

        String accountsTableData = getDataFromFile(testAccountsTable);

        Assertions.assertEquals(expectedAccountsTableData, accountsTableData,
            "The accounts table after adding an account differs from the expected!");

        Path inboxPath = Outlook.getAccountInboxFolderPath(client, newAccName);
        Path sentPath = Outlook.getAccountSentFolderPath(client, newAccName);

        Assertions.assertTrue(Files.exists(inboxPath),
            "The inbox folder for the account doesn't exist in the email!");

        Assertions.assertTrue(Files.exists(sentPath),
            "The inbox folder for the account doesn't exist in the email!");
    }

    @Test
    void testAddNewAccountInExistingTable() throws IOException{
        List<Account> existingAccounts = List.of(new Account("roskata123@abv.bg", "Roskata123"));
        seedDatabase(existingAccounts);
        String oldTestAccountTableData = getDataFromFile(this.testAccountsTable);

        String newAccName = "Stefan";
        String newAccEmail = "stefan@abv.bg";
        Account newAccount = client.addNewAccount(newAccName, newAccEmail);

        Account expectedNewAccount = new Account(newAccEmail, newAccName);

        String expectedAccountsTableData = oldTestAccountTableData +
            newAccName + colSeparator +
            newAccEmail + colSeparator +
            entrySeparator;

        Assertions.assertEquals(expectedNewAccount, newAccount,
            "The returned account differs from the expected!");

        String accountsTableData = getDataFromFile(testAccountsTable);

        Assertions.assertEquals(expectedAccountsTableData, accountsTableData,
            "The accounts table after adding an account differs from the expected!");

        Path inboxPath = Outlook.getAccountInboxFolderPath(client, newAccName);
        Path sentPath = Outlook.getAccountSentFolderPath(client, newAccName);

        Assertions.assertTrue(Files.exists(inboxPath),
            "The inbox folder for the account doesn't exist in the email!");

        Assertions.assertTrue(Files.exists(sentPath),
            "The inbox folder for the account doesn't exist in the email!");
    }

    @Test
    void testAddNewAccountAlreadyExists() throws IOException{
        List<Account> existingAccounts = List.of(new Account("roskata123@abv.bg", "Roskata123"));
        seedDatabase(existingAccounts);

        String newAccName = "Roskata123";
        String newAccEmail = "stefan@abv.bg";

        Assertions.assertThrows(AccountAlreadyExistsException.class,
            () -> client.addNewAccount(newAccName, newAccEmail),
            "Expected AccountAlreadyExistsException but nothing was thrown!");
    }

    @Test
    void testAddNewAccountNullOrEmptyOrBlankName() throws IOException{
        client = new Outlook(testDatabaseRoot);

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
    void testAddNewAccountNullOrEmptyOrBlankEmail() throws IOException{
        client = new Outlook(testDatabaseRoot);

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
        this.client = new Outlook(this.testDatabaseRoot);

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> this.client.createFolder(null, "/Important"),
            "Expected IllegalArgumentException because of null name, but nothing was thrown!");

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> this.client.createFolder("", "/Important"),
            "Expected IllegalArgumentException because of empty name, but nothing was thrown!");

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> this.client.createFolder("   ", "/Important"),
            "Expected IllegalArgumentException because of blank name, but nothing was thrown!");
    }

    @Test
    void testCreateFolderThrowsIllegalArgumentExceptionBecausePathIsNullOrBlank(){
        this.client = new Outlook(this.testDatabaseRoot);

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
    void testCreateFolderThrowsAccountNotFoundException() throws IOException {
        List<Account> existingAccounts = List.of(new Account("roskata123@abv.bg", "Roskata123"),
            new Account("stefan@abv.bg", "Stefan"));
        seedDatabase(existingAccounts);
        String oldTestAccountTableData = getDataFromFile(this.testAccountsTable);

        Assertions.assertThrows(AccountNotFoundException.class,
            () -> this.client.createFolder("Roskata", "/Important"),
            "Expected AccountNotFoundException, but nothing was thrown!");
    }

    @Test
    void testCreateFolderThrowsInvalidPathException() throws IOException {

        List<Account> existingAccounts = List.of(new Account("roskata123@abv.bg", "Roskata123"),
            new Account("stefan@abv.bg", "Stefan"));
        seedDatabase(existingAccounts);

        Assertions.assertThrows(InvalidPathException.class,
            () -> client.createFolder("Roskata123", "Important"),
            "Expected InvalidPathException because folder was not in root(Inbox), but nothing was thrown!");

        Assertions.assertThrows(InvalidPathException.class,
            () -> client.createFolder("Roskata123", "/Important/Documents"),
            "Expected InvalidPathException because subfolder was not found, but nothing was thrown!");

        Assertions.assertThrows(InvalidPathException.class,
            () -> client.createFolder("Roskata123", "/Important/Documents"),
            "Expected InvalidPathException because subfolder was not found, but nothing was thrown!");

        Path roskata123InboxImportant = Path.of(
            Outlook.getAccountInboxFolderPath(client, "Roskata123").toString(),
            "Important");
        Files.createDirectories(roskata123InboxImportant);

        Assertions.assertThrows(InvalidPathException.class,
            () -> client.createFolder("Roskata123", "/Important/Documents/PDF"),
            "Expected InvalidPathException because subfolder was not found, but nothing was thrown!");
    }

    @Test
    void testCreateFolderThrowsFolderAlreadyExistsException() throws IOException {

        List<Account> existingAccounts = List.of(new Account("roskata123@abv.bg", "Roskata123"),
            new Account("stefan@abv.bg", "Stefan"));
        seedDatabase(existingAccounts);

        Assertions.assertThrows(FolderAlreadyExistsException.class,
            () -> client.createFolder("Roskata123", "/"),
            "Expected FolderAlreadyExistsException, but nothing was thrown!");

        Path roskata123InboxImportant = Path.of(
            Outlook.getAccountInboxFolderPath(client, "Roskata123").toString(),
            "Important");
        Files.createDirectories(roskata123InboxImportant);

        Assertions.assertThrows(FolderAlreadyExistsException.class,
            () -> client.createFolder("Roskata123", "/Important"),
            "Expected FolderAlreadyExistsException, but nothing was thrown!");

        Assertions.assertThrows(FolderAlreadyExistsException.class,
            () -> client.createFolder("Roskata123", "/Important/"),
            "Expected FolderAlreadyExistsException, but nothing was thrown!");

        Assertions.assertThrows(FolderAlreadyExistsException.class,
            () -> client.createFolder("Roskata123", "/Important//"),
            "Expected FolderAlreadyExistsException, but nothing was thrown!");
    }
}
