/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bop.seqAlign.framework;

import com.bop.common.math.TwoDimensionalCoordinates;

/**
 * @author Marco Ruiz
 */
public class TransitionDelta {
	
	private final AlignmentMatrix source;
	private final Transition previous;
	private final Transition current;
	
	// Computed
	private final char symbolA, symbolB, symbolAlignment;
	private final TwoDimensionalCoordinates distance;
	private final int scoreContribution;
	private float scoreContributionLevel = Float.NaN;
	
	public TransitionDelta(TransitionDelta target) {
		this(target.source, target.previous, target.current);
	}

	public TransitionDelta(AlignmentMatrix matrix, Transition previous, Transition current) {
		this.source = matrix;
		this.previous = previous;
		this.current = current;

		this.distance = current.getCoords().substract(previous.getCoords());
		this.scoreContribution = getReferencedScore(current) - getReferencedScore(previous);

		this.symbolA = computeSymbol(SequenceDesignator.SEQUENCE_A);
		this.symbolB = computeSymbol(SequenceDesignator.SEQUENCE_B);
		this.symbolAlignment = (symbolA == symbolB) ? symbolA : getScoreDifferenceSymbol(); 
	}
	
	/**
	 * Creates a value between -1 and 1 corresponding to this symbol's score contribution level (relative
	 * to the maximum score contribution)
	 * 
	 * @return
	 */
	public void setMaxScoreContribution(int max) {
		this.scoreContributionLevel = (max == 0) ? Float.NaN : Math.round(100.0 * scoreContribution / max) / 100.0f;
	}

	private int getReferencedScore(Transition transition) {
		return source.getReferencedTransition(transition).getScore();
	}

	public int getAccumulatedScore() {
		return getReferencedScore(current);
	}

	private char getScoreDifferenceSymbol() {
		return scoreContribution > 0 ? AlignmentSolution.POSITIVE_SYMBOL : AlignmentSolution.NEGATIVE_SYMBOL;
	}

	private char computeSymbol(SequenceDesignator designator) {
		return isCurrentSameAsPrevious(designator) ? source.getReferencedSymbol(designator, current) : AlignmentSolution.GAP_SYMBOL;
	}
	
	public boolean isCurrentSameAsPrevious(SequenceDesignator designator) {
		return current.getIndex(designator) == previous.getIndex(designator) + 1;
	}

	public char getSymbol(SequenceDesignator designator) {
		return symbolA;
	}

	public char getSymbolA() {
		return symbolA;
	}

	public char getSymbolB() {
		return symbolB;
	}

	public char getSymbolAlignment() {
		return symbolAlignment;
	}

	public Transition getPrevious() {
		return previous;
	}

	public Transition getCurrent() {
		return current;
	}
	
	public TwoDimensionalCoordinates getDistance() {
		return distance;
	}
	
	public boolean isIdentity() {
		return symbolA == symbolB;
	}
	
	public boolean isPositive() {
		return scoreContribution > 0;
	}
	
	public int getScoreContribution() {
		return scoreContribution;
	}
	
	public float getScoreContributionLevel() {
		return scoreContributionLevel;
	}
}
