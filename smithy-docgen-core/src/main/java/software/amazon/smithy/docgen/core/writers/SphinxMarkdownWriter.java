/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.smithy.docgen.core.writers;

import java.util.function.Consumer;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.codegen.core.SymbolWriter;
import software.amazon.smithy.docgen.core.DocSymbolProvider;
import software.amazon.smithy.utils.SmithyUnstableApi;

/**
 * Writes documentation in <a href="https://spec.commonmark.org">CommonMark</a>-based
 * format for the <a href="https://www.sphinx-doc.org">Sphinx</a> doc build system.
 *
 * <p>The specific markdown parser being written for is
 * <a href="https://myst-parser.readthedocs.io/en/latest/index.html">MyST</a> with the
 * following <a href="https://myst-parser.readthedocs.io/en/latest/syntax/optional.html">
 * extensions</a> enabled: {@code linkify} and {@code colon_fence}
 */
@SmithyUnstableApi
public final class SphinxMarkdownWriter extends MarkdownWriter {
    /**
     * Factory to construct {@code SphinxMarkdownWriter}s.
     */
    public static final class Factory implements SymbolWriter.Factory<DocWriter> {
        @Override
        public DocWriter apply(String s, String s1) {
            return new SphinxMarkdownWriter();
        }
    }

    @Override
    public DocWriter openMemberEntry(Symbol memberSymbol, Consumer<DocWriter> writeType) {
        memberSymbol.getProperty(DocSymbolProvider.LINK_ID_PROPERTY, String.class).ifPresent(linkId -> {
            write("($L)=", linkId);
        });
        writeInline("""
                $L: $C
                :\s""", memberSymbol.getName(), writeType);
        indent();
        return this;
    }
}
