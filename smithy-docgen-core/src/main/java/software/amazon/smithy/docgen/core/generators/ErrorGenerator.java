/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.smithy.docgen.core.generators;

import java.util.function.Consumer;
import software.amazon.smithy.codegen.core.directed.GenerateErrorDirective;
import software.amazon.smithy.docgen.core.DocGenerationContext;
import software.amazon.smithy.docgen.core.DocSettings;
import software.amazon.smithy.docgen.core.DocSymbolProvider;
import software.amazon.smithy.docgen.core.generators.MemberGenerator.MemberListingType;
import software.amazon.smithy.docgen.core.sections.ShapeDetailsSection;
import software.amazon.smithy.docgen.core.sections.ShapeSection;
import software.amazon.smithy.docgen.core.sections.ShapeSubheadingSection;
import software.amazon.smithy.model.traits.ErrorTrait;

/**
 * Generates documentation for errors.
 *
 * <p>The output of this can be customized in a number of ways. To add details to
 * or re-write particular sections, register an interceptor with
 * {@link software.amazon.smithy.docgen.core.DocIntegration#interceptors}. The following
 * sections are guaranteed to be present:
 *
 * <ul>
 *     <li>{@link ShapeSubheadingSection}: Enables adding additional details that are
 *     inserted right after the shape's heading, before modeled docs.
 *
 *     <li>{@link ShapeDetailsSection}: Enables adding additional details that are inserted
 *     directly after the shape's modeled documentation.
 *
 *     <li>{@link ShapeSection}: Enables re-writing or overwriting the entire page,
 *     including changes made in other sections.
 * </ul>
 *
 * Additionally, if the error has members the following sections will also be present:
 *
 * <ul>
 *     <li>{@link software.amazon.smithy.docgen.core.sections.MemberSection}: enables
 *     modifying documentation for an individual error member.
 *
 *     <li>{@link software.amazon.smithy.docgen.core.sections.ShapeMembersSection}:
 *     enables modifying the documentation for all of the error's members.
 * </ul>
 *
 * <p>To change the intermediate format (e.g. from markdown to restructured text),
 * a new {@link software.amazon.smithy.docgen.core.DocFormat} needs to be introduced
 * via {@link software.amazon.smithy.docgen.core.DocIntegration#docFormats}.
 *
 * @see MemberGenerator for more details on how member documentation is generated.
 */
public class ErrorGenerator implements Consumer<GenerateErrorDirective<DocGenerationContext, DocSettings>> {
    @Override
    public void accept(GenerateErrorDirective<DocGenerationContext, DocSettings> directive) {
        var shape = directive.shape();
        var symbol = directive.symbolProvider().toSymbol(shape);
        directive.context().writerDelegator().useShapeWriter(shape, writer -> {
            writer.pushState(new ShapeSection(directive.context(), shape));
            symbol.getProperty(DocSymbolProvider.LINK_ID_PROPERTY, String.class).ifPresent(writer::writeAnchor);
            writer.openHeading(symbol.getName());

            writer.pushState(new ShapeSubheadingSection(directive.context(), shape));
            writer.write("This is an error caused by the $L.\n", shape.expectTrait(ErrorTrait.class).getValue());
            writer.popState();

            writer.writeShapeDocs(shape, directive.model());
            writer.injectSection(new ShapeDetailsSection(directive.context(), shape));

            new MemberGenerator(directive.context(), writer, shape, MemberListingType.MEMBERS).run();

            writer.closeHeading();
            writer.popState();
        });
    }
}
