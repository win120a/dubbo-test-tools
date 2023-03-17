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

import static org.junit.jupiter.api.Assertions.*;

@EnabledOnOs(OS.WINDOWS)
class WindowsFilePathComparatorTest {

    @Test
    void testWindowsDriveLetterSmallerThan() {
        assertTrue(
                FilePathComparator.getInstance().compare(
                        "C:\\",
                        "D:\\"
                ) < 0
        );
    }

    @Test
    void testWindowsDriveLetterGreaterThan() {
        assertTrue(
                FilePathComparator.getInstance().compare(
                        "D:\\",
                        "C:\\"
                ) > 0
        );
    }

    @Test
    void testWindowsDifferentDirectorySmallerThan() {
        assertTrue(
                FilePathComparator.getInstance().compare(
                        "D:\\",
                        "D:\\dubbo"
                ) < 0
        );
    }

    @Test
    void testWindowsDifferentDirectoryGreaterThan() {
        assertTrue(
                FilePathComparator.getInstance().compare(
                        "D:\\httpd",
                        "D:\\dubbo"
                ) > 0
        );
    }

    @Test
    void testWindowsDifferentDirectoryGreaterThan2() {
        assertTrue(
                FilePathComparator.getInstance().compare(
                        "D:\\httpd\\aa",
                        "D:\\dubbo"
                ) > 0
        );
    }

    @Test
    void testWindowsDifferentDriveSmallerThan() {
        assertTrue(
                FilePathComparator.getInstance().compare(
                        "D:\\dubbo",
                        "E:\\dubbo"
                ) < 0
        );
    }

    @Test
    void testWindowsDifferentDriveGreaterThan() {
        assertTrue(
                FilePathComparator.getInstance().compare(
                        "E:\\httpd",
                        "D:\\httpd"
                ) > 0
        );
    }
}