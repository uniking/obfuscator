package me.superblaubeere27;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.FileInputStream;
import java.util.*;

public class AsmTools {

    static int lableNumber = 0;
    static Map<String, String> lablesRename = new HashMap<>();
    static Random random = new Random();
    static final int obfNameLen = 6;
    static char obfNameHead = 'o';
    static char[] obfNameTail = new char[]{'O', 'o', '0'};

    public static String renameLable(String lable){
        Object v = lablesRename.get(lable);
        if(v == null){
            v = "L" + lableNumber;
            lableNumber++;
            lablesRename.put(lable, (String)v);
        }

        return (String)v;
    }


    private static String getOpcodeName(int opcode){
        String name = "unknow";
        switch (opcode){
            case Opcodes.ARRAYLENGTH:
                name = "ARRAYLENGTH";
                break;
            case Opcodes.RETURN:
                name = "RETURN";
                break;
            case Opcodes.IRETURN:
                name = "IRETURN";
                break;
            case Opcodes.IADD:
                name = "IADD";
                break;
            case Opcodes.GOTO:
                name = "GOTO";
                break;
            case Opcodes.INVOKEVIRTUAL:
                name = "INVOKEVIRTUAL";
                break;
            case Opcodes.INVOKESPECIAL:
                name = "INVOKESPECIAL";
                break;
            case Opcodes.INVOKESTATIC:
                name = "INVOKESTATIC";
                break;
            case Opcodes.GETSTATIC:
                name = "GETSTATIC";
                break;
            case Opcodes.PUTSTATIC:
                name = "PUTSTATIC";
                break;
            case Opcodes.ILOAD:
                name = "ILOAD";
                break;
            case Opcodes.BIPUSH:
                name = "BIPUSH";
                break;
            case Opcodes.ISTORE:
                name = "ISTORE";
                break;
            case Opcodes.LSTORE:
                name = "LSTORE";
                break;
            case Opcodes.ARETURN:
                name = "ARETURN";
                break;
            case Opcodes.ALOAD:
                name = "ALOAD";
                break;
            case Opcodes.BALOAD:
                name = "BALOAD";
                break;
            case Opcodes.AASTORE:
                name = "AASTORE";
                break;
            case Opcodes.DUP:
                name = "DUP";
                break;
            case Opcodes.ACONST_NULL:
                name = "ACONST_NULL";
                break;
            case Opcodes.ICONST_0:
                name = "ICONST_0";
                break;
            case Opcodes.ICONST_1:
                name = "ICONST_1";
                break;
            case Opcodes.ICONST_2:
                name = "ICONST_2";
                break;
            case Opcodes.ICONST_3:
                name = "ICONST_3";
                break;
            case Opcodes.ICONST_4:
                name = "ICONST_4";
                break;
            case Opcodes.ICONST_5:
                name = "ICONST_5";
                break;
            case Opcodes.LCONST_0:
                name = "LCONST_0";
                break;
            case Opcodes.LCONST_1:
                name = "LCONST_1";
                break;
            case Opcodes.FCONST_0:
                name = "FCONST_0";
                break;
            case Opcodes.FCONST_1:
                name = "FCONST_1";
                break;
            case Opcodes.SIPUSH:
                name = "SIPUSH";
                break;
            case Opcodes.IFEQ:
                name = "IFEQ";
                break;
            case Opcodes.IFNE:
                name = "IFNE";
                break;
            case Opcodes.IFLT:
                name = "IFLT";
                break;
            case Opcodes.IFGE:
                name = "IFGE";
                break;
            case Opcodes.IFGT:
                name = "IFGT";
                break;
            case Opcodes.IFLE:
                name = "IFLE";
                break;
            case Opcodes.IF_ICMPEQ:
                name = "IF_ICMPEQ";
                break;
            case Opcodes.IF_ICMPNE:
                name = "IF_ICMPNE";
                break;
            case Opcodes.IF_ICMPLT:
                name = "IF_ICMPLT";
                break;
            case Opcodes.IF_ICMPGE:
                name = "IF_ICMPGE";
                break;
            case Opcodes.IF_ICMPGT:
                name = "IF_ICMPGT";
                break;
            case Opcodes.IF_ACMPEQ:
                name = "IF_ACMPEQ";
                break;
            case Opcodes.IF_ACMPNE:
                name = "IF_ACMPNE";
                break;
            case Opcodes.IF_ICMPLE:
                name = "IF_ICMPLE";
                break;
            case Opcodes.IFNULL:
                name = "IFNULL";
                break;
            case Opcodes.IFNONNULL:
                name = "IFNONNULL";
                break;
            case Opcodes.PUTFIELD:
                name = "PUTFIELD";
                break;
            case Opcodes.GETFIELD:
                name = "GETFIELD";
                break;
            case Opcodes.ISUB:
                name = "ISUB";
                break;
            case Opcodes.ASTORE:
                name = "ASTORE";
                break;
            case Opcodes.NEW:
                name = "NEW";
                break;
            case Opcodes.NEWARRAY:
                name = "NEWARRAY";
                break;
            case Opcodes.ANEWARRAY:
                name = "ANEWARRAY";
                break;
            case Opcodes.BASTORE:
                name = "BASTORE";
                break;
            case Opcodes.POP:
                name = "POP";
                break;
            case Opcodes.POP2:
                name = "POP2";
                break;
            case  Opcodes.IXOR:
                name = "IXOR";
                break;
            default:
                name = "unknow="+opcode;
        }

        return name;
    }

