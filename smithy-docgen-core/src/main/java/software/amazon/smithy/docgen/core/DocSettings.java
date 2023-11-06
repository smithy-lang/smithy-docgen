/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.smithy.docgen.core;

import java.util.Objects;
import software.amazon.smithy.model.node.Node;
import software.amazon.smithy.model.node.ObjectNode;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.utils.SmithyUnstableApi;

/**
 * Settings for documentation generation. These can be set in the
 * {@code smithy-build.json} configuration for this plugin.
 *
 * @param service The shape id of the service to generate documentation for.
 * @param format The format to generate documentation in. The default is markdown.
 * @param snippetGeneratorSettings Settings to pass along to snippet generators. By
 *        default, the settings for the plugin in the current projection will be used,
 *        if available.
 */
@SmithyUnstableApi
public record DocSettings(ShapeId service, String format, ObjectNode snippetGeneratorSettings) {

    /**
     * Settings for documentation generation. These can be set in the
     * {@code smithy-build.json} configuration for this plugin.
     *
     * @param service The shape id of the service to generate documentation for.
     * @param format The format to generate documentation in. The default is markdown.
     */
    public DocSettings {
        Objects.requireNonNull(service);
        Objects.requireNonNull(format);
        Objects.requireNonNull(snippetGeneratorSettings);
    }

    /**
     * Load the settings from an {@code ObjectNode}.
     *
     * @param pluginSettings the {@code ObjectNode} to load settings from.
     * @return loaded settings based on the given node.
     */
    public static DocSettings fromNode(ObjectNode pluginSettings) {
        return new DocSettings(
            pluginSettings.expectStringMember("service").expectShapeId(),
            pluginSettings.getStringMemberOrDefault("format", "sphinx-markdown"),
            pluginSettings.getObjectMember("snippetGeneratorSettings").orElse(Node.objectNode())
        );
    }
}
