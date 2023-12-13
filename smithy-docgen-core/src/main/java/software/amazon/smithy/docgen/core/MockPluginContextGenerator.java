/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.smithy.docgen.core;

import java.util.Optional;
import software.amazon.smithy.build.MockManifest;
import software.amazon.smithy.build.PluginContext;
import software.amazon.smithy.build.model.ProjectionConfig;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.node.Node;
import software.amazon.smithy.model.node.ObjectNode;
import software.amazon.smithy.utils.SmithyUnstableApi;

/**
 * Generates mock plugin contexts to pass along to snippet generators.
 */
@SmithyUnstableApi
class MockPluginContextGenerator {
    private final PluginContext baseContext;
    private final ObjectNode pluginSettings;

    /**
     * Constructs a MockPluginContextGenerator.
     *
     * @param baseContext The context to base plugin context generation on.
     * @param pluginSettings Settings specifically given to pass to plugins via the doc generator.
     */
    MockPluginContextGenerator(PluginContext baseContext, ObjectNode pluginSettings) {
        if (baseContext.getProjection().isEmpty()) {
            // PluginContext will NPE if there's no projection config so you have to do it manually
            // until that gets fixed.
            var builder = PluginContext.builder()
                    .projection(baseContext.getProjectionName(), ProjectionConfig.builder().build())
                    .model(baseContext.getModel())
                    .originalModel(baseContext.getOriginalModel().orElse(baseContext.getModel()))
                    .events(baseContext.getEvents())
                    .settings(baseContext.getSettings())
                    .fileManifest(baseContext.getFileManifest())
                    .sources(baseContext.getSources());
            baseContext.getPluginClassLoader().ifPresent(builder::pluginClassLoader);
            baseContext.getOriginalModel().ifPresent(builder::originalModel);
            baseContext.getArtifactName().ifPresent(builder::artifactName);
            baseContext = builder.build();
        }
        this.baseContext = baseContext;
        this.pluginSettings = pluginSettings;
    }

    /**
     * Create a new plugin context with the given model and plugin name.
     *
     * @param model The version of the model to hand to the plugin.
     * @param pluginName The name of the plugin. This is used to search for existing config.
     * @return Returns a plugin context with the given model and config for the named plugin.
     */
    PluginContext getStubbedContext(Model model, String pluginName) {
        ObjectNode settings = pluginSettings.getObjectMember(pluginName)
                .or(() -> baseContext.getProjection()
                        .map(ProjectionConfig::getPlugins)
                        .flatMap(plugins -> Optional.ofNullable(plugins.get(pluginName))))
                .orElse(Node.objectNode());

        return baseContext.toBuilder()
                .fileManifest(new MockManifest())
                .artifactName(pluginName)
                .model(model)
                .settings(settings)
                .build();
    }
}
