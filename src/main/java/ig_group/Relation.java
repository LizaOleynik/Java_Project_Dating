package ig_group;

import static ig_group.Type_Relation.*;

enum Type_Relation {
    Friends,
    Colleagues,
    Relatives,
    Couple,
    Married_couple
}

public class Relation {
    private Person p_1;
    private Person p_2;
    private Type_Relation type;

    public Relation(Person p1, Person p2, Type_Relation type){
        this.p_1 = p1;
        this.p_2 = p2;
        this.type = type;
    }

    public Person getP_1(){
        return this.p_1;
    }

    public Person getP_2(){
        return this.p_2;
    }

    public Type_Relation getType() { return this.type;}

    public Person Partner(Person p){
        if (p==this.p_1)
            return this.getP_2();
        else
            return this.getP_1();
    }

    public Person Partner(String p){
        if (p.equals(p_1.get_Name()))
            return this.getP_2();
        else
            return this.getP_1();
    }

    public int getIntType(Type_Relation t){
        switch (t){
            case Friends: return 1;
            case Colleagues: return 2;
            case Relatives: return 3;
            case Couple: return 4;
            case Married_couple: return 5;
        } return 0;
    }

}
