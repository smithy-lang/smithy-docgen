/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.smithy.docgen.core.sections.sphinx;

import java.util.List;
import software.amazon.smithy.docgen.core.DocGenerationContext;
import software.amazon.smithy.utils.CodeSection;

/**
 * Generates a requirements file needed to install and run sphinx.
 *
 * <p>Any requirements added here will be installed in the environment used to
 * automatically build the docs with {@code sphinx-build}.
 *
 * @param context The context used to generate documentation.
 * @param requirements The requirements as a list of <a href="https://peps.python.org/pep-0508/">PEP 508</a> strings.
 */
public record RequirementsSection(DocGenerationContext context, List<String> requirements) implements CodeSection {
}