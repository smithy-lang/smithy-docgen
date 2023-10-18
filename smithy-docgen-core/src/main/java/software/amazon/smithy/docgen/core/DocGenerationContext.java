/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.smithy.docgen.core;

import java.util.List;
import software.amazon.smithy.build.FileManifest;
import software.amazon.smithy.codegen.core.CodegenContext;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.codegen.core.WriterDelegator;
import software.amazon.smithy.docgen.core.writers.DocWriter;
import software.amazon.smithy.docgen.core.writers.MarkdownWriter;
import software.amazon.smithy.model.Model;

public final class DocGenerationContext implements CodegenContext<DocSettings, DocWriter, DocIntegration> {
    private final Model model;
    private final DocSettings docSettings;
    private final SymbolProvider symbolProvider;
    private final FileManifest fileManifest;
    private final WriterDelegator<DocWriter> writerDelegator;
    private final List<DocIntegration> docIntegrations;

    public DocGenerationContext(
            Model model,
            DocSettings docSettings,
            SymbolProvider symbolProvider,
            FileManifest fileManifest,
            List<DocIntegration> docIntegrations
    ) {
        this.model = model;
        this.docSettings = docSettings;
        this.symbolProvider = symbolProvider;
        this.fileManifest = fileManifest;
        // TODO: pull the factory from the integrations
        this.writerDelegator = new WriterDelegator<>(fileManifest, symbolProvider, new MarkdownWriter.Factory());
        this.docIntegrations = docIntegrations;
    }

    @Override
    public Model model() {
        return model;
    }

    @Override
    public DocSettings settings() {
        return docSettings;
    }

    @Override
    public SymbolProvider symbolProvider() {
        return symbolProvider;
    }

    @Override
    public FileManifest fileManifest() {
        return fileManifest;
    }

    @Override
    public WriterDelegator<DocWriter> writerDelegator() {
        return writerDelegator;
    }

    @Override
    public List<DocIntegration> integrations() {
        return docIntegrations;
    }
}
