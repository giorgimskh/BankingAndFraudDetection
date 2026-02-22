package rules;

public class RuleResult {
    private final Decision decision;
    private final String reason;


    public RuleResult(Decision decision, String reason) {
        if(decision==null)
            throw new IllegalArgumentException("Decision cant be null");
        if(decision!=Decision.ALLOW && reason.isEmpty())
            throw new IllegalArgumentException("Reason cant be empty");

        this.decision = decision;
        this.reason = reason;
    }

    public static RuleResult allow(){
        return new RuleResult(Decision.ALLOW,"");
    }

    public static RuleResult review(){
            return new RuleResult(Decision.REVIEW,"");
    }

    public static RuleResult block(){
        return new RuleResult(Decision.BLOCK, block().reason);
    }



}
