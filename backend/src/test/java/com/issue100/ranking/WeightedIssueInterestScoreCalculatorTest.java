package com.issue100.ranking;

import static org.assertj.core.api.Assertions.assertThat;
import com.issue100.ranking.IssueInterestScoreCalculator.IssueScoreInput;
import org.junit.jupiter.api.Test;

class WeightedIssueInterestScoreCalculatorTest {
    private final WeightedIssueInterestScoreCalculator calculator = new WeightedIssueInterestScoreCalculator();
    @Test void appliesRequiredWeights() {
        double score = calculator.calculate(new IssueScoreInput(100, 100, 100, 100, 100, 100, 100, 100));
        assertThat(score).isEqualTo(100.0);
    }
    @Test void clampsInputsAndOutput() {
        double score = calculator.calculate(new IssueScoreInput(200, -10, 100, 100, 100, 100, 100, 100));
        assertThat(score).isBetween(0.0, 100.0);
    }
}