    private static String getFrameName(int type){
        String name = "unknow";
        switch (type){
            case Opcodes.F_NEW:
                name = "F_NEW";
                break;
            case Opcodes.F_FULL:
                name = "F_FULL";
                break;
            case Opcodes.F_APPEND:
                name = "F_APPEND";
                break;
            case Opcodes.F_CHOP:
                name = "F_CHOP";
                break;
            case Opcodes.F_SAME:
                name = "F_SAME";
                break;
            case Opcodes.F_SAME1:
                name = "F_SAME1";
                break;
            default:
                name = "unkonw="+type;
        }
        return name;
    }

    public static String generateName(){
        StringBuilder sb = new StringBuilder();
        sb.append(obfNameHead);
        for(int i = 0; i<obfNameLen-1; i++){
            int rn = Math.abs(random.nextInt()) % obfNameTail.length;
            sb.append(obfNameTail[rn]);
        }

        return sb.toString();
    }

    public static String generateObfLocalName(Set<String> hideLocal){
        String name = "";
        while (true){
            name = generateName();
            if(!hideLocal.contains(name)){
                hideLocal.add(name);
                break;
            }
        }
        return name;
    }

    private static LocalVariableNode getLocalVariableNodeByIndex(List<LocalVariableNode> localVariables, int index){
        for (LocalVariableNode localVariableNode : localVariables){
            if(localVariableNode.index == index){
                return localVariableNode;
            }
        }

        return null;
    }

    public static void recoverHidenLocal(MethodNode methodNode, LabelNode start, LabelNode end){
        Set<String> hideLocal = new HashSet<>();
        for (AbstractInsnNode abstractInsnNode : methodNode.instructions){
            int type = abstractInsnNode.getType();
            if(type == AbstractInsnNode.VAR_INSN){
                int opcode = abstractInsnNode.getOpcode();
                if(opcode >= Opcodes.ISTORE && opcode <= Opcodes.SASTORE){
                    VarInsnNode varInsnNode = (VarInsnNode) abstractInsnNode;
                    if(getLocalVariableNodeByIndex(methodNode.localVariables, varInsnNode.var) == null){
                        //hide local
                        //methodNode.localVariables.add(new LocalVariableNode(generateObfLocalName(hideLocal, )))
                    }
                }
            }
        }
    }

