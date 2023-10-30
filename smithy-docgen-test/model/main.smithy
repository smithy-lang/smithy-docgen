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
}

operation DocumentedOperation {
    input := {
        structure: DocumentedStructure
    }
    output := {
        structure: DocumentedStructure
    }
}

/// This structure is an example documentable structure with several members that
/// also must be documented. This is intended as a base case with standard members
/// that don't do anything crazy. More complex use cases should be put in their own
/// structure to ensure this model is easy to traverse.
structure DocumentedStructure {
    /// This is a simple string member.
    /// It has documentation that can span multiple lines.
    string: String

    /// This is a simple integer member.
    integer: Integer

    // This doesn't have a doc string (this is just a normal comment), so it should
    // pull the docs from the target shape.
    enum: DocumentedStringEnum

    undocumented: UndocumentedStructure

    /// This is a self-referential member. This is a thing that should be possible.
    self: DocumentedStructure
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

// This structure has no docs anywhere
structure UndocumentedStructure {
    blob: Blob
    boolean: Boolean
}
