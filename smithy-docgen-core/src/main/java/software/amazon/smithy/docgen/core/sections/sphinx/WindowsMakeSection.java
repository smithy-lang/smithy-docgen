/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.smithy.docgen.core.sections.sphinx;

import software.amazon.smithy.docgen.core.DocGenerationContext;
import software.amazon.smithy.utils.CodeSection;

/**
 * Generates a batch script that wraps sphinx-build with default arguments.
 * @param context The context used to generate documentation.
 */
public record WindowsMakeSection(DocGenerationContext context) implements CodeSection {
}
