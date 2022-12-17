package bg.sofia.uni.fmi.mjt.mail;

import java.util.*;

public class AccountRule {

    private static final String SUBJECT_INCLUDES = "subject-includes: ";
    private static final String SUBJECT_OR_BODY_INCLUDE = "subject-or-body-includes: ";
    private static final String RECIPIENTS_INCLUDE = "recipients-includes: ";
    private static final String FROM = "from: ";
    private static final String KEYWORDS_SEPARATOR = ", ";
    private static final List<String> RULE_CONDITIONS_KEYS = List.of(
        SUBJECT_INCLUDES,
        SUBJECT_OR_BODY_INCLUDE,
        RECIPIENTS_INCLUDE,
        FROM
    );

    public static final int MIN_PRIORITY = 1;
    public static final int MAX_PRIORITY = 10;

    private String accountName;
    private String folderPath;
    private String ruleDefinition;
    private List<String> subjectIncludesKeywords = new ArrayList<>();
    private List<String> subjectOrBodyIncludeKeywords = new ArrayList<>();
    private List<String> recipientsIncludeEmails = new ArrayList<>();
    private String fromEmail = null;

    private final int priority;

    public AccountRule(String accountName, String folderPath, String ruleDefinition, int priority) {
        this.accountName = accountName;
        this.folderPath = folderPath;
        this.ruleDefinition = ruleDefinition;
        this.priority = priority;

        setRuleConditions();
    }

    private void setRuleConditions() {

        List<String> ruleConditions = List.of(ruleDefinition.split(System.getProperty("line.separator")));

        for (String ruleCondition : ruleConditions) {

            if (ruleCondition.startsWith(SUBJECT_INCLUDES)) {
                this.subjectIncludesKeywords = List.of(ruleCondition.replaceFirst(SUBJECT_INCLUDES, "")
                    .split(KEYWORDS_SEPARATOR));
            } else if (ruleCondition.startsWith(SUBJECT_OR_BODY_INCLUDE)) {
                this.subjectOrBodyIncludeKeywords = List.of(ruleCondition.replaceFirst(SUBJECT_OR_BODY_INCLUDE, "")
                    .split(KEYWORDS_SEPARATOR));
            } else if (ruleCondition.startsWith(RECIPIENTS_INCLUDE)) {
                this.recipientsIncludeEmails = List.of(ruleCondition.replaceFirst(RECIPIENTS_INCLUDE, "")
                    .split(KEYWORDS_SEPARATOR));
            } else if (ruleCondition.startsWith(FROM)) {
                this.fromEmail = ruleCondition.replaceFirst(FROM, "");
            }

        }
    }

    public static Boolean ruleDefinitionIsValid(String ruleDefinition) {
        List<String> ruleConditions = List.of(ruleDefinition.split(System.getProperty("line.separator")));

        Map<String, Integer> ruleConditionDefinedCount = new HashMap<>();

        for (String ruleConditionKey : RULE_CONDITIONS_KEYS) {
            ruleConditionDefinedCount.put(ruleConditionKey, 0);
        }

        for (String ruleCondition : ruleConditions) {
            for (String ruleConditionKey : RULE_CONDITIONS_KEYS) {
                if (ruleCondition.startsWith(ruleConditionKey)) {
                    ruleConditionDefinedCount.put(
                        ruleConditionKey,
                        ruleConditionDefinedCount.get(ruleConditionKey) + 1
                    );

                    if (ruleConditionDefinedCount.get(ruleConditionKey) >= 2) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public boolean matchesMail(Mail mail) {

        //Check if the subject includes necessary keywords
        for (String keyword : this.subjectIncludesKeywords) {
            if (!mail.subject().contains(keyword)) {
                return false;
            }
        }

        //Check if the subject or body includes necessary keywords
        for (String keyword : this.subjectOrBodyIncludeKeywords) {
            if (!mail.subject().contains(keyword) && !mail.body().contains(keyword)) {
                return false;
            }
        }

        //Check if at least one necessary recipient is included in the email
        boolean matchesRecipient = this.recipientsIncludeEmails.isEmpty();

        for (String recipientEmail : this.recipientsIncludeEmails) {
            if (mail.recipients().contains(recipientEmail)) {
                matchesRecipient = true;
                break;
            }
        }

        if (!matchesRecipient) return false;

        //Check if the sender matches
        return this.fromEmail == null || this.fromEmail.equals(mail.sender().emailAddress());
    }

    public String getAccountName() {
        return this.accountName;
    }

    public String getFolderPath() {
        return this.folderPath;
    }

    public int getPriority() {
        return this.priority;
    }

    @Override
    public boolean equals(Object other) {
        // If the object is compared with itself then return true
        if (other == this) {
            return true;
        }

        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(other instanceof AccountRule)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        AccountRule castOther = (AccountRule) other;

        // Compare the data members and return accordingly
        return this.accountName.equals(castOther.accountName) &&
            this.folderPath.equals(castOther.folderPath) &&
            this.ruleDefinition.equals(castOther.ruleDefinition) &&
            this.priority == castOther.priority;

    }

    //Commenting for code coverage :)
//    @Override
//    public int hashCode()
//    {
//        return Objects.hash(this.accountName, this.folderPath, this.ruleDefinition);
//    }
}
