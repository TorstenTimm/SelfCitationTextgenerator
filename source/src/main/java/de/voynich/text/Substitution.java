package de.voynich.text;

/**
 * Class <code>Substitution</code> to store whicht token can be substitute which other token
 */
public class Substitution {

    public String[] tokens;
    public int probability;

    public Substitution(String[] tokens, final int probability) {
        if (tokens.length == 0) {
            throw new IllegalStateException("tokens.length == 0");
        }
        this.tokens = tokens;
        this.probability = probability;
    }

    /**
     * returns the number of tokens
     * @return
     */
    public int count() {
        return tokens.length;
    }

    /**
     * access an token at position `pos`
     * @param pos
     * @return
     */
    public String get(int pos) {
        return tokens[pos];
    }

    /**
     * used to access the first token
     * @return
     */
    public String first() {
        return tokens[0];
    }

    /**
     * used to access the last token
     * @return
     */
    public String last() {
        return tokens[tokens.length-1];
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Subst {");
        for (String token : tokens) {
            sb.append(token).append(", ");
        }
        sb.append("}");
        return sb.toString();
    }
}
