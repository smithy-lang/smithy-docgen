/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.smithy.docgen.core.integrations;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import software.amazon.smithy.docgen.core.DocFormat;
import software.amazon.smithy.docgen.core.DocGenerationContext;
import software.amazon.smithy.docgen.core.DocIntegration;
import software.amazon.smithy.docgen.core.DocSettings;
import software.amazon.smithy.docgen.core.sections.sphinx.ConfSection;
import software.amazon.smithy.docgen.core.sections.sphinx.MakefileSection;
import software.amazon.smithy.docgen.core.sections.sphinx.WindowsMakeSection;
import software.amazon.smithy.docgen.core.writers.SphinxMarkdownWriter;
import software.amazon.smithy.model.shapes.ServiceShape;
import software.amazon.smithy.utils.SmithyInternalApi;

/**
 * Adds Sphinx project scaffolding for compatible formats.
 */
@SmithyInternalApi
public final class SphinxIntegration implements DocIntegration {
    private static final String MARKDOWN_FORMAT = "sphinx-markdown";
    private static final Set<String> FORMATS = Set.of(MARKDOWN_FORMAT);
    private static final Logger LOGGER = Logger.getLogger(SphinxIntegration.class.getName());

    @Override
    public List<DocFormat> docFormats(DocSettings settings) {
        return List.of(
            new DocFormat(MARKDOWN_FORMAT, ".md", new SphinxMarkdownWriter.Factory())
        );
    }

    @Override
    public void customize(DocGenerationContext context) {
        if (!FORMATS.contains(context.docFormat().name())) {
            LOGGER.finest(String.format(
                "Format %s is not a Sphinx-compatible format, skipping Sphinx project setup.",
                context.docFormat().name()
            ));
            return;
        }
        // TODO: add some way to disable project file generation
        LOGGER.finest("Generating Sphinx project files.");
        writeConf(context);
        writeMakefile(context);
    }

    private void writeConf(DocGenerationContext context) {
        var service = context.model().expectShape(context.settings().service(), ServiceShape.class);
        var serviceSymbol = context.symbolProvider().toSymbol(service);

        context.writerDelegator().useFileWriter("content/conf.py", writer -> {
            writer.pushState(new ConfSection(context));
            writer.write("""
                # Configuration file for the Sphinx documentation builder.
                # For the full list of built-in configuration values, see the documentation:
                # https://www.sphinx-doc.org/en/master/usage/configuration.html
                project = $1S
                version = $2S
                release = $2S
                templates_path = ["_templates"]
                html_static_path = ["_static"]
                html_theme = "alabaster"
                """,
                serviceSymbol.getName(),
                service.getVersion());

            if (context.docFormat().name().equals(MARKDOWN_FORMAT)) {
                writer.write("""
                extensions = ["myst_parser"]
                myst_enable_extensions = [
                    # Makes bare links into actual links
                    "linkify",

                    # Used to write directives that can be parsed by normal parsers
                    "colon_fence",
                ]
                """);

            }
            writer.popState();
        });
    }

    private void writeMakefile(DocGenerationContext context) {
        context.writerDelegator().useFileWriter("Makefile", writer -> {
            writer.pushState(new MakefileSection(context));
            writer.writeWithNoFormatting("""
                # Minimal makefile for Sphinx documentation
                # You can set these variables from the command line, and also
                # from the environment for the first two.
                SPHINXOPTS    ?=
                SPHINXBUILD   ?= sphinx-build
                SOURCEDIR     = content
                BUILDDIR      = build

                # Put it first so that "make" without argument is like "make help".
                help:
                \t@$(SPHINXBUILD) -M help "$(SOURCEDIR)" "$(BUILDDIR)" $(SPHINXOPTS) $(O)

                .PHONY: help Makefile

                # Catch-all target: route all unknown targets to Sphinx using the new
                # "make mode" option.  $(O) is meant as a shortcut for $(SPHINXOPTS).
                %: Makefile
                \t@$(SPHINXBUILD) -M $@ "$(SOURCEDIR)" "$(BUILDDIR)" $(SPHINXOPTS) $(O)
                """);
            writer.popState();
        });

        context.writerDelegator().useFileWriter("make.bat", writer -> {
            writer.pushState(new WindowsMakeSection(context));
            writer.write("""
                @ECHO OFF

                pushd %~dp0

                REM Command file for Sphinx documentation

                if "%SPHINXBUILD%" == "" (
                    set SPHINXBUILD=sphinx-build
                )
                set SOURCEDIR=content
                set BUILDDIR=build

                %SPHINXBUILD% >NUL 2>NUL
                if errorlevel 9009 (
                    echo.
                    echo.The 'sphinx-build' command was not found. Make sure you have Sphinx
                    echo.installed, then set the SPHINXBUILD environment variable to point
                    echo.to the full path of the 'sphinx-build' executable. Alternatively you
                    echo.may add the Sphinx directory to PATH.
                    echo.
                    echo.If you don't have Sphinx installed, grab it from
                    echo.https://www.sphinx-doc.org/
                    exit /b 1
                )

                if "%1" == "" goto help

                %SPHINXBUILD% -M %1 %SOURCEDIR% %BUILDDIR% %SPHINXOPTS% %O%
                goto end

                :help
                %SPHINXBUILD% -M help %SOURCEDIR% %BUILDDIR% %SPHINXOPTS% %O%

                :end
                popd
                """);
            writer.popState();
        });
    }
}
