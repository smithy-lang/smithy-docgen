/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.smithy.docgen.core.writers;

import static software.amazon.smithy.docgen.core.DocgenUtils.getSymbolLink;

import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Consumer;
import software.amazon.smithy.codegen.core.CodegenException;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.codegen.core.SymbolReference;
import software.amazon.smithy.codegen.core.SymbolWriter;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.traits.DocumentationTrait;
import software.amazon.smithy.model.traits.StringTrait;
import software.amazon.smithy.utils.Pair;
import software.amazon.smithy.utils.SmithyUnstableApi;
import software.amazon.smithy.utils.StringUtils;

/**
 * Writes documentation in <a href="https://spec.commonmark.org">CommonMark</a> format.
 */
@SmithyUnstableApi
public class MarkdownWriter extends DocWriter {

    /**
     * Constructs a MarkdownWriter.
     *
     * @param importContainer this file's import container.
     * @param filename The full path to the file being written to.
     */
    public MarkdownWriter(DocImportContainer importContainer, String filename) {
        super(importContainer, filename);
    }

    /**
     * Constructs a MarkdownWriter.
     *
     * @param filename The full path to the file being written to.
     */
    public MarkdownWriter(String filename) {
        this(new DocImportContainer(), filename);
    }

    /**
     * Factory to construct {@code MarkdownWriter}s.
     */
    public static final class Factory implements SymbolWriter.Factory<DocWriter> {
        @Override
        public DocWriter apply(String filename, String namespace) {
            return new MarkdownWriter(filename);
        }
    }

    @Override
    String referenceFormatter(Object value) {
        var reference = getReferencePair(value);
        if (reference.getRight().isPresent()) {
            return String.format("[%s](%s)", reference.getLeft(), reference.getRight().get());
        } else {
            return reference.getLeft();
        }
    }

    private Pair<String, Optional<String>> getReferencePair(Object value) {
        String text;
        Optional<String> ref;
        var relativeTo = Paths.get(filename);
        if (value instanceof Symbol symbolValue) {
            text = symbolValue.getName();
            ref = getSymbolLink(symbolValue, relativeTo);
        } else if (value instanceof SymbolReference referenceValue) {
            text = referenceValue.getAlias();
            ref = getSymbolLink(referenceValue.getSymbol(), relativeTo);
        } else if (value instanceof Pair pairValue) {
            if (pairValue.getLeft() instanceof String left && pairValue.getRight() instanceof String right) {
                text = left;
                ref = Optional.of(right);
            } else {
                throw new CodegenException(
                        "Invalid type provided to $R. Expected both key and vale of the Pair to be Strings, but "
                        + "found " + value.getClass()
                );
            }
        } else {
            throw new CodegenException(
                    "Invalid type provided to $R. Expected a Symbol, SymbolReference, or Pair<String, String>, but "
                    + "found " + value.getClass()
            );
        }
        return Pair.of(text, ref);
    }

    @Override
    public DocWriter writeShapeDocs(Shape shape, Model model) {
        Optional<DocumentationTrait> docTrait;
        if (shape.isMemberShape()) {
            docTrait = shape.asMemberShape().get().getMemberTrait(model, DocumentationTrait.class);
        } else {
            docTrait = shape.getTrait(DocumentationTrait.class);
        }
        var documentation = docTrait.map(StringTrait::getValue)
                .orElse("Placeholder documentation for `" + shape.getId() + "`");
        writeWithNewline(documentation.replace("$", "$$"));
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
    public DocWriter openMemberListing() {
        return this;
    }

    @Override
    public DocWriter closeMemberListing() {
        return this;
    }

    @Override
    public DocWriter openMemberEntry(Symbol memberSymbol, Consumer<DocWriter> writeType) {
        writeInline("- **$L** (*$C*): ", memberSymbol.getName(), writeType);
        return this;
    }

    @Override
    public DocWriter closeMemberEntry() {
        return this;
    }

    @Override
    public DocWriter writeAnchor(String linkId) {
        // Anchors have no meaning in base markdown
        return this;
    }

    @Override
    public String toString() {
        // Ensure there's exactly one trailing newline
        return super.toString().stripTrailing() + "\n";
    }
}
