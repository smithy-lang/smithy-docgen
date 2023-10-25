/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.smithy.docgen.core.writers;

import java.util.function.Consumer;
import software.amazon.smithy.codegen.core.CodegenException;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.codegen.core.SymbolWriter;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.utils.SmithyUnstableApi;

/**
 * A {@code SymbolWriter} provides abstract methods that will be used during
 * documentation generation. This allows for other formats to be swapped out
 * without much difficulty.
 */
@SmithyUnstableApi
public abstract class DocWriter extends SymbolWriter<DocWriter, DocImportContainer> {
    private static final int MAX_HEADING_DEPTH = 6;

    private int headingDepth = 0;

    /**
     * Constructor.
     *
     * @param importContainer The container to store any imports in.
     */
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
     * @param model The model whose documentation is being written.
     * @return returns the writer.
     */
    public abstract DocWriter writeShapeDocs(Shape shape, Model model);

    /**
     * Writes a heading with the given content.
     *
     * <p>{@link #closeHeading} will be called to enable cleaning up any resources or
     * context this method creates.
     *
     * @param content A string to use as the heading content.
     * @return returns the writer.
     */
    public DocWriter openHeading(String content) {
        headingDepth++;
        if (headingDepth > MAX_HEADING_DEPTH) {
            throw new CodegenException(String.format(
                "Tried opening a heading nested more deeply than the max depth of %d.",
                MAX_HEADING_DEPTH
            ));
        }
        return openHeading(content, headingDepth);
    }

    /**
     * Writes a heading of a given level with the given content.
     *
     * <p>{@link #closeHeading} will be called to enable cleaning up any resources or
     * context this method creates.
     *
     * @param content A string to use as the heading content.
     * @param level The level of the heading to open. This corresponds to HTML heading
     *              levels, and will only have values between 1 and 6.
     * @return returns the writer.
     */
    abstract DocWriter openHeading(String content, int level);

    /**
     * Closes the current heading, cleaning any context created for the current level,
     * then writes a blank line.
     *
     * @return returns the writer.
     */
    public DocWriter closeHeading() {
        headingDepth--;
        if (headingDepth < 0) {
            throw new CodegenException(
                "Attempted to close a heading when at the base heading level."
            );
        }
        write("");
        return this;
    }

    /**
     * Writes any context needed before listing members.
     *
     * <p>For example, a direct HTML writer might need to write out a {@code ul} tag.
     *
     * @return returns the writer.
     */
    public abstract DocWriter openMemberListing();

    /**
     * Writes any closing context needed before ending a member listing.
     *
     * <p>For example, a direct HTML writer might need to write a closing {@code ul}
     * tag.
     *
     * @return returns the writer.
     */
    public abstract DocWriter closeMemberListing();

    /**
     * Writes out the heading information for a member's documentation.
     *
     * <p>For example, a direct HTML writer that renders member listings as HTML lists
     * might write an opening {@code li} tag.
     *
     * <p>Implementations MUST create a linkable element where possible if the
     * {@link software.amazon.smithy.docgen.core.DocSymbolProvider#LINK_ID_PROPERTY}
     * is set.
     *
     * @param memberSymbol A symbol representing the member being documented.
     * @param writeType A consumer that writes the type, including links to any referenced
     *                  shapes.
     * @return returns the writer.
     */
    public abstract DocWriter openMemberEntry(Symbol memberSymbol, Consumer<DocWriter> writeType);

    /**
     * Writes any context needed to close out the documentation for a member.
     *
     * <p>For example, a direct HTML writer might need to write a closing {@code li}
     * tag.
     *
     * @return returns the writer.
     */
    public abstract DocWriter closeMemberEntry();
}
