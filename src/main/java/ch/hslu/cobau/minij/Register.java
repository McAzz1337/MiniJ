package ch.hslu.cobau.minij;

import ch.hslu.cobau.minij.ast.expression.BinaryOperator;

public enum Register {

    RDI("rdi"),
    RSI("rsi"),
    RDX("rdx"),
    RCX("rcx"),
    R8("r8"),
    R9("r9"),
    RAX("rax"),
    RBX("rbx"),
    R10("r10"),
    R11("r11"),
    R12("r12"),
    RBP("rbp"),
    RSP("rsp");

    public final String name;
    public boolean inUse;

    private Register(String name) {

        this.name = name;
        inUse = false;
    }

    public String asAddress(int offset) {

        if (offset < 0) {

            return "[" + name + "+" + Math.abs(offset) + "]";
        }

        return "[" + name + "-" + offset + "]";
    }

    public static Register getFreeRegister() {

        for (Register r : Register.values()) {

            if (r == Register.RBP) {

                return null;
            }
            else if (!r.inUse) {

                return r;
            }
        }

        return null;
    }

    public static Register getFreeRegister(Register desired) {

        boolean latched = false;
        int lastIndex = 0;
        for (int i = 0; i <  Register.values().length; i++) {

            Register r = Register.values()[i];
            if (r == desired) {

                lastIndex = i;
                latched = true;
            }

            if (latched) {

                if (r == Register.RBP) {

                    break;
                }
                else if (!r.inUse) {

                    return r;
                }

            }

        }

        for (int i = 0; i < lastIndex; i++) {

            Register r = Register.values()[i];

            if (r == Register.RBP) {

                continue;
            }
            else if (!r.inUse) {

                return r;
            }

        }

        return null;
    }

}
