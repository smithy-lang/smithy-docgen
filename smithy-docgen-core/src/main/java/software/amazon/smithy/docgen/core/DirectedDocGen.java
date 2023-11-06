/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.smithy.docgen.core;

import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.codegen.core.directed.CreateContextDirective;
import software.amazon.smithy.codegen.core.directed.CreateSymbolProviderDirective;
import software.amazon.smithy.codegen.core.directed.DirectedCodegen;
import software.amazon.smithy.codegen.core.directed.GenerateEnumDirective;
import software.amazon.smithy.codegen.core.directed.GenerateErrorDirective;
import software.amazon.smithy.codegen.core.directed.GenerateIntEnumDirective;
import software.amazon.smithy.codegen.core.directed.GenerateOperationDirective;
import software.amazon.smithy.codegen.core.directed.GenerateResourceDirective;
import software.amazon.smithy.codegen.core.directed.GenerateServiceDirective;
import software.amazon.smithy.codegen.core.directed.GenerateStructureDirective;
import software.amazon.smithy.codegen.core.directed.GenerateUnionDirective;
import software.amazon.smithy.docgen.core.generators.ErrorGenerator;
import software.amazon.smithy.docgen.core.generators.OperationGenerator;
import software.amazon.smithy.docgen.core.generators.ServiceGenerator;
import software.amazon.smithy.docgen.core.generators.StructureGenerator;
import software.amazon.smithy.utils.SmithyUnstableApi;

/**
 * The main entry points for documentation generation.
 */
@SmithyUnstableApi
final class DirectedDocGen implements DirectedCodegen<DocGenerationContext, DocSettings, DocIntegration> {

    @Override
    public SymbolProvider createSymbolProvider(CreateSymbolProviderDirective<DocSettings> directive) {
        return new DocSymbolProvider(directive.model(), directive.settings());
    }

    @Override
    public DocGenerationContext createContext(CreateContextDirective<DocSettings, DocIntegration> directive) {
        return new DocGenerationContext(
            directive.model(),
            directive.settings(),
            directive.symbolProvider(),
            directive.fileManifest(),
            directive.integrations()
        );
    }

    @Override
    public void generateService(GenerateServiceDirective<DocGenerationContext, DocSettings> directive) {
        new ServiceGenerator().accept(directive);
    }

    @Override
    public void generateStructure(GenerateStructureDirective<DocGenerationContext, DocSettings> directive) {
        new StructureGenerator().accept(directive);
    }

    @Override
    public void generateOperation(GenerateOperationDirective<DocGenerationContext, DocSettings> directive) {
        new OperationGenerator().accept(directive);
    }

    @Override
    public void generateError(GenerateErrorDirective<DocGenerationContext, DocSettings> directive) {
        new ErrorGenerator().accept(directive);
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
