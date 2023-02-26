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

import org.apache.dubbo.errorcode.util.ErrorUrlUtils;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Link testing (fork-join) task.
 */
public class HttpRequestingLinkTester implements LinkTester {

    private static final CloseableHttpClient HTTP_CLIENT = HttpClients.createDefault();

    @Override
    public List<String> test(List<String> codes) {

        List<String> urls = codes.stream()
                .distinct()
                .sorted()
                .map(ErrorUrlUtils::getErrorUrl)
                .collect(Collectors.toList());

        List<String> result = new ArrayList<>();

        for (String url : urls) {

            HttpGet getRequest = new HttpGet(url);
            getRequest.addHeader("Accept-Language", "zh-CN");

            try (CloseableHttpResponse resp = HTTP_CLIENT.execute(getRequest)) {
                if (resp.getStatusLine().getStatusCode() != 200) {
                    result.add(ErrorUrlUtils.getErrorCodeThroughErrorUrl(url));
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return result;
    }

    @Override
    public void close() {
        try {
            HTTP_CLIENT.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
