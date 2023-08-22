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
import software.amazon.smithy.model.Model;

public final class DocgenGenerationContext
        implements CodegenContext<DocgenSettings, MarkdownTextWriter, DocgenIntegration> {
    private final Model model;
    private final DocgenSettings docgenSettings;
    private final SymbolProvider symbolProvider;
    private final FileManifest fileManifest;
    private final WriterDelegator<MarkdownTextWriter> writerDelegator;
    private final List<DocgenIntegration> docgenIntegrations;

    public DocgenGenerationContext(
            Model model,
            DocgenSettings docgenSettings,
            SymbolProvider symbolProvider,
            FileManifest fileManifest,
            List<DocgenIntegration> docgenIntegrations
    ) {
        this.model = model;
        this.docgenSettings = docgenSettings;
        this.symbolProvider = symbolProvider;
        this.fileManifest = fileManifest;
        this.writerDelegator = new WriterDelegator<>(fileManifest, symbolProvider,
                new MarkdownTextWriter.Factory());
        this.docgenIntegrations = docgenIntegrations;
    }

    @Override
    public Model model() {
        return model;
    }

    @Override
    public DocgenSettings settings() {
        return docgenSettings;
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
    public WriterDelegator<MarkdownTextWriter> writerDelegator() {
        return writerDelegator;
    }

    @Override
    public List<DocgenIntegration> integrations() {
        return docgenIntegrations;
    }
}
