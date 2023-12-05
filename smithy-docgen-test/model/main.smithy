$version: "2.0"

metadata suppressions = [
    {id: "UnstableTrait", namespace: "com.example", reason: "These are used for examples."}
    {id: "DeprecatedTrait", namespace: "com.example", reason: "These are used for examples."}
]

namespace com.example

use aws.protocols#awsJson1_0
use aws.protocols#restJson1
use aws.protocols#restXml

/// This service is not intended to be representative of a real service. Rather, it is
/// meant to exercise different kinds of behavior that the documentation generator
/// should handle. For example, the implementation <b>must</b> be able to handle HTML
/// tags since that's part of the [CommonMark spec](https://spec.commonmark.org/).
@title("Documented Service")
@httpBasicAuth
@httpDigestAuth
@httpBearerAuth
@httpApiKeyAuth(name: "auth-bearing-header", in: "header", scheme: "Bearer")
@auth([httpApiKeyAuth, httpBearerAuth, httpDigestAuth])
@awsJson1_0
@restJson1
@restXml
service DocumentedService {
    version: "2023-10-13"
    operations: [
        DocumentedOperation
        UnauthenticatedOperation
        OptionalAuthOperation
        LimitedAuthOperation
        LimitedOptionalAuthOperation
        HttpTraits
    ]
    resources: [
        DocumentationResource
    ]
    errors: [
        ClientError
        ServiceError
    ]
}

/// This operation showcases most of the various HTTP traits.
@http(method: "POST", uri: "/HttpTraits/{label}/{greedyLabel+}?static", code: 200)
@httpChecksumRequired
operation HttpTraits {
    input := {
        /// This is a label member that's bound to a normal label.
        @httpLabel
        @required
        label: String

        /// This is a label member that's bound to a greedy label.
        @httpLabel
        @required
        greedyLabel: String

        /// This is a header member bound to a single static header.
        @httpHeader("x-custom-header")
        singletonHeader: String

        /// This is a header member that's bound to a list.
        @httpHeader("x-list-header")
        listHeader: StringList

        /// This is a header member that's bound to a map with a prefix.
        @httpPrefixHeaders("prefix-")
        prefixHeaders: DenseStringMap

        /// This is a query param that's bound to a single param.
        @httpQuery("singelton")
        singletonQuery: String

        /// This is a query param that's bound to a list.
        @httpQuery("list")
        listQuery: StringList

        /// This is an open listing of all query params.
        @httpQueryParams
        mapQuery: StringMap

        /// This is the operation's payload, only useable since everything
        /// else is bound to some other part of the HTTP request.
        @httpPayload
        payload: Blob
    }

    output := {
        /// This allows people to more easily interact with the http response
        /// without having to leak the response object.
        @httpResponseCode
        responseCode: Integer
    }
}

/// This operation does not support any of the service's auth types.
@auth([])
@http(method: "POST", uri: "/UnauthenticatedOperation")
operation UnauthenticatedOperation {}

/// This operation supports all of the service's auth types, but optionally.
@optionalAuth
@http(method: "POST", uri: "/OptionalAuthOperation")
operation OptionalAuthOperation {}

/// This operation supports a limited set of the service's auth.
@auth([httpBasicAuth, httpApiKeyAuth])
@http(method: "POST", uri: "/LimitedAuthOperation")
operation LimitedAuthOperation {}

