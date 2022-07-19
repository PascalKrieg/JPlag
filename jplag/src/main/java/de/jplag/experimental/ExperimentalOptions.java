package de.jplag.experimental;

public class ExperimentalOptions {
    private int genericWindowLength = 15;
    private int genericMaxInsertionLength = 3;
    private int genericMaxIterations = 1;
    private int genericWindowIncrement = 4;

    public ExperimentalOptions() {

    }

    public ExperimentalOptions(int genericWindowLength, int genericMaxInsertionLength, int genericMaxIterations, int genericWindowIncrement) {
        this.genericWindowLength = genericWindowLength;
        this.genericMaxInsertionLength = genericMaxInsertionLength;
        this.genericMaxIterations = genericMaxIterations;
        this.genericWindowIncrement = genericWindowIncrement;
    }

    public int getGenericWindowLength() {
        return genericWindowLength;
    }

    public void setGenericWindowLength(int genericWindowLength) {
        this.genericWindowLength = genericWindowLength;
    }


    public int getGenericMaxInsertionLength() {
        return genericMaxInsertionLength;
    }

    public void setGenericMaxInsertionLength(int genericMaxInsertionLength) {
        this.genericMaxInsertionLength = genericMaxInsertionLength;
    }


    public int getGenericMaxIterations() {
        return genericMaxIterations;
    }

    public void setGenericMaxIterations(int genericMaxIterations) {
        this.genericMaxIterations = genericMaxIterations;
    }


    public int getGenericWindowIncrement() {
        return genericWindowIncrement;
    }

    public void setGenericWindowIncrement(int genericWindowIncrement) {
        this.genericWindowIncrement = genericWindowIncrement;
    }
}
