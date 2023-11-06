/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.smithy.docgen.core.integrations;

import static java.lang.String.format;
import static software.amazon.smithy.docgen.core.DocgenUtils.normalizeNewlines;
import static software.amazon.smithy.docgen.core.DocgenUtils.runCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import software.amazon.smithy.codegen.core.CodegenException;
import software.amazon.smithy.docgen.core.DocFormat;
import software.amazon.smithy.docgen.core.DocGenerationContext;
import software.amazon.smithy.docgen.core.DocIntegration;
import software.amazon.smithy.docgen.core.DocSettings;
import software.amazon.smithy.docgen.core.sections.sphinx.ConfSection;
import software.amazon.smithy.docgen.core.sections.sphinx.MakefileSection;
import software.amazon.smithy.docgen.core.sections.sphinx.RequirementsSection;
import software.amazon.smithy.docgen.core.sections.sphinx.WindowsMakeSection;
import software.amazon.smithy.docgen.core.writers.SphinxMarkdownWriter;
import software.amazon.smithy.model.node.Node;
import software.amazon.smithy.model.node.ObjectNode;
import software.amazon.smithy.model.node.StringNode;
import software.amazon.smithy.model.shapes.ServiceShape;
import software.amazon.smithy.utils.SmithyInternalApi;

/**
 * Adds Sphinx project scaffolding for compatible formats.
 *
 * <p>This integration runs in low priority to allow other integrations to generate
 * files that will be picked up by sphinx-build. To have an integration reliably run
 * after this, override {@link DocIntegration#runAfter} with the output of
 * {@link SphinxIntegration#name} in the list. Similarly, to guarantee an integration
 * is run before this, override {@link DocIntegration#runBefore} with the same argument.
 *
 * <p>To customize the project files generated by this integration, you can make use
 * of {@link DocIntegration#interceptors} to intercept and modify the files before
 * they're written. The following named code sections are used:
 *
 * <ul>
 *     <li>{@link ConfSection}: Creates the {@code conf.py}
 *     <li>{@link MakefileSection}: Creates the {@code Makefile} build script for unix.
 *     <li>{@link WindowsMakeSection}: Creates the {@code make.bat} build script for
 *     Windows.
 *     <li>{@link RequirementsSection}: Creates the {@code requirements.txt} used to
 *     build the docs. Any dependencies here will be installed into the environment
 *     used to run {@code sphinx-build}.
 * </ul>
 *
 * This integration supports several customization options. To see all those options,
 * see {@link SphinxSettings}. These settings are configured similarly to the doc
 * generation plugin settings. Below is an example {@code smithy-build.json} with
 * sphinx project auto build disabled.
 *
 * <pre>{@code
 * {
 *     "version": "1.0",
 *     "projections": {
 *         "sphinx-markdown": {
 *             "plugins": {
 *                 "docgen": {
 *                     "service": "com.example#DocumentedService",
 *                     "format": "sphinx-markdown",
 *                     "integrations": {
 *                         "sphinx": {
 *                             "autoBuild": false
 *                         }
 *                     }
 *                 }
 *             }
 *         }
 *     }
 * }
 * }</pre>
 */
@SmithyInternalApi
public final class SphinxIntegration implements DocIntegration {
    private static final String MARKDOWN_FORMAT = "sphinx-markdown";
    private static final Set<String> FORMATS = Set.of(MARKDOWN_FORMAT);
    private static final Logger LOGGER = Logger.getLogger(SphinxIntegration.class.getName());

    // The default requirements needed to build the docs.
    private static final List<String> BASE_REQUIREMENTS = List.of(
        "Sphinx==7.2.6",
        "myst-parser==2.0.0",
        "linkify-it-py==2.0.2"
    );

    private SphinxSettings settings = SphinxSettings.fromNode(Node.objectNode());

    @Override
    public String name() {
        return "sphinx";
    }

    @Override
    public byte priority() {
        // Run at the end so that any integration-generated changes can happen.
        return -128;
    }

    @Override
    public List<DocFormat> docFormats(DocSettings settings) {
        return List.of(
            new DocFormat(MARKDOWN_FORMAT, ".md", new SphinxMarkdownWriter.Factory())
        );
    }

    @Override
    public void customize(DocGenerationContext context) {
        if (!FORMATS.contains(context.docFormat().name())) {
            LOGGER.finest(format(
                "Format %s is not a Sphinx-compatible format, skipping Sphinx project setup.",
                context.docFormat().name()
            ));
            return;
        }
        LOGGER.info("Generating Sphinx project files.");
        writeRequirements(context);
        writeConf(context);
        writeMakefile(context);
        runSphinx(context);
    }

