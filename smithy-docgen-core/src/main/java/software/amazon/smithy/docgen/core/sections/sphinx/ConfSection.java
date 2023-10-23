/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.smithy.docgen.core.sections.sphinx;

import software.amazon.smithy.docgen.core.DocGenerationContext;
import software.amazon.smithy.utils.CodeSection;

/**
 * Generates the {@code conf.py} file for sphinx.
 * @see <a href="https://www.sphinx-doc.org/en/master/usage/configuration.html">
 *     sphinx config docs</a>
 * @param context The context used to generate documentation.
 */
public record ConfSection(DocGenerationContext context) implements CodeSection {
}