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

package org.apache.dubbo.errorcode.linktest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests of LinkTestingForkJoinTask, which uses mocked link tester.
 */
class LinkTestingForkJoinTaskTest {

    @Test
    void testMockedLinkTester() {
        List<String> errorCodesToTest = Arrays.asList(
            // 20 error codes.

            "0-4", "1-1", "2-2", "3-1", "4-11",
            "5-1", "8-1", "9-1", "10-1", "11-1",
            "22-33", "41-1", "43-1", "43-3", "44-11",
            "66-6", "79-0", "88-1", "90-1", "99-1"
        );

        List<String> expectedReturn = Arrays.asList("1-1", "3-1", "43-1");

        LinkTester linkTester = mock(LinkTester.class);

        // Since we're using views of errorCodesToTest collection,
        // we need to use argument matchers.
        when(linkTester.test(anyList())).thenAnswer(invocation -> {

            List<String> codesToTest = invocation.getArgument(0);

            if (codesToTest == null) {
                return Collections.emptyList();
            }

            List<String> actualReturn = new ArrayList<>();

            for (String i : codesToTest) {
                if (expectedReturn.contains(i)) {
                    actualReturn.add(i);
                }
            }

            return actualReturn;
        });

        Assertions.assertEquals(expectedReturn,
                LinkTestingForkJoinTask.findDocumentMissingErrorCodes(linkTester, errorCodesToTest));
    }
}
