/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.smithy.docgen.core;


import java.util.logging.Logger;
import software.amazon.smithy.build.PluginContext;
import software.amazon.smithy.build.SmithyBuildPlugin;
import software.amazon.smithy.codegen.core.directed.CodegenDirector;
import software.amazon.smithy.docgen.core.writers.DocWriter;

/**
 * Generates API documentation from a Smithy model.
 */
public final class SmithyDocPlugin implements SmithyBuildPlugin {

    private static final Logger LOGGER = Logger.getLogger(SmithyDocPlugin.class.getName());

    @Override
    public String getName() {
        return "docgen";
    }

    @Override
    public void execute(PluginContext pluginContext) {
        LOGGER.fine("Beginning documentation generation.");
        DocSettings docSettings = DocSettings.from(pluginContext.getSettings());

        CodegenDirector<DocWriter, DocIntegration, DocGenerationContext, DocSettings> runner
                = new CodegenDirector<>();

        runner.directedCodegen(new DirectedDocGen());
        runner.integrationClass(DocIntegration.class);
        runner.fileManifest(pluginContext.getFileManifest());
        runner.model(pluginContext.getModel());
        runner.settings(docSettings);
        runner.service(docSettings.service());
        runner.performDefaultCodegenTransforms();
        runner.run();
        LOGGER.fine("Finished documentation generation.");
    }
}