    private void writeRequirements(DocGenerationContext context) {
        context.writerDelegator().useFileWriter("requirements.txt", writer -> {
            // Merge base and configured requirements into a single immutable list
            List<String> requirements = new ArrayList<>(BASE_REQUIREMENTS);
            requirements.addAll(settings.extraDependencies());
            requirements = List.copyOf(requirements);

            writer.pushState(new RequirementsSection(context, requirements));
            requirements.forEach(writer::write);
            writer.popState();
        });
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
                html_theme = $3S
                """,
                serviceSymbol.getName(),
                service.getVersion(),
                settings.theme());

            if (context.docFormat().name().equals(MARKDOWN_FORMAT)) {
                writer.write("""
                extensions = ["myst_parser"]
                myst_enable_extensions = [
                    # Makes bare links into actual links
                    "linkify",

                    # Used to write directives that can be parsed by normal parsers
                    "colon_fence",

                    # Used to create formatted member lists
                    "deflist",
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

    private void runSphinx(DocGenerationContext context) {
        if (!settings.autoBuild()) {
            LOGGER.info("Auto-build has been disabled. Skipping sphinx-build.");
            logManualBuildInstructions(context);
            return;
        }

        var baseDir = context.fileManifest().getBaseDir();

        LOGGER.info("Flushing writers in preparation for sphinx-build.");
        context.writerDelegator().flushWriters();

        // Python must be available to run sphinx
        try {
            LOGGER.info("Attempting to discover python3 in order to run sphinx.");
            runCommand("python3 --version", baseDir);
        } catch (CodegenException e) {
            LOGGER.warning("Unable to find python3 on path. Skipping automatic HTML doc build.");
            logManualBuildInstructions(context);
            return;
        }

        // TODO: detect if the user's existing python environment can be used
        // You can get a big JSON document describing the python environment from
        // `pip inspect` that has all the information we need.
        try {
            // First, we create a virtualenv to install dependencies into. This is necessary
            // to not pollute the user's environment.
            runCommand("python3 -m venv venv", baseDir);

            // Next, install the dependencies into the venv.
            runCommand("./venv/bin/pip install -r requirements.txt", baseDir);

            // Finally, run sphinx itself.
            runCommand("./venv/bin/sphinx-build -M " + settings.format() + " content build", baseDir);

            System.out.printf(normalizeNewlines("""
                Successfully built HTML docs. They can be found in "%1$s".

                Other output formats can also be built. A python virtual environment \
                has been created at "%2$s" containing the build tools needed for \
                manually building the docs in other formats. See the virtual \
                environment docs for information on how to activate it: \
                https://docs.python.org/3/library/venv.html#how-venvs-work

                Once the environment is activated, run `make %4$s` from "%3$s" to \
                to build the docs, substituting %4$s for whatever format you wish \
                to build.

                To build the docs without activating the virtual environment, simply \
                run `./venv/bin/sphinx-build -M %4$s content build` from "%3$s", \
                similarly substituting %4$s for your desired format.

                See sphinx docs for other output formats you can choose: \
                https://www.sphinx-doc.org/en/master/usage/builders/index.html

                """),
                baseDir.resolve("build/" + settings.format()),
                baseDir.resolve("venv"),
                baseDir,
                settings.format()
            );
        } catch (CodegenException e) {
            LOGGER.warning("Unable to automatically build HTML docs: " + e);
            logManualBuildInstructions(context);
        }
    }

    private void logManualBuildInstructions(DocGenerationContext context) {
        // TODO: try to get this printed out in the projection section
        System.out.printf(normalizeNewlines("""
            To build the HTML docs manually, you need to first install the python \
            dependencies. These can be found in the `requirements.txt` file in \
            "%1$s". The easiest way to install these is by running `pip install \
            -r requirements.txt`. Depending on your environment, you may need to \
            instead install them from your system package manager, or another \
            source.

            Once the dependencies are installed, run `make %2$s` from \
            "%1$s". Other output formats can also be built. See sphinx docs for \
            other output formats: \
            https://www.sphinx-doc.org/en/master/usage/builders/index.html

            """),
            context.fileManifest().getBaseDir(),
            settings.format()
        );
    }

    /**
     * Settings for sphinx projects, regardless of their intermediate format.
     *
     * <p>These settings can be set in the {@code smithy-build.json} file under the
     * {@code sphinx} key of the doc generation plugin's {@code integrations} config.
     * The following example shows a {@code smithy-build.json} configuration that sets
     * the default sphinx output format to be dirhtml instead of html.
     *
     * <pre>{@code
     * {
     *     "version": "1.0",
     *     "projections": {
     *         "sphinx-markdown": {
     *             "plugins": {
     *                 "docgen": {
     *                     "service": "com.example#DocumentedService",
     *                     "format": "sphinx-markdown",
     *                     "integrations": {
     *                         "sphinx": {
     *                             "format": "dirhtml"
     *                         }
     *                     }
     *                 }
     *             }
     *         }
     *     }
     * }
     * }</pre>
     *
     * @param format The sphinx output format that will be built automatically during
     *               generation. The default is html. See
     *               <a href="https://www.sphinx-doc.org/en/master/usage/builders/index.html">
     *               sphinx docs</a> for other output format options.
     * @param theme The sphinx html theme to use. The default is alabaster. If your
     *              chosen theme requires a python dependency to be added, use the
     *              {@link #extraDependencies} setting.
     * @param extraDependencies Any extra python dependencies that should be added to
     *                          the {@code requirements.txt} file for the sphinx project.
     *                          These can be particularly useful for custom {@link #theme}s.
     * @param autoBuild Whether to automatically attempt to build the generated sphinx
     *                  project. The default is true. This will attempt to discover Python
     *                  3 on the path, create a virtual environment inside the output
     *                  directory, install all the dependencies into that virtual environment,
     *                  and finally run sphinx-build.
     */
    public record SphinxSettings(
            String format,
            String theme,
            List<String> extraDependencies,
            boolean autoBuild
    ) {
        /**
         * Load the settings from an {@code ObjectNode}.
         *
         * @param node the {@code ObjectNode} to load settings from.
         * @return loaded settings based on the given node.
         */
        public static SphinxSettings fromNode(ObjectNode node) {
            List<String> extraDependencies = List.of();
            if (node.containsMember("extraDependencies")) {
                var array = node.expectArrayMember("extraDependencies");
                extraDependencies = array.getElementsAs(StringNode::getValue);
            }
            return new SphinxSettings(
                    node.getStringMemberOrDefault("format", "html"),
                    node.getStringMemberOrDefault("theme", "alabaster"),
                    extraDependencies,
                    node.getBooleanMemberOrDefault("autoBuild", true)
            );
        }
    }
}
