/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.smithy.docgen.core.integrations;

import java.util.List;
import software.amazon.smithy.docgen.core.DocFormat;
import software.amazon.smithy.docgen.core.DocGenerationContext;
import software.amazon.smithy.docgen.core.DocIntegration;
import software.amazon.smithy.docgen.core.DocSettings;
import software.amazon.smithy.docgen.core.interceptors.DefaultValueInterceptor;
import software.amazon.smithy.docgen.core.interceptors.DeprecatedInterceptor;
import software.amazon.smithy.docgen.core.interceptors.ErrorFaultInterceptor;
import software.amazon.smithy.docgen.core.interceptors.ExternalDocsInterceptor;
import software.amazon.smithy.docgen.core.interceptors.NullabilityInterceptor;
import software.amazon.smithy.docgen.core.interceptors.SinceInterceptor;
import software.amazon.smithy.docgen.core.writers.DocWriter;
import software.amazon.smithy.docgen.core.writers.MarkdownWriter;
import software.amazon.smithy.utils.CodeInterceptor;
import software.amazon.smithy.utils.CodeSection;
import software.amazon.smithy.utils.SmithyInternalApi;

/**
 * Applies the built-in {@link DocFormat}s and base {@code CodeSection}s.
 *
 * <p>This integration runs in high priority to ensure that other integrations can see
 * and react to changes it makes. To have an integration reliably run
 * before this, override {@link DocIntegration#runBefore} with the output of
 * {@link BuiltinsIntegration#name} in the list. Similarly, to guarantee an integration
 * is run after this, override {@link DocIntegration#runAfter} with the same argument.
 */
@SmithyInternalApi
public class BuiltinsIntegration implements DocIntegration {

    @Override
    public byte priority() {
        // Add the builtins at a highest priority so that they almost always are run
        // first. Using runBefore it is still possible to ensure an integration is run
        // before this.
        return 127;
    }

    @Override
    public List<DocFormat> docFormats(DocSettings settings) {
        return List.of(
            new DocFormat("markdown", ".md", new MarkdownWriter.Factory())
        );
    }

    @Override
    public List<? extends CodeInterceptor<? extends CodeSection, DocWriter>> interceptors(
            DocGenerationContext context) {
        return List.of(
                new ExternalDocsInterceptor(),
                new ErrorFaultInterceptor(),
                new DefaultValueInterceptor(),
                new SinceInterceptor(),
                new DeprecatedInterceptor(),
                new NullabilityInterceptor() // This goes last so that its output is always first.
        );
    }
}
