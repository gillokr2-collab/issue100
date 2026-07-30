package com.issue100.ranking;

public interface IssueInterestScoreCalculator {
    double calculate(IssueScoreInput input);

    record IssueScoreInput(double articleVelocity, double publishers, double articles,
        double uniqueViews, double viewVelocity, double outboundClicks,
        double searches, double freshness) {}
}
