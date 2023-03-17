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

package org.apache.dubbo.errorcode.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import static org.junit.jupiter.api.Assertions.assertTrue;

@EnabledOnOs({OS.LINUX, OS.FREEBSD, OS.MAC})
class UnixFilePathComparatorTest {

    @Test
    void testUnixSmallerThan() {
        assertTrue(
                FilePathComparator.getInstance().compare(
                        "/a/b/c",
                        "/a/b/d"
                ) < 0
        );
    }

    @Test
    void testUnixGreaterThan() {
        assertTrue(
                FilePathComparator.getInstance().compare(
                        "/a/b/d",
                        "/a/b/c"
                ) > 0
        );
    }

    @Test
    void testUnixDifferentDirectorySmallerThan() {
        assertTrue(
                FilePathComparator.getInstance().compare(
                        "/a/b/d",
                        "/a/b/d/e"
                ) < 0
        );
    }

    @Test
    void testUnixDifferentDirectoryGreaterThan() {
        assertTrue(
                FilePathComparator.getInstance().compare(
                        "/a/b/d/e",
                        "/a/b/d"
                ) > 0
        );
    }

    @Test
    void testRootDirectory() {
        assertTrue(
                FilePathComparator.getInstance().compare(
                        "/",
                        "/a/b/d"
                ) < 0
        );
    }
}