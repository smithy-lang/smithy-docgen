/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.smithy.docgen.core.generators;

import software.amazon.smithy.model.node.Node;
import software.amazon.smithy.model.shapes.OperationShape;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.traits.ExamplesTrait.Example;

public class ExampleInputGenerator implements SnippetGenerator {
    @Override
    public String name() {
        return "input";
    }

    @Override
    public boolean isWireProtocolGenerator() {
        return true;
    }

    @Override
    public String tabTitle() {
        return "Input";
    }

    @Override
    public String language() {
        return "json";
    }

    @Override
    public String generateShapeSnippet(Shape shape, Node value) {
        return "";
    }

    @Override
    public String generateExampleSnippet(OperationShape operation, Example example) {
        return Node.prettyPrintJson(example.getInput());
    }
}
