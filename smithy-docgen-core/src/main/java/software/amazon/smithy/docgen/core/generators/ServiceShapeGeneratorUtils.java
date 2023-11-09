/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.smithy.docgen.core.generators;

import java.util.List;
import java.util.Locale;
import software.amazon.smithy.docgen.core.DocGenerationContext;
import software.amazon.smithy.docgen.core.DocSymbolProvider;
import software.amazon.smithy.docgen.core.sections.BoundOperationSection;
import software.amazon.smithy.docgen.core.sections.BoundOperationsSection;
import software.amazon.smithy.docgen.core.sections.BoundResourceSection;
import software.amazon.smithy.docgen.core.sections.BoundResourcesSection;
import software.amazon.smithy.docgen.core.writers.DocWriter;
import software.amazon.smithy.docgen.core.writers.DocWriter.ListType;
import software.amazon.smithy.model.shapes.EntityShape;
import software.amazon.smithy.model.shapes.OperationShape;
import software.amazon.smithy.model.shapes.ResourceShape;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.utils.SmithyInternalApi;

/**
 * Provides common generation methods for services and resources.
 */
@SmithyInternalApi
final class ServiceShapeGeneratorUtils {
    private ServiceShapeGeneratorUtils() {}

    static void generateOperationListing(
            DocGenerationContext context,
            DocWriter writer,
            EntityShape shape,
            List<OperationShape> operations
    ) {
        writer.pushState(new BoundOperationsSection(context, shape, operations));

        if (operations.isEmpty()) {
            writer.popState();
            return;
        }

        var parentLinkId = context.symbolProvider().toSymbol(shape)
                .expectProperty(DocSymbolProvider.LINK_ID_PROPERTY, String.class);
        writer.openHeading("Operations", parentLinkId + "-operations");
        writer.openList(ListType.UNORDERED);

        for (var operation : operations) {
            writer.pushState(new BoundOperationSection(context, shape, operation));
            writeListingElement(context, writer, operation);
            writer.popState();
        }

        writer.closeList(ListType.UNORDERED);
        writer.closeHeading();
        writer.popState();
    }

    static void generateResourceListing(
            DocGenerationContext context,
            DocWriter writer,
            EntityShape shape,
            List<ResourceShape> resources
    ) {
        writer.pushState(new BoundResourcesSection(context, shape, resources));

        if (resources.isEmpty()) {
            writer.popState();
            return;
        }

        var parentLinkId = context.symbolProvider().toSymbol(shape)
                .expectProperty(DocSymbolProvider.LINK_ID_PROPERTY, String.class);
        var heading = shape.isServiceShape() ? "Resources" : "Sub-Resources";
        writer.openHeading(heading, parentLinkId + "-" + heading.toLowerCase(Locale.ENGLISH));
        writer.openList(ListType.UNORDERED);

        for (var resource : resources) {
            writer.pushState(new BoundResourceSection(context, shape, resource));
            writeListingElement(context, writer, resource);
            writer.popState();
        }

        writer.closeList(ListType.UNORDERED);
        writer.closeHeading();
        writer.popState();
    }

    private static void writeListingElement(DocGenerationContext context, DocWriter writer, Shape shape) {
        writer.openListItem(ListType.UNORDERED);
        var symbol = context.symbolProvider().toSymbol(shape);
        writer.writeInline("$R: ", symbol).writeShapeDocs(shape, context.model());
        writer.closeListItem(ListType.UNORDERED);
    }
}
