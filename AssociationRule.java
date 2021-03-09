package fpgrowth;

import java.util.*;

public class AssociationRule {
    public final int[] antecedent;
    public final int[] consequent;
    public final double support;
    public final double confidence;
    public final double lift;
    public final double leverage;

    public AssociationRule(int[] antecedent, int[] consequent, double support, double confidence, double lift, double leverage) {
        this.antecedent = antecedent;
        this.consequent = consequent;
        this.support = support;
        this.confidence = confidence;
        this.lift = lift;
        this.leverage = leverage;
    }

    public boolean equals(Object o) {
        if (o instanceof AssociationRule) {
            AssociationRule a = (AssociationRule) o;
            if (support != a.support)
                return false;

            if (confidence != a.confidence)
                return false;

            if (antecedent.length != a.antecedent.length)
                return false;

            if (consequent.length != a.consequent.length)
                return false;

            for (int i = 0; i < antecedent.length; i++)
                if (antecedent[i] != a.antecedent[i])
                    return false;

            for (int i = 0; i < consequent.length; i++)
                if (consequent[i] != a.consequent[i])
                    return false;

            return true;
        }
        return false;
    }

    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + Arrays.hashCode(this.antecedent);
        hash = 13 * hash + Arrays.hashCode(this.consequent);
        hash = 13 * hash + (int) (Double.doubleToLongBits(this.support) ^ (Double.doubleToLongBits(this.support) >>> 32));
        hash = 13 * hash + (int) (Double.doubleToLongBits(this.confidence) ^ (Double.doubleToLongBits(this.confidence) >>> 32));
        return hash;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append('(');
        sb.append(antecedent[0]);

        for (int i = 1; i < antecedent.length; i++) {
            sb.append(", ");
            sb.append(antecedent[i]);
        }

        sb.append(") => (");
        sb.append(consequent[0]);

        for (int i = 1; i < consequent.length; i++) {
            sb.append(", ");
            sb.append(consequent[i]);
        }

        sb.append(String.format(") support = %.2f%% confidence = %.2f%% lift = %.2f leverage = %.4f", 100*support, 100*confidence, lift, leverage));
        return sb.toString();
    }
}

