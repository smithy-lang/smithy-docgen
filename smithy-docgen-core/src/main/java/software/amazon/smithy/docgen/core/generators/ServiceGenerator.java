/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.smithy.docgen.core.generators;

import java.util.function.Consumer;
import software.amazon.smithy.codegen.core.directed.GenerateServiceDirective;
import software.amazon.smithy.docgen.core.DocGenerationContext;
import software.amazon.smithy.docgen.core.DocSettings;
import software.amazon.smithy.docgen.core.sections.ShapeDetailsSection;
import software.amazon.smithy.docgen.core.sections.ShapeSection;
import software.amazon.smithy.docgen.core.sections.ShapeSubheadingSection;
import software.amazon.smithy.model.knowledge.TopDownIndex;
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
 *     <li>{@link ShapeSection}: Enables re-writing or overwriting the entire page,
 *     including changes made in other sections.
 *
 *     <li>{@link ShapeSubheadingSection}: Enables adding additional details that are
 *     inserted right after the shape's heading, before modeled docs.
 *
 *     <li>{@link ShapeDetailsSection}: Enables adding in additional details that are
 *     inserted after the service's modeled documentation.
 *
 *     <li>{@link software.amazon.smithy.docgen.core.sections.BoundOperationsSection}:
 *     enables modifying the listing of operations transitively bound to the service,
 *     which includes operations bound to resources.
 *
 *     <li>{@link software.amazon.smithy.docgen.core.sections.BoundOperationSection}:
 *     enables modifying the listing of an individual operation transitively bound to
 *     the service.
 *
 *     <li>{@link software.amazon.smithy.docgen.core.sections.BoundResourcesSection}:
 *     enables modifying the listing of resources directly bound to the service.
 *
 *     <li>{@link software.amazon.smithy.docgen.core.sections.BoundResourceSection}:
 *     enables modifying the listing of an individual resource directly bound to
 *     the service.
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
 *
 * @see <a href="https://smithy.io/2.0/spec/service-types.html#service">
 *     Smithy service shape docs.</a>
 */
@SmithyInternalApi
public final class ServiceGenerator implements Consumer<GenerateServiceDirective<DocGenerationContext, DocSettings>> {

    @Override
    public void accept(GenerateServiceDirective<DocGenerationContext, DocSettings> directive) {
        var service = directive.service();
        var context = directive.context();
        var serviceSymbol = directive.symbolProvider().toSymbol(service);

        directive.context().writerDelegator().useShapeWriter(service, writer -> {
            writer.pushState(new ShapeSection(context, service));
            writer.openHeading(serviceSymbol.getName());
            writer.injectSection(new ShapeSubheadingSection(context, service));
            writer.writeShapeDocs(service, directive.model());
            writer.injectSection(new ShapeDetailsSection(context, service));

            var topDownIndex = TopDownIndex.of(context.model());

            // TODO: topographically sort resources
            var resources = topDownIndex.getContainedResources(service).stream().sorted().toList();
            ServiceShapeGeneratorUtils.generateResourceListing(context, writer, service, resources);

            var operations = topDownIndex.getContainedOperations(service).stream().sorted().toList();
            ServiceShapeGeneratorUtils.generateOperationListing(context, writer, service, operations);

            writer.closeHeading();
            writer.popState();
        });
    }

}
