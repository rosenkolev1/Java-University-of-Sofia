import bg.sofia.uni.fmi.mjt.markdown.MarkdownConverter;
import bg.sofia.uni.fmi.mjt.markdown.MarkdownConverterAPI;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Main {
    public static void main(String[] args) {

//        var absPath = Paths.get("").toAbsolutePath();

        MarkdownConverterAPI converter = new MarkdownConverter();
        var filePathMarkdown = Path.of("src/markdownText.txt");
        var filePathHtml = Path.of("src/htmlText.txt");

        var directoryPathMarkdown = Path.of("src/Markdown Directory");
        var directoryPathHtml = Path.of("src/Markdown Directory/newDir");

        converter.convertMarkdown(filePathMarkdown, filePathHtml);

//        converter.convertAllMarkdownFiles(directoryPathMarkdown, directoryPathHtml);

//        try (var bufferedReader = Files.newBufferedReader(filePathMarkdown)) {
//
//            try (var bufferedWriter = Files.newBufferedWriter(filePathHtml, StandardOpenOption.TRUNCATE_EXISTING)){
//                converter.convertMarkdown(bufferedReader, bufferedWriter);
//            }
//
//        } catch (IOException e) {
//            throw new IllegalStateException("A problem occurred while reading or writing from a file", e);
//        }
    }
}