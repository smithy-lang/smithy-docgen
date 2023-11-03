/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.smithy.docgen.core.generators;

import software.amazon.smithy.codegen.core.CodegenException;
import software.amazon.smithy.docgen.core.DocGenerationContext;
import software.amazon.smithy.docgen.core.sections.MemberSection;
import software.amazon.smithy.docgen.core.sections.ShapeMembersSection;
import software.amazon.smithy.docgen.core.writers.DocWriter;
import software.amazon.smithy.model.shapes.BigDecimalShape;
import software.amazon.smithy.model.shapes.BigIntegerShape;
import software.amazon.smithy.model.shapes.BlobShape;
import software.amazon.smithy.model.shapes.BooleanShape;
import software.amazon.smithy.model.shapes.ByteShape;
import software.amazon.smithy.model.shapes.DocumentShape;
import software.amazon.smithy.model.shapes.DoubleShape;
import software.amazon.smithy.model.shapes.EnumShape;
import software.amazon.smithy.model.shapes.FloatShape;
import software.amazon.smithy.model.shapes.IntEnumShape;
import software.amazon.smithy.model.shapes.IntegerShape;
import software.amazon.smithy.model.shapes.ListShape;
import software.amazon.smithy.model.shapes.LongShape;
import software.amazon.smithy.model.shapes.MapShape;
import software.amazon.smithy.model.shapes.MemberShape;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.shapes.ShapeVisitor;
import software.amazon.smithy.model.shapes.ShortShape;
import software.amazon.smithy.model.shapes.StringShape;
import software.amazon.smithy.model.shapes.StructureShape;
import software.amazon.smithy.model.shapes.TimestampShape;
import software.amazon.smithy.model.shapes.UnionShape;
import software.amazon.smithy.utils.SmithyUnstableApi;

/**
 * Generates documentation for shape members.
 *
 * <p>The output of this can be customized in a number of ways. To add details to
 * or re-write particular sections, register an interceptor with
 * {@link software.amazon.smithy.docgen.core.DocIntegration#interceptors}. The following
 * sections are guaranteed to be present:
 *
 * <ul>
 *     <li>{@link MemberSection}: Enables re-writing the documentation for specific members.
 *     <li>{@link ShapeMembersSection}: Enables re-writing or overwriting the entire list
 *     of members, including changes made in other sections.
 * </ul>
 *
 * <p>To change the intermediate format (e.g. from markdown to restructured text),
 * a new {@link software.amazon.smithy.docgen.core.DocFormat} needs to be introduced
 * via {@link software.amazon.smithy.docgen.core.DocIntegration#docFormats}.
 */
@SmithyUnstableApi
public final class MemberGenerator implements Runnable {

    private final DocGenerationContext context;
    private final Shape shape;
    private final MemberListingType listingType;
    private final DocWriter writer;

    /**
     * Constructs a MemberGenerator.
     *
     * @param context The context used to generate documentation.
     * @param writer The writer to write to.
     * @param shape The shape whose members are being generated.
     * @param listingType The type of listing being generated.
     */
    public MemberGenerator(
            DocGenerationContext context,
            DocWriter writer,
            Shape shape,
            MemberListingType listingType
    ) {
        this.context = context;
        this.writer = writer;
        this.shape = shape;
        this.listingType = listingType;
    }

    @Override
    public void run() {
        writer.pushState(new ShapeMembersSection(context, shape, shape.members(), listingType));
        writer.openHeading(listingType.getTitle());
        writer.openMemberListing();
        for (MemberShape member: shape.getAllMembers().values()) {
            writer.pushState(new MemberSection(context, member));

            var symbol = context.symbolProvider().toSymbol(member);
            var target = context.model().expectShape(member.getTarget());
            writer.openMemberEntry(symbol, w -> target.accept(new MemberTypeVisitor(w, context)));
            writer.writeShapeDocs(member, context.model());

            writer.closeMemberEntry();
            writer.popState();
        }
        writer.closeMemberListing();
        writer.closeHeading();
        writer.popState();
    }

    /**
     * The type of listing. This controls the heading title and anchor id for the section.
     */
    public enum MemberListingType {
        /**
         * Indicates the listing is for normal shape members.
         */
        MEMBERS("Members");

        private final String title;

        MemberListingType(String title) {
            this.title = title;
        }

        /**
         * @return returns the heading title that should be used for the listing.
         */
        public String getTitle() {
            return title;
        }
    }

    private static class MemberTypeVisitor extends ShapeVisitor.Default<Void> {

        private final DocWriter writer;
        private final DocGenerationContext context;

        MemberTypeVisitor(DocWriter writer, DocGenerationContext context) {
            this.writer = writer;
            this.context = context;
        }

        @Override
        protected Void getDefault(Shape shape) {
            throw new CodegenException(String.format(
                    "Unexpected member %s of type %s", shape.getId(), shape.getType()));
        }

        @Override
        public Void blobShape(BlobShape shape) {
            writeShapeName(shape);
            return null;
        }

        @Override
        public Void booleanShape(BooleanShape shape) {
            writeShapeName(shape);
            return null;
        }

        @Override
        public Void byteShape(ByteShape shape) {
            writeShapeName(shape);
            return null;
        }

        @Override
        public Void shortShape(ShortShape shape) {
            writeShapeName(shape);
            return null;
        }

        @Override
        public Void integerShape(IntegerShape shape) {
            writeShapeName(shape);
            return null;
        }

        @Override
        public Void intEnumShape(IntEnumShape shape) {
            writeShapeName(shape);
            return null;
        }

        @Override
        public Void longShape(LongShape shape) {
            writeShapeName(shape);
            return null;
        }

        @Override
        public Void floatShape(FloatShape shape) {
            writeShapeName(shape);
            return null;
        }

        @Override
        public Void documentShape(DocumentShape shape) {
            writeShapeName(shape);
            return null;
        }

        @Override
        public Void doubleShape(DoubleShape shape) {
            writeShapeName(shape);
            return null;
        }

        @Override
        public Void bigIntegerShape(BigIntegerShape shape) {
            writeShapeName(shape);
            return null;
        }

        @Override
        public Void bigDecimalShape(BigDecimalShape shape) {
            writeShapeName(shape);
            return null;
        }

        @Override
        public Void stringShape(StringShape shape) {
            writeShapeName(shape);
            return null;
        }

        @Override
        public Void enumShape(EnumShape shape) {
            writeShapeName(shape);
            return null;
        }

        @Override
        public Void timestampShape(TimestampShape shape) {
            writeShapeName(shape);
            return null;
        }

        @Override
        public Void listShape(ListShape shape) {
            writer.writeInline("List<");
            context.model().expectShape(shape.getMember().getTarget()).accept(this);
            writer.writeInline(">");
            return null;
        }

        @Override
        public Void mapShape(MapShape shape) {
            writer.writeInline("Map<");
            context.model().expectShape(shape.getKey().getTarget()).accept(this);
            writer.writeInline(", ");
            context.model().expectShape(shape.getValue().getTarget()).accept(this);
            writer.writeInline(">");
            return null;
        }

        @Override
        public Void structureShape(StructureShape shape) {
            writeShapeName(shape);
            return null;
        }

        @Override
        public Void unionShape(UnionShape shape) {
            writeShapeName(shape);
            return null;
        }

        private void writeShapeName(Shape shape) {
            var symbol = context.symbolProvider().toSymbol(shape);
            writer.writeInline("$R", symbol);
        }
    }
}
