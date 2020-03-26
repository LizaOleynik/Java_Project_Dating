package ig_group;

import org.jgrapht.Graph;

import java.util.ArrayList;

public class Hobby {
    public boolean visitFlg = false;
    private String name;
    private Hobby parent;
    private int value;
    private ArrayList<Hobby> child;

    public String getName(){
        return this.name;
    }

    public Hobby getParent(){
        return this.parent;
    }

    public int getValue() {return this.value;}

    public Hobby(String name, int factor, Hobby parent){
        this(name, factor);
        this.parent = parent;
    }

    public Hobby(String name, int factor){
        this.name = name;
        this.value = factor;
        this.parent = null;
        this.child = new ArrayList<>();
    }

    public boolean isLeave(){
        return this.child.size() == 0;
    }

    public void addChild(Hobby hobby){
        this.child.add(hobby);
    }

    public void addInGraph(Graph g){
        for (Hobby child: this.child)
        {
            g.addVertex(child);
            g.addEdge(this, child, this.value);
            child.addInGraph(g);
        }
    }

    public Hobby findByName(String name){
        if (this.name.equals(name))
            return this;
        for (Hobby h: this.child) {
            Hobby th = h.findByName(name);
            if (th != null)
                return th;
        }
        return null;
    }
}