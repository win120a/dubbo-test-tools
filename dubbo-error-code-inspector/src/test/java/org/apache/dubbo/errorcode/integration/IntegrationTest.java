/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.dubbo.errorcode.integration;

import org.apache.dubbo.errorcode.Main;
import org.apache.dubbo.errorcode.config.ErrorCodeInspectorConfig;
import org.apache.dubbo.errorcode.reporter.InspectionResult;
import org.apache.dubbo.errorcode.reporter.Reporter;
import org.apache.dubbo.errorcode.util.FileUtils;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;

@Disabled
@EnabledIfSystemProperty(named = "dubbo.eci.integration-test", matches = "true")
class IntegrationTest {

    private static final String INTEGRATION_TEST_REPORTER = "org.apache.dubbo.errorcode.integration.IntegrationTestReporter";

    private static final List<String> EXPECTED_ERROR_CODES = Arrays.asList(
            "0-1", "0-2", "0-3", "0-4", "0-5", "0-6", "0-7", "0-8", "0-9", "0-10",
            "0-11", "0-12", "0-13", "0-14", "0-15", "0-16", "0-17", "0-18", "0-19", "0-20",
            "0-21", "0-22", "0-23", "0-24", "0-25", "0-26",
            "1-1", "1-3", "1-4", "1-5", "1-6", "1-7", "1-8", "1-9", "1-10",
            "1-11", "1-12", "1-13", "1-14", "1-15", "1-16", "1-17", "1-18", "1-19", "1-20",
            "1-21", "1-22", "1-23", "1-24", "1-25", "1-26", "1-27", "1-28", "1-29", "1-30",
            "1-31", "1-32", "1-33", "1-34", "1-35", "1-36", "1-37", "1-38", "1-39",
            "2-1", "2-2", "2-3", "2-4", "2-5", "2-6", "2-7", "2-8", "2-9", "2-10",
            "2-11", "2-12", "2-13", "2-14", "2-15", "2-16", "2-17", "2-18", "2-19", "2-20",
            "3-1", "3-2", "3-3", "3-4", "3-5", "3-6", "3-7",
            "4-1", "4-2", "4-3", "4-4", "4-5", "4-6", "4-7", "4-8", "4-9", "4-10",
            "4-11", "4-12", "4-13", "4-14", "4-15", "4-16", "4-17", "4-18", "4-19", "4-20",
            "5-1", "5-2", "5-3", "5-4", "5-5", "5-6", "5-7", "5-8", "5-9", "5-10",
            "5-11", "5-12", "5-13", "5-14", "5-15", "5-16", "5-17", "5-20",
            "5-21", "5-22", "5-23", "5-24", "5-25", "5-26", "5-27", "5-28", "5-29", "5-30",
            "5-31", "5-32", "5-33", "5-34", "5-35", "5-36", "5-37", "5-39", "5-40",
            "6-1", "6-2", "6-3", "6-4", "6-5", "6-6", "6-7", "6-8", "6-9", "6-10",
            "6-11", "6-12", "6-13", "6-14", "6-15", "6-16", "7-1", "7-2", "7-3", "7-4", "7-5", "7-6");

    @BeforeAll
    static void init() throws IOException {
        if (System.getProperty("dubbo.eci.integration-test") == null ||
                !"true".equals(System.getProperty("dubbo.eci.integration-test"))) {
            return;
        }

        String reporterConfig = FileUtils.getResourceFilePath("reporter-classes.cfg");
        Path reporterConfigPathObject = Paths.get(reporterConfig);

        Files.copy(reporterConfigPathObject,
                Paths.get(reporterConfigPathObject.getParent().toString(), "reporter-classes.bu.cfg"),
                StandardCopyOption.REPLACE_EXISTING);

        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(reporterConfigPathObject, StandardOpenOption.APPEND)) {
            bufferedWriter.write(System.getProperty("line.separator"));
            bufferedWriter.write(INTEGRATION_TEST_REPORTER);
        }

        GitRepoHelper.initGitRepo("D:\\ci-test");
        GitRepoHelper.buildDubbo("D:\\ci-test\\dubbo");
    }

    @Test
    void test() throws Exception {
        System.setProperty("dubbo.eci.link-test.repo", "D:\\ci-test\\dubbo-website");

        Main.main(new String[] {"D:\\ci-test\\dubbo"});

        for (Reporter r : ErrorCodeInspectorConfig.REPORTERS) {
            if (r instanceof Future) {
                // Injected reporter.

                IntegrationTestReporter integrationTestReporter = (IntegrationTestReporter) r;

                InspectionResult result = integrationTestReporter.get();

                Assertions.assertEquals(Arrays.asList("1-23", "1-24", "1-25"), result.getLinkNotReachableErrorCodes());
                Assertions.assertEquals(EXPECTED_ERROR_CODES, result.getAllErrorCodes());

                return;
            }
        }

        Assertions.fail("Integration Test reporter didn't injected! ");
    }

    @AfterAll
    static void tearDown() throws IOException {
        if (System.getProperty("dubbo.eci.integration-test") == null ||
                !"true".equals(System.getProperty("dubbo.eci.integration-test"))) {
            return;
        }

        String reporterConfig = FileUtils.getResourceFilePath("reporter-classes.cfg");
        Path reporterConfigPathObject = Paths.get(reporterConfig);

        Files.delete(reporterConfigPathObject);

        Files.copy(Paths.get(reporterConfigPathObject.getParent().toString(), "reporter-classes.bu.cfg"),
                reporterConfigPathObject,
                StandardCopyOption.REPLACE_EXISTING);
    }
}
