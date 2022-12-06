package bg.sofia.uni.fmi.mjt.mail;

import bg.sofia.uni.fmi.mjt.mail.exceptions.*;

import java.util.*;

public class Outlook implements MailClient {

    private static final String INBOX_FOLDER = "inbox";
    private static final String SENT_FOLDER = "sent";
    public static final String INBOX_FOLDER_PATH = "/" + INBOX_FOLDER;
    public static final String SENT_FOLDER_PATH = "/" + SENT_FOLDER;

    private Mail receiveEmailTemp = null;
    private List<Account> accounts;

    private List<AccountRule> rules;

    //The inbox folders and subfolders
    private List<AccountFolder> accountsInboxFolders;

    //The sent folders
    private List<AccountFolder> accountsSentFolders;

    //Stored emails inside inbox folder
    private List<StoredMail> storedInboxMails;

    //Stored emails inside sent folder
    private List<StoredMail> storedSentMails;

    private List<Mail> mails;

    public Outlook() {
        accounts = new ArrayList<>();
        rules = new ArrayList<>();
        accountsInboxFolders = new ArrayList<>();
        accountsSentFolders = new ArrayList<>();
        mails = new ArrayList<>();
        storedInboxMails = new ArrayList<>();
        storedSentMails = new ArrayList<>();
    }

    private Boolean accountExists(String accountName) {
        for (Account acc : this.accounts) {
            if (acc.name().equals(accountName)) return true;
        }

        return false;
    }

    private Boolean accountExistsEmail(String accountEmail) {
        for (Account acc : this.accounts) {
            if (acc.emailAddress().equals(accountEmail)) return true;
        }

        return false;
    }

    private List<String> getInboxFoldersPathsForAccount(String accountName) {
        List<String> inboxFoldersPaths = new ArrayList<>();

        for(AccountFolder folder : this.accountsInboxFolders) {
            if(folder.accountName().equals(accountName)){
                inboxFoldersPaths.add(folder.path());
            }
        }

        return inboxFoldersPaths;
    }

    private Account getAccountByName(String name) {
        for(Account acc : this.accounts) {
            if(acc.name().equals(name)) {
                return acc;
            }
        }

        return null;
    }

    private Account getAccountByEmail(String email) {
        for(Account acc : this.accounts) {
            if(acc.emailAddress().equals(email)) {
                return acc;
            }
        }

        return null;
    }

    private void validateIsNotNullEmptyOrBlank(String data, String name) {
        if(data == null || data.isBlank()){
            throw new IllegalArgumentException("The " + name + " is null, empty or blank!");
        }
    }

    public Mail createMail(String senderEmail, String metadata, String content){
        Account sender = this.getAccountByEmail(senderEmail);

        return Mail.createMail(sender, metadata, content);
    }

    private String getFolderPathForMail(Mail mail, List<AccountRule> accountRules) {
        if(accountRules.isEmpty()) {
            return this.INBOX_FOLDER_PATH;
        }

        AccountRule highestPriorityRule = accountRules.get(0);

        boolean rulesHasChangedOnce = false;

        for(int i = 1; i < accountRules.size(); i++) {
            AccountRule curRule = accountRules.get(i);

            if(curRule.matchesMail(mail)) {
                if(!highestPriorityRule.matchesMail(mail) ||
                    curRule.getPriority() < highestPriorityRule.getPriority()) {

                    highestPriorityRule = curRule;
                    rulesHasChangedOnce = true;
                }
            }
        }

        String folderPathForEmail = null;

        if(!rulesHasChangedOnce && !highestPriorityRule.matchesMail(mail)) {
            folderPathForEmail = this.INBOX_FOLDER_PATH;
        }
        else {
            folderPathForEmail = highestPriorityRule.getFolderPath();
        }

        return folderPathForEmail;
    }

    private AccountFolder getInboxFolder(String accountName, String folderPath) {
        for(var folder : this.accountsInboxFolders) {
            if(folder.accountName().equals(accountName) &&
                folder.path().equals(folderPath)) {
                return folder;
            }
        }

        return null;
    }

    private List<StoredMail> getStoredInboxMailsForAccount(String accountName) {
        List<StoredMail> filteredMails = new ArrayList<>();

        for(var storedMail : this.storedInboxMails) {
            if(storedMail.inboxInfo().accountName().equals(accountName)) {
                filteredMails.add(storedMail);
            }
        }

        return filteredMails;
    }

    private List<StoredMail> getStoredMails(String accountName, String folderPath) {
        List<StoredMail> filteredMails = new ArrayList<>();

        //Check inbox mails
        for(var storedMail : this.storedInboxMails) {
            if(storedMail.inboxInfo().accountName().equals(accountName) &&
                storedMail.inboxInfo().path().equals(folderPath)) {
                filteredMails.add(storedMail);
            }
        }

        //Check sent mails
        for(var storedMail : this.storedSentMails) {
            if(storedMail.inboxInfo().accountName().equals(accountName) &&
                storedMail.inboxInfo().path().equals(folderPath)) {
                filteredMails.add(storedMail);
            }
        }

        return filteredMails;
    }

