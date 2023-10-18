/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.smithy.docgen.core.writers;

import software.amazon.smithy.codegen.core.SymbolWriter;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.utils.SmithyUnstableApi;

/**
 * A {@code SymbolWriter} provides abstract methods that will be used during
 * documentation generation. This allows for other formats to be swapped out
 * without much difficulty.
 */
@SmithyUnstableApi
public abstract class DocWriter extends SymbolWriter<DocWriter, DocImportContainer> {
    public DocWriter(DocImportContainer importContainer) {
        super(importContainer);
    }

    /**
     * Writes out the content of the shape's
     * <a href="https://smithy.io/2.0/spec/documentation-traits.html#smithy-api-documentation-trait">
     * documentation trait</a>, if present.
     *
     * <p>Smithy's documentation trait is in the
     * <a href="https://spec.commonmark.org">CommonMark</a> format, so writers
     * for formats that aren't based on CommonMark will need to convert the value to
     * their format. This includes raw HTML, which CommonMark allows.
     *
     * <p>If the shape doesn't have a documentation trait, the writer MAY write out
     * default documentation.
     *
     * @param shape The shape whose documentation should be written.
     * @return returns the writer.
     */
    public abstract DocWriter writeShapeDocs(Shape shape);

    /**
     * Writes a heading with the given content.
     *
     * <p>{@link #closeHeading} will be called to enable cleaning up any resources or
     * context this method creates.
     *
     * @param content A string to use as the heading content.
     * @return returns the writer.
     */
    public abstract DocWriter openHeading(String content);

    /**
     * Closes the current heading, cleaning any context created for the current level.
     *
     * @return returns the writer.
     */
    public abstract DocWriter closeHeading();
}
