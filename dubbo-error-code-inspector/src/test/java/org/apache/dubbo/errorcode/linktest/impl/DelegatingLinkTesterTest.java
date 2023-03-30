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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

@SuppressWarnings("all")
class DelegatingLinkTesterTest {

    @BeforeEach
    void init() {
        System.setProperty("dubbo.eci.link-test.repo", "");
    }

    @Test
    void testConstructorSelection() {

        DelegatingLinkTester delegatingLinkTester = new DelegatingLinkTester();
        Assertions.assertEquals(HttpRequestingLinkTester.class, delegatingLinkTester.getLinkTester().getClass());

        System.setProperty("dubbo.eci.link-test.repo", "D:\\dubbo-website");

        delegatingLinkTester = new DelegatingLinkTester();
        Assertions.assertEquals(GitRepositoryFileLinkTester.class, delegatingLinkTester.getLinkTester().getClass());
    }

    @Test
    void testCloseMethodTransferring() throws IOException {

        LinkTester linkTester = Mockito.mock(LinkTester.class);

        DelegatingLinkTester delegatingLinkTester = new DelegatingLinkTester(linkTester);
        delegatingLinkTester.close();

        Mockito.verify(linkTester).close();
    }

    @Test
    void testFallbackLinkTesterTransferring() throws IOException {

        LinkTester linkTester = Mockito.mock(LinkTester.class);
        Mockito.when(linkTester.test(Mockito.any())).thenThrow(UnsupportedOperationException.class);

        LinkTester fallbackLinkTester = Mockito.mock(LinkTester.class);
        Mockito.when(fallbackLinkTester.test(Mockito.any())).thenReturn(Collections.emptyList());

        DelegatingLinkTester delegatingLinkTester = new DelegatingLinkTester(linkTester, () -> fallbackLinkTester);

        delegatingLinkTester.test(Arrays.asList("1-1", "2-2"));

        Mockito.verify(fallbackLinkTester).test(Mockito.any());
    }

    @Test
    void testFallbackLinkTesterFinalThrows() throws IOException {

        LinkTester linkTester = Mockito.mock(LinkTester.class);
        Mockito.when(linkTester.test(Mockito.any())).thenThrow(UnsupportedOperationException.class);

        LinkTester fallbackLinkTester = Mockito.mock(LinkTester.class);
        Mockito.when(fallbackLinkTester.test(Mockito.any())).thenThrow(UnsupportedOperationException.class);

        DelegatingLinkTester delegatingLinkTester = new DelegatingLinkTester(linkTester, () -> fallbackLinkTester);

        Assertions.assertThrows(InternalError.class, () -> delegatingLinkTester.test(Arrays.asList("1-1", "2-2")));
    }

}