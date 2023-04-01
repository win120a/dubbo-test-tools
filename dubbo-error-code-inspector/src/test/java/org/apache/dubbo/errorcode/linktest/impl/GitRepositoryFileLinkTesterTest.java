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

package org.apache.dubbo.errorcode.linktest.impl;

import org.apache.dubbo.errorcode.linktest.LinkTester;
import org.apache.dubbo.errorcode.util.FileUtils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

class GitRepositoryFileLinkTesterTest {

    private static Path gitFilePathObject;

    private static GitRepositoryFileLinkTester linkTester;

    @BeforeAll
    static void init() {
        String gitFilePath = FileUtils.getResourceFilePath("mock-website-git-repo");
        gitFilePathObject = Paths.get(gitFilePath);
        linkTester = new GitRepositoryFileLinkTester(gitFilePath);
    }

    @Test
    void testFindFaqFilePath() {
        Assertions.assertEquals("content\\zh-cn\\docs3-v2\\java-sdk\\faq".replace('\\', File.separatorChar),
                gitFilePathObject.relativize(Paths.get(linkTester.findFaqFilePath())).toString());
    }

    @Test
    void testNonWebsiteGitRepoPath() {
        LinkTester nonWebSiteGitRepoTester = new GitRepositoryFileLinkTester(FileUtils.getResourceFilePath("mock-source"));

        Assertions.assertThrows(IllegalStateException.class, () -> nonWebSiteGitRepoTester.test(Arrays.asList("0-1")));
    }

    @Test
    void testNotExistedErrorCode() {
        Assertions.assertEquals(Arrays.asList("1-4"), linkTester.test(Arrays.asList("1-4")));
    }

    @Test
    void testPartExistsErrorCodes() {
        Assertions.assertEquals(Arrays.asList("1-4"), linkTester.test(Arrays.asList("1-4", "1-1")));
    }

    @Test
    void testAllExistsErrorCodes() {
        Assertions.assertEquals(Collections.emptyList(), linkTester.test(Arrays.asList("1-1")));
    }
}