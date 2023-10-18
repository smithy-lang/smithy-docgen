/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.smithy.docgen.core;

import java.util.List;
import software.amazon.smithy.docgen.core.writers.MarkdownWriter;
import software.amazon.smithy.utils.SmithyInternalApi;

/**
 * Applies the built-in {@link DocFormat}s.
 */
@SmithyInternalApi
public class BuiltInDocFormatsIntegration implements DocIntegration {
    @Override
    public List<DocFormat> docFormats(DocSettings settings) {
        return List.of(
            new DocFormat("markdown", ".md", new MarkdownWriter.Factory())
        );
    }
}
