/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.qa.api;

import org.eclipse.jubula.toolkit.enums.ValueSets.AUTActivationMethod;
import org.eclipse.jubula.toolkit.enums.ValueSets.BinaryChoice;
import org.eclipse.jubula.toolkit.enums.ValueSets.Direction;
import org.eclipse.jubula.toolkit.enums.ValueSets.InteractionMode;
import org.eclipse.jubula.toolkit.enums.ValueSets.KeyStroke;
import org.eclipse.jubula.toolkit.enums.ValueSets.Modifier;
import org.eclipse.jubula.toolkit.enums.ValueSets.Operator;
import org.eclipse.jubula.toolkit.enums.ValueSets.SearchType;
import org.eclipse.jubula.toolkit.enums.ValueSets.TreeDirection;
import org.eclipse.jubula.toolkit.enums.ValueSets.Unit;
import org.junit.Assert;
import org.junit.Test;

/** @author BREDEX GmbH */
public class TestValueSets {
    /** Value set test: Operator */
    @Test
    public void testOperator() {
        Assert.assertEquals(Operator.equals.rcValue(), "equals"); //$NON-NLS-1$
        Assert.assertEquals(Operator.valueOf("equals"), Operator.equals); //$NON-NLS-1$
        Assert.assertEquals("not equals", Operator.notEquals.rcValue()); //$NON-NLS-1$
        Assert.assertEquals(Operator.valueOf("notEquals"), Operator.notEquals); //$NON-NLS-1$
        Assert.assertEquals("matches", Operator.matches.rcValue()); //$NON-NLS-1$
        Assert.assertEquals(Operator.valueOf("matches"), Operator.matches); //$NON-NLS-1$
        Assert.assertEquals("simple match", Operator.simpleMatch.rcValue()); //$NON-NLS-1$
        Assert.assertEquals(Operator.valueOf("simpleMatch"), Operator.simpleMatch); //$NON-NLS-1$
    }
    /** Value set test: Modifier */
    @Test
    public void testModifier() {
        Assert.assertEquals("shift", Modifier.shift.rcValue()); //$NON-NLS-1$
        Assert.assertEquals(Modifier.valueOf("shift"), Modifier.shift); //$NON-NLS-1$
        Assert.assertEquals("cmd", Modifier.cmd.rcValue()); //$NON-NLS-1$
        Assert.assertEquals(Modifier.valueOf("cmd"), Modifier.cmd); //$NON-NLS-1$
        Assert.assertEquals("control", Modifier.control.rcValue()); //$NON-NLS-1$
        Assert.assertEquals(Modifier.valueOf("control"), Modifier.control); //$NON-NLS-1$
        Assert.assertEquals("none", Modifier.none.rcValue()); //$NON-NLS-1$
        Assert.assertEquals(Modifier.valueOf("none"), Modifier.none); //$NON-NLS-1$
        Assert.assertEquals("alt", Modifier.alt.rcValue()); //$NON-NLS-1$
        Assert.assertEquals(Modifier.valueOf("alt"), Modifier.alt); //$NON-NLS-1$
        Assert.assertEquals("meta", Modifier.meta.rcValue()); //$NON-NLS-1$
        Assert.assertEquals(Modifier.valueOf("meta"), Modifier.meta); //$NON-NLS-1$
        Assert.assertEquals("mod", Modifier.mod.rcValue()); //$NON-NLS-1$
        Assert.assertEquals(Modifier.valueOf("mod"), Modifier.mod); //$NON-NLS-1$
    }
    /** Value set test: SearchType */
    @Test
    public void testSearchType() {
        Assert.assertEquals("absolute", SearchType.absolute.rcValue()); //$NON-NLS-1$
        Assert.assertEquals(SearchType.valueOf("absolute"), SearchType.absolute); //$NON-NLS-1$
        Assert.assertEquals("relative", SearchType.relative.rcValue()); //$NON-NLS-1$
        Assert.assertEquals(SearchType.valueOf("relative"), SearchType.relative); //$NON-NLS-1$
    }
    /** Value set test: TreeDirection */
    @Test
    public void testTreeDirection() {
        Assert.assertEquals("down", TreeDirection.down.rcValue()); //$NON-NLS-1$
        Assert.assertEquals(TreeDirection.valueOf("down"), TreeDirection.down); //$NON-NLS-1$
        Assert.assertEquals("up", TreeDirection.up.rcValue()); //$NON-NLS-1$
        Assert.assertEquals(TreeDirection.valueOf("up"), TreeDirection.up); //$NON-NLS-1$
        Assert.assertEquals("next", TreeDirection.next.rcValue()); //$NON-NLS-1$
        Assert.assertEquals(TreeDirection.valueOf("next"), TreeDirection.next); //$NON-NLS-1$
        Assert.assertEquals("previous", TreeDirection.previous.rcValue()); //$NON-NLS-1$
        Assert.assertEquals(TreeDirection.valueOf("previous"), TreeDirection.previous); //$NON-NLS-1$
    }
    /** Value set test: Unit */
    @Test
    public void testUnit() {
        Assert.assertEquals("pixel", Unit.pixel.rcValue()); //$NON-NLS-1$
        Assert.assertEquals(Unit.valueOf("pixel"), Unit.pixel); //$NON-NLS-1$
        Assert.assertEquals("percent", Unit.percent.rcValue()); //$NON-NLS-1$
        Assert.assertEquals(Unit.valueOf("percent"), Unit.percent); //$NON-NLS-1$
    }
    /** Value set test: AUTActivationMethod */
    @Test
    public void testAUTActivationMethod() {
        Assert.assertEquals("AUT_DEFAULT", AUTActivationMethod.autDefault.rcValue()); //$NON-NLS-1$
        Assert.assertEquals(AUTActivationMethod.valueOf("autDefault"), AUTActivationMethod.autDefault); //$NON-NLS-1$
        Assert.assertEquals("CENTER", AUTActivationMethod.center.rcValue()); //$NON-NLS-1$
        Assert.assertEquals(AUTActivationMethod.valueOf("center"), AUTActivationMethod.center); //$NON-NLS-1$
        Assert.assertEquals("NONE", AUTActivationMethod.none.rcValue()); //$NON-NLS-1$
        Assert.assertEquals(AUTActivationMethod.valueOf("none"), AUTActivationMethod.none); //$NON-NLS-1$
        Assert.assertEquals("NE", AUTActivationMethod.northeast.rcValue()); //$NON-NLS-1$
        Assert.assertEquals(AUTActivationMethod.valueOf("northeast"), AUTActivationMethod.northeast); //$NON-NLS-1$
        Assert.assertEquals("NW", AUTActivationMethod.northwest.rcValue()); //$NON-NLS-1$
        Assert.assertEquals(AUTActivationMethod.valueOf("northwest"), AUTActivationMethod.northwest); //$NON-NLS-1$
        Assert.assertEquals("SE", AUTActivationMethod.southeast.rcValue()); //$NON-NLS-1$
        Assert.assertEquals(AUTActivationMethod.valueOf("southeast"), AUTActivationMethod.southeast); //$NON-NLS-1$
        Assert.assertEquals("SW", AUTActivationMethod.southwest.rcValue()); //$NON-NLS-1$
        Assert.assertEquals(AUTActivationMethod.valueOf("southwest"), AUTActivationMethod.southwest); //$NON-NLS-1$
        Assert.assertEquals("TITLEBAR", AUTActivationMethod.titlebar.rcValue()); //$NON-NLS-1$
        Assert.assertEquals(AUTActivationMethod.valueOf("titlebar"), AUTActivationMethod.titlebar); //$NON-NLS-1$
    }
    /** Value set test: BinaryChoice */
    @Test
    public void testBinaryChoice() {
        Assert.assertEquals("yes", BinaryChoice.yes.rcValue()); //$NON-NLS-1$
        Assert.assertEquals(BinaryChoice.valueOf("yes"), BinaryChoice.yes); //$NON-NLS-1$
        Assert.assertEquals("no", BinaryChoice.no.rcValue()); //$NON-NLS-1$
        Assert.assertEquals(BinaryChoice.valueOf("no"), BinaryChoice.no); //$NON-NLS-1$
    }
    /** Value set test: Direction */
    @Test
    public void testDirection() {
        Assert.assertEquals("down", Direction.down.rcValue()); //$NON-NLS-1$
        Assert.assertEquals(Direction.valueOf("down"), Direction.down); //$NON-NLS-1$
        Assert.assertEquals("left", Direction.left.rcValue()); //$NON-NLS-1$
        Assert.assertEquals(Direction.valueOf("left"), Direction.left); //$NON-NLS-1$
        Assert.assertEquals("right", Direction.right.rcValue()); //$NON-NLS-1$
        Assert.assertEquals(Direction.valueOf("right"), Direction.right); //$NON-NLS-1$
        Assert.assertEquals("up", Direction.up.rcValue()); //$NON-NLS-1$
        Assert.assertEquals(Direction.valueOf("up"), Direction.up); //$NON-NLS-1$
    }
    /** Value set test: KeyStroke */
    @Test
    public void testKeyStroke() {
        Assert.assertEquals("DELETE", KeyStroke.delete.rcValue()); //$NON-NLS-1$
        Assert.assertEquals(KeyStroke.valueOf("delete"), KeyStroke.delete); //$NON-NLS-1$
    }
    /** Value set test: InteractionMode */
    @Test
    public void testInteractionMode() {
        Assert.assertEquals(
                new Integer(1), InteractionMode.primary.rcIntValue());
        Assert.assertEquals(
                InteractionMode.valueOf("primary"), InteractionMode.primary); //$NON-NLS-1$
        Assert.assertEquals(
                new Integer(2), InteractionMode.tertiary.rcIntValue()); 
        Assert.assertEquals(
                InteractionMode.valueOf("tertiary"), InteractionMode.tertiary); //$NON-NLS-1$
        Assert.assertEquals(
                new Integer(3), InteractionMode.secondary.rcIntValue()); 
        Assert.assertEquals(
                InteractionMode.valueOf("secondary"), InteractionMode.secondary); //$NON-NLS-1$
    }
}