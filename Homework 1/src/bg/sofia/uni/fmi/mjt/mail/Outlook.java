package bg.sofia.uni.fmi.mjt.mail;

import bg.sofia.uni.fmi.mjt.mail.exceptions.AccountAlreadyExistsException;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Outlook implements MailClient {

    private List<Account> accounts;

    private final Path databaseRoot;
    private final Path accountsTable;

    private final String newLine = System.getProperty("line.separator");
    private final String entrySeparator = "/*****************************************/" + newLine;
    private final String colSeparator = System.getProperty("line.separator");
    private final String accountTableName = "accounts.txt";
    private final String accountEmailFolderTemplate = "%s_email";
    private final String accountInboxFolder = "Inbox";
    private final String accountSentFolder = "Sent";

    public Outlook() {
        this(Path.of("src"));
    }

    public Outlook(Path databaseRoot) {

        this.databaseRoot = databaseRoot;
        this.accountsTable = Path.of(databaseRoot.toString(), this.accountTableName);

        //Pravq go za da raboti v Grader
        try {
            this.initialiseAccounts();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Path getAccountEmailFolderPath(Outlook client, String accountName) {
        return Path.of(client.databaseRoot.toString(), String.format(client.accountEmailFolderTemplate, accountName));
    }

    public static Path getAccountInboxFolderPath(Outlook client, String accountName) {
        return Path.of(client.databaseRoot.toString(),
            String.format(client.accountEmailFolderTemplate, accountName),
            client.accountInboxFolder);
    }

    public static Path getAccountSentFolderPath(Outlook client, String accountName) {
        return Path.of(client.databaseRoot.toString(),
            String.format(client.accountEmailFolderTemplate, accountName),
            client.accountSentFolder);
    }

//    private Path getAccountEmailFolderPath(String accountName) {
//        return Path.of(this.databaseRoot.toString(), String.format(this.accountEmailFolderTemplate, accountName));
//    }
//
//    private Path getAccountInboxFolderPath(String accountName) {
//        return Path.of(this.databaseRoot.toString(),
//            String.format(this.accountEmailFolderTemplate, accountName),
//            this.accountInboxFolder);
//    }
//
//    private Path getAccountSentFolderPath(String accountName) {
//        return Path.of(this.databaseRoot.toString(),
//            String.format(this.accountEmailFolderTemplate, accountName),
//            this.accountSentFolder);
//    }

    private String getEntrySeparatorFromTable(Reader reader) throws IOException {
        String currentEntrySeparator = "";

        for (int i = 0; i < this.entrySeparator.length(); i++) {
            int newSymbolInt = reader.read();

            if (newSymbolInt == -1) return null;

            if (newSymbolInt != this.entrySeparator.charAt(i)) return null;

            currentEntrySeparator += String.valueOf((char) newSymbolInt);
        }

        return currentEntrySeparator;
    }

    private String getColFromTable(Reader reader) throws IOException {
        String colData = "";

        while (true) {
            int newSymbolInt = reader.read();

            if (newSymbolInt == -1) {
                return null;
            }

            String newSymbol = String.valueOf((char) newSymbolInt);

            String tempColSeparator = null;

            if (this.colSeparator.startsWith(newSymbol)) {
                tempColSeparator = newSymbol;

                for (int i = 1; i < this.colSeparator.length(); i++) {
                    char colSepChar = this.colSeparator.charAt(i);
                    newSymbolInt = reader.read();

                    if (newSymbolInt == -1) {
                        return null;
                    }

                    tempColSeparator += (char) newSymbolInt;

                    if (newSymbolInt != colSepChar) break;
                }

                if (!tempColSeparator.equals(this.colSeparator)) {
                    colData += tempColSeparator;
                } else {
                    return colData;
                }
            } else {
                colData += (char) newSymbolInt;
            }
        }
    }

    private void initialiseAccounts() throws IOException {

        this.accounts = new ArrayList<Account>();

        if (!Files.exists(this.accountsTable)) {
            Files.createDirectories(this.accountsTable.getParent());
            Files.createFile(this.accountsTable);
            return;
        }

        try (var bufferedReader = Files.newBufferedReader(this.accountsTable)) {
            bufferedReader.mark(1);
            int newSymbolInt = bufferedReader.read();

            if (newSymbolInt == -1) {
                return;
            }

            Boolean startWithNewLine = false;
            //Trim new line at start of file
            if (this.newLine.startsWith(String.valueOf((char) newSymbolInt))) {
                startWithNewLine = true;

                for (int i = 1; i < this.newLine.length(); i++) {
                    bufferedReader.read();
                }
            }

            if (!startWithNewLine) {
                bufferedReader.reset();
            }

            while (true) {

                String email = getColFromTable(bufferedReader);
                if (email == null) break;

                String name = getColFromTable(bufferedReader);
                if (name == null) break;

                String curEntrySeparator = getEntrySeparatorFromTable(bufferedReader);
                if (curEntrySeparator == null || !curEntrySeparator.equals(this.entrySeparator)) break;

                this.accounts.add(new Account(name, email));
            }

        }
    }

    private void addAccountEntry(Account newAcc) {

        try (var bufferedWriter = Files.newBufferedWriter(this.accountsTable,
            StandardOpenOption.APPEND)) {

            bufferedWriter.write(newAcc.name());
            bufferedWriter.write(this.colSeparator);
            bufferedWriter.write(newAcc.emailAddress());
            bufferedWriter.write(this.colSeparator);
            bufferedWriter.write(this.entrySeparator);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private Boolean accountExists(String accountName) {
        for (Account acc : this.accounts) {
            if (acc.name().equals(accountName)) return true;
        }

        return false;
    }

    @Override
    public Account addNewAccount(String accountName, String email) {
        if (accountName == null || accountName.isBlank()) {
            throw new IllegalArgumentException("The account name is null or empty or blank!");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("The email is null or empty or blank!");
        }

        if (this.accountExists(accountName)) {
            throw new AccountAlreadyExistsException("An account with name: '" +
                accountName + "' already exists!");
        }

        var newAcc = new Account(email, accountName);
        this.accounts.add(newAcc);

        addAccountEntry(newAcc);

        //Create the new folders
        Path accountEmailFolder = Outlook.getAccountEmailFolderPath(this, accountName);
        Path accountInboxFolder = Outlook.getAccountInboxFolderPath(this, accountName);
        Path accountSentFolder = Outlook.getAccountSentFolderPath(this, accountName);

        try {
            Files.createDirectory(accountEmailFolder);
            Files.createDirectory(accountInboxFolder);
            Files.createDirectory(accountSentFolder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return newAcc;
    }

    @Override
    public void createFolder(String accountName, String path) {

    }

    @Override
    public void addRule(String accountName, String folderPath, String ruleDefinition, int priority) {

    }

    @Override
    public void receiveMail(String accountName, String mailMetadata, String mailContent) {

    }

    @Override
    public Collection<Mail> getMailsFromFolder(String account, String folderPath) {
        return null;
    }

    @Override
    public void sendMail(String accountName, String mailMetadata, String mailContent) {

    }
}
