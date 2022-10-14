/*
 * Copyright 2021 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openrewrite.java.testing.junit5

import org.junit.jupiter.api.Test
import org.openrewrite.config.Environment
import org.openrewrite.java.Assertions.java
import org.openrewrite.java.JavaParser
import org.openrewrite.test.RecipeSpec
import org.openrewrite.test.RewriteTest

class CleanupAssertionsTest : RewriteTest {
    override fun defaults(spec: RecipeSpec) {
        spec.recipe(Environment.builder()
                .scanRuntimeClasspath("org.openrewrite.java.testing.junit5")
                .build()
                .activateRecipes("org.openrewrite.java.testing.junit5.CleanupAssertions"))
            .parser(JavaParser.fromJavaVersion()
                .classpath("junit-jupiter-api"))
    }

    @Test
    fun assertTrueComparisonNullToAssertNull() = rewriteRun(java(
        """
            import org.junit.jupiter.api.Assertions;
            import org.junit.jupiter.api.Test;

            class ExampleTest {
                @Test
                void test() {
                    Assertions.assertTrue("" == null);
                }
            }
        """, """
            import org.junit.jupiter.api.Assertions;
            import org.junit.jupiter.api.Test;

            class ExampleTest {
                @Test
                void test() {
                    Assertions.assertNull("");
                }
            }
        """
    ))

    @Test
    fun assertFalseNegatedEqualsToAssertEquals() = rewriteRun(java(
        """
            import org.junit.jupiter.api.Assertions;
            import org.junit.jupiter.api.Test;

            class ExampleTest {
                @Test
                void test() {
                    Assertions.assertFalse(!"".equals(""));
                }
            }
        """, """
            import org.junit.jupiter.api.Assertions;
            import org.junit.jupiter.api.Test;

            class ExampleTest {
                @Test
                void test() {
                    Assertions.assertEquals("", "");
                }
            }
        """
    ))

    @Test
    fun assertFalseNegatedEqualsNullToAssertNull() = rewriteRun(java(
        """
            import org.junit.jupiter.api.Assertions;
            import org.junit.jupiter.api.Test;

            class ExampleTest {
                @Test
                void test() {
                    Assertions.assertFalse(!"".equals(null));
                }
            }
        """, """
            import org.junit.jupiter.api.Assertions;
            import org.junit.jupiter.api.Test;

            class ExampleTest {
                @Test
                void test() {
                    Assertions.assertNull("");
                }
            }
        """
    ))
}