    public List<StoredMail> getStoredInboxMailsForAccountAsUnmodifiable(String accountName) {
        return Collections.unmodifiableList(this.getStoredInboxMailsForAccount(accountName));
    }

    private List<StoredMail> getStoredSentMailsForAccount(String accountName) {
        List<StoredMail> filteredMails = new ArrayList<>();

        for(var storedMail : this.storedSentMails) {
            if(storedMail.inboxInfo().accountName().equals(accountName)) {
                filteredMails.add(storedMail);
            }
        }

        return filteredMails;
    }

    public List<StoredMail> getStoredSentMailsForAccountAsUnmodifiable(String accountName) {
        return Collections.unmodifiableList(this.getStoredSentMailsForAccount(accountName));
    }

    public List<AccountFolder> getInboxFoldersForAcc(String accountName) {
        List<AccountFolder> filteredFolders = new ArrayList<>();

        for(var folder : this.accountsInboxFolders) {
            if(folder.accountName().equals(accountName)) {
                filteredFolders.add(folder);
            }
        }

        return Collections.unmodifiableList(filteredFolders);
    }

    public AccountFolder getSentFolderForAcc(String accountName) {

        for(var folder : this.accountsSentFolders) {
            if(folder.accountName().equals(accountName)) {
                return folder;
            }
        }

        return null;
    }

    public List<AccountRule> getAccountRulesForAcc(String accountName) {
        List<AccountRule> filteredRules = new ArrayList<>();

        for(var rule : this.rules) {
            if(rule.getAccountName().equals(accountName)) {
                filteredRules.add(rule);
            }
        }

        return Collections.unmodifiableList(filteredRules);
    }

    @Override
    public Account addNewAccount(String accountName, String email) {

        validateIsNotNullEmptyOrBlank(accountName, "account name");
        validateIsNotNullEmptyOrBlank(email, "email");

        if (this.accountExists(accountName) || this.accountExistsEmail(email)) {
            throw new AccountAlreadyExistsException("An account with this name or email already exists!");
        }

        var newAcc = new Account(email, accountName);
        this.accounts.add(newAcc);

        //Add the inbox folder and sent folder to the user
        this.accountsInboxFolders.add(new AccountFolder(accountName, this.INBOX_FOLDER_PATH));
        this.accountsSentFolders.add(new AccountFolder(accountName, this.SENT_FOLDER_PATH));

        return newAcc;
    }

    @Override
    public void createFolder(String accountName, String path) {

        validateIsNotNullEmptyOrBlank(accountName, "account name");
        validateIsNotNullEmptyOrBlank(path, "path");

        if(!this.accountExists(accountName)) {
            throw new AccountNotFoundException("The given account does not exist!");
        }

        List<String> paths = List.of(path.split("/"));

        if(paths.size() <= 1 || path.endsWith("/")) {
            throw new InvalidPathException("The path given is invalid!");
        }

        paths = paths.subList(1, paths.size());

        for(String curPath : paths) {
            if(curPath.isEmpty()) {
                throw new InvalidPathException("The path given is invalid!");
            }
        }

        if(!path.startsWith(this.INBOX_FOLDER_PATH)) {
            throw new InvalidPathException("The path doesn't start from the root(inbox) folder!");
        }

        if(!path.equals(this.INBOX_FOLDER_PATH) && !path.startsWith(this.INBOX_FOLDER_PATH + "/")) {
            throw new InvalidPathException("The path doesn't start from the root(inbox) folder!");
        }

        List<String> inboxFolders = getInboxFoldersPathsForAccount(accountName);

        //Check if some of the subpaths do not exist
        for(int i = 1; i < paths.size() - 1; i++) {
            String curPathToCheck = "/" + String.join("/", paths.subList(0, i + 1));

            if(!inboxFolders.contains(curPathToCheck)) {
                throw new InvalidPathException("The subfolder path '" + curPathToCheck.toString() +
                    "' does not exist!");
            }
        }

        if(inboxFolders.contains(path)) {
            throw new FolderAlreadyExistsException("The new folder already exists");
        }

        AccountFolder newFolder = new AccountFolder(accountName, path);
        this.accountsInboxFolders.add(newFolder);
    }

