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

import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;

/**
 * Delegating LinkTester, which tests the property and selects the tester.
 */
public class DelegatingLinkTester implements LinkTester {

    private LinkTester linkTester;

    private final Supplier<? extends LinkTester> fallbackLinkTesterSupplier;

    public DelegatingLinkTester() {

        String testingRepoProperty = System.getProperty("dubbo.eci.link-test.repo");

        if (testingRepoProperty == null || testingRepoProperty.isEmpty()) {
            linkTester = new HttpRequestingLinkTester();
        } else {
            linkTester = new GitRepositoryFileLinkTester(testingRepoProperty);
        }

        fallbackLinkTesterSupplier = HttpRequestingLinkTester::new;
    }

    DelegatingLinkTester(LinkTester linkTester) {
        this(linkTester, HttpRequestingLinkTester::new);
    }

    <T extends LinkTester> DelegatingLinkTester(LinkTester linkTester, Supplier<T> fallbackLinkTesterSupplier) {
        this.linkTester = linkTester;
        this.fallbackLinkTesterSupplier = fallbackLinkTesterSupplier;
    }

    @Override
    public List<String> test(List<String> codesToTest) {

        try {
            return linkTester.test(codesToTest);

        } catch (Exception e) {
            try {
                linkTester = fallbackLinkTesterSupplier.get();
                return linkTester.test(codesToTest);
            } catch (Exception fallbackError) {
                throw new InternalError(e);
            }
        }
    }

    @Override
    public void close() throws IOException {
        linkTester.close();
    }

    LinkTester getLinkTester() {
        return linkTester;
    }
}
