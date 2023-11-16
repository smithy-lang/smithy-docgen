$version: "2.0"

namespace com.example

/// This service is not intended to be representative of a real service. Rather, it is
/// meant to exercise different kinds of behavior that the documentation generator
/// should handle. For example, the implementation <b>must</b> be able to handle HTML
/// tags since that's part of the [CommonMark spec](https://spec.commonmark.org/).
@title("Documented Service")
service DocumentedService {
    version: "2023-10-13"
    operations: [
        DocumentedOperation
    ]
    resources: [
        DocumentationResource
    ]
    errors: [
        ClientError
        ServiceError
    ]
}

@examples(
    [
        {
            title: "Basic Example"
            documentation: "This **MUST** also support CommonMark"
            input: {
                structure: {
                    string: "foo"
                    integer: 4
                    enum: "BAR"
                    undocumented: {boolean: false}
                }
            }
            output: {
                structure: {
                    string: "spam"
                    integer: 8
                    enum: "FOO"
                    undocumented: {boolean: true}
                }
            }
        }
    ]
)
operation DocumentedOperation {
    input := {
        structure: DocumentedStructure
    }
    output := {
        structure: DocumentedStructure
    }
    errors: [
        DocumentedOperationError
    ]
}

/// This structure is an example documentable structure with several members that
/// also must be documented. This is intended as a base case with standard members
/// that don't do anything crazy. More complex use cases should be put in their own
/// structure to ensure this model is easy to traverse.
@externalDocumentation(
    "Smithy Reference": "https://smithy.io/"
    Structures: "https://smithy.io/2.0/spec/aggregate-types.html#structure"
)
structure DocumentedStructure {
    /// This is a simple string member.
    /// It has documentation that can span multiple lines.
    @since("2023-11-16")
    string: String

    /// This is a simple integer member.
    @deprecated
    integer: Integer

    // This doesn't have a doc string (this is just a normal comment), so it should
    // pull the docs from the target shape.
    @deprecated(message: "Please use intEnum instead")
    enum: DocumentedStringEnum

    // This also will pull docs from the shape.
    intEnum: DocumentedIntEnum

    @deprecated(since: "2023-11-15")
    undocumented: UndocumentedStructure

    /// This is a self-referential member. This is a thing that should be possible.
    self: DocumentedStructure

    @recommended(reason: "Because unions are cool")
    union: DocumentedUnion
}

/// This in an enum that can have one of the following values:
///
/// - `FOO`
/// - `BAR`
///
/// Like other shapes in the model, this doesn't actually mean anything.
enum DocumentedStringEnum {
    /// One of the more common placeholders in the programming world.
    FOO

    /// Another very common placeholder, often seen with `foo`.
    BAR
}

/// This in an enum that can have one of the following values:
///
/// - `SPAM`: `1`
/// - `EGGS`: `2`
///
/// Like other shapes in the model, this doesn't actually mean anything.
intEnum DocumentedIntEnum {
    /// The spam and eggs placeholders are really only common in Python code bases.
    SPAM = 1

    /// They're a reference to a famous Monty Python skit, which is fitting because the
    /// language itself is named after Monty Python.
    EGGS = 2
}

// This structure has no docs anywhere
structure UndocumentedStructure {
    blob: Blob
    boolean: Boolean
}

@mixin
@error("client")
structure ErrorMixin {
    /// The wire-level error identifier.
    code: String

    /// A message with details about why the error happened.
    message: String
}

/// This is an error that is the fault of the calling client.
structure ClientError with [ErrorMixin] {}

/// This is an error that is the fault of the service.
@error("server")
structure ServiceError with [ErrorMixin] {}

/// This error is only returned by DocumentedOperation
structure DocumentedOperationError with [ErrorMixin] {}

/// Unions can only have one member set. The member name is used as a tag to
/// determine which member is intended at runtime.
union DocumentedUnion {
    /// Union members for the most part look like structure members, with the exception
    /// that exactly one must be set.
    string: String

    /// It doesn't matter that multiple members target the same type, since the type
    /// isn't the discriminator, the tag (member name) is.
    otherString: String

