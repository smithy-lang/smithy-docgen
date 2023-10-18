/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.smithy.docgen.core;

import static java.lang.String.format;

import java.util.Locale;
import java.util.logging.Logger;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.ServiceShape;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.shapes.ShapeVisitor;
import software.amazon.smithy.model.traits.StringTrait;
import software.amazon.smithy.model.traits.TitleTrait;
import software.amazon.smithy.utils.SmithyUnstableApi;

/**
 * Creates documentation Symbols for each shape in the model.
 *
 * <p>These symbols contain many important pieces of metadata. Particularly
 * important are:
 *
 * <p><ul>
 *     <li>{@code name}: The name of the symbol will be used as the title for its
 *     definition section. For services, this defaults to the value of the
 *     {@code title} trait. For other shapes, it defaults to the shape name including
 *     any renames from the attached service. For members, the member name is appended
 *     to the parent with a separating {@code -}.
 *     <li>{@code definitionFile}: The file in which the documentation for this shape
 *     should be written. By default these are all written to a single flat directory.
 *     If this is empty, the shape does not have its own definition section.
 *     <li>{@link #SHAPE_PROPERTY}: A named Shape property containing the shape that
 *     the symbol represents. Decorators provided by
 *     {@link DocIntegration#decorateSymbolProvider} MUST set or preserve this
 *     property.
 *     <li>{@link #LINK_ID_PROPERTY}: A named String property containing the string to
 *     use for the id for links to the shape. In HTML, this would be the {@code id} for
 *     the tag containing the shape's definition. Given a link id {@code foo}, a link
 *     to the shape's definition might look like {@code https://example.com/shapes#foo}
 *     for example. If this or {@code definitionFile} is empty, it is not possible to
 *     link to the shape.
 * </ul>
 *
 * <p>Decorators provided by {@link DocIntegration#decorateSymbolProvider} MUST set
 * these properties or preserve
 *
 */
@SmithyUnstableApi
public final class DocSymbolProvider extends ShapeVisitor.Default<Symbol> implements SymbolProvider {

    /**
     * The name for a shape symbol's named property containing the shape the symbol
     * represents.
     *
     * <p>Decorators provided by {@link DocIntegration#decorateSymbolProvider} MUST
     * preserve this property.
     *
     * <p>Use {@code symbol.expectProperty(SHAPE_PROPERTY, Shape.class)} to access this
     * property.
     */
    public static final String SHAPE_PROPERTY = "shape";

    /**
     * The name for a shape symbol's named property containing the string to use for
     * the id for links to the shape. In HTML, this would be the {@code id} for the tag
     * containing the shape's definition. Given a link id {@code foo}, a link to the
     * shape's definition might look like {@code https://example.com/shapes#foo} for
     * example.
     *
     * <p>If this or {@code definitionFile} is empty, it is not possible to link to
     * the shape.
     *
     * <p>Use {@code symbol.getProperty(LINK_ID_PROPERTY, String.class)} to access this
     * property.
     */
    public static final String LINK_ID_PROPERTY = "linkId";

    private static final Logger LOGGER = Logger.getLogger(DocSymbolProvider.class.getName());

    private final Model model;
    private final DocSettings docSettings;
    private final ServiceShape serviceShape;

    public DocSymbolProvider(Model model, DocSettings docSettings) {
        this.model = model;
        this.docSettings = docSettings;
        this.serviceShape = model.expectShape(docSettings.service(), ServiceShape.class);
    }

    @Override
    public Symbol toSymbol(Shape shape) {
        var symbol = shape.accept(this);
        LOGGER.fine(() -> format("Creating symbol from %s: %s", shape, symbol));
        return symbol;
    }

    @Override
    public Symbol serviceShape(ServiceShape shape) {
        return getSymbolBuilder(shape)
            .definitionFile(getDefinitionFile(serviceShape, shape))
            .build();
    }

    private Symbol.Builder getSymbolBuilder(Shape shape) {
        var name = getShapeName(serviceShape, shape);
        return Symbol.builder()
            .name(name)
            .putProperty(SHAPE_PROPERTY, shape)
            .definitionFile(getDefinitionFile(serviceShape, shape))
            .putProperty(LINK_ID_PROPERTY, getLinkId(name));
    }

    private String getDefinitionFile(ServiceShape serviceShape, Shape shape) {
        if (shape.isMemberShape()) {
            return getDefinitionFile(serviceShape, model.expectShape(shape.getId().withoutMember()));
        }
        return getDefinitionFile(
            getShapeName(serviceShape, shape).replaceAll("\\s+", "") + ".md"
        );
    }

    public String getDefinitionFile(String filename) {
        return "content/" + filename;
    }

    private String getShapeName(ServiceShape serviceShape, Shape shape) {
        if (shape.isServiceShape()) {
            return shape.getTrait(TitleTrait.class)
                .map(StringTrait::getValue)
                .orElse(shape.getId().getName());
        }
        var name = shape.getId().getName(serviceShape);
        if (shape.isMemberShape()) {
            name += "-" + toMemberName(shape.asMemberShape().get());
        }
        return name;
    }

    private String getLinkId(String shapeName) {
        return shapeName.toLowerCase(Locale.ENGLISH).replaceAll("\\s+", "-");
    }

    // All other shapes don't get generation, so we'll do null checks where this might
    // have impact.
    @Override
    protected Symbol getDefault(Shape shape) {
        return null;
    }
}
