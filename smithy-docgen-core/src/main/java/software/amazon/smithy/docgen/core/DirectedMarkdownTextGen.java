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

import java.util.ArrayList;
import java.util.List;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.codegen.core.directed.CreateContextDirective;
import software.amazon.smithy.codegen.core.directed.CreateSymbolProviderDirective;
import software.amazon.smithy.codegen.core.directed.DirectedCodegen;
import software.amazon.smithy.codegen.core.directed.GenerateEnumDirective;
import software.amazon.smithy.codegen.core.directed.GenerateErrorDirective;
import software.amazon.smithy.codegen.core.directed.GenerateIntEnumDirective;
import software.amazon.smithy.codegen.core.directed.GenerateResourceDirective;
import software.amazon.smithy.codegen.core.directed.GenerateServiceDirective;
import software.amazon.smithy.codegen.core.directed.GenerateStructureDirective;
import software.amazon.smithy.codegen.core.directed.GenerateUnionDirective;
import software.amazon.smithy.model.Model;

public class DirectedMarkdownTextGen
        implements DirectedCodegen<DocgenGenerationContext, DocgenSettings, DocgenIntegration> {

    @Override
    public SymbolProvider createSymbolProvider(CreateSymbolProviderDirective<DocgenSettings> directive) {
        return new DocgenSymbolProvider(directive.model(), directive.settings());
    }

    @Override
    public DocgenGenerationContext createContext(CreateContextDirective<DocgenSettings, DocgenIntegration> directive) {
        Model model = directive.model();
        DocgenSettings docgenSettings = directive.settings();
        List<DocgenIntegration> docgenIntegrations = new ArrayList<>(directive.integrations());

        return new DocgenGenerationContext(
                model,
                docgenSettings,
                directive.symbolProvider(),
                directive.fileManifest(),
                docgenIntegrations);
    }

    @Override
    public void generateService(GenerateServiceDirective<DocgenGenerationContext, DocgenSettings> directive) {
        // no-op for now
    }

    @Override
    public void generateStructure(GenerateStructureDirective<DocgenGenerationContext, DocgenSettings> directive) {
        // no-op for now
    }

    @Override
    public void generateError(GenerateErrorDirective<DocgenGenerationContext, DocgenSettings> directive) {
        // no-op for now
    }

    @Override
    public void generateUnion(GenerateUnionDirective<DocgenGenerationContext, DocgenSettings> directive) {
        // no-op for now
    }

    @Override
    public void generateEnumShape(GenerateEnumDirective<DocgenGenerationContext, DocgenSettings> directive) {
        // no-op for now
    }

    @Override
    public void generateIntEnumShape(GenerateIntEnumDirective<DocgenGenerationContext, DocgenSettings> directive) {
        // no-op for now
    }

    @Override
    public void generateResource(GenerateResourceDirective<DocgenGenerationContext, DocgenSettings> directive) {
        // no-op for now
    }
}
