package me.superblaubeere27.jobf.processors.cfg;

import org.objectweb.asm.Label;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LabelNode;

import java.util.*;

public class Node extends Element {

    private static Random random = new Random();

    private String name;
    private ArrayList<Arc> arcs;

    private ArrayList<AbstractInsnNode> abstractInsnNodes;

    private boolean frame = false;

    private int key;

    /*
    0无跳转
    1有条件跳转
    2无条件跳转
     */
    public static final int JUMP_NO = 0;
    public static final int JUMP_CONDITION = 1;
    public static final int JUMP_UNCONDITION = 2;
    private int jump = JUMP_NO;

    /** Constructs a new {@code Node}.
     *
     * @param id the id of the new {@code Node}. @see fr.univnantes.controlflowgraph.Element#Element(int).
     * @param name the name of the new {@code Node}. {@code name} is the label to print on the {@code Node}.
     */
    public Node(int id, String name) {
        super(id);
        init(name);
    }

    /** Constructs a new {@code Node} with a generated unique id.
     *
     * @param name the name of the new {@code Node}. {@code name} is the label to print on the {@code Node}.
     */
    public Node(String name){
        super();
        init(name);
    }

    private void init(String name) {
        this.name = name;
        arcs = new ArrayList<Arc>();
        abstractInsnNodes = new ArrayList<>();
        key = Keys.getInstance().getKey();
    }

    /** Gets the {@code name} of this {@code Node}.
     *
     * @return The {@code name} of this {@code Node}.
     */
    public String getName() {
        return this.name;
    }

    /** Gets the list of @see fr.univnantes.controlflowgraph.Arc whose start from this {@code Node}.
     *
     * @return The list of arc whose start from this {@code Node}.
     */
    public List<Arc> getArcs() {
        return this.arcs;
    }

    /** Add a new Arc to this {@code Node}.
     *
     * @param a the new arc to add. Doing this implies this arc start from this {@code Node}.
     */
    public void addArc(Arc a) {
        this.arcs.add(a);
    }

    public void addArc(int i, Arc a) {
        this.arcs.add(i, a);
    }

    public void clearArc(){
        this.arcs.clear();
    }

    public void addAbstractInsnNode(AbstractInsnNode abstractInsnNode){
        this.abstractInsnNodes.add(abstractInsnNode);
    }

    /** Gets the finality state of this {@code Node}.
     *
     * @return {@code true} if this {@code Node} is final (so does not have any exiting {@code Arc}); {@code false} otherwise.
     */
    public boolean isFinal() {
        return this.arcs.isEmpty();
    }

    public String toString() {
        return super.toString()+",name="+this.name+",arcs["+this.arcs.size()+"]";
    }

    /** Finds a node in the graph starting by this {@code Node}.
     *
     * @param toFind the {@code Node} to find
     *
     * @return a {@code Node} whose is equals to the {@code Node} in parameter if a {@code Node} correspond to it in graph starting by this {@code Node}; {@code null} otherwise.
     */
    public Node findNode(Node toFind){
        if(toFind == null)
            return null;
        LinkedList<Node> backList = new LinkedList<Node>();
        LinkedList<Node> queue = new LinkedList<Node>();
        queue.add(this);
        Node cur = null, next = null;
        while(!queue.isEmpty()) {
            cur = queue.remove();
            backList.add(cur);
            for(Arc a : cur.getArcs()) {
                next = a.getNext();
                if(next.equals(toFind)) {
                    return next;
                }
                if(!queue.contains(next) && !backList.contains(next)) {
                    queue.add(next);
                }
            }
        }
        return null;
    }

    public void setFrame(boolean s){
        frame = s;
    }

    public boolean haveFrame(){
        return frame;
    }

    public void setJump(int s){
        jump = s;
    }

    public int getJump(){
        return jump;
    }

    public int getKey(){
        return key;
    }

    public Label getLabel(){
        if(abstractInsnNodes.size() > 0){
            AbstractInsnNode abstractInsnNode = abstractInsnNodes.get(0);
            if(abstractInsnNode.getType() == AbstractInsnNode.LABEL){
                LabelNode ln = (LabelNode) abstractInsnNode;
                return ln.getLabel();
            }
            return null;
        }else {
            return null;
        }
    }

    public LabelNode getLabelNode(){
        if(abstractInsnNodes.size() > 0){
            AbstractInsnNode abstractInsnNode = abstractInsnNodes.get(0);
            if(abstractInsnNode.getType() == AbstractInsnNode.LABEL){
                LabelNode ln = (LabelNode) abstractInsnNode;
                return ln;
            }
            return null;
        }else {
            return null;
        }
    }

    public List<AbstractInsnNode> getAbstractInsnNodes(){
        return abstractInsnNodes;
    }

    public void setAbstractInsnNodes(ArrayList<AbstractInsnNode> abstractInsnNodes){
        this.abstractInsnNodes = abstractInsnNodes;
    }

    /** Finds a node in the graph starting by this {@code Node}.
     *
     * @param id the id of the {@code Node} to find
     *
     * @return a {@code Node} whose have the same id than {@code Node} in parameter if a {@code Node} correspond to it in graph starting by this {@code Node}; {@code null} otherwise.
     */
    public Node findNode(int id) {
        return findNode(new Node(id,""){});
    }

    public List<Node> getAllAvailNode(){
        ArrayList<Node> visited = getAllNode();

        //delete root
        visited.remove(0);

        //delete end label
        for(int i=0; i<visited.size(); i++){
            List<Arc> arcs1 = visited.get(i).getArcs();
            for(Arc arc : arcs1){
                if(arc.getNext().getAbstractInsnNodes().size() == 1 &&
                arc.getNext().getAbstractInsnNodes().get(0).getType() == AbstractInsnNode.LABEL){
                    if(visited.get(i).getArcs().size() != 1){
                        System.out.println("error");
                    }
                    visited.get(i).clearArc();
                    break;
                }
            }
        }
        for(int i=0; i<visited.size(); i++){
            if(visited.get(i).getAbstractInsnNodes().size() == 1 &&
                    visited.get(i).getAbstractInsnNodes().get(0).getType() == AbstractInsnNode.LABEL){
                visited.remove(i);
                break;
            }
        }

        return visited;
    }


    public ArrayList<Node> getAllNode(){
        LinkedList<Node> queue = new LinkedList<>();
        ArrayList<Node> visited = new ArrayList<Node>();
        queue.add(this);
        Node cur = null;
        while(queue.size() > 0) {
            cur = queue.remove();
            if(!visited.contains(cur))
                visited.add(cur);

            for(Arc arc : cur.getArcs()) {
                if(!visited.contains(arc.getNext())){
                    queue.add(arc.getNext());
                }
            }
        }

        return visited;
    }
}
