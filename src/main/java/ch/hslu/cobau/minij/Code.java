package ch.hslu.cobau.minij;

public class Code {

    String header = "";
    String data = "\n";
    String bss = "\n";
    String text = "\n";

    boolean print = false;
    boolean lineNumbers = false;
    int lineNumber = 17;

    public Code() {


    }

    public void addSections() {


        header += "DEFAULT REL\n";
        header += "bits 64\n";

        data += "section .data\n";
        bss += "section .bss\n";
        bss += "alignb 8\n";
        text += "section .text\n";
    }

    public void togglePrint() {

        print = !print;
    }

    public Code add(String s) {

        text += s;

        return this;
    }

    public Code comma() {

        text += ", ";

        return this;
    }

    private String putLineNumber() {

        if (lineNumbers) {
            return "\t;#" + lineNumber++ + "\n";
        }

        return "\n";
    }

    public Code newLine() {

        text += putLineNumber();

        return this;
    }

    public Code comment(String s) {

        text += "; " + s;

        return this;
    }

    public void addExternFunction(String identifier) {

        header += "extern " + identifier + "\n";
    }

    public void addGlobalFunction(String identifier) {

        header += "global " + identifier + "\n";
    }

    public void addFunctionLabel(String identifier) {

        if (print)
            System.out.println(identifier + ":");

        text += identifier + ":\n";
    }

    public Code addInstruction(Instruction instruction) {

        if (print)
            System.out.println(instruction.mnemonic);

        text += instruction.mnemonic;

        return this;
    }

    public Code addInstruction(Instruction instruction, String word) {

        if (print)
            System.out.println(instruction.mnemonic + word);

        text += instruction.mnemonic + word;

        return this;
    }


    public Code addInstruction(Instruction instruction, String word, String word1) {

        if (print)
            System.out.println(instruction.mnemonic + word + ", " + word1);

        text += instruction.mnemonic + word + ", " + word1;

        return this;
    }

    public Code addInstructionAsString(String instruction) {

        if (print)
            System.out.println(instruction);

        text += instruction;

        return this;
    }

    public void addData(String identifier, String type, int size) {

        data += "\t" + identifier + " " + type + " " + size + "\n";
    }

    public void addLabel(String label) {

        text += label + ":\n";
    }

    public int getInsertPoint() {

        return text.length();
    }

    public void addCode(Code code, int insertPoint) {


        String patched = text.substring(0, insertPoint);
        patched += "\t" + code.text;
        patched += "\t" + text.substring(insertPoint + 1);

        text = patched;
    }


    @Override
    public String toString() {
        return header + data + bss + text;
    }
}
