/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.smithy.docgen.core;

import software.amazon.smithy.codegen.core.SmithyIntegration;
import software.amazon.smithy.docgen.core.writers.DocWriter;

public interface DocIntegration extends SmithyIntegration<DocSettings, DocWriter, DocGenerationContext> {
}