    @Override
    public void addRule(String accountName, String folderPath, String ruleDefinition, int priority) {

        validateIsNotNullEmptyOrBlank(accountName, "account name");
        validateIsNotNullEmptyOrBlank(folderPath, "folder path");
        validateIsNotNullEmptyOrBlank(ruleDefinition, "rule definition");

        if(priority < 1 || priority > 10){
            throw new IllegalArgumentException("The priority is not in the range [1-10]");
        }

        if(!this.accountExists(accountName)) {
            throw new AccountNotFoundException("The given account does not exist!");
        }

        List<String> inboxFolders = getInboxFoldersPathsForAccount(accountName);

        if(!inboxFolders.contains(folderPath)){
            throw new FolderNotFoundException("The given folder does not exist!");
        }

        if(!AccountRule.ruleDefinitionIsValid(ruleDefinition)) {
            throw new RuleAlreadyDefinedException(
                "The rule definition is incorrect because a rule condition is met more than once"
            );
        }

        AccountRule newAccRule = new AccountRule(accountName, folderPath, ruleDefinition, priority);

        for(AccountRule accRule : this.rules) {
            if(accRule.equals(newAccRule)) {
                throw new RuleAlreadyDefinedException("The rule has already been added!");
            }
        }

        this.rules.add(newAccRule);

        //Recalculate the positions of stored emails directly inside /inbox based on the new rule
        var storedMails = this.getStoredMails(accountName, INBOX_FOLDER_PATH);
        var accountRules = this.getAccountRulesForAcc(accountName);

        for(var storedMail : storedMails) {
            String newFolderPath = this.getFolderPathForMail(storedMail.mail(), accountRules);

            this.storedInboxMails.remove(storedMail);
            this.storedInboxMails.add(new StoredMail(storedMail.mail(), new AccountFolder(accountName, newFolderPath)));
        }
    }

    @Override
    public void receiveMail(String accountName, String mailMetadata, String mailContent) {

        validateIsNotNullEmptyOrBlank(accountName, "account name");
        validateIsNotNullEmptyOrBlank(mailMetadata, "mail metadata");
        validateIsNotNullEmptyOrBlank(mailContent, "mail content");

        if(!this.accountExists(accountName)) {
            throw new AccountNotFoundException("The given account does not exist!");
        }

        List<AccountRule> accountRules = this.getAccountRulesForAcc(accountName);
        String newMailFolderPath = this.getFolderPathForMail(this.receiveEmailTemp, accountRules);

        var accountInboxFolder = this.getInboxFolder(accountName, newMailFolderPath);
        var newStoredMail = new StoredMail(this.receiveEmailTemp, accountInboxFolder);

        this.storedInboxMails.add(newStoredMail);
    }

    @Override
    public Collection<Mail> getMailsFromFolder(String account, String folderPath) {

        validateIsNotNullEmptyOrBlank(account, "account name");
        validateIsNotNullEmptyOrBlank(folderPath, "folder path");

        if(!this.accountExists(account)) {
            throw new AccountNotFoundException("The given account does not exist!");
        }

        List<String> inboxFolders = getInboxFoldersPathsForAccount(account);

        if(!inboxFolders.contains(folderPath) &&
        !folderPath.equals(SENT_FOLDER_PATH)){
            throw new FolderNotFoundException("The given folder does not exist!");
        }

        //Get the stored mails stored in this account's folder
        var storedEmails = this.getStoredMails(account, folderPath);

        //Get all the normal mails based on the stored mails. Map function pretty much :)
        List<Mail> normalMails = new ArrayList<>();

        for(var storedMail : storedEmails) {
            normalMails.add(storedMail.mail());
        }

        return Collections.unmodifiableList(normalMails);
    }

    @Override
    public void sendMail(String accountName, String mailMetadata, String mailContent) {
        validateIsNotNullEmptyOrBlank(accountName, "account name");
        validateIsNotNullEmptyOrBlank(mailMetadata, "mail metadata");
        validateIsNotNullEmptyOrBlank(mailContent, "mail content");

        Account senderAcc = this.getAccountByName(accountName);

        Mail newMail = this.createMail(senderAcc.emailAddress(), mailMetadata, mailContent);
        //Set this to the current email that is about to be sent to many users.
        //This is because the receive function doesn't accept the original mail as a parameter
        //So instead of changing the metadata to be correct and create a new Mail object inside the receive function
        //I just carry over this email temporarily
        this.receiveEmailTemp = newMail;

        this.mails.add(newMail);

        //Call receive for each of the recipients
        for(String recipientEmail : newMail.recipients()) {
            Account recipientAcc = getAccountByEmail(recipientEmail);

            if(recipientAcc != null) {
                receiveMail(recipientAcc.name(), mailMetadata, mailContent);
            }
        }
        //Clear the temp receive email
        this.receiveEmailTemp = null;

        //Add the newly sent email to the account's sent folder
        this.storedSentMails.add(new StoredMail(newMail, new AccountFolder(senderAcc.name(),
            this.SENT_FOLDER_PATH)));

    }
}
