package org.apache.dubbo.errorcode.linktest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Link tester which finds corresponding markdown file in website's Git repository.
 */
public class GitRepositoryFileLinkTester implements LinkTester {

    private static final String PATH = System.getProperty("dubbo.eci.link-test.repo");

    private static class GitRepoTesterPatterns {
        static final Pattern rewriteCondPattern = Pattern.compile("RewriteCond .*Accept-Language.*zh*.");
        static final Pattern rewriteRulePattern = Pattern.compile("RewriteRule .*faq/*.");
        static final Pattern chineseLanguageConfigPattern = Pattern.compile("\\[languages\\..*cn*.\\]");
    }

    @Override
    public List<String> test(List<String> codesToTest) {

        List<String> docMissingErrorCodes = new ArrayList<>();

        String faqFilePath = findFaqFilePath();

        for (String code : codesToTest) {
            String docFilePath = code.replace('-', File.separatorChar) + ".md";

            if (!Files.exists(Paths.get(faqFilePath, docFilePath))) {
                docMissingErrorCodes.add(code);
            }
        }

        return docMissingErrorCodes;
    }

    @Override
    public void close() throws IOException {
        // No need.
    }

    private String findFaqFilePath() {
        Path htaPath = Paths.get(PATH, ".htaccess");

        try (Scanner htaScanner = new Scanner(htaPath)) {

            while (htaScanner.hasNextLine()) {
                String nextLine = htaScanner.nextLine();

                if (GitRepoTesterPatterns.rewriteCondPattern.matcher(nextLine).find()) {
                    nextLine = htaScanner.nextLine();

                    if (GitRepoTesterPatterns.rewriteRulePattern.matcher(nextLine).find()) {
                        return Paths.get(findContentPath(),
                                nextLine.split("\"")[1].replace("$1", "")).toString();
                    }
                }
            }

            throw new IllegalStateException("Can't find FAQ file folder by searching .htaccess.");

        } catch (IOException e) {
            throw new IllegalStateException("Can't read .htaccess.", e);
        }
    }

    private static String findContentPath() {

        Path configTomlPath = Paths.get(PATH, "config.toml");

        try (Scanner configTomlScanner = new Scanner(configTomlPath)) {
            while (configTomlScanner.hasNextLine()) {
                String nextLine = configTomlScanner.nextLine();

                if (GitRepoTesterPatterns.chineseLanguageConfigPattern.matcher(nextLine).find()) {

                    while (!nextLine.startsWith("contentDir")) {
                        nextLine = configTomlScanner.nextLine();

                        if (nextLine.startsWith("[")) {
                            throw new IllegalStateException("Can't find content directory in config.toml");
                        }
                    }

                    return Paths.get(PATH, nextLine.split("=")[1]
                            .replace("\"", "")
                            .trim()).getParent().toString();
                }
            }

            throw new IllegalStateException("Can't find contents folder by searching config.toml");

        } catch (IOException e) {
            throw new IllegalStateException("Can't read config.toml.", e);
        }
    }
}
