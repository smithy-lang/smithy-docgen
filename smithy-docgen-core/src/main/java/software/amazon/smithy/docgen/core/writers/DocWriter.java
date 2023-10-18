/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.smithy.docgen.core.writers;

import software.amazon.smithy.codegen.core.SymbolWriter;
import software.amazon.smithy.model.shapes.Shape;

public abstract class DocWriter extends SymbolWriter<DocWriter, DocImportContainer> {
    public DocWriter(DocImportContainer importContainer) {
        super(importContainer);
    }

    public abstract DocWriter writeShapeDocs(Shape shape);

    public abstract DocWriter openHeading(String content);

    public abstract DocWriter closeHeading();
}
