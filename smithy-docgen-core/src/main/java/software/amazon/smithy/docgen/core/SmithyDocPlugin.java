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

import software.amazon.smithy.build.PluginContext;
import software.amazon.smithy.build.SmithyBuildPlugin;
import software.amazon.smithy.codegen.core.directed.CodegenDirector;
import software.amazon.smithy.docgen.core.writers.DocWriter;

/**
 * Generates API documentation from a Smithy model.
 */
public final class SmithyDocPlugin implements SmithyBuildPlugin {

    @Override
    public String getName() {
        return "docgen";
    }

    @Override
    public void execute(PluginContext pluginContext) {
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
    }
}
