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
import software.amazon.smithy.docgen.core.generators.ServiceGenerator;
import software.amazon.smithy.model.Model;

public class DirectedDocGen
        implements DirectedCodegen<DocGenerationContext, DocSettings, DocIntegration> {

    @Override
    public SymbolProvider createSymbolProvider(CreateSymbolProviderDirective<DocSettings> directive) {
        return new DocSymbolProvider(directive.model(), directive.settings());
    }

    @Override
    public DocGenerationContext createContext(CreateContextDirective<DocSettings, DocIntegration> directive) {
        Model model = directive.model();
        DocSettings docSettings = directive.settings();
        List<DocIntegration> docIntegrations = new ArrayList<>(directive.integrations());

        return new DocGenerationContext(
                model,
            docSettings,
                directive.symbolProvider(),
                directive.fileManifest(),
            docIntegrations);
    }

    @Override
    public void generateService(GenerateServiceDirective<DocGenerationContext, DocSettings> directive) {
        new ServiceGenerator().accept(directive);
    }

    @Override
    public void generateStructure(GenerateStructureDirective<DocGenerationContext, DocSettings> directive) {
        // no-op for now
    }

    @Override
    public void generateError(GenerateErrorDirective<DocGenerationContext, DocSettings> directive) {
        // no-op for now
    }

    @Override
    public void generateUnion(GenerateUnionDirective<DocGenerationContext, DocSettings> directive) {
        // no-op for now
    }

    @Override
    public void generateEnumShape(GenerateEnumDirective<DocGenerationContext, DocSettings> directive) {
        // no-op for now
    }

    @Override
    public void generateIntEnumShape(GenerateIntEnumDirective<DocGenerationContext, DocSettings> directive) {
        // no-op for now
    }

    @Override
    public void generateResource(GenerateResourceDirective<DocGenerationContext, DocSettings> directive) {
        // no-op for now
    }
}
