/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.smithy.docgen.core;

import java.util.Objects;
import software.amazon.smithy.model.node.ObjectNode;
import software.amazon.smithy.model.shapes.ShapeId;

public record DocSettings(ShapeId service) {

    public DocSettings {
        Objects.requireNonNull(service);
    }

    public static DocSettings from(ObjectNode pluginSettings) {
        return new DocSettings(pluginSettings.expectStringMember("service").expectShapeId());
    }
}
