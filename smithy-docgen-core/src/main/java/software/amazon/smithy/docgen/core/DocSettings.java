/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.smithy.docgen.core;

import java.util.Objects;
import software.amazon.smithy.model.node.ObjectNode;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.utils.SmithyUnstableApi;

/**
 * Settings for documentation generation. These can be set in the
 * {@code smithy-build.json} configuration for this plugin.
 *
 * @param service The shape id of the service to generate documentation for.
 */
@SmithyUnstableApi
public record DocSettings(ShapeId service) {

    /**
     * Settings for documentation generation. These can be set in the
     * {@code smithy-build.json} configuration for this plugin.
     *
     * @param service The shape id of the service to generate documentation for.
     */
    public DocSettings {
        Objects.requireNonNull(service);
    }

    /**
     * Load the settings from an {@code ObjectNode}.
     *
     * @param pluginSettings the {@code ObjectNode} to load settings from.
     * @return loaded settings based on the given node.
     */
    public static DocSettings from(ObjectNode pluginSettings) {
        return new DocSettings(pluginSettings.expectStringMember("service").expectShapeId());
    }
}
