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
import org.apache.dubbo.errorcode.reporter.Reporter;
import org.apache.dubbo.errorcode.util.FileUtils;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Future;

@Disabled
class IntegrationTest {

    private static final String INTEGRATION_TEST_REPORTER = "org.apache.dubbo.errorcode.integration.IntegrationTestReporter";

    @BeforeAll
    static void init() throws IOException {
        String reporterConfig = FileUtils.getResourceFilePath("reporter-classes.cfg");
        Path reporterConfigPathObject = Paths.get(reporterConfig);

        Files.copy(reporterConfigPathObject,
                Paths.get(reporterConfigPathObject.getParent().toString(), "reporter-classes.bu.cfg"),
                StandardCopyOption.REPLACE_EXISTING);

        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(reporterConfigPathObject, StandardOpenOption.APPEND)) {
            bufferedWriter.write(System.getProperty("line.separator"));
            bufferedWriter.write(INTEGRATION_TEST_REPORTER);
        }
    }

    @Test
    void test() throws Exception {
        System.setProperty("dubbo.eci.link-test.repo", "D:\\dubbo-website");

        Main.main(new String[] {"D:\\ci-test\\dubbo"});

        for (Reporter r : ErrorCodeInspectorConfig.REPORTERS) {
            if (r instanceof Future) {
                // Injected reporter.

                System.out.println(((Future<?>) r).get());
            }
        }
    }

    @AfterAll
    static void tearDown() throws IOException {
        String reporterConfig = FileUtils.getResourceFilePath("reporter-classes.cfg");
        Path reporterConfigPathObject = Paths.get(reporterConfig);

        Files.delete(reporterConfigPathObject);

        Files.copy(Paths.get(reporterConfigPathObject.getParent().toString(), "reporter-classes.bu.cfg"),
                reporterConfigPathObject,
                StandardCopyOption.REPLACE_EXISTING);
    }
}
