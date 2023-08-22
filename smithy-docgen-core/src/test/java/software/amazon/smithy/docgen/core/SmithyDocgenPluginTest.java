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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import software.amazon.smithy.build.PluginContext;
import software.amazon.smithy.build.SmithyBuildPlugin;
import software.amazon.smithy.build.MockManifest;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.node.Node;
import software.amazon.smithy.utils.IoUtils;

public class SmithyDocgenPluginTest {

    @Test
    public void assertDocumentationFiles() {
        MockManifest manifest = new MockManifest();
        Model model = Model.assembler()
                .addImport(getClass().getResource("sample-service.smithy"))
                .discoverModels(getClass().getClassLoader())
                .assemble()
                .unwrap();
        PluginContext context = PluginContext.builder()
                .fileManifest(manifest)
                .model(model)
                .settings(Node.objectNodeBuilder()
                        .withMember("service", "smithy.example#SampleService")
                        .build())
                .build();

        SmithyBuildPlugin plugin = new SmithyDocgenPlugin();
        plugin.execute(context);

        assertFalse(manifest.getFiles().isEmpty());
        assertServicePageContents(manifest);
    }

    private void assertServicePageContents(MockManifest manifest) {
        String servicePage = manifest.expectFileString("/sources/SampleService.md");

        assertEquals(servicePage,
                IoUtils.readUtf8File(getClass().getResource("expected-outputs/SampleService.md").getPath()));
    }
}
