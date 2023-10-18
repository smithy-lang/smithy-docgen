/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.smithy.docgen.core.writers;

import software.amazon.smithy.codegen.core.CodegenException;
import software.amazon.smithy.codegen.core.SymbolWriter;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.traits.DocumentationTrait;
import software.amazon.smithy.utils.StringUtils;

public final class MarkdownWriter extends DocWriter {
    private static int MAX_HEADING_DEPTH = 5;

    private int headerLevel = 0;

    public MarkdownWriter() {
        super(new DocImportContainer());
    }

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
    public DocWriter openHeading(String content) {
        headerLevel++;
        if (headerLevel > MAX_HEADING_DEPTH) {
            throw new CodegenException(String.format(
                "Tried opening a heading nested more deeply than the max depth of %d.",
                MAX_HEADING_DEPTH
            ));
        }

        writeWithNewline(StringUtils.repeat("#", headerLevel) + " " + content);

        return this;
    }

    @Override
    public DocWriter closeHeading() {
        if (headerLevel <= 0) {
            throw new CodegenException("Closed a header that was never opened.");
        }
        write("");

        headerLevel--;
        return this;
    }
}
