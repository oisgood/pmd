/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.ast.SourceCodePositioner;

import apex.jorje.data.Location;
import apex.jorje.data.Locations;
import apex.jorje.semantic.ast.AstNode;
import apex.jorje.semantic.exception.UnexpectedCodePathException;

abstract class AbstractApexNode<T extends AstNode> extends AbstractApexNodeBase implements ApexNode<T> {

    protected final T node;

    protected AbstractApexNode(T node) {
        super(node.getClass());
        this.node = node;
    }

    @Override
    @SuppressWarnings("unchecked")
    public NodeStream<? extends ApexNode<?>> children() {
        return (NodeStream<? extends ApexNode<?>>) super.children();
    }

    void calculateLineNumbers(SourceCodePositioner positioner) {
        if (!hasRealLoc()) {
            return;
        }

        Location loc = node.getLoc();
        calculateLineNumbers(positioner, loc.getStartIndex(), loc.getEndIndex());
    }

    protected void handleSourceCode(String source) {
        // default implementation does nothing
    }

    @Deprecated
    @InternalApi
    @Override
    public T getNode() {
        return node;
    }

    @Override
    public boolean hasRealLoc() {
        try {
            Location loc = node.getLoc();
            return loc != null && Locations.isReal(loc);
        } catch (UnexpectedCodePathException e) {
            return false;
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            // bug in apex-jorje? happens on some ReferenceExpression nodes
            return false;
        }
    }

    public String getLocation() {
        if (hasRealLoc()) {
            return String.valueOf(node.getLoc());
        } else {
            return "no location";
        }
    }

    @Override
    public String getDefiningType() {
        if (node.getDefiningType() != null) {
            return node.getDefiningType().getApexName();
        }
        return null;
    }

    @Override
    public String getNamespace() {
        if (node.getDefiningType() != null) {
            return node.getDefiningType().getNamespace().toString();
        }
        return null;
    }
}
