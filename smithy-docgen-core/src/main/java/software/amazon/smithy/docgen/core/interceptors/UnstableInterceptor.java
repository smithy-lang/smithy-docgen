/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.smithy.docgen.core.interceptors;

import software.amazon.smithy.docgen.core.sections.ShapeSubheadingSection;
import software.amazon.smithy.docgen.core.writers.DocWriter;
import software.amazon.smithy.docgen.core.writers.DocWriter.AdmonitionType;
import software.amazon.smithy.model.traits.UnstableTrait;
import software.amazon.smithy.utils.CodeInterceptor;
import software.amazon.smithy.utils.SmithyInternalApi;

/**
 * Adds a warning admonition to shapes marked as
 * <a href="https://smithy.io/2.0/spec/documentation-traits.html#unstable-trait">unstable</a>.
 */
@SmithyInternalApi
public final class UnstableInterceptor implements CodeInterceptor<ShapeSubheadingSection, DocWriter> {
    @Override
    public Class<ShapeSubheadingSection> sectionType() {
        return ShapeSubheadingSection.class;
    }

    @Override
    public boolean isIntercepted(ShapeSubheadingSection section) {
        return section.shape().hasTrait(UnstableTrait.class);
    }

    @Override
    public void write(DocWriter writer, String previousText, ShapeSubheadingSection section) {
        writer.openAdmonition(AdmonitionType.WARNING);
        writer.write("This is unstable or experimental and MAY change in the future.");
        writer.closeAdmonition();
    }
}
