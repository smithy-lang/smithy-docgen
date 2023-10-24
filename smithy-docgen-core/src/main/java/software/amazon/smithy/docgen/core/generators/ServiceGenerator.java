/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.smithy.docgen.core.generators;

import java.util.function.Consumer;
import software.amazon.smithy.codegen.core.directed.GenerateServiceDirective;
import software.amazon.smithy.docgen.core.DocGenerationContext;
import software.amazon.smithy.docgen.core.DocSettings;
import software.amazon.smithy.docgen.core.sections.ServiceDetailsSection;
import software.amazon.smithy.docgen.core.sections.ServiceSection;
import software.amazon.smithy.model.shapes.ServiceShape;
import software.amazon.smithy.utils.SmithyInternalApi;

/**
 * Generates top-level documentation for the service.
 *
 * <p>The output of this can be customized in a number of ways. To add details to
 * or re-write particular sections, register an interceptor with
 * {@link software.amazon.smithy.docgen.core.DocIntegration#interceptors}. The following
 * sections are guaranteed to be present:
 *
 * <ul>
 *     <li>{@link ServiceDetailsSection}: Enables adding in additional details that are
 *     inserted after the service's modeled documentation.
 *     <li>{@link ServiceSection}: Enables re-writing or overwriting the entire page,
 *     including changes made in other sections.
 * </ul>
 *
 * <p>To change the intermediate format (e.g. from markdown to restructured text),
 * a new {@link software.amazon.smithy.docgen.core.DocFormat} needs to be introduced
 * via {@link software.amazon.smithy.docgen.core.DocIntegration#docFormats}.
 *
 * <p>To change the filename or title, implement
 * {@link software.amazon.smithy.docgen.core.DocIntegration#decorateSymbolProvider}
 * and modify the generated symbol's definition file. See
 * {@link software.amazon.smithy.docgen.core.DocSymbolProvider} for details on other
 * symbol-driven configuration options.
 */
@SmithyInternalApi
public final class ServiceGenerator implements Consumer<GenerateServiceDirective<DocGenerationContext, DocSettings>> {

    @Override
    public void accept(GenerateServiceDirective<DocGenerationContext, DocSettings> directive) {
        ServiceShape serviceShape = directive.service();
        var serviceSymbol = directive.symbolProvider().toSymbol(serviceShape);

        directive.context().writerDelegator().useShapeWriter(serviceShape, writer -> {
            writer.pushState(new ServiceSection(directive.service(), directive.context()));
            writer.openHeading(serviceSymbol.getName());
            writer.writeShapeDocs(serviceShape);
            writer.injectSection(new ServiceDetailsSection(directive.service(), directive.context()));
            writer.closeHeading();
            writer.popState();
        });
    }

}
