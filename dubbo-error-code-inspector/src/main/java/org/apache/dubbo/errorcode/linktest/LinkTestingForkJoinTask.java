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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

/**
 * Link testing (fork-join) task.
 */
public class LinkTestingForkJoinTask extends RecursiveTask<List<String>> {

    private static final int THRESHOLD = 10;

    private final int start;

    private final int end;

    private final List<String> url;

    private static final ForkJoinPool FORK_JOIN_POOL = new ForkJoinPool();

    private final transient LinkTester linkTester;

    public LinkTestingForkJoinTask(int start, int end, List<String> url, LinkTester linkTester) {
        this.start = start;
        this.end = end;
        this.url = url;
        this.linkTester = linkTester;
    }

    @Override
    protected List<String> compute() {

        if (end - start >= THRESHOLD) {

            int middle = (start + end) / 2;

            LinkTestingForkJoinTask left = new LinkTestingForkJoinTask(start, middle, url, linkTester);
            LinkTestingForkJoinTask right = new LinkTestingForkJoinTask(middle, end, url, linkTester);

            left.fork();
            right.fork();

            List<String> leftR = left.join();
            List<String> rightR = right.join();

            List<String> result = new ArrayList<>(end - start);

            result.addAll(leftR);
            result.addAll(rightR);

            return result;

        } else {

            List<String> result = new ArrayList<>();

            for (int i = start; i < end; i++) {
                result.addAll(linkTester.test(url.subList(start, end)));
            }

            return result;
        }
    }

    public static void closeHttpClient() {
        FORK_JOIN_POOL.shutdown();
    }

    public static List<String> findDocumentMissingErrorCodes(List<String> codes) {

        List<String> urls = codes.stream().distinct().sorted().collect(Collectors.toList());

        try (LinkTester linkTester = new GitRepositoryFileLinkTester()) {
            LinkTestingForkJoinTask firstTask = new LinkTestingForkJoinTask(0, urls.size(), urls, linkTester);

            return FORK_JOIN_POOL.invoke(firstTask)
                    .stream()
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
