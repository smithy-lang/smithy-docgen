/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.smithy.docgen.core.writers;

import software.amazon.smithy.codegen.core.ImportContainer;
import software.amazon.smithy.codegen.core.Symbol;

public class DocImportContainer implements ImportContainer {
    @Override
    public void importSymbol(Symbol symbol, String s) {
        // no-op
    }
}