    struct: DocumentedStructure
}

/// A resource shape. To have some sense of readability this will represent the concept
/// of documentation itself as a resource, presenting the image of a service which
/// stores such things.
resource DocumentationResource {
    identifiers: {id: DocumentationId}
    properties: {contents: DocumentationContents, archived: DocumentationArchived}
    put: PutDocumentation
    create: CreateDocumentation
    read: GetDocumentation
    update: UpdateDocumentation
    delete: DeleteDocumentation
    list: ListDocumentation
    operations: [
        ArchiveDocumentation
    ]
    collectionOperations: [
        DeleteArchivedDocumentation
    ]
    resources: [
        DocumentationArtifact
    ]
}

/// The identifier for the documentation resoruce.
///
/// These properites and identifiers all have their own shapes to enable documentation
/// sharing, not necessarily because they have meaningful collections of constraints
/// or other wire-level traits.
string DocumentationId

/// The actual body of the documentation.
string DocumentationContents

/// Whether or not the documentation has been archived. This could mean that changes
/// are rejected, for example.
boolean DocumentationArchived

/// Put operations create a resource with a user-specified identifier.
@idempotent
operation PutDocumentation {
    input := for DocumentationResource {
        @required
        $id

        @required
        $contents
    }
}

/// Create operations instead have the service create the identifier.
operation CreateDocumentation {
    input := for DocumentationResource {
        @required
        $contents
    }
}

/// Gets the contents of a documentation resource.
@readonly
operation GetDocumentation {
    input := for DocumentationResource {
        @required
        $id
    }

    output := for DocumentationResource {
        @required
        $id

        @required
        $contents

        @required
        $archived
    }
}

/// Does an update on the documentation resource. These can often also be the put
/// lifecycle operation.
@idempotent
operation UpdateDocumentation {
    input := for DocumentationResource {
        @required
        $id

        @required
        $contents
    }
}

/// Deletes documentation.
@idempotent
operation DeleteDocumentation {
    input := for DocumentationResource {
        @required
        $id
    }
}

/// Archives documentation. This is here to be a non-lifecycle instance operation.
/// We need both instance operations and collection operations that aren't lifecycle
/// operations to make sure both cases are being documented.
@idempotent
operation ArchiveDocumentation {
    input := for DocumentationResource {
        @required
        $id
    }
}

/// Deletes all documentation that's been archived. This is a collection operation that
/// isn't part of a lifecycle operation, which is again needed to make sure everything
/// is being documented as expected.
operation DeleteArchivedDocumentation {}

/// Lists the avialable documentation resources.
@readonly
operation ListDocumentation {
    input := {
        // Whether to list documentation that has been archived.
        showArchived: Boolean = false
    }

    output := {
        /// A list of all the documentation. No pagination here.
        @required
        documentation: DocumentationList
    }
}

list DocumentationList {
    member: Documentation
}

/// A concrete documentation resource instance.
structure Documentation for DocumentationResource {
    @required
    $id

    @required
    $contents

    @required
    $archived
}

/// This would be something like a built PDF.
resource DocumentationArtifact {
    identifiers: {id: DocumentationId, artifactId: DocumentationArtifactId}
    properties: {data: DocumentationArtifactData}
    put: PutDocumentationArtifact
    read: GetDocumentationArtifact
    delete: DeleteDocumentationArtifact
}

/// Sub-resources need distinct identifiers.
string DocumentationArtifactId

/// This would be the bytes containing the artifact
blob DocumentationArtifactData

@idempotent
operation PutDocumentationArtifact {
    input := for DocumentationArtifact {
        @required
        $id

        @required
        $artifactId

        @required
        $data
    }
}

@readonly
operation GetDocumentationArtifact {
    input := for DocumentationArtifact {
        @required
        $id

        @required
        $artifactId
    }

    output := for DocumentationArtifact {
        @required
        $id

        @required
        $artifactId

        @required
        $data
    }
}

@idempotent
operation DeleteDocumentationArtifact {
    input := for DocumentationArtifact {
        @required
        $id

        @required
        $artifactId
    }
}
