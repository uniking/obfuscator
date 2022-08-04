package me.superblaubeere27;

import me.superblaubeere27.jobf.processors.Flattening;
import me.superblaubeere27.jobf.utils.RefInvoke;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.*;
import java.lang.reflect.Modifier;


public class TestMain {
    public int max(int a, int b){
        int index = 9999;
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

    public void mm(int a, int b){

//        while(a>b){
//            System.out.println("0");
//        }

        if(a > b)
            System.out.println("1");
        else
            System.out.println("2");

//        switch (a){
//            case 3:
//            case 4:
//                System.out.println("3");
//                System.out.println("4");
//                break;
//            case 5:
//                System.out.println("5");
//                break;
//        }

//        for (int i=0; i<a+b; i++){
//            System.out.println(""+i);
//        }
    }

    private static String getFrameStackType(int type){
        String name = "";
        switch (type){
            case 0:
                name = "TOP";
                break;
            case 1:
                name = "INTEGER";
                break;
            case 2:
                name = "FLOAT";
                break;
            case 3:
                name = "DOUBLE";
                break;
            case 4:
                name = "LONG";
                break;
            case 5:
                name = "NULL";
                break;
            default:
                name = "unknow="+type;
        }

        return name;
    }

    private static void injectMethod(ClassNode cn) throws IOException{
        MethodNode newM = new MethodNode(Modifier.PUBLIC, "injectMethodTest", "()V", null, null);
        newM.instructions = new InsnList();

        //newM.instructions.add(new LabelNode());
        newM.instructions.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
        // 增加加载字符串常量的指令，这里加载的字符串是类名.方法声明
        newM.instructions.add(new LdcInsnNode("hello world"));
        // 增加调用java/io/PrintStream.println方法的指令
        newM.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false));
        newM.instructions.add(new InsnNode(Opcodes.RETURN));
        //newM.instructions.add(new LabelNode());

        cn.methods.add(newM);

