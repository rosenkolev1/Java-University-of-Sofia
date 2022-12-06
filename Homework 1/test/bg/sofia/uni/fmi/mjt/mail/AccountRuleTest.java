package bg.sofia.uni.fmi.mjt.mail;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AccountRuleTest {

    private final String newLine = System.lineSeparator();

    @Test
    void testEqualsMethod() {

        String rule1Definition = "subject-includes: mjt, izpit, 2022" + newLine +
            "subject-or-body-includes: izpit" + newLine +
            "from: stoyo@fmi.bg";
        int rule1Priority = 2;
        String rule1Account = "Roskata123";
        String rule1Path = "something";

        var rule1 = new AccountRule(rule1Account, rule1Path, rule1Definition, rule1Priority);

        String rule2Definition = "subject-includes: mjt, izpit, 2022" + newLine +
            "subject-or-body-includes: izpit" + newLine +
            "from: stoyo@fmi.bg";
        int rule2Priority = 4;
        String rule2Account = "Roskata123";
        String rule2Path = "something";

        var rule2 = new AccountRule(rule2Account, rule2Path, rule2Definition, rule2Priority);

        String rule3Definition = "subject-includes: mjt, izpit, 2022" + newLine +
            "subject-or-body-includes: izpit" + newLine +
            "from: stoyo@fmi.bg";
        int rule3Priority = 2;
        String rule3Account = "Roskata123";
        String rule3Path = "something";

        var rule3 = new AccountRule(rule3Account, rule3Path, rule3Definition, rule3Priority);

        Assertions.assertEquals(rule1, rule1);
        Assertions.assertNotEquals(rule1, rule2);
        Assertions.assertEquals(rule1, rule3);
        Assertions.assertNotEquals(rule1, new Account("Something@s.s", "Someone"));
    }

    @Test
    void testRuleDefinitionIsValidReturnsTrue() {
        String ruleDefinition = "subject-includes: mjt, izpit, 2022" + newLine +
            "subject-or-body-includes: izpit" + newLine +
            "recipients-includes: roskata123@abv.bg, anotherEmail@gmail.com" + newLine +
            "from: stoyo@fmi.bg";

        Assertions.assertTrue(AccountRule.ruleDefinitionIsValid(ruleDefinition));

        ruleDefinition = "subject-includes: mjt, izpit, 2022" + newLine +
            "subject-or-body-includes: izpit" + newLine +
            "recipients-includes: roskata123@abv.bg, anotherEmail@gmail.com";

        Assertions.assertTrue(AccountRule.ruleDefinitionIsValid(ruleDefinition));

        ruleDefinition = "subject-includes: mjt, izpit, 2022" + newLine +
            "recipients-includes: roskata123@abv.bg, anotherEmail@gmail.com";

        Assertions.assertTrue(AccountRule.ruleDefinitionIsValid(ruleDefinition));

        ruleDefinition = "recipients-includes: roskata123@abv.bg, anotherEmail@gmail.com";

        Assertions.assertTrue(AccountRule.ruleDefinitionIsValid(ruleDefinition));
    }

    @Test
    void testRuleDefinitionIsValidReturnsFalse() {
        String ruleDefinition = "subject-includes: mjt, izpit, 2022" + newLine +
            "subject-or-body-includes: izpit" + newLine +
            "recipients-includes: roskata123@abv.bg, anotherEmail@gmail.com" + newLine +
            "from: stoyo@fmi.bg" + newLine +
            "subject-includes: something";

        Assertions.assertFalse(AccountRule.ruleDefinitionIsValid(ruleDefinition));

        ruleDefinition = "subject-includes: mjt, izpit, 2022" + newLine +
            "subject-or-body-includes: izpit" + newLine +
            "recipients-includes: roskata123@abv.bg, anotherEmail@gmail.com" + newLine +
            "from: stoyo@fmi.bg" + newLine +
            "subject-or-body-includes: something";

        Assertions.assertFalse(AccountRule.ruleDefinitionIsValid(ruleDefinition));

        ruleDefinition = "subject-includes: mjt, izpit, 2022" + newLine +
            "subject-or-body-includes: izpit" + newLine +
            "recipients-includes: roskata123@abv.bg, anotherEmail@gmail.com" + newLine +
            "from: stoyo@fmi.bg" + newLine +
            "recipients-includes: something";

        Assertions.assertFalse(AccountRule.ruleDefinitionIsValid(ruleDefinition));

        ruleDefinition = "subject-includes: mjt, izpit, 2022" + newLine +
            "subject-or-body-includes: izpit" + newLine +
            "recipients-includes: roskata123@abv.bg, anotherEmail@gmail.com" + newLine +
            "from: stoyo@fmi.bg" + newLine +
            "from: something";

        Assertions.assertFalse(AccountRule.ruleDefinitionIsValid(ruleDefinition));

        ruleDefinition = "subject-includes: mjt, izpit, 2022" + newLine +
            "subject-includes: novIzpit";

        Assertions.assertFalse(AccountRule.ruleDefinitionIsValid(ruleDefinition));
    }

    @Test
    void testMatchesMailReturnFalse() {
        String ruleDefinition = "subject-includes: mjt, izpit, 2022" + newLine +
            "subject-or-body-includes: izpit, kur" + newLine +
            "recipients-includes: roskata123@abv.bg, anotherEmail@gmail.com" + newLine +
            "from: stefan@abv.bg";
        AccountRule rule = new AccountRule("Roskata123", "somePath", ruleDefinition, 1);

        LocalDateTime receivedTime = LocalDateTime.of(2022, 9, 20, 12, 00);
        Account sender = new Account("stefan@abv.bg", "Stefan");

        Mail mail = new Mail(
            sender,
            Set.of(
                "vr@abv.bg",
                "roskata123@abv.bg"
            ),
            "mjt izpit",
            "Absolutno ni6to kur vajno nqma v tozi izpit",
            receivedTime
        );

        Assertions.assertFalse(rule.matchesMail(mail),
            "Expected false because '2022' is not included in the subject!");

        mail = new Mail(
            sender,
            Set.of(
                "vr@abv.bg",
                "roskata123@abv.bg"
            ),
            "mjt izpit 2022",
            "Absolutno ni6to ne6to vajno nqma v tozi",
            receivedTime
        );

        Assertions.assertFalse(rule.matchesMail(mail),
            "Expected false because 'kur' is not included in the body or subject!");

        mail = new Mail(
            sender,
            Set.of(
                "vr@abv.bg",
                "rosen123@abv.bg"
            ),
            "mjt izpit 2022 kur",
            "Absolutno ni6to vajno nqma v tozi izpit",
            receivedTime
        );

        Assertions.assertFalse(rule.matchesMail(mail),
            "Expected false because 'roskata123@abv.bg' is not included amongst the recipients!");

        mail = new Mail(
            new Account(sender.emailAddress() + "s", sender.name()),
            Set.of(
                "vr@abv.bg",
                "roskata123@abv.bg"
            ),
            "mjt izpit 2022 kur",
            "Absolutno ni6to vajno nqma v tozi izpit",
            receivedTime
        );

        Assertions.assertFalse(rule.matchesMail(mail),
            "Expected false because the sender is 'stefan@abv.bgs' instead of 'stefan@abv.bg'!");

        ruleDefinition = "subject-includes: mjt, izpit, 2022" + newLine +
            "subject-or-body-includes: izpit, kur" + newLine +
            "recipients-includes: roskata123@abv.bg, anotherEmail@gmail.com";
        rule = new AccountRule("Roskata123", "somePath", ruleDefinition, 1);
    }

    @Test
    void testMatchesMailReturnTrue() {
        String ruleDefinition = "subject-includes: mjt, izpit, 2022" + newLine +
            "subject-or-body-includes: izpit, kur" + newLine +
            "recipients-includes: roskata123@abv.bg, anotherEmail@gmail.com" + newLine +
            "from: stefan@abv.bg";
        AccountRule rule = new AccountRule("Roskata123", "somePath", ruleDefinition, 1);

        LocalDateTime receivedTime = LocalDateTime.of(2022, 9, 20, 12, 00);
        Account sender = new Account("stefan@abv.bg", "Stefan");

        Mail mail = new Mail(
            sender,
            Set.of(
                "vr@abv.bg",
                "roskata123@abv.bg"
            ),
            "mjt izpit 2022",
            "Absolutno ni6to kur vajno nqma v tozi izpit",
            receivedTime
        );

        Assertions.assertTrue(rule.matchesMail(mail));

        mail = new Mail(
            sender,
            Set.of(
                "vr@abv.bg",
                "roskata123@abv.bg"
            ),
            "mjt izpit 2022",
            "Absolutno ni6to kur vajno nqma v tozi",
            receivedTime
        );

        Assertions.assertTrue(rule.matchesMail(mail));

        mail = new Mail(
            sender,
            Set.of(
                "vr@abv.bg",
                "roskata123@abv.bg"
            ),
            "mjt izpit 2022 kur",
            "Absolutno ni6to vajno nqma v tozi",
            receivedTime
        );

        Assertions.assertTrue(rule.matchesMail(mail));

        mail = new Mail(
            sender,
            Set.of(
                "vr@abv.bg",
                "roskata123@abv.bg",
                "anotherEmail@gmail.com"
            ),
            "mjt izpit 2022",
            "Absolutno ni6to kur vajno nqma v tozi",
            receivedTime
        );

        Assertions.assertTrue(rule.matchesMail(mail));

        mail = new Mail(
            sender,
            Set.of(
                "vr@abv.bg",
                "anotherEmail@gmail.com"
            ),
            "mjt izpit 2022",
            "Absolutno ni6to kur vajno nqma v tozi",
            receivedTime
        );

        Assertions.assertTrue(rule.matchesMail(mail));

        mail = new Mail(
            sender,
            Set.of(
                "roskata123@abv.bg"
            ),
            "mjt izpit 2022",
            "Absolutno ni6to kur vajno nqma v tozi",
            receivedTime
        );

        Assertions.assertTrue(rule.matchesMail(mail));

        ruleDefinition = "subject-includes: mjt, izpit, 2022" + newLine +
            "subject-or-body-includes: izpit, kur" + newLine +
            "recipients-includes: roskata123@abv.bg, anotherEmail@gmail.com";
        rule = new AccountRule("Roskata123", "somePath", ruleDefinition, 1);

        mail = new Mail(
            sender,
            Set.of(
                "roskata123@abv.bg"
            ),
            "mjt izpit 2022",
            "Absolutno ni6to kur vajno nqma v tozi",
            receivedTime
        );

        Assertions.assertTrue(rule.matchesMail(mail),
            "Testing for when the fromSender field in the rule is null");

        ruleDefinition = "subject-or-body-includes: izpit, kur" + newLine +
            "recipients-includes: roskata123@abv.bg, anotherEmail@gmail.com" + newLine +
            "from: stefan@abv.bg";
        rule = new AccountRule("Roskata123", "somePath", ruleDefinition, 1);

        Assertions.assertTrue(rule.matchesMail(mail),
            "Testing for when the subject-includes field in the rule is null");

        ruleDefinition = "subject-includes: mjt, izpit, 2022" + newLine +
            "recipients-includes: roskata123@abv.bg, anotherEmail@gmail.com" + newLine +
            "from: stefan@abv.bg";
        rule = new AccountRule("Roskata123", "somePath", ruleDefinition, 1);

        Assertions.assertTrue(rule.matchesMail(mail),
            "Testing for when the subject-or-body-includes field in the rule is null");

        ruleDefinition = "subject-includes: mjt, izpit, 2022" + newLine +
            "subject-or-body-includes: izpit, kur" + newLine +
            "from: stefan@abv.bg";
        rule = new AccountRule("Roskata123", "somePath", ruleDefinition, 1);

        Assertions.assertTrue(rule.matchesMail(mail),
            "Testing for when the recipients field in the rule is null");

        ruleDefinition = "from: stefan@abv.bg";
        rule = new AccountRule("Roskata123", "somePath", ruleDefinition, 1);

        Assertions.assertTrue(rule.matchesMail(mail),
            "Testing for when the only the 'from' field is inside the rule definition");

        ruleDefinition = "subject-includes: mjt, izpit, 2022";
        rule = new AccountRule("Roskata123", "somePath", ruleDefinition, 1);

        Assertions.assertTrue(rule.matchesMail(mail),
            "Testing for when the only the 'subject-includes' field is inside the rule definition");

        ruleDefinition = "subject-or-body-includes: izpit, kur";
        rule = new AccountRule("Roskata123", "somePath", ruleDefinition, 1);

        Assertions.assertTrue(rule.matchesMail(mail),
            "Testing for when the only the 'subject-or-body-includes:' field is inside the rule definition");

        ruleDefinition = "recipients-includes: roskata123@abv.bg, anotherEmail@gmail.com";
        rule = new AccountRule("Roskata123", "somePath", ruleDefinition, 1);

        Assertions.assertTrue(rule.matchesMail(mail),
            "Testing for when the only the 'recipients-includes:' field is inside the rule definition");
    }
}

