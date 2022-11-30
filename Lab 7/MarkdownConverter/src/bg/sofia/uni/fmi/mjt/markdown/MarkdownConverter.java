package bg.sofia.uni.fmi.mjt.markdown;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class MarkdownConverter implements MarkdownConverterAPI {

    private static final String NEWLINE = System.getProperty("line.separator");
    private static final String HEADER = "#";
    private static final String BOLD = "**";
    private static final String ITALIC = "*";
    private static final String CODEFRAG = "`";

    private static final String HTML_DOC = "<html>" + NEWLINE +
        "<body>" + NEWLINE +
        "%s" +
        "</body>" + NEWLINE +
        "</html>" + NEWLINE;
    private static final String TAG_CLOSE_HTML = "/";
    private static final String HEADER_HTML = "<h%s>";
    private static final String BOLD_HTML = "<strong>";
    private static final String ITALIC_HTML = "<em>";
    private static final String CODE_HTML = "<code>";

    private static final String MARKDOWN_EXTENSION = ".md";
    private static final String HTML_EXTENSION = ".html";

    public MarkdownConverter() {

    }

    private String convertToHtmlDoc(String htmlBodyContent) {
        return String.format(HTML_DOC, htmlBodyContent);
    }

    private String convertTagToClosing(String htmlTag) {

        String htmlClosingTag = htmlTag.substring(0, 1) + TAG_CLOSE_HTML + htmlTag.substring(1);

        return htmlClosingTag;
    }

    private String replaceMarkdownTagWithHtmlTag(String markdownLine, String markdownTag, String htmlTag) {

        int openingBoldIndex = markdownLine.indexOf(markdownTag);
        int closingBoldIndex = markdownLine.lastIndexOf(markdownTag);

        if (openingBoldIndex == closingBoldIndex) {
            return markdownLine;
        }

        String newParsedLineContent = "";

        //Get before bold
        for (int i = 0; i < openingBoldIndex; i++) {
            newParsedLineContent += markdownLine.charAt(i);
        }

        newParsedLineContent += htmlTag;

        //Get in bold
        for (int i = openingBoldIndex + markdownTag.length(); i < closingBoldIndex; i++) {
            newParsedLineContent += markdownLine.charAt(i);
        }

        newParsedLineContent += convertTagToClosing(htmlTag);

        //Get after bold
        for (int i = closingBoldIndex + markdownTag.length(); i < markdownLine.length(); i++) {
            newParsedLineContent += markdownLine.charAt(i);
        }

        return newParsedLineContent;
    }

    private String convertMarkdownLine(String line) {
        String parsedLineFormat = "%s";
        String parsedLineContent = line;

        if (line.startsWith(HEADER)) {
            int headerSize = 1;

            while (Character.toString(line.charAt(headerSize)).equals(HEADER)) {
                headerSize++;
            }

            String headerHtmlTag = String.format(HEADER_HTML, String.valueOf(headerSize));
            parsedLineFormat = headerHtmlTag + "%s" + convertTagToClosing(headerHtmlTag);

            var parsedLineContentChars = new char[line.length() - (headerSize + 1)];
            line.getChars(headerSize + 1, line.length(), parsedLineContentChars, 0);

            parsedLineContent = String.valueOf(parsedLineContentChars);
        }

        if (parsedLineContent.contains(BOLD)) {
            parsedLineContent = replaceMarkdownTagWithHtmlTag(parsedLineContent, BOLD, BOLD_HTML);
        }
        if (parsedLineContent.contains(ITALIC)) {
            parsedLineContent = replaceMarkdownTagWithHtmlTag(parsedLineContent, ITALIC, ITALIC_HTML);
        }
        if (parsedLineContent.contains(CODEFRAG)) {
            parsedLineContent = replaceMarkdownTagWithHtmlTag(parsedLineContent, CODEFRAG, CODE_HTML);
        }

        return String.format(parsedLineFormat, parsedLineContent);
    }

    @Override
    public void convertMarkdown(Reader source, Writer output) {

        try {
            String markdownLine = "";
            String parsedMarkdown = "";

            while (true) {
                int readerCharInt = source.read();

                if (readerCharInt == -1) {
                    parsedMarkdown += convertMarkdownLine(markdownLine);
                    break;
                }

                String readerCharString = Character.toString(readerCharInt);

                if (NEWLINE.startsWith(readerCharString)) {

                    for (int i = 1; i < NEWLINE.length(); i++) {
                        source.read();
                    }

                    parsedMarkdown += convertMarkdownLine(markdownLine) + NEWLINE;
                    markdownLine = "";
                } else {
                    markdownLine += readerCharString;
                }
            }

            output.write(convertToHtmlDoc(parsedMarkdown));
        } catch (IOException e) {
            return;
        }
    }

    @Override
    public void convertMarkdown(Path from, Path to) {
        try (var bufferedReader = Files.newBufferedReader(from)) {

            if (!Files.exists(to)) {
                Files.createFile(to);
            }

            try (var bufferedWriter = Files.newBufferedWriter(to, StandardOpenOption.TRUNCATE_EXISTING)) {
                convertMarkdown(bufferedReader, bufferedWriter);
            }

        } catch (IOException e) {
            return;
        }
    }

    @Override
    public void convertAllMarkdownFiles(Path sourceDir, Path targetDir) {

        try (var files = Files.newDirectoryStream(sourceDir)) {

            for (Path curPath : files) {
                if (curPath.getFileName().toString().endsWith(MARKDOWN_EXTENSION)) {

                    String fileName = curPath.getFileName().toString();
                    String htmlFileName =
                        fileName.substring(0, fileName.lastIndexOf(MARKDOWN_EXTENSION)) + HTML_EXTENSION;

                    if (!Files.exists(targetDir)) {
                        Files.createDirectory(targetDir);
                    }

                    Path htmlFilePath = Path.of(targetDir.toString(), htmlFileName);

                    convertMarkdown(curPath, htmlFilePath);
                }
            }

        } catch (IOException e) {
            return;
        }
    }
}
