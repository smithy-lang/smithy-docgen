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

import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.ServiceShape;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.shapes.ShapeVisitor;

public class DocgenSymbolProvider extends ShapeVisitor.Default<Symbol> implements SymbolProvider {
    private final Model model;
    private final DocgenSettings docgenSettings;
    private final ServiceShape serviceShape;

    public DocgenSymbolProvider(Model model, DocgenSettings docgenSettings) {
        this.model = model;
        this.docgenSettings = docgenSettings;
        this.serviceShape = model.expectShape(docgenSettings.service(), ServiceShape.class);
    }

    @Override
    public Symbol toSymbol(Shape shape) {
        return shape.accept(this);
    }

    @Override
    public Symbol serviceShape(ServiceShape shape) {
        return getSymbolBuilder(shape)
                .definitionFile(getDefinitionFile(serviceShape, shape))
                .build();
    }

    private Symbol.Builder getSymbolBuilder(Shape shape) {
        return Symbol.builder()
                .name(getShapeName(serviceShape, shape))
                .putProperty("shape", shape)
                .putProperty("shapeType", shape.getType());
    }

    private static String getDefinitionFile(ServiceShape serviceShape, Shape shape) {
        return getDefinitionFile(getShapeName(serviceShape, shape) + ".md");
    }

    public static String getDefinitionFile(String filename) {
        return "sources/" + filename;
    }

    private static String getShapeName(ServiceShape serviceShape, Shape shape) {
        String name = shape.getId().getName(serviceShape);
        if (shape.getId().getMember().isPresent()) {
            name += "-" + shape.getId().getMember().get();
        }
        return name;
    }

    // All other shapes don't get generation, so we'll do null checks where this might
    // have impact.
    @Override
    protected Symbol getDefault(Shape shape) {
        return null;
    }
}
