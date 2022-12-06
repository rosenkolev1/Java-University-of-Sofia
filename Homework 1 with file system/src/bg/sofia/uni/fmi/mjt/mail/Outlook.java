package bg.sofia.uni.fmi.mjt.mail;

import bg.sofia.uni.fmi.mjt.mail.exceptions.*;

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
    private final String pathSeparator = "/";
    private final String accountTableName = "accounts.txt";
    private final String accountRulesTableName = "rules.txt";
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

        if (!Files.isRegularFile(this.accountsTable)) {
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

    private Path getPathStartingFromInbox(String accountName, String path) {
        Path newFolderPath = Path.of(Outlook.getAccountInboxFolderPath(this, accountName).toString(), path);

        return newFolderPath;
    }

    private Path getRulePathForAccount(String accountName) {
        return Path.of(
            Outlook.getAccountEmailFolderPath(this, accountName).toString(),
            accountRulesTableName);
    }

    private void addRuleEntry(AccountRule rule) throws IOException {

        Path rulePath = getRulePathForAccount(rule.getAccountName());

        if(!Files.isRegularFile(rulePath)) {
            Files.createFile(rulePath);
        }

        try (var bufferedWriter = Files.newBufferedWriter(
            rulePath,
            StandardOpenOption.APPEND)) {

            bufferedWriter.write(rule.getRuleDefinition());
            bufferedWriter.write(this.colSeparator);
            bufferedWriter.write(rule.getFolderPath());
            bufferedWriter.write(this.colSeparator);
            bufferedWriter.write(String.valueOf(rule.getPriority()));
            bufferedWriter.write(this.colSeparator);
            bufferedWriter.write(this.entrySeparator);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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
        if(accountName == null || accountName.isBlank()){
            throw new IllegalArgumentException("The account name is null, empty or blank!");
        }
        if(path == null || path.isBlank()) {
            throw new IllegalArgumentException("The path is null, empty or blank!");
        }

        if(!this.accountExists(accountName)) {
            throw new AccountNotFoundException("The given account does not exist!");
        }

        List<String> paths = List.of(path.split(this.pathSeparator));

        if(!path.startsWith(this.pathSeparator)) {
            throw new InvalidPathException("The path doesn't start from the root(Inbox) folder!");
        }

        //Check if the some of the subpaths do not exist
        for(int i = 1; i < paths.size() - 1; i++) {
            Path curPathToCheck = Outlook.getAccountInboxFolderPath(this, accountName);

            for(String pathPart : paths.subList(1, i + 1)) {
                curPathToCheck = Path.of(curPathToCheck.toString(), pathPart);
            }

            if(!Files.isDirectory(curPathToCheck)) {
                throw new InvalidPathException("The subfolder path '" + curPathToCheck.toString() +
                    "' does not exist!");
            }
        }

        Path newFolderPath = getPathStartingFromInbox(accountName, path);

        if(Files.isDirectory(newFolderPath)) {
            throw new FolderAlreadyExistsException("The new folder already exists");
        }

        //Finally all the checks have passed, hopefully
        try {
            Files.createDirectory(newFolderPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addRule(String accountName, String folderPath, String ruleDefinition, int priority) {
        if(accountName == null || accountName.isBlank()){
            throw new IllegalArgumentException("The account name is null, empty or blank!");
        }

        if(folderPath == null || folderPath.isBlank()){
            throw new IllegalArgumentException("The folder path is null, empty or blank!");
        }

        if(priority < 1 || priority > 10){
            throw new IllegalArgumentException("The priority is not in the range [1-10]");
        }

        if(!this.accountExists(accountName)) {
            throw new AccountNotFoundException("The given account does not exist!");
        }

        if(!Files.isDirectory(getPathStartingFromInbox(accountName, folderPath))){
            throw new FolderNotFoundException("The given folder does not exist!");
        }

        if(!AccountRule.ruleDefinitionIsValid(ruleDefinition)) {
            throw new RuleAlreadyDefinedException(
                "The rule definition is incorrect because a rule condition is met more than once"
            );
        }



        AccountRule newAccRule = new AccountRule(accountName, folderPath, ruleDefinition, priority);

        try {
            addRuleEntry(newAccRule);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