        // 把ClassWriter接收到的内容转换成字节数组
        ClassWriter cw = new ClassWriter(0);
        cn.accept(cw);
        byte[] byteArray = cw.toByteArray();
        // 把相应的内容写回到A.class中
        FileOutputStream fos = new FileOutputStream("A.class");
        fos.write(byteArray);
        fos.close();
    }

    private static void inJectHelloWorld(MethodNode methodNode){
        AbstractInsnNode first = methodNode.instructions.getFirst();
        methodNode.instructions.insertBefore(first, new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
        // 增加加载字符串常量的指令，这里加载的字符串是类名.方法声明
        methodNode.instructions.insertBefore(first, new LdcInsnNode("hello world"));
        // 增加调用java/io/PrintStream.println方法的指令
        methodNode.instructions.insertBefore(first, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false));
    }

    /*
    classPath A.class
     */
    public void writeToFile(ClassNode cn, String classPath, int flags) throws IOException {
        // 把ClassWriter接收到的内容转换成字节数组
        ClassWriter cw = new ClassWriter(flags);
        cn.accept(cw);
        byte[] byteArray = cw.toByteArray();
        // 把相应的内容写回到A.class中
        FileOutputStream fos = new FileOutputStream(classPath);
        fos.write(byteArray);
        fos.close();
    }

    public void flatteningTest(String classPath){
        try {
            ClassReader cr = new ClassReader(new FileInputStream(classPath));
            //ClassReader cr = new ClassReader(new FileInputStream("NodeId-fail.class"));
            ClassNode cn = new ClassNode();
            cr.accept(cn, 0);

            Flattening flattening = new Flattening();
            flattening.process(null, cn);

            writeToFile(cn, "Flattening.class", ClassWriter.COMPUTE_FRAMES);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void test1(String a, int b, int c, int d){
        System.out.println(a+b+c+d);
    }

    public static void test2(String a, int b){
        System.out.println(a+b);
    }

    public static void test3(String a, int b, int c){
        System.out.println(a+b+c);
    }

    public static void test4(String a, int b, int c, int d){
        System.out.println(a+b+c+d);
    }


    int t = 10;
    String hello = "hello";
    public void haha(int a){
//        if(a>10){
//            System.out.println("a>10");
//        }else {
//            System.out.println("a<=10");
//        }
//
//        System.out.println("a>10");
//        System.out.println("a<=10");
//        System.out.println("a>10");
//        System.out.println("a<=10");
//
//        if(a>10){
//            if(a<20){
//                System.out.println("a>10 a<20");
//            }else{
//                System.out.println("a>10 a>20");
//            }
//        }
//
//        if(a>10 && a<20){
//            System.out.println("a>10 a<20");
//        }
//
//        if(a>10 || a<0){
//            System.out.println("a>10 || a<=0");
//        }else{
//            System.out.println("not a>10 || a<=0");
//        }
//
//        if((a>10 && a<20) || a==20){
//            System.out.println("a>10 a<=20");
//        }
//
//        int i=0;
//        if(a < i){
//            System.out.println(t);
//        }else{
//            System.out.println("hello");
//        }
//
//        String ii="hello";
//        if(hello == ii){
//            System.out.println(hello);
//        }
//
//        String aa = "hello";
//        int b=10;
//        while(t>0){
//            t--;
//            //System.out.println(t);
//            //test1(a, b, c, d);
//            test2(aa, b);
//        }
//
//        String aa2 = "hello";
//        int b2=10;
//        int c=11;
//        while(t>0){
//            t--;
//            //System.out.println(t);
//            //test1(a, b, c, d);
//            test3(aa2, b2, c);
//        }
//
//
//        String aa3 = "hello";
//        int b3=10;
//        int c3=11;
//        int d3=12;
//        while(t>0){
//            t--;
//            //System.out.println(t);
//            //test1(a, b, c, d);
//            test4(aa3, b3, c3, d3);
//        }
//
//
//        int e=13;
//        System.out.println(e);
//
//
//        int f = 2;
//        int g = 3;
//        if(f == 3){
//            System.out.println("hello");
//        }else{
//            System.out.println(c);
//
//            if(b == 23){
//                System.out.println("world");
//            }else{
//                System.out.println(g);
//            }
//        }
//
//        long i5=0;
//        String ii5="hello";
//        while (i5<a){
//            System.out.println(i5);
//            System.out.println(ii5);
//            i5++;
//        }
//
//        if("hello" == hello){
//            System.out.println(hello);
//        }


/*
B	byte	const_value_index	CONSTANT_Integer
C	char	const_value_index	CONSTANT_Integer
D	double	const_value_index	CONSTANT_Double
F	float	const_value_index	CONSTANT_Float
I	int	const_value_index	CONSTANT_Integer
J	long	const_value_index	CONSTANT_Long
S	short	const_value_index	CONSTANT_Integer
Z	boolean	const_value_index	CONSTANT_Integer
s	String	const_value_index	CONSTANT_Utf8
e	Enum type	enum_const_value	Not applicable
c	Class	class_info_index	Not applicable
@	Annotation type	annotation_value	Not applicable
[	Array type	array_value	Not applicable
 */

//        byte bb = 1;
//        System.out.println(bb);
//        char cc = 'a';
//        System.out.println(cc);
//        double dd = 0.0;
//        System.out.println(dd);
//        float ff = 0;
//        System.out.println(ff);
//        int ii = 1;
//        System.out.println(ii);
//        long ll = 1000;
//        System.out.println(ll);
//        short ss = 8;
//        System.out.println(ss);
//        boolean bo = false;
//        System.out.println(bo);
//        String str = "ss";
//        System.out.println(str);
//        byte[] barray = new byte[]{'a', 'b', 'c', 'd', 'e', 'f'};
//        String aa = new String(barray);
//        System.out.println(aa);
//        System.out.println("hello");


//        byte[] barray = new byte[]{'a', 'b', 'c', 'd', 'e', 'f'};
//        for(int i=0; i<barray.length; i++){
//            System.out.println(barray[i]);
//        }

        //三目操作
        System.out.println(a>10?"a>10":"a<=10");

        /*
        foreach隐藏变量
        1, [B 加载barray并存储到新变量
        2, , 对1执行arraylength, 拿到长度
        3, 赋初值index=0, 接下来遍历
         */
//        byte[] barray = new byte[]{'1', '2'};
//        for (byte b : barray){
//            System.out.println(b);
//        }

        //enum type, class, Annotation type, Array type

    }

    public void hahaObf(int a) {
        byte[] barray = null;
        byte[] a1 = null;
        int a2 = 0;
        int a3 = 0;
        byte b = 0;
        short index = -7774;

        while(true) {
            switch (index) {
                case -7924:
                    ++a3;
                    index = 5493;
                    break;
                case -7774:
                    barray = new byte[]{49, 50, 51};
                    index = 8640;
                    break;
                case 17:
                    return;
                case 79:
                    System.out.println(b);
                    index = -7924;
                    break;
                case 5493:
                    if (a3 < a2) {
                        index = 79;
                    } else {
                        index = 17;
                    }
                    break;
                case 8640:
                    a2 = barray.length;
                    a3 = 0;
                    index = 5493;
            }
        }
    }

    /*
    修改的还不够全
     */
    public void changeClassName(ClassNode classNode, String newClassName){
        String[] pp = classNode.name.split("/");
        String oldClassName = pp[pp.length-1];
        classNode.name = classNode.name.replace(oldClassName, newClassName);
        for (MethodNode methodNode : classNode.methods){
            for (AbstractInsnNode abstractInsnNode : methodNode.instructions){
                int type = abstractInsnNode.getType();
                switch (type){
                    case AbstractInsnNode.METHOD_INSN:
                        MethodInsnNode min = (MethodInsnNode) abstractInsnNode;
                        min.owner = min.owner.replace(oldClassName, newClassName);
                        min.desc = min.desc.replace(oldClassName, newClassName);
                        break;
                    case AbstractInsnNode.LDC_INSN:
                        LdcInsnNode lin = (LdcInsnNode) abstractInsnNode;
                        if(lin.cst instanceof String){
                            lin.cst = ((String)lin.cst).replace(oldClassName, newClassName);
                        }
                        break;
                    case AbstractInsnNode.FRAME:
                        FrameNode fn = (FrameNode) abstractInsnNode;
                        if(fn != null && fn.local != null){
                            for(int i=0; i<fn.local.size();i++){
                                Object obj = fn.local.get(i);
                                if(obj instanceof String){
                                    fn.local.set(i, ((String)obj).replace(oldClassName, newClassName));
                                }
                            }
                        }
                        break;
                    case AbstractInsnNode.TYPE_INSN:
                        TypeInsnNode tin = (TypeInsnNode)abstractInsnNode;
                        tin.desc = tin.desc.replace(oldClassName, newClassName);
                        break;
                    case AbstractInsnNode.FIELD_INSN:
                        FieldInsnNode fieldInsnNode = (FieldInsnNode) abstractInsnNode;
                        fieldInsnNode.owner = fieldInsnNode.owner.replace(oldClassName, newClassName);
                        break;
                }
            }

            for(LocalVariableNode localVariableNode : methodNode.localVariables){
                localVariableNode.desc = localVariableNode.desc.replace(oldClassName, newClassName);
            }
        }
        for(InnerClassNode innerClassNode:classNode.innerClasses){
            System.out.println(innerClassNode.name);
            if(innerClassNode.name != null){
                innerClassNode.name = innerClassNode.name.replace(oldClassName, newClassName);
            }
            if(innerClassNode.outerName != null){
                innerClassNode.outerName = innerClassNode.outerName.replace(oldClassName, newClassName);
            }
        }
    }

    /*
    运行时修改
     */
    public void runtimeChange(){
        try {
            String className = this.getClass().getSimpleName();
            InputStream in = TestMain.class.getResourceAsStream(className + ".class");
            ClassReader classReader = new ClassReader(in);
            ClassNode classNode = new ClassNode(Opcodes.ASM4);
            classReader.accept(classNode, 0);

            changeClassName(classNode, "TestMainChange");

            //插入一个hello world
            for(MethodNode methodNode : classNode.methods){
//                if(methodNode.name.equals("hahaObf")){
//                    printMethodInfo(methodNode);
//                }

                if(methodNode.name.equals("haha")){
                    AsmTools.printMethodInfo(methodNode);

                    //inJectHelloWorld(methodNode);
                    Flattening flattening = new Flattening();

//                    methodNode.localVariables.add(new LocalVariableNode("a1", "[B", null, new LabelNode(), new LabelNode(), 3));
//                    methodNode.localVariables.add(new LocalVariableNode("a2", "I", null, new LabelNode(), new LabelNode(), 4));
//                    methodNode.localVariables.add(new LocalVariableNode("a3", "I", null, new LabelNode(), new LabelNode(), 5));

                    flattening.processMethod(methodNode);

                    AsmTools.printMethodInfo(methodNode);
                }
            }
            //校验修改是否正确
            //ClassWriter classWriter = new ClassWriter(Opcodes.ASM9);
            //classNode.accept(classWriter);
            //CheckClassAdapter.verify(new ClassReader(classWriter.toByteArray()), true, new PrintWriter(System.out));

            writeToFile(classNode, "obfuscator-core/target/classes/"+classNode.name+".class", 0);
            Class<?> modifiedClass = getClass().getClassLoader().loadClass("me.superblaubeere27.TestMainChange");

            Object objTestMainChange = RefInvoke.newObject(modifiedClass);
            RefInvoke.invokeDeclaredMethod(modifiedClass, "haha", objTestMainChange, new Class[]{int.class}, new Object[]{5});
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        TestMain tm = new TestMain();
        AsmTools.printClassInfo("SandboxCore.class");
        //tm.printClassInfo("obfuscator-core/target/classes/me/superblaubeere27/TestMain.class");
        //tm.printClassInfo("Flattening.class");
        //tm.flatteningTest("obfuscator-core/target/classes/me/superblaubeere27/TestMain.class");

        //tm.haha(5);
        //tm.runtimeChange();

        //tm.hahaObf(1);
    }
}
