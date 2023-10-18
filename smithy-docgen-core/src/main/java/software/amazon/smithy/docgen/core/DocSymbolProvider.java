/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.smithy.docgen.core;

import static java.lang.String.format;

import java.util.logging.Logger;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.ServiceShape;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.shapes.ShapeVisitor;

public class DocSymbolProvider extends ShapeVisitor.Default<Symbol> implements SymbolProvider {

    private static final Logger LOGGER = Logger.getLogger(DocSymbolProvider.class.getName());

    private final Model model;
    private final DocSettings docSettings;
    private final ServiceShape serviceShape;

    public DocSymbolProvider(Model model, DocSettings docSettings) {
        this.model = model;
        this.docSettings = docSettings;
        this.serviceShape = model.expectShape(docSettings.service(), ServiceShape.class);
    }

    @Override
    public Symbol toSymbol(Shape shape) {
        var symbol = shape.accept(this);
        LOGGER.fine(() -> format("Creating symbol from %s: %s", shape, symbol));
        return symbol;
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
                .putProperty("shape", shape);
    }

    private static String getDefinitionFile(ServiceShape serviceShape, Shape shape) {
        return getDefinitionFile(getShapeName(serviceShape, shape) + ".md");
    }

    public static String getDefinitionFile(String filename) {
        return "content/" + filename;
    }

    private static String getShapeName(ServiceShape serviceShape, Shape shape) {
        return shape.getId().getName(serviceShape);
    }

    // All other shapes don't get generation, so we'll do null checks where this might
    // have impact.
    @Override
    protected Symbol getDefault(Shape shape) {
        return null;
    }
}
