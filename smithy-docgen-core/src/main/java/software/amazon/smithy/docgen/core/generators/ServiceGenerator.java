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

        directive.context().writerDelegator().useShapeWriter(serviceShape, writer -> {
            writer.openHeader(serviceShape.expectTrait(TitleTrait.class).getValue());
            writer.writeShapeDocs(serviceShape);

            writer.closeHeader();
        });
    }

}
