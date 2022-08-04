/*
 * Copyright (c) 2017-2019 superblaubeere27, Sam Sun, MarcoMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.superblaubeere27.jobf.processors;

import com.sun.org.apache.bcel.internal.generic.ACONST_NULL;
import com.sun.org.apache.bcel.internal.generic.BIPUSH;
import lombok.extern.slf4j.Slf4j;
import me.superblaubeere27.annotations.ObfuscationTransformer;
import me.superblaubeere27.jobf.IClassTransformer;
import me.superblaubeere27.jobf.ProcessorCallback;
import me.superblaubeere27.jobf.processors.cfg.MethodAnalyzer;
import me.superblaubeere27.jobf.processors.cfg.Node;
import me.superblaubeere27.jobf.utils.NameUtils;
import me.superblaubeere27.jobf.utils.NodeUtils;
import me.superblaubeere27.jobf.utils.Utils;
import me.superblaubeere27.jobf.utils.values.DeprecationLevel;
import me.superblaubeere27.jobf.utils.values.EnabledValue;
import org.checkerframework.checker.units.qual.A;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;
import java.util.*;

import static java.lang.reflect.Modifier.STATIC;
import static org.objectweb.asm.Opcodes.*;

@Slf4j(topic = "obfuscator")
public class Flattening implements IClassTransformer {
    private static final String PROCESSOR_NAME = "Flattening";
    private static Random random = new Random();
    private EnabledValue enabled = new EnabledValue(PROCESSOR_NAME, "Flattening", DeprecationLevel.OK, false);

    /*
    不能重复
     */
    private int[] getKeys(int l){
        Set<Integer> hs = new HashSet<>();

        int[] a = new int[l];
        for(int i=0; i<l; ++i){
            while (true){
                int one = random.nextInt() % 9999;
                if(hs.contains(one)){
                    continue;
                }else{
                    a[i] = one;
                    break;
                }
            }
        }
        return a;
    }



    private void LineProcess(ProcessorCallback callback, ClassNode classNode){
        //if (!enabled.getObject()) return;

        if (!NodeUtils.isClassValid(classNode)) {
            return;
        }
        if (classNode.version == Opcodes.V1_1 || classNode.version < Opcodes.V1_4) {
            log.warn("!!! WARNING !!! " + classNode.name + "'s lang level is too low (VERSION < V1_4)");
            return;
        }

        for (MethodNode originMethod : classNode.methods) {
            if (!NodeUtils.isMethodValid(originMethod)) {
                continue;
            }

            if(!originMethod.name.equals("mm")){
                continue;
            }

            //获取BasicBlock
            ArrayList<ArrayList<AbstractInsnNode>> bbs = new ArrayList<>();
            ArrayList<AbstractInsnNode> bb = null;
            for (AbstractInsnNode abstractInsnNode : originMethod.instructions.toArray()) {
                int type = abstractInsnNode.getType();

                //每行作为一个bb
                if (type == AbstractInsnNode.LABEL) {
                    //new block
                    if (bb != null) {
                        bbs.add(bb);
                        bb = null;
                    }

                    //first label
                    bb = new ArrayList<>();
                    bb.add(abstractInsnNode);
                } else {
                    //old block
                    //method没有label
                    if(bb == null){
                        return;
                    }
                    bb.add(abstractInsnNode);
                }
            }

            //method没有label
            if(bb == null){
                continue;
            }

            if(bb.size() != 1){
                bbs.add(bb);
                bb = null;
            }


            int bbs_num = bbs.size();
            int[] keys = getKeys(bbs_num);
            String strKey = "";
            for (int key : keys){
                strKey = strKey +"," + key;
            }
            System.out.println("keys="+strKey);
            LabelNode[] labelNodes = new LabelNode[bbs.size()];
            for (int i=0; i<bbs.size(); ++i){
                LabelNode ln = (LabelNode) bbs.get(i).get(0);
                labelNodes[i] = ln;
            }
            //labelNodes随机排序

            //构造keys, bb的switch

            /*
                public int max(int a, int b){
                        int index = 33;
                        while(true){
                            switch (index){
                                case 1:
                                    b=b+10;
                                    index = 20;
                                    break;
                                case 3:
                                    b=b+3;
                                    index = 30;
                                    return 0;
                            }
                        }
                    }
             */
            MethodNode methodNode = new MethodNode();
            methodNode.instructions = new InsnList();

            //int index
            //变量位置
            int indexI = originMethod.localVariables.size() + 1;
            //index = keys[0]
            Label label0 = new Label();
            methodNode.instructions.add(new LabelNode(label0));
            System.out.println("set index="+keys[0]);
            methodNode.instructions.add(new IntInsnNode(SIPUSH, keys[0]));
            methodNode.instructions.add(new VarInsnNode(ISTORE, indexI));

            //while start
            Label labelWhileStart = new Label();
            methodNode.instructions.add(new LabelNode(labelWhileStart));

            //while frame
            //methodNode.instructions.add(new FrameNode(Opcodes.F_APPEND,2, new Object[] {Opcodes.INTEGER, Opcodes.INTEGER}, 0, null));

            //switch
            methodNode.instructions.add(new VarInsnNode(ILOAD, indexI));
            Label labelDefault = new Label();//默认while
            methodNode.instructions.add(new LookupSwitchInsnNode(
                    new LabelNode(labelDefault),
                    keys,//keys
                    labelNodes//labels
            ));
            //循环随机插入lables
            for(int i=0; i<bbs_num; ++i){
                for(AbstractInsnNode insn : bbs.get(i)){
                    methodNode.instructions.add(insn);
                }
                //插入swith值, 指向下一个block
                if(i<bbs_num-1){
                    //index = next key
                    Label label5 = new Label();
                    methodNode.instructions.add(new LabelNode(label5));
                    methodNode.instructions.add(new IntInsnNode(SIPUSH, keys[i+1]));
                    methodNode.instructions.add(new VarInsnNode(ISTORE, indexI));
                    //break
                    Label label6 = new Label();
                    methodNode.instructions.add(new LabelNode(label6));
                    methodNode.instructions.add(new JumpInsnNode(GOTO, new LabelNode(labelDefault)));
                }else{
                    //最后一个block不用给switch赋值了
                }
            }

            //goto while start
            methodNode.instructions.add(new LabelNode(labelDefault));
            //methodNode.instructions.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null));
            methodNode.instructions.add(new JumpInsnNode(GOTO, new LabelNode(labelWhileStart)));


            //覆盖原始方法指令
            originMethod.instructions = methodNode.instructions;
            //声明局部变量int index
            Label label9 = new Label();
            originMethod.localVariables.add(new LocalVariableNode("index", "S", null, new LabelNode(labelWhileStart), new LabelNode(label9), indexI));
        }
    }

    private void sort(int[] keys, LabelNode[] labelNode){
        //先使用冒泡排序, 以后改成快速排序
        for(int i=0; i<keys.length; i++){
            for(int j=0; j<keys.length-i-1; j++){
                if(keys[j] > keys[j+1]){
                    int tk;
                    LabelNode tln;

                    tk = keys[j];
                    keys[j] = keys[j+1];
                    keys[j+1] = tk;

                    tln = labelNode[j];
                    labelNode[j] = labelNode[j+1];
                    labelNode[j+1] = tln;
                }
            }
        }
    }

    /*
    (Ljava/lang/String;III)

    从签名解析局部变量
     */
    private List<String> parseLocal(String sign){
        List<String> locals = new ArrayList<String>();
        char[] bufSign = sign.toCharArray();
        boolean classSign = false;
        StringBuilder classBuilder = new StringBuilder();
        for (int i=0; i<sign.length(); i++){
            if(bufSign[i] == ')'){
                break;
            }
            if(bufSign[i] == '('){
                continue;
            }
            if(bufSign[i] == 'L'){
                classSign = true;
            }
            if(bufSign[i] == ';'){
                classSign = false;
                locals.add(classBuilder.toString());
                classBuilder.setLength(0);
            }

            if(classSign){
                classBuilder.append(bufSign[i]);
            }else{
                locals.add(String.valueOf(bufSign[i]));
            }

        }

        return locals;
    }

    private LocalVariableNode getLocalVariableNodeByIndex(List<LocalVariableNode> localVariables, int index){
        for (LocalVariableNode localVariableNode : localVariables){
            if(localVariableNode.index == index){
                return localVariableNode;
            }
        }

        return null;
    }

    /*
    向指令中增加变量, 并返回增加变量的类型供frmae使用
     */
    private List<String> addLocals(MethodNode methodNode, MethodNode originMethodNode){
        //声明所有局部变量
        List<String> locals_parameter = parseLocal(originMethodNode.desc);
        List<String> frame = new ArrayList<>();

        int num_parameter =  0;
        if(Modifier.isStatic(originMethodNode.access)){
            num_parameter = locals_parameter.size();
        }else{
            //this
            num_parameter = locals_parameter.size() + 1;
        }

        for(int i=num_parameter; i<originMethodNode.maxLocals; i++){
            LocalVariableNode localVariableNode = getLocalVariableNodeByIndex(originMethodNode.localVariables, i);
            if(localVariableNode == null){
                continue;
            }
            String localSign = localVariableNode.desc;
            switch (localSign){
                case "I":
                case "Z":
                case "S":
                case "B":
                case "C":
                    frame.add(localSign);
                    methodNode.instructions.add(new InsnNode(ICONST_0));
                    methodNode.instructions.add(new VarInsnNode(ISTORE, localVariableNode.index));
                    break;
                case "J":
                    frame.add(localSign);
                    methodNode.instructions.add(new InsnNode(LCONST_0));
                    methodNode.instructions.add(new VarInsnNode(LSTORE, localVariableNode.index));
                    break;
                case "D":
                    frame.add(localSign);
                    methodNode.instructions.add(new InsnNode(DCONST_0));
                    methodNode.instructions.add(new VarInsnNode(DSTORE, localVariableNode.index));
                    break;
                case "F":
                    frame.add(localSign);
                    methodNode.instructions.add(new InsnNode(FCONST_0));
                    methodNode.instructions.add(new VarInsnNode(FSTORE, localVariableNode.index));
                    break;
                default:
                    methodNode.instructions.add(new InsnNode(ACONST_NULL));
                    methodNode.instructions.add(new VarInsnNode(ASTORE, localVariableNode.index));

                    if(localSign.startsWith("L")){
                        frame.add(deleteHeadTail(localSign));
                    }else{
                        frame.add(localSign);
                    }

            }
        }

        return frame;
    }

    private String deleteHeadTail(String sign){
        if(!sign.startsWith("L")){
            return sign;
        }
        String result = sign.substring(1);
        result = result.substring(0, result.length()-1);
        return result;
    }

    private Object desc2FrameLocalType(String desc){
        Object type = "";
        switch (desc){
            case "T":
                type = TOP;
                break;
            case "I":
            case "Z":
            case "S":
            case "B":
            case "C":
                type = INTEGER;
                break;
            case "F":
                type = FLOAT;
                break;
            case "D":
                type = DOUBLE;
                break;
            case "J":
                type = LONG;
                break;
            case "N":
                type = NULL;
                break;
            default:
                type = desc;
                break;
        }

        return type;
    }

    public void processMethod(MethodNode originMethod){

        MethodAnalyzer ma = new MethodAnalyzer(originMethod);
        Node root =  ma.getGraph();

        List<Node> allNode = root.getAllAvailNode();

        //初始化switch的key[], label[]
        int bbs_num = allNode.size();
        int[] keys = new int[bbs_num];
        LabelNode[] labelNode = new LabelNode[bbs_num];
        for (int i=0; i<bbs_num; ++i){
            keys[i] = allNode.get(i).getKey();
            labelNode[i] = allNode.get(i).getLabelNode();
        }
        sort(keys, labelNode);

        //声明临时methodnode
        MethodNode methodNode = new MethodNode();
        methodNode.instructions = new InsnList();

        //start label
        Label label0 = new Label();
        methodNode.instructions.add(new LabelNode(label0));

        //构造keys, bb的switch
        //int index
        //变量位置, this is 0


        //while start
        Label labelWhileStart = new Label();

        //while frame
        List<String> frame_append = addLocals(methodNode, originMethod);
        //index = keys[0]
        int indexI = originMethod.maxLocals;
        System.out.println("set index="+keys[0]);
        methodNode.instructions.add(new IntInsnNode(SIPUSH, allNode.get(0).getKey()));
        methodNode.instructions.add(new VarInsnNode(ISTORE, indexI));

        if(frame_append.size() > 2){
            List<String> frame_full = new ArrayList<>();
            List<String> locals_parameter = parseLocal(originMethod.desc);
            if(!Modifier.isStatic(originMethod.access)){
                frame_full.add(deleteHeadTail(originMethod.localVariables.get(0).desc));
            }
            for (String parameter : locals_parameter){
                frame_full.add(parameter);
            }
            for (String append:frame_append){
                frame_full.add(append);
            }

            int numLocal = frame_full.size() + 1;
            Object[] local = new Object[numLocal];
            for (int i=0; i< numLocal-1; i++){
                local[i] = desc2FrameLocalType(frame_full.get(i));
            }
            local[numLocal-1] = INTEGER;
            methodNode.instructions.add(new LabelNode(labelWhileStart));
            //methodNode.instructions.add(new FrameNode(F_FULL,numLocal, local, 0, null));
        }else{
            int numLocal = frame_append.size() + 1;
            Object[] local = new Object[numLocal];
            for (int i=0; i< numLocal-1; i++){
                local[i] = desc2FrameLocalType(frame_append.get(i));
            }
            local[numLocal-1] = INTEGER;

            methodNode.instructions.add(new LabelNode(labelWhileStart));
            //methodNode.instructions.add(new FrameNode(Opcodes.F_APPEND,numLocal, local, 0, null));
            //methodNode.instructions.add(new FrameNode(Opcodes.F_APPEND,numLocal, local, 3, new Object[]{"java/io/PrintStream", 1, 1}));
        }

        //switch
        methodNode.instructions.add(new VarInsnNode(ILOAD, indexI));
        Label label2 = new Label();
        Label label3 = new Label();
        Label labelDefault = new Label();//默认while
        methodNode.instructions.add(new LookupSwitchInsnNode(
                new LabelNode(labelDefault),
                keys,//keys
                labelNode//labels
        ));
        //插入lables的代码部分
        for(Node node : allNode){
            for(AbstractInsnNode insn : node.getAbstractInsnNodes()){
                int type = insn.getType();
                if(type == AbstractInsnNode.JUMP_INSN){
                    switch (node.getJump()) {
                        case Node.JUMP_CONDITION:
                            //rewrite if index
                            /*
                            if ()
                                index = A
                            else
                                index = B
                             */
                            methodNode.instructions.add(insn);
                            Label ok = new Label();
                            Label fail = new Label();
                            JumpInsnNode jumpInsnNode = (JumpInsnNode) insn;
                            jumpInsnNode.label = new LabelNode(fail);

                            //ok, set index
                            methodNode.instructions.add(new LabelNode(ok));
                            int key = node.getArcs().get(1).getNext().getKey();
                            methodNode.instructions.add(new IntInsnNode(SIPUSH, key));
                            methodNode.instructions.add(new VarInsnNode(ISTORE, indexI));
                            //write break
                            Label label6 = new Label();
                            methodNode.instructions.add(new LabelNode(label6));
                            methodNode.instructions.add(new JumpInsnNode(GOTO, new LabelNode(labelDefault)));

                            //fail, set index
                            methodNode.instructions.add(new LabelNode(fail));
                            //methodNode.instructions.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null));
                            key = node.getArcs().get(0).getNext().getKey();
                            methodNode.instructions.add(new IntInsnNode(SIPUSH, key));
                            methodNode.instructions.add(new VarInsnNode(ISTORE, indexI));
                            //write break
                            label6 = new Label();
                            methodNode.instructions.add(new LabelNode(label6));
                            methodNode.instructions.add(new JumpInsnNode(GOTO, new LabelNode(labelDefault)));

                            break;
                        case Node.JUMP_UNCONDITION:
                            //delete goto
                            //methodNode.instructions.add(insn);
                            break;
                        case Node.JUMP_NO:
                            //不会被执行, 只有有助于理解
                            methodNode.instructions.add(insn);
                            break;
                    }
                }else if(type == AbstractInsnNode.LABEL){
                    methodNode.instructions.add(insn);
                    // first add frame
                    if (!node.haveFrame()) {
                        //methodNode.instructions.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null));
                    }
                }else if(type == AbstractInsnNode.LINE){
                    ;
                }
                else{
                    methodNode.instructions.add(insn);
                }
            }

            if(node.getJump() == Node.JUMP_UNCONDITION ||
            node.getJump() == Node.JUMP_NO){
                if (node.getArcs() != null && node.getArcs().size() == 1) {
                    //set index
                    Label label6 = new Label();
                    methodNode.instructions.add(new LabelNode(label6));
                    int key = node.getArcs().get(0).getNext().getKey();
                    methodNode.instructions.add(new IntInsnNode(SIPUSH, key));
                    methodNode.instructions.add(new VarInsnNode(ISTORE, indexI));
                }

                if(node.getArcs().size() == 0){
                    System.out.println("block end");
                }else{
                    //write break
                    Label label6 = new Label();
                    methodNode.instructions.add(new LabelNode(label6));
                    methodNode.instructions.add(new JumpInsnNode(GOTO, new LabelNode(labelDefault)));
                }
            }
        }

        //goto while start
        methodNode.instructions.add(new LabelNode(labelDefault));
        //methodNode.instructions.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null));
        methodNode.instructions.add(new JumpInsnNode(GOTO, new LabelNode(labelWhileStart)));


        //end label
        Label label6 = new Label();
        methodNode.instructions.add(new LabelNode(label6));

        //覆盖原始方法指令
        originMethod.instructions = methodNode.instructions;

        //修正this变量的作用范围
        for (LocalVariableNode localVariableNode:originMethod.localVariables){
            localVariableNode.start = new LabelNode(label0);
            localVariableNode.end = new LabelNode(label6);
        }

        //声明局部变量int index
        originMethod.maxLocals = originMethod.maxLocals+1;
        originMethod.localVariables.add(new LocalVariableNode("index", "S", null, new LabelNode(labelWhileStart), new LabelNode(label6), indexI));
    }



    /*
        public int max(int a, int b){
            int index = 33;
            while(true){
                switch (index){
                    case 1:
                        b=b+10;
                        index = 20;
                        break;
                    case 3:
                        b=b+3;
                        index = 30;
                        return 0;
                }
            }
        }

    1, 抽取BasicBlock
    2, 除了return, 将BB的跳转指令替换为对index的赋值
     */
    private void basicBlockProcess(ProcessorCallback callback, ClassNode classNode){
        //if (!enabled.getObject()) return;

        if (!NodeUtils.isClassValid(classNode)) {
            return;
        }
        if (classNode.version == Opcodes.V1_1 || classNode.version < Opcodes.V1_4) {
            log.warn("!!! WARNING !!! " + classNode.name + "'s lang level is too low (VERSION < V1_4)");
            return;
        }

        for (MethodNode originMethod : classNode.methods) {
            if (!NodeUtils.isMethodValid(originMethod)) {
                continue;
            }

            if(!originMethod.name.equals("mm")){
                continue;
            }

            MethodAnalyzer ma = new MethodAnalyzer(originMethod);
            Node root =  ma.getGraph();


            //获取target label
            Set<LabelNode> targetLabel = new HashSet<>();
            for (AbstractInsnNode abstractInsnNode : originMethod.instructions.toArray()) {
                int opcode = abstractInsnNode.getOpcode();
                if(opcode == GOTO){
                    JumpInsnNode jin = (JumpInsnNode) abstractInsnNode;
                    System.out.println("jump " + jin.label.getLabel().toString());
                    targetLabel.add(jin.label);
                }
            }


            //获取BasicBlock
            ArrayList<ArrayList<AbstractInsnNode>> bbs = new ArrayList<>();
            Map<LabelNode, Integer> l2k = new HashMap<>();
            ArrayList<AbstractInsnNode> bb = null;
            for (AbstractInsnNode abstractInsnNode : originMethod.instructions.toArray()) {
                int type = abstractInsnNode.getType();
                int opcode = abstractInsnNode.getOpcode();

                if(type == AbstractInsnNode.LABEL){
                    if(targetLabel.contains(abstractInsnNode)){
                        if(bb != null){
                            bbs.add(bb);
                            bb = null;
                        }
                    }else{
                        LabelNode ln = (LabelNode) abstractInsnNode;
                        if(ln.getLabel() == null){
                            abstractInsnNode = new LabelNode(new Label());
                        }
                    }

                }

                if(bb != null){
                    bb.add(abstractInsnNode);
                }else{
                    bb = new ArrayList<>();
                    bb.add(abstractInsnNode);
                }

                if(opcode == GOTO){
                    JumpInsnNode jin = (JumpInsnNode) abstractInsnNode;
                    bbs.add(bb);
                    bb = null;
                }else if(opcode == Opcodes.RETURN){
                    bbs.add(bb);
                    bb = null;
                }
            }

            int bbs_num = bbs.size();
            int[] keys = getKeys(bbs_num);
            LabelNode[] labelNodes = new LabelNode[bbs.size()];
            for (int i=0; i<bbs.size(); ++i){
                AbstractInsnNode lb = bbs.get(i).get(0);
                LabelNode ln = (LabelNode) lb;
                l2k.put(ln, i);
                labelNodes[i] = ln;
            }

            for(int i=0; i<keys.length; ++i){
                System.out.println("key:"+keys[i] +" " + labelNodes[i].getLabel());
            }


            //构造keys, bb的switch
            MethodNode methodNode = new MethodNode();
            methodNode.instructions = new InsnList();

            //int index
            //变量位置
            int indexI = originMethod.localVariables.size() + 1;
            //index = keys[0]
            Label label0 = new Label();
            methodNode.instructions.add(new LabelNode(label0));
            System.out.println("set index="+keys[0]);
            methodNode.instructions.add(new IntInsnNode(SIPUSH, keys[0]));
            methodNode.instructions.add(new VarInsnNode(ISTORE, indexI));

            //while start
            Label labelWhileStart = new Label();
            methodNode.instructions.add(new LabelNode(labelWhileStart));

            //switch
            //methodNode.instructions.add(new FrameNode(Opcodes.F_APPEND,1, new Object[] {Opcodes.INTEGER}, 0, null));
            methodNode.instructions.add(new VarInsnNode(ILOAD, indexI));
            Label label2 = new Label();
            Label label3 = new Label();
            Label labelDefault = new Label();//默认while
            methodNode.instructions.add(new LookupSwitchInsnNode(
                    new LabelNode(labelDefault),
                    keys,//keys
                    labelNodes//labels
            ));
            //插入lables的代码部分
            for(int i=0; i<bbs_num; ++i){
                for(AbstractInsnNode insn : bbs.get(i)){
                    int type = insn.getType();
                    int opcde = insn.getOpcode();
                    if(opcde == GOTO) {
                        //跳转改为对index的赋值, break
                        //index = next key
                        Label label5 = new Label();
                        methodNode.instructions.add(new LabelNode(label5));
                        JumpInsnNode jin = (JumpInsnNode) insn;
                        Integer key = l2k.get(jin.label);
                        int ikey = key;
                        methodNode.instructions.add(new IntInsnNode(SIPUSH, ikey));
                        methodNode.instructions.add(new VarInsnNode(ISTORE, indexI));
                        //break
                        Label label6 = new Label();
                        methodNode.instructions.add(new LabelNode(label6));
                        methodNode.instructions.add(new JumpInsnNode(GOTO, new LabelNode(labelDefault)));

                    }else{
                        methodNode.instructions.add(insn);
                    }
                }
            }

            //goto while start
            methodNode.instructions.add(new LabelNode(labelDefault));
            //methodNode.instructions.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null));
            methodNode.instructions.add(new JumpInsnNode(GOTO, new LabelNode(labelWhileStart)));


            //覆盖原始方法指令
            originMethod.instructions = methodNode.instructions;
            //声明局部变量int index
            Label label9 = new Label();
            originMethod.localVariables.add(new LocalVariableNode("index", "I", null, new LabelNode(labelWhileStart), new LabelNode(label9), indexI));
        }
    }

    @Override
    public void process(ProcessorCallback callback, ClassNode classNode) {
        basicBlockProcess(callback, classNode);
    }

    @Override
    public ObfuscationTransformer getType() {
        return ObfuscationTransformer.FLATTENING;
    }

}