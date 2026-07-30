package com.issue100.ranking;

import org.springframework.stereotype.Component;

@Component
public class WeightedIssueInterestScoreCalculator implements IssueInterestScoreCalculator {
    @Override
    public double calculate(IssueScoreInput input) {
        double score =
            normalize(input.articleVelocity()) * .25 +
            normalize(input.publishers()) * .20 +
            normalize(input.articles()) * .15 +
            normalize(input.uniqueViews()) * .15 +
            normalize(input.viewVelocity()) * .10 +
            normalize(input.outboundClicks()) * .05 +
            normalize(input.searches()) * .05 +
            normalize(input.freshness()) * .05;
        return Math.round(Math.clamp(score, 0, 100) * 10.0) / 10.0;
    }

    private double normalize(double value) {
        return Math.clamp(value, 0, 100);
    }
}
