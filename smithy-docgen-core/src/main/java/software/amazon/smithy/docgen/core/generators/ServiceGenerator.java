/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.smithy.docgen.core.generators;

import java.util.function.Consumer;
import software.amazon.smithy.codegen.core.directed.GenerateServiceDirective;
import software.amazon.smithy.docgen.core.DocGenerationContext;
import software.amazon.smithy.docgen.core.DocSettings;
import software.amazon.smithy.model.shapes.ServiceShape;
import software.amazon.smithy.model.traits.TitleTrait;

public final class ServiceGenerator
        implements Consumer<GenerateServiceDirective<DocGenerationContext, DocSettings>> {

    @Override
    public void accept(GenerateServiceDirective<DocGenerationContext, DocSettings> directive) {
        ServiceShape serviceShape = directive.service();
        var serviceSymbol = directive.symbolProvider().toSymbol(serviceShape);

        directive.context().writerDelegator().useShapeWriter(serviceShape, writer -> {
            writer.openHeading(serviceSymbol.getName());
            writer.writeShapeDocs(serviceShape);

            writer.closeHeading();
        });
    }

}
