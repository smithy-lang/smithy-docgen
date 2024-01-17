/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.smithy.docgen.core.interceptors;

import java.util.LinkedHashSet;
import java.util.Set;
import software.amazon.smithy.docgen.core.sections.ShapeDetailsSection;
import software.amazon.smithy.docgen.core.writers.DocWriter;
import software.amazon.smithy.docgen.core.writers.DocWriter.NoticeType;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.traits.ReferencesTrait;
import software.amazon.smithy.model.traits.ReferencesTrait.Reference;
import software.amazon.smithy.utils.CodeInterceptor;
import software.amazon.smithy.utils.SmithyInternalApi;

/**
 * Adds a "see also" to structures / operations that reference resources using
 * the <a href="https://smithy.io/2.0/spec/resource-traits.html#references-trait">references trait</a>.
 */
@SmithyInternalApi
public final class ReferencesInterceptor implements CodeInterceptor.Appender<ShapeDetailsSection, DocWriter> {
    @Override
    public Class<ShapeDetailsSection> sectionType() {
        return ShapeDetailsSection.class;
    }

    @Override
    public boolean isIntercepted(ShapeDetailsSection section) {
        var model = section.context().model();
        if (model.getResourceShapes().isEmpty()) {
            // We can only link to local references for now, so if the model doesn't have any
            // then we know we can't link.
            return false;
        }

        if (section.shape().isMemberShape()) {
            // Since the containing shape will show information about the reference, it's not
            // necessary to also show that on the members.
            return false;
        }

        return !getLocalReferences(section.context().model(), section.shape()).isEmpty();
    }

    @Override
    public void append(DocWriter writer, ShapeDetailsSection section) {
        var model = section.context().model();
        var symbolProvider = section.context().symbolProvider();
        var references = getLocalReferences(model, section.shape()).stream()
                .map(reference -> symbolProvider.toSymbol(model.expectShape(reference.getResource())))
                .toList();

        writer.pushState();
        writer.putContext("refs", references);
        writer.putContext("multipleRefs", references.size() > 1);
        writer.openAdmonition(NoticeType.INFO);
        writer.write("""
                This references \
                ${?multipleRefs}the following resources: ${/multipleRefs}\
                ${^multipleRefs}the resource ${/multipleRefs}\
                ${#refs}
                ${value:R}${^key.last}, ${/key.last}\
                ${/refs}
                .
                """);
        writer.closeAdmonition();
        writer.popState();
    }

    private Set<Reference> getLocalReferences(Model model, Shape shape) {
        var references = new LinkedHashSet<Reference>();
        if (shape.isOperationShape()) {
            var operation = shape.asOperationShape().get();
            references.addAll(getLocalReferences(model, model.expectShape(operation.getInputShape())));
            references.addAll(getLocalReferences(model, model.expectShape(operation.getInputShape())));
            return references;
        }
        for (var member : shape.members()) {
            references.addAll(getLocalReferences(model, member));
        }
        var shapeRefs = shape.getMemberTrait(model, ReferencesTrait.class);
        if (shapeRefs.isPresent()) {
            for (var reference : shapeRefs.get().getReferences()) {
                // We can only link to local references for now.
                if (model.getShape(reference.getResource()).isPresent()) {
                    references.add(reference);
                }
            }
        }
        return references;
    }
}
