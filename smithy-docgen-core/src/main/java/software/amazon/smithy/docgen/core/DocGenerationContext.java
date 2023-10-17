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

import java.util.List;
import software.amazon.smithy.build.FileManifest;
import software.amazon.smithy.codegen.core.CodegenContext;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.codegen.core.WriterDelegator;
import software.amazon.smithy.docgen.core.writers.DocWriter;
import software.amazon.smithy.docgen.core.writers.MarkdownWriter;
import software.amazon.smithy.model.Model;

public final class DocGenerationContext
        implements CodegenContext<DocSettings, DocWriter, DocIntegration> {
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
        this.writerDelegator = new WriterDelegator<>(fileManifest, symbolProvider,
                new MarkdownWriter.Factory());
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
