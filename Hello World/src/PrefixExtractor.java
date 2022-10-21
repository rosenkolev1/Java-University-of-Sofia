

public class PrefixExtractor {
    public static String getLongestCommonPrefix(String[] words)
    {
        if(words == null || words.length == 0) return "";
        if(words[0] == null || words[0].isEmpty()) return "";

        StringBuilder commonPrefix = new StringBuilder();
        commonPrefix.append(words[0].charAt(0));
        StringBuilder lastCommonPrefix = new StringBuilder("");

        int commonIndex = 1;
        while(true)
        {
            for (int i = 1; i < words.length; i++)
            {
                if(words[i].indexOf(commonPrefix.toString()) != 0)
                    return lastCommonPrefix.toString();
            }

            lastCommonPrefix = new StringBuilder(commonPrefix);
            if(commonIndex < words[0].length()) commonPrefix.append(words[0].charAt(commonIndex++));
            else break;
        }

        return words[0];
    }
}
