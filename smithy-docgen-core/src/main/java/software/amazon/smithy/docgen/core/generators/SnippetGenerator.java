/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.smithy.docgen.core.generators;

import java.io.Serializable;
import java.util.Comparator;
import software.amazon.smithy.build.PluginContext;
import software.amazon.smithy.model.node.Node;
import software.amazon.smithy.model.shapes.OperationShape;
import software.amazon.smithy.model.shapes.ServiceShape;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.traits.ExamplesTrait.Example;
import software.amazon.smithy.utils.SmithyUnstableApi;

/**
 * Generates snippets for documentation.
 */
@SmithyUnstableApi
public interface SnippetGenerator {

    /**
     * Gets the name to use for the generator.
     *
     * <p>If this snippet generator is associated to a code generator, this must match
     * the name of that code generator plugin.
     *
     * @return the generator's name.
     */
    default String name() {
        return getClass().getCanonicalName();
    }

    /**
     *
     * @return returns whether the generator generates wire protocol snippets.
     */
    default boolean isWireProtocolGenerator() {
        return false;
    }

    /**
     * Configures the snippet generator.
     *
     * <p>If the generator can't be configured with the given configuration, or it
     * doesn't support the provided service, it should return {@code false}
     *
     * @param context A synthetic plugin context to configure the generator with.
     * @param service The service being generated for. This takes priority over any
     *                configuration present.
     * @return returns whether the generator is configurable.
     */
    default boolean configure(PluginContext context, ServiceShape service) {
        return true;
    }

    /**
     * @return returns the title to use in code tabs.
     */
    String tabTitle();

    /**
     * @return returns the language used to format code blocks.
     */
    String language();

    /**
     * Generates a snippet that instantiates a shape.
     *
     * @param shape The shape to instantiate.
     * @param value The value that the shape should be instantiated with.
     * @return returns a string that demonstrates instantiating the shape.
     */
    String generateShapeSnippet(Shape shape, Node value);

    /**
     * Generates a snippet for a given modeled example.
     *
     * @param operation The operation whose example is being generated.
     * @param example The example to generate.
     * @return returns a string representing the example.
     */
    String generateExampleSnippet(OperationShape operation, Example example);

    /**
     * Sorts snippet generators with wire protocol generators in front, and
     * then alphabetically by name.
     */
    final class SnippetComparator implements Comparator<SnippetGenerator>, Serializable {

        @Override
        public int compare(SnippetGenerator sg1, SnippetGenerator sg2) {
            if (sg1.isWireProtocolGenerator() && !sg2.isWireProtocolGenerator()) {
                return 1;
            } else if (!sg1.isWireProtocolGenerator() && sg2.isWireProtocolGenerator()) {
                return -1;
            }
            return String.CASE_INSENSITIVE_ORDER.compare(sg1.name(), sg2.name());
        }
    }
}
