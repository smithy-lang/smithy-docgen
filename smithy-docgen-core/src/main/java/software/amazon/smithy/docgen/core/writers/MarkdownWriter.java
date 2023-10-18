/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.smithy.docgen.core.writers;

import software.amazon.smithy.codegen.core.SymbolWriter;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.traits.DocumentationTrait;
import software.amazon.smithy.utils.SmithyUnstableApi;
import software.amazon.smithy.utils.StringUtils;

/**
 * Writes documentation in <a href="https://spec.commonmark.org">CommonMark</a> format.
 */
@SmithyUnstableApi
public final class MarkdownWriter extends DocWriter {

    /**
     * Constructor.
     */
    public MarkdownWriter() {
        super(new DocImportContainer());
    }

    /**
     * Factory to construct {@code MarkdownWriter}s.
     */
    public static final class Factory implements SymbolWriter.Factory<DocWriter> {
        @Override
        public DocWriter apply(String s, String s1) {
            return new MarkdownWriter();
        }
    }

    @Override
    public DocWriter writeShapeDocs(Shape shape) {
        shape.getTrait(DocumentationTrait.class)
                .map(DocumentationTrait::getValue)
                .ifPresent(this::writeWithNewline);
        return this;
    }

    private DocWriter writeWithNewline(Object content, Object... args) {
        write(content, args);
        write("");
        return this;
    }

    @Override
    public DocWriter openHeading(String content, int level) {
        writeWithNewline(StringUtils.repeat("#", level) + " " + content);
        return this;
    }

    @Override
    public String toString() {
        // Ensure there's exactly one trailing newline
        return super.toString().stripTrailing() + "\n";
    }
}
