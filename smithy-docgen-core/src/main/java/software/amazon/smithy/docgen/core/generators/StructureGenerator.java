/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.smithy.docgen.core.generators;

import java.util.function.Consumer;
import software.amazon.smithy.codegen.core.directed.GenerateStructureDirective;
import software.amazon.smithy.docgen.core.DocGenerationContext;
import software.amazon.smithy.docgen.core.DocSettings;
import software.amazon.smithy.docgen.core.DocSymbolProvider;
import software.amazon.smithy.docgen.core.generators.MemberGenerator.MemberListingType;
import software.amazon.smithy.docgen.core.sections.ShapeDetailsSection;
import software.amazon.smithy.docgen.core.sections.ShapeSection;
import software.amazon.smithy.model.shapes.StructureShape;
import software.amazon.smithy.model.traits.InputTrait;
import software.amazon.smithy.model.traits.OutputTrait;
import software.amazon.smithy.utils.SmithyInternalApi;

/**
 * Generates documentation for structures.
 *
 * <p>The output of this can be customized in a number of ways. To add details to
 * or re-write particular sections, register an interceptor with
 * {@link software.amazon.smithy.docgen.core.DocIntegration#interceptors}. The following
 * sections are guaranteed to be present:
 *
 * <ul>
 *     <li>{@link ShapeDetailsSection}: Enables adding additional details that are inserted
 *     directly after the shape's modeled documentation.
 *     <li>{@link ShapeSection}: Enables re-writing or overwriting the entire page,
 *     including changes made in other sections.
 * </ul>
 *
 * Additionally, if the structure has members the following sections will also be present:
 *
 * <ul>
 *     <li>{@link software.amazon.smithy.docgen.core.sections.MemberSection}: enables
 *     modifying documentation for an individual structure member.
 *     <li>{@link software.amazon.smithy.docgen.core.sections.ShapeMembersSection}:
 *     enables modifying the documentation for all of the structure's members.
 * </ul>
 *
 * <p>To change the intermediate format (e.g. from markdown to restructured text),
 * a new {@link software.amazon.smithy.docgen.core.DocFormat} needs to be introduced
 * via {@link software.amazon.smithy.docgen.core.DocIntegration#docFormats}.
 *
 * @see MemberGenerator for more details on how member documentation is generated.
 */
@SmithyInternalApi
public final class StructureGenerator
    implements Consumer<GenerateStructureDirective<DocGenerationContext, DocSettings>> {

    @Override
    public void accept(GenerateStructureDirective<DocGenerationContext, DocSettings> directive) {
        StructureShape shape = directive.shape();

        // Input and output structures are documented alongside the relevant operations.
        if (shape.hasTrait(InputTrait.class) || shape.hasTrait(OutputTrait.class)) {
            return;
        }

        var symbol = directive.symbolProvider().toSymbol(shape);
        directive.context().writerDelegator().useShapeWriter(shape, writer -> {
            writer.pushState(new ShapeSection(directive.context(), shape));

            symbol.getProperty(DocSymbolProvider.LINK_ID_PROPERTY, String.class).ifPresent(writer::writeAnchor);
            writer.openHeading(symbol.getName());

            writer.writeShapeDocs(shape, directive.model());
            writer.injectSection(new ShapeDetailsSection(directive.context(), shape));

            new MemberGenerator(directive.context(), writer, shape, MemberListingType.MEMBERS).run();

            writer.closeHeading();
            writer.popState();
        });
    }
}
