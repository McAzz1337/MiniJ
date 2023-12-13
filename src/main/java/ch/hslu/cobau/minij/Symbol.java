package ch.hslu.cobau.minij;

import ch.hslu.cobau.minij.Register;
import ch.hslu.cobau.minij.ast.entity.Declaration;
import ch.hslu.cobau.minij.ast.expression.VariableAccess;
import ch.hslu.cobau.minij.ast.type.BooleanType;
import ch.hslu.cobau.minij.ast.type.IntegerType;
import ch.hslu.cobau.minij.ast.type.RecordType;
import ch.hslu.cobau.minij.ast.type.Type;

public class Symbol {

    private String identifier = "";
    private Register register = null;
    private Type type = null;
    private int offset = 0;
    private int size = 0;

    public Symbol(String identifier, Type type) {

        this.identifier = identifier;
        this.type = type;
    }

    public Symbol(String identifier, Type type,  Register register) {

        this(identifier, type);
        setRegister(register);
    }

    public Symbol(String identifier, Type type, Register register, int offset) {

        this(identifier, type, register);
        this.offset = offset;
    }

    private void determineSize() {

        if (type instanceof BooleanType) size = 4;
        else if (type instanceof IntegerType) size = 8;
        else if (type instanceof RecordType) size = -1;
    }


    public String getIdentifier() {

        return identifier;
    }

    public Register getRegister() {

        return register;
    }

    public String getRegisterAsString(boolean includeSize) {

        String output = "";

        if (includeSize) {

            output += "QWORD ";
        }

        if (register == null) {

            output += "[" + identifier + "]";
        }
        else if (register == Register.RBP) {

            output += register.asAddress(offset);
        }
        else {

            output += register.name;
        }

        return output;
    }

    public Type getType() {

        return type;
    }

    public int getOffset() {

        return offset;
    }

    public int getSize() {

        if (size == 0) {

            determineSize();
        }

        return size;
    }

    public void setRegister(Register register) {

        if (register != Register.RBP) {

            if (register.inUse) {
                System.out.println("Cannot move symbol " + identifier + " from reg " + this.register.name + " to " + register.name + " because it is already in use");
                System.exit(1337);
            }
            else {

                register.inUse = true;
            }
        }

        if (this.register != null && this.register != Register.RBP) {
            this.register.inUse = false;
        }

        this.register = register;
    }

    public void setRegister(Register register, int offset) {

        setRegister(register);
        setOffset(offset);
    }

    public void setOffset(int offset) {

        this.offset = offset;
    }





}
