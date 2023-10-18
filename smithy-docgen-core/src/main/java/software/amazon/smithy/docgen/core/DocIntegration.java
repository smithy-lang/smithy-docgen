/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.smithy.docgen.core;

import software.amazon.smithy.codegen.core.SmithyIntegration;
import software.amazon.smithy.docgen.core.writers.DocWriter;
import software.amazon.smithy.utils.SmithyUnstableApi;

/**
 * Allows integrating additional functionality into the documentation generator.
 *
 * <p>{@code DocIntegration}s are loaded as a Java SPI. To make your integration
 * discoverable, add a file to {@code META-INF/services} named
 * {@code software.amazon.smithy.docgen.core.DocIntegration} where each line is
 * the fully-qualified class name of your integrations. Several tools, such as
 * {@code AutoService}, can do this for you.
 */
@SmithyUnstableApi
public interface DocIntegration extends SmithyIntegration<DocSettings, DocWriter, DocGenerationContext> {
}
