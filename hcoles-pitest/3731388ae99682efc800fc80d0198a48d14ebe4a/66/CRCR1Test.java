/*
 * Copyright 2011 Henry Coles and Stefan Penndorf
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pitest.mutationtest.engine.gregor.mutators.rv;

import org.junit.Before;
import org.junit.Test;
import org.pitest.mutationtest.engine.Mutant;
import org.pitest.mutationtest.engine.gregor.MutatorTestBase;
import org.pitest.mutationtest.engine.gregor.mutators.rv.CRCR1Mutator;

import java.util.concurrent.Callable;

public class CRCR1Test extends MutatorTestBase {

  @Before
  public void setupEngineToMutateOnlyInlineConstants() {
    createTesteeWith(CRCR1Mutator.CRCR_1_MUTATOR);
  }

  private static class HasIntegerICONST0 implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {
      return 0;
    }

  }

  @Test
  public void shouldReplaceInteger0With1() throws Exception{
    final Mutant mutant = getFirstMutant(HasIntegerICONST0.class);
    assertMutantCallableReturns(new HasIntegerICONST0(), mutant, 1);
  }

  private static class HasIntegerICONST1 implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {
      return 1;
    }

  }

  @Test
  public void shouldNotReplaceInteger1() throws Exception {
    assertNoMutants(HasIntegerICONST1.class);
  }

  private static class HasIntegerICONST2 implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {
      return 2;
    }

  }

  @Test
  public void shouldReplaceInteger2With1() throws Exception {
    final Mutant mutant = getFirstMutant(HasIntegerICONST2.class);
    assertMutantCallableReturns(new HasIntegerICONST2(), mutant, 1);
  }

  private static class HasIntegerICONST3 implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {
      return 3;
    }

  }

  @Test
  public void shouldReplaceInteger3With1() throws Exception {
    final Mutant mutant = getFirstMutant(HasIntegerICONST3.class);
    assertMutantCallableReturns(new HasIntegerICONST3(), mutant, 1);
  }

  private static class HasIntegerICONST4 implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {
      return 4;
    }

  }

  @Test
  public void shouldReplaceInteger4With1() throws Exception {
    final Mutant mutant = getFirstMutant(HasIntegerICONST4.class);
    assertMutantCallableReturns(new HasIntegerICONST4(), mutant, 1);
  }

  private static class HasIntegerICONST5 implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {
      return 5;
    }

  }

  @Test
  public void shouldReplaceInteger5With1() throws Exception {
    final Mutant mutant = getFirstMutant(HasIntegerICONST5.class);
    assertMutantCallableReturns(new HasIntegerICONST5(), mutant, 1);
  }

  private static class HasIntegerLDC implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {
      return 987654321;
    }

  }

  @Test
  public void shouldReplaceLargeIntegerConstantsWith1()
      throws Exception {
    final Mutant mutant = getFirstMutant(HasIntegerLDC.class);
    assertMutantCallableReturns(new HasIntegerLDC(), mutant, 1);
  }

  private static class HasIntegerICONSTM1 implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {
      return -1;
    }

  }

  @Test
  public void shouldReplaceIntegerMinus1With1() throws Exception {
    final Mutant mutant = getFirstMutant(HasIntegerICONSTM1.class);
    assertMutantCallableReturns(new HasIntegerICONSTM1(), mutant, 1);
  }

  private static class HasBIPUSH implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {
      return 28;
    }

  }

  @Test
  public void shouldReplaceSmallIntegerConstantsWith1()
      throws Exception {
    final Mutant mutant = getFirstMutant(HasBIPUSH.class);
    assertMutantCallableReturns(new HasBIPUSH(), mutant, 1);
  }

  private static class HasSIPUSH implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {
      return 32700;
    }

  }

  @Test
  public void shouldReplaceMediumIntegerConstantsWith1()
      throws Exception {
    final Mutant mutant = getFirstMutant(HasSIPUSH.class);
    assertMutantCallableReturns(new HasSIPUSH(), mutant, 1);
  }


  private static class HasIntegerLDC2 implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {
      return 2144567;
    }

  }

  @Test
  public void shouldReplaceIntegerLdcWith1() throws Exception {
    final Mutant mutant = getFirstMutant(HasIntegerLDC2.class);
    assertMutantCallableReturns(new HasIntegerLDC2(), mutant,
        1);
  }

  private static class HasLongLCONST0 implements Callable<Long> {

    @Override
    public Long call() throws Exception {
      return 0L;
    }

  }

  @Test
  public void shouldReplaceLong0With1() throws Exception {
    final Mutant mutant = getFirstMutant(HasLongLCONST0.class);
    assertMutantCallableReturns(new HasLongLCONST1(), mutant, 1L);  }

  private static class HasLongLCONST1 implements Callable<Long> {

    @Override
    public Long call() throws Exception {
      return 1L;
    }

  }

  @Test
  public void shouldReplaceLong1With0(){
    assertNoMutants(HasLongLCONST1.class);
  }

  private static class HasLongLDC implements Callable<Long> {

    @Override
    public Long call() throws Exception {
      return 2999999999L;
    }

  }

  @Test
  public void shouldReplaceLongLDCWith1() throws Exception {
    final Mutant mutant = getFirstMutant(HasLongLDC.class);
    assertMutantCallableReturns(new HasLongLDC(), mutant, 1L);
  }

  /*
   * Double and Float
   */

  private static class HasFloatFCONST0 implements Callable<Float> {

    @Override
    public Float call() throws Exception {
      return 0.0F;
    }

  }

  @Test
  public void shouldReplaceFloat0With1() throws Exception {
    final Mutant mutant = getFirstMutant(HasFloatFCONST0.class);
    assertMutantCallableReturns(new HasFloatFCONST0(), mutant, 1.0F);
  }

  private static class HasFloatFCONST1 implements Callable<Float> {

    @Override
    public Float call() throws Exception {
      return 1.0F;
    }

  }

  @Test
  public void shouldNotReplaceFloat1(){
    assertNoMutants(HasFloatFCONST1.class);
  }

  private static class HasFloatFCONST2 implements Callable<Float> {

    @Override
    public Float call() throws Exception {
      return 2.0F;
    }

  }

  @Test
  public void shouldReplaceFloat2With1() throws Exception {
    final Mutant mutant = getFirstMutant(HasFloatFCONST2.class);
    assertMutantCallableReturns(new HasFloatFCONST2(), mutant, 1.0F);
  }

  private static class HasFloatLDC implements Callable<Float> {

    @Override
    public Float call() throws Exception {
      return 8364.123F;
    }

  }

  @Test
  public void shouldReplaceFloatWith1() throws Exception {
    final Mutant mutant = getFirstMutant(HasFloatLDC.class);
    assertMutantCallableReturns(new HasFloatLDC(), mutant, 1.0F);
  }

  private static class HasDoubleDCONST0 implements Callable<Double> {

    @Override
    public Double call() throws Exception {
      return 0.0D;
    }

  }

  @Test
  public void shouldNotReplaceDouble1() throws Exception {
    final Mutant mutant = getFirstMutant(HasDoubleDCONST0.class);
    assertMutantCallableReturns(new HasDoubleDCONST0(), mutant, 1.0D);
  }

  private static class HasDoubleDCONST1 implements Callable<Double> {

    @Override
    public Double call() throws Exception {
      return 1.0D;
    }

  }

  @Test
  public void shoulNotdReplaceDouble1W() throws Exception {
    assertNoMutants(HasDoubleDCONST1.class);
  }

  private static class HasDoubleLDC implements Callable<Double> {

    @Override
    public Double call() throws Exception {
      return 123456789.123D;
    }

  }

  @Test
  public void shouldReplaceDoubleWith1() throws Exception {
    final Mutant mutant = getFirstMutant(HasDoubleLDC.class);
    assertMutantCallableReturns(new HasDoubleLDC(), mutant, 1.0D);
  }
}
