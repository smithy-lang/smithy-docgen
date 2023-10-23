/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.smithy.docgen.core.sections;

import software.amazon.smithy.docgen.core.DocGenerationContext;
import software.amazon.smithy.model.shapes.ServiceShape;
import software.amazon.smithy.utils.CodeSection;
import software.amazon.smithy.utils.SmithyUnstableApi;

/**
 * Injects details after the model-defined service documentation.
 *
 * <p>To overwrite the whole service page, instead intercept the {@link ServiceSection}.
 *
 * @param service The service shape being generated.
 * @param context The context used to generate documentation.
 */
@SmithyUnstableApi
public record ServiceDetailsSection(ServiceShape service, DocGenerationContext context) implements CodeSection {
}
