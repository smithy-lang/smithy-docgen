## Smithy DocGen

Smithy build plugin to generate API documentation from models authored in
[Smithy](https://smithy.io) IDL.

NOTE: this project is currently in a pre-release state. Interfaces and output
formatting may change before full release.

### Configuration

This generator supports the following top-level configuration options:

* `service` - The shape ID of the service to generate documentation for.
* `format` - The format that the documentation should be generated in.
* `references` - A map of resource shape ID to URL for resources referenced by
  the [references trait](https://smithy.io/2.0/spec/resource-traits.html#references-trait)
  that aren't included in service.

```json
{
    "version": "1.0",
    "projections": {
        "plain-markdown": {
            "plugins": {
                "docgen": {
                    "service": "com.example#MyService",
                    "format": "markdown",
                    "references": {
                        "com.example#ExternalReference": "https://example.com/"
                    }
                }
            }
        }
    }
}
```

#### Supported Formats

The output format can be selected with the `format` configuration option. The
example below demonstrtates selecting a plain markdown output format:

```json
{
    "version": "1.0",
    "projections": {
        "plain-markdown": {
            "plugins": {
                "docgen": {
                    "service": "com.example#MyService",
                    "format": "markdown"
                }
            }
        }
    }
}
```

By default, two formats are currently supported: `markdown` and
`sphinx-markdown`. The `markdown` format renders docs as plain
[CommonMark](https://commonmark.org), while `sphinx-commonmark` creates a
[Sphinx](https://www.sphinx-doc.org/) markdown project that gets rendered to
HTTP. `sphinx-markdown` is used by default.

The generator is designed to allow for different output formats by supplying a
new
[DocWriter](https://github.com/smithy-lang/smithy-docgen/blob/main/smithy-docgen-core/src/main/java/software/amazon/smithy/docgen/core/writers/DocWriter.java)
via a
[DocIntegration](https://github.com/smithy-lang/smithy-docgen/blob/main/smithy-docgen-core/src/main/java/software/amazon/smithy/docgen/core/DocIntegration.java).

##### sphinx-markdown

The `sphinx-markdown` format uses Sphinx's markdown support provided by
[MySt](https://myst-parser.readthedocs.io/en/latest/), which builds on top of
CommonMark. By default, it will render the generated markdown into HTML as long
as Python 3 is found on the path.

* `format` (default: `html`) - The
  [sphinx output format](https://www.sphinx-doc.org/en/master/usage/builders/index.html).
* `theme` (default: [`furo`](https://github.com/pradyunsg/furo)) - The theme to
  use for sphinx. If this is changed, the new theme will likely need to be added
  to the `extraDependencies` list.
* `extraDependencies` (default: `[]`) - A list of additional dependencies to be
  added to the `requirements.txt` file, which is installed before building the
  documentation.
* `extraExtensions` (default: `[]`) - A list of additional sphinx extentions to
  add to the sphinx extensions list in `conf.py`. Any additional extensions will
  likely need to be added to the `extraDependencies` list.
* `autoBuild` (default: `true`) - Whether to automatically render the
  documentation to HTML. You may wish to disable autobuild if you want to add
  additional documentation to the project before building, such as hand-written
  guides.

The following example `smithy-build.json` demonstrates configuring the
`sphinx-markdown` format.

```json
{
    "version": "1.0",
    "projections": {
        "sphinx-markdown": {
            "plugins": {
                "docgen": {
                    "service": "com.example#DocumentedService",
                    "format": "sphinx-markdown",
                    "integrations": {
                      "sphinx": {
                        "format": "dirhtml",
                        "autoBuild": false
                      }
                    }
                }
            }
        }
    }
}
```

### Trait Support

Currently, most prelude (`smithy.api`) traits are supported, or deliberately
excluded where not relevant to customer documentation. Trait information is
easily added using Smithy's
[interceptor](https://github.com/smithy-lang/smithy/blob/main/smithy-utils/src/main/java/software/amazon/smithy/utils/CodeInterceptor.java)
system. Most trait information is added using interceptors, the implementations
of which can be found in the
[interceptors](https://github.com/smithy-lang/smithy-docgen/tree/main/smithy-docgen-core/src/main/java/software/amazon/smithy/docgen/core/interceptors)
package.

Auth traits are automatically supported as part of the service's auth list,
where the trait's docs are used by default. More context can be added using
the same interceptors that are run on normal shapes.

#### Traits Missing Support

The following prelude traits and trait categories are currently unsupported. All
traits outside of the prelude are unsupported, with the exception of auth traits
which have default support.

* Protocol Traits - These should get a similar treatment to auth traits, where a
  dedicated section is created for them and documentation is added without
  needing to add explicit support. Each protocol also needs to be able to register
  an example generator.
* [cors](https://smithy.io/2.0/spec/http-bindings.html#smithy-api-cors-trait)
* [Event Streaming](https://smithy.io/2.0/spec/streaming.html#event-streams)
* [examples](https://smithy.io/2.0/spec/documentation-traits.html#smithy-api-examples-trait) -
  The sections and wrapping for these are created, and currently there's a
  stub that simply places the values of example inputs and outputs inside the
  example blocks. An interface needs to be created for code generators to
  actually integrate into this. Updates to directed codegen will likely be
  needed to make this feasible. Protocols will need to implement this also.

## Security

See [CONTRIBUTING](CONTRIBUTING.md#security-issue-notifications) for more information.

## License

This project is licensed under the Apache-2.0 License.