    public static void printInsnInfo(AbstractInsnNode ain){
        int type = ain.getType();
        switch (type){
            case AbstractInsnNode.INSN:
                InsnNode in = (InsnNode) ain;
                System.out.println("INSN " + getOpcodeName(in.getOpcode()));
                break;
            case AbstractInsnNode.INT_INSN:
                IntInsnNode iin = (IntInsnNode) ain;
                System.out.println("INT_INSN "  + getOpcodeName(iin.getOpcode()) + " " + iin.operand);
                break;
            case AbstractInsnNode.VAR_INSN:
                VarInsnNode vin = (VarInsnNode) ain;
                System.out.println("VAR_INSN " + getOpcodeName(vin.getOpcode()) + " " + vin.var);
                break;
            case AbstractInsnNode.TYPE_INSN:
                TypeInsnNode tin = (TypeInsnNode)ain;
                System.out.println("TYPE_INSN " + tin.getOpcode() + " " + tin.desc);
                break;
            case AbstractInsnNode.FIELD_INSN:
                FieldInsnNode fieldn = (FieldInsnNode) ain;
                System.out.println("FIELD_INSN " + getOpcodeName(fieldn.getOpcode()) + " " + fieldn.owner + " " + fieldn.name + " " + fieldn.desc);
                break;
            case AbstractInsnNode.METHOD_INSN:
                MethodInsnNode min = (MethodInsnNode) ain;
                System.out.println("METHOD_INSN " + getOpcodeName(min.getOpcode()) + " " + min.owner + " " + min.name + " " + min.desc + " " + min.itf);
                break;
            case AbstractInsnNode.INVOKE_DYNAMIC_INSN:
                System.out.println("INVOKE_DYNAMIC_INSN");
                break;
            case AbstractInsnNode.JUMP_INSN:
                JumpInsnNode jin = (JumpInsnNode) ain;
                System.out.println("JUMP_INSN "+ getOpcodeName(jin.getOpcode()) + " " + renameLable(jin.label.getLabel().toString()));
                break;
            case AbstractInsnNode.LABEL:
                LabelNode ln = (LabelNode) ain;

                //System.out.println("LABEL " + ln.getLabel().toString() +" " +ln.getLabel().info);
                System.out.println(renameLable(ln.getLabel().toString()));
                break;
            case AbstractInsnNode.LDC_INSN:
                LdcInsnNode lin = (LdcInsnNode) ain;
                System.out.println("LDC_INSN " + lin.cst);
                break;
            case AbstractInsnNode.IINC_INSN:
                System.out.println("IINC_INSN");
                break;
            case AbstractInsnNode.TABLESWITCH_INSN:
                System.out.println("TABLESWITCH_INSN");
                break;
            case AbstractInsnNode.LOOKUPSWITCH_INSN:
                LookupSwitchInsnNode lsi = (LookupSwitchInsnNode) ain;
                String switchinfo = "LOOKUPSWITCH_INSN ";
                for(int i=0; i<lsi.keys.size(); i++){
                    switchinfo = switchinfo + " " + lsi.keys.get(i) + ":" + renameLable(lsi.labels.get(i).getLabel().toString()) + " ";
                }
                switchinfo = switchinfo + "default:" + renameLable(lsi.dflt.getLabel().toString());
                System.out.println(switchinfo);
                break;
            case AbstractInsnNode.MULTIANEWARRAY_INSN:
                System.out.println("MULTIANEWARRAY_INSN");
                break;
            case AbstractInsnNode.FRAME:
                FrameNode fn = (FrameNode) ain;
                System.out.println("FRAME " + getFrameName(fn.type) + " local=" + fn.local + " stack=" + fn.stack);
                break;
            case AbstractInsnNode.LINE:
                LineNumberNode lnn = (LineNumberNode) ain;

                System.out.println("LINE " + lnn.line + " " + renameLable(lnn.start.getLabel().toString()));
                break;
            default:
                System.out.println("unknow insn type");
                break;
        }
    }


    public static void printMethodInfo(MethodNode methodNode){
        System.out.println("------printMethodInfo start, " + methodNode.name + " " + methodNode.desc + "------");
        try {
            for(AbstractInsnNode ain : methodNode.instructions.toArray()){
                AsmTools.printInsnInfo(ain);
            }

            for (LocalVariableNode localVariableNode:methodNode.localVariables){
                System.out.println("LOCALVARIABLE " + localVariableNode.name +" " + localVariableNode.desc + " "
                        + AsmTools.renameLable(localVariableNode.start.getLabel().toString()) +" "
                        + AsmTools.renameLable(localVariableNode.end.getLabel().toString()) + " " + localVariableNode.index);
            }

            System.out.println("MAXSTACK = "+methodNode.maxStack);
            System.out.println("MAXLOCALS = "+methodNode.maxLocals);
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("------printMethodInfo end, " + methodNode.name + "------");
    }

    public static void printClassInfo(String classPath){
        try {
            ClassReader cr = new ClassReader(new FileInputStream(classPath));
            //ClassReader cr = new ClassReader(new FileInputStream("NodeId-fail.class"));
            ClassNode cn = new ClassNode();
            cr.accept(cn, 0);
            System.out.println("-----" + cn.name+"-----");
            System.out.println("class version="+cn.version);
            System.out.println("sourceFile=" + cn.sourceFile);
            System.out.println("sourceDebug="+cn.sourceDebug);
            for (MethodNode mn : cn.methods){
                printMethodInfo(mn);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
