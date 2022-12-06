package bg.sofia.uni.fmi.mjt.mail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private String accountName;
    private String folderPath;
    private String ruleDefinition;
    private List<String> subjectIncludesKeywords = null;
    private List<String> subjectOrBodyIncludeKeywords = null;
    private List<String> recipientsIncludeEmails = null;
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

        for(String ruleCondition : ruleConditions) {

            if(ruleCondition.startsWith(SUBJECT_INCLUDES)) {
                this.subjectIncludesKeywords = List.of(ruleCondition.replaceFirst(SUBJECT_INCLUDES, "")
                    .split(KEYWORDS_SEPARATOR));
            }
            else if(ruleCondition.startsWith(SUBJECT_OR_BODY_INCLUDE)) {
                this.subjectOrBodyIncludeKeywords = List.of(ruleCondition.replaceFirst(SUBJECT_OR_BODY_INCLUDE, "")
                    .split(KEYWORDS_SEPARATOR));
            }
            else if(ruleCondition.startsWith(RECIPIENTS_INCLUDE)) {
                this.recipientsIncludeEmails = List.of(ruleCondition.replaceFirst(RECIPIENTS_INCLUDE, "")
                    .split(KEYWORDS_SEPARATOR));
            }
            else if(ruleCondition.startsWith(FROM)) {
                this.fromEmail = ruleCondition.replaceFirst(FROM, "");
            }

        }
    }

    public static Boolean ruleDefinitionIsValid(String ruleDefinition) {
        List<String> ruleConditions = List.of(ruleDefinition.split(System.getProperty("line.separator")));

        Map<String, Integer> ruleConditionDefinedCount = new HashMap<>();

        for(String ruleConditionKey : RULE_CONDITIONS_KEYS) {
            ruleConditionDefinedCount.put(ruleConditionKey, 0);
        }

        for(String ruleCondition : ruleConditions) {
            for(String ruleConditionKey : RULE_CONDITIONS_KEYS) {
                if(ruleCondition.startsWith(ruleConditionKey)) {
                    ruleConditionDefinedCount.put(
                        ruleConditionKey,
                        ruleConditionDefinedCount.get(ruleConditionKey) + 1
                    );

                    if(ruleConditionDefinedCount.get(ruleConditionKey) >= 2) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private boolean CompareRules(AccountRule first, AccountRule second){

        return first.getRuleDefinition().equals(second.getRuleDefinition()) &&
            first.getPriority() == second.getPriority() &&
            first.getAccountName().equals(second.getAccountName()) &&
            first.getFolderPath().equals(second.getFolderPath());
    }

    public String getAccountName() {
        return this.accountName;
    }

    public String getFolderPath() {
        return this.folderPath;
    }

    public String getRuleDefinition() {
        return this.ruleDefinition;
    }

    public int getPriority() {
        return this.priority;
    }
}
