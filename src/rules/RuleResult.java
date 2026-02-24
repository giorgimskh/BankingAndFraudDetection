package rules;

public class RuleResult {
    private final Decision decision;
    private final String reason;


    public RuleResult(Decision decision, String reason) {
        if(decision==null)
            throw new IllegalArgumentException("Decision cant be null");
        if(decision==Decision.ALLOW){
            this.reason=(reason==null) ? "":reason;
        }else{
            if(reason==null || reason.trim().isEmpty()){
                throw new IllegalArgumentException("Reason cant be empty for "+decision);
            }
            this.reason=reason;
        }

        this.decision = decision;
    }

    public static RuleResult allow(){
        return new RuleResult(Decision.ALLOW,"");
    }

    public static RuleResult review(String reason){
            return new RuleResult(Decision.REVIEW,reason);
    }

    public static RuleResult block(String reason){
        return new RuleResult(Decision.BLOCK, reason);
    }

    public static boolean isAllow(RuleResult result){
        return result.decision==Decision.ALLOW;
    }

    public static boolean isReview(RuleResult result){
        return result.decision==Decision.REVIEW;
    }

    public  boolean isBlock(RuleResult result){
        return result.decision==Decision.BLOCK;
    }

    public Decision getDecision() {
        return decision;
    }

    public String getReason() {
        return reason;
    }

}
