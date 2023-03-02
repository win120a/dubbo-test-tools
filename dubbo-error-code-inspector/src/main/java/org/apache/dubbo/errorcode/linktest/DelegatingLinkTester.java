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

import java.io.IOException;
import java.util.List;

/**
 * Delegating LinkTester, which tests the property and selects the tester.
 */
public class DelegatingLinkTester implements LinkTester {

    private LinkTester linkTester;

    public DelegatingLinkTester() {
        if (System.getProperty("dubbo.eci.link-test.repo") == null) {
            System.out.println("Initially use HTTP Requesting Link Tester.");
            linkTester = new HttpRequestingLinkTester();
        } else {
            linkTester = new GitRepositoryFileLinkTester();
        }
    }

    @Override
    public List<String> test(List<String> codesToTest) {

        try {
            return linkTester.test(codesToTest);

        } catch (Exception e) {
            if (linkTester.getClass() != HttpRequestingLinkTester.class) {
                linkTester = new HttpRequestingLinkTester();
                return linkTester.test(codesToTest);
            }

            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws IOException {
        linkTester.close();
    }
}
