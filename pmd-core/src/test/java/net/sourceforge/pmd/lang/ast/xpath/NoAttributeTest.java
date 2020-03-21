/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.xpath;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import net.sourceforge.pmd.internal.util.IteratorUtil;
import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.xpath.NoAttribute.NoAttrScope;

/**
 * @author Clément Fournier
 */
public class NoAttributeTest {


    @Test
    public void testNoAttrInherited() {
        Node child = new NodeNoInherited(12);

        Set<String> attrNames = IteratorUtil.toList(child.getXPathAttributesIterator()).stream().map(Attribute::getName).collect(Collectors.toSet());

        assertTrue(attrNames.contains("SomeInt"));
        assertTrue(attrNames.contains("Child"));
        // from Node
        assertTrue(attrNames.contains("BeginLine"));

        assertFalse(attrNames.contains("SomeLong"));
        assertFalse(attrNames.contains("Image"));
        assertFalse(attrNames.contains("SomeName"));
    }


    @Test
    public void testNoAttrAll() {

        assertTrue(0 < IteratorUtil.count(new NodeAllAttr(12).getXPathAttributesIterator()));

        NodeNoAttrAll child = new NodeNoAttrAll(12);
        Set<String> attrNames = IteratorUtil.toList(child.getXPathAttributesIterator()).stream().map(Attribute::getName).collect(Collectors.toSet());

        // from Noded, so not suppressed
        assertTrue(attrNames.contains("Image"));
        assertFalse(attrNames.contains("MySuppressedAttr"));

    }

    @Test
    public void testNoAttrAllIsNotInherited() {

        NodeNoAttrAllChild child = new NodeNoAttrAllChild(12);

        Set<String> attrNames = IteratorUtil.toList(child.getXPathAttributesIterator()).stream().map(Attribute::getName).collect(Collectors.toSet());

        // suppressed because the parent has NoAttribute(scope = ALL)
        assertFalse(attrNames.contains("MySuppressedAttr"));
        // not suppressed because defined in the class, which has no annotation
        assertTrue(attrNames.contains("NotSuppressedAttr"));
    }


    private static class DummyNodeParent extends DummyNode {

        DummyNodeParent(int id) {
            super();
        }

        DummyNodeParent(int id, boolean findBoundary) {
            super(findBoundary);
        }

        public String getSomeName() {
            return "Foo";
        }

        public int getSomeInt() {
            return 42;
        }

        public long getSomeLong() {
            return 42;
        }

        public long getSomeLong2() {
            return 42;
        }


    }

    @NoAttribute(scope = NoAttrScope.INHERITED)
    private static class NodeNoInherited extends DummyNodeParent {

        NodeNoInherited(int id) {
            super(id);
        }

        // getSomeName is inherited and filtered out by NoAttrScope.INHERITED
        // getSomeInt is inherited but overridden here, so NoAttrScope.INHERITED has no effect
        // getSomeLong is inherited and overridden here,
        //      and even with scope INHERITED its @NoAttribute takes precedence

        // isChild overrides nothing so with INHERITED it's not filtered out


        @Override
        public int getSomeInt() {
            return 43;
        }

        @NoAttribute // Notice
        @Override
        public long getSomeLong() {
            return 43;
        }


        @NoAttribute(scope = NoAttrScope.INHERITED)
        @Override
        public String getImage() {
            return super.getImage();
        }

        public boolean isChild() {
            return true;
        }


    }

    private static class NodeAllAttr extends DummyNodeParent {

        NodeAllAttr(int id) {
            super(id);
        }
    }

    @NoAttribute(scope = NoAttrScope.ALL)
    private static class NodeNoAttrAll extends DummyNodeParent {


        NodeNoAttrAll(int id) {
            super(id);
        }

        public int getMySuppressedAttr() {
            return 12;
        }

    }


    private static class NodeNoAttrAllChild extends NodeNoAttrAll {


        NodeNoAttrAllChild(int id) {
            super(id);
        }

        public int getNotSuppressedAttr() {
            return 12;
        }


    }


}
