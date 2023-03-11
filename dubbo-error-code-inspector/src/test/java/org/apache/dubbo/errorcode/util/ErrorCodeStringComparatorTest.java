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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Unit tests of ErrorCodeStringComparator.
 */
class ErrorCodeStringComparatorTest {

    @Test
    void testSmallerThan() {
        // 1-1 < 99-1
        Assertions.assertTrue(
                ErrorCodeStringComparator.getInstance().compare(
                        "1-1", "99-1"
                ) < 0
        );
    }

    @Test
    void testSameCategorySmallerThan() {
        // 2-1 < 2-3
        Assertions.assertTrue(
                ErrorCodeStringComparator.getInstance().compare(
                        "2-1", "2-3"
                ) < 0
        );
    }

    @Test
    void testEquals() {
        // 1-1 = 1-1
        Assertions.assertEquals(0,
                ErrorCodeStringComparator.getInstance().compare("1-1", "1-1"));
    }

    @Test
    void testGreaterThan() {
        // 5-3 > 1-1
        Assertions.assertTrue(
                ErrorCodeStringComparator.getInstance().compare(
                        "5-3", "1-1"
                ) > 0
        );
    }

    @Test
    void testSameCategoryGreaterThan() {
        // 1-3 > 1-1
        Assertions.assertTrue(
                ErrorCodeStringComparator.getInstance().compare(
                        "1-3", "1-1"
                ) > 0
        );
    }
}
