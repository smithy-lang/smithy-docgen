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
 * Generates the entire Service detail page.
 *
 * <p>This enables overwriting the entire page. To simply add details after the
 * service's trait-defined documentation, instead intercept
 * {@link ServiceDetailsSection}.
 *
 * @param service The service shape being generated.
 * @param context The context used to generate documentation.
 */
@SmithyUnstableApi
public record ServiceSection(ServiceShape service, DocGenerationContext context) implements CodeSection {
}