/// This operation supports a limited set of the service's auth, optionally.
@optionalAuth
@auth([httpBasicAuth, httpDigestAuth])
@http(method: "POST", uri: "/LimitedOptionalAuthOperation")
operation LimitedOptionalAuthOperation {}

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
@requestCompression(
    encodings: ["gzip"]
)
@http(method: "POST", uri: "/DocumentedOperation")
operation DocumentedOperation {
    input := {
        /// This is an idempotency token, which will inherently mark this operation
        /// as idempotent.
        @idempotencyToken
        token: String

        structure: DocumentedStructure

        lengthExamples: LengthTraitExamples

        rangeExamples: RangeTraitExamples
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
    @jsonName("foo")
    @xmlName("bar")
    string: String

    /// This member has a pattern trait on it.
    @pattern("^[A-Za-z]+$")
    pattern: String

    /// This is a simple integer member.
    @deprecated
    integer: Integer

    /// This is a timestamp with a custom format
    timestamp: DateTime

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

    xmlTraits: XmlTraits
}

/// Timestamp in RFC3339 format
@timestampFormat("date-time")
timestamp DateTime

/// This structure showcases various XML traits
@xmlName("foo")
structure XmlTraits {
    /// This shows that the xml name isn't inherited from the target.
    nested: XmlTraits

    /// This shows an xml name targeting a normal shape.
    @xmlName("bar")
    xmlName: String

    /// This shows how xml attributes are displayed.
    @xmlAttribute
    xmlAttribute: String

    /// This list uses the default nesting behavior.
    nestedList: StringList

    /// This list uses the non-default flat list behavior.
    @xmlFlattened
    flatList: StringList

    /// This map uses the default nesting behavior.
    nestedMap: StringMap

    /// This map uses the non-default flat map behavior.
    @xmlFlattened
    flatMap: StringMap

    /// This string tag needs an xml namespace added to it.
    @xmlNamespace(prefix: "example", uri: "https://example.com")
    xmlNamespace: String
}

list StringList {
    member: String
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

/// This shows how the length trait is applied to various types.
structure LengthTraitExamples {
    @length(min: 4)
    string: String

    @length(max: 255)
    blob: Blob

    @length(min: 2, max: 4)
    list: StringSet

    map: StringMap
}

/// A set of strings.
@uniqueItems
list StringSet {
    member: String
}

/// A string map that allows null values.
@sparse
@length(max: 16)
map StringMap {
    key: String
    value: String
}

/// A map that disallows null values.
map DenseStringMap {
    key: String
    value: String
}

/// This shows how the range trait is applied to various types.
structure RangeTraitExamples {
    @range(min: 0)
    positive: Integer

    @range(max: 0)
    negative: Long

    @range(min: 0, max: 255)
    unsignedByte: Short
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
@unstable
structure UndocumentedStructure {
    blob: SensitiveBlob

    @internal
    boolean: Boolean
}

@sensitive
blob SensitiveBlob

@mixin
@error("client")
@httpError(400)
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
@httpError(500)
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
@noReplace
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
@http(method: "PUT", uri: "/DocumentationResource/{id}")
operation PutDocumentation {
    input := for DocumentationResource {
        @required
        @httpLabel
        $id

        @required
        $contents
    }
}

/// Create operations instead have the service create the identifier.
@http(method: "POST", uri: "/DocumentationResource")
operation CreateDocumentation {
    input := for DocumentationResource {
        @required
        $contents
    }
}

/// Gets the contents of a documentation resource.
@readonly
@http(method: "GET", uri: "/DocumentationResource/{id}")
operation GetDocumentation {
    input := for DocumentationResource {
        @required
        @httpLabel
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
@http(method: "PUT", uri: "/DocumentationResource")
operation UpdateDocumentation {
    input := for DocumentationResource {
        @required
        @httpQuery("id")
        $id

        @required
        $contents
    }
}

/// Deletes documentation.
@idempotent
@http(method: "DELETE", uri: "/DocumentationResource/{id}")
operation DeleteDocumentation {
    input := for DocumentationResource {
        @required
        @httpLabel
        $id
    }
}

/// Archives documentation. This is here to be a non-lifecycle instance operation.
/// We need both instance operations and collection operations that aren't lifecycle
/// operations to make sure both cases are being documented.
@idempotent
@http(method: "PUT", uri: "/DocumentationResource/{id}/archive")
operation ArchiveDocumentation {
    input := for DocumentationResource {
        @required
        @httpLabel
        $id
    }
}

/// Deletes all documentation that's been archived. This is a collection operation that
/// isn't part of a lifecycle operation, which is again needed to make sure everything
/// is being documented as expected.
@http(method: "DELETE", uri: "/DocumentationResource?delete-archived")
@idempotent
operation DeleteArchivedDocumentation {}

/// Lists the avialable documentation resources.
@readonly
@http(method: "GET", uri: "/DocumentationResource")
@paginated(inputToken: "paginationToken", outputToken: "paginationToken", items: "documentation", pageSize: "pageSize")
operation ListDocumentation {
    input := {
        /// Whether to list documentation that has been archived.
        @httpQuery("showArchived")
        showArchived: Boolean = false

        @httpHeader("x-example-pagination-token")
        paginationToken: String

        @httpHeader("x-example-page-size")
        pageSize: Integer
    }

    output := {
        @required
        documentation: DocumentationList

        paginationToken: String
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
@http(method: "PUT", uri: "/DocumentationResource/{id}/artifact/{artifactId}")
operation PutDocumentationArtifact {
    input := for DocumentationArtifact {
        @required
        @httpLabel
        $id

        @required
        @httpLabel
        $artifactId

        @required
        $data
    }
}

@readonly
@http(method: "GET", uri: "/DocumentationResource/{id}/artifact/{artifactId}")
operation GetDocumentationArtifact {
    input := for DocumentationArtifact {
        @required
        @httpLabel
        $id

        @required
        @httpLabel
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
@http(method: "DELETE", uri: "/DocumentationResource/{id}/artifact/{artifactId}")
operation DeleteDocumentationArtifact {
    input := for DocumentationArtifact {
        @required
        @httpLabel
        $id

        @required
        @httpLabel
        $artifactId
    }
}
