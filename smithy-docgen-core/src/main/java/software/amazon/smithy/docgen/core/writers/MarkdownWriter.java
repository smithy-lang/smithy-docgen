/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.smithy.docgen.core.writers;

import software.amazon.smithy.codegen.core.SymbolWriter;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.traits.DocumentationTrait;
import software.amazon.smithy.utils.StringUtils;

public final class MarkdownWriter
    extends DocWriter {
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
    public DocWriter openHeader(String content) {
        headerLevel++;
        if (headerLevel > 5) {
            throw new RuntimeException("Tried opening a header nested too deeply.");
        }

        writeWithNewline(StringUtils.repeat("#", headerLevel) + " " + content);

        return this;
    }

    @Override
    public DocWriter closeHeader() {
        if (headerLevel <= 0) {
            throw new RuntimeException("Closed a header that was never opened.");
        }
        write("");

        headerLevel--;
        return this;
    }
}
