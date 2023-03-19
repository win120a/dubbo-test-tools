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

import org.apache.dubbo.errorcode.util.ErrorUrlUtils;

import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class HttpRequestingLinkTesterTest {

    private CloseableHttpClient generateMockedHttpClient(List<String> positiveResultErrorCodes) throws IOException {

        if (positiveResultErrorCodes == null) {
            positiveResultErrorCodes = Collections.emptyList();
        }

        StatusLine positiveStatusLine = mock(StatusLine.class);
        when(positiveStatusLine.getStatusCode()).thenReturn(200);

        StatusLine negativeStatusLine = mock(StatusLine.class);
        when(negativeStatusLine.getStatusCode()).thenReturn(404);

        CloseableHttpResponse negativeResponse = mock(CloseableHttpResponse.class);
        when(negativeResponse.getStatusLine()).thenReturn(negativeStatusLine);

        CloseableHttpResponse positiveResponse = mock(CloseableHttpResponse.class);
        when(positiveResponse.getStatusLine()).thenReturn(positiveStatusLine);

        CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);

        List<String> finalPositiveResultErrorCodes = positiveResultErrorCodes;
        when(mockHttpClient.execute(any(HttpUriRequest.class))).thenAnswer(invocation -> {
            HttpUriRequest request = invocation.getArgument(0);

            String errorCode = ErrorUrlUtils.getErrorCodeThroughErrorUrl(request.getURI().toString());

            return finalPositiveResultErrorCodes.contains(errorCode) ? positiveResponse : negativeResponse;
        });

        return mockHttpClient;
    }

    @Test
    void testAlways404HttpClient() throws IOException {
        HttpRequestingLinkTester httpRequestingLinkTester = new HttpRequestingLinkTester(generateMockedHttpClient(Collections.emptyList()));
        Assertions.assertEquals(Arrays.asList("1-1", "3-3"), httpRequestingLinkTester.test(Arrays.asList("1-1", "3-3")));
    }

    @Test
    void testPartialPassHttpClient() throws IOException {
        HttpRequestingLinkTester httpRequestingLinkTester = new HttpRequestingLinkTester(
                generateMockedHttpClient(Arrays.asList("1-1", "3-3")));

        Assertions.assertEquals(Collections.singletonList("4-4"),
                httpRequestingLinkTester.test(Arrays.asList("1-1", "3-3", "4-4")));
    }

    @Test
    void testNullInvocation() throws IOException {
        CloseableHttpClient mockedHttpClient = generateMockedHttpClient(Arrays.asList("1-1", "3-3"));

        HttpRequestingLinkTester httpRequestingLinkTester = new HttpRequestingLinkTester(mockedHttpClient);
        httpRequestingLinkTester.test(null);

        verify(mockedHttpClient, never()).execute(any(HttpUriRequest.class));
        verify(mockedHttpClient, never()).close();
    }
}
