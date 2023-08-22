/*
 * Copyright 2023 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package software.amazon.smithy.docgen.core;

import software.amazon.smithy.codegen.core.SymbolWriter;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.traits.DocumentationTrait;
import software.amazon.smithy.utils.StringUtils;

public final class MarkdownTextWriter
        extends SymbolWriter<MarkdownTextWriter, MarkdownTextImportContainer> {
    private int headerLevel = 0;

    public MarkdownTextWriter() {
        super(new MarkdownTextImportContainer());
    }

    public static final class Factory implements SymbolWriter.Factory<MarkdownTextWriter> {
        @Override
        public MarkdownTextWriter apply(String s, String s1) {
            return new MarkdownTextWriter();
        }
    }

    public MarkdownTextWriter writeShapeDocs(Shape shape) {
        shape.getTrait(DocumentationTrait.class)
                .map(DocumentationTrait::getValue)
                .ifPresent(this::writeWithNewline);
        return this;
    }

    private MarkdownTextWriter writeWithNewline(Object content, Object... args) {
        write(content, args);
        write("");
        return this;
    }

    public MarkdownTextWriter openHeader(String content) {
        headerLevel++;
        if (headerLevel > 5) {
            throw new RuntimeException("Tried opening a header nested too deeply.");
        }

        writeWithNewline(StringUtils.repeat("#", headerLevel) + " " + content);

        return this;
    }

    public MarkdownTextWriter closeHeader() {
        if (headerLevel <= 0) {
            throw new RuntimeException("Closed a header that was never opened.");
        }
        write("");

        headerLevel--;
        return this;
    }
}
