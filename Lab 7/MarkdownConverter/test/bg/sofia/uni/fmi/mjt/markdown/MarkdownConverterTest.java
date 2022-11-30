package bg.sofia.uni.fmi.mjt.markdown;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class MarkdownConverterTest {

    private MarkdownConverterAPI converter = new MarkdownConverter();

    private final String NEWLINE = System.getProperty("line.separator");

    void testConvertMarkdownWithStringReaderAndStringWriter(String input, String expectedOutput, String assertionMessage) throws IOException {
        String output = null;

        try (var reader = new StringReader(input)) {
            try (var writer = new StringWriter()) {
                converter.convertMarkdown(reader, writer);
                output = writer.toString();
            }
        }

        Assertions.assertEquals(expectedOutput, output, assertionMessage);
    }

    @Test
    void testConvertMarkdownReaderWriter_Headers() throws IOException {

        String line1 = "# Heading 1";
        String line2 = "## Heading 2" + NEWLINE +
            "### Heading 3" + NEWLINE +
            "#### Heading 4" + NEWLINE +
            "##### Heading 5" + NEWLINE +
            "###### Heading 6";

        String expectedOutput1 = "<html>" + NEWLINE + "<body>" + NEWLINE + "<h1>Heading 1</h1></body>" + NEWLINE + "</html>" + NEWLINE + "";
        String expectedOutput2 = "<html>" + NEWLINE + "<body>" + NEWLINE + "<h2>Heading 2</h2>" + NEWLINE + "" +
            "<h3>Heading 3</h3>" + NEWLINE + "" +
            "<h4>Heading 4</h4>" + NEWLINE + "" +
            "<h5>Heading 5</h5>" + NEWLINE + "" +
            "<h6>Heading 6</h6>" + "</body>" + NEWLINE + "</html>" + NEWLINE + "";

        testConvertMarkdownWithStringReaderAndStringWriter(line1, expectedOutput1,
            "Parsing the markdown header into html is not correct!");
        testConvertMarkdownWithStringReaderAndStringWriter(line2, expectedOutput2,
            "Parsing the multiple markdown headers into html is not correct!");
    }

    @Test
    void testConvertMarkdownReaderWriter_Headers_Empty() throws IOException {

        String line1 = "## ";
        String line2 = "##";

        String expectedOutput1 = "<html>" + NEWLINE + "<body>" + NEWLINE + "<h2></h2></body>" + NEWLINE + "</html>" + NEWLINE + "";

        testConvertMarkdownWithStringReaderAndStringWriter(line1, expectedOutput1,
            "Parsing the empty markdown header into html is not correct!");
    }

    @Test
    void testConvertMarkdownReaderWriter_Bold() throws IOException {

        String line1 = "I just love **bold text**.";
        String line2 = "Love**is**bold" + NEWLINE +
            "**Love** is bold" + NEWLINE +
            "Love is **bold**";

        String expectedOutput1 = "<html>" + NEWLINE + "<body>" + NEWLINE + "I just love <strong>bold text</strong>.</body>" + NEWLINE + "</html>" + NEWLINE + "";
        String expectedOutput2 = "<html>" + NEWLINE + "<body>" + NEWLINE + "Love<strong>is</strong>bold" + NEWLINE + "" +
            "<strong>Love</strong> is bold" + NEWLINE + "" +
            "Love is <strong>bold</strong></body>" + NEWLINE + "</html>" + NEWLINE + "";

        testConvertMarkdownWithStringReaderAndStringWriter(line1, expectedOutput1,
            "Parsing the markdown bold tag into html is not correct!");
        testConvertMarkdownWithStringReaderAndStringWriter(line2, expectedOutput2,
            "Parsing the multiple markdown bold tags into html is not correct!");
    }

    @Test
    void testConvertMarkdownReaderWriter_Bold_Empty() throws IOException {

        String line1 = "Nothing is **** bold!";

        String expectedOutput1 = "<html>" + NEWLINE + "<body>" + NEWLINE + "Nothing is <strong></strong> bold!</body>" + NEWLINE + "</html>" + NEWLINE + "";

        testConvertMarkdownWithStringReaderAndStringWriter(line1, expectedOutput1,
            "Parsing the empty markdown bold tag into html is not correct!");
    }

    @Test
    void testConvertMarkdownReaderWriter_Italic() throws IOException {
        String line1 = "I just love *italic text*.";
        String line2 = "Love*is*italic" + NEWLINE +
            "*Love* is italic" + NEWLINE +
            "Love is *italic*";

        String expectedOutput1 = "<html>" + NEWLINE + "<body>" + NEWLINE + "I just love <em>italic text</em>.</body>" + NEWLINE + "</html>" + NEWLINE + "";
        String expectedOutput2 = "<html>" + NEWLINE + "<body>" + NEWLINE + "Love<em>is</em>italic" + NEWLINE + "" +
            "<em>Love</em> is italic" + NEWLINE + "" +
            "Love is <em>italic</em></body>" + NEWLINE + "</html>" + NEWLINE + "";

        testConvertMarkdownWithStringReaderAndStringWriter(line1, expectedOutput1,
            "Parsing the markdown italic tag into html is not correct!");
        testConvertMarkdownWithStringReaderAndStringWriter(line2, expectedOutput2,
            "Parsing the multiple markdown italic tags into html is not correct!");
    }

    @Test
    void testConvertMarkdownReaderWriter_Italic_Empty() throws IOException {
        String line1 = "Nothing is ** italic!";

        String expectedOutput1 = "<html>" + NEWLINE + "<body>" + NEWLINE + "Nothing is <em></em> italic!</body>" + NEWLINE + "</html>" + NEWLINE + "";

        testConvertMarkdownWithStringReaderAndStringWriter(line1, expectedOutput1,
            "Parsing the empty markdown italic tag into html is not correct!");
    }

    @Test
    void testConvertMarkdownReaderWriter_Codefrag() throws IOException {
        String line1 = "Always `.close()` your streams.";
        String line2 = "`Always` .close() your streams." + NEWLINE +
            "Always .close() `your` streams." + NEWLINE +
            "Always .close() your `streams.`";

        String expectedOutput1 = "<html>" + NEWLINE + "<body>" + NEWLINE + "Always <code>.close()</code> your streams.</body>" + NEWLINE + "</html>" + NEWLINE + "";
        String expectedOutput2 = "<html>" + NEWLINE + "<body>" + NEWLINE + "<code>Always</code> .close() your streams." + NEWLINE + "" +
            "Always .close() <code>your</code> streams." + NEWLINE + "" +
            "Always .close() your <code>streams.</code></body>" + NEWLINE + "</html>" + NEWLINE + "";

        testConvertMarkdownWithStringReaderAndStringWriter(line1, expectedOutput1,
            "Parsing the markdown codefrag tag into html is not correct!");
        testConvertMarkdownWithStringReaderAndStringWriter(line2, expectedOutput2,
            "Parsing the multiple markdown codefrag tags into html is not correct!");
    }

    @Test
    void testConvertMarkdownReaderWriter_Codefrag_Empty() throws IOException {
        String line1 = "Always `` your streams.";

        String expectedOutput1 = "<html>" + NEWLINE + "<body>" + NEWLINE + "Always <code></code> your streams.</body>" + NEWLINE + "</html>" + NEWLINE + "";

        testConvertMarkdownWithStringReaderAndStringWriter(line1, expectedOutput1,
            "Parsing the empty markdown codefrag tag into html is not correct!");
    }

    @Test
    void testConvertMarkdownReaderWriter_MultipleMarkdownTags() throws IOException {

        String line1 = "# Heading 1" + NEWLINE +
            "## Heading 2" + NEWLINE +
            "### Heading 3" + NEWLINE +
            "#### Heading 4" + NEWLINE +
            "##### Heading 5" + NEWLINE +
            "###### Heading 6" + NEWLINE +
            "" + NEWLINE +
            "I just love **bold text**." + NEWLINE +
            "Love**is**bold" + NEWLINE +
            "**Love** is bold" + NEWLINE +
            "Love is **bold**" + NEWLINE +
            "" + NEWLINE +
            "I just love *italic text*." + NEWLINE +
            "Love*is*italic" + NEWLINE +
            "*Love* is italic" + NEWLINE +
            "Love is *italic*" + NEWLINE +
            "" + NEWLINE +
            "Always `.close()` your streams." + NEWLINE +
            "`Always` .close() your streams." + NEWLINE +
            "Always .close() `your` streams." + NEWLINE +
            "Always .close() your `streams.`" + NEWLINE +
            "" + NEWLINE +
            "I just love **bold text** *Love* is italic. Always .close() your `streams.`";

        String expectedOutput1 = "<html>" + NEWLINE + "<body>" + NEWLINE + "<h1>Heading 1</h1>" + NEWLINE +
            "<h2>Heading 2</h2>" + NEWLINE +
            "<h3>Heading 3</h3>" + NEWLINE +
            "<h4>Heading 4</h4>" + NEWLINE +
            "<h5>Heading 5</h5>" + NEWLINE +
            "<h6>Heading 6</h6>" + NEWLINE +
            "" + NEWLINE +
            "I just love <strong>bold text</strong>." + NEWLINE +
            "Love<strong>is</strong>bold" + NEWLINE +
            "<strong>Love</strong> is bold" + NEWLINE +
            "Love is <strong>bold</strong>" + NEWLINE +
            "" + NEWLINE +
            "I just love <em>italic text</em>." + NEWLINE +
            "Love<em>is</em>italic" + NEWLINE +
            "<em>Love</em> is italic" + NEWLINE +
            "Love is <em>italic</em>" + NEWLINE +
            "" + NEWLINE +
            "Always <code>.close()</code> your streams." + NEWLINE +
            "<code>Always</code> .close() your streams." + NEWLINE +
            "Always .close() <code>your</code> streams." + NEWLINE +
            "Always .close() your <code>streams.</code>" + NEWLINE +
            "" + NEWLINE +
            "I just love <strong>bold text</strong> <em>Love</em> is italic. Always .close() your <code>streams.</code>" +
            "</body>" + NEWLINE + "</html>" + NEWLINE + "";

        testConvertMarkdownWithStringReaderAndStringWriter(line1, expectedOutput1,
            "Parsing the many markdown lines and tags into html is not correct!");
    }

    @Test
    void testConvertMarkdownReaderWriter_NoMarkdownTags() throws IOException {
        String line1 = "Always close your streams.";
        String line2 = "";

        String expectedOutput1 = "<html>" + NEWLINE + "<body>" + NEWLINE + "Always close your streams.</body>" + NEWLINE + "</html>" + NEWLINE + "";
        String expectedOutput2 = "<html>" + NEWLINE + "<body>" + NEWLINE + "</body>" + NEWLINE + "</html>" + NEWLINE + "";

        testConvertMarkdownWithStringReaderAndStringWriter(line1, expectedOutput1,
            "Parsing the markdown line without special tags into html is not correct!");
        testConvertMarkdownWithStringReaderAndStringWriter(line2, expectedOutput2,
            "Parsing the empty line into html is not correct!");
    }

    @Test
    void testConvertMarkdownPathsFromTo_CreateNewFile_Correct() throws IOException {
        //Setup phase

        String line1 = "# Heading 1" + NEWLINE +
            "## Heading 2" + NEWLINE +
            "### Heading 3" + NEWLINE +
            "#### Heading 4" + NEWLINE +
            "##### Heading 5" + NEWLINE +
            "###### Heading 6" + NEWLINE +
            "" + NEWLINE +
            "I just love **bold text**." + NEWLINE +
            "Love**is**bold" + NEWLINE +
            "**Love** is bold" + NEWLINE +
            "Love is **bold**" + NEWLINE +
            "" + NEWLINE +
            "I just love *italic text*." + NEWLINE +
            "Love*is*italic" + NEWLINE +
            "*Love* is italic" + NEWLINE +
            "Love is *italic*" + NEWLINE +
            "" + NEWLINE +
            "Always `.close()` your streams." + NEWLINE +
            "`Always` .close() your streams." + NEWLINE +
            "Always .close() `your` streams." + NEWLINE +
            "Always .close() your `streams.`" + NEWLINE +
            "" + NEWLINE +
            "I just love **bold text** *Love* is italic. Always .close() your `streams.`";

        String expectedOutput1 = "<html>" + NEWLINE + "<body>" + NEWLINE + "<h1>Heading 1</h1>" + NEWLINE +
            "<h2>Heading 2</h2>" + NEWLINE +
            "<h3>Heading 3</h3>" + NEWLINE +
            "<h4>Heading 4</h4>" + NEWLINE +
            "<h5>Heading 5</h5>" + NEWLINE +
            "<h6>Heading 6</h6>" + NEWLINE +
            "" + NEWLINE +
            "I just love <strong>bold text</strong>." + NEWLINE +
            "Love<strong>is</strong>bold" + NEWLINE +
            "<strong>Love</strong> is bold" + NEWLINE +
            "Love is <strong>bold</strong>" + NEWLINE +
            "" + NEWLINE +
            "I just love <em>italic text</em>." + NEWLINE +
            "Love<em>is</em>italic" + NEWLINE +
            "<em>Love</em> is italic" + NEWLINE +
            "Love is <em>italic</em>" + NEWLINE +
            "" + NEWLINE +
            "Always <code>.close()</code> your streams." + NEWLINE +
            "<code>Always</code> .close() your streams." + NEWLINE +
            "Always .close() <code>your</code> streams." + NEWLINE +
            "Always .close() your <code>streams.</code>" + NEWLINE +
            "" + NEWLINE +
            "I just love <strong>bold text</strong> <em>Love</em> is italic. Always .close() your <code>streams.</code>" +
            "</body>" + NEWLINE + "</html>" + NEWLINE + "";

        var filePathMarkdown = Path.of("test/markdownText.txt");
        var filePathHtml = Path.of("test/htmlText.txt");

        try {
            Files.createFile(filePathMarkdown);

            try (var writer = Files.newBufferedWriter(filePathMarkdown)) {
                writer.write(line1);
            }

            //Act phase
            converter.convertMarkdown(filePathMarkdown, filePathHtml);

            //Assertion phase
            String output1 = "";

            try (var reader = Files.newBufferedReader(filePathHtml)) {
                int readerCharInt = reader.read();

                while(readerCharInt != -1) {
                    output1 += (char) readerCharInt;
                    readerCharInt = reader.read();
                }
            }

            Assertions.assertEquals(expectedOutput1, output1,
                "Parsing the many markdown lines and tags into non-existent html file from source file is not correct!");
        }
        finally {
            //Cleanup phase
            Files.delete(filePathMarkdown);
            Files.delete(filePathHtml);
        }
    }

    @Test
    void testConvertMarkdownPathsFromTo_TruncateExistingFile_Correct() throws IOException {
        //Setup phase

        String markdownFileNewContent = "I just love **bold text** *Love* is italic. Always .close() your `streams.`";

        String htmlFileOldContent = "<html>" + NEWLINE + "<body>" + NEWLINE + "<h1>Heading 1</h1>" + NEWLINE +
            "<h2>Heading 2</h2>" + NEWLINE +
            "<h3>Heading 3</h3>" + NEWLINE +
            "<h4>Heading 4</h4>" + NEWLINE +
            "<h5>Heading 5</h5>" + NEWLINE +
            "<h6>Heading 6</h6>" + NEWLINE +
            "" + NEWLINE +
            "I just love <strong>bold text</strong>." + NEWLINE +
            "Love<strong>is</strong>bold" + NEWLINE +
            "<strong>Love</strong> is bold" + NEWLINE +
            "Love is <strong>bold</strong>" + NEWLINE +
            "" + NEWLINE +
            "I just love <em>italic text</em>." + NEWLINE +
            "Love<em>is</em>italic" + NEWLINE +
            "<em>Love</em> is italic" + NEWLINE +
            "Love is <em>italic</em>" + NEWLINE +
            "" + NEWLINE +
            "Always <code>.close()</code> your streams." + NEWLINE +
            "<code>Always</code> .close() your streams." + NEWLINE +
            "Always .close() <code>your</code> streams." + NEWLINE +
            "Always .close() your <code>streams.</code></body>" + NEWLINE + "</html>" + NEWLINE + "";

        var htmlFileNewContentExpected = "<html>" + NEWLINE + "<body>" + NEWLINE + "" +
            "I just love <strong>bold text</strong> <em>Love</em> is italic. Always .close() your <code>streams.</code>" +
            "</body>" + NEWLINE + "</html>" + NEWLINE + "";

        var filePathMarkdown = Path.of("test/markdownText.txt");
        var filePathHtml = Path.of("test/htmlText.txt");

        try {
            Files.createFile(filePathMarkdown);
            Files.createFile(filePathHtml);

            try (var writer = Files.newBufferedWriter(filePathMarkdown)) {
                writer.write(markdownFileNewContent);
            }

            try (var writer = Files.newBufferedWriter(filePathHtml)) {
                writer.write(htmlFileOldContent);
            }

            //Act phase
            converter.convertMarkdown(filePathMarkdown, filePathHtml);

            //Assertion phase
            String output1 = "";

            try (var reader = Files.newBufferedReader(filePathHtml)) {
                int readerCharInt = reader.read();

                while(readerCharInt != -1) {
                    output1 += (char) readerCharInt;
                    readerCharInt = reader.read();
                }
            }

            Assertions.assertEquals(htmlFileNewContentExpected, output1,
                "Parsing the new markdown line into existing html file from source file should truncate the old html content");
        }
        finally {
            //Cleanup phase
            Files.delete(filePathMarkdown);
            Files.delete(filePathHtml);
        }
    }

    @Test
    void testConvertAllMarkdownFiles_CreateNewTargetDir() throws IOException {
        //Setup phase

        var markdownFile1Content = "I just love **bold text** *Love* is italic. Always .close() your `streams.`";
        var markdownFile2Content = "# Heading 1" + NEWLINE + "**Love** is bold" + NEWLINE + "Love is *italic*" + NEWLINE + "Always `.close()` your streams.";

        var markdownFile1 = Path.of("test/Markdown Directory/markdownFile_1.md");
        var markdownFile2 = Path.of("test/Markdown Directory/markdownFile_2.md");

        var directoryPathMarkdown = Path.of("test/Markdown Directory");
        var directoryPathHtml = Path.of("test/HTML Directory");

        var htmlFile1 = Path.of("test/HTML Directory/markdownFile_1.html");
        var htmlFile2 = Path.of("test/HTML Directory/markdownFile_2.html");

        var htmlFile1ExpectedContent = "<html>" + NEWLINE + "<body>" + NEWLINE + "" +
            "I just love <strong>bold text</strong> <em>Love</em> is italic. Always .close() your <code>streams.</code>" +
            "</body>" + NEWLINE + "</html>" + NEWLINE + "";
        var htmlFile2ExpectedContent = "<html>" + NEWLINE + "<body>" + NEWLINE + "<h1>Heading 1</h1>" + NEWLINE +
            "<strong>Love</strong> is bold" + NEWLINE +
            "Love is <em>italic</em>" + NEWLINE +
            "Always <code>.close()</code> your streams." + "</body>" + NEWLINE + "</html>" + NEWLINE + "";

        try {
            Files.createDirectory(directoryPathMarkdown);
            Files.createFile(markdownFile1);
            Files.createFile(markdownFile2);

            try (var writer = Files.newBufferedWriter(markdownFile1)) {
                writer.write(markdownFile1Content);
            }

            try (var writer = Files.newBufferedWriter(markdownFile2)) {
                writer.write(markdownFile2Content);
            }

            //Act phase
            converter.convertAllMarkdownFiles(directoryPathMarkdown, directoryPathHtml);

            //Assertion phase
            String htmlFile1ActualContent = "";

            Assertions.assertTrue(Files.exists(htmlFile1),
                "The first html file was not created in the new target dir!");

            Assertions.assertTrue(Files.exists(htmlFile2),
                "The second html file was not created in the new target dir!");

            try (var reader = Files.newBufferedReader(htmlFile1)) {
                int readerCharInt = reader.read();

                while(readerCharInt != -1) {
                    htmlFile1ActualContent += (char) readerCharInt;
                    readerCharInt = reader.read();
                }
            }

            Assertions.assertEquals(htmlFile1ExpectedContent, htmlFile1ActualContent,
                "Parsing the first markdown file into the a new html file in the target dir didn't work because content " +
                    "is different than expected!");

            String htmlFile2ActualContent = "";

            try (var reader = Files.newBufferedReader(htmlFile2)) {
                int readerCharInt = reader.read();

                while(readerCharInt != -1) {
                    htmlFile2ActualContent += (char) readerCharInt;
                    readerCharInt = reader.read();
                }
            }

            Assertions.assertEquals(htmlFile2ExpectedContent, htmlFile2ActualContent,
                "Parsing the second markdown file into the a new html file in the target dir didn't work because content " +
                    "is different than expected!");
        }
        finally {

            //Cleanup phase
            Files.delete(markdownFile1);
            Files.delete(markdownFile2);
            Files.delete(htmlFile1);
            Files.delete(htmlFile2);
            Files.delete(directoryPathMarkdown);
            Files.delete(directoryPathHtml);
        }
    }

    @Test
    void testConvertAllMarkdownFiles_ExistingTargetDir() throws IOException {
        //Setup phase

        var markdownFile1Content = "I just love **bold text** *Love* is italic. Always .close() your `streams.`";
        var markdownFile2Content = "# Heading 1" + NEWLINE + "**Love** is bold" + NEWLINE + "Love is *italic*" + NEWLINE + "Always `.close()` your streams.";

        var htmlFile1_OldContent = "<html>" + NEWLINE + "<body>" + NEWLINE + "<h6>Heading 6</h6>" + NEWLINE + "<h5>Heading 5</h5>" + NEWLINE + "<h4>Heading 4</h4></body>" + NEWLINE + "</html>" + NEWLINE + "";
        var htmlFile2_OldContent = "<html>" + NEWLINE + "<body>" + NEWLINE + "<code>Always</code> .close() your streams."+ NEWLINE +
            "Always .close() <code>your</code> streams." + NEWLINE +
            "Always .close() your <code>streams.</code>" + "</body>" + NEWLINE + "<html>" + NEWLINE + "";

        var markdownFile1 = Path.of("test/Markdown Directory/markdownFile_1.md");
        var markdownFile2 = Path.of("test/Markdown Directory/markdownFile_2.md");

        var htmlFile1 = Path.of("test/HTML Directory/markdownFile_1.html");
        var htmlFile2 = Path.of("test/HTML Directory/markdownFile_2.html");

        var directoryPathMarkdown = Path.of("test/Markdown Directory");
        var directoryPathHtml = Path.of("test/HTML Directory");

        var htmlFile1ExpectedContent = "<html>" + NEWLINE + "<body>" + NEWLINE + "" +
            "I just love <strong>bold text</strong> <em>Love</em> is italic. Always .close() your <code>streams.</code>" +
            "</body>" + NEWLINE + "</html>" + NEWLINE + "";
        var htmlFile2ExpectedContent = "<html>" + NEWLINE + "<body>" + NEWLINE + "<h1>Heading 1</h1>" + NEWLINE +
            "<strong>Love</strong> is bold" + NEWLINE +
            "Love is <em>italic</em>" + NEWLINE +
            "Always <code>.close()</code> your streams.</body>" + NEWLINE + "</html>" + NEWLINE + "";

        try {
            Files.createDirectory(directoryPathMarkdown);
            Files.createDirectory(directoryPathHtml);
            Files.createFile(markdownFile1);
            Files.createFile(markdownFile2);
            Files.createFile(htmlFile1);
            Files.createFile(htmlFile2);

            try (var writer = Files.newBufferedWriter(htmlFile1)) {
                writer.write(htmlFile1_OldContent);
            }

            try (var writer = Files.newBufferedWriter(htmlFile2)) {
                writer.write(htmlFile2_OldContent);
            }

            try (var writer = Files.newBufferedWriter(markdownFile1)) {
                writer.write(markdownFile1Content);
            }

            try (var writer = Files.newBufferedWriter(markdownFile2)) {
                writer.write(markdownFile2Content);
            }

            //Act phase
            converter.convertAllMarkdownFiles(directoryPathMarkdown, directoryPathHtml);

            //Assertion phase
            String htmlFile1ActualContent = "";

            Assertions.assertTrue(Files.exists(htmlFile1),
                "The first html file was somehow deleted in the target dir!");

            Assertions.assertTrue(Files.exists(htmlFile2),
                "The second html file was somehow deleted the target dir!");

            try (var reader = Files.newBufferedReader(htmlFile1)) {
                int readerCharInt = reader.read();

                while(readerCharInt != -1) {
                    htmlFile1ActualContent += (char) readerCharInt;
                    readerCharInt = reader.read();
                }
            }

            Assertions.assertEquals(htmlFile1ExpectedContent, htmlFile1ActualContent,
                "Parsing the first markdown file by truncating the existing html file in the target dir didn't work because content " +
                    "is different than expected!");

            String htmlFile2ActualContent = "";

            try (var reader = Files.newBufferedReader(htmlFile2)) {
                int readerCharInt = reader.read();

                while(readerCharInt != -1) {
                    htmlFile2ActualContent += (char) readerCharInt;
                    readerCharInt = reader.read();
                }
            }

            Assertions.assertEquals(htmlFile2ExpectedContent, htmlFile2ActualContent,
                "Parsing the second markdown file by truncating the existing html file in the target dir didn't work because content " +
                    "is different than expected!");
        }
        finally {
            //Cleanup phase
            Files.delete(markdownFile1);
            Files.delete(markdownFile2);
            Files.delete(htmlFile1);
            Files.delete(htmlFile2);
            Files.delete(directoryPathMarkdown);
            Files.delete(directoryPathHtml);
        }
    }
}